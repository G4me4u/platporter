package com.g4mesoft.platporter.world;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.g4mesoft.math.Vec2f;
import com.g4mesoft.math.Vec2i;
import com.g4mesoft.net.EntityProtocol;
import com.g4mesoft.net.WorldProtocol;
import com.g4mesoft.net.server.ClientConnection;
import com.g4mesoft.net.server.ServerNetworkGameEvent;
import com.g4mesoft.net.server.ServerNetworkGameEventListener;
import com.g4mesoft.net.server.ServerNetworkManager;
import com.g4mesoft.platporter.PlatPorter;
import com.g4mesoft.platporter.world.entity.PPEntity;
import com.g4mesoft.platporter.world.tile.Tile;
import com.g4mesoft.util.GameEventManager;
import com.g4mesoft.world.entity.Entity;
import com.g4mesoft.world.entity.EntityFacing;

public class ServerPPWorld extends PPWorld {

	private static final int ACTIVATE_POOL_SIZE = 16;

	private final ServerNetworkManager server;
	
	private final WorldProtocol worldProtocol;
	private final EntityProtocol entityProtocol;
	protected final Set<Vec2i> dirtyTiles;
	
	protected final int[] levelsTiles;
	protected final byte[] levelsData;
	protected final Vec2f[] spawnPoints;
	
	protected int currentLevel;
	
