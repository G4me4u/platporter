package com.g4mesoft.net.packet;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;

public class HandshakePacket implements IPacket {

	private static final long PACKET_MESSAGE = 0x48656C6C6FL;
	
	private long message;
	
	public HandshakePacket() {
		message = -1;
	}

	@Override
	public void read(PacketByteBuffer buffer) {
		message = buffer.getLong();
	}

	@Override
	public void write(PacketByteBuffer buffer) {
		buffer.putLong(PACKET_MESSAGE);
	}

	@Override
	public void processPacket(NetworkManager mangager) {
		System.out.println(message);
	}
}
