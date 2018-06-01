package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.sound.Sounds;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.platporter.world.ServerPPWorld;
import com.g4mesoft.platporter.world.entity.PPEntity;

public class OrbTile extends Tile {
	
	private static final int ACTION_ID_MASK = 0xF0;
	private static final int WIN_MASK = 0x01;
	
	@Override
	public void entityInside(PPWorld world, int xt, int yt, PPEntity entity) {
		byte data = world.getData(xt, yt);
		world.setTile(xt, yt, STAGE_ENTER_TILE);

		if (world.isClient()) {
			if ((data & WIN_MASK) != 0) {
				Sounds.playSound(Sounds.GAME_WON_SOUND, 1.0f);
			} else {
				Sounds.playSound(Sounds.LEVEL_WON_SOUND, 1.0f);
			}
			return;
		}
		
		if ((data & WIN_MASK) != 0) {
			world.setData(xt, yt, (byte)PPWorld.WIN_LEVEL);
			
			for (PPEntity ent : world.getPPEntities())
				((ServerPPWorld)world).loadLevel(ent, PPWorld.WIN_LEVEL);
		} else {
			int actionId = data & ACTION_ID_MASK;
			world.activateTile(0, 0, actionId);
			((ServerPPWorld)world).loadLevel(entity, 0);
		}
	}
	
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
