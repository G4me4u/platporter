package com.g4mesoft.platporter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Map;

import com.g4mesoft.Application;
import com.g4mesoft.graphic.Renderer2D;
import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.input.key.KeyInputListener;
import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.client.ClientNetworkManager;
import com.g4mesoft.net.server.ServerNetworkManager;
import com.g4mesoft.platporter.sound.Sounds;
import com.g4mesoft.platporter.world.ClientPPWorld;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.platporter.world.ServerPPWorld;
import com.g4mesoft.util.FileUtil;
import com.g4mesoft.util.GameEventManager;
import com.g4mesoft.util.ScheduledTaskManager;

public class PlatPorter extends Application {

	private static final String CLIENT_DISPLAY_CONFIG = "/config/display_client.txt";
	private static final String SERVER_DISPLAY_CONFIG = "/config/display_server.txt";
	private static final String IP_CONFIG = "/config/ip_config.txt";
	
	private static final int SIZE = 128;
	private static final int BORDER = 2;
	
	private NetworkManager networkManager;
	
	private BufferedImage image;
	private int[] pixels;
	private Screen2D screen;
	
	private ScheduledTaskManager taskManager;
	private GameEventManager eventManager;
	
	private boolean client;
	
	private PPWorld world;
	
	private long connectionTimer;

	public PlatPorter(boolean client) {
		super(client ? CLIENT_DISPLAY_CONFIG : SERVER_DISPLAY_CONFIG);
		this.client = client;
	}
	
	@Override
	public void init() {
		super.init();
		
		KeyInputListener.getInstance().registerDisplay(getDisplay());
		
		image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		screen = new Screen2D(pixels, SIZE, SIZE);

		taskManager = new ScheduledTaskManager();
		eventManager = new GameEventManager();
		
		setMinimumFps(120);
		
		Map<String, String> ipConfig = null;
		Reader ipConfigReader = null;
		try {
			ipConfigReader = new InputStreamReader(PlatPorter.class.getResourceAsStream(IP_CONFIG));
			ipConfig = FileUtil.readConfigFile(ipConfigReader, "=");
		} catch (IOException e) {
			throw new RuntimeException("IP Config file not found: " + IP_CONFIG, e);
		} finally {
			if (ipConfigReader != null) {
				try {
					ipConfigReader.close();
				} catch (IOException e) {
					// Nothing can be done here
				}
			}
		}
		
		if (client) {
			if (!ipConfig.containsKey("ip") || !ipConfig.containsKey("port"))
				throw new RuntimeException(IP_CONFIG + " does not contain both ip and port parameters!");
			
			try {
				ClientNetworkManager clientNetworkManager = new ClientNetworkManager(this);
				clientNetworkManager.connect(new InetSocketAddress(ipConfig.get("ip"), Integer.valueOf(ipConfig.get("port"))));
				networkManager = clientNetworkManager;
			} catch (SocketException se) {
				se.printStackTrace();
			}

			world = new ClientPPWorld(this);
			
			Sounds.loadAllSounds();
		} else {
			if (!ipConfig.containsKey("port"))
				throw new RuntimeException(IP_CONFIG + " does not contain port parameter!");
			
			try {
				networkManager = new ServerNetworkManager(Integer.valueOf(ipConfig.get("port")), this);
			} catch (SocketException se) {
				se.printStackTrace();
			}
			
			setDebug(false);

			world = new ServerPPWorld(this);
		}
	}
	
	@Override
	public void stop() {
		super.stop();
		
		networkManager.close();
	}
	
	@Override
	protected void tick() {
		taskManager.update();
		
		networkManager.update();
		world.update();
		
		if (client && !((ClientNetworkManager)networkManager).isConnected())
			connectionTimer++;
		
		KeyInputListener.getInstance().updateKeys();
	}

	@Override
	protected void render(Renderer2D renderer, float dt) {
		if (!client)
			return;
		
		screen.clear(ColorPalette.getColor(0));
		
		if (((ClientNetworkManager)networkManager).isConnected()) {
			world.render(screen, dt);
		} else {
			int textColor = (connectionTimer & 16L) != 0L ? ColorPalette.getColor(555) : ColorPalette.getColor(222);
			screen.drawText("Connecting...", SIZE / 2, SIZE / 2, textColor, Screen2D.CENTER_TEXT_X | Screen2D.CENTER_TEXT_Y);
		}
		
		Graphics g = renderer.getGraphics();

		int width = getDisplay().getWidth();
		int height = getDisplay().getHeight();

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		
		int scale = Math.max(Math.min(width, height) / (SIZE + BORDER), 1);
		int size = SIZE * scale;
		int x0 = (width - size) >> 1;
		int y0 = (height - size) >> 1;

		g.drawImage(image, x0, y0, size, size, null);
	}
	
	public ScheduledTaskManager getTaskManager() {
		return taskManager;
	}

	public GameEventManager getEventManager() {
		return eventManager;
	}
	
	public NetworkManager getNetworkManager() {
		return networkManager;
	}

	public PPWorld getWorld() {
		return world;
	}
	
	public boolean isClient() {
		return client;
	}
}
