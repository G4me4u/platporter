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
	// private static final byte MULTIPLE_TILES = 0x01;
	private static final byte ALL_TILES = 0x02;

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
		case ALL_TILES:
			if (buffer.remaining() < 8) // int int
				return;
			
			int numTiles = buffer.getInt();
			int numData = buffer.getInt();

			if (buffer.remaining() != numTiles * 4 + numData)
				return;
			
			if (numTiles != numData)
				return;
			if (numTiles != PPWorld.WORLD_WIDTH * PPWorld.WORLD_HEIGHT)
				return;
			
			int[] tiles = new int[numTiles];
			for (int i = 0; i < numTiles; i++)
				tiles[i] = buffer.getInt();
			byte[] data = new byte[numData];
			for (int i = 0; i < numTiles; i++)
				data[i] = buffer.getByte();
			
			manager.platPorter.getWorld().setAllTiles(tiles, data);
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

		sendBuffer.reset();
		
		sendBuffer.putByte(WORLD_UPDATE);
		sendBuffer.putByte(ALL_TILES);
		
		sendBuffer.putInt(tiles.length);
		sendBuffer.putInt(data.length);
		for (int tile : tiles)
			sendBuffer.putInt(tile);
		for (byte dat : data)
			sendBuffer.putByte(dat);
		
		sendData(receiverUUID, sendBuffer);
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
