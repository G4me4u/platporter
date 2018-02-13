package com.g4mesoft.platporter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.g4mesoft.net.PacketByteBuffer;

public class PlatPorterMain {

	public static void main(String[] args) throws Exception {
		DatagramSocket client = new DatagramSocket();
		DatagramSocket server = new DatagramSocket(25565, InetAddress.getLocalHost());

		client.connect(server.getLocalSocketAddress());
		
		PacketByteBuffer buffer = new PacketByteBuffer();
		buffer.putInt(10);
		buffer.putFloat(2.0f);
		
		DatagramPacket packet = new DatagramPacket(buffer.toByteArray(), buffer.getSize());
		client.send(packet);
		
		packet = new DatagramPacket(new byte[1024], 1024);
		server.receive(packet);
		
		buffer = new PacketByteBuffer(packet.getData(), 0, packet.getLength());
		System.out.println(buffer.getInt());
		System.out.println(buffer.getFloat());
		
		client.close();
		server.close();
	}
}
