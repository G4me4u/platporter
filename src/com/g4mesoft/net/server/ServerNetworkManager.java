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

import com.g4mesoft.net.EntityProtocol;
import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.NetworkSide;
import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.packet.client.C00PingPacket;
import com.g4mesoft.net.packet.client.C01PositionPacket;
import com.g4mesoft.net.packet.server.S00PongPacket;
import com.g4mesoft.net.packet.server.S01PositionPacket;
import com.g4mesoft.platporter.PlatPorter;
import com.g4mesoft.world.entity.EntityFacing;

public class ServerNetworkManager extends NetworkManager {

	public static final long SERVER_HANDSHAKE = 0x736572766572L;
	
	public static final long MAX_PING_INTERVAL = 100;
	
	private Map<UUID, ClientConnection> connectedClients;
	private List<ClientConnection> clientsToDisconnect;
	
	public ServerNetworkManager(int port, PlatPorter platPorter) throws SocketException {
		super(new DatagramSocket(port), NetworkSide.SERVER, platPorter, UUID.randomUUID());
	
		connectedClients = new HashMap<UUID, ClientConnection>();
		clientsToDisconnect = new ArrayList<ClientConnection>();
	
		System.out.println("Server started on ip: " + socket.getLocalSocketAddress());
	}

	@Override
	public void update() {
		super.update();
		
		for (ClientConnection client : connectedClients.values()) {
			if (uptime - client.getLastPingTime() > MAX_PING_INTERVAL)
				disconnectClient(client);
		}
		
		if (!clientsToDisconnect.isEmpty()) {
			EntityProtocol addPlayerProtocol = (EntityProtocol)getProtocol(EntityProtocol.class);

			for (ClientConnection client : clientsToDisconnect) {
				UUID clientUUID = client.getClientUUID();
				connectedClients.remove(clientUUID);

				ServerNetworkGameEvent disconnectEvent = 	
						new ServerNetworkGameEvent(this, "disconnected", ServerNetworkGameEvent.DISCONNECTED, client);
				platPorter.getEventManager().handleEvent(disconnectEvent);
			
				for (UUID otherClientUUID : connectedClients.keySet())
					addPlayerProtocol.removeEntity(otherClientUUID, clientUUID);
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
	
	public boolean isClientConnected(UUID clientUUID) {
		return connectedClients.containsKey(clientUUID);
	}
	
	public ClientConnection getClient(UUID clientUUID, SocketAddress address) {
		if (address != null) {
			ClientConnection client = connectedClients.get(clientUUID);
			if (client != null && address.equals(client.getAddress()))
				return client;
		}
		return null;
	}

	public ClientConnection getClient(SocketAddress address) {
		if (address != null) {
			for (ClientConnection client : connectedClients.values()) {
				if (address.equals(client.getAddress()))
					return client;
			}
		}
		return null;
	}
	
	public void disconnectClient(UUID clientUUID) {
		disconnectClient(connectedClients.get(clientUUID));
	}

	public void disconnectClient(ClientConnection client) {
		System.out.println("Disconnecting client: " + client.getClientUUID());
		
		if (client == null || !connectedClients.containsKey(client.getClientUUID()))
			return;
		if (clientsToDisconnect.contains(client))
			return;
		clientsToDisconnect.add(client);
	}
	
	public ClientConnection addClient(SocketAddress address) {
		if (address == null || isAddressConnected(address))
			return null;
	
		UUID clientUUID;
		do {
			clientUUID = UUID.randomUUID();
		} while (connectedClients.containsKey(clientUUID));
		
		ClientConnection client = new ClientConnection(address, clientUUID);
		client.setLastPingTime(uptime);

		connectedClients.put(clientUUID, client);

		EntityProtocol addPlayerProtocol = (EntityProtocol)getProtocol(EntityProtocol.class);
		for (UUID otherClientUUID : connectedClients.keySet()) {
			if (clientUUID.equals(otherClientUUID))
				continue;
			addPlayerProtocol.addEntity(otherClientUUID, clientUUID);
			addPlayerProtocol.addEntity(clientUUID, otherClientUUID);
		}
		
		ServerNetworkGameEvent connectEvent = 
				new ServerNetworkGameEvent(this, "connected", ServerNetworkGameEvent.CONNECTED, client);
		platPorter.getEventManager().handleEvent(connectEvent);
		
		System.out.println("Client with id: " + clientUUID + " connected.");
		
		return client;
	}
	
	public void processPing(C00PingPacket pingPacket) {
		ClientConnection client = getClient(pingPacket.senderUUID, pingPacket.address);
		if (client == null) 
			return;
		
		client.setLastPingTime(uptime);
		addPacketToSend(new S00PongPacket(), client.getClientUUID());
	
		System.out.println("Received ping from: " + client.getClientUUID());
	}

	public void handlePositionPacket(C01PositionPacket positionPacket) {
		UUID entityUUID = positionPacket.senderUUID;
		float x = positionPacket.x;
		float y = positionPacket.y;
		EntityFacing facing = positionPacket.facing;
		
		for (UUID clientUUID : connectedClients.keySet()) {
			if (clientUUID.equals(entityUUID))
				continue;
			addPacketToSend(new S01PositionPacket(entityUUID, x, y, facing), clientUUID);
		}
	}
	
	public Map<UUID, ClientConnection> getConnectedClients() {
		return connectedClients;
	}
}
