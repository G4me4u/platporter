package com.g4mesoft.net.client;

import com.g4mesoft.util.GameEvent;
import com.g4mesoft.util.GameEventListener;

public abstract class ClientNetworkGameEventListener extends GameEventListener {

	public ClientNetworkGameEventListener() {
		super(ClientNetworkGameEvent.class);
	}

	@Override
	protected final void handleEvent(GameEvent event) {
		ClientNetworkGameEvent networkEvent = (ClientNetworkGameEvent)event;
		switch (networkEvent.getNetworkAction()) {
		case ClientNetworkGameEvent.DISCONNECTED:
			clientDisconnected(networkEvent);
			break;
		case ClientNetworkGameEvent.CONNECTED:
			clientConnected(networkEvent);
			break;
		case ClientNetworkGameEvent.FULLY_CONNECTED:
			clientFullyConnected(networkEvent);
			break;
		}
	}
	
	public abstract void clientDisconnected(ClientNetworkGameEvent event);

	public abstract void clientConnected(ClientNetworkGameEvent event);

	public abstract void clientFullyConnected(ClientNetworkGameEvent event);
}
