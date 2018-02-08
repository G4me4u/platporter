package com.g4mesoft.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.packet.client.C00HandshakePacket;
import com.g4mesoft.net.packet.client.C01AcknowledgePacket;
import com.g4mesoft.net.packet.client.C02TestPacket;
import com.g4mesoft.net.packet.server.S00HandshakePacket;

public final class PacketRegistry {

	private static final Map<Class<? extends Packet>, Integer> packetClassToId;
	private static final List<Class<? extends Packet>> packetClasses;

	private PacketRegistry() {
	}
	
	private static void addPacketEntry(Class<? extends Packet> clazz) {
		int id = packetClasses.size();
		packetClasses.add(clazz);
		packetClassToId.put(clazz, Integer.valueOf(id));
	}
	
	public static Class<? extends Packet> getPacketClass(int id) {
		if (packetClasses.size() <= id)
			throw new IllegalArgumentException("Id doesn't exist in registry!");
		return packetClasses.get(id);
	}
	
	public static int getId(Class<? extends Packet> clazz) {
		Integer id = packetClassToId.get(clazz);
		if (id == null)
			throw new IllegalArgumentException("Packet does not exist in registry!");
		return id.intValue();
	}
	
	static {
		packetClassToId = new HashMap<Class<? extends Packet>, Integer>();
		packetClasses = new ArrayList<Class<? extends Packet>>();
	
		// Handshake
		addPacketEntry(C00HandshakePacket.class);
		addPacketEntry(C01AcknowledgePacket.class);
		addPacketEntry(C02TestPacket.class);

		addPacketEntry(S00HandshakePacket.class);
	}
}
