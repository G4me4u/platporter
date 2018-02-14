package com.g4mesoft.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.LinkedList;

import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.platporter.PlatPorter;
import com.sun.xml.internal.ws.Closeable;

public abstract class NetworkManager implements Closeable {

	public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	public static final int MAX_BATCH_PACKETS = 1000;
	
	protected final DatagramSocket socket;
	public final NetworkSide side;
	protected final PlatPorter platPorter;
	
	protected final ReceiveThread receiveThread;
	
	private final PacketByteBuffer sendBuffer;
	private final PacketByteBuffer receiveBuffer;
	
	private final PacketLinkedList[] packetsToSend;
	private final PacketLinkedList[] packetsToProcess;
	private int packetsToSendIndex;
	private int packetsToProcessIndex;
	
	protected long uptime;
	
	public NetworkManager(DatagramSocket socket, NetworkSide side, PlatPorter platPorter) {
		this.socket = socket;
		this.side = side;
		this.platPorter = platPorter;
		
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

	public DatagramPacket prepareToSendPacket(Packet packet) {
		sendBuffer.reset();
		sendBuffer.putInt(PacketRegistry.getId(packet.getClass()));
		packet.write(sendBuffer);
		return new DatagramPacket(sendBuffer.getData(), sendBuffer.getSize());
	}

	protected abstract boolean sendPacket(Packet packet, Object identifier);
	
	protected void sendAllPackets() {
		PacketLinkedList packets = packetsToSend[packetsToSendIndex];
		packetsToSendIndex ^= 1;

		int numToSend = Math.min(packets.size(), MAX_BATCH_PACKETS);
		while (numToSend-- > 0) {
			PacketEntry packetEntry = packets.poll();
			sendPacket(packetEntry.packet, packetEntry.identifier);
		}
	}
	
	protected void processAllPackets() {
		PacketLinkedList packets = packetsToProcess[packetsToProcessIndex];
		packetsToProcessIndex ^= 1;

		while (packets.size() > 0)
			packets.poll().packet.processPacket(this);
	}

	public void update() {
		uptime++;
		
		processAllPackets();
		sendAllPackets();
	}
	
	public void addPacketToSend(Packet packet) {
		addPacketToSend(packet, null);
	}

	public void addPacketToSend(Packet packet, Object identifier) {
		packetsToSend[packetsToSendIndex].add(new PacketEntry(packet, identifier));
	}
	
	public void receiveDatagramPacket(DatagramPacket datagramPacket) {
		receiveBuffer.reset();
		receiveBuffer.putBytes(datagramPacket.getData(), 
		                       datagramPacket.getOffset(),
		                       datagramPacket.getLength());
		receiveBuffer.resetPos();

		if (receiveBuffer.getSize() < 4)
			return;
		
		int packetId = receiveBuffer.getInt();
		Class<? extends Packet> packetClazz = PacketRegistry.getPacketClass(packetId);

		Packet packet;
		try {
			packet = packetClazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return;
		}
		
		if (receiveBuffer.getSize() - 4 != packet.getByteSize())
			return;
		
		packet.setReceiveAddress(datagramPacket.getSocketAddress());
		packet.read(receiveBuffer);
		
		if (confirmPacket(packet))
			packetsToProcess[packetsToProcessIndex].add(new PacketEntry(packet, null));
	}
	
	protected boolean confirmPacket(Packet packet) {
		return true;
	}
	
	public boolean isServer() {
		return side == NetworkSide.SERVER;
	}
	
	public boolean isClient() {
		return side == NetworkSide.CLIENT;
	}
	
	public SocketAddress getAddress() {
		return socket.getLocalSocketAddress();
	}
	
	public long getUpTime() {
		return uptime;
	}
	
	@Override
	public void close() {
		receiveThread.stopListening();
		socket.close();
	}
	
	@SuppressWarnings("serial")
	private class PacketLinkedList extends LinkedList<PacketEntry> {
	}
	
	private class PacketEntry {
		
		public final Packet packet;
		public final Object identifier;
		
		public PacketEntry(Packet packet, Object identifier) {
			this.packet = packet;
			this.identifier = identifier;
		}
	}
}
