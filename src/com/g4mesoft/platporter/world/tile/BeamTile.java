package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;

public class BeamTile extends Tile {
	private static final int ON_WALL_MASK = 0x01;
	
	@Override
	public boolean hasHitbox(PPWorld world, int xt, int yt) {
		return false;
	}
	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		byte data = world.getData(xt, yt);
		
		boolean onWall = (data & ON_WALL_MASK) != 0;
		
		int sy = onWall ? 1 : 0;
		
		
		int sx = (int)(world.worldTime / 5L) % 4 + 4;
		screen.drawSprite(xt * 8, yt * 8, sx, sy, ColorPalette.getColors(541, 213, 123, -1));
	}
}
