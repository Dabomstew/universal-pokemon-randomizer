package com.dabomstew.pkrandom;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

public class GFXFunctions {

    public static BufferedImage drawTiledImage(byte[] data, int[] palette, int width, int height, int bpp) {
        return drawTiledImage(data, palette, 0, width, height, 8, 8, bpp);
    }

    public static BufferedImage drawTiledImage(byte[] data, int[] palette, int offset, int width, int height, int bpp) {
        return drawTiledImage(data, palette, offset, width, height, 8, 8, bpp);
    }

    public static BufferedImage drawTiledImage(byte[] data, int[] palette, int offset, int width, int height,
            int tileWidth, int tileHeight, int bpp) {
        if (bpp != 1 && bpp != 2 && bpp != 4 && bpp != 8) {
            throw new IllegalArgumentException("Bits per pixel must be a multiple of 2.");
        }
        int pixelsPerByte = 8 / bpp;
        if (width * height / pixelsPerByte + offset > data.length) {
            throw new IllegalArgumentException("Invalid input image.");
        }

        int bytesPerTile = tileWidth * tileHeight / pixelsPerByte;
        int numTiles = width * height / (tileWidth * tileHeight);
        int widthInTiles = width / tileWidth;

        BufferedImage bim = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int tile = 0; tile < numTiles; tile++) {
            int tileX = tile % widthInTiles;
            int tileY = tile / widthInTiles;
            for (int yT = 0; yT < tileHeight; yT++) {
                for (int xT = 0; xT < tileWidth; xT++) {
                    int value = data[tile * bytesPerTile + yT * tileWidth / pixelsPerByte + xT / pixelsPerByte + offset] & 0xFF;
                    if (pixelsPerByte != 1) {
                        value = (value >>> (xT % pixelsPerByte) * bpp) & ((1 << bpp) - 1);
                    }
                    bim.setRGB(tileX * tileWidth + xT, tileY * tileHeight + yT, palette[value]);
                }
            }
        }

        return bim;
    }

    public static int conv16BitColorToARGB(int palValue) {
        int red = (int) ((palValue & 0x1F) * 8.25);
        int green = (int) (((palValue & 0x3E0) >> 5) * 8.25);
        int blue = (int) (((palValue & 0x7C00) >> 10) * 8.25);
        return 0xFF000000 | (red << 16) | (green << 8) | blue;
    }

    public static void pseudoTransparency(BufferedImage img, int transColor) {
        int width = img.getWidth();
        int height = img.getHeight();
        Queue<Integer> visitPixels = new LinkedList<Integer>();
        boolean[][] queued = new boolean[width][height];

        for (int x = 0; x < width; x++) {
            queuePixel(x, 0, width, height, visitPixels, queued);
            queuePixel(x, height - 1, width, height, visitPixels, queued);
        }

        for (int y = 0; y < height; y++) {
            queuePixel(0, y, width, height, visitPixels, queued);
            queuePixel(width - 1, y, width, height, visitPixels, queued);
        }

        while (!visitPixels.isEmpty()) {
            int nextPixel = visitPixels.poll();
            int x = nextPixel % width;
            int y = nextPixel / width;
            if (img.getRGB(x, y) == transColor) {
                img.setRGB(x, y, 0);
                queuePixel(x - 1, y, width, height, visitPixels, queued);
                queuePixel(x + 1, y, width, height, visitPixels, queued);
                queuePixel(x, y - 1, width, height, visitPixels, queued);
                queuePixel(x, y + 1, width, height, visitPixels, queued);
            }
        }
    }

    private static void queuePixel(int x, int y, int width, int height, Queue<Integer> queue, boolean[][] queued) {
        if (x >= 0 && x < width && y >= 0 && y < height && !queued[x][y]) {
            queue.add((y) * width + (x));
            queued[x][y] = true;
        }
    }

}
