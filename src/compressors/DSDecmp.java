package compressors;

import com.dabomstew.pkrandom.FileFunctions;

//MODIFIED DSDECMP-JAVA SOURCE FOR RANDOMIZER'S NEEDS
//License is below

//Copyright (c) 2010 Nick Kraayenbrink
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

public class DSDecmp {

    public static byte[] Decompress(byte[] data) {
        return Decompress(data, 0);
    }

    public static byte[] Decompress(byte[] data, int offset) {
        switch (data[offset] & 0xFF) {
        case 0x10:
            return decompress10LZ(data, offset);
        case 0x11:
            return decompress11LZ(data, offset);
        default:
            return null;
        }
    }

    private static byte[] decompress10LZ(byte[] data, int offset) {
        offset++;
        int length = (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8) | ((data[offset + 2] & 0xFF) << 16);
        offset += 3;
        if (length == 0) {
            length = FileFunctions.readFullInt(data, offset);
            offset += 4;
        }

        byte[] outData = new byte[length];
        int curr_size = 0;
        int flags;
        boolean flag;
        int disp, n, b, cdest;
        while (curr_size < outData.length) {
            flags = data[offset++] & 0xFF;
            for (int i = 0; i < 8; i++) {
                flag = (flags & (0x80 >> i)) > 0;
                if (flag) {
                    disp = 0;
                    b = data[offset++] & 0xFF;
                    n = b >> 4;
                    disp = (b & 0x0F) << 8;
                    disp |= data[offset++] & 0xFF;
                    n += 3;
                    cdest = curr_size;
                    if (disp > curr_size)
                        throw new ArrayIndexOutOfBoundsException("Cannot go back more than already written");
                    for (int j = 0; j < n; j++)
                        outData[curr_size++] = outData[cdest - disp - 1 + j];

                    if (curr_size > outData.length)
                        break;
                } else {
                    b = data[offset++] & 0xFF;
                    try {
                        outData[curr_size++] = (byte) b;
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        if (b == 0)
                            break;
                    }

                    if (curr_size > outData.length)
                        break;
                }
            }
        }
        return outData;
    }

    private static byte[] decompress11LZ(byte[] data, int offset) {
        offset++;
        int length = (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8) | ((data[offset + 2] & 0xFF) << 16);
        offset += 3;
        if (length == 0) {
            length = FileFunctions.readFullInt(data, offset);
            offset += 4;
        }

        byte[] outData = new byte[length];

        int curr_size = 0;
        int flags;
        boolean flag;
        int b1, bt, b2, b3, len, disp, cdest;

        while (curr_size < outData.length) {
            flags = data[offset++] & 0xFF;

            for (int i = 0; i < 8 && curr_size < outData.length; i++) {
                flag = (flags & (0x80 >> i)) > 0;
                if (flag) {
                    b1 = data[offset++] & 0xFF;

                    switch (b1 >> 4) {
                    case 0:
                        // ab cd ef
                        // =>
                        // len = abc + 0x11 = bc + 0x11
                        // disp = def

                        len = b1 << 4;
                        bt = data[offset++] & 0xFF;
                        len |= bt >> 4;
                        len += 0x11;

                        disp = (bt & 0x0F) << 8;
                        b2 = data[offset++] & 0xFF;
                        disp |= b2;
                        break;

                    case 1:
                        // ab cd ef gh
                        // =>
                        // len = bcde + 0x111
                        // disp = fgh
                        // 10 04 92 3F => disp = 0x23F, len = 0x149 + 0x11 =
                        // 0x15A
                        bt = data[offset++] & 0xFF;
                        b2 = data[offset++] & 0xFF;
                        b3 = data[offset++] & 0xFF;

                        len = (b1 & 0xF) << 12; // len = b000
                        len |= bt << 4; // len = bcd0
                        len |= (b2 >> 4); // len = bcde
                        len += 0x111; // len = bcde + 0x111
                        disp = (b2 & 0x0F) << 8; // disp = f
                        disp |= b3; // disp = fgh
                        break;

                    default:
                        // ab cd
                        // =>
                        // len = a + threshold = a + 1
                        // disp = bcd

                        len = (b1 >> 4) + 1;

                        disp = (b1 & 0x0F) << 8;
                        b2 = data[offset++] & 0xFF;
                        disp |= b2;
                        break;
                    }

                    if (disp > curr_size)
                        throw new ArrayIndexOutOfBoundsException("Cannot go back more than already written");

                    cdest = curr_size;

                    for (int j = 0; j < len && curr_size < outData.length; j++)
                        outData[curr_size++] = outData[cdest - disp - 1 + j];

                    if (curr_size > outData.length)
                        break;
                } else {
                    outData[curr_size++] = data[offset++];

                    if (curr_size > outData.length)
                        break;
                }
            }

        }
        return outData;
    }

}
