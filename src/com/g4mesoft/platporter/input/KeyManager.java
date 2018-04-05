package com.g4mesoft.platporter.input;

import com.g4mesoft.input.key.KeyInput;
import com.g4mesoft.input.key.KeyInputListener;
import com.g4mesoft.input.key.KeySingleInput;
import com.sun.glass.events.KeyEvent;

public class KeyManager {

	public static final KeyInput KEY_UP = registerKey("up", KeyEvent.VK_UP, KeyEvent.VK_SPACE);
	public static final KeyInput KEY_RIGHT = registerKey("right", KeyEvent.VK_RIGHT);
	public static final KeyInput KEY_DOWN = registerKey("down", KeyEvent.VK_DOWN);
	public static final KeyInput KEY_LEFT = registerKey("left", KeyEvent.VK_LEFT);
	

	private static KeyInput registerKey(String name, int... keyCodes) {
		KeyInput key = new KeySingleInput(name, keyCodes);
		KeyInputListener.getInstance().addKey(key);
		return key;
	}
}
