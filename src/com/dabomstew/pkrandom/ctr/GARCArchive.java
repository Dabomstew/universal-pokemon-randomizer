package com.dabomstew.pkrandom.ctr;

import cuecompressors.BLZCoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class GARCArchive {

    private final int VER_4 = 0x0400;
    private final int VER_6 = 0x0600;
    private final int garcHeaderSize_4 = 0x1C;
    private final int garcHeaderSize_6 = 0x24;
    private final String garcMagic = "CRAG";
    private final String fatoMagic = "OTAF";
    private final String fatbMagic = "BTAF";
    private final String fimbMagic = "BMIF";
    private boolean skipDecompression;

    public List<Map<Integer,byte[]>> files = new ArrayList<>();
    private Map<Integer,Boolean> isCompressed = new TreeMap<>();

    private GARCFrame garc;
    private FATOFrame fato;
    private FATBFrame fatb;
    private FIMBFrame fimb;

    public GARCArchive() {

    }

    public GARCArchive(byte[] data, boolean skipDecompression) throws IOException {
        this.skipDecompression = skipDecompression;
        boolean success = readFrames(data);
        if (!success) {
            throw new IOException("Invalid GARC file");
        }
        files = fimb.files;
    }

    private boolean readFrames(byte[] data) {
        ByteBuffer bbuf = ByteBuffer.wrap(data);
        bbuf.order(ByteOrder.LITTLE_ENDIAN);
        // GARC
        byte[] magicBuf = new byte[4];
        bbuf.get(magicBuf);
        String magic = new String(magicBuf);
        if (!magic.equals(garcMagic)) {
            return false;
        }
        garc = new GARCFrame();
        garc.headerSize = bbuf.getInt();
        garc.endianness = bbuf.getShort();
        garc.version = bbuf.getShort();
        int frameCount = bbuf.getInt();
        if (frameCount != 4) {
            return false;
        }
        garc.dataOffset = bbuf.getInt();
        garc.fileSize = bbuf.getInt();
        if (garc.version == VER_4) {
            garc.contentLargestUnpadded = bbuf.getInt();
            garc.contentPadToNearest = 4;
        } else if (garc.version == VER_6) {
            garc.contentLargestPadded = bbuf.getInt();
            garc.contentLargestUnpadded = bbuf.getInt();
            garc.contentPadToNearest = bbuf.getInt();
        } else {
            return false;
        }

        // FATO
        fato = new FATOFrame();
        bbuf.get(magicBuf);
        magic = new String(magicBuf);
        if (!magic.equals(fatoMagic)) {
            return false;
        }
        fato.headerSize = bbuf.getInt();
        fato.entryCount = bbuf.getShort();
        fato.padding = bbuf.getShort();
        fato.entries = new int[fato.entryCount];
        for (int i = 0; i < fato.entryCount; i++) {
            fato.entries[i] = bbuf.getInt();
        }

        // FATB
        fatb = new FATBFrame();
        bbuf.get(magicBuf);
        magic = new String(magicBuf);
        if (!magic.equals(fatbMagic)) {
            return false;
        }
        fatb.headerSize = bbuf.getInt();
        fatb.fileCount = bbuf.getInt();
        fatb.entries = new FATBEntry[fatb.fileCount];
        for (int i = 0; i < fatb.fileCount; i++) {
            fatb.entries[i] = new FATBEntry();
            fatb.entries[i].vector = bbuf.getInt();
            fatb.entries[i].subEntries = new TreeMap<>();
            int bitVector = fatb.entries[i].vector;
            int counter = 0;
            for (int b = 0; b < 32; b++) {
                boolean exists = (bitVector & 1) == 1;
                bitVector >>>= 1;
                if (!exists) continue;
                FATBSubEntry subEntry = new FATBSubEntry();
                subEntry.start = bbuf.getInt();
                subEntry.end = bbuf.getInt();
                subEntry.length = bbuf.getInt();
                fatb.entries[i].subEntries.put(b,subEntry);
                counter++;
            }
            fatb.entries[i].isFolder = counter > 1;
        }

        // FIMB
        fimb = new FIMBFrame();
        bbuf.get(magicBuf);
        magic = new String(magicBuf);
        if (!magic.equals(fimbMagic)) {
            return false;
        }
        fimb.headerSize = bbuf.getInt();
        fimb.dataSize = bbuf.getInt();
        fimb.files = new ArrayList<>();
        for (int i = 0; i < fatb.fileCount; i++) {
            FATBEntry entry = fatb.entries[i];
            Map<Integer,byte[]> files = new TreeMap<>();
            for (int k: entry.subEntries.keySet()) {
                FATBSubEntry subEntry = entry.subEntries.get(k);
                bbuf.position(garc.dataOffset + subEntry.start);
                byte[] file = new byte[subEntry.length];
                boolean compressed = bbuf.get(bbuf.position()) == 0x11 && !skipDecompression;
                bbuf.get(file);
                if (compressed) {
                    try {
                        files.put(k,new BLZCoder(null).BLZ_DecodePub(file,"GARC"));
                        isCompressed.put(i,true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                } else {
                    files.put(k,file);
                    isCompressed.put(i,false);
                }
            }
            fimb.files.add(files);
        }
        return true;
    }

    public void updateFiles(List<Map<Integer,byte[]>> files) {
        fimb.files = files;
    }

    public byte[] getBytes() throws IOException {
        int garcHeaderSize = garc.version == VER_4 ? garcHeaderSize_4 : garcHeaderSize_6;
        ByteBuffer garcBuf = ByteBuffer.allocate(garcHeaderSize);
        garcBuf.order(ByteOrder.LITTLE_ENDIAN);
        garcBuf.put(garcMagic.getBytes());
        garcBuf.putInt(garcHeaderSize);
        garcBuf.putShort((short)0xFEFF);
        garcBuf.putShort((short)VER_4);
        garcBuf.putInt(4);

        ByteBuffer fatoBuf = ByteBuffer.allocate(fato.headerSize);
        fatoBuf.order(ByteOrder.LITTLE_ENDIAN);
        fatoBuf.put(fatoMagic.getBytes());
        fatoBuf.putInt(fato.headerSize);
        fatoBuf.putShort((short)fato.entryCount);
        fatoBuf.putShort((short)fato.padding);

        ByteBuffer fatbBuf = ByteBuffer.allocate(fatb.headerSize);
        fatbBuf.order(ByteOrder.LITTLE_ENDIAN);
        fatbBuf.put(fatbMagic.getBytes());
        fatbBuf.putInt(fatb.headerSize);
        fatbBuf.putInt(fatb.fileCount);

        ByteBuffer fimbHeaderBuf = ByteBuffer.allocate(fimb.headerSize);
        fimbHeaderBuf.order(ByteOrder.LITTLE_ENDIAN);
        fimbHeaderBuf.put(fimbMagic.getBytes());
        fimbHeaderBuf.putInt(fimb.headerSize);

        ByteArrayOutputStream fimbPayloadStream = new ByteArrayOutputStream(); // Unknown size, can't use ByteBuffer

        int fimbOffset = 0;
        int largestSize = 0;
        int largestPadded = 0;
        for (int i = 0; i < fimb.files.size(); i++) {
            Map<Integer,byte[]> directory = fimb.files.get(i);
            int bitVector = 0;
            int totalLength = 0;
            for (int k: directory.keySet()) {
                bitVector |= (1 << k);
                byte[] file = directory.get(k);
                if (isCompressed.get(i)) {
                    file = new BLZCoder(null).BLZ_EncodePub(file,false,false,"GARC");
                }
                fimbPayloadStream.write(file);
                totalLength += file.length;
            }

            int paddingRequired = totalLength % garc.contentPadToNearest;
            if (paddingRequired != 0) {
                paddingRequired = garc.contentPadToNearest - paddingRequired;
            }

            if (totalLength > largestSize) {
                largestSize = totalLength;
            }
            if (totalLength + paddingRequired > largestPadded) {
                largestPadded = totalLength + paddingRequired;
            }

            for (int j = 0; j < paddingRequired; j++) {
                fimbPayloadStream.write(fato.padding & 0xFF);
            }

            fatoBuf.putInt(fatbBuf.position() - 12);

            fatbBuf.putInt(bitVector);
            fatbBuf.putInt(fimbOffset);
            fimbOffset = fimbPayloadStream.size();
            fatbBuf.putInt(fimbOffset);
            fatbBuf.putInt(totalLength);
        }

        int dataOffset = garcHeaderSize + fatoBuf.position() + fatbBuf.position() + fimb.headerSize;
        garcBuf.putInt(dataOffset);
        garcBuf.putInt(dataOffset + fimbOffset);
        if (garc.version == VER_4) {
            garcBuf.putInt(largestSize);
        } else if (garc.version == VER_6) {
            garcBuf.putInt(largestPadded);
            garcBuf.putInt(largestSize);
            garcBuf.putInt(garc.contentPadToNearest);
        }
        fimbHeaderBuf.putInt(fimbPayloadStream.size());

        garcBuf.flip();
        fatoBuf.flip();
        fatbBuf.flip();
        fimbHeaderBuf.flip();

        byte[] fullArray = new byte[garcBuf.limit() + fatoBuf.limit() + fatbBuf.limit() + fimbHeaderBuf.limit() + fimbPayloadStream.size()];
        System.arraycopy(garcBuf.array(),
                0,
                fullArray,
                0,
                garcBuf.limit());
        System.arraycopy(fatoBuf.array(),
                0,
                fullArray,
                garcBuf.limit(),
                fatoBuf.limit());
        System.arraycopy(fatbBuf.array(),
                0,
                fullArray,
                garcBuf.limit()+fatoBuf.limit(),
                fatbBuf.limit());
        System.arraycopy(fimbHeaderBuf.array(),
                0,
                fullArray,
                garcBuf.limit()+fatoBuf.limit()+fatbBuf.limit(),
                fimbHeaderBuf.limit());
//        garcBuf.get(fullArray);
//        fatoBuf.get(fullArray,garcBuf.limit(),fatoBuf.limit());
//        fatbBuf.get(fullArray,garcBuf.limit()+fatoBuf.limit(),fatbBuf.limit());
//        fimbHeaderBuf.get(fullArray,garcBuf.limit()+fatoBuf.limit()+fatbBuf.limit(),fimbHeaderBuf.limit());
        System.arraycopy(fimbPayloadStream.toByteArray(),
                0,
                fullArray,
                garcBuf.limit()+fatoBuf.limit()+fatbBuf.limit()+fimbHeaderBuf.limit(),
                fimbPayloadStream.size());
        return fullArray;
    }



    public byte[] getFile(int index) {
        return fimb.files.get(index).get(0);
    }

    public byte[] getFile(int index, int subIndex) {
        return fimb.files.get(index).get(subIndex);
    }

    public void setFile(int index, byte[] data) {
        fimb.files.get(index).put(0,data);
    }

    public Map<Integer,byte[]> getDirectory(int index) {
        return fimb.files.get(index);
    }

    private class GARCFrame {
        int headerSize;
        int endianness;
        int version;
        int dataOffset;
        int fileSize;

        int contentLargestPadded;
        int contentLargestUnpadded;
        int contentPadToNearest;
    }

    private class FATOFrame {
        int headerSize;
        int entryCount;
        int padding;

        int[] entries;
    }

    private class FATBFrame {
        int headerSize;
        int fileCount;
        FATBEntry[] entries;
    }

    private class FATBEntry {
        int vector;
        boolean isFolder;
        Map<Integer,FATBSubEntry> subEntries;
    }

    private class FATBSubEntry {
        boolean exists;
        int start;
        int end;
        int length;
        int padding;
    }

    private class FIMBFrame {
        int headerSize;
        int dataSize;
        List<Map<Integer,byte[]>> files;
    }
}
