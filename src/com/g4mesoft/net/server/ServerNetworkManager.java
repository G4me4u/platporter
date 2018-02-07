package com.g4mesoft.net.server;

import java.net.DatagramSocket;
import java.net.SocketException;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.packet.IPacket;

public class ServerNetworkManager extends NetworkManager {

	public ServerNetworkManager(int port) throws SocketException {
		super(new DatagramSocket(port));
	}

	@Override
	protected boolean sendPacket(IPacket packet) {
		return false;
	}
}
