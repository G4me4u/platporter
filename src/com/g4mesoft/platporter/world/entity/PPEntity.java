package com.g4mesoft.platporter.world.entity;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.math.Vec2f;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.platporter.world.tile.BeamTile;
import com.g4mesoft.platporter.world.tile.Tile;
import com.g4mesoft.world.entity.EntityFacing;
import com.g4mesoft.world.entity.LivingEntity;
import com.g4mesoft.world.phys.AABB;

public abstract class PPEntity extends LivingEntity {

	protected static final Random RANDOM = new Random();
	
	protected boolean onGround;
	
	protected boolean wasOnLadder;
	protected boolean onLadder;
	
	protected int oldFootX;
	protected int oldFootY;
	
	protected boolean wasInLaser;
	protected boolean inLaser;
	
	public Vec2f velocity;
	public EntityFacing facing;

	protected Vec2f newPos;
	protected boolean posUpdated;
	
	protected final UUID entityUUID;
	
	protected PPEntity(PPWorld world, UUID entityUUID) {
		super(world);
		
		oldFootX = -1;
		oldFootY = -1;
		
		this.entityUUID = entityUUID;
		
		velocity = new Vec2f();
		facing = EntityFacing.RIGHT;
		
		newPos = new Vec2f();
	}
	
	@Override
	protected void update() {
		if (posUpdated) {
			body.move(newPos.x - pos.x, newPos.y - pos.y);
			pos.set(newPos);
			posUpdated = false;
		}

		velocity.x *= getHorizontalFriction();
		velocity.y *= getVerticalFriction();
		move(velocity.x, velocity.y);
		
		PPWorld world = (PPWorld)this.world;

		int xf = (int)(body.x0 + body.x1) >>> 1;
		int yf = (int)(body.y1 + 0.0625f);
		Tile footTile = world.getTile(xf, yf);
		wasOnLadder = onLadder;
		onLadder = footTile == Tile.LADDER_TILE;

		if (xf != oldFootX || yf != oldFootY) {
			world.steppedOffTile(oldFootX, oldFootY, this);
			world.steppedOnTile(xf, yf, this);

			oldFootX = xf;
			oldFootY = yf;
		}
		
		int xc = (int)(body.x0 + body.x1) >>> 1;
		int yc = (int)(body.y0 + body.y1) >>> 1;
		Tile centerTile = world.getTile(xc, yc);
		wasInLaser = inLaser;
		inLaser = centerTile instanceof BeamTile;
		world.entityInsideTile(xc, yc, this);
		
		if (inLaser) {
			EntityFacing laserFacing = ((BeamTile)centerTile).getFacing(world, xc, yc);
			switch (laserFacing) {
			case UP:
			case DOWN:
				velocity.y = laserFacing.getOffset().y * 0.3f;
				break;
			case RIGHT:
			case LEFT:
				velocity.x = laserFacing.getOffset().x * 0.3f;
				break;
			}
		}
	}
	
	public float getHorizontalFriction() {
		return onLadder ? 0.75f : 0.85f;
	}

	public float getVerticalFriction() {
		return onLadder ? 0.85f : 0.95f;
	}
	
	public void move(float xm, float ym) {
		float oxm = xm;
		float oym = ym;
		
		List<AABB> tileHitboxes = ((PPWorld)world).getTileColliders(body.expand(xm, ym));
		for (AABB aabb : tileHitboxes)
			ym = aabb.clipY(body, ym);
		body.move(0, ym);
		if (ym != oym)
			velocity.y = 0;

		for (AABB aabb : tileHitboxes) 
			xm = aabb.clipX(body, xm);
		body.move(xm, 0);
		if (xm != oxm)
			velocity.x = 0;
		
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
