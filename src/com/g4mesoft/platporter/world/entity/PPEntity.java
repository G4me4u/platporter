package com.g4mesoft.platporter.world.entity;

import java.util.List;
import java.util.UUID;

import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.math.Vec2f;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.platporter.world.tile.Tile;
import com.g4mesoft.world.entity.EntityFacing;
import com.g4mesoft.world.entity.LivingEntity;
import com.g4mesoft.world.phys.AABB;

public abstract class PPEntity extends LivingEntity {

	protected boolean onGround;
	
	protected boolean wasOnLadder;
	protected boolean onLadder;
	
	protected Vec2f velocity;
	protected EntityFacing facing;

	protected Vec2f newPos;
	protected boolean posUpdated;
	
	protected final UUID entityUUID;
	
	protected PPEntity(PPWorld world, UUID entityUUID) {
		super(world);
		
		this.entityUUID = entityUUID;
		
		velocity = new Vec2f();
		facing = EntityFacing.RIGHT;
		
		newPos = new Vec2f();
	}
	
	@Override
	protected void update() {
		if (posUpdated)
			pos.set(newPos);
			
		PPWorld world = (PPWorld)this.world;

		Tile footTile = world.getTile((int)(body.x0 + body.x1) >>> 1, (int)(body.y1 + 0.0625f));
		wasOnLadder = onLadder;
		onLadder = footTile == Tile.LADDER_TILE;
	}
	
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

	public abstract void render(Screen2D screen, float dt);

	@Override
	protected AABB createBody() {
		return new AABB(0.0f, 0.0f, 1.0f, 1.0f);
	}
	
	public UUID getUUID() {
		return entityUUID;
	}

	public void setPosition(float x, float y, EntityFacing facing) {
		posUpdated = true;
		newPos.set(x, y);
		this.facing = facing;
	}
}
