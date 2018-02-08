package com.g4mesoft.net.client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.NetworkSide;
import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.packet.client.C00HandshakePacket;
import com.g4mesoft.net.packet.server.S00HandshakePacket;

public class ClientNetworkManager extends NetworkManager {

	public static final long CLIENT_HANDSHAKE_SEQUENCE = 0x636c69656e74L;
	
	public ClientNetworkManager() throws SocketException {
		super(new DatagramSocket(), NetworkSide.CLIENT);
	}

	public void connect(SocketAddress serverAddress) throws SocketException {
		socket.connect(serverAddress);
		
		addPacketToSend(new C00HandshakePacket(CLIENT_HANDSHAKE_SEQUENCE));
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
	}
}