	public ServerPPWorld(PlatPorter platPorter) {
		super(platPorter);
		
		dirtyTiles = new HashSet<Vec2i>();

		server = (ServerNetworkManager)platPorter.getNetworkManager();
		worldProtocol = (WorldProtocol)server.getProtocol(WorldProtocol.class);
		entityProtocol = (EntityProtocol)server.getProtocol(EntityProtocol.class);
		
		levelsTiles = new int[WORLD_WIDTH * WORLD_HEIGHT * NUM_LEVELS];
		levelsData = new byte[WORLD_WIDTH * WORLD_HEIGHT * NUM_LEVELS];
		spawnPoints = new Vec2f[NUM_LEVELS];
		
		handleEvents();
		
		BufferedImage levelImage = null;
		try {
			levelImage = ImageIO.read(PPWorld.class.getResource("/assets/levels.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		parseLevels(levelImage);
	}
	
	private void handleEvents() {
		GameEventManager eventManager = platPorter.getEventManager();
		
		eventManager.addEventListener(new ServerNetworkGameEventListener() {
			@Override
			public void clientDisconnected(ServerNetworkGameEvent event) { 
				removeEntity(event.getClient().getClientUUID());
			}
			
			@Override
			public void clientConnected(ServerNetworkGameEvent event) { }

			@Override
			public void clientFullyConnected(ServerNetworkGameEvent event) {
				ClientConnection client = event.getClient();
				worldProtocol.sendWorldTiles(tiles, data, client.getClientUUID());
			
				UUID clientUUID = event.getClient().getClientUUID();
				for (Entity e : getEntityList()) {
					if (e instanceof PPEntity) {
						PPEntity ent = (PPEntity)e;
						if (ent.getUUID().equals(clientUUID))
							continue;
						
						float x = ent.pos.x;
						float y = ent.pos.y;
						entityProtocol.addEntity(clientUUID, ent.getUUID(), x, y, ent.facing);
					}
				}
				
				Vec2f spawnPoint = spawnPoints[currentLevel];
				float x = spawnPoint.x;
				float y = spawnPoint.y;
				EntityFacing facing = EntityFacing.RIGHT;
				entityProtocol.setEntityPosition(clientUUID, clientUUID, x, y, facing);
			}
		});
	}
	
	@Override
	public boolean setTileIndex(int xt, int yt, int tileIndex) {
		if (super.setTileIndex(xt, yt, tileIndex)) {
			dirtyTiles.add(new Vec2i(xt, yt));
			return true;
		}
		return false;
	}

	@Override
	public boolean setData(int xt, int yt, byte data) {
		if (super.setData(xt, yt, data)) {
			dirtyTiles.add(new Vec2i(xt, yt));
			return true;
		}
		return false;
	}

	private void parseLevels(BufferedImage levelImage) {
		int[] numSpawnPoints = new int[NUM_LEVELS];
			
		for (int x = 0; x < WORLD_WIDTH; x++) {
			for (int y = 0; y < WORLD_HEIGHT; y++) {
				int i = x + y * WORLD_WIDTH;
				int rgb = levelImage.getRGB(x, y);
				int id = (rgb >>> 8) & 0xFFFF;
				if (id == 0xC8C8) {
					int lx = x / LEVEL_SIZE;
					int ly = y / LEVEL_SIZE;
					int l = lx + ly * 2;
					
					numSpawnPoints[l]++;
					if (spawnPoints[l] == null)
						spawnPoints[l] = new Vec2f();
					spawnPoints[l].set(x, y);
				} else {
					tiles[i] = Tile.parseTile(id).index;
					data[i] = (byte)rgb;
				}
			}
		}
		
		for (int l = 0; l < NUM_LEVELS; l++) {
			if (numSpawnPoints[l] != 0) {
				spawnPoints[l].div(numSpawnPoints[l]);
			} else {
				float xc = ((l % 2) + 0.5f) * LEVEL_SIZE;
				float yc = ((l / 2) + 0.5f) * LEVEL_SIZE;
				spawnPoints[l] = new Vec2f(xc, yc);
			}
		}
	}
	
	private void loadLevel(PPEntity entity, int index) {
		// Send entity positions
		float x = spawnPoints[index].x;
		float y = spawnPoints[index].y;
		EntityFacing facing = EntityFacing.RIGHT;
		for (Entity ent : getEntityList()) {
			if (ent instanceof PPEntity)
				entityProtocol.setEntityPosition(null, ((PPEntity) ent).getUUID(), x, y, facing);
		}
	}
	
	@Override
	public synchronized void addEntity(Entity e) {
		super.addEntity(e);
		
		if (e instanceof PPEntity)  {
			PPEntity ent = (PPEntity)e;
			
			Vec2f spawnPoint = spawnPoints[currentLevel];
			float x = spawnPoint.x;
			float y = spawnPoint.y;
			EntityFacing facing = EntityFacing.RIGHT;
			ent.setPosition(x, y, facing);
			
			for (ClientConnection client : server.getConnectedClients().values()) {
				UUID clientUUID = client.getClientUUID();
				if (!client.isFullyConnected() || ent.getUUID().equals(clientUUID))
					continue;
				entityProtocol.addEntity(clientUUID, ent.getUUID(), x, y, facing);
			}
		}
	}
	
	@Override
	public synchronized Entity removeEntity(Entity e) {
		e = super.removeEntity(e);
		
		if (e instanceof PPEntity)
			entityProtocol.removeEntity(null, ((PPEntity)e).getUUID());
		return e;
	}
	
	@Override
	public void update() {
		super.update();
		
		for (Vec2i dirty : dirtyTiles) {
			int xt = dirty.x;
			int yt = dirty.y;
			
			// Send new tile state to all clients
			worldProtocol.sendTile(xt, yt, getTileIndex(xt, yt), getData(xt, yt), null);
		}
		dirtyTiles.clear();
	}
	
	@Override
	public void interactWithTile(int xt, int yt, PPEntity entity) {
		super.interactWithTile(xt, yt, entity);

		getTile(xt, yt).interactWith(this, xt, yt, entity);
	}
	
	@Override
	public void steppedOnTile(int xt, int yt, PPEntity entity) {
		super.steppedOnTile(xt, yt, entity);
		
		getTile(xt, yt).steppedOn(this, xt, yt, entity);
	}

	@Override
	public void steppedOffTile(int xt, int yt, PPEntity entity) {
		super.steppedOffTile(xt, yt, entity);
		
		getTile(xt, yt).steppedOff(this, xt, yt, entity);
	}
	
	@Override
	public void activateTile(int activateId, boolean state) {
		if (activateId < 0 || activateId >= ACTIVATE_POOL_SIZE)
			return;
		
		for (int yt = 0; yt < WORLD_HEIGHT; yt++) {
			for (int xt = 0; xt < WORLD_WIDTH; xt++) {
				Tile tile = getTile(xt, yt);
				int tileActivateId = tile.getActivateId(this, xt, yt);
				if (tileActivateId == activateId)
					tile.toggleActivate(this, xt, yt);
			}
		}
	}
}
