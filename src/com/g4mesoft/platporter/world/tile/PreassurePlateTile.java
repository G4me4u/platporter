package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.sound.Sounds;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.platporter.world.entity.PPEntity;
import com.g4mesoft.world.phys.AABB;

public class PreassurePlateTile extends Tile {

	private static final int ON_MASK = 0x01;
	
	private static final int MULTI_ACTIVATE_MASK = 0x08;
	private static final int ACTIVATE_ID_MASK = 0xF0;
	
	@Override
	public void steppedOn(PPWorld world, int xt, int yt, PPEntity entity) {
		if (world.isClient()) {
			Sounds.playSound(Sounds.PRESSURE_PLATE_SOUND, 1.0f, 0.9f + (float)Math.random() * 0.2f);
			return;
		}
		
		byte data = world.getData(xt, yt);
		if ((data & ON_MASK) != 0)
			return;
		
		world.setData(xt, yt, (byte)(data | ON_MASK));
		activateTiles(world, xt, yt, data);
	}

	@Override
	public void steppedOff(PPWorld world, int xt, int yt, PPEntity entity) {
		if (world.isClient())
			return;
		
		byte data = world.getData(xt, yt);
		if ((data & ON_MASK) == 0)
			return;
		
		world.setData(xt, yt, (byte)(data & (~ON_MASK)));
		activateTiles(world, xt, yt, data);
	}
	
	private void activateTiles(PPWorld world, int xt, int yt, byte data) {
		int activateId = (data & ACTIVATE_ID_MASK) >>> 4;
		if ((data & MULTI_ACTIVATE_MASK) != 0) {
			int activateId0 = (activateId >>> 0) & 0x03;
			int activateId1 = (activateId >>> 2) & 0x03;
			world.activateTile(xt, yt, activateId0);
			world.activateTile(xt, yt, activateId1);
		} else {
			world.activateTile(xt, yt, activateId);
		}
	}
	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		int sx = isTurnedOn(world, xt, yt) ? 6 : 7;
		screen.drawSprite(xt * 8, yt * 8, sx, 4, ColorPalette.getColors(222, 111, 410, -1));
	}
	
	public boolean isTurnedOn(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & ON_MASK) != 0;
	}
	
	@Override
	public AABB getBoundingBox(PPWorld world, int xt, int yt) {
		return new AABB(xt + 0.125f, yt + 0.875f, xt + 0.875f, yt + 1.00f);
	}
}
