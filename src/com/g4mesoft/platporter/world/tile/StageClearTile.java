package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;

public class StageClearTile extends Tile {
	private static final int TIME_MASK = 0x03;
	
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
		int sy = 9;
		byte data = world.getData(xt, yt);
		int animTimer = (int)(world.worldTime) % 80 - 20;
		
		int sx = 0;
		
		if (animTimer < 5 || animTimer > 50) {
			sx = 0;
		} else if (animTimer < 10 || animTimer > 44) {
			sx = 1;
		} else if (animTimer < 15 || animTimer > 39) {
			sx = 2;
		} else if (animTimer < 40){
			sx = 3;
		}
		
		screen.drawSprite(xt * 8, yt * 8, sx, sy, ColorPalette.getColors(111, 333, 555, -1));
	}
}
