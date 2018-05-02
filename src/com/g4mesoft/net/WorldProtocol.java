package com.g4mesoft.net;

import java.net.SocketAddress;
import java.util.UUID;

import com.g4mesoft.net.client.ClientNetworkManager;
import com.g4mesoft.platporter.world.PPWorld;
import com.g4mesoft.platporter.world.entity.PPEntity;

public class WorldProtocol extends Protocol {

	private static final byte WORLD_UPDATE = 0x00;
	private static final byte WORLD_EVENT = 0x01;

	private static final byte SINGLE_TILE = 0x00;
	private static final byte TILE_REGION = 0x01;

	private static final byte TILE_INTERACTION = 0x00;
	
	
	private final PacketByteBuffer sendBuffer;
	
	public WorldProtocol(NetworkManager manager) {
		super(manager);
		
		sendBuffer = new PacketByteBuffer();
	}
	
	@Override
	protected void handleData(PacketByteBuffer buffer, UUID senderUUID, SocketAddress senderAddress) {
		if (buffer.remaining() < 1) // action
			return;

		byte worldAction = buffer.getByte();
		switch (worldAction) {
		case WORLD_UPDATE:
			handleWorldUpdate(buffer, senderUUID, senderAddress);
			break;
		case WORLD_EVENT:
			handleWorldEvent(buffer, senderUUID, senderAddress);
			break;
		}
	}
	
	private void handleWorldUpdate(PacketByteBuffer buffer, UUID senderUUID, SocketAddress senderAddress) {
		if (manager.isServer())
			return;
		if (buffer.remaining() < 1) // action
			return;
		
		switch (buffer.getByte()) {
		case SINGLE_TILE:
			if (buffer.remaining() != 13) // int int int byte
				return;
			int xt = buffer.getInt();
			int yt = buffer.getInt();

			manager.platPorter.getWorld().setTileIndex(xt, yt, buffer.getInt());
			manager.platPorter.getWorld().setData(xt, yt, buffer.getByte());
		case TILE_REGION:
			if (buffer.remaining() < 8) // int int
				return;
			
			int numToUpdate = buffer.getInt();
			int indexOffset = buffer.getInt();

			if (buffer.remaining() != numToUpdate * 5) // hole region
				return;
			
			if (indexOffset + numToUpdate > PPWorld.WORLD_WIDTH * PPWorld.WORLD_HEIGHT)
				return;
			
			int[] tiles = new int[numToUpdate];
			byte[] data = new byte[numToUpdate];
			for (int i = 0; i < numToUpdate; i++) {
				tiles[i] = buffer.getInt();
				data[i] = buffer.getByte();
			}
			
			manager.platPorter.getWorld().setTileRegion(tiles, data, indexOffset);
		default:
			// Unspecified action
			return;
		}
	}

	private void handleWorldEvent(PacketByteBuffer buffer, UUID senderUUID, SocketAddress senderAddress) {
		if (manager.isClient())
			return;
		if (buffer.remaining() < 1) // action
			return;
		
		switch (buffer.getByte()) {
		case TILE_INTERACTION:
			if (buffer.remaining() != 8) // int int
				return;
			
			int xt = buffer.getInt();
			int yt = buffer.getInt();
			
			PPWorld world = manager.platPorter.getWorld();
			PPEntity entity = world.getEntity(senderUUID);
			if (entity != null)
				world.interactWithTile(xt, yt, entity);
			
			break;
		default: 
			// Unspecified action
			return;
		}
	}

	public void sendTile(int xt, int yt, int tile, byte data, UUID receiverUUID) {
		if (manager.isClient())
			throw new IllegalStateException("Client cant send world data!");
			
		sendBuffer.reset();

		sendBuffer.putByte(WORLD_UPDATE);
		sendBuffer.putByte(SINGLE_TILE);

		sendBuffer.putInt(xt);
		sendBuffer.putInt(yt);
		sendBuffer.putInt(tile);
		sendBuffer.putByte(data);

		sendData(receiverUUID, sendBuffer);
	}
	
	public void sendWorldTiles(int[] tiles, byte[] data, UUID receiverUUID) {
		if (manager.isClient())
			throw new IllegalStateException("Client cant send world data!");
		if (tiles.length != data.length)
			throw new IllegalArgumentException("Mismatch between tile indices and data array sizes!");
		
		int len = tiles.length;
		while (len != 0) {
			int numToSend = Math.min(len, 2 * PPWorld.LEVEL_SIZE * PPWorld.LEVEL_SIZE);

			sendBuffer.reset();
			
			sendBuffer.putByte(WORLD_UPDATE);
			sendBuffer.putByte(TILE_REGION);
			
			sendBuffer.putInt(numToSend);
			
			int i = len - numToSend;
			sendBuffer.putInt(i);
			for ( ; i < len; i++) {
				sendBuffer.putInt(tiles[i]);
				sendBuffer.putByte(data[i]);
			}
			len -= numToSend;
			
			sendData(receiverUUID, sendBuffer);
		}
	}

	public void sendWorldInteractionEvent(int xt, int yt) {
		if (manager.isServer())
			throw new IllegalStateException("Server cant send world events!");
	
		sendBuffer.reset();
		
		sendBuffer.putByte(WORLD_EVENT);
		sendBuffer.putByte(TILE_INTERACTION);
		
		sendBuffer.putInt(xt);
		sendBuffer.putInt(yt);
		
		UUID serverUUID = ((ClientNetworkManager)manager).getServerUUID();
		sendData(serverUUID, sendBuffer);
	}
}
