package com.g4mesoft.platporter.world.entity.player;

import java.util.UUID;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.platporter.world.PPWorld;

public class NetworkPlayerEntity extends PlayerEntity {

	public NetworkPlayerEntity(PPWorld world, UUID playerUUID) {
		super(world, playerUUID);
	}

	@Override
	public int getBodyColor() {
		return ColorPalette.getColors(511, 410, 500,  -1);
	}

	@Override
	public int getEyeColor() {
		return ColorPalette.getColors( -1, 440, 555,  -1);
	}
	
	@Override
	protected void update() {
		super.update();
	}
}
