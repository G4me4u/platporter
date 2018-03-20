package com.g4mesoft.graphics;

public class ColorPalette {

	public static final int NUM_COLORS = 256;
	public static final int COLORS_PER_CHANNEL = 6;
	public static final int NUM_VISIBLE_COLORS = COLORS_PER_CHANNEL * 
	                                             COLORS_PER_CHANNEL * 
	                                             COLORS_PER_CHANNEL;
	
	private static int[] colors = null;
	
	public final int[] palette;
	
	public ColorPalette() {
		if (colors == null)
			colors = generatePalette();
		palette = colors;
	}
	
	private static int[] generatePalette() {
		int[] palette = new int[NUM_COLORS];
		
		int i = 0;
		for (int r = 0; r < 6; r++) {
			for (int g = 0; g < 6; g++) {
				for (int b = 0; b < 6; b++) {
					int rr = r * 255 / 5;
					int gg = g * 255 / 5;
					int bb = b * 255 / 5;
					int mid = (rr * 30 + gg * 59 + bb * 11) / 100;
					
					int r1 = (rr + mid) / 2 * 230 / 255 + 10;
					int g1 = (gg + mid) / 2 * 230 / 255 + 10;
					int b1 = (bb + mid) / 2 * 230 / 255 + 10;
					palette[i++] = (r1 << 16 | g1 << 8 | b1);
				}
			}
		}
	
		return palette;
	}
	
	public static int getColors(int rgb1, int rgb2, int rgb3, int rgb4) {
		return getColor(rgb4) << 24 | 
		       getColor(rgb3) << 16 |
		       getColor(rgb2) <<  8 |
		       getColor(rgb1) <<  0;
	}
	
	public static int getColor(int rgb) {
		int r = rgb / 100;
		int g = (rgb % 100) / 10;
		int b = rgb % 10;
		
		return (b + (g + r * COLORS_PER_CHANNEL) * COLORS_PER_CHANNEL) & 0xFF;
	}
}
