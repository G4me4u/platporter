package com.g4mesoft.net.server;

import com.g4mesoft.util.GameEvent;

public class ServerNetworkGameEvent extends GameEvent {

	public static final int DISCONNECTED = 0x01;
	public static final int CONNECTED = 0x02;
	
	private final int networkAction;
	private final ClientConnection client;
	
	public ServerNetworkGameEvent(Object sender, String desc, int networkAction, ClientConnection client) {
		super(sender, desc);

		switch (networkAction) {
		case DISCONNECTED:
		case CONNECTED:
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
