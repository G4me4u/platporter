package com.g4mesoft.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Screen2D {

	private static final String TILE_SHEET_LOCATION = "/assets/tilesheet.png";
	public static final int MIRROR_X = 1;
	public static final int MIRROR_Y = 2;
	
	private final int[] pixels;
	private final int width;
	private final int height;
	
	private final ColorPalette palette;
	private final SpriteSheet sheet;
	
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
		sheet = new SpriteSheet(tileSheetImage);
	}
	
	public void clear(int color) {
		Arrays.fill(pixels, palette.palette[color & 0xFF]);
	}
	
	public void setPixel(int xp, int yp, int color) {
		pixels[xp + yp * width] = palette.palette[color & 0xFF];
	}
	
	public void drawRect(int x0, int y0, int w, int h, int color) {
		if (color >= ColorPalette.NUM_VISIBLE_COLORS)
			return;
		for (int yy = y0; yy < y0 + h; yy++) {
			if (yy < 0 || yy >= height) continue;
			int pi = x0 + yy * width;
			for (int xx = x0; xx < x0 + w; xx++, pi++	) {
				if (xx < 0 || xx >= width) continue;
				pixels[pi] = palette.palette[color];
			}
		}
	}
	
	public void drawSprite(int x, int y, int xt, int yt, int colors) {
		drawSprite(x, y, xt, yt, colors, 0);
	}

	public void drawSprite(int x, int y, int xt, int yt, int colors, int flags) {
		int sx = xt << 3;
		int sy = yt << 3;

		boolean mirrorX = (flags & MIRROR_X) != 0;
		boolean mirrorY = (flags & MIRROR_Y) != 0;
		
		int sax = mirrorX ? -1 : 1;
		int say = mirrorY ? -1 : 1;
		
		if (mirrorY) 
			sy += 7;
		for (int yy = y; yy < y + 8; yy++, sy += say) {
			if (yy < 0 || yy >= height) continue;
			int pi = x + yy * width;
			int si = sx + sy * sheet.width + (mirrorX ? 7 : 0);
			for (int xx = x; xx < x + 8; xx++, pi++, si += sax) {
				if (xx < 0 || xx >= width) continue;
				int colIndex = (colors >>> (sheet.pixels[si] << 3)) & 0xFF;
				if (colIndex < ColorPalette.NUM_VISIBLE_COLORS)
					pixels[pi] = palette.palette[colIndex];
			}
		}
	}

	public ColorPalette getPalette() {
		return palette;
	}
}
