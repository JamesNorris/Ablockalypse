package com.github.JamesNorris.Util;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;

public class EffectUtil {
	public enum ZAEffect {
		SMOKE, FLAMES, POTION_BREAK, EXTINGUISH;
	}

	private static ConfigurationData cd = External.getYamlManager().getConfigurationData();

	/**
	 * Plays a selection of effects near the player.
	 * 
	 * @param player The player used to play this effect for
	 * @param l The location to play the effect at
	 * @param effect The type of effect to be played through this manager
	 */
	public static void generateEffect(Player player, Location l, ZAEffect effect) {
		if (cd.extraEffects)
			switch (effect) {
				case SMOKE:
					player.playEffect(l, Effect.SMOKE, 1);
				break;
				case FLAMES:
					player.playEffect(l, Effect.MOBSPAWNER_FLAMES, 1);
				break;
				case POTION_BREAK:
					player.playEffect(l, Effect.POTION_BREAK, 2);
				break;
				case EXTINGUISH:
					player.playEffect(l, Effect.EXTINGUISH, 1);
				break;
			}
	}

	/**
	 * Plays a selection of effects near the player.
	 * 
	 * @param player The player used to play this effect for
	 * @param effect The type of effect to be played through this manager
	 */
	public static void generateEffect(Player player, ZAEffect effect) {// TODO allow effects to be changed from the API
		Location l = player.getLocation();
		generateEffect(player, l, effect);
	}

	/**
	 * Plays a radius of effect around the location.
	 * 
	 * @param loc The location to play the effect
	 * @param effect The effect to play
	 * @param radius The radius to play the effect
	 */
	public static void generateControlledEffect(Location loc, ZAEffect effect, int radius) {
		if (cd.extraEffects)
			switch (effect) {
				case SMOKE:
					new ControlledEffect(loc.getWorld(), Effect.SMOKE, radius, 1, loc, true);
				break;
				case FLAMES:
					new ControlledEffect(loc.getWorld(), Effect.MOBSPAWNER_FLAMES, radius, 1, loc, true);
				break;
				case POTION_BREAK:
					new ControlledEffect(loc.getWorld(), Effect.POTION_BREAK, radius, 1, loc, true);
				break;
				case EXTINGUISH:
					new ControlledEffect(loc.getWorld(), Effect.EXTINGUISH, radius, 1, loc, true);
				break;
			}
	}
}
