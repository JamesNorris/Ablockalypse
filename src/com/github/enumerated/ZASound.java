package com.github.enumerated;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Sound;

import com.google.common.collect.Maps;

public enum ZASound {
    //@formatter:off
	ACHIEVEMENT(1, new Sound[] {Sound.LEVEL_UP}, 1), AREA_BUY(11, new Sound[] {Sound.DOOR_OPEN}, 3), 
	AREA_REPLACE(12, new Sound[] {Sound.DOOR_CLOSE}, 3), BARRIER_BREAK(9, new Sound[] {Sound.ZOMBIE_WOODBREAK}, 2), 
	BARRIER_REPAIR(10, new Sound[] {Sound.PISTON_EXTEND, Sound.ITEM_BREAK}, 1), DEATH(2, new Sound[] {Sound.GHAST_DEATH}, 1), 
	END(3, new Sound[] {Sound.AMBIENCE_CAVE}, 7), EXPLOSION(13, new Sound[] {Sound.EXPLODE}, 5), 
	LAST_STAND(4, new Sound[] {Sound.GHAST_SCREAM}, 3), NEXT_LEVEL(5, new Sound[] {Sound.AMBIENCE_THUNDER}, 7), 
	PREV_LEVEL(6, new Sound[] {Sound.AMBIENCE_RAIN}, 7), START(7, new Sound[] {Sound.AMBIENCE_CAVE}, 7), 
	TELEPORT(8, new Sound[] {Sound.PORTAL_TRIGGER}, 1);
	//@formatter:on
    //
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
