package com.g4mesoft.platporter.world;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.WorldProtocol;
import com.g4mesoft.net.server.ClientConnection;
import com.g4mesoft.net.server.ServerNetworkGameEvent;
import com.g4mesoft.net.server.ServerNetworkGameEventListener;
import com.g4mesoft.platporter.PlatPorter;
import com.g4mesoft.platporter.world.tile.Tile;
import com.g4mesoft.util.GameEventManager;

public class ServerPPWorld extends PPWorld {

	protected int currentLevel;

	private final WorldProtocol worldProtocol;
	
	public ServerPPWorld(PlatPorter platPorter) {
		super(platPorter);

		NetworkManager networkManager = platPorter.getNetworkManager();
		worldProtocol = (WorldProtocol)networkManager.getProtocol(WorldProtocol.class);
	
		GameEventManager eventManager = platPorter.getEventManager();
		eventManager.addEventListener(new ServerNetworkGameEventListener() {
			@Override
			public void clientDisconnected(ServerNetworkGameEvent event) {
			}
			
			@Override
			public void clientConnected(ServerNetworkGameEvent event) {
				ClientConnection client = event.getClient();
				worldProtocol.sendWorldTiles(tiles, data, client.getClientUUID());
			}
		});
		
		BufferedImage levelImage = null;
		try {
			levelImage = ImageIO.read(PPWorld.class.getResource("/assets/levels.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		parseLevels(levelImage);
		
		loadLevel(1);
	}

	private void parseLevels(BufferedImage levelImage) {
		for (int x = 0; x < WORLD_WIDTH; x++) {
			for (int y = 0; y < WORLD_HEIGHT * NUM_LEVELS; y++) {
				int i = x + y * WORLD_WIDTH;
				int rgb = levelImage.getRGB(x, y);
				levelsTiles[i] = Tile.parseTile((rgb >>> 8) & 0xFFFF).index;
				levelsData[i] = (byte)rgb;
			}
		}
	}
	
	private void loadLevel(int index) {
		this.currentLevel = index;
		
		int ti = WORLD_WIDTH * WORLD_HEIGHT;
		int li = ti * (index + 1);
		while (ti != 0) {
			ti--;
			li--;
			
			tiles[ti] = levelsTiles[li];
			data[ti] = levelsData[li];
		}
		
		// Send new world data to all clients
		worldProtocol.sendWorldTiles(tiles, data, null);
	}
	
	@Override
	public void update() {
		super.update();
	}
}
