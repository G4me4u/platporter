package com.g4mesoft.platporter.world.entity.player;

import java.util.UUID;

import com.g4mesoft.platporter.world.PPWorld;

public class NetworkPlayerEntity extends PlayerEntity {

	public NetworkPlayerEntity(PPWorld world, UUID playerUUID) {
		super(world, playerUUID);
	}

	@Override
	protected void update() {
		super.update();
	}
}
