package com.g4mesoft.net.packet.client;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;
import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.server.ServerNetworkManager;

public class C00HandshakePacket extends Packet {

	public long sequence;
	
	public C00HandshakePacket() {
	}

	public C00HandshakePacket(long sequence) {
		this.sequence = sequence;
	}

	@Override
	public void read(PacketByteBuffer buffer) {
		sequence = buffer.getLong();
	}

	@Override
	public void write(PacketByteBuffer buffer) {
		buffer.putLong(sequence);
	}

	@Override
	public void processPacket(NetworkManager manager) {
		if (manager.isServer())
			((ServerNetworkManager)manager).makeHandshake(this);
	}
	
	@Override
	public int getByteSize() {
		// long
		return 8;
	}
}
