package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.platporter.world.PPWorld;

public class AirTile extends Tile {

	@Override
	public boolean hasHitbox(PPWorld world, int xt, int yt) {
		return false;
	}
}
