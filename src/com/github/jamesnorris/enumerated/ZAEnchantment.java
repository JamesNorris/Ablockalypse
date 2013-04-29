package com.github.jamesnorris.enumerated;

import java.util.Map;

import org.bukkit.enchantments.Enchantment;

import com.google.common.collect.Maps;

public enum ZAEnchantment {
    DAMAGE(1, Local.PACK_A_PUNCH_STRING.getSetting(), Enchantment.DAMAGE_ALL, (Integer) Setting.PACK_A_PUNCH_COST.getSetting());
    private final static Map<Integer, ZAEnchantment> BY_ID = Maps.newHashMap();

    static {
        for (ZAEnchantment setting : values()) {
            BY_ID.put(setting.id, setting);
        }
    }

    public static ZAEnchantment getById(final int id) {
        return BY_ID.get(id);
    }
    private Enchantment ench;
    private int id, cost;
    private String label;

    ZAEnchantment(int id, String label, Enchantment ench, int cost) {
        this.id = id;
        this.label = label;
        this.cost = cost;
        this.ench = ench;
    }

    public int getCost() {
        return cost;
    }

    public Enchantment getEnchantment() {
        return ench;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
