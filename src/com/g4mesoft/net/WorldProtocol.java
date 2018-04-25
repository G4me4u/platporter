package com.g4mesoft.net;

import java.net.SocketAddress;
import java.util.UUID;

import com.g4mesoft.platporter.world.PPWorld;

public class WorldProtocol extends Protocol {

	private static final byte SINGLE_TILE = 0x00;
	// private static final byte MULTIPLE_TILES = 0x01;
	private static final byte ALL_TILES = 0x02;
	
	private final PacketByteBuffer sendBuffer;
	
	public WorldProtocol(NetworkManager manager) {
		super(manager);
		
		sendBuffer = new PacketByteBuffer();
	}
	
	@Override
	protected void handleData(PacketByteBuffer buffer, UUID senderUUID, SocketAddress senderAddress) {
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

	public void sendTile(int xt, int yt, int tile, byte data, UUID receiverUUID) {
		sendBuffer.reset();

		sendBuffer.putByte(SINGLE_TILE);

		sendBuffer.putInt(xt);
		sendBuffer.putInt(yt);
		sendBuffer.putInt(tile);
		sendBuffer.putByte(data);

		sendData(receiverUUID, sendBuffer);
	}
	
	public void sendWorldTiles(int[] tiles, byte[] data, UUID receiverUUID) {
		sendBuffer.reset();
		
		sendBuffer.putByte(ALL_TILES);
		
		sendBuffer.putInt(tiles.length);
		sendBuffer.putInt(data.length);
		for (int tile : tiles)
			sendBuffer.putInt(tile);
		for (byte dat : data)
			sendBuffer.putByte(dat);
		
		sendData(receiverUUID, sendBuffer);
	}
}
