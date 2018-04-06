package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.world.phys.AABB;

public class BeamStandTile extends Tile {
	private static final int ON_WALL_MASK = 0x01;
	private static final int MIRROR_STAND_MASK = 0x02;
	private static final int MIRROR_BEAM_MASK = 0x04;
	private static final int BEAM_TIME_MASK = 0x08 | 0x016;
	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		byte data = world.getData(xt, yt);
		
		boolean onWall = (data & ON_WALL_MASK) != 0;
		boolean mirror = (data & MIRROR_STAND_MASK) != 0;
		boolean beamMirror = (data & MIRROR_BEAM_MASK) != 0;
		
		int sy = onWall ? 1 : 0;
		
		int standFlags = 0;
		int beamFlags = 0;
		
		if (beamMirror) {
			if (onWall) {
				beamFlags |= Screen2D.MIRROR_X;
			} else {
				beamFlags |= Screen2D.MIRROR_Y;
			}
		}
		
		if (mirror) {
			if (onWall) {
				standFlags |= Screen2D.MIRROR_X;
			} else {
				standFlags |= Screen2D.MIRROR_Y;
			}
		}
		
		int sx = (int)(world.worldTime / 5L + ((world.getData(xt, yt) & BEAM_TIME_MASK) >> 3)) % 4 + 4;
		screen.drawSprite(xt * 8, yt * 8, sx, sy, ColorPalette.getColors(541, 213, 123, -1), beamFlags);
		screen.drawSprite(xt * 8, yt * 8, 3, sy, ColorPalette.getColors(541, 213, 123, -1), standFlags);
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
		return (world.getData(xt, yt) & MIRROR_STAND_MASK) != 0;
	}
}
