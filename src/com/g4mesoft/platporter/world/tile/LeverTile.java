package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.platporter.world.entity.PPEntity;
import com.g4mesoft.world.phys.AABB;

public class LeverTile extends Tile {

	private static final int LEVER_ON_MASK = 0x01;
	private static final int ON_WALL_MASK = 0x02;
	private static final int MIRROR_MASK = 0x04;
	
	@Override
	public void interactWith(PPWorld world, int xt, int yt, PPEntity entity) {
		int data = world.getData(xt, yt) & 0xFF;
		world.setData(xt, yt, (byte)(data ^ LEVER_ON_MASK));
	}
	
	@Override
	public void render(PPWorld world, Screen2D screen, int xt, int yt) {
		boolean onWall = isOnWall(world, xt, yt);

		int sx = onWall ? 0 : 1;
		
		int flags = 0;
		if (onWall) {
			if (isTurnedOn(world, xt, yt))
				flags |= Screen2D.MIRROR_Y;
			if (isMirrored(world, xt, yt))
				flags |= Screen2D.MIRROR_X;
		} else {
			if (isTurnedOn(world, xt, yt))
				flags |= Screen2D.MIRROR_X;
			if (isMirrored(world, xt, yt))
				flags |= Screen2D.MIRROR_Y;
		}
		
		screen.drawSprite(xt * 8, yt * 8, sx, 4, ColorPalette.getColors(222, 111, 410, -1), flags);
	}
	
	public boolean isTurnedOn(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & LEVER_ON_MASK) != 0;
	}
	
	public boolean isOnWall(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & ON_WALL_MASK) != 0;
	}
	
	public boolean isMirrored(PPWorld world, int xt, int yt) {
		return (world.getData(xt, yt) & MIRROR_MASK) != 0;
	}
	
	@Override
	public AABB getBoundingBox(PPWorld world, int xt, int yt) {
		if (isOnWall(world, xt, yt)) {
			if (isMirrored(world, xt, yt)) {
				return new AABB(xt + 0.75f, yt + 0.25f, xt + 1.0f, yt + 0.75f);
			} else {
				return new AABB(xt, yt + 0.25f, xt + 0.25f, yt + 0.75f);
			}
		} else {
			if (isMirrored(world, xt, yt)) {
				return new AABB(xt + 0.25f, yt, xt + 0.75f, yt + 0.25f);
			} else {
				return new AABB(xt + 0.25f, yt + 0.75f, xt + 0.75f, yt + 1.0f);
			}
		}
	}
}
