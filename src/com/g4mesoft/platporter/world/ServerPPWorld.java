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
		
		loadLevel(1);
	}
	
	private void handleEvents() {
		GameEventManager eventManager = platPorter.getEventManager();
		
		eventManager.addEventListener(new ServerNetworkGameEventListener() {
			@Override
			public void clientDisconnected(ServerNetworkGameEvent event) { 
				UUID clientUUID = event.getClient().getClientUUID();
				removeEntity(clientUUID);
				
				entityProtocol.removeEntity(null, clientUUID);
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
		for (int l = 0; l < NUM_LEVELS; l++) {
			int numSpawnPoints = 0;
			Vec2f spawnPoint = spawnPoints[l] = new Vec2f();
			
			for (int x = 0; x < WORLD_WIDTH; x++) {
				for (int y = 0; y < WORLD_HEIGHT; y++) {
					int yy = y + l * WORLD_HEIGHT;
					int i = x + yy * WORLD_WIDTH;
					int rgb = levelImage.getRGB(x, yy);
					int id = (rgb >>> 8) & 0xFFFF;
					if (id == 0xC8C8) {
						numSpawnPoints++;
						spawnPoint.set(x, y);
					} else {
						levelsTiles[i] = Tile.parseTile(id).index;
						levelsData[i] = (byte)rgb;
					}
				}
			}
			
			if (numSpawnPoints != 0) {
				spawnPoint.div(numSpawnPoints);
			} else {
				spawnPoint.set(WORLD_WIDTH / 2.0f, WORLD_HEIGHT / 2.0f);
			}
		}
	}
	
	private void loadLevel(int index) {
		this.currentLevel = index;
		
		int ti = WORLD_WIDTH * WORLD_HEIGHT;
		int li = WORLD_WIDTH * WORLD_HEIGHT * (index + 1);
		while (ti != 0) {
			ti--;
			li--;
			
			tiles[ti] = levelsTiles[li];
			data[ti] = levelsData[li];
		}
		
		// Send new world data to all clients
		worldProtocol.sendWorldTiles(tiles, data, null);
		
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
