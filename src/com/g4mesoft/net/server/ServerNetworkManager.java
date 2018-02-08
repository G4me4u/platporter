package com.g4mesoft.net.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.NetworkSide;
import com.g4mesoft.net.client.ClientNetworkManager;
import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.packet.client.C00HandshakePacket;
import com.g4mesoft.net.packet.server.S00HandshakePacket;

public class ServerNetworkManager extends NetworkManager {

	private static final long SERVER_HANDSHAKE = 0x736572766572L;
	
	private Map<UUID, ClientConnection> connectedClients;
	
	public ServerNetworkManager(int port) throws SocketException {
		super(new DatagramSocket(port), NetworkSide.SERVER);
	
		connectedClients = new HashMap<UUID, ClientConnection>();
	}

	@Override
	protected boolean sendPacket(Packet packet, Object identifier) {
		ClientConnection client = connectedClients.get(identifier);
		if (client == null)
			return false;
		
		DatagramPacket datagramPacket = prepareToSendPacket(packet);
		datagramPacket.setSocketAddress(client.getAddress());
		
		try {
			socket.send(datagramPacket);
		} catch (IOException se) {
			se.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean isAddressConnected(SocketAddress address) {
		for (ClientConnection client : connectedClients.values())
			if (client.getAddress().equals(address))
				return true;
		return false;
	}
	
	public void makeHandshake(C00HandshakePacket handshakePacket) {
		long seq = handshakePacket.sequence;
		if (seq != ClientNetworkManager.CLIENT_HANDSHAKE_SEQUENCE) {
			if (seq == SERVER_HANDSHAKE + 1L) {
				// TODO: confirm client
			}
			return;
		}

		SocketAddress address = handshakePacket.address;
		if (address == null || isAddressConnected(address))
			return;
	
		UUID clientUUID;
		do {
			clientUUID = UUID.randomUUID();
		} while (connectedClients.containsKey(clientUUID));
		
		addPacketToSend(new S00HandshakePacket(SERVER_HANDSHAKE, seq + 1L, clientUUID), clientUUID);
		connectedClients.put(clientUUID, new ClientConnection(address, clientUUID));
	}
}
