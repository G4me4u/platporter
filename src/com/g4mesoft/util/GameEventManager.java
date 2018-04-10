package com.g4mesoft.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameEventManager {

	private Map<Class<? extends GameEvent>, List<GameEventListener>> eventListeners;
	
	public GameEventManager() {
		eventListeners = new HashMap<Class<? extends GameEvent>, List<GameEventListener>>();
	}
	
	public void handleEvent(GameEvent event) {
		List<GameEventListener> listeners = eventListeners.get(event.getClass());
		if (listeners == null || listeners.isEmpty())
			return;
		for (GameEventListener listener : listeners) {
			listener.handleEvent(event);
			if (event.cancel)
				break;
		}
	}
	
	public void addEventListener(GameEventListener listener) {
		Class<? extends GameEvent> eventClazz = listener.getEventClass();
		if (eventClazz == null)
			return;
		
		List<GameEventListener> listeners = eventListeners.get(eventClazz);
		if (listeners == null) {
			listeners = new ArrayList<GameEventListener>();
			eventListeners.put(eventClazz, listeners);
		}
		
		if (listeners.contains(listener))
			return;
		listeners.add(listener);
	}
	
	public void removeEventListener(GameEventListener listener) {
		Class<? extends GameEvent> eventClazz = listener.getEventClass();
		if (eventClazz == null)
			return;
		List<GameEventListener> listeners = eventListeners.get(eventClazz);
		if (listeners == null)
			return;
		listeners.remove(listener);
	}
}
