package com.g4mesoft.platporter.world.entity;

import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.world.entity.LivingEntity;
import com.g4mesoft.world.phys.AABB;

public class PPEntity extends LivingEntity {

	protected PPEntity(PPWorld world) {
		super(world);
	}

	@Override
	protected void update() {
		
	}
	
	public void render() {
		
	}

	@Override
	protected AABB createBody() {
		return new AABB(0, 0, 8, 8);
	}
}
