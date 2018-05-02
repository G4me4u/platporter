package com.g4mesoft.platporter.world.tile;

import java.util.HashMap;
import java.util.Map;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.platporter.world.entity.PPEntity;
import com.g4mesoft.world.phys.AABB;

public class Tile {

	public static final Tile[] tiles = new Tile[256];
	private static int nextTileId = 0;

	public static final Tile AIR_TILE = new AirTile();
	public static final Tile ERROR_TILE = new SolidTile(0, 0, ColorPalette.getColors(222, 333, 444, -1));
	public static final Tile PLATFORM_TILE = new PlatformTile();
	public static final Tile BOOST_TILE = new BoostTile();
	public static final Tile LADDER_TILE = new LadderTile();
	public static final Tile DOOR_TILE = new DoorTile();
	public static final Tile LEVER_TILE = new LeverTile();
	public static final Tile BEAM_STAND_TILE = new BeamStandTile();
	public static final Tile BEAM_TILE = new BeamLaserTile();
	public static final Tile ORB_TILE = new OrbTile();
	public static final Tile STAGE_ENTER_TILE = new StageEnterTile();
	public static final Tile BARRIER_TILE = new BarrierTile();
	public static final Tile PREASSURE_PLATE_TILE = new PreassurePlateTile();
	
	private static final Map<Integer, Tile> ID_TO_TILE;
	
	public final int index;
	
	protected Tile() {
		index = nextTileId++;
		if (tiles[index] != null)
			throw new IllegalArgumentException("Duplicate tile-id!");
		
		tiles[index] = this;
	}

	public void interactWith(PPWorld world, int xt, int yt, PPEntity entity) {
	}
	
	public void steppedOn(PPWorld world, int xt, int yt, PPEntity entity) {
	}

	public void steppedOff(PPWorld world, int xt, int yt, PPEntity entity) {
	}
	
	public void entityInside(PPWorld world, int xt, int yt, PPEntity entity) {
	}
	
	public int getActivateId(PPWorld world, int xt, int yt) {
		return -1;
	}

	public void toggleActivate(PPWorld world, int xt, int yt) {
	}

	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
	}

	public boolean hasHitbox(PPWorld world, int xt, int yt) {
		return true;
	}
	
	public boolean isBackgroundLayer(PPWorld world, int xt, int yt) {
		return false;
	}
	
	public AABB getBoundingBox(PPWorld world, int xt, int yt) {
		return new AABB(xt, yt, xt + 1.0f, yt + 1.0f);
	}
	
	static {
		ID_TO_TILE = new HashMap<Integer, Tile>(nextTileId);
		
		ID_TO_TILE.put(0xFFFF, AIR_TILE);
		ID_TO_TILE.put(0xFF00, ERROR_TILE);
		ID_TO_TILE.put(0xCF54, PLATFORM_TILE);
		ID_TO_TILE.put(0x1234, BOOST_TILE);
		ID_TO_TILE.put(0xFF7F, LADDER_TILE);
		ID_TO_TILE.put(0xC61C, DOOR_TILE);
		ID_TO_TILE.put(0x90FF, LEVER_TILE);
		ID_TO_TILE.put(0x461F, BEAM_STAND_TILE);
		ID_TO_TILE.put(0x434E, BEAM_TILE);
		ID_TO_TILE.put(0x0026, ORB_TILE);
		ID_TO_TILE.put(0x7F7F, STAGE_ENTER_TILE);
		ID_TO_TILE.put(0x0000, BARRIER_TILE);
		ID_TO_TILE.put(0x00FF, PREASSURE_PLATE_TILE);
	}
	
	public static Tile parseTile(int id) {
		id &= 0xFFFFFF;
		Tile tile = ID_TO_TILE.get(id);
		return tile == null ? Tile.AIR_TILE : tile;
	}
}
