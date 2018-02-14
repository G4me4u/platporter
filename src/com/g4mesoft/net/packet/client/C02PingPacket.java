package com.g4mesoft.net.packet.client;

import java.util.UUID;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;
import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.server.ServerNetworkManager;

public class C02PingPacket extends Packet {
	
	public UUID clientUUID;
	
	public C02PingPacket() {
	}
	
	public C02PingPacket(UUID clientUUID) {
		this.clientUUID = clientUUID;
	}
	
	@Override
	public void read(PacketByteBuffer buffer) {
		clientUUID = buffer.getUUID();
	}

	@Override
	public void write(PacketByteBuffer buffer) {
		buffer.putUUID(clientUUID);
	}

	@Override
	public void processPacket(NetworkManager manager) {
		if (manager.isServer()) {
			((ServerNetworkManager)manager).processPing(this);
		}
	}

	@Override
	public int getByteSize() {
		// uuid
		return 16;
	}

}
