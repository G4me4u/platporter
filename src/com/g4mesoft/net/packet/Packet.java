package com.g4mesoft.net.packet;

import java.net.SocketAddress;
import java.util.UUID;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;

public abstract class Packet {

	public SocketAddress address;
	public boolean received;
	
	public UUID senderUUID;
	
	protected Packet() {
		address = null;
		received = false;
		senderUUID = null;
	}
	
	public void setSenderAddress(SocketAddress address) {
		this.address = address;
		received = true;
	}

	public void setSenderUUID(UUID senderUUID) {
		this.senderUUID = senderUUID;
	}
	
	public abstract void read(PacketByteBuffer buffer);
	
	public abstract void write(PacketByteBuffer buffer);
	
	public abstract void processPacket(NetworkManager manager);

	public abstract boolean checkSize(int bytesToRead);
}
