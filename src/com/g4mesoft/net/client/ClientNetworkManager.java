package com.g4mesoft.net.client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

import com.g4mesoft.net.NetworkManager;
import com.g4mesoft.net.packet.IPacket;

public class ClientNetworkManager extends NetworkManager {

	public ClientNetworkManager(SocketAddress serverAddress) throws SocketException {
		super(new DatagramSocket());
		
		socket.connect(serverAddress);
	}

	@Override
	protected boolean sendPacket(IPacket packet) {
		try {
			socket.send(prepareToSendPacket(packet));
		} catch (IOException se) {
			se.printStackTrace();
			return false;
		}
		return true;
	}
}
