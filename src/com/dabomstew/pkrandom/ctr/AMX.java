package com.dabomstew.pkrandom.ctr;

/*----------------------------------------------------------------------------*/
/*--  AMX.java - class for handling AMX script archives                     --*/
/*--                                                                        --*/
/*--  Contains code based on "pk3DS", copyright (C) Kaphotics               --*/
/*--  Contains code based on "pkNX", copyright (C) Kaphotics                --*/
/*--  Contains code based on "poketools", copyright (C) FireyFly            --*/
/*--  Additional contributions by the UPR-ZX team                           --*/
/*--                                                                        --*/
/*--  This program is free software: you can redistribute it and/or modify  --*/
/*--  it under the terms of the GNU General Public License as published by  --*/
/*--  the Free Software Foundation, either version 3 of the License, or     --*/
/*--  (at your option) any later version.                                   --*/
/*--                                                                        --*/
/*--  This program is distributed in the hope that it will be useful,       --*/
/*--  but WITHOUT ANY WARRANTY; without even the implied warranty of        --*/
/*--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          --*/
/*--  GNU General Public License for more details.                          --*/
/*--                                                                        --*/
/*--  You should have received a copy of the GNU General Public License     --*/
/*--  along with this program. If not, see <http://www.gnu.org/licenses/>.  --*/
/*----------------------------------------------------------------------------*/

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.exceptions.RandomizerIOException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        try {
            while (inBuf.position() < data.length) {
                compressBytes(inBuf, out);
            }
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }

        return out.toByteArray();
    }

    // Modified version of the AMX script compression algorithm from pkNX
    private void compressBytes(ByteBuffer inBuf, ByteArrayOutputStream out) throws IOException {
        List<Byte> bytes = new ArrayList<>();
        int instructionTemp = inBuf.getInt(inBuf.position());
        long instruction = Integer.toUnsignedLong(instructionTemp);
        boolean sign = (instruction & 0x80000000) > 0;

        // Signed (negative) values are handled opposite of unsigned (positive) values.
        // Positive values are "done" when we've shifted the value down to zero, but
        // we don't need to store the highest 1s in a signed value. We handle this by
        // tracking the loop via a NOTed shadow copy of the instruction if it's signed.
        int shadowTemp = sign ? ~instructionTemp : instructionTemp;
        long shadow = Integer.toUnsignedLong(shadowTemp);
        do
        {
            long least7 = instruction & 0b01111111;
            byte byteVal = (byte)least7;

            if (bytes.size() > 0)
            {
                // Continuation bit on all but the lowest byte
                byteVal |= 0x80;
            }

            bytes.add(byteVal);

            instruction >>= 7;
            shadow >>= 7;
        }
        while (shadow != 0);

        if (bytes.size() < 5)
        {
            // Ensure "sign bit" (bit just to the right of highest continuation bit) is
            // correct. Add an extra empty continuation byte if we need to. Values can't
            // be longer than 5 bytes, though.

            int signBit = sign ? 0x40 : 0x00;

            if ((bytes.get(bytes.size() - 1) & 0x40) != signBit)
                bytes.add((byte)(sign ? 0xFF : 0x80));
        }

        // Reverse for endianess
        for (int i = 0; i < bytes.size() / 2; i++) {
            byte temp = bytes.get(i);
            bytes.set(i, bytes.get(bytes.size() - i - 1));
            bytes.set(bytes.size() - i - 1,  temp);
        }

        byte[] ret = new byte[bytes.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = bytes.get(i);
        }

        inBuf.position(inBuf.position() + 4);
        out.write(ret);
    }
}
