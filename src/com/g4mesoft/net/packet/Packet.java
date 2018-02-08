package com.g4mesoft.net.packet;

import java.net.SocketAddress;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;

public abstract class Packet {

	public SocketAddress address;
	public boolean received;
	
	protected Packet() {
		address = null;
		received = false;
	}
	
	public void setReceiveAddress(SocketAddress address) {
		this.address = address;
		received = true;
	}
	
	public abstract void read(PacketByteBuffer buffer);
	
	public abstract void write(PacketByteBuffer buffer);
	
	public abstract void processPacket(NetworkManager manager);
	
	public abstract int getByteSize();
}
