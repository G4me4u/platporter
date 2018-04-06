package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;

public class BeamTile extends Tile {
	private static final int ON_WALL_MASK = 0x01;
	private static final int MIRROR_MASK = 0x08;
	private static final int TIME_MASK = 0x02 | 0x04;
	
	
	@Override
	public boolean hasHitbox(PPWorld world, int xt, int yt) {
		return false;
	}
	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		int flags = 0;
		
		byte data = world.getData(xt, yt);
		
		boolean onWall = (data & ON_WALL_MASK) != 0;
		
		int sy = isOnWall(world, xt, yt) ? 1 : 0;
		
		if (isMirrored(world, xt, yt)) {
			if (isOnWall(world, xt, yt)) {
				flags |= Screen2D.MIRROR_X;
			} else {
				flags |= Screen2D.MIRROR_Y;
			}
		}
		
		
		int sx = (int)(world.worldTime / 5L + (3 - ((world.getData(xt, yt) & TIME_MASK) >> 1))) % 4 + 4;
		screen.drawSprite(xt * 8, yt * 8, sx, sy, ColorPalette.getColors(541, 213, 123, -1), flags);
	}
	
	private boolean isOnWall(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & ON_WALL_MASK) != 0;
	}
	private boolean isMirrored(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & MIRROR_MASK) != 0;
	}
}
