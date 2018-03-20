package com.g4mesoft.net.client;

import com.g4mesoft.net.packet.client.C00PingPacket;
import com.g4mesoft.util.IScheduledTask;

public class PingTask implements IScheduledTask {

	private final ClientNetworkManager client;
	
	public PingTask(ClientNetworkManager client) {
		this.client = client;
	}
	
	@Override
	public boolean doTask() {
		if (!client.isConnected())
			return false;
		client.addPacketToSend(new C00PingPacket());
		return true;
	}
}
