package com.g4mesoft.net.server;

import com.g4mesoft.util.GameEvent;

public class ServerNetworkGameEvent extends GameEvent {

	public static final int DISCONNECTED = 0x01;
	public static final int CONNECTED = 0x02;
	public static final int FULLY_CONNECTED = 0x03;
	
	private final int networkAction;
	private final ClientConnection client;
	
	public ServerNetworkGameEvent(Object sender, int networkAction, ClientConnection client) {
		super(sender);

		switch (networkAction) {
		case DISCONNECTED:
		case CONNECTED:
		case FULLY_CONNECTED:
			break;
		default:
			throw new IllegalArgumentException("Invalid network action!");
		}
		
		this.networkAction = networkAction;
		this.client = client;
	}
	
	public int getNetworkAction() {
		return networkAction;
	}
	
	public ClientConnection getClient() {
		return client;
	}
}
