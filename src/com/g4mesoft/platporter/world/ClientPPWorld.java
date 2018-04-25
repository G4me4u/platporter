package com.g4mesoft.platporter.world;

import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.platporter.PlatPorter;
import com.g4mesoft.platporter.input.KeyManager;
import com.g4mesoft.platporter.world.entity.PPEntity;
import com.g4mesoft.platporter.world.tile.Tile;
import com.g4mesoft.world.entity.Entity;
import com.g4mesoft.world.phys.AABB;

public class ClientPPWorld extends PPWorld {

	protected boolean viewHitboxes;
	
	public ClientPPWorld(PlatPorter platPorter) {
		super(platPorter);
	}
	
	@Override
	public void update() {
		super.update();
		
		if (KeyManager.KEY_TOGGLE_HITBOX.isClicked())
			viewHitboxes = !viewHitboxes;
	}
	
	@Override
	public void render(Screen2D screen, float dt) {
		renderTiles(screen, dt, true);

		for (Entity entity : entities) {
			if (entity instanceof PPEntity)
				((PPEntity)entity).render(screen, dt);
		}
		
		renderTiles(screen, dt, false);
		
		if (viewHitboxes)
			renderHitboxes(screen, dt);
	}
	
	private void renderHitboxes(Screen2D screen, float dt) {
		for (int yt = 0; yt < WORLD_HEIGHT; yt++) {
			for (int xt = 0; xt < WORLD_WIDTH; xt++) {
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
		for (int yt = 0; yt < WORLD_HEIGHT; yt++) {
			for (int xt = 0; xt < WORLD_WIDTH; xt++) {
				Tile tile = getTile(xt, yt);
				if (tile != Tile.AIR_TILE && tile.isBackgroundLayer(this, xt, yt) == background)
					tile.render(this, screen, xt, yt);
			}
		}
	}
}
