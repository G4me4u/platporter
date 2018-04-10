package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.world.phys.AABB;

public class BeamStandTile extends BeamTile {
	
	private static final int ON_WALL_MASK = 0x01;
	private static final int MIRROR_STAND_MASK = 0x02;
	private static final int MIRROR_LASER_MASK = 0x04;
	private static final int BEAM_TIME_MASK = 0x24;
	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		super.renderLaser(world, screen, xt, yt);
		
		boolean onWall = isOnWall(world, xt, yt);
		int sy = onWall ? 1 : 0;
		
		int flags = 0;
		if (isStandMirrored(world, xt, yt)) {
			if (onWall) {
				flags |= Screen2D.MIRROR_X;
			} else {
				flags |= Screen2D.MIRROR_Y;
			}
		}
		
		screen.drawSprite(xt * 8, yt * 8, 3, sy, ColorPalette.getColors(541, 213, 123, -1), flags);
	}
	
	@Override
	public AABB getBoundingBox(PPWorld world, int xt, int yt) {
		if (isOnWall(world, xt, yt)) {
			if (isStandMirrored(world, xt, yt))
				return new AABB(xt + 0.625f, yt + 0.125f, xt + 1.0f, yt + 0.875f);
			return new AABB(xt, yt + 0.125f, xt + 0.375f, yt + 0.875f);
		}
		if (isStandMirrored(world, xt, yt))
			return new AABB(xt + 0.125f, yt, xt + 0.875f, yt + 0.375f);
		return new AABB(xt + 0.125f, yt + 0.625f, xt + 0.875f, yt + 1.0f);
	}
	
	@Override
	public boolean isOnWall(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & ON_WALL_MASK) != 0;
	}
	
	@Override
	public boolean isLaserMirrored(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & MIRROR_LASER_MASK) != 0;
	}

	public boolean isStandMirrored(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & MIRROR_STAND_MASK) != 0;
	}

	@Override
	protected int getAnimOffset(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & BEAM_TIME_MASK) >> 3;
	}
}
