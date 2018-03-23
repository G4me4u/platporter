package com.g4mesoft.platporter.world;

import java.util.ArrayList;
import java.util.List;

import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.PlatPorter;
import com.g4mesoft.platporter.world.entity.PPEntity;
import com.g4mesoft.platporter.world.entity.PlayerEntity;
import com.g4mesoft.platporter.world.tile.Tile;
import com.g4mesoft.world.World;
import com.g4mesoft.world.entity.Entity;
import com.g4mesoft.world.phys.AABB;

public class PPWorld extends World {

	private static final int WORLD_WIDTH = 16;
	private static final int WORLD_HEIGHT = 16;
	
	private final PlatPorter platPorter;
	
	private final int[] tiles;
	private final byte[] data;
	
	public PPWorld(PlatPorter platPorter) {
		this.platPorter = platPorter;
	
		tiles = new int[WORLD_WIDTH * WORLD_HEIGHT];
		data = new byte[WORLD_WIDTH * WORLD_HEIGHT];

		for (int yt = 10; yt < 16; yt++) {
			for (int xt = 0; xt < WORLD_WIDTH; xt++) {
				setTile(xt, yt, Tile.PLATFORM_TILE);
			}
		}
		
		addEntity(new PlayerEntity(this));
	}
	
	public void setData(int xt, int yt, byte data) {
		if (xt < 0 || xt >= WORLD_WIDTH) 
			return;
		if (yt < 0 || yt >= WORLD_HEIGHT) 
			return;
		this.data[xt + yt * WORLD_WIDTH] = data;
	}
	
	public void setTile(int xt, int yt, Tile tile) {
		setTileIndex(xt, yt, tile.index);
	}
	
	public void setTileIndex(int xt, int yt, int tileIndex) {
		if (xt < 0 || xt >= WORLD_WIDTH) 
			return;
		if (yt < 0 || yt >= WORLD_HEIGHT) 
			return;
		int index = xt + yt * WORLD_WIDTH;
		if (Tile.tiles[tileIndex] != null) {
			tiles[index] = tileIndex;
		} else tiles[index] = Tile.AIR_TILE.index;
		data[index] = 0x00;
	}
	
	public byte getData(int xt, int yt) {
		return data[xt + yt * WORLD_WIDTH];
	}

	public Tile getTile(int xt, int yt) {
		return Tile.tiles[getTileIndex(xt, yt)];
	}
	
	public int getTileIndex(int xt, int yt) {
		if (xt < 0 || xt >= WORLD_WIDTH) 
			return Tile.AIR_TILE.index;
		if (yt < 0 || yt >= WORLD_HEIGHT) 
			return Tile.AIR_TILE.index;
		return tiles[xt + yt * WORLD_WIDTH];
	}
	
	public void render(Screen2D screen, float dt) {
		for (Entity entity : entities) {
			if (entity instanceof PPEntity)
				((PPEntity)entity).render(screen, dt);
		}
		
		// TODO: improve rendering function
		for (int yt = 0; yt < WORLD_HEIGHT; yt++) {
			for (int xt = 0; xt < WORLD_WIDTH; xt++) {
				Tile tile = getTile(xt, yt);
				if (tile != Tile.AIR_TILE)
					tile.render(this, screen, xt, yt);
			}
		}
	}
	
	public List<AABB> getTileColliders(AABB body) {
		List<AABB> colliders = new ArrayList<AABB>();
		
		int xt0 = (int)(body.x0 / 8.0f);
		int yt0 = (int)(body.y0 / 8.0f);
		int xt1 = (int)(body.x1 / 8.0f);
		int yt1 = (int)(body.y1 / 8.0f);
		
		for (int yt = yt0; yt <= yt1; yt++) {
			for (int xt = xt0; xt <= xt1; xt++) {
				Tile tile = getTile(xt, yt);
				if (tile != Tile.AIR_TILE)
					colliders.add(tile.getBoundingBox(this, xt, yt));
			}
		}
		
		return colliders;
	}
	
	@Override
	public boolean isClient() {
		return platPorter.isClient();
	}
}
