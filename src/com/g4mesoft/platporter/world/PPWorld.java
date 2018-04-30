package com.g4mesoft.platporter.world;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.PlatPorter;
import com.g4mesoft.platporter.world.entity.PPEntity;
import com.g4mesoft.platporter.world.tile.Tile;
import com.g4mesoft.world.World;
import com.g4mesoft.world.entity.Entity;
import com.g4mesoft.world.phys.AABB;

public class PPWorld extends World {

	public static final int WORLD_WIDTH = 16;
	public static final int WORLD_HEIGHT = 16;
	public static final int NUM_LEVELS = 16;
	
	public final PlatPorter platPorter;
	
	protected final int[] tiles;
	protected final byte[] data;

	protected final int[] levelsTiles;
	protected final byte[] levelsData;
		
	public PPWorld(PlatPorter platPorter) {
		this.platPorter = platPorter;
	
		tiles = new int[WORLD_WIDTH * WORLD_HEIGHT];
		data = new byte[WORLD_WIDTH * WORLD_HEIGHT];
	
		levelsTiles = new int[WORLD_WIDTH * WORLD_HEIGHT * NUM_LEVELS];
		levelsData = new byte[WORLD_WIDTH * WORLD_HEIGHT * NUM_LEVELS];
	}
	
	public boolean setData(int xt, int yt, byte data) {
		if (xt < 0 || xt >= WORLD_WIDTH) 
			return false;
		if (yt < 0 || yt >= WORLD_HEIGHT) 
			return false;
		
		int index = xt + yt * WORLD_WIDTH;
		if (this.data[index] == data)
			return false;
		
		this.data[index] = data;
		
		return true;
	}
	
	public void setTile(int xt, int yt, Tile tile) {
		setTileIndex(xt, yt, tile == null ? Tile.AIR_TILE.index : tile.index);
	}
	
	public boolean setTileIndex(int xt, int yt, int tileIndex) {
		if (xt < 0 || xt >= WORLD_WIDTH) 
			return false;
		if (yt < 0 || yt >= WORLD_HEIGHT) 
			return false;
		
		int index = xt + yt * WORLD_WIDTH;
		if (tiles[index] == tileIndex)
			return false;
		
		if (Tile.tiles[tileIndex] != null) {
			tiles[index] = tileIndex;
		} else tiles[index] = Tile.AIR_TILE.index;
		data[index] = 0x00;
		
		return true;
	}
	
	public byte getData(int xt, int yt) {
		return data[xt + yt * WORLD_WIDTH];
	}

	public Tile getTile(int xt, int yt) {
		return Tile.tiles[getTileIndex(xt, yt)];
	}
	
	public int getTileIndex(int xt, int yt) {
		if (xt < 0 || xt >= WORLD_WIDTH) 
			return Tile.PLATFORM_TILE.index;
		if (yt < 0 || yt >= WORLD_HEIGHT) 
			return Tile.PLATFORM_TILE.index;
		return tiles[xt + yt * WORLD_WIDTH];
	}
	
	@Override
	public void update() {
		super.update();
	}
	
	public void render(Screen2D screen, float dt) {
	}
	
	public List<AABB> getTileColliders(AABB body) {
		List<AABB> colliders = new ArrayList<AABB>();
		
		int xt0 = (int)body.x0 - 1;
		int yt0 = (int)body.y0 - 1;
		int xt1 = (int)body.x1 + 1;
		int yt1 = (int)body.y1 + 1;
		
		for (int yt = yt0; yt <= yt1; yt++) {
			for (int xt = xt0; xt <= xt1; xt++) {
				Tile tile = getTile(xt, yt);
				if (tile.hasHitbox(this, xt, yt))
					colliders.add(tile.getBoundingBox(this, xt, yt));
			}
		}
		
		return colliders;
	}
	
	public boolean removeEntity(UUID entityUUID) {
		if (entityUUID == null)
			return false;
		
		for (Entity entity : entities) {
			if (entity instanceof PPEntity) {
				if (entityUUID.equals(((PPEntity)entity).getUUID())) {
					return removeEntity(entity) != null;
				}
			}
		}
		
		return false;
	}
	
	public PPEntity getEntity(UUID entityUUID) {
		if (entityUUID == null)
			return null;
		
		for (Entity entity : entities) {
			if (entity instanceof PPEntity) {
				PPEntity ent = (PPEntity)entity;
				if (entityUUID.equals(ent.getUUID()))
					return ent;
			}
		}
		
		return null;
	}
	
	public void interactWithTile(int xt, int yt, PPEntity entity) {
	}
	
	@Override
	public boolean isClient() {
		return platPorter.isClient();
	}

	public void setAllTiles(int[] tiles, byte[] data) {
		if (tiles.length != data.length || tiles.length != this.tiles.length)
			return;
		
		int i = 0;
		for (int yt = 0; yt < WORLD_WIDTH; yt++) {
			for (int xt = 0; xt < WORLD_WIDTH; xt++) {
				setTileIndex(xt, yt, tiles[i]);
				setData(xt, yt, data[i]);
				i++;
			}
		}
	}
}
