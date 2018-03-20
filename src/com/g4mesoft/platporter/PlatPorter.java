package com.g4mesoft.platporter;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.net.InetSocketAddress;
import java.net.SocketException;

import com.g4mesoft.Application;
import com.g4mesoft.graphic.Renderer2D;
import com.g4mesoft.graphics.ColorPalette;
import com.g4mesoft.graphics.Screen2D;
import com.g4mesoft.input.key.KeyInputListener;
import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.client.ClientNetworkManager;
import com.g4mesoft.net.server.ServerNetworkManager;
import com.g4mesoft.util.ScheduledTaskManager;

public class PlatPorter extends Application {

	private static final String CLIENT_DISPLAY_CONFIG = "/config/display_client.txt";
	private static final String SERVER_DISPLAY_CONFIG = "/config/display_server.txt";
	
	private static final int WIDTH = 128;
	private static final int HEIGHT = 128;
	private static final int SCALE = 4;
	
	private NetworkManager networkManager;
	
	private BufferedImage image;
	private int[] pixels;
	private Screen2D screen;
	
	private ScheduledTaskManager taskManager;
	
	private boolean client;
	
	public PlatPorter(boolean client) {
		super(client ? CLIENT_DISPLAY_CONFIG : SERVER_DISPLAY_CONFIG);
		this.client = client;
	}
	
	@Override
	public void init() {
		super.init();
		
		KeyInputListener.getInstance().registerDisplay(getDisplay());
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		screen = new Screen2D(pixels, WIDTH, HEIGHT);

		taskManager = new ScheduledTaskManager();

		if (client) {
			try {
				ClientNetworkManager clientNetworkManager = new ClientNetworkManager(this);
				clientNetworkManager.connect(new InetSocketAddress("169.254.7.62", 25565));
				networkManager = clientNetworkManager;
			} catch (SocketException se) {
				se.printStackTrace();
			}
		} else {
			try {
				networkManager = new ServerNetworkManager(25565, this);
			} catch (SocketException se) {
				se.printStackTrace();
			}
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
		
		KeyInputListener.getInstance().updateKeys();
	}

	@Override
	protected void render(Renderer2D renderer, float dt) {
		if (!client)
			return;
		
		screen.clear(ColorPalette.getColor(0));
		screen.render(dt);
		
		Graphics g = renderer.getGraphics();
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
	}
	
	public ScheduledTaskManager getTaskManager() {
		return taskManager;
	}
	
	public boolean isClient() {
		return client;
	}
	
	public static void main(String[] args) throws Exception {
		PlatPorter server = new PlatPorter(false);
		Thread thread = new Thread(() -> {
			server.start();
		});
		thread.setDaemon(true);
		thread.start();

		new PlatPorter(true).start();
		server.stopRunning();
	}
}
