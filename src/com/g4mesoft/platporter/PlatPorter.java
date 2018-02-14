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
import com.g4mesoft.net.client.ClientNetworkManager;
import com.g4mesoft.platporter.input.KeyManager;
import com.g4mesoft.util.ScheduledTaskManager;

public class PlatPorter extends Application {

	private static final int WIDTH = 128;
	private static final int HEIGHT = 128;
	private static final int SCALE = 4;
	
	private ClientNetworkManager client;
	
	private BufferedImage image;
	private int[] pixels;
	private Screen2D screen;
	
	private ScheduledTaskManager taskManager;
	
	@Override
	public void init() {
		super.init();
		
		KeyInputListener.getInstance().registerDisplay(getDisplay());
		
		try {
			client = new ClientNetworkManager(this);
			client.connect(new InetSocketAddress("10.160.206.181", 25565));
		} catch (SocketException se) {
			se.printStackTrace();
		}
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		screen = new Screen2D(pixels, WIDTH, HEIGHT);

		taskManager = new ScheduledTaskManager();
	}
	
	@Override
	public void stop() {
		super.stop();
		
		client.close();
	}
	
	@Override
	protected void tick() {
		taskManager.update();
		
		client.update();
		
		KeyInputListener.getInstance().updateKeys();
	}

	@Override
	protected void render(Renderer2D renderer, float dt) {
		screen.clear(ColorPalette.getColor(0));
		screen.render(dt);
		
		Graphics g = renderer.getGraphics();
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
	}
	
	public static void main(String[] args) throws Exception {
		new PlatPorter().start();
	}
}
