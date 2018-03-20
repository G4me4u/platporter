package com.g4mesoft.net;

import java.net.SocketAddress;
import java.util.UUID;

import com.g4mesoft.net.client.ClientNetworkManager;
import com.g4mesoft.net.server.ClientConnection;
import com.g4mesoft.net.server.ServerNetworkManager;

public class HandshakeProtocol extends Protocol {

	private final static byte SERVER_ID = 0x11;
	private final static byte CLIENT_ID = 0x22;
	private final PacketByteBuffer sendBuffer;
	
	public HandshakeProtocol(NetworkManager manager) {
		super(manager);
		
		sendBuffer = new PacketByteBuffer();
	}
	
	@Override
	protected void handleData(PacketByteBuffer buffer, UUID senderUUID, SocketAddress senderAddress) {
		if (buffer.remaining() < 1) // byte
			return;
		
		byte id = buffer.getByte();
		if (id == CLIENT_ID) {
			if (manager.isClient())
				return;
			
			ServerNetworkManager server = (ServerNetworkManager)manager;
			ClientConnection client = server.addClient(senderAddress);
			if (client == null)
				return;
			
			sendBuffer.reset();
			sendBuffer.putByte(SERVER_ID);
			sendBuffer.putUUID(client.getClientUUID());
			sendData(client.getClientUUID(), sendBuffer);
		} else if (id == SERVER_ID) {
			if (manager.isServer())
				return;

			ClientNetworkManager client = (ClientNetworkManager)manager;

			if (buffer.remaining() == 16) // uuid
				client.connectionFinished(buffer.getUUID(), senderUUID);
		}
	}
	
	public void handshakeServer() {
		if (manager.isServer())
			return;
		
		ClientNetworkManager client = (ClientNetworkManager)manager;
		
		sendBuffer.reset();
		sendBuffer.putByte(CLIENT_ID);
		sendData(client.getServerUUID(), sendBuffer);
	}
}
