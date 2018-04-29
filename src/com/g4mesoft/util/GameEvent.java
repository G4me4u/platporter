package com.g4mesoft.util;

public abstract class GameEvent {

	private final Object sender;
	
	boolean cancel;
	
	public GameEvent(Object sender) {
		this.sender = sender;
		
		cancel = false;
	}
	
	public void cancelEvent() {
		cancel = true;
	}
	
	public Object getSender() {
		return sender;
	}
}
