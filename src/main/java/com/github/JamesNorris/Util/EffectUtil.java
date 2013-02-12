package com.github.JamesNorris.Util;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.JamesNorris.Enumerated.Setting;
import com.github.JamesNorris.Enumerated.ZAEffect;

public class EffectUtil {
    /**
     * Plays a radius of effect around the location.
     * 
     * @param loc The location to play the effect
     * @param effect The effect to play
     * @param radius The radius to play the effect
     */
    public static void generateControlledEffect(Location loc, ZAEffect effect, int radius) {
        World w = loc.getWorld();
        if ((Boolean) Setting.EXTRAEFFECTS.getSetting())
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
                case TELEPORTATION:
                    new ControlledEffect(w, Effect.ENDER_SIGNAL, radius, 1, loc, true);
                break;
                case LIGHTNING:
                    int x = loc.getBlockX(),
                    y = loc.getBlockY(),
                    z = loc.getBlockZ();
                    for (int i = -radius; i <= radius; i++)
                        for (int k = -radius; k <= radius; k++) {
                            loc = w.getBlockAt(x + i, y, z + k).getLocation();
                            w.strikeLightningEffect(loc);
                        }
                break;
                case BEACON:
                // TODO
                break;
            }
    }

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
    public static void generateEffect(Player player, ZAEffect effect) {
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
    public static void generateEffect(final World w, final Location l, ZAEffect effect) {
        if ((Boolean) Setting.EXTRAEFFECTS.getSetting())
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
                case TELEPORTATION:
                    w.playEffect(l, Effect.ENDER_SIGNAL, 1);
                break;
                case LIGHTNING:
                    w.strikeLightningEffect(l);
                break;
                case BEACON:
                    // TODO
                    for (int i = 0; i < 10; i++)
                        w.playEffect(l.add(0, i, 0), Effect.MOBSPAWNER_FLAMES, 1);
                break;
            }
    }
}
