package com.g4mesoft.net;

import java.net.SocketAddress;
import java.util.UUID;

import com.g4mesoft.net.client.ClientNetworkManager;

public class EntityProtocol extends Protocol {

	private final static byte REMOVE_ENTITY_ACTION = 0x00;
	private final static byte ADD_ENTITY_ACTION = 0x01;
	
	private final PacketByteBuffer sendBuffer;
	
	public EntityProtocol(NetworkManager manager) {
		super(manager);
		
		sendBuffer = new PacketByteBuffer();
	}
	
	@Override
	protected void handleData(PacketByteBuffer buffer, UUID senderUUID, SocketAddress senderAddress) {
		if (manager.isServer())
			return;

		if (buffer.remaining() != 17) // byte uuid
			return;
		byte action = buffer.getByte();
		UUID playerUUID = buffer.getUUID();
		
		ClientNetworkManager client = (ClientNetworkManager)manager;
		switch (action) {
		case ADD_ENTITY_ACTION:
			client.addNetworkEntity(playerUUID);
			break;
		case REMOVE_ENTITY_ACTION:
			client.removeNetworkEntity(playerUUID);
			break;
		}
	}
	
	public void addEntity(UUID receiverUUID, UUID playerUUID) {
		handleEntity(receiverUUID, playerUUID, ADD_ENTITY_ACTION);
	}

	public void removeEntity(UUID receiverUUID, UUID playerUUID) {
		handleEntity(receiverUUID, playerUUID, REMOVE_ENTITY_ACTION);
	}
	
	private void handleEntity(UUID receiverUUID, UUID playerUUID, byte action) {
		if (manager.isClient())
			return;
		
		sendBuffer.reset();
		sendBuffer.putByte(action);
		sendBuffer.putUUID(playerUUID);
		sendData(receiverUUID, sendBuffer);
	}
}
