package com.g4mesoft.platporter.world;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.net.WorldProtocol;
import com.g4mesoft.net.client.ClientNetworkManager;
import com.g4mesoft.platporter.PlatPorter;
import com.g4mesoft.platporter.input.KeyManager;
import com.g4mesoft.platporter.world.entity.PPEntity;
import com.g4mesoft.platporter.world.tile.Tile;
import com.g4mesoft.world.entity.Entity;
import com.g4mesoft.world.phys.AABB;

public class ClientPPWorld extends PPWorld {

	protected boolean viewHitboxes;
	
	protected final ClientNetworkManager client;
	protected WorldProtocol worldProtocol;
	
	protected int tileOffsetX;
	protected int tileOffsetY;
	
	public ClientPPWorld(PlatPorter platPorter) {
		super(platPorter);
		
		client = (ClientNetworkManager)platPorter.getNetworkManager();
		worldProtocol = (WorldProtocol)client.getProtocol(WorldProtocol.class);
	}
	
	@Override
	public void update() {
		super.update();
		
		if (KeyManager.KEY_TOGGLE_HITBOX.isClicked())
			viewHitboxes = !viewHitboxes;
		
		PPEntity player = getEntity(client.getConnectionUUID());
		if (player != null) {
			AABB body = player.getBody();
			int lx = (int)(body.x0 + body.x1) / (LEVEL_SIZE * 2);
			int ly = (int)(body.y0 + body.y1) / (LEVEL_SIZE * 2);
			tileOffsetX = Math.max(0, Math.min(WORLD_WIDTH, lx * LEVEL_SIZE));
			tileOffsetY = Math.max(0, Math.min(WORLD_HEIGHT, ly * LEVEL_SIZE));
		}
	}
	
	@Override
	public void render(Screen2D screen, float dt) {
		int xo = screen.getOffsetX();
		int yo = screen.getOffsetY();
		screen.setOffset(-tileOffsetX * 8, -tileOffsetY * 8);

		renderTiles(screen, dt, true);

		for (Entity entity : entities) {
			if (entity instanceof PPEntity)
				((PPEntity)entity).render(screen, dt);
		}
		
		renderTiles(screen, dt, false);
		
		if (viewHitboxes)
			renderHitboxes(screen, dt);
	
		screen.setOffset(xo, yo);
	}
	
	private void renderHitboxes(Screen2D screen, float dt) {
		for (int yt = tileOffsetY; yt < tileOffsetY + LEVEL_SIZE; yt++) {
			for (int xt = tileOffsetX; xt < tileOffsetX + LEVEL_SIZE; xt++) {
				Tile tile = getTile(xt, yt);
				if (tile.hasHitbox(this, xt, yt))
					drawHitbox(screen, tile.getBoundingBox(this, xt, yt), ColorPalette.getColor(500));
			}
		}
		
		for (Entity ent : entities) {
			if (ent instanceof PPEntity)
				drawHitbox(screen, ((PPEntity)ent).getBody(), ColorPalette.getColor(5));
		}
	}
	
	private void drawHitbox(Screen2D screen, AABB hitbox, int color) {
		int x0 = Math.round(hitbox.x0 * 8.0f);
		int y0 = Math.round(hitbox.y0 * 8.0f);
		int x1 = Math.round(hitbox.x1 * 8.0f);
		int y1 = Math.round(hitbox.y1 * 8.0f);
		screen.drawRect(x0, y0, x1 - x0, y1 - y0, color);
	}
	
	private void renderTiles(Screen2D screen, float dt, boolean background) {
		for (int yt = tileOffsetY; yt < tileOffsetY + LEVEL_SIZE; yt++) {
			for (int xt = tileOffsetX; xt < tileOffsetX + LEVEL_SIZE; xt++) {
				Tile tile = getTile(xt, yt);
				if (tile != Tile.AIR_TILE && tile.isBackgroundLayer(this, xt, yt) == background)
					tile.render(this, screen, xt, yt);
			}
		}
	}
	
	@Override
	public void interactWithTile(int xt, int yt, PPEntity entity) {
		super.interactWithTile(xt, yt, entity);

		ClientNetworkManager client = (ClientNetworkManager)platPorter.getNetworkManager();
		if (client.getConnectionUUID().equals(entity.getUUID()))
			worldProtocol.sendWorldInteractionEvent(xt, yt);
	}
	
	@Override
	public void steppedOnTile(int xt, int yt, PPEntity entity) {
		getTile(xt, yt).steppedOn(this, xt, yt, entity);
	}

	@Override
	public void steppedOffTile(int xt, int yt, PPEntity entity) {
		getTile(xt, yt).steppedOn(this, xt, yt, entity);
	}
}
