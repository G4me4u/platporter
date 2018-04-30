package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;

public class BarrierTile extends Tile {
	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		screen.drawSprite(xt * 8, yt * 8, 1, 0, ColorPalette.getColors(-1, -1, 100, -1));
	}
}
