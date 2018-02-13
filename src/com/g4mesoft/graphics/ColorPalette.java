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
		for (int r = 0; r < COLORS_PER_CHANNEL; r++) {
			for (int g = 0; g < COLORS_PER_CHANNEL; g++) {
				for (int b = 0; b < COLORS_PER_CHANNEL; b++) {
					int rr = r * 191 / (COLORS_PER_CHANNEL - 1) + 64;
					int gg = g * 191 / (COLORS_PER_CHANNEL - 1) + 64;
					int bb = b * 191 / (COLORS_PER_CHANNEL - 1) + 64;
					palette[i++] = (rr << 16) | (gg << 8) | bb;
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
