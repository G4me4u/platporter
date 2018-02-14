package com.g4mesoft.net.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.NetworkSide;
import com.g4mesoft.net.client.ClientNetworkManager;
import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.packet.client.C00HandshakePacket;
import com.g4mesoft.net.packet.client.C01AcknowledgePacket;
import com.g4mesoft.net.packet.client.C02PingPacket;
import com.g4mesoft.net.packet.server.S00HandshakePacket;
import com.g4mesoft.net.packet.server.S01PongPacket;
import com.g4mesoft.platporter.PlatPorter;

public class ServerNetworkManager extends NetworkManager {

	public static final long SERVER_HANDSHAKE = 0x736572766572L;
	
	public static final long MAX_PING_INTERVAL = 100;
	
	private Map<UUID, ClientConnection> connectedClients;
	private List<ClientConnection> clientsToConfirm;
	
	private List<ClientConnection> clientsToDisconnect;
	
	public ServerNetworkManager(int port, PlatPorter platPorter) throws SocketException {
		super(new DatagramSocket(port), NetworkSide.SERVER, platPorter);
	
		connectedClients = new HashMap<UUID, ClientConnection>();
		clientsToConfirm = new ArrayList<ClientConnection>();
		
		clientsToDisconnect = new ArrayList<ClientConnection>();
	}

	@Override
	public void update() {
		super.update();
		
		for (ClientConnection client : connectedClients.values()) {
			if (uptime - client.getLastPingTime() < MAX_PING_INTERVAL)
				disconnectClient(client);
		}
		
		if (!clientsToDisconnect.isEmpty()) {
			for (ClientConnection client : clientsToDisconnect) {
				if (!client.isConnectionConfirmed())
					clientsToConfirm.remove(client);
				connectedClients.remove(client.getClientUUID());
			}
			clientsToDisconnect.clear();
		}
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
	
	public ClientConnection getClient(UUID clientUUID, SocketAddress address) {
		if (address != null) {
			ClientConnection client = connectedClients.get(clientUUID);
			if (client != null && address.equals(client.getAddress()))
				return client;
		}
		return null;
	}
	
	public void disconnectClient(UUID clientUUID) {
		disconnectClient(connectedClients.get(clientUUID));
	}

	public void disconnectClient(ClientConnection client) {
		if (client == null || !connectedClients.containsKey(client.getClientUUID()))
			return;
		if (clientsToDisconnect.contains(client))
			return;
		clientsToDisconnect.add(client);
	}
	
	public void handleHandshake(C00HandshakePacket handshakePacket) {
		long seq = handshakePacket.sequence;
		if (seq != ClientNetworkManager.CLIENT_HANDSHAKE)
			return;

		SocketAddress address = handshakePacket.address;
		if (address == null || isAddressConnected(address))
			return;
	
		UUID clientUUID;
		do {
			clientUUID = UUID.randomUUID();
		} while (connectedClients.containsKey(clientUUID));
		
		addPacketToSend(new S00HandshakePacket(SERVER_HANDSHAKE, seq + 1L, clientUUID), clientUUID);
		
		ClientConnection client = new ClientConnection(address, clientUUID);
		connectedClients.put(clientUUID, client);
		clientsToConfirm.add(client);
		
		client.setLastPingTime(uptime);
	}
	
	public void handleAcknowledgement(C01AcknowledgePacket acknowledgePacket) {
		if (acknowledgePacket.acknowledge != SERVER_HANDSHAKE + 1L)
			return;
		
		SocketAddress address = acknowledgePacket.address;
		if (address == null)
			return;
		
		for (int i = 0; i < clientsToConfirm.size(); i++) {
			ClientConnection client = clientsToConfirm.get(i);
			if (!acknowledgePacket.clientUUID.equals(client.getClientUUID()))
				continue;
			if (!address.equals(client.getAddress()))
				continue;
			
			clientsToConfirm.remove(i);
			break;
		}
	}

	public void processPing(C02PingPacket pingPacket) {
		ClientConnection client = getClient(pingPacket.clientUUID, pingPacket.address);
		if (client == null) 
			return;
		
		long pingInterval = uptime - client.getLastPingTime();
		client.setLastPingTime(uptime);
		addPacketToSend(new S01PongPacket(pingInterval), client.getClientUUID());
	}
}
