package com.g4mesoft.platporter.sound;

import java.io.IOException;

import com.g4mesoft.sound.SoundManager;
import com.g4mesoft.sound.format.AudioParsingException;
import com.g4mesoft.sound.processor.AudioSource;

public class Sounds {

	public static int JUMP_SOUND = -1;
	public static int LEVEL_ENTER_SOUND = -1;
	public static int LEVEL_WON_SOUND = -1;
	public static int LEVER_USE_SOUND = -1;
	public static int PRESSURE_PLATE_SOUND = -1;
	public static int BOOST_TILE_SOUND = -1;
	public static int GAME_WON_SOUND = -1;
	public static int WORLD_ENTER_SOUND = -1;
	
	private Sounds() {
	}
	
	public static void loadAllSounds() {
		try {
			JUMP_SOUND = loadSound("/assets/player_jump.wav");
			LEVEL_ENTER_SOUND = loadSound("/assets/level_enter.wav");
			LEVEL_WON_SOUND = loadSound("/assets/level_won.wav");
			LEVER_USE_SOUND = loadSound("/assets/lever_use.wav");
			PRESSURE_PLATE_SOUND = loadSound("/assets/pressure_plate_activate.wav");
			BOOST_TILE_SOUND = loadSound("/assets/boost_tile.wav");
			GAME_WON_SOUND = loadSound("/assets/game_won.wav");
			WORLD_ENTER_SOUND = loadSound("/assets/world_enter.wav");
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
	
	public static AudioSource playSound(int sound, float volume, float pitch) {
		if (sound == -1)
			return null;
		
		AudioSource audio = playSound(sound, volume);
		if (pitch != 1.0f)
			audio.setPitch(pitch);
		return audio;
	}

	public static AudioSource playSound(int sound, float volume) {
		if (sound == -1)
			return null;

		AudioSource audio = SoundManager.getInstance().playSound(sound);
		audio.setVolume(volume);
		return audio;
	}
}
