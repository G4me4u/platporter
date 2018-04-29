package com.g4mesoft.net;

import com.g4mesoft.util.Registry;

public final class ProtocolRegistry extends Registry<Protocol> {

	private static ProtocolRegistry instance;

	private ProtocolRegistry() {
		// Connect
		addEntry(HandshakeProtocol.class);
		
		// World
		addEntry(EntityProtocol.class);
		addEntry(WorldProtocol.class);
	}
	
	public static ProtocolRegistry getInstance() {
		if (instance == null)
			instance = new ProtocolRegistry();
		return instance;
	}
}
