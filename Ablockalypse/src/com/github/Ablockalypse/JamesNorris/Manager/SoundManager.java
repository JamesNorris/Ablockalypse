package com.github.Ablockalypse.JamesNorris.Manager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundManager {
	public enum ZASound {
		ACHIEVEMENT, DEATH, END, LAST_STAND, START, TELEPORT;
	}

	private Player player;

	/**
	 * Creates a new sound manager for a game, that can play specific types of sounds for players.
	 * 
	 * @param player The player to follow with this instance
	 */
	public SoundManager(Player player) {
		this.player = player;
	}

	@SuppressWarnings("unused") @Override public void finalize() {
		for (Method m : this.getClass().getDeclaredMethods())
			m = null;
		for (Field f : this.getClass().getDeclaredFields())
			f = null;
	}

	/**
	 * Plays a selection of sounds near the player.
	 * 
	 * @param sound The type of sound to be played through this manager
	 */
	public void generateSound(ZASound sound) {
		Location l = player.getLocation();
		switch (sound) {
			case TELEPORT:
				player.playSound(l, Sound.AMBIENCE_THUNDER, 7, 1);
				player.playSound(l, Sound.PORTAL_TRAVEL, 1, 1);
			break;
			case LAST_STAND:
				player.playSound(l, Sound.GHAST_SCREAM, 2, 15);
			break;
			case DEATH:
				player.playSound(l, Sound.GHAST_DEATH, 2, 20);
			break;
			case START:
				player.playSound(l, Sound.GHAST_MOAN, 4, 1);
			break;
			case END:
				player.playSound(l, Sound.BLAZE_BREATH, 5, 10);
				player.playSound(l, Sound.CREEPER_HISS, 5, 10);
				player.playSound(l, Sound.EXPLODE, 1, 15);
			break;
			case ACHIEVEMENT:
				player.playSound(l, Sound.LEVEL_UP, 1, 5);
			break;
		}
	}
}
