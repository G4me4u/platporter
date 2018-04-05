package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;

public class LeverTile extends Tile {

	private static final int LEVER_ON_MASK = 0x01;
	private static final int ON_WALL_MASK = 0x02;
	private static final int FLIP_MASK = 0x04;
	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		boolean onWall = isOnWall(world, xt, yt);
		boolean flip = (world.getData(xt, yt) & FLIP_MASK) != 0;

		int sx = onWall ? 0 : 1;
		
		int flags = 0;
		if (onWall) {
			if (isTurnedOn(world, xt, yt))
				flags |= Screen2D.MIRROR_Y;
			if (flip)
				flags |= Screen2D.MIRROR_X;
		} else {
			if (isTurnedOn(world, xt, yt))
				flags |= Screen2D.MIRROR_X;
			if (flip)
				flags |= Screen2D.MIRROR_Y;
		}
		
		screen.drawSprite(xt * 8, yt * 8, sx, 4, ColorPalette.getColors(222, 111, 410, -1), flags);
	}
	
	public boolean isTurnedOn(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & LEVER_ON_MASK) != 0;
	}
	
	public boolean isOnWall(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & ON_WALL_MASK) != 0;
	}
}
