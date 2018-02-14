package com.g4mesoft.net.client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.UUID;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.NetworkSide;
import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.packet.client.C00HandshakePacket;
import com.g4mesoft.net.packet.client.C01AcknowledgePacket;
import com.g4mesoft.net.packet.client.C02PingPacket;
import com.g4mesoft.net.packet.server.S00HandshakePacket;
import com.g4mesoft.net.packet.server.S01PongPacket;
import com.g4mesoft.platporter.PlatPorter;

public class ClientNetworkManager extends NetworkManager {

	public static final long CLIENT_HANDSHAKE = 0x636c69656e74L;
	
	public static final long PING_INTERVAL = 20;
	public static final long MAX_PONG_INTERVAL = 100;
	
	private SocketAddress serverAddress;

	private boolean connected;
	private UUID connectionUUID;
	
	private long lastServerPong;
	private long pingDelay;
	
	public ClientNetworkManager(PlatPorter platPorter) throws SocketException {
		super(new DatagramSocket(), NetworkSide.CLIENT, platPorter);
	}

	public void connect(SocketAddress serverAddress) throws SocketException {
		this.serverAddress = serverAddress;
		
		connected = false;
		socket.connect(serverAddress);

		addPacketToSend(new C00HandshakePacket(CLIENT_HANDSHAKE));
	}
	
	public void disconnect() {
		serverAddress = null;
		connected = false;

		socket.disconnect();
	}

	@Override
	public void update() {
		if (uptime - lastServerPong < MAX_PONG_INTERVAL)
			disconnect();
	}

	@Override
	protected boolean confirmPacket(Packet packet) {
		return serverAddress != null && serverAddress.equals(packet.address);
	}
	
	@Override
	protected boolean sendPacket(Packet packet, Object identifier) {
		try {
			socket.send(prepareToSendPacket(packet));
		} catch (IOException se) {
			se.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void makeHandshake(S00HandshakePacket handshakePacket) {
		if (connected || handshakePacket.acknowledgement != CLIENT_HANDSHAKE + 1L)
			return;
		
		connected = true;
		connectionUUID = handshakePacket.clientUUID;
		
		lastServerPong = uptime;
		
		platPorter.getTaskManager().addTask(() -> {
			if (!connected)
				return false;
			addPacketToSend(new C02PingPacket(connectionUUID));
			return true;
		}, PING_INTERVAL);
		
		addPacketToSend(new C01AcknowledgePacket(handshakePacket.sequence + 1L, connectionUUID));
	}
	
	public void processPong(S01PongPacket pongPacket) {
		if (!connected)
			return;
		lastServerPong = uptime;
		
		pingDelay = Math.max(pongPacket.pingInterval - PING_INTERVAL, 0L);
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public UUID getConnectionUUID() {
		return connectionUUID;
	}
	
	public long getPingDelay() {
		return pingDelay;
	}
}
