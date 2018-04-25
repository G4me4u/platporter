package com.g4mesoft.net.client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.UUID;

import com.g4mesoft.net.HandshakeProtocol;
import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.NetworkSide;
import com.g4mesoft.net.Protocol;
import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.packet.server.S00PongPacket;
import com.g4mesoft.net.packet.server.S01PositionPacket;
import com.g4mesoft.platporter.PlatPorter;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.platporter.world.entity.PPEntity;
import com.g4mesoft.platporter.world.entity.player.ClientPlayerEntity;
import com.g4mesoft.platporter.world.entity.player.NetworkPlayerEntity;

public class ClientNetworkManager extends NetworkManager {

	public static final long CLIENT_HANDSHAKE = 0x636c69656e74L;
	
	public static final long PING_INTERVAL = 20;
	public static final long MAX_PONG_INTERVAL = 100;
	
	private SocketAddress serverAddress;
	private UUID serverUUID;
	
	private boolean connected;
	private boolean handshaking;
	
	private long lastServerPong;
	
	public ClientNetworkManager(PlatPorter platPorter) throws SocketException {
		super(new DatagramSocket(), NetworkSide.CLIENT, platPorter, null);
	}

	public boolean connect(SocketAddress serverAddress) throws SocketException {
		this.serverAddress = serverAddress;
		
		connected = false;
		socket.connect(serverAddress);

		handshaking = true;

		Protocol protocol = getProtocol(HandshakeProtocol.class);
		if (protocol != null) {
			((HandshakeProtocol)protocol).handshakeServer();
			return true;
		}
		
		return false;
	}
	
	public void disconnect() {
		if (!connected)
			return;
		
		serverAddress = null;
		connected = false;
		handshaking = false;

		if (socket.isConnected())
			socket.disconnect();
		
		ClientNetworkGameEvent disconnectEvent = 
				new ClientNetworkGameEvent(this, ClientNetworkGameEvent.DISCONNECTED, "disconnected");
		platPorter.getEventManager().handleEvent(disconnectEvent);
	}

	@Override
	public void update() {
		super.update();
		
		if (connected && (uptime - lastServerPong > MAX_PONG_INTERVAL) || !socket.isConnected())
			disconnect();
	}

	@Override
	protected boolean confirmPacket(Packet packet) {
		if (serverUUID == null)
			return serverAddress != null && serverAddress.equals(packet.address);
		return serverUUID.equals(packet.senderUUID);
	}
	
	@Override
	protected boolean sendPacket(Packet packet, Object identifier) {
		try {
			socket.send(prepareToSendPacket(packet));
		} catch (IOException se) {
			se.printStackTrace();
			return false;
		} catch (NullPointerException npe) {
			// This exception is thrown when the server
			// is closed, and the client tries to send
			// a packet. But if the exception was thrown
			// for another reason, we should still print
			// it.
			if (socket.isConnected())
				npe.printStackTrace();
		}
		return true;
	}
	
	public void connectionFinished(UUID clientUUID, UUID serverUUID) {
		if (connected)
			return;
		
		connected = true;
		connectionUUID = clientUUID;
		this.serverUUID = serverUUID;
		handshaking = false;
		
		lastServerPong = uptime;
		platPorter.getTaskManager().addTask(new PingTask(this), PING_INTERVAL);
	
		PPWorld world = platPorter.getWorld();
		world.addEntity(new ClientPlayerEntity(world, clientUUID));
	
		ClientNetworkGameEvent connectedEvent = 
				new ClientNetworkGameEvent(this, ClientNetworkGameEvent.CONNECTED, "connected");
		platPorter.getEventManager().handleEvent(connectedEvent);
	}

	public void processPong(S00PongPacket pongPacket) {
		if (connected)
			lastServerPong = uptime;
	}

	public void addNetworkEntity(UUID playerUUID) {
		PPWorld world = platPorter.getWorld();
		world.addEntity(new NetworkPlayerEntity(world, playerUUID));
	}

	public void removeNetworkEntity(UUID entityUUID) {
		PPWorld world = platPorter.getWorld();
		world.removeEntity(entityUUID);
	}

	public void handlePositionPacket(S01PositionPacket positionPacket) {
		PPWorld world = platPorter.getWorld();
		PPEntity entity = world.getEntity(positionPacket.entityUUID);
		if (entity != null && positionPacket.facing != null)
			entity.setPosition(positionPacket.x, positionPacket.y, positionPacket.facing);
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public boolean isHandshaking() {
		return handshaking;
	}
	
	public UUID getServerUUID() {
		return serverUUID;
	}
}
