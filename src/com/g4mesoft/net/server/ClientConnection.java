package com.g4mesoft.net.server;

import java.net.SocketAddress;
import java.util.UUID;

public class ClientConnection {

	private final SocketAddress address;
	private final UUID clientUUID;
	
	private boolean connectionConfirmed;
	
	public ClientConnection(SocketAddress address, UUID clientUUID) {
		this.address = address;
		this.clientUUID = clientUUID;
	
		connectionConfirmed = false;
	}
	
	public void confirmConnection() {
		connectionConfirmed = true;
	}
	
	public SocketAddress getAddress() {
		return address;
	}
	
	public UUID getClientUUID() {
		return clientUUID;
	}
	
	public boolean isConnectionConfirmed() {
		return connectionConfirmed;
	}
}
