package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.world.phys.AABB;

public class BeamStandTile extends Tile {
	private static final int ON_WALL_MASK = 0x01;
	private static final int MIRROR_MASK = 0x02;
	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		byte data = world.getData(xt, yt);
		
		boolean onWall = (data & ON_WALL_MASK) != 0;
		boolean mirror = (data & MIRROR_MASK) != 0;
		
		int sy = onWall ? 1 : 0;
		
		int flags = 0;
		
		if (mirror) {
			if (onWall) {
				flags |= Screen2D.MIRROR_X;
			} else {
				flags |= Screen2D.MIRROR_Y;
			}
		}
		
		int sx = (int)(world.worldTime / 5L) % 4 + 4;
		screen.drawSprite(xt * 8, yt * 8, sx, sy, ColorPalette.getColors(541, 213, 123, -1));
		screen.drawSprite(xt * 8, yt * 8, 3, sy, ColorPalette.getColors(541, 213, 123, -1), flags);
	}
	
	@Override
	public AABB getBoundingBox(PPWorld world, int xt, int yt) {
		if (isOnWall(world, xt, yt)) {
			if (isMirrored(world, xt, yt)) {
				return new AABB(xt + 0.625f, yt + 0.125f, xt + 1.0f, yt + 0.875f);
			} else {
				return new AABB(xt, yt + 0.125f, xt + 0.375f, yt + 0.875f);
			}
		} else if (isMirrored(world, xt, yt)) {
			return new AABB(xt + 0.125f, yt, xt + 0.875f, yt + 0.375f);
		} else {
			return new AABB(xt + 0.125f, yt + 0.625f, xt + 0.875f, yt + 1.0f);
		}
		
	}
	
	private boolean isOnWall(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & ON_WALL_MASK) != 0;
	}
	private boolean isMirrored(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & MIRROR_MASK) != 0;
	}
}
