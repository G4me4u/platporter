package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;

public class BeamLaserTile extends BeamTile {
	
	private static final int ON_WALL_MASK = 0x01;
	private static final int MIRROR_MASK = 0x08;
	private static final int TIME_MASK = 0x06;
	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		super.renderLaser(world, screen, xt, yt);
	}
	
	@Override
	public boolean isOnWall(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & ON_WALL_MASK) != 0;
	}
	
	@Override
	public boolean isLaserMirrored(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & MIRROR_MASK) != 0;
	}

	@Override
	protected int getAnimOffset(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & TIME_MASK) >> 1;
	}
	
	@Override
	public boolean hasHitbox(PPWorld world, int xt, int yt) {
		return false;
	}
}
