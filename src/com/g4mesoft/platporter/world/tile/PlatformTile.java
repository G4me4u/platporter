package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;

public class PlatformTile extends Tile {

	private static final int SPRITE_OFFSET_MASK = 0x03;
	private static final int SPRITE_X_BIT = 0;
	private static final int SPRITE_Y_BIT = 2;
	
	private static final int FLIP_X_MASK = 0x10;
	private static final int FLIP_Y_MASK = 0x20;
	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		int data = world.getData(xt, yt);
		int sx = ((data >>> SPRITE_X_BIT) & SPRITE_OFFSET_MASK);
		int sy = ((data >>> SPRITE_Y_BIT) & SPRITE_OFFSET_MASK) + 5;

		int flags = 0;
		if ((data & FLIP_X_MASK) != 0)
			flags |= Screen2D.MIRROR_X;
		if ((data & FLIP_Y_MASK) != 0)
			flags |= Screen2D.MIRROR_Y;
		screen.drawSprite(xt * 8, yt * 8, sx, sy, ColorPalette.getColors(541, 213, 123, -1), flags);
	}
}
