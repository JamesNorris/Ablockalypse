package com.github.JamesNorris.Util;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;

public class EffectUtil {
	public enum ZAEffect {
		SMOKE, FLAMES, POTION_BREAK, EXTINGUISH, WOOD_BREAK, IRON_BREAK;
	}

	private static ConfigurationData cd = External.getYamlManager().getConfigurationData();

	/**
	 * Plays a selection of effects at the give location.
	 * 
	 * @param player The player used to play this effect for
	 * @param l The location to play the effect at
	 * @param effect The type of effect to be played through this manager
	 */
	public static void generateEffect(Player player, Location l, ZAEffect effect) {
		World w = player.getWorld();
		generateEffect(w, l, effect);
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
	 * Plays a selection of effects at the location given.
	 * 
	 * @param w The world to play the effect in
	 * @param l The location to play the effect at
	 * @param effect The effect to play
	 */
	public static void generateEffect(World w, Location l, ZAEffect effect) {
		if (cd.extraEffects)
			switch (effect) {
				case SMOKE:
					w.playEffect(l, Effect.SMOKE, 1);
				break;
				case FLAMES:
					w.playEffect(l, Effect.MOBSPAWNER_FLAMES, 1);
				break;
				case POTION_BREAK:
					w.playEffect(l, Effect.POTION_BREAK, 2);
				break;
				case EXTINGUISH:
					w.playEffect(l, Effect.EXTINGUISH, 1);
				break;
				case WOOD_BREAK:
					w.playEffect(l, Effect.ZOMBIE_CHEW_WOODEN_DOOR, 1);
				break;
				case IRON_BREAK:
					w.playEffect(l, Effect.ZOMBIE_CHEW_IRON_DOOR, 1);
				break;
			}
	}

	/**
	 * Plays a radius of effect around the location.
	 * 
	 * @param loc The location to play the effect
	 * @param effect The effect to play
	 * @param radius The radius to play the effect
	 */
	public static void generateControlledEffect(Location loc, ZAEffect effect, int radius) {
		World w = loc.getWorld();
		if (cd.extraEffects)
			switch (effect) {
				case SMOKE:
					new ControlledEffect(w, Effect.SMOKE, radius, 1, loc, true);
				break;
				case FLAMES:
					new ControlledEffect(w, Effect.MOBSPAWNER_FLAMES, radius, 1, loc, true);
				break;
				case POTION_BREAK:
					new ControlledEffect(w, Effect.POTION_BREAK, radius, 1, loc, true);
				break;
				case EXTINGUISH:
					new ControlledEffect(w, Effect.EXTINGUISH, radius, 1, loc, true);
				break;
				case WOOD_BREAK:
					new ControlledEffect(w, Effect.ZOMBIE_CHEW_WOODEN_DOOR, radius, 1, loc, true);
				break;
				case IRON_BREAK:
					new ControlledEffect(w, Effect.ZOMBIE_CHEW_IRON_DOOR, radius, 1, loc, true);
				break;
			}
	}
}
