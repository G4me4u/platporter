package com.g4mesoft.platporter.world.entity.player;

import java.util.UUID;

import com.g4mesoft.platporter.input.KeyManager;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.world.entity.EntityFacing;

public class ClientPlayerEntity extends PlayerEntity {

	public ClientPlayerEntity(PPWorld world, UUID playerUUID) {
		super(world, playerUUID);
	}

	@Override
	protected void update() {
		super.update();
		
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
}
