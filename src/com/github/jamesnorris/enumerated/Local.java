package com.github.jamesnorris.enumerated;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.jamesnorris.External;
import com.google.common.collect.Maps;

public enum Local {
    //@formatter:off
    BASE_STRING("baseString"),
    BASE_PASSAGE_STRING("passageString"), 
    BASE_ENCHANTMENT_STRING("enchantmentString"), 
    BASE_JOIN_STRING("joinString"), 
    BASE_PERK_STRING("perkString"), 
    BASE_WEAPON_STRING("weaponString"), 
    PACK_A_PUNCH_STRING("packapunchString"),
    PERK_DEADSHOT_DAIQUIRI_STRING("perkDeadshotDaiquiriString"), 
    PERK_JUGGERNOG_STRING("perkJuggernogString"),
    PERK_STAMINUP_STRING("perkStaminupString"),
    PERK_PHD_FLOPPER_STRING("perkPHDFlopperString"),
    WRONG_VERSION("wrongVersion");
	//@formatter:on
    //
    private final static Map<Integer, Local> BY_ID = Maps.newHashMap();

    public static Local getById(final int id) {
        return BY_ID.get(id);
    }

    public static int getHighestId() {
        return BY_ID.size();
    }

    private String setting, object;

    Local(String setting) {
        this.setting = setting;
    }

    public String getName() {
        return setting;
    }

    public String getSetting() {
        return object;
    }

    public void set(String object) {
        this.object = object;
    }

    static {
        FileConfiguration local = External.getConfig(External.localizationFile, External.local);
        int id = 0;
        for (Local setting : values()) {
            setting.set(local.getString(setting.getName()));
            BY_ID.put(++id, setting);
        }
    }
}
