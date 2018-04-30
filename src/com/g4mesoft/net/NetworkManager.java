package com.g4mesoft.net;

import java.io.Closeable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.platporter.PlatPorter;

public abstract class NetworkManager implements Closeable {

	public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	public static final int MAX_BATCH_PACKETS = 1000;
	
	public static final UUID NO_UUID = new UUID(0L, 0L);
	
	protected final DatagramSocket socket;
	public final NetworkSide side;
	protected final PlatPorter platPorter;
	protected UUID connectionUUID;
	
	protected final ReceiveThread receiveThread;
	
	private final PacketByteBuffer sendBuffer;
	private final PacketByteBuffer receiveBuffer;
	
	private final PacketLinkedList[] packetsToSend;
	private final PacketLinkedList[] packetsToProcess;
	private int packetsToSendIndex;
	private int packetsToProcessIndex;
	
	private final Map<Integer, Protocol> idToProtocol;
	
	protected long uptime;
	
	public NetworkManager(DatagramSocket socket, NetworkSide side, PlatPorter platPorter, UUID connectionUUID) {
		this.socket = socket;
		this.side = side;
		this.platPorter = platPorter;
		this.connectionUUID = connectionUUID == null ? NO_UUID : connectionUUID;
		
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
		
		idToProtocol = new HashMap<Integer, Protocol>();
	}

	public DatagramPacket prepareToSendPacket(Packet packet) {
		sendBuffer.reset();
		sendBuffer.putInt(PacketRegistry.getInstance().getId(packet.getClass()));
		sendBuffer.putUUID(connectionUUID);
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
		PacketLinkedList packets;
		synchronized (packetsToProcess) {
			int oldPacketsToProcessIndex = packetsToProcessIndex;
			packetsToProcessIndex ^= 1;
			packets = packetsToProcess[oldPacketsToProcessIndex];
		}

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

		if (receiveBuffer.getSize() < 12) // int uuid
			return;
		
		int packetId = receiveBuffer.getInt();
		UUID senderUUID = receiveBuffer.getUUID();
		
		Class<? extends Packet> packetClazz = PacketRegistry.getInstance().getClass(packetId);

		if (packetClazz == null)
			return;

		Packet packet;
		try {
			packet = packetClazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return;
		}

		if (!packet.checkSize(receiveBuffer.remaining()))
			return;
		
		packet.setSenderAddress(datagramPacket.getSocketAddress());
		packet.setSenderUUID(senderUUID);
		packet.read(receiveBuffer);
		
		if (confirmPacket(packet)) {
			synchronized (packetsToProcess) {
				packetsToProcess[packetsToProcessIndex].add(new PacketEntry(packet, null));
			}
		}
	}
	
	protected boolean confirmPacket(Packet packet) {
		return true;
	}
	
	public Protocol getProtocol(Class<? extends Protocol> clazz) {
		return getProtocol(ProtocolRegistry.getInstance().getId(clazz));
	}
	
	public Protocol getProtocol(int protocolId) {
		Protocol protocol = idToProtocol.get(protocolId);
		if (protocol == null) {
			Class<? extends Protocol> protocolClazz = ProtocolRegistry.getInstance().getClass(protocolId);

			if (protocolClazz == null)
				return null;
			
			try {
				protocol = protocolClazz.getDeclaredConstructor(NetworkManager.class).newInstance(this);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			
			idToProtocol.put(protocolId, protocol);
		}
		return protocol;
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
	
	public UUID getConnectionUUID() {
		return connectionUUID;
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
