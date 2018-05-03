package com.g4mesoft.net.client;

import com.g4mesoft.util.GameEvent;

public class ClientNetworkGameEvent extends GameEvent {

	public static final int DISCONNECTED = 0x01;
	public static final int CONNECTED = 0x02;
	public static final int FULLY_CONNECTED = 0x03;
	
	private final int networkAction;
	
	public ClientNetworkGameEvent(Object sender, int networkAction) {
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
	}
	
	public int getNetworkAction() {
		return networkAction;
	}
}
