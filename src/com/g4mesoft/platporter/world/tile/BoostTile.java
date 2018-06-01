package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.sound.Sounds;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.platporter.world.entity.PPEntity;

public class BoostTile extends Tile {
	
	private static final int X_OFFSET_MASK = 0x01;
	private static final int BOOST_GAIN_MASK = 0x06;
	private static final int OFF_MASK = 0x08;
	private static final int ACTIVATE_ID_MASK = 0xF0;
	
	@Override
	public void steppedOn(PPWorld world, int xt, int yt, PPEntity entity) {
		if (!world.isClient())
			return;
		
		byte data = world.getData(xt, yt);
		if ((data & OFF_MASK) == 0) {
			float gain = ((world.getData(xt, yt) & BOOST_GAIN_MASK) >>> 1) * 0.5f + 1.0f;
			entity.velocity.y = -0.71f * gain;
			
			Sounds.playSound(Sounds.BOOST_TILE_SOUND, 1.0f);
		}
	}
	
	@Override
	public void toggleActivate(PPWorld world, int xt, int yt) {
		byte data = world.getData(xt, yt);
		world.setData(xt, yt, (byte)(data ^ OFF_MASK));
	}
	
	@Override
	public int getActivateId(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & ACTIVATE_ID_MASK) >>> 4;
	}
	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		byte data = world.getData(xt, yt);
		int sx = 4;
		if ((data & X_OFFSET_MASK) != 0)
			sx = 5;
		int sy = 6;
		if ((data & OFF_MASK) != 0)
			sy = 7;

		screen.drawSprite(xt * 8, yt * 8, sx, sy, ColorPalette.getColors(541, 213, 123, 005));
	}
}
