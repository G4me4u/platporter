package com.g4mesoft.platporter.world.tile;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.world.entity.EntityFacing;

public abstract class BeamTile extends Tile {
	
	public void renderLaser(PPWorld world, Screen2D screen, int xt, int yt) {
		boolean onWall = isOnWall(world, xt, yt);
		int sy = onWall ? 1 : 0;
		
		int flags = 0;
		if (isLaserMirrored(world, xt, yt)) {
			if (onWall) {
				flags |= Screen2D.MIRROR_X;
			} else {
				flags |= Screen2D.MIRROR_Y;
			}
		}
		
		int sx = (int)(world.worldTime / 3L + getAnimOffset(world, xt, yt)) % 4 + 4;
		screen.drawSprite(xt * 8, yt * 8, sx, sy, ColorPalette.getColors(-1, 213, -1, -1), flags);
	}
	
	public abstract boolean isOnWall(PPWorld world, int xt, int yt);
	
	public abstract boolean isLaserMirrored(PPWorld world, int xt, int yt);
	
	protected abstract int getAnimOffset(PPWorld world, int xt, int yt);
	
	public EntityFacing getFacing(PPWorld world, int xt, int yt) {
		int index = isOnWall(world, xt, yt) ? 0x01 : 0x00;
		if (isLaserMirrored(world, xt, yt))
			index |= 0x02;
		return EntityFacing.fromIndex(index);
	}
}
