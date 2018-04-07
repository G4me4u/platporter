package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;

public class OrbTile extends Tile {
	
	private static final int TIME_MASK = 0x02 | 0x01;
	
	@Override
	public boolean hasHitbox(PPWorld world, int xt, int yt) {
		return false;
	}
	
	@Override
	public boolean isBackgroundLayer(PPWorld world, int xt, int yt) {
		return true;
	}
	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		int sy = 8;
		
		byte data = world.getData(xt, yt);
		
		int animTimer = ((int)(world.worldTime / 10L) + (data & TIME_MASK)) % 4;
		
		int sx = 0;
		
		if (animTimer % 2 != 0) {
			if (animTimer % 3 != 0) {
				sx = 1;
			} else {
				sx = 2;
			}
		} else {
			sx = 0;
		}
		
		
		
		screen.drawSprite(xt * 8, yt * 8, sx, sy, ColorPalette.getColors(050, 020, -1, -1));
	}
}
