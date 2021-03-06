package com.g4mesoft.platporter.input;

import java.awt.event.KeyEvent;

import com.g4mesoft.input.key.KeyInput;
import com.g4mesoft.input.key.KeyInputListener;
import com.g4mesoft.input.key.KeySingleInput;

public class KeyManager {

	public static final KeyInput KEY_UP = registerKey("up", KeyEvent.VK_UP);
	public static final KeyInput KEY_RIGHT = registerKey("right", KeyEvent.VK_RIGHT);
	public static final KeyInput KEY_DOWN = registerKey("down", KeyEvent.VK_DOWN);
	public static final KeyInput KEY_LEFT = registerKey("left", KeyEvent.VK_LEFT);

	public static final KeyInput KEY_INTERACT = registerKey("interact", KeyEvent.VK_SPACE);

	public static final KeyInput KEY_TOGGLE_HITBOX = registerKey("toggle_hitbox", KeyEvent.VK_H);

	private static KeyInput registerKey(String name, int... keyCodes) {
		KeyInput key = new KeySingleInput(name, keyCodes);
		KeyInputListener.getInstance().addKey(key);
		return key;
	}
}
