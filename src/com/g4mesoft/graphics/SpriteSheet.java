package com.g4mesoft.graphics;

import java.awt.image.BufferedImage;

public class SpriteSheet {

	protected int width;
	protected int height;
	protected int[] pixels;
	
	public SpriteSheet(BufferedImage image) {
		width = image.getWidth();
		height = image.getHeight();
		pixels = image.getRGB(0, 0, width, height, null, 0, width);
	
		processPixels();
	}
	
	public SpriteSheet(int width, int height, int[] pixels) {
		this.width = width;
		this.height = height;
		this.pixels = pixels;
	}
	
	private void processPixels() {
		for (int i = 0, len = pixels.length; i < len; i++) {
			int b = pixels[i] & 0xFF;
			pixels[i] = b >> 6; // Two bits left
		}
	}
}
