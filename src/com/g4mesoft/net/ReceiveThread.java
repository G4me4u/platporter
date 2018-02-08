package com.g4mesoft.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

public class ReceiveThread extends Thread {

	private final NetworkManager manager;
	private final byte[] buffer;
	
	private boolean listening;
	
	public ReceiveThread(NetworkManager manager) {
		this.manager = manager;
		
		buffer = new byte[NetworkManager.DEFAULT_BUFFER_SIZE];
		
		setDaemon(true);
	}

	public void stopListening() {
		listening = false;
	}
	
	public void startListening() {
		listening = true;
		start();
	}
	
	@Override
	public void run() {
		DatagramPacket packet;
		while (listening) {
			packet = new DatagramPacket(buffer, NetworkManager.DEFAULT_BUFFER_SIZE);
			try {
				manager.socket.receive(packet);
				manager.receiveDatagramPacket(packet);
			} catch (SocketException se) {
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
