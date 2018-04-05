package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;

public class LadderTile extends Tile {

	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		screen.drawSprite(xt * 8, yt * 8, 0, 1, ColorPalette.getColors(222, 333, 444, -1));
	}
	
	@Override
	public boolean isBackgroundLayer(PPWorld world, int xt, int yt) {
		return true;
	}
	
	@Override
	public boolean hasHitbox(PPWorld world, int xt, int yt) {
		return false;
	}
}
