package com.g4mesoft.net.packet.server;

import java.util.UUID;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;
import com.g4mesoft.net.client.ClientNetworkManager;
import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.world.entity.EntityFacing;

public class S01PositionPacket extends Packet {

	public UUID entityUUID;
	public float x;
	public float y;
	public EntityFacing facing;
	
	public S01PositionPacket() {
	}
	
	public S01PositionPacket(UUID entityUUID, float x, float y, EntityFacing facing) {
		this.entityUUID = entityUUID;
		this.x = x;
		this.y = y;
		this.facing = facing;
	}

	@Override
	public void read(PacketByteBuffer buffer) {
		entityUUID = buffer.getUUID();
		x = buffer.getFloat();
		y = buffer.getFloat();
		facing = EntityFacing.fromIndex(buffer.getInt());
	}

	@Override
	public void write(PacketByteBuffer buffer) {
		buffer.putUUID(entityUUID);
		buffer.putFloat(x);
		buffer.putFloat(y);
		buffer.putInt(facing == null ? -1 : facing.getIndex());
	}

	@Override
	public void processPacket(NetworkManager manager) {
		if (manager.isClient())
			((ClientNetworkManager)manager).handlePositionPacket(this);
	}

	@Override
	public boolean checkSize(int bytesToRead) {
		// uuid float float int
		return bytesToRead == 28;
	}
}
