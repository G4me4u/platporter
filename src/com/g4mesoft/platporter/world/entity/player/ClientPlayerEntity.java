package com.g4mesoft.platporter.world.entity.player;

import java.util.UUID;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.packet.client.C01PositionPacket;
import com.g4mesoft.platporter.input.KeyManager;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.platporter.world.tile.Tile;
import com.g4mesoft.world.entity.EntityFacing;

public class ClientPlayerEntity extends PlayerEntity {

	public ClientPlayerEntity(PPWorld world, UUID playerUUID) {
		super(world, playerUUID);
	}

	@Override
	protected void update() {
		super.update();
		
		if (!world.isClient())
			return;
		
		if (KeyManager.KEY_LEFT.isPressed()) {
			velocity.x -= 0.02f;
			facing = EntityFacing.LEFT;
		} else if (KeyManager.KEY_RIGHT.isPressed()) {
			velocity.x += 0.02f;
			facing = EntityFacing.RIGHT;
		}
		
		if (onLadder) {
			if (KeyManager.KEY_UP.isPressed()) {
				velocity.y -= 0.025f;
			} else if (KeyManager.KEY_DOWN.isPressed()) {
				velocity.y += 0.025f;
			} else {
				velocity.y = 0.0f;
			}
		} else {
			if (KeyManager.KEY_UP.isPressed() && onGround) {
				velocity.y = -0.5f;
				velocity.x *= 1.5f;
			}
	
			velocity.y += 0.08f;
		}
		
		velocity.x *= getHorizontalFriction();
		velocity.y *= getVerticalFriction();

		move(velocity.x, velocity.y);

		float speedX = Math.abs(velocity.x);
		if (speedX < 0.01f) {
			animTimer = 0;
		} else {
			animTimer += (int)(speedX * 32.0f);
		}
		
		NetworkManager client = ((PPWorld)world).platPorter.getNetworkManager();
		client.addPacketToSend(new C01PositionPacket(pos.x, pos.y, facing));
	
		if (KeyManager.KEY_INTERACT.isClicked()) {
			int xi;
			if (facing == EntityFacing.LEFT) {
				xi = (int)(body.x0 - 0.25f);
			} else {
				xi = (int)(body.x1 + 0.25f);
			}
			int yi = (int)(body.y0 + body.y1) >>> 1;
			
			Tile interactTile = ((PPWorld)world).getTile(xi, yi);
			interactTile.interactWith((PPWorld)world, xi, yi, this);
		}
	}
	
	public float getHorizontalFriction() {
		return onLadder ? 0.75f : 0.85f;
	}

	public float getVerticalFriction() {
		return onLadder ? 0.85f : 0.95f;
	}
}
