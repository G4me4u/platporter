package com.g4mesoft.net;

import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.packet.client.C00PingPacket;
import com.g4mesoft.net.packet.client.C01PositionPacket;
import com.g4mesoft.net.packet.protocol.ProtocolAcknowledgePacket;
import com.g4mesoft.net.packet.protocol.ProtocolDataPacket;
import com.g4mesoft.net.packet.server.S00PongPacket;
import com.g4mesoft.net.packet.server.S01PositionPacket;
import com.g4mesoft.util.Registry;

public final class PacketRegistry extends Registry<Packet> {

	private static PacketRegistry instance;

	private PacketRegistry() {
		// Protocol
		addEntry(ProtocolDataPacket.class);
		addEntry(ProtocolAcknowledgePacket.class);
		
		// Ping pong
		addEntry(C00PingPacket.class);
		
		addEntry(S00PongPacket.class);
		
		// Gameplay
		addEntry(C01PositionPacket.class);
		addEntry(S01PositionPacket.class);
	}
	
	public static PacketRegistry getInstance() {
		if (instance == null)
			instance = new PacketRegistry();
		return instance;
	}
}
