package com.g4mesoft.net.server;

import java.net.SocketAddress;
import java.util.UUID;

public class ClientConnection {

	private final SocketAddress address;
	private final UUID clientUUID;
	
	public ClientConnection(SocketAddress address, UUID clientUUID) {
		this.address = address;
		this.clientUUID = clientUUID;
	}
	
	public SocketAddress getAddress() {
		return address;
	}
	
	public UUID getClientUUID() {
		return clientUUID;
	}
}
