package com.github.JamesNorris.Util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {
	public enum ZASound {
		ACHIEVEMENT, DEATH, END, LAST_STAND, NEXT_LEVEL, PREV_LEVEL, START, TELEPORT;
	}

	/**
	 * Plays a selection of sounds near the player.
	 * 
	 * @param sound The type of sound to be played through this manager
	 */
	public static void generateSound(Player player, ZASound sound) {// TODO allow sounds to be changed from the API
		Location l = player.getLocation();
		switch (sound) {
			case TELEPORT:
				player.playSound(l, Sound.PORTAL_TRIGGER, 1, 1);
			break;
			case LAST_STAND:
				player.playSound(l, Sound.GHAST_SCREAM, 3, 1);
			break;
			case DEATH:
				player.playSound(l, Sound.GHAST_DEATH, 1, 1);
			break;
			case START:
				player.playSound(l, Sound.AMBIENCE_THUNDER, 7, 1);
				player.playSound(l, Sound.PORTAL_TRAVEL, 1, 1);
			break;
			case END:
				player.playSound(l, Sound.AMBIENCE_RAIN, 7, 1);
				player.playSound(l, Sound.AMBIENCE_THUNDER, 7, 1);
			break;
			case ACHIEVEMENT:
				player.playSound(l, Sound.LEVEL_UP, 1, 1);
			break;
			case NEXT_LEVEL:
				player.playSound(l, Sound.AMBIENCE_THUNDER, 7, 1);
			break;
			case PREV_LEVEL:
				player.playSound(l, Sound.AMBIENCE_CAVE, 7, 1);
			break;
		}
	}
}
