package com.g4mesoft.net;

import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.packet.client.C00PingPacket;
import com.g4mesoft.net.packet.protocol.ProtocolAcknowledgePacket;
import com.g4mesoft.net.packet.protocol.ProtocolDataPacket;
import com.g4mesoft.net.packet.server.S00PongPacket;

public final class PacketRegistry extends NetworkRegistry<Packet> {

	private static PacketRegistry instance;

	private PacketRegistry() {
		// Protocol
		addEntry(ProtocolDataPacket.class);
		addEntry(ProtocolAcknowledgePacket.class);
		
		// Ping pong
		addEntry(C00PingPacket.class);
		
		addEntry(S00PongPacket.class);
	}
	
	public static PacketRegistry getInstance() {
		if (instance == null)
			instance = new PacketRegistry();
		return instance;
	}
}
