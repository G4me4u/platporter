package com.g4mesoft.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Screen2D {

	private static final String TILE_SHEET_LOCATION = "/assets/tilesheet.png";
	public static final int MIRROR_X = 1;
	public static final int MIRROR_Y = 2;
	
	private static final String FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyz1234567890.,!?=+-*/\\<>(){}[]'\"%@:;#$&";
	public static final int MIRROR_X_CHARS = 1;
	public static final int MIRROR_Y_CHARS = 2;
	public static final int CENTER_TEXT_X = 4;
	public static final int CENTER_TEXT_Y = 8;
	public static final int INVERSE_TEXT = 16;
	
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
	
	public void drawSprite(int x, int y, int sx, int sy, int colors) {
		drawSprite(x, y, sx, sy, colors, 0);
	}

	public void drawSprite(int x, int y, int sx, int sy, int colors, int flags) {
		sx <<= 3;
		sy <<= 3;

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
	
	public void drawText(String msg, int x, int y, int colors) {
		drawText(msg, x, y, colors, 0);
	}
	
	public void drawText(String msg, int x, int y, int colors, int flags) {
		int len = msg.length();
		if (len == 0)
			return;

		if ((flags & CENTER_TEXT_X) != 0)
			x -= len * 4;
		if ((flags & CENTER_TEXT_Y) != 0)
			y -= 4;
		if (y < -7 || y >= height) 
			return;
		
		int i, ic;
		if ((flags & INVERSE_TEXT) != 0) {
			ic = -1;
			i = len - 1;
		} else {
			ic = 1;
			i = 0;
		}

		int spriteFlags = 0;
		if ((flags & MIRROR_X_CHARS) != 0)
			spriteFlags |= MIRROR_X;
		if ((flags & MIRROR_Y_CHARS) != 0)
			spriteFlags |= MIRROR_Y;
		
		for ( ; i < len && i >= 0; i += ic) {
			if (x >= width) 
				return;
			if (x < -7)
				continue;

			char c = msg.charAt(i);
			
			if (c != ' ') {
				int index = FONT_CHARACTERS.indexOf(Character.toLowerCase(c));
				
				int sx;
				int sy;
				if (index != -1) {
					sx = index % 16;
					sy = index / 16 + 12;
				} else {
					sx = sy = 15;
				}
				
	 			drawSprite(x, y, sx, sy, colors, spriteFlags);
			}

 			x += 8;
		}
	}

	public ColorPalette getPalette() {
		return palette;
	}
}
