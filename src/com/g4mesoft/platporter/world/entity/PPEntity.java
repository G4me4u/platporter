package com.g4mesoft.platporter.world.entity;

import java.util.ArrayList;
import java.util.List;

import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.math.Vec2f;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.world.entity.EntityFacing;
import com.g4mesoft.world.entity.LivingEntity;
import com.g4mesoft.world.phys.AABB;

public abstract class PPEntity extends LivingEntity {

	protected boolean onGround;
	protected Vec2f velocity;
	protected EntityFacing facing;
	
	protected PPEntity(PPWorld world) {
		super(world);
		velocity = new Vec2f(0, 0);
		facing = EntityFacing.RIGHT;
	}
	
	public abstract void render(Screen2D screen, float dt);
	
	public void move(float xm, float ym) {
		float oxm = xm;
		float oym = ym;
		
		List<AABB> tileHitboxes = new ArrayList<>();
		for (AABB aabb : tileHitboxes) 
			xm = aabb.clipX(body, xm);
		body.move(xm, 0);

		for (AABB aabb : tileHitboxes) 
			ym = aabb.clipY(body, ym);
		body.move(0, ym);
		
		onGround = oym > 0.0f && ym != oym;
		
		pos.add(xm, ym);
	}

	@Override
	protected AABB createBody() {
		return new AABB(0, 0, 8, 8);
	}
}
