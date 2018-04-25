package com.g4mesoft.platporter.world.entity.player;

import java.util.UUID;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.platporter.world.entity.PPEntity;
import com.g4mesoft.world.entity.EntityFacing;
import com.g4mesoft.world.phys.AABB;

public abstract class PlayerEntity extends PPEntity {
	
	protected int animTimer;
	
	public PlayerEntity(PPWorld world, UUID playerUUID) {
		super(world, playerUUID);
	
		animTimer = 0;
	}

	@Override
	public void render(Screen2D screen, float dt) {
		int xp = (int)Math.round((prevPos.x + (pos.x - prevPos.x) * dt) * 8.0f);
		int yp = (int)Math.round((prevPos.y + (pos.y - prevPos.y) * dt) * 8.0f);
		
		int flags = 0;
		if (facing == EntityFacing.RIGHT)
			flags |= Screen2D.MIRROR_X;
		screen.drawSprite(xp, yp, (animTimer >> 2) % 3, 3, getBodyColor(), flags);
		
		if (pos.y - prevPos.y < -0.1f)
			flags |= Screen2D.MIRROR_Y;
		screen.drawSprite(xp, yp, 0, 2, getEyeColor(), flags);
	}
	

	@Override
	protected void update() {
		super.update();
		
		float speedX = Math.abs(pos.x - prevPos.x);
		if (speedX < 0.01f) {
			animTimer = 0;
		} else {
			animTimer += (int)(speedX * 32.0f);
		}
	}
	
	public abstract int getEyeColor();

	public abstract int getBodyColor();
	
	@Override
	protected AABB createBody() {
		return new AABB(0.12f, 0.255f, 0.87f, 1.0f);
	}
}
