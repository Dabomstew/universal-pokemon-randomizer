package pptxt;

/*----------------------------------------------------------------------------*/
/*--  N3DSTxtHandler.java - text processing for the 3DS Pokemon games       --*/
/*--                                                                        --*/
/*--  Contains code based on "pk3DS", copyright (C) Kaphotics               --*/
/*--  Ported to Java by the UPR-ZX team under the terms of the GPL:         --*/
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

import com.dabomstew.pkrandom.constants.Gen7Constants;
import com.dabomstew.pkrandom.constants.N3DSConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.stream.Collectors;

public class N3DSTxtHandler {

    private static final int KEY_BASE = 0x7C89;
    private static final int KEY_ADVANCE = 0x2983;
    private static final int KEY_TERMINATOR = 0x0000;
    private static final int KEY_VARIABLE = 0x0010;
    private static final int KEY_TEXTRETURN = 0xBE00;
    private static final int KEY_TEXTCLEAR = 0xBE01;
    private static final int KEY_TEXTWAIT = 0xBE02;
    private static final int KEY_TEXTNULL = 0xBDFF;
    private static boolean remapChars = false;
    private static boolean setEmptyText = false;
    private static int romType;

    public static List<String> readTexts(byte[] ds, boolean remapChars, int romType) {
        N3DSTxtHandler.remapChars = remapChars;
        N3DSTxtHandler.romType = romType;
        List<String> strings = new ArrayList<>();
        int numSections, numEntries, totalLength, initialKey, sectionDataOffset, sectionLength;

        numSections = readShort(ds, 0);
        numEntries = readShort(ds, 2);
        totalLength = readLong(ds, 4);
        initialKey = readLong(ds,8);
        sectionDataOffset = readLong(ds, 0xC);
        sectionLength = readLong(ds, sectionDataOffset);

        if (numSections != 1 || initialKey != 0 || sectionLength != totalLength) {
            System.err.println("Invalid text file");
            return new ArrayList<>();
        }

        int[] entryOffsets = new int[numEntries];
        int[] entryLengths = new int[numEntries];

        for (int i = 0; i < numEntries; i++) {
            entryOffsets[i] = readLong(ds, (i * 8) + sectionDataOffset + 4) + sectionDataOffset;
            entryLengths[i] = readShort(ds, (i * 8) + sectionDataOffset + 8);
            byte[] encEntryData = Arrays.copyOfRange(ds, entryOffsets[i], entryOffsets[i] + entryLengths[i]*2);
            strings.add(getEntryString(cryptEntryData(encEntryData,getEntryKey(i))));
        }

        return strings;
    }

    public static byte[] saveEntry(byte[] originalData, List<String> values, int romType) throws IOException {
        int key = KEY_BASE;

        ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
        ByteBuffer offsetsBuf = ByteBuffer.allocate(values.size() * 8);
        offsetsBuf.order(ByteOrder.LITTLE_ENDIAN);
        int dataOffset = 4 + values.size() * 8;
        for (int i = 0; i < values.size(); i++) {
            String text = values.get(i).trim();
            if (text.length() == 0 && setEmptyText) {
                text = String.format("[~ %d]",i);
            }
            byte[] decEntryData = getEntryData(text,romType);
            byte[] encEntryData = cryptEntryData(decEntryData,key);
            offsetsBuf.putInt(dataOffset + dataStream.size());
            offsetsBuf.putShort((short)(encEntryData.length / 2));
            offsetsBuf.position(offsetsBuf.position()+2);
            dataStream.write(encEntryData);
            if (encEntryData.length % 4 == 2) {
                dataStream.write(0);
                dataStream.write(0);
            }
            key = (key + KEY_ADVANCE) & 0xFFFF;
        }

        int sectionDataOffset = 0x10;
        ByteBuffer headerBuf = ByteBuffer.allocate(0x14);
        headerBuf.order(ByteOrder.LITTLE_ENDIAN);
        headerBuf.putShort((short)1);
        headerBuf.putShort((short)values.size());
        headerBuf.putInt(4 + values.size()*8 + dataStream.size());
        headerBuf.putInt(0);
        headerBuf.putInt(sectionDataOffset);
        headerBuf.putInt(4 + values.size()*8 + dataStream.size());

        headerBuf.flip();
        offsetsBuf.flip();

        byte[] fullArray = new byte[headerBuf.limit() + offsetsBuf.limit() + dataStream.size()];
        System.arraycopy(headerBuf.array(),
                0,
                fullArray,
                0,
                headerBuf.limit());
        System.arraycopy(offsetsBuf.array(),
                0,
                fullArray,
                headerBuf.limit(),
                offsetsBuf.limit());
        System.arraycopy(dataStream.toByteArray(),
                0,
                fullArray,
                headerBuf.limit()+offsetsBuf.limit(),
                dataStream.size());

        return fullArray;
    }

