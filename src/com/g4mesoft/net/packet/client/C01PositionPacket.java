package com.g4mesoft.net.packet.client;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;
import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.server.ServerNetworkManager;
import com.g4mesoft.world.entity.EntityFacing;

public class C01PositionPacket extends Packet {
	
	public float x;
	public float y;
	public EntityFacing facing;
	
	public C01PositionPacket() {
	}
	
	@Override
	public void read(PacketByteBuffer buffer) {
		x = buffer.getFloat();
		y = buffer.getFloat();
		facing = EntityFacing.fromIndex(buffer.getInt());
	}

	@Override
	public void write(PacketByteBuffer buffer) {
		buffer.putFloat(x);
		buffer.putFloat(y);
		buffer.putInt(facing.getIndex());
	}

	@Override
	public void processPacket(NetworkManager manager) {
		if (manager.isServer())
			((ServerNetworkManager)manager).handlePositionPacket(this);
	}

	@Override
	public boolean checkSize(int bytesToRead) {
		// float float int
		return bytesToRead == 12;
	}

}
