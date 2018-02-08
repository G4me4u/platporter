package com.g4mesoft.net.packet.client;

import java.util.UUID;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;
import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.server.ServerNetworkManager;

public class C01AcknowledgePacket extends Packet {

	public long acknowledge;
	public UUID clientUUID;
	
	public C01AcknowledgePacket() {
	}
	
	public C01AcknowledgePacket(long acknowledge, UUID clientUUID) {
		this.acknowledge = acknowledge;
		this.clientUUID = clientUUID;
	}

	@Override
	public void read(PacketByteBuffer buffer) {
		acknowledge = buffer.getLong();
		clientUUID = buffer.getUUID();
	}

	@Override
	public void write(PacketByteBuffer buffer) {
		buffer.putLong(acknowledge);
		buffer.putUUID(clientUUID);
	}

	@Override
	public void processPacket(NetworkManager manager) {
		if (manager.isServer())
			((ServerNetworkManager)manager).handleAcknowledgement(this);
	}

	@Override
	public int getByteSize() {
		return 24;
	}
}
