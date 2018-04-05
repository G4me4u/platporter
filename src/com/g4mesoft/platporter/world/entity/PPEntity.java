package com.g4mesoft.platporter.world.entity;

import java.util.List;
import java.util.UUID;

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

	protected final UUID entityUUID;
	
	protected PPEntity(PPWorld world, UUID entityUUID) {
		super(world);
		
		this.entityUUID = entityUUID;
		
		velocity = new Vec2f(0, 0);
		facing = EntityFacing.RIGHT;
	}
	
	public abstract void render(Screen2D screen, float dt);
	
	public void move(float xm, float ym) {
		float oxm = xm;
		float oym = ym;
		
		List<AABB> tileHitboxes = ((PPWorld)world).getTileColliders(body.expand(xm, ym));
		for (AABB aabb : tileHitboxes) 
			xm = aabb.clipX(body, xm);
		body.move(xm, 0);
		if (xm != oxm)
			velocity.x = 0;

		for (AABB aabb : tileHitboxes)
			ym = aabb.clipY(body, ym);
		body.move(0, ym);
		if (ym != oym)
			velocity.y = 0;
		
		onGround = oym > 0.0f && ym != oym;
		
		pos.add(xm, ym);
	}

	@Override
	protected AABB createBody() {
		return new AABB(0.0f, 0.0f, 1.0f, 1.0f);
	}
	
	public UUID getUUID() {
		return entityUUID;
	}

	public void setPosition(float x, float y, EntityFacing facing) {
		pos.set(x, y);
		this.facing = facing;
	}
}
