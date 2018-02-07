package com.g4mesoft.net.packet;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;

public interface IPacket {

	public void read(PacketByteBuffer buffer);
	
	public void write(PacketByteBuffer buffer);
	
	public void processPacket(NetworkManager mangager);
}
