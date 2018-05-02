package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;

public class OrbTile extends Tile {
	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		int animTimer = (int)(world.worldTime / 10L) % 4;
		int sx = (animTimer & 0x1) << ((animTimer & 0x2) >>> 1);
		
		screen.drawSprite(xt * 8, yt * 8, sx, 8, ColorPalette.getColors(050, 020, -1, -1));
	}
	
	@Override
	public boolean hasHitbox(PPWorld world, int xt, int yt) {
		return false;
	}
	
	@Override
	public boolean isBackgroundLayer(PPWorld world, int xt, int yt) {
		return true;
	}
}
