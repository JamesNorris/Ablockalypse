package com.github.enumerated;

import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.Location;

import com.google.common.collect.Maps;

public enum ZAEffect {
    //@formatter:off
	EXTINGUISH(4, Effect.EXTINGUISH, 1), 
	FLAMES(2, Effect.MOBSPAWNER_FLAMES, 1), 
	IRON_BREAK(6, Effect.ZOMBIE_CHEW_IRON_DOOR, 1), 
	POTION_BREAK(3, Effect.POTION_BREAK, 2),
	SMOKE(1, Effect.SMOKE, 1), 
	TELEPORTATION(7, Effect.ENDER_SIGNAL, 1), 
	WOOD_BREAK(5, Effect.ZOMBIE_CHEW_WOODEN_DOOR, 1);
	//@formatter:on
    //
    private final static Map<Integer, ZAEffect> BY_ID = Maps.newHashMap();
    static {
        for (ZAEffect setting : values()) {
            BY_ID.put(setting.id, setting);
        }
    }

    public static ZAEffect getById(final int id) {
        return BY_ID.get(id);
    }

    private Effect effect;
    private int id, type;

    ZAEffect(int id, Effect effect, int type) {
        this.id = id;
        this.effect = effect;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void play(Location loc) {
        if ((Boolean) Setting.EXTRA_EFFECTS.getSetting()) {
            loc.getWorld().playEffect(loc, effect, type);
        }
    }
}
