package com.dabomstew.pkrandom.newnds;

/*----------------------------------------------------------------------------*/
/*--  NARCArchive.java - class for packing/unpacking GARC archives          --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
/*--  Originally part of "Universal Pokemon Randomizer" by Dabomstew        --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2020.                                 --*/
/*--                                                                        --*/
/*--  The custom code written here is licensed under the terms of the GPL:  --*/
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class NARCArchive {

    private List<String> filenames = new ArrayList<>();
    public List<byte[]> files = new ArrayList<>();

    private boolean hasFilenames = false;

    public NARCArchive() {
        // creates a new empty NARC with no filenames by default
    }

    public NARCArchive(byte[] data) throws IOException {
        Map<String, byte[]> frames = readNitroFrames(data);
        if (!frames.containsKey("FATB") || !frames.containsKey("FNTB") || !frames.containsKey("FIMG")) {
            throw new IOException("Not a valid narc file");
        }

        // File contents
        byte[] fatbframe = frames.get("FATB");
        byte[] fimgframe = frames.get("FIMG");
        int fileCount = readLong(fatbframe, 0);
        for (int i = 0; i < fileCount; i++) {
            int startOffset = readLong(fatbframe, 4 + i * 8);
            int endOffset = readLong(fatbframe, 8 + i * 8);
            int length = (endOffset - startOffset);
            byte[] thisFile = new byte[length];
            System.arraycopy(fimgframe, startOffset, thisFile, 0, length);
            files.add(thisFile);
        }

        // Filenames?
        byte[] fntbframe = frames.get("FNTB");
        int unk1 = readLong(fntbframe, 0);
        if (unk1 == 8) {
            // Filenames exist
            hasFilenames = true;
            int offset = 8;
            for (int i = 0; i < fileCount; i++) {
                int fnLength = (fntbframe[offset] & 0xFF);
                offset++;
                byte[] filenameBA = new byte[fnLength];
                System.arraycopy(fntbframe, offset, filenameBA, 0, fnLength);
                String filename = new String(filenameBA, "US-ASCII");
                filenames.add(filename);
            }
        } else {
            hasFilenames = false;
            for (int i = 0; i < fileCount; i++) {
                filenames.add(null);
            }
        }
    }

    public byte[] getBytes() throws IOException {
        // Get bytes required for FIMG frame
        int bytesRequired = 0;
        for (byte[] file : files) {
            bytesRequired += Math.ceil(file.length / 4.0) * 4;
        }
        // FIMG frame & FATB frame build

        // 4 for numentries, 8*size for entries, 8 for nitro header
        byte[] fatbFrame = new byte[4 + files.size() * 8 + 8];
        // bytesRequired + 8 for nitro header
        byte[] fimgFrame = new byte[bytesRequired + 8];

        // Nitro headers
        fatbFrame[0] = 'B';
        fatbFrame[1] = 'T';
        fatbFrame[2] = 'A';
        fatbFrame[3] = 'F';
        writeLong(fatbFrame, 4, fatbFrame.length);

        fimgFrame[0] = 'G';
        fimgFrame[1] = 'M';
        fimgFrame[2] = 'I';
        fimgFrame[3] = 'F';
        writeLong(fimgFrame, 4, fimgFrame.length);
        int offset = 0;

        writeLong(fatbFrame, 8, files.size());
        for (int i = 0; i < files.size(); i++) {
            byte[] file = files.get(i);
            int bytesRequiredForFile = (int) (Math.ceil(file.length / 4.0) * 4);
            System.arraycopy(file, 0, fimgFrame, offset + 8, file.length);
            for (int filler = file.length; filler < bytesRequiredForFile; filler++) {
                fimgFrame[offset + 8 + filler] = (byte) 0xFF;
            }
            writeLong(fatbFrame, 12 + i * 8, offset);
            writeLong(fatbFrame, 16 + i * 8, offset + file.length);
            offset += bytesRequiredForFile;
        }

        // FNTB Frame
        int bytesForFNTBFrame = 16;
        if (hasFilenames) {
            for (String filename : filenames) {
                bytesForFNTBFrame += filename.getBytes("US-ASCII").length + 1;
            }
        }
        byte[] fntbFrame = new byte[bytesForFNTBFrame];

        fntbFrame[0] = 'B';
        fntbFrame[1] = 'T';
        fntbFrame[2] = 'N';
        fntbFrame[3] = 'F';
        writeLong(fntbFrame, 4, fntbFrame.length);

        if (hasFilenames) {
            writeLong(fntbFrame, 8, 8);
            writeLong(fntbFrame, 12, 0x10000);
            int fntbOffset = 16;
            for (String filename : filenames) {
                byte[] fntbfilename = filename.getBytes("US-ASCII");
                fntbFrame[fntbOffset] = (byte) fntbfilename.length;
                System.arraycopy(fntbfilename, 0, fntbFrame, fntbOffset + 1, fntbfilename.length);
                fntbOffset += 1 + fntbfilename.length;
            }
        } else {
            writeLong(fntbFrame, 8, 4);
            writeLong(fntbFrame, 12, 0x10000);
        }

        // Now for the actual Nitro file
        int nitrolength = 16 + fatbFrame.length + fntbFrame.length + fimgFrame.length;
        byte[] nitroFile = new byte[nitrolength];
        nitroFile[0] = 'N';
        nitroFile[1] = 'A';
        nitroFile[2] = 'R';
        nitroFile[3] = 'C';
        writeWord(nitroFile, 4, 0xFFFE);
        writeWord(nitroFile, 6, 0x0100);
        writeLong(nitroFile, 8, nitrolength);
        writeWord(nitroFile, 12, 0x10);
        writeWord(nitroFile, 14, 3);
        System.arraycopy(fatbFrame, 0, nitroFile, 16, fatbFrame.length);
        System.arraycopy(fntbFrame, 0, nitroFile, 16 + fatbFrame.length, fntbFrame.length);
        System.arraycopy(fimgFrame, 0, nitroFile, 16 + fatbFrame.length + fntbFrame.length, fimgFrame.length);

        return nitroFile;
    }

    private Map<String, byte[]> readNitroFrames(byte[] data) throws IOException {

        // Read the number of frames
        int frameCount = readWord(data, 0x0E);

        // each frame
        int offset = 0x10;
        Map<String, byte[]> frames = new TreeMap<>();
        for (int i = 0; i < frameCount; i++) {
            byte[] magic = new byte[] { data[offset + 3], data[offset + 2], data[offset + 1], data[offset] };
            String magicS = new String(magic, "US-ASCII");

            int frame_size = readLong(data, offset + 4);
            // Patch for BB/VW and other DS hacks which don't update
            // the size of their expanded NARCs correctly
            if (i == frameCount - 1 && offset + frame_size < data.length) {
                frame_size = data.length - offset;
            }
            byte[] frame = new byte[frame_size - 8];
            System.arraycopy(data, offset + 8, frame, 0, frame_size - 8);
            frames.put(magicS, frame);
            offset += frame_size;
        }
        return frames;
    }

    private int readWord(byte[] data, int offset) {
        return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8);
    }

    private int readLong(byte[] data, int offset) {
        return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8) | ((data[offset + 2] & 0xFF) << 16)
                | ((data[offset + 3] & 0xFF) << 24);
    }

    private void writeWord(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
    }

    private void writeLong(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
        data[offset + 2] = (byte) ((value >> 16) & 0xFF);
        data[offset + 3] = (byte) ((value >> 24) & 0xFF);
    }

}
