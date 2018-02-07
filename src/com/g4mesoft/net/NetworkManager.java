package com.g4mesoft.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;

import com.g4mesoft.net.packet.IPacket;
import com.sun.xml.internal.ws.Closeable;

public abstract class NetworkManager implements Closeable {

	public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	public static final int MAX_BATCH_PACKETS = 1000;
	
	protected final DatagramSocket socket;
	
	protected final ReceiveThread receiveThread;
	
	private final PacketByteBuffer sendBuffer;
	private final PacketByteBuffer receiveBuffer;
	
	private final PacketLinkedList[] packetsToSend;
	private final PacketLinkedList[] packetsToProcess;
	private int packetsToSendIndex;
	private int packetsToProcessIndex;
	
	public NetworkManager(DatagramSocket socket) {
		this.socket = socket;
		
		receiveThread = new ReceiveThread(this);

		sendBuffer = new PacketByteBuffer(DEFAULT_BUFFER_SIZE);
		receiveBuffer = new PacketByteBuffer(DEFAULT_BUFFER_SIZE);
		
		packetsToSend = new PacketLinkedList[2];
		packetsToSend[0] = new PacketLinkedList();
		packetsToSend[1] = new PacketLinkedList();
		
		packetsToProcess = new PacketLinkedList[2];
		packetsToProcess[0] = new PacketLinkedList();
		packetsToProcess[1] = new PacketLinkedList();
		
		receiveThread.startListening();
	}

	public DatagramPacket prepareToSendPacket(IPacket packet) {
		sendBuffer.reset();
		sendBuffer.putInt(PacketRegistry.getId(packet.getClass()));
		packet.write(sendBuffer);
		return new DatagramPacket(sendBuffer.getData(), sendBuffer.getSize());
	}

	protected abstract boolean sendPacket(IPacket packet);
	
	protected void sendAllPackets() {
		PacketLinkedList packetsToSend = this.packetsToSend[packetsToSendIndex];
		packetsToSendIndex ^= 1;
		
		int numToSend = Math.min(packetsToSend.size(), MAX_BATCH_PACKETS);
		while (numToSend-- > 0)
			sendPacket(packetsToSend.poll());
	}
	
	protected void processAllPackets() {
		PacketLinkedList packetsToProcess = this.packetsToProcess[packetsToProcessIndex];
		packetsToProcessIndex ^= 1;

		while (packetsToProcess.size() > 0)
			packetsToProcess.poll().processPacket(this);
	}

	public void update() {
		sendAllPackets();
		processAllPackets();
	}
	
	public void addPacketToSend(IPacket packet) {
		packetsToSend[packetsToSendIndex].add(packet);
	}
	
	public void receiveDatagramPacket(DatagramPacket datagramPacket) {
		receiveBuffer.reset();
		receiveBuffer.putBytes(datagramPacket.getData(), 
		                       datagramPacket.getOffset(),
		                       datagramPacket.getLength());
		receiveBuffer.resetPos();

		int packetId = receiveBuffer.getInt();
		Class<? extends IPacket> packetClazz = PacketRegistry.getPacketClass(packetId);
		
		IPacket packet;
		try {
			packet = packetClazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return;
		}
		
		packet.read(receiveBuffer);
		packetsToProcess[packetsToProcessIndex].add(packet);
	}
	
	@Override
	public void close() {
		receiveThread.stopListening();
		socket.close();
	}
	
	@SuppressWarnings("serial")
	private class PacketLinkedList extends LinkedList<IPacket> {
	}
}
