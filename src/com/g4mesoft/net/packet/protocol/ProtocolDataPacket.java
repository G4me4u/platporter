package com.g4mesoft.net.packet.protocol;

import java.util.UUID;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;
import com.g4mesoft.net.Protocol;
import com.g4mesoft.net.packet.Packet;

public class ProtocolDataPacket extends Packet {

	public int protocolId;
	public UUID sessionId;
	public byte[] data;
	
	public ProtocolDataPacket() {
	}
	
	public ProtocolDataPacket(int protocolId, UUID sessionId, byte[] data) {
		this.protocolId = protocolId;
		this.sessionId = sessionId;
		this.data = data;
	}
	
	@Override
	public void read(PacketByteBuffer buffer) {
		protocolId = buffer.getInt();
		sessionId = buffer.getUUID();
		int dataLength = Math.min(buffer.getInt(), buffer.remaining());
		data = buffer.getBytes(new byte[dataLength]);
	}

	@Override
	public void write(PacketByteBuffer buffer) {
		buffer.putInt(protocolId);
		buffer.putUUID(sessionId);
		buffer.putInt(data.length);
		buffer.putBytes(data);
	}

	@Override
	public void processPacket(NetworkManager manager) {
		Protocol protocol = manager.getProtocol(protocolId);
		if (protocol != null)
			protocol.receiveData(senderUUID, sessionId, new PacketByteBuffer(data, 0, data.length, false), address);
	}
	
	public boolean checkSize(int bytesToRead) {
		// int uuid int (data)
		return bytesToRead >= 24;
	}
}
