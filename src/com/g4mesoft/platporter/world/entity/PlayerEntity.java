package com.g4mesoft.platporter.world.entity;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.input.KeyManager;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.world.entity.EntityFacing;


public class PlayerEntity extends PPEntity {
	
	public PlayerEntity(PPWorld world) {
		super(world);
	}

	@Override
	public void render(Screen2D screen, float dt) {
		float xp = prevPos.x + (pos.x - prevPos.x) * dt;
		float yp = prevPos.y + (pos.y - prevPos.y) * dt;
		screen.drawRect((int) xp,(int) yp, 8, 8, ColorPalette.getColor(135));
		
	}

	@Override
	protected void update() {
		if (KeyManager.KEY_LEFT.isPressed()) {
			velocity.x -= 1.50f;
			facing = EntityFacing.LEFT;
		} else if (KeyManager.KEY_RIGHT.isPressed()) {
			velocity.x += 1.50f;
			facing = EntityFacing.RIGHT;
		}
		
		if (KeyManager.KEY_UP.isPressed() && onGround) {
			velocity.y = -15.00f;
		}
		
//		velocity.y += 2.00f;
		
		velocity.x *= 0.85f;
		velocity.y *= 0.95f;
		
		move(velocity.x, velocity.y);
	}

}
