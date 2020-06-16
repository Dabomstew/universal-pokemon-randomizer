package com.dabomstew.pkrandom.ctr;

import cuecompressors.BLZCoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class GARCArchive {

    private final int VER_4 = 0x0400;
    private final int VER_6 = 0x0600;
    private final String garcMagic = "CRAG";
    private final String fatoMagic = "OTAF";
    private final String fatbMagic = "BTAF";
    private final String fimbMagic = "BMIF";

    public List<Map<Integer,byte[]>> files = new ArrayList<>();

    private GARCFrame garc;
    private FATOFrame fato;
    private FATBFrame fatb;
    private FIMBFrame fimb;

    public GARCArchive() {

    }

    public GARCArchive(byte[] data) throws IOException {
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
        fatb.entries = new FATBEntry[fato.entryCount];
        for (int i = 0; i < fato.entryCount; i++) {
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
                boolean compressed = bbuf.get(bbuf.position() + 1) == 0x11;
                bbuf.get(file);
                if (compressed) {
                    files.put(k,new BLZCoder(null).BLZ_DecodePub(file,"garc"));
                } else {
                    files.put(k,file);
                }
            }
            fimb.files.add(files);
        }
        return true;
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
