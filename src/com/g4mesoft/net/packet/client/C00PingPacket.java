package com.g4mesoft.net.packet.client;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;
import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.server.ServerNetworkManager;

public class C00PingPacket extends Packet {
	
	public C00PingPacket() {
	}
	
	@Override
	public void read(PacketByteBuffer buffer) {
	}

	@Override
	public void write(PacketByteBuffer buffer) {
	}

	@Override
	public void processPacket(NetworkManager manager) {
		if (manager.isServer())
			((ServerNetworkManager)manager).processPing(this);
	}

	@Override
	public boolean checkSize(int bytesToRead) {
		// none
		return bytesToRead == 0;
	}

}
