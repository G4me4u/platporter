package com.g4mesoft.net.packet.server;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;
import com.g4mesoft.net.client.ClientNetworkManager;
import com.g4mesoft.net.packet.Packet;

public class S01PongPacket extends Packet {

	public long pingInterval;
	
	public S01PongPacket() {
	}

	public S01PongPacket(long pingInterval) {
		this.pingInterval = pingInterval;
	}
	
	@Override
	public void read(PacketByteBuffer buffer) {
		pingInterval = buffer.getLong();
	}

	@Override
	public void write(PacketByteBuffer buffer) {
		buffer.putLong(pingInterval);
	}

	@Override
	public void processPacket(NetworkManager manager) {
		if (manager.isClient())
			((ClientNetworkManager)manager).processPong(this);
	}

	@Override
	public int getByteSize() {
		// long
		return 8;
	}

}
