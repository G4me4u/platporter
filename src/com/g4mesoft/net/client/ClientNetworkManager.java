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
import com.g4mesoft.net.packet.server.S00HandshakePacket;

public class ClientNetworkManager extends NetworkManager {

	public static final long CLIENT_HANDSHAKE_SEQUENCE = 0x636c69656e74L;
	
	private SocketAddress serverAddress;

	private boolean connected;
	private UUID connectionUUID;
	
	public ClientNetworkManager() throws SocketException {
		super(new DatagramSocket(), NetworkSide.CLIENT);
	}

	public void connect(SocketAddress serverAddress) throws SocketException {
		this.serverAddress = serverAddress;
		
		connected = false;
		socket.connect(serverAddress);

		addPacketToSend(new C00HandshakePacket(CLIENT_HANDSHAKE_SEQUENCE));
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
	
	public boolean isConnected() {
		return connected;
	}
	
	public UUID getConnectionUUID() {
		return connectionUUID;
	}

	public void makeHandshake(S00HandshakePacket handshakePacket) {
		connected = true;
		connectionUUID = handshakePacket.clientUUID;
	}
}
