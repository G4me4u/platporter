package com.g4mesoft.net;

import java.net.SocketAddress;
import java.util.UUID;

import com.g4mesoft.net.client.ClientNetworkManager;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.platporter.world.entity.PPEntity;
import com.g4mesoft.platporter.world.entity.player.NetworkPlayerEntity;
import com.g4mesoft.world.entity.EntityFacing;

public class EntityProtocol extends Protocol {

	private final static byte REMOVE_ENTITY_ACTION = 0x00;
	private final static byte ADD_ENTITY_ACTION = 0x01;
	private final static byte SET_ENTITY_POSITION = 0x02;
	
	private final PacketByteBuffer sendBuffer;
	
	public EntityProtocol(NetworkManager manager) {
		super(manager);
		
		sendBuffer = new PacketByteBuffer();
	}
	
	@Override
	protected void handleData(PacketByteBuffer buffer, UUID senderUUID, SocketAddress senderAddress) {
		if (manager.isServer())
			return;
		
		if (buffer.remaining() < 17) // byte uuid
			return;
		byte action = buffer.getByte();
		UUID entityUUID = buffer.getUUID();
		
		ClientNetworkManager client = (ClientNetworkManager)manager;
		PPWorld world = client.platPorter.getWorld();
		
		switch (action) {
		case ADD_ENTITY_ACTION:
			if (buffer.remaining() != 12) // float float int
				return;
			
			float x = buffer.getFloat();
			float y = buffer.getFloat();
			EntityFacing facing = EntityFacing.fromIndex(buffer.getInt());
			if (facing == null)
				facing = EntityFacing.RIGHT;
			
			PPEntity entity = new NetworkPlayerEntity(world, entityUUID);
			entity.setPosition(x, y, facing);
			world.addEntity(entity);
			break;
		case REMOVE_ENTITY_ACTION:
			world.removeEntity(entityUUID);
			break;
		case SET_ENTITY_POSITION:
			if (buffer.remaining() != 12) // float float int
				return;
			
			entity = world.getEntity(entityUUID);
			if (entity != null) {
				x = buffer.getFloat();
				y = buffer.getFloat();
				facing = EntityFacing.fromIndex(buffer.getInt());
				
				if (facing != null)
					entity.setPosition(x, y, facing);
			}
			break;
		}
	}
	
	public void addEntity(UUID receiverUUID, UUID entityUUID, float x, float y, EntityFacing facing) {
		if (manager.isClient())
			return;
		
		sendBuffer.reset();
		sendBuffer.putByte(ADD_ENTITY_ACTION);
		sendBuffer.putUUID(entityUUID);
		sendBuffer.putFloat(x);
		sendBuffer.putFloat(y);
		sendBuffer.putInt(facing.getIndex());
		sendData(receiverUUID, sendBuffer);
	}

	public void removeEntity(UUID receiverUUID, UUID entityUUID) {
		if (manager.isClient())
			return;
		
		sendBuffer.reset();
		sendBuffer.putByte(REMOVE_ENTITY_ACTION);
		sendBuffer.putUUID(entityUUID);
		sendData(receiverUUID, sendBuffer);
	}
	
	public void setEntityPosition(UUID receiverUUID, UUID entityUUID, float x, float y, EntityFacing facing) {
		if (manager.isClient())
			return;
		
		sendBuffer.reset();
		sendBuffer.putByte(SET_ENTITY_POSITION);
		sendBuffer.putUUID(entityUUID);
		sendBuffer.putFloat(x);
		sendBuffer.putFloat(y);
		sendBuffer.putInt(facing.getIndex());
		sendData(receiverUUID, sendBuffer);
	}
}
