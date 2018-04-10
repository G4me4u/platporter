package com.g4mesoft.util;

public abstract class GameEvent {

	private final Object sender;
	private final String desc;
	
	boolean cancel;
	
	public GameEvent(Object sender, String desc) {
		this.sender = sender;
		this.desc = desc;
		
		cancel = false;
	}
	
	public void cancelEvent() {
		cancel = true;
	}
	
	public Object getSender() {
		return sender;
	}
	
	public String getDescription() {
		return desc;
	}
}
