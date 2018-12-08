package com.github.jamesnorris.ablockalypse.enumerated;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Sound;

import com.google.common.collect.Maps;

public enum ZASound {
    ACHIEVEMENT(1, new Sound[] {Sound.ENTITY_PLAYER_LEVELUP}, 1),
    AREA_BUY(11, new Sound[] {Sound.BLOCK_WOODEN_DOOR_OPEN}, 3),
    AREA_REPLACE(12, new Sound[] {Sound.BLOCK_WOODEN_DOOR_CLOSE}, 3),
    BARRIER_BREAK(9, new Sound[] {Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR}, 2),
    BARRIER_REPAIR(10, new Sound[] {Sound.BLOCK_PISTON_EXTEND, Sound.ENTITY_ITEM_BREAK}, 1),
    DEATH(2, new Sound[] {Sound.ENTITY_GHAST_DEATH}, 1),
    END(3, new Sound[] {Sound.AMBIENT_CAVE}, 7),
    EXPLOSION(13, new Sound[] {Sound.ENTITY_GENERIC_EXPLODE}, 5),
    LAST_STAND(4, new Sound[] {Sound.ENTITY_GHAST_SCREAM}, 3),
    NEXT_LEVEL(5, new Sound[] {Sound.ENTITY_LIGHTNING_BOLT_THUNDER}, 7),
    PREV_LEVEL(6, new Sound[] {Sound.WEATHER_RAIN}, 7),
    START(7, new Sound[] {Sound.AMBIENT_CAVE}, 7),
    TELEPORT(8, new Sound[] {Sound.BLOCK_PORTAL_TRAVEL}, 1);
    private final static Map<Integer, ZASound> BY_ID = Maps.newHashMap();
    static {
        for (ZASound setting : values()) {
            BY_ID.put(setting.id, setting);
        }
    }

    public static ZASound getById(final int id) {
        return BY_ID.get(id);
    }

    private int id, type;
    private Sound[] sounds;

    ZASound(int id, Sound[] sounds, int type) {
        this.id = id;
        this.sounds = sounds;
    }

    public int getId() {
        return id;
    }

    public void play(Location loc) {
        if ((Boolean) Setting.EXTRA_SOUNDS.getSetting()) {
            for (Sound sound : sounds) {
                loc.getWorld().playSound(loc, sound, type, 1);
            }
        }
    }
}
