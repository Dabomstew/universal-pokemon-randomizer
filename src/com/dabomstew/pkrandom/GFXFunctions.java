package com.dabomstew.pkrandom;

import java.awt.image.BufferedImage;

public class GFXFunctions {

	public static BufferedImage drawTiledImage(byte[] data, int[] palette,
			int width, int height, int bpp) {
		return drawTiledImage(data, palette, 0, width, height, 8, 8, bpp);
	}
	
	public static BufferedImage drawTiledImage(byte[] data, int[] palette, int offset,
			int width, int height, int bpp) {
		return drawTiledImage(data, palette, offset, width, height, 8, 8, bpp);
	}

	public static BufferedImage drawTiledImage(byte[] data, int[] palette, int offset,
			int width, int height, int tileWidth, int tileHeight, int bpp) {
		if (bpp != 1 && bpp != 2 && bpp != 4 && bpp != 8) {
			throw new IllegalArgumentException(
					"Bits per pixel must be a multiple of 2.");
		}
		int pixelsPerByte = 8 / bpp;
		if (width * height / pixelsPerByte + offset > data.length) {
			throw new IllegalArgumentException("Invalid input image.");
		}

		int bytesPerTile = tileWidth * tileHeight / pixelsPerByte;
		int numTiles = width * height / (tileWidth * tileHeight);
		int widthInTiles = width / tileWidth;

		BufferedImage bim = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);

		for (int tile = 0; tile < numTiles; tile++) {
			int tileX = tile % widthInTiles;
			int tileY = tile / widthInTiles;
			for (int yT = 0; yT < tileHeight; yT++) {
				for (int xT = 0; xT < tileWidth; xT++) {
					int value = data[tile * bytesPerTile + yT * tileWidth
							/ pixelsPerByte + xT / pixelsPerByte + offset] & 0xFF;
					if (pixelsPerByte != 1) {
						value = (value >>> (xT % pixelsPerByte) * bpp)
								& ((1 << bpp) - 1);
					}
					bim.setRGB(tileX * tileWidth + xT, tileY * tileHeight + yT,
							palette[value]);
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

}
