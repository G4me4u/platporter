package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.platporter.world.entity.PPEntity;
import com.g4mesoft.world.phys.AABB;

public class PreassurePlateTile extends Tile {

	private static final int ON_MASK = 0x01;
	
	int sx;
	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		sx = 7;
		if (isTurnedOn(world, xt, yt))
			sx = 6;
		
		screen.drawSprite(xt * 8, yt * 8, sx, 4, ColorPalette.getColors(222, 111, 410, -1));
	}
	
	public boolean isTurnedOn(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & ON_MASK) != 0;
	}
	
	@Override
	public void steppedOn(PPWorld world, int xt, int yt, PPEntity entity) {
		byte data = world.getData(xt, yt);
		world.setData(xt, yt, (byte)(data | ON_MASK));
	}

	@Override
	public void steppedOff(PPWorld world, int xt, int yt, PPEntity entity) {
		byte data = world.getData(xt, yt);
		world.setData(xt, yt, (byte)(data | ON_MASK));
	}
	
	@Override
	public AABB getBoundingBox(PPWorld world, int xt, int yt) {
		return new AABB(xt + 0.125f, yt + 0.875f, xt + 0.875f, yt + 1.00f);
	}
}
