package com.g4mesoft.net;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import com.g4mesoft.net.client.ClientNetworkManager;
import com.g4mesoft.net.packet.Packet;
import com.g4mesoft.net.packet.protocol.ProtocolAcknowledgePacket;
import com.g4mesoft.net.packet.protocol.ProtocolDataPacket;
import com.g4mesoft.net.server.ClientConnection;
import com.g4mesoft.net.server.ServerNetworkManager;
import com.g4mesoft.util.IScheduledTask;

public abstract class Protocol {

	private static final int RESEND_INTERVAL = 4;
	private static final int RECEIVED_SESSION_DECAY = 60 * 1000;
	
	protected final NetworkManager manager;

	protected final Map<UUID, PacketSession> packetSessions;
	
	protected final ReceivedSessionSet[] receivedSessions;
	protected int currentSessionSet;
	protected long lastSessionSetSwitch;
	
	public Protocol(NetworkManager manager) {
		this.manager = manager;
		
		packetSessions = new HashMap<UUID, PacketSession>();
		receivedSessions = new ReceivedSessionSet[2];
		receivedSessions[0] = new ReceivedSessionSet();
		receivedSessions[1] = new ReceivedSessionSet();
	}
	
	public UUID ensureValidUUID(UUID senderUUID, SocketAddress senderAddress) {
		if (NetworkManager.NO_UUID.equals(senderUUID) && manager.isServer()) {
			ServerNetworkManager server = (ServerNetworkManager)manager;
			ClientConnection client = server.getClient(senderAddress);
			
			if (client != null) 
				senderUUID = client.getClientUUID();
		}
		return senderUUID;
	}
	
	public void acknowledgePacket(UUID senderUUID, UUID sessionId, boolean senderIsReceiver, SocketAddress senderAddress) {
		PacketSession session = packetSessions.remove(sessionId);
		if (session != null)
			session.cancelSession();
		
		if (senderIsReceiver) {
			ProtocolAcknowledgePacket packet = new ProtocolAcknowledgePacket(getId(), sessionId, false);
			manager.addPacketToSend(packet, ensureValidUUID(senderUUID, senderAddress));
		}
	}
	
	public void receiveData(UUID senderUUID, UUID sessionId, PacketByteBuffer buffer, SocketAddress senderAddress) {
		PacketSession session = packetSessions.get(sessionId);
		if (session == null) {
			if (receivedSessions[currentSessionSet].contains(sessionId))  {
				return;
			} else if (receivedSessions[currentSessionSet ^ 1].contains(sessionId)) {
				return;
			}
			
			if (System.currentTimeMillis() - lastSessionSetSwitch >= RECEIVED_SESSION_DECAY) {
				currentSessionSet ^= 1;
				receivedSessions[currentSessionSet].clear();
			}
			
			receivedSessions[currentSessionSet].add(sessionId);
			
			handleData(buffer, senderUUID, senderAddress);
			
			ProtocolAcknowledgePacket packet = new ProtocolAcknowledgePacket(getId(), sessionId, true);
			createPacketSession(packet, ensureValidUUID(senderUUID, senderAddress), sessionId, true);
		}
	}
	
	protected void handleData(PacketByteBuffer buffer, UUID senderUUID, SocketAddress senderAddress) {
	}
	
	public void sendData(UUID receiverUUID, PacketByteBuffer buffer) {
		buffer.resetPos();
		byte[] data = buffer.getBytes(new byte[buffer.getSize()]);
		
		if (manager.isServer() && receiverUUID == null) {
			// send to all clients
			ServerNetworkManager server = ((ServerNetworkManager)manager);
			for (ClientConnection client : server.getConnectedClients().values()) {
				UUID nextSessionId = UUID.randomUUID();
				ProtocolDataPacket packet = new ProtocolDataPacket(getId(), nextSessionId, data);
				createPacketSession(packet, client.getClientUUID(), nextSessionId, false);
			}
			
			return;
		}

		UUID nextSessionId = UUID.randomUUID();
		ProtocolDataPacket packet = new ProtocolDataPacket(getId(), nextSessionId, data);
		createPacketSession(packet, receiverUUID, nextSessionId, false);
	}
	
	private void createPacketSession(Packet packet, UUID receiverUUID, UUID sessionId, boolean received) {
		PacketSession session = new PacketSession(receiverUUID);
		packetSessions.put(sessionId, session);
		session.sendPacket(packet);
	}
	
	public int getId() {
		return ProtocolRegistry.getInstance().getId(getClass());
	}

	private class PacketSession {
		
		private final UUID receiverUUID;

		private PacketSessionTask task;
		
		public PacketSession(UUID receiverUUID) {
			this.receiverUUID = receiverUUID;
			
			task = null;
		}

		public void cancelSession() {
			if (task != null)
				task.invalidTask = true;
			task = null;
		}

		private void sendPacket(Packet packet) {
			if (task != null)
				task.invalidTask = false;
			task = new PacketSessionTask(receiverUUID, packet);
			if (task.doTask())
				manager.platPorter.getTaskManager().addTask(task, RESEND_INTERVAL);
		}
	}
	
	private class PacketSessionTask implements IScheduledTask {

		private final UUID receiverUUID;
		private final Packet packet;

		private boolean invalidTask;
		
		public PacketSessionTask(UUID receiverUUID, Packet packet) {
			this.receiverUUID = receiverUUID;
			this.packet = packet;

			invalidTask = false;
		}
		
		@Override
		public boolean doTask() {
			if (invalidTask)
				return false;
				
			if (manager.isServer()) {
				ServerNetworkManager server = (ServerNetworkManager)manager;
				if (!server.isClientConnected(receiverUUID))
					return false;
			} else {
				ClientNetworkManager client = (ClientNetworkManager)manager;
				if (!client.isConnected() && !client.isHandshaking())
					return false;
			}

			manager.addPacketToSend(packet, receiverUUID);
			return true;
		}
	}
	
	@SuppressWarnings("serial")
	private class ReceivedSessionSet extends HashSet<UUID> {
	}
}
