package com.g4mesoft.platporter.sound;

import java.io.IOException;

import com.g4mesoft.sound.SoundManager;
import com.g4mesoft.sound.format.AudioParsingException;
import com.g4mesoft.sound.processor.AudioSource;

public class Sounds {

	public static int JUMP_SOUND = -1;
	
	private Sounds() {
	}
	
	public static void loadAllSounds() {
		try {
			JUMP_SOUND = loadSound("/assets/player_jump.wav");
		} catch (IOException | AudioParsingException e) {
			throw new RuntimeException("Some sounds failed to load", e);
		}
	}
	
	public static int loadSound(String path) throws IOException, AudioParsingException {
		int id = SoundManager.getInstance().loadSound(Sounds.class.getResourceAsStream(path));
		if (id == -1)
			throw new IllegalArgumentException("Argument path is not a wave file: " + path);
		return id;
	}
	
	public static void playSound(int sound, float volume, float pitch) {
		AudioSource audio = SoundManager.getInstance().playSound(JUMP_SOUND);
		audio.setVolume(volume);
	}
}
