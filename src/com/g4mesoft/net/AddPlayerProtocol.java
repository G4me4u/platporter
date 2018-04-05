package com.g4mesoft.net;

import java.net.SocketAddress;
import java.util.UUID;

import com.g4mesoft.net.client.ClientNetworkManager;

public class AddPlayerProtocol extends Protocol {

	private final static byte REMOVE_PLAYER_ACTION = 0x00;
	private final static byte ADD_PLAYER_ACTION = 0x01;
	
	private final PacketByteBuffer sendBuffer;
	
	public AddPlayerProtocol(NetworkManager manager) {
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
		case ADD_PLAYER_ACTION:
			client.addNetworkPlayer(playerUUID);
			break;
		case REMOVE_PLAYER_ACTION:
			client.removeNetworkPlayer(playerUUID);
			break;
		}
	}
	
	public void addPlayer(UUID receiverUUID, UUID playerUUID) {
		handlePlayer(receiverUUID, playerUUID, false);
	}

	public void removePlayer(UUID receiverUUID, UUID playerUUID) {
		handlePlayer(receiverUUID, playerUUID, true);
	}
	
	private void handlePlayer(UUID receiverUUID, UUID playerUUID, boolean remove) {
		if (manager.isClient())
			return;
		
		sendBuffer.reset();
		sendBuffer.putByte(remove ? REMOVE_PLAYER_ACTION : ADD_PLAYER_ACTION);
		sendBuffer.putUUID(playerUUID);
		sendData(receiverUUID, sendBuffer);
	}
}
