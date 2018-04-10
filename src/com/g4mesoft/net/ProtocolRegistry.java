package com.g4mesoft.net;

public final class ProtocolRegistry extends NetworkRegistry<Protocol> {

	private static ProtocolRegistry instance;

	private ProtocolRegistry() {
		addEntry(HandshakeProtocol.class);
		
		addEntry(EntityProtocol.class);
	}
	
	public static ProtocolRegistry getInstance() {
		if (instance == null)
			instance = new ProtocolRegistry();
		return instance;
	}
}
