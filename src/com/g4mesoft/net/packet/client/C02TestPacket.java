package com.g4mesoft.net.packet.client;

import java.util.UUID;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;
import com.g4mesoft.net.client.ClientNetworkManager;
import com.g4mesoft.net.packet.Packet;

public class C02TestPacket extends Packet {

	private float angle;
	private UUID clientUUID;
	
	public C02TestPacket() {
	}

	public C02TestPacket(float angle, UUID clientUUID) {
		this.angle = angle;
		this.clientUUID = clientUUID;
	}
	
	@Override
	public void read(PacketByteBuffer buffer) {
		angle = buffer.getFloat();
		clientUUID = buffer.getUUID();
	}

	@Override
	public void write(PacketByteBuffer buffer) {
		buffer.putFloat(angle);
		buffer.putUUID(clientUUID);
	}

	@Override
	public void processPacket(NetworkManager manager) {
		if (manager.isServer()) {
			manager.addPacketToSend(this, clientUUID);
		} else {
			((ClientNetworkManager)manager).setAngle(angle);
		}
	}

	@Override
	public int getByteSize() {
		return 20;
	}

}
