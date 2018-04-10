package com.g4mesoft.util;

public abstract class GameEventListener {

	private Class<? extends GameEvent> eventClazz;
	
	public GameEventListener(Class<? extends GameEvent> eventClazz) {
		this.eventClazz = eventClazz;
	}
	
	protected abstract void handleEvent(GameEvent event);
	
	public Class<? extends GameEvent> getEventClass() {
		return eventClazz;
	}
}
