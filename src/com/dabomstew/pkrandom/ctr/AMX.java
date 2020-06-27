package com.dabomstew.pkrandom.ctr;

import com.dabomstew.pkrandom.FileFunctions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class AMX {

    public byte[] decData;
    public int scriptOffset = 0;

    private int amxMagic = 0x0A0AF1E0;
    private int amxMagicDebug = 0x0A0AF1EF;
    private long mask = 0xFF;

    private int length;

    private int scriptInstrStart;
    private int scriptMovementStart;
    private int finalOffset;
    private int allocatedMemory;

    private int compLength;
    private int decompLength;

    private int ptrOffset;
    private int ptrCount;

    private byte[] extraData;

    public AMX(byte[] data, int scriptNum) throws IOException {
        int found = 0;
        for (int i = 0; i < data.length - 3; i++) {
            int val = FileFunctions.readFullIntLittleEndian(data,i);
            if (val == amxMagic) {
                if (found == scriptNum) {
                    int length = FileFunctions.readFullIntLittleEndian(data,i-4);
                    readHeaderAndDecompress(Arrays.copyOfRange(data,i-4,i-4+length));
                    scriptOffset = i-4;
                    break;
                } else {
                    found++;
                }
            }
        }
    }

    public AMX(byte[] encData) throws IOException {
        readHeaderAndDecompress(encData);
    }

    // Credit to the creators of pk3DS (Kaphotics et al)
    private void readHeaderAndDecompress(byte[] encData) throws IOException {
        length = FileFunctions.readFullIntLittleEndian(encData,0);
        int magic = FileFunctions.readFullIntLittleEndian(encData,4);
        if (magic != amxMagic) {
            throw new IOException();
        }

        ptrOffset = FileFunctions.read2ByteInt(encData,8);
        ptrCount = FileFunctions.read2ByteInt(encData,0xA);

        scriptInstrStart = FileFunctions.readFullIntLittleEndian(encData,0xC);
        scriptMovementStart = FileFunctions.readFullIntLittleEndian(encData,0x10);
        finalOffset = FileFunctions.readFullIntLittleEndian(encData,0x14);
        allocatedMemory = FileFunctions.readFullIntLittleEndian(encData,0x18);

        compLength = length - scriptInstrStart;
        byte[] compressedBytes = Arrays.copyOfRange(encData,scriptInstrStart,length);
        decompLength = finalOffset - scriptInstrStart;

        decData = decompressBytes(compressedBytes, decompLength);
        extraData = Arrays.copyOfRange(encData,0x1C,scriptInstrStart);
    }

    // Credit to FireyFly
    private byte[] decompressBytes(byte[] data, int length) {
        byte[] code = new byte[length];
        int i = 0, j = 0, x = 0, f = 0;
        while (i < code.length) {
            int b = data[f++];
            int v = b & 0x7F;
            if (++j == 1) {
                x = ((((v >>> 6 == 0 ? 1 : 0) - 1 ) << 6) | v);
            } else {
                x = (x << 7) | (v & 0xFF);
            }
            if ((b & 0x80) != 0) continue;
            code[i++] = (byte)(x & 0xFF);
            code[i++] = (byte)((x >>> 8) & 0xFF);
            code[i++] = (byte)((x >>> 16) & 0xFF);
            code[i++] = (byte)((x >>> 24) & 0xFF);
            j = 0;
        }
        return code;
    }

    public byte[] getBytes() {

        ByteBuffer bbuf = ByteBuffer.allocate(length*2);

        bbuf.order(ByteOrder.LITTLE_ENDIAN);

        bbuf.putInt(length);
        bbuf.putInt(amxMagic);
        bbuf.putShort((short)ptrOffset);
        bbuf.putShort((short)ptrCount);
        bbuf.putInt(scriptInstrStart);
        bbuf.putInt(scriptMovementStart);
        bbuf.putInt(finalOffset);
        bbuf.putInt(allocatedMemory);
        bbuf.put(extraData);
        bbuf.put(compressScript(decData));
        bbuf.flip();
        bbuf.putInt(bbuf.limit());

        return Arrays.copyOfRange(bbuf.array(),0,bbuf.limit());
    }

    private byte[] compressScript(byte[] data) {
        if (data == null || data.length % 4 != 0) {
            return null;
        }
        ByteBuffer inBuf = ByteBuffer.wrap(data);
        inBuf.order(ByteOrder.LITTLE_ENDIAN);

        ByteArrayOutputStream out = new ByteArrayOutputStream(compLength);

        while (inBuf.position() < data.length) {
            compressBytes(inBuf,out);
        }

        return out.toByteArray();
    }

    // Modified version of the AMX script compression algorithm from pk3DS
    private void compressBytes(ByteBuffer inBuf, ByteArrayOutputStream out) {
        short cmd = inBuf.getShort(inBuf.position());
        short val = inBuf.getShort(inBuf.position()+2);

        short b1 = (short)(cmd & 0xFF);
        short b2 = (short)((cmd >>> 8) & 0xFF);
        short b3 = (short)(val & 0xFF);
        short b4 = (short)((val >>> 8) & 0xFF);

        boolean sign4 = val < 0 && cmd < 0 && b1 >= 0xC0 && ((cmd >>> 7) & 0x7F) == 0x7F;
        boolean sign3 = val < 0 && cmd < 0 && ((b2 & 0xE0) == 0xE0) && ((b3 & 0xF) == 0xF);
        boolean sign2 = val < 0 && (b3 & 0xF0) == 0xF0 && (b4 & 0xFF) == 0xFF;
        boolean sign1 = val < 0 && ((b3 & 0x10) == 0 || (val & 0xFE) != 0xFE) && (b4 & 0x8) == 0x8;
        boolean sign0 = val < 0 && cmd > 0 && ((b4 & 0x8) == 0 || (b4 & 0xF0) != 0xF0);
        boolean manyb = cmd >= 0x40 || (((b2 >>> 6) & 0x3) | ((val << 2) & 0x7C)) > 0;
        boolean literal = cmd >= 0 && cmd < 0x40;

        if (sign4) {
            int dev = 0x40 + inBuf.getInt(inBuf.position());
            if (dev < 0) {
                inBuf.position(inBuf.position()+4);
                return;
            }
            out.write((dev & 0x3F) | 0x40);
        } else if (sign3) {
            byte first = (byte)(((cmd >>> 7) & 0x7F) | 0x80);
            byte second = (byte)(b1 & 0x7F);
            out.write(first);
            out.write(second);
        } else if (sign2) {
            byte first = (byte)((((b2 >>> 6) & 0x3) | ((val << 2) & 0x7C)) | 0x80);
            byte second = (byte)(((cmd >>> 7) & 0x7F) | 0x80);
            byte third = (byte)(b1 & 0x7F);
            out.write(first);
            out.write(second);
            out.write(third);
        } else if (sign1) {
            byte first = (byte)(((val >> 5) & 0x7F) | 0x80);
            byte second = (byte)((((b2 >>> 6) & 0x3) | ((val << 2) & 0x7C)) | 0x80);
            byte third = (byte)(((cmd >>> 7) & 0x7F) | 0x80);
            byte fourth = (byte)(b1 & 0x7F);
            out.write(first);
            out.write(second);
            out.write(third);
            out.write(fourth);
        } else if (sign0) {
            byte first = (byte)(((b4 >> 4) & 0xF) | 0x80);
            byte second = (byte)(((val >> 5) & 0x7F) | 0x80);
            byte third = (byte)((((b2 >>> 6) & 0x3) | ((val << 2) & 0x7C)) | 0x80);
            byte fourth = (byte)(((cmd >>> 7) & 0x7F) | 0x80);
            byte fifth = (byte)(b1 & 0x7F);
            out.write(first);
            out.write(second);
            out.write(third);
            out.write(fourth);
            out.write(fifth);
        } else if (manyb) {
            long bitStorage = 0;

            int dv = inBuf.getInt(inBuf.position());
            int ctr = 0;

            while (dv != 0 || ctr < 2) {
                byte bits = (byte)((byte)dv & 0x7F);
                dv >>>= 7;
                bitStorage |= ((long)bits << (ctr++*8));
                if (ctr != 1) {
                    bitStorage |= (((long)1 << (7 + ((ctr-1)*8))) & (mask << ((ctr-1)*8)));
                }
                if (dv == 0 && ((bits >>> 6) & 1) != 0) {
                    long operand = ((long)1 << (7 + ((ctr)*8))) & (mask << (ctr*8));
                    bitStorage |= operand;
                }
            }
            boolean skip = true;
            for (int i = 0; i < 8; i++) {
                byte bitsPart = (byte)((bitStorage >>> (7 - i)*8) & 0xFF);
                if (skip) {
                    if (bitsPart != 0) {
                        skip = false;
                        out.write(bitsPart);
                    }
                } else {
                    out.write(bitsPart);
                }
            }
        } else if (literal) {
            out.write(cmd & 0xFF);
        }
        inBuf.position(inBuf.position()+4);
    }
}
