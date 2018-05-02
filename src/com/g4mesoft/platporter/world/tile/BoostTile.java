package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.platporter.world.entity.PPEntity;

public class BoostTile extends Tile {
	
	private static final int X_OFFSET_MASK = 0x01;
	
	@Override
	public void steppedOn(PPWorld world, int xt, int yt, PPEntity entity) {
		entity.velocity.y = -1.0f;
	}
	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		byte data = world.getData(xt, yt);
		int sx = 4;
		if ((data & X_OFFSET_MASK) != 0)
			sx = 5;
		int sy = 6;

		screen.drawSprite(xt * 8, yt * 8, sx, sy, ColorPalette.getColors(541, 213, 123, 005));
	}
}
