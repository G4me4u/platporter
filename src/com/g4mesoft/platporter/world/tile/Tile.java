package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.world.phys.AABB;

public class Tile {

	public static final Tile[] tiles = new Tile[256];
	private static int nextTileId = 0;

	public static final Tile AIR_TILE = new AirTile();
	public static final Tile PLATFORM_TILE = new PlatformTile();
	public static final Tile LADDER_TILE = new LadderTile();
	
	public final int index;
	
	protected Tile() {
		index = nextTileId++;
		if (tiles[index] != null)
			throw new IllegalArgumentException("Duplicate tile-id!");
		
		tiles[index] = this;
	}
	
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
	}

	public boolean hasHitbox() {
		return true;
	}
	
	public AABB getBoundingBox(PPWorld world, int xt, int yt) {
		return new AABB(xt, yt, xt + 1.0f, yt + 1.0f);
	}
}
