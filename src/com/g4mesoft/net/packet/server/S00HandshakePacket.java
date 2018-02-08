package com.g4mesoft.net.packet.server;

import java.util.UUID;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;
import com.g4mesoft.net.client.ClientNetworkManager;
import com.g4mesoft.net.packet.Packet;

public class S00HandshakePacket extends Packet {

	public long sequence;
	public long acknowledgement;
	public UUID clientUUID;
	
	public S00HandshakePacket() {
	}

	public S00HandshakePacket(long sequence, long acknowledgement, UUID clientUUID) {
		this.sequence = sequence;
		this.acknowledgement = acknowledgement;
		this.clientUUID = clientUUID;
	}

	@Override
	public void read(PacketByteBuffer buffer) {
		sequence = buffer.getLong();
		acknowledgement = buffer.getLong();
		clientUUID = buffer.getUUID();
	}

	@Override
	public void write(PacketByteBuffer buffer) {
		buffer.putLong(sequence);
		buffer.putLong(acknowledgement);
		buffer.putUUID(clientUUID);
	}

	@Override
	public void processPacket(NetworkManager manager) {
		if (manager.isClient())
			((ClientNetworkManager)manager).makeHandshake(this);
	}
	
	@Override
	public int getByteSize() {
		// long long UUID
		return 32;
	}
}