    private static byte[] getEntryData(String entry, int romType) throws IOException {
        if (entry == null) {
            return new byte[2];
        }

        ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
        int i = 0;
        while (i < entry.length()) {
            int val = entry.charAt(i++);
            val = tryRemapChar(val);

            if (val == '[') {
                int bracket = entry.indexOf("]",i);
                if (bracket < 0) {
                    throw new IOException("Variable text is not capped properly: " + entry);
                }
                String varText = entry.substring(i,bracket);
                List<Integer> varValues = getVariableValues(varText,romType);
                for (int v: varValues) {
                    dataStream.write(v & 0xFF);
                    dataStream.write((v >>> 8) & 0xFF);
                }
                i += 1 + varText.length();
            } else if (val == '\\') {
                List<Integer> escapeValues = getEscapeValues(entry.charAt(i++));
                for (int escVal: escapeValues) {
                    dataStream.write(escVal & 0xFF);
                    dataStream.write((escVal >>> 8) & 0xFF);
                }
            } else {
                dataStream.write(val & 0xFF);
                dataStream.write((val >>> 8) & 0xFF);
            }
        }
        dataStream.write(KEY_TERMINATOR & 0xFF);
        dataStream.write((KEY_TERMINATOR >>> 8) & 0xFF);
        return dataStream.toByteArray();
    }

