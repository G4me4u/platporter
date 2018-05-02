package com.g4mesoft.net.server;

import com.g4mesoft.util.GameEvent;
import com.g4mesoft.util.GameEventListener;

public abstract class ServerNetworkGameEventListener extends GameEventListener {

	public ServerNetworkGameEventListener() {
		super(ServerNetworkGameEvent.class);
	}

	@Override
	protected final void handleEvent(GameEvent event) {
		ServerNetworkGameEvent networkEvent = (ServerNetworkGameEvent)event;
		switch (networkEvent.getNetworkAction()) {
		case ServerNetworkGameEvent.DISCONNECTED:
			clientDisconnected(networkEvent);
			break;
		case ServerNetworkGameEvent.CONNECTED:
			clientConnected(networkEvent);
			break;
		case ServerNetworkGameEvent.FULLY_CONNECTED:
			clientFullyConnected(networkEvent);
			break;
		}
	}
	
	public abstract void clientDisconnected(ServerNetworkGameEvent event);

	public abstract void clientConnected(ServerNetworkGameEvent event);

	public abstract void clientFullyConnected(ServerNetworkGameEvent event);
}
