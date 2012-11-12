package com.github.JamesNorris.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.JamesNorris.Util.Enumerated.ZASound;

public class SoundUtil {
	/**
	 * Plays a selection of sounds near the player.
	 * 
	 * @param player The player to play the sound near
	 * @param sound The type of sound to be played through this manager
	 */
	public static void generateSound(Player player, ZASound sound) {
		Location l = player.getLocation();
		World w = l.getWorld();
		generateSound(w, l, sound);
	}

	/**
	 * Plays a selection of sounds near the player.
	 * 
	 * @param w Thw world to play the sound in
	 * @param l The location to play the sound at
	 * @param sound The sound to play
	 */
	public static void generateSound(final World w, final Location l, ZASound sound) {
		switch (sound) {
			case TELEPORT:
				w.playSound(l, Sound.PORTAL_TRIGGER, 1, 1);
			break;
			case LAST_STAND:
				w.playSound(l, Sound.GHAST_SCREAM, 3, 1);
			break;
			case DEATH:
				w.playSound(l, Sound.GHAST_DEATH, 1, 1);
			break;
			case START:
				w.playSound(l, Sound.AMBIENCE_THUNDER, 7, 1);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Ablockalypse.instance, new Runnable() {
					@Override public void run() {
						w.playSound(l, Sound.PORTAL_TRAVEL, 1, 1);
					}
				}, 40);
			break;
			case END:// TODO change
				w.playSound(l, Sound.AMBIENCE_RAIN, 7, 1);
				w.playSound(l, Sound.AMBIENCE_THUNDER, 7, 1);
			break;
			case ACHIEVEMENT:
				w.playSound(l, Sound.LEVEL_UP, 1, 1);
			break;
			case NEXT_LEVEL:
				w.playSound(l, Sound.AMBIENCE_THUNDER, 7, 1);
			break;
			case PREV_LEVEL:
				w.playSound(l, Sound.AMBIENCE_CAVE, 7, 1);
			break;
			case BARRIER_BREAK:
				w.playSound(l, Sound.ZOMBIE_WOODBREAK, 2, 1);
			break;
			case BARRIER_REPAIR:
				w.playSound(l, Sound.PISTON_EXTEND, 1, 1);
				w.playSound(l, Sound.ITEM_BREAK, 1, 1);
			break;
			case AREA_BUY:
				w.playSound(l, Sound.DOOR_OPEN, 3, 1);
			break;
			case AREA_REPLACE:
				w.playSound(l, Sound.DOOR_CLOSE, 3, 1);
			break;
			case EXPLOSION:
				w.playSound(l, Sound.EXPLODE, 5, 1);
			break;
		}
	}
}
