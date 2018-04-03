package com.g4mesoft.platporter.world.entity;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.input.KeyManager;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.world.entity.EntityFacing;
import com.g4mesoft.world.phys.AABB;

public class PlayerEntity extends PPEntity {
	
	private int animTimer;
	
	public PlayerEntity(PPWorld world) {
		super(world);
	
		animTimer = 0;
	}

	@Override
	public void render(Screen2D screen, float dt) {
		int xp = (int)Math.round((prevPos.x + (pos.x - prevPos.x) * dt) * 8.0f);
		int yp = (int)Math.round((prevPos.y + (pos.y - prevPos.y) * dt) * 8.0f);
		
		int flags = 0;
		if (facing == EntityFacing.RIGHT)
			flags |= Screen2D.MIRROR_X;
		screen.drawSprite(xp, yp, (animTimer >> 2) % 3, 3, ColorPalette.getColors(125, 410, 555,  -1), flags);
		
		if (velocity.y < 0.0f)
			flags |= Screen2D.MIRROR_Y;
		screen.drawSprite(xp, yp, 0, 2, ColorPalette.getColors( -1, 050, 555,  -1), flags);
	}

	@Override
	protected void update() {
		if (KeyManager.KEY_LEFT.isPressed()) {
			velocity.x -= 0.02f;
			facing = EntityFacing.LEFT;
		} else if (KeyManager.KEY_RIGHT.isPressed()) {
			velocity.x += 0.02f;
			facing = EntityFacing.RIGHT;
		}
		
		if (KeyManager.KEY_UP.isPressed() && onGround) {
			velocity.y = -0.5f;
			velocity.x *= 1.5f;
		}

		velocity.y += 0.08f;
		
		velocity.x *= 0.85f;
		velocity.y *= 0.95f;

		move(velocity.x, velocity.y);

		float speedX = Math.abs(velocity.x);
		if (speedX < 0.01f) {
			animTimer = 0;
		} else {
			animTimer += (int)(speedX * 32.0f);
		}
	}
	
	@Override
	protected AABB createBody() {
		return new AABB(0.125f, 0.25f, 0.875f, 1.0f);
	}
}
