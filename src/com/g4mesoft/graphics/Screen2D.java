package com.g4mesoft.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Screen2D {

	private static final String TILE_SHEET_LOCATION = "/assets/tilesheet.png";
	
	private final int[] pixels;
	private final int width;
	private final int height;
	
	private final ColorPalette palette;
	private final SpriteSheet tileSheet;
	
	public Screen2D(int[] pixels, int width, int height) {
		this.pixels = pixels;
		this.width = width;
		this.height = height;
		
		palette = new ColorPalette();
		
		BufferedImage tileSheetImage = null;
		try {
			tileSheetImage = ImageIO.read(Screen2D.class.getResource(TILE_SHEET_LOCATION));
		} catch (IOException e) {
			e.printStackTrace();
		}
		tileSheet = new SpriteSheet(tileSheetImage);
	}
	
	public void clear(int color) {
		Arrays.fill(pixels, palette.palette[color & 0xFF]);
	}
	
	public void setPixel(int xp, int yp, int color) {
		pixels[xp + yp * width] = palette.palette[color & 0xFF];
	}
	
	public void render(float dt) {
		drawSprite(tileSheet, 0, 0, 0, 0, 0, ColorPalette.getColors(-1, 511, 522, 151));
		drawSprite(tileSheet, 0, 1, 0, 8, 0, ColorPalette.getColors(-1, 511, 522, 151));
		drawSprite(tileSheet, 1, 0, 8, 0, 0, ColorPalette.getColors(-1, 511, 522, 151));
		drawSprite(tileSheet, 1, 1, 8, 8, 0, ColorPalette.getColors(-1, 511, 522, 151));

		drawSprite(tileSheet, 1, 0, 16, 0, 0, ColorPalette.getColors(-1, 511, 522, 151));
		drawSprite(tileSheet, 1, 1, 16, 8, 0, ColorPalette.getColors(-1, 511, 522, 151));
		drawSprite(tileSheet, 2, 0, 24, 0, 0, ColorPalette.getColors(-1, 511, 522, 151));
		drawSprite(tileSheet, 2, 1, 24, 8, 0, ColorPalette.getColors(-1, 511, 522, 151));
	}
	
	public void drawSprite(SpriteSheet sheet, int xt, int yt, int xo, int yo, int flags, int colors) {
		int si = (xt << 3) + (yt << 3) * sheet.width;
		
		int x0 = xo;
		int y0 = yo;
		int x1 = xo + 8;
		int y1 = yo + 8;
		
		if (x0 < 0) {
			x0 -= xo;
			si -= xo;
		} else if (x1 > width) {
			x1 = width;
		}
		if (y0 < 0) {
			y0 -= yo;
			si -= yo * sheet.width;
		} else if (y1 > width) {
			y1 = width;
		}

		int bi = x0 + y0 * width;
		for (int y = y0; y < y1; y++) {
			int bic = bi;
			int sic = si;
			for (int x = x0; x < x1; x++) {
				int col = (colors >> (sheet.pixels[si++] << 3)) & 0xFF;
				if (col < ColorPalette.NUM_VISIBLE_COLORS)
					pixels[bi] = palette.palette[col];
				bi++;
			}
			bi = bic + width;
			si = sic + sheet.width;
		}
	}
}
