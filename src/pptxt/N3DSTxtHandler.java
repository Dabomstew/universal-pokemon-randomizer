package pptxt;

import com.dabomstew.pkrandom.constants.Gen7Constants;
import com.dabomstew.pkrandom.constants.N3DSConstants;

import java.util.*;

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
