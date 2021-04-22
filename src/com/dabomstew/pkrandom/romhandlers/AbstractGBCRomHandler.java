package com.dabomstew.pkrandom.romhandlers;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.constants.GBConstants;

public abstract class AbstractGBCRomHandler extends AbstractGBRomHandler {

    private String[] tb;
    private Map<String, Byte> d;
    private int longestTableToken;

    public AbstractGBCRomHandler(Random random, PrintStream logStream) {
        super(random, logStream);
    }

    protected void clearTextTables() {
        tb = new String[256];
        if (d != null) {
            d.clear();
        } else {
            d = new HashMap<String, Byte>();
        }
        longestTableToken = 0;
    }

    protected void readTextTable(String name) {
        try {
            Scanner sc = new Scanner(FileFunctions.openConfig(name + ".tbl"), "UTF-8");
            while (sc.hasNextLine()) {
                String q = sc.nextLine();
                if (!q.trim().isEmpty()) {
                    String[] r = q.split("=", 2);
                    if (r[1].endsWith("\r\n")) {
                        r[1] = r[1].substring(0, r[1].length() - 2);
                    }
                    int hexcode = Integer.parseInt(r[0], 16);
                    if (tb[hexcode] != null) {
                        String oldMatch = tb[hexcode];
                        tb[hexcode] = null;
                        if (d.get(oldMatch) == hexcode) {
                            d.remove(oldMatch);
                        }
                    }
                    tb[hexcode] = r[1];
                    longestTableToken = Math.max(longestTableToken, r[1].length());
                    d.put(r[1], (byte) hexcode);
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
        }

    }

    protected String readString(int offset, int maxLength, boolean textEngineMode) {
        StringBuilder string = new StringBuilder();
        for (int c = 0; c < maxLength; c++) {
            int currChar = rom[offset + c] & 0xFF;
            if (tb[currChar] != null) {
                string.append(tb[currChar]);
                if (textEngineMode && (tb[currChar].equals("\\r") || tb[currChar].equals("\\e"))) {
                    break;
                }
            } else {
                if (currChar == GBConstants.stringTerminator) {
                    break;
                } else {
                    string.append("\\x" + String.format("%02X", currChar));
                }
            }
        }
        return string.toString();
    }

    protected int lengthOfStringAt(int offset, boolean textEngineMode) {
        int len = 0;
        while (rom[offset + len] != GBConstants.stringTerminator
                && (!textEngineMode || (rom[offset + len] != GBConstants.stringPrintedTextEnd && rom[offset + len] != GBConstants.stringPrintedTextPromptEnd))) {
            len++;
        }

        if (textEngineMode
                && (rom[offset + len] == GBConstants.stringPrintedTextEnd || rom[offset + len] == GBConstants.stringPrintedTextPromptEnd)) {
            len++;
        }
        return len;
    }

    protected byte[] translateString(String text) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (text.length() != 0) {
            int i = Math.max(0, longestTableToken - text.length());
            if (text.charAt(0) == '\\' && text.charAt(1) == 'x') {
                baos.write(Integer.parseInt(text.substring(2, 4), 16));
                text = text.substring(4);
            } else {
                while (!(d.containsKey(text.substring(0, longestTableToken - i)) || (i == longestTableToken))) {
                    i++;
                }
                if (i == longestTableToken) {
                    text = text.substring(1);
                } else {
                    baos.write(d.get(text.substring(0, longestTableToken - i)) & 0xFF);
                    text = text.substring(longestTableToken - i);
                }
            }
        }
        return baos.toByteArray();
    }

    protected String readFixedLengthString(int offset, int length) {
        return readString(offset, length, false);
    }

    // pads the length with terminators, so length should be at least str's len
    // + 1
    protected void writeFixedLengthString(String str, int offset, int length) {
        byte[] translated = translateString(str);
        int len = Math.min(translated.length, length);
        System.arraycopy(translated, 0, rom, offset, len);
        while (len < length) {
            rom[offset + len] = GBConstants.stringTerminator;
            len++;
        }
    }

    protected void writeVariableLengthString(String str, int offset, boolean alreadyTerminated) {
        byte[] translated = translateString(str);
        System.arraycopy(translated, 0, rom, offset, translated.length);
        if (!alreadyTerminated) {
            rom[offset + translated.length] = GBConstants.stringTerminator;
        }
    }

    protected int makeGBPointer(int offset) {
        if (offset < GBConstants.bankSize) {
            return offset;
        } else {
            return (offset % GBConstants.bankSize) + GBConstants.bankSize;
        }
    }

    protected int bankOf(int offset) {
        return (offset / GBConstants.bankSize);
    }

    protected int calculateOffset(int bank, int pointer) {
        if (pointer < GBConstants.bankSize) {
            return pointer;
        } else {
            return (pointer % GBConstants.bankSize) + bank * GBConstants.bankSize;
        }
    }

    protected String readVariableLengthString(int offset, boolean textEngineMode) {
        return readString(offset, Integer.MAX_VALUE, textEngineMode);
    }

    protected static boolean romSig(byte[] rom, String sig) {
        try {
            int sigOffset = GBConstants.romSigOffset;
            byte[] sigBytes = sig.getBytes("US-ASCII");
            for (int i = 0; i < sigBytes.length; i++) {
                if (rom[sigOffset + i] != sigBytes[i]) {
                    return false;
                }
            }
            return true;
        } catch (UnsupportedEncodingException ex) {
            return false;
        }

    }

    protected static boolean romCode(byte[] rom, String code) {
        try {
            int sigOffset = GBConstants.romCodeOffset;
            byte[] sigBytes = code.getBytes("US-ASCII");
            for (int i = 0; i < sigBytes.length; i++) {
                if (rom[sigOffset + i] != sigBytes[i]) {
                    return false;
                }
            }
            return true;
        } catch (UnsupportedEncodingException ex) {
            return false;
        }

    }

}
