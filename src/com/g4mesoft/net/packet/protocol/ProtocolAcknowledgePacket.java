package com.g4mesoft.net.packet.protocol;

import java.util.UUID;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.PacketByteBuffer;
import com.g4mesoft.net.Protocol;
import com.g4mesoft.net.packet.Packet;

public class ProtocolAcknowledgePacket extends Packet {
	
	public int protocolId;
	public UUID sessionId;
	public boolean receiver;
	
	public ProtocolAcknowledgePacket() {
	}
	
	public ProtocolAcknowledgePacket(int protocolId, UUID sessionId, boolean receiver) {
		this.protocolId = protocolId;
		this.sessionId = sessionId;
		this.receiver = receiver;
	}

	@Override
	public void read(PacketByteBuffer buffer) {
		protocolId = buffer.getInt();
		sessionId = buffer.getUUID();
		receiver = buffer.getBoolean();
	}

	@Override
	public void write(PacketByteBuffer buffer) {
		buffer.putInt(protocolId);
		buffer.putUUID(sessionId);
		buffer.putBoolean(receiver);
	}

	@Override
	public void processPacket(NetworkManager manager) {
		Protocol protocol = manager.getProtocol(protocolId);
		if (protocol != null)
			protocol.acknowledgePacket(senderUUID, sessionId, receiver, address);
	}

	@Override
	public boolean checkSize(int bytesToRead) {
		// int uuid boolean
		return bytesToRead == 21;
	}
}
