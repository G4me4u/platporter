package com.g4mesoft.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.Queue;

import com.g4mesoft.net.packet.IPacket;
import com.sun.xml.internal.ws.Closeable;

public abstract class NetworkManager implements Closeable {

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	private static final int MAX_BATCH_PACKETS = 1000;
	
	protected final DatagramSocket socket;
	
	private final PacketByteBuffer sendBuffer;
	private final PacketByteBuffer receiveBuffer;
	
	private final Queue<IPacket> packetsToSend;
	
	public NetworkManager(DatagramSocket socket) {
		this.socket = socket;

		sendBuffer = new PacketByteBuffer(DEFAULT_BUFFER_SIZE);
		receiveBuffer = new PacketByteBuffer(DEFAULT_BUFFER_SIZE);
		
		packetsToSend = new LinkedList<IPacket>();
	}

	public DatagramPacket prepareToSendPacket(IPacket packet) {
		sendBuffer.reset();
		packet.write(sendBuffer);
		return new DatagramPacket(sendBuffer.getData(), sendBuffer.getSize());
	}

	protected abstract boolean sendPacket(IPacket packet);
	
	public void sendAllPackets() {
		int numToSend = Math.min(packetsToSend.size(), MAX_BATCH_PACKETS);
		while (numToSend-- > 0)
			sendPacket(packetsToSend.poll());
	}
	
	public void addPacketToSend(IPacket packet) {
		packetsToSend.add(packet);
	}
	
	@Override
	public void close() {
		socket.close();
	}
}
