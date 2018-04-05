package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;

public class SolidTile extends Tile {

	private int sx;
	private int sy;
	private int colors;
	
	public SolidTile(int sx, int sy, int colors) {
		this.sx = sx;
		this.sy = sy;
		this.colors = colors;
	}
	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		screen.drawSprite(xt * 8, yt * 8, sx, sy, colors);
	}
}
