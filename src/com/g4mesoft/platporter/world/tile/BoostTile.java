package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;

public class BoostTile extends Tile {

	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		int sx = 4;
		int sy = 6;

		screen.drawSprite(xt * 8, yt * 8, sx, sy, ColorPalette.getColors(541, 213, 123, 005));
	}
}
