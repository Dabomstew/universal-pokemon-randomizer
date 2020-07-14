package com.dabomstew.pkrandom.ctr;

/*----------------------------------------------------------------------------*/
/*--  BFLIM.java - class for reading/parsing BFLIM images.                  --*/
/*--               Note that this class is optimized around handling Gen 7  --*/
/*--               Pokemon icons, and won't work for all types of BFLIMs    --*/
/*--                                                                        --*/
/*--  Code based on "Switch Toolbox", copyright (C) KillzXGaming            --*/
/*--                                                                        --*/
/*--  Ported to Java by UPR-ZX Team under the terms of the GPL:             --*/
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

import java.awt.image.BufferedImage;

public class BFLIM {

    private int width;
    private int height;
    private byte[] imageData;
    private Header header;
    private Image image;

    public BFLIM(byte[] bflimBytes) {
        if (bflimBytes.length < 0x28) {
            throw new IllegalArgumentException("Invalid BFLIM: not long enough to contain a header");
        }
        header = new Header(bflimBytes);
        image = new Image(bflimBytes);
        width = image.width;
        height = image.height;
        imageData = new byte[image.imageSize];
        System.arraycopy(bflimBytes, 0, imageData, 0, image.imageSize);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public BufferedImage getImage() {
        // Swap width and height, because the image is rendered on its side
        int swappedWidth = height;
        int swappedHeight = width;
        byte[] decodedImageData = decodeBlock(imageData, swappedWidth, swappedHeight);
        int[] colorData = convertToColorData(decodedImageData);
        int[] correctedColorData = rearrangeImage(colorData, width, height);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, correctedColorData[x + (y * this.width)]);
            }
        }
        return image;
    }

    private static int[] SwizzleLUT = {
             0,  1,  8,  9,  2,  3, 10, 11,
            16, 17, 24, 25, 18, 19, 26, 27,
             4,  5, 12, 13,  6,  7, 14, 15,
            20, 21, 28, 29, 22, 23, 30, 31,
            32, 33, 40, 41, 34, 35, 42, 43,
            48, 49, 56, 57, 50, 51, 58, 59,
            36, 37, 44, 45, 38, 39, 46, 47,
            52, 53, 60, 61, 54, 55, 62, 63
    };

    private byte[] decodeBlock(byte[] data, int width, int height) {
        byte[] output = new byte[width * height * 4];
        int inputOffset = 0;
        for (int ty = 0; ty < height; ty += 8) {
            for (int tx = 0; tx < width; tx += 8) {
                for (int px = 0; px < 64; px++) {
                    int x = SwizzleLUT[px] & 7;
                    int y = (SwizzleLUT[px] - x) >> 3;
                    int outputOffset = (tx + x + ((height - 1 - (ty + y)) * width)) * 4;
                    int value = FileFunctions.read2ByteInt(data, inputOffset);
                    decodeRGBA5551(output, outputOffset, value);
                    inputOffset += 2;
                }
            }
        }
        return output;
    }

    private int[] convertToColorData(byte[] decodedImageData) {
        int[] output = new int[decodedImageData.length / 4];
        for (int i = 0; i < decodedImageData.length; i += 4) {
            int a = decodedImageData[i];
            int r = decodedImageData[i + 1];
            int g = decodedImageData[i + 2];
            int b = decodedImageData[i + 3];
            int color = (a << 24) | (r << 16) | (g << 8) | b;
            output[i / 4] = color;
        }
        return output;
    }

    private int[] rearrangeImage(int[] colorData, int width, int height) {
        int[] output = new int[colorData.length];
        for (int destY = 0; destY < height; destY++) {
            for (int destX = 0; destX < width; destX++) {
                int srcX = height - destY - 1;
                int srcY = width - destX - 1;
                int srcIndex = srcX + (srcY * height);
                int destIndex = destX + (destY * width);
                output[destIndex] = colorData[srcIndex];
            }
        }
        return output;
    }

    private static void decodeRGBA5551(byte[] output, int outputOffset, int value) {
        int R = ((value >> 1) & 0x1f) << 3;
        int G = ((value >> 6) & 0x1f) << 3;
        int B = ((value >> 11) & 0x1f) << 3;
        int A = value & 1;
        output[outputOffset] = (byte) A;
        output[outputOffset + 1] = (byte) R;
        output[outputOffset + 2] = (byte) G;
        output[outputOffset + 3] = (byte) B;
    }

    private class Header {
        public int version;

        public Header(byte[] bflimBytes) {
            int headerOffset = bflimBytes.length - 0x28;
            int signature = FileFunctions.readFullInt(bflimBytes, headerOffset);
            if (signature != 0x464C494D) {
                throw new IllegalArgumentException("Invalid BFLIM: cannot find FLIM header");
            }
            boolean bigEndian = FileFunctions.read2ByteInt(bflimBytes, headerOffset + 4) == 0xFFFE;
            if (bigEndian) {
                throw new IllegalArgumentException("Unsupported BFLIM: this is a big endian BFLIM");
            }
            int headerSize = FileFunctions.read2ByteInt(bflimBytes, headerOffset + 6);
            if (headerSize != 0x14) {
                throw new IllegalArgumentException("Invalid BFLIM: header length does not equal 0x14");
            }
            version = FileFunctions.readFullIntLittleEndian(bflimBytes, headerOffset + 8);
        }
    }

    private class Image {
        public int size;
        public short width;
        public short height;
        public short alignment;
        public byte format;
        public byte flags;
        public int imageSize;

        public Image(byte[] bflimBytes) {
            int imageHeaderOffset = bflimBytes.length - 0x14;
            int signature = FileFunctions.readFullInt(bflimBytes, imageHeaderOffset);
            if (signature != 0x696D6167) {
                throw new IllegalArgumentException("Invalid BFLIM: cannot find imag header");
            }
            size = FileFunctions.readFullIntLittleEndian(bflimBytes, imageHeaderOffset + 4);
            width = (short) FileFunctions.read2ByteInt(bflimBytes, imageHeaderOffset + 8);
            height = (short) FileFunctions.read2ByteInt(bflimBytes, imageHeaderOffset + 10);
            alignment = (short) FileFunctions.read2ByteInt(bflimBytes, imageHeaderOffset + 12);
            format = bflimBytes[imageHeaderOffset + 14];
            flags = bflimBytes[imageHeaderOffset + 15];
            imageSize = FileFunctions.readFullIntLittleEndian(bflimBytes, imageHeaderOffset + 16);
        }
    }
}
