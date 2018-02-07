package com.g4mesoft.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.g4mesoft.net.packet.HandshakePacket;
import com.g4mesoft.net.packet.IPacket;

public final class PacketRegistry {

	private static final Map<Class<? extends IPacket>, Integer> packetClassToId;
	private static final List<Class<? extends IPacket>> packetClasses;

	private PacketRegistry() {
	}
	
	private static void addPacketEntry(Class<? extends IPacket> clazz) {
		int id = packetClasses.size();
		packetClasses.add(clazz);
		packetClassToId.put(clazz, Integer.valueOf(id));
	}
	
	public static Class<? extends IPacket> getPacketClass(int id) {
		if (packetClasses.size() <= id)
			throw new IllegalArgumentException("Id doesn't exist in registry!");
		return packetClasses.get(id);
	}
	
	public static int getId(Class<? extends IPacket> clazz) {
		Integer id = packetClassToId.get(clazz);
		if (id == null)
			throw new IllegalArgumentException("Packet does not exist in registry!");
		return id.intValue();
	}
	
	static {
		packetClassToId = new HashMap<Class<? extends IPacket>, Integer>();
		packetClasses = new ArrayList<Class<? extends IPacket>>();
		
		addPacketEntry(HandshakePacket.class);
	}
}