    private static byte[] cryptEntryData(byte[] data, int key) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < result.length; i+= 2) {
            int sh = (readShort(data, i) & 0xFFFF) ^ (key & 0xFFFF);
            result[i] = (byte)(sh & 0xFF);
            result[i+1] = (byte)((sh >> 8) & 0xFF);
            key = (key << 3 | key >>> 13) & 0xFFFF;
        }
        return result;
    }

    private static int getEntryKey(int index) {
        int key = KEY_BASE;
        for (int i = 0; i < index; i++) {
            key = (key + KEY_ADVANCE) & 0xFFFF;
        }
        return key;
    }

    private static String getEntryString(byte[] data) {
        if (data == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        int i = 0;

        while (i < data.length) {
            int val = readShort(data, i);
            if (val == KEY_TERMINATOR) break;
            i += 2;

            switch (val) {
                case KEY_VARIABLE:
                    RefInt refI = new RefInt(i);
                    sb.append(getVariableString(data,refI));
                    i = refI.val;
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '[':
                    sb.append("\\[");
                    break;
                default:
                    sb.append((char)tryUnmapChar(val));
            }
        }
        return sb.toString();
    }

    private static List<Integer> getEscapeValues(char esc) throws IOException {
        List<Integer> vals = new ArrayList<>();
        switch (esc) {
            case 'n':
                vals.add((int) '\n');
                return vals;
            case '\\':
                vals.add((int) '\\');
                return vals;
            case '[':
                vals.add((int) '[');
                return vals;
            case 'r':
                vals.add(KEY_VARIABLE);
                vals.add(1);
                vals.add(KEY_TEXTRETURN);
                return vals;
            case 'c':
                vals.add(KEY_VARIABLE);
                vals.add(1);
                vals.add(KEY_TEXTCLEAR);
                return vals;
            default:
                throw new IOException("Invalid terminated line: \\" + esc);
        }
    }

    private static List<Integer> getVariableValues(String variable, int romType) throws IOException {
        String[] splitString = variable.split(" ");
        if (splitString.length < 2) {
            throw new IOException("Incorrectly formatted variable text: " + variable);
        }

        List<Integer> vals = new ArrayList<>();
        vals.add(KEY_VARIABLE);
        switch (splitString[0]) {
            case "~":
                vals.add(1);
                vals.add(KEY_TEXTNULL);
                vals.add(Integer.parseInt(splitString[1]));
                break;
            case "WAIT":
                vals.add(1);
                vals.add(KEY_TEXTWAIT);
                vals.add(Integer.parseInt(splitString[1]));
                break;
            case "VAR":
                vals.addAll(getVariableParameters(splitString[1],romType));
                break;
            default:
                throw new IOException("Unknown variable method type: " + variable);
        }
        return vals;
    }

    private static List<Integer> getVariableParameters(String text, int romType) throws IOException {
        List<Integer> vals = new ArrayList<>();
        int bracket = text.indexOf("(");
        boolean noArgs = bracket < 0;
        String variable = noArgs ? text : text.substring(0,bracket);
        int varVal = getVariableNumber(variable,romType);
        if (!noArgs) {
            String[] args = text.substring(bracket + 1, text.length() - 2).split(",");
            vals.add(1 + args.length);
            vals.add(varVal);
            vals.addAll(Arrays.stream(args)
                    .mapToInt(s -> Integer.parseInt(s,16))
                    .boxed()
                    .collect(Collectors.toList()));

        } else {
            vals.add(1);
            vals.add(varVal);
        }
        return vals;
    }

    private static int getVariableNumber(String variable, int romType) throws IOException {
        int v = N3DSConstants.getVariableCode(variable,romType);
        if (v != 0) {
            return v;
        }
        try {
            return Integer.parseInt(variable);
        } catch (NumberFormatException e) {
            throw new IOException("Variable parse error: " + variable);
        }
    }

    private static String getVariableString(byte[] data, RefInt refI) {
        StringBuilder sb = new StringBuilder();
        int count = readShort(data,refI.val);
        refI.val += 2;
        int variable = readShort(data,refI.val);
        refI.val += 2;

        switch (variable) {
            case KEY_TEXTRETURN:
                return "\\r";
            case KEY_TEXTCLEAR:
                return "\\c";
            case KEY_TEXTWAIT:
                int time = readShort(data, refI.val);
                refI.val += 2;
                return String.format("[WAIT %d]", time);
            case KEY_TEXTNULL:
                int line = readShort(data,refI.val);
                refI.val += 2;
                return String.format("[~ %d]",line);
        }

        String varName = N3DSConstants.getTextVariableCodes(romType).getOrDefault(variable,String.format("%04X",variable));
        sb.append("[VAR ").append(varName);
        if (count > 1) {
            sb.append("(");
            while (count > 1 && refI.val < data.length) {
                int arg = readShort(data,refI.val);
                refI.val += 2;
                sb.append(String.format("%04X",arg));
                if (--count == 1 || refI.val >= data.length) break;
                sb.append(",");
            }
            sb.append(")");
        }
        sb.append("]");
        return sb.toString();
    }

    private static int tryRemapChar(int val) {
        if (!remapChars) {
            return val;
        }
        switch (val) {
            case 0x202F:
                return 0xE07F;
            case 0x2026:
                return 0xE08D;
            case 0x2642:
                return 0xE08E;
            case 0x2640:
                return 0xE08F;
            default:
                return val;
        }
    }

    private static int tryUnmapChar(int val) {
        if (!remapChars) {
            return val;
        }
        switch (val & 0xFFFF) {
            case 0xE07F:
                return 0x202F;
            case 0xE08D:
                return 0x2026;
            case 0xE08E:
                return 0x2642;
            case 0xE08F:
                return 0x2640;
            default:
                return val;
        }
    }

    private static int readShort(byte[] data, int offset) {
        return (data[offset] & 0xFF) + ((data[offset + 1] & 0xFF) << 8);
    }

    private static int readLong(byte[] data, int offset) {
        return (data[offset] & 0xFF) + ((data[offset + 1] & 0xFF) << 8) + ((data[offset + 2] & 0xFF) << 16)
                + ((data[offset + 3] & 0xFF) << 24);
    }

    private static class RefInt {
        int val;

        RefInt(int val) {
            this.val = val;
        }
    }
}
