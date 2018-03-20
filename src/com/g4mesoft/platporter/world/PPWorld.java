package com.g4mesoft.platporter.world;

import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.PlatPorter;
import com.g4mesoft.world.World;

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
	}
	
	public void render(Screen2D screen) {
	}
	
	@Override
	public boolean isClient() {
		return platPorter.isClient();
	}
}
