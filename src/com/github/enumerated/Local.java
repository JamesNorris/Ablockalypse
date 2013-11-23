package com.github.enumerated;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.Ablockalypse;
import com.google.common.collect.Maps;

public enum Local {
    //@formatter:off
    BASE_ENCHANTMENT_STRING("enchantmentString"), 
    BASE_JOIN_STRING("joinString"), 
    BASE_PASSAGE_STRING("passageString"), 
    BASE_PERK_STRING("perkString"), 
    BASE_STRING("baseString"), 
    BASE_TELEPORTER_SETTINGS_STRING("teleporterSettingsString"),
    BASE_WEAPON_STRING("weaponString"), 
    PACK_A_PUNCH_STRING("packapunchString"),
    PERK_DEADSHOT_DAIQUIRI_STRING("perkDeadshotDaiquiriString"), 
    PERK_JUGGERNOG_STRING("perkJuggernogString"),
    PERK_PHD_FLOPPER_STRING("perkPHDFlopperString"),
    PERK_STAMINUP_STRING("perkStaminupString"),
    WRONG_VERSION("wrongVersion");
	//@formatter:on
    //
    private final static Map<Integer, Local> BY_ID = Maps.newHashMap();
    static {
        FileConfiguration local = YamlConfiguration.loadConfiguration(Ablockalypse.getExternal().getLocalizationFile());
        if (local == null) {
            Ablockalypse.getErrorTracker().crash("The localization (local.yml) file cound not be reached.", 80);
        }
        int id = 0;
        for (Local setting : values()) {
            setting.set(local.getString(setting.getName()));
            BY_ID.put(++id, setting);
        }
    }

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
}
