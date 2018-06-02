package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.world.phys.AABB;

public class DoorTile extends Tile {

	private static final int OPEN_MASK = 0x01;
	private static final int FLIP_X_MASK = 0x02;
	private static final int MULTIPLE_ACTIVATIONS_MASK = 0x04;
	
	private static final int ACTIVATE_ID_MASK = 0xF0;
	
	@Override
	public void toggleActivate(PPWorld world, int xt, int yt) {
		byte data = world.getData(xt, yt);
		if ((data & MULTIPLE_ACTIVATIONS_MASK) != 0)
			world.setData(xt, yt, (byte)(data ^ MULTIPLE_ACTIVATIONS_MASK));
		else
			world.setData(xt, yt, (byte)(data ^ OPEN_MASK));
	}
	
	@Override
	public int getActivateId(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & ACTIVATE_ID_MASK) >>> 4;
	}
	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		byte data = world.getData(xt, yt);
		int sx = 3 + (data & OPEN_MASK);
		int flags = (data & FLIP_X_MASK) != 0 ? Screen2D.MIRROR_X : 0;
		screen.drawSprite(xt * 8, yt * 8, sx, 4, ColorPalette.getColors(410, -1, 521, -1), flags);
	}
	
	@Override
	public boolean hasHitbox(PPWorld world, int xt, int yt) {
		return !isOpen(world, xt, yt);
	}
	
	@Override
	public boolean isBackgroundLayer(PPWorld world, int xt, int yt) {
		return isOpen(world, xt, yt);
	}
	
	@Override
	public AABB getBoundingBox(PPWorld world, int xt, int yt) {
		int data = world.getData(xt, yt);
		if ((data & FLIP_X_MASK) != 0)
			return new AABB(xt + 0.615f, yt, xt + 0.875f, yt + 1.0f);
		return new AABB(xt + 0.125f, yt, xt + 0.365f, yt + 1.0f);
	}
	
	public boolean isOpen(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & OPEN_MASK) != 0;
	}
}
