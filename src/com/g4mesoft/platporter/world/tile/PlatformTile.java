package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;

public class PlatformTile extends Tile {

	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		screen.drawRect(xt << 3, yt << 3, 8, 8, ColorPalette.getColor(551));
	}
}
