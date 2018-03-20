package com.g4mesoft.net.server;

import java.net.SocketAddress;
import java.util.UUID;

public class ClientConnection {

	private final SocketAddress address;
	private final UUID clientUUID;
	
	private long lastPingTime;
	
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
	
	public void setLastPingTime(long time) {
		lastPingTime = time;
	}
	
	public long getLastPingTime() {
		return lastPingTime;
	}
}
