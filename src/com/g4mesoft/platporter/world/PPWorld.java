package com.g4mesoft.platporter.world;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.platporter.PlatPorter;
import com.g4mesoft.platporter.input.KeyManager;
import com.g4mesoft.platporter.world.entity.PPEntity;
import com.g4mesoft.platporter.world.tile.Tile;
import com.g4mesoft.world.World;
import com.g4mesoft.world.entity.Entity;
import com.g4mesoft.world.phys.AABB;

public class PPWorld extends World {

	private static final int WORLD_WIDTH = 16;
	private static final int WORLD_HEIGHT = 16;
	private static final int NUM_LEVELS = 16;
	
	private boolean viewHitboxes;
	public final PlatPorter platPorter;
	
	private final int[] tiles;
	private final byte[] data;

	private final int[] levelsTiles;
	private final byte[] levelsData;
	
	public PPWorld(PlatPorter platPorter) {
		this.platPorter = platPorter;
	
		tiles = new int[WORLD_WIDTH * WORLD_HEIGHT];
		data = new byte[WORLD_WIDTH * WORLD_HEIGHT];
	
		levelsTiles = new int[WORLD_WIDTH * WORLD_HEIGHT * NUM_LEVELS];
		levelsData = new byte[WORLD_WIDTH * WORLD_HEIGHT * NUM_LEVELS];

		BufferedImage levelImage = null;
		try {
			levelImage = ImageIO.read(PPWorld.class.getResource("/assets/levels.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		parseLevels(levelImage);
		
		loadLevel(1);
	}
	
	private void parseLevels(BufferedImage levelImage) {
		for (int x = 0; x < WORLD_WIDTH; x++) {
			for (int y = 0; y < WORLD_HEIGHT * NUM_LEVELS; y++) {
				int i = x + y * WORLD_WIDTH;
				int rgb = levelImage.getRGB(x, y);
				levelsTiles[i] = Tile.parseTile((rgb >>> 8) & 0xFFFF).index;
				levelsData[i] = (byte)rgb;
			}
		}
	}
	
	private void loadLevel(int index) {
		int ti = WORLD_WIDTH * WORLD_HEIGHT;
		int li = ti * (index + 1);
		while (ti != 0) {
			ti--;
			li--;
			
			tiles[ti] = levelsTiles[li];
			data[ti] = levelsData[li];
		}
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
			return Tile.PLATFORM_TILE.index;
		if (yt < 0 || yt >= WORLD_HEIGHT) 
			return Tile.PLATFORM_TILE.index;
		return tiles[xt + yt * WORLD_WIDTH];
	}
	
	@Override
	public void update() {
		super.update();
		
		if (KeyManager.KEY_TOGGLE_HITBOX.isClicked())
			viewHitboxes = !viewHitboxes;
	}
	
	public void render(Screen2D screen, float dt) {
		renderTiles(screen, dt, true);

		for (Entity entity : entities) {
			if (entity instanceof PPEntity)
				((PPEntity)entity).render(screen, dt);
		}
		
		renderTiles(screen, dt, false);
		
		if (viewHitboxes)
			renderHitboxes(screen, dt);
	}
	
	private void renderHitboxes(Screen2D screen, float dt) {
		for (int yt = 0; yt < WORLD_HEIGHT; yt++) {
			for (int xt = 0; xt < WORLD_WIDTH; xt++) {
				Tile tile = getTile(xt, yt);
				if (tile.hasHitbox(this, xt, yt))
					drawHitbox(screen, tile.getBoundingBox(this, xt, yt), ColorPalette.getColor(500));
			}
		}
		
		for (Entity ent : entities) {
			if (ent instanceof PPEntity)
				drawHitbox(screen, ((PPEntity)ent).getBody(), ColorPalette.getColor(5));
		}
	}
	
	private void drawHitbox(Screen2D screen, AABB hitbox, int color) {
		int x0 = Math.round(hitbox.x0 * 8.0f);
		int y0 = Math.round(hitbox.y0 * 8.0f);
		int x1 = Math.round(hitbox.x1 * 8.0f);
		int y1 = Math.round(hitbox.y1 * 8.0f);
		screen.drawRect(x0, y0, x1 - x0, y1 - y0, color);
	}
	
	private void renderTiles(Screen2D screen, float dt, boolean background) {
		for (int yt = 0; yt < WORLD_HEIGHT; yt++) {
			for (int xt = 0; xt < WORLD_WIDTH; xt++) {
				Tile tile = getTile(xt, yt);
				if (tile != Tile.AIR_TILE && tile.isBackgroundLayer(this, xt, yt) == background)
					tile.render(this, screen, xt, yt);
			}
		}
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
		Tile tile = getTile(xt, yt);
		tile.interactWith(this, xt, yt, entity);
	
		NetworkManager manager = platPorter.getNetworkManager();
		if (manager.isClient()) {
			// TODO: create action protocol
		}
	}
	
	@Override
	public boolean isClient() {
		return platPorter.isClient();
	}
}
