package com.github.JamesNorris.Enumerated;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.JamesNorris.External;
import com.google.common.collect.Maps;

public enum Local {
    //@formatter:off
	BASEAREASTRING(14, "areaString"), BASEENCHANTMENTSTRING(22, "enchantmentString"), BASEJOINSTRING(15, "joinString"), BASEPERKSTRING(8, "perkString"), BASESTRING(7, "baseString"),
	BASEWEAPONSTRING(2, "weaponString"), ENCHANTMENTDAMAGESTRING(23, "enchantmentDamageString"),
	ENCHANTMENTRANDOMSTRING(1, "enchantmentRandomString"), NAMEDBOW(21, "bow"), NAMEDDIAMONDSWORD(19, "diamondSword"), NAMEDGOLDSWORD(20, "goldSword"),
	NAMEDIRONSWORD(18, "ironSword"), NAMEDSTONESWORD(17, "stoneSword"), NAMEDWOODSWORD(16, "woodSword"), PERKDAMAGESTRING(11, "perkDamageString"), PERKHEALSTRING(9, "perkHealString"), PERKJUGGERNAUTSTRING(24, "perkJuggernautString"),
	PERKREGENERATIONSTRING(11, "perkRegenerationString"), PERKSPEEDSTRING(10, "perkSpeedString"), WEAPONDIAMONDSTRING(6, "weaponDiamondSwordString"), WEAPONGOLDSTRING(12, "weaponGoldSwordString"), WEAPONGRENADESTRING(13, "weaponGrenadeString"), WEAPONIRONSTRING(5, "weaponIronSwordString"),
	WEAPONSTONESTRING(4, "weaponStoneSwordString"), WEAPONWOODSTRING(3, "weaponWoodSwordString");//on 25
	//@formatter:on
    //
    private final static Map<Integer, Local> BY_ID = Maps.newHashMap();

    public static Local getById(final int id) {
        return BY_ID.get(id);
    }

    public static int getHighestId() {
        return BY_ID.size();
    }

    private int id;
    private String setting, object;

    Local(int id, String setting) {
        this.id = id;
        this.setting = setting;
    }

    public int getId() {
        return id;
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
        for (Local setting : values()) {
            setting.set(local.getString(setting.getName()));
            BY_ID.put(setting.id, setting);
        }
    }
}
