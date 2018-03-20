package com.g4mesoft.net.packet.server;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;
import com.g4mesoft.net.client.ClientNetworkManager;
import com.g4mesoft.net.packet.Packet;

public class S00PongPacket extends Packet {

	public S00PongPacket() {
	}

	@Override
	public void read(PacketByteBuffer buffer) {
	}

	@Override
	public void write(PacketByteBuffer buffer) {
	}

	@Override
	public void processPacket(NetworkManager manager) {
		if (manager.isClient())
			((ClientNetworkManager)manager).processPong(this);
	}

	@Override
	public boolean checkSize(int bytesToRead) {
		// none
		return bytesToRead == 0;
	}

}
