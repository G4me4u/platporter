package com.g4mesoft.net;

public enum NetworkSide {

	SERVER(0, "server"), 
	CLIENT(1, "client");
	
	private int id;
	private String name;
	
	NetworkSide(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
}
