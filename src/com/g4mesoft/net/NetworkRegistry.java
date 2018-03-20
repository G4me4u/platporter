package com.g4mesoft.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class NetworkRegistry<T> {

	protected final Map<Class<? extends T>, Integer> classToId;
	protected final List<Class<? extends T>> classes;

	public NetworkRegistry() {
		classToId = new HashMap<Class<? extends T>, Integer>();
		classes = new ArrayList<Class<? extends T>>();
	}
	
	protected void addEntry(Class<? extends T> clazz) {
		int id = classes.size();
		classes.add(clazz);
		classToId.put(clazz, Integer.valueOf(id));
	}

	public Class<? extends T> getClass(int id) {
		if (classes.size() <= id)
			throw new IllegalArgumentException("Id doesn't exist in registry!");
		return classes.get(id);
	}
	
	public int getId(Class<? extends T> clazz) {
		Integer id = classToId.get(clazz);
		if (id == null)
			throw new IllegalArgumentException("Packet does not exist in registry!");
		return id.intValue();
	}
}
