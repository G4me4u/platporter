package com.g4mesoft.graphics;

import java.util.Arrays;

public class Screen2D {

	private final int[] pixels;
	private final int width;
	private final int height;
	
	private final ColorPalette palette;
	
	public Screen2D(int[] pixels, int width, int height) {
		this.pixels = pixels;
		this.width = width;
		this.height = height;
		
		palette = new ColorPalette();
	}
	
	public void clear(int color) {
		Arrays.fill(pixels, palette.palette[color & 0xFF]);
	}
	
	public void setPixel(int xp, int yp, int color) {
		pixels[xp + yp * width] = palette.palette[color & 0xFF];
	}
	
	public void render(float dt) {
		
	}
	
	public void drawSprite(SpriteSheet sheet, int xo, int yo, int colors) {
		int x0 = Math.max(0, xo);
		int y0 = Math.max(0, yo);
		int x1 = Math.min(xo + sheet.width, width);
		int y1 = Math.min(yo + sheet.height, height);

		int bi = x0 + y0 * width;
		int si = (x0 - xo) + (y0 - yo) * sheet.width;
		for (int y = y0; y < y1; y++) {
			int bic = bi;
			int sic = si;
			for (int x = x0; x < x1; x++) {
				int col = (colors >> (sheet.pixels[si++] * 8)) & 0xFF;
				if (col < ColorPalette.NUM_VISIBLE_COLORS)
					pixels[bi++] = palette.palette[col];
			}
			bi = bic + width;
			si = sic + sheet.width;
		}
	}
}
