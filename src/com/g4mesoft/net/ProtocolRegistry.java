package com.g4mesoft.net;

public final class ProtocolRegistry extends NetworkRegistry<Protocol> {

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
