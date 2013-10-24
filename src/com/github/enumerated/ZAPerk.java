package com.github.enumerated;

import java.util.Map;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.aspect.entity.ZAPlayer;
import com.google.common.collect.Maps;

public enum ZAPerk {
    DEADSHOT_DAIQUIRI(1, Local.PERK_DEADSHOT_DAIQUIRI_STRING.getSetting(), (Integer) Setting.PERK_DURATION.getSetting(), (Integer) Setting.DEADSHOT_DAIQUIRI_COST.getSetting(), (Integer) Setting.DEADSHOT_DAIQUIRI_LEVEL.getSetting()) {
        @Override public void givePerk(ZAPlayer zap) {
            zap.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, getDuration(), 1));
            zap.addToPerkList(this);
        }
    },
    JUGGERNOG(2, Local.PERK_JUGGERNOG_STRING.getSetting(), (Integer) Setting.PERK_DURATION.getSetting(), (Integer) Setting.JUGGERNOG_COST.getSetting(), (Integer) Setting.JUGGERNOG_LEVEL.getSetting()) {
        @Override public void givePerk(ZAPlayer zap) {
            zap.setHitAbsorption(1);// 1 full heart per hit
            zap.addToPerkList(this);
        }
    },
    PHD_FLOPPER(3, Local.PERK_PHD_FLOPPER_STRING.getSetting(), (Integer) Setting.PERK_DURATION.getSetting(), (Integer) Setting.PHD_FLOPPER_COST.getSetting(), (Integer) Setting.PHD_FLOPPER_LEVEL.getSetting()) {
        @Override public void givePerk(ZAPlayer zap) {
            // PlayerMove.java does all the work
            zap.addToPerkList(this);
        }
    },
    STAMINUP(4, Local.PERK_STAMINUP_STRING.getSetting(), (Integer) Setting.PERK_DURATION.getSetting(), (Integer) Setting.STAMINUP_COST.getSetting(), (Integer) Setting.STAMINUP_LEVEL.getSetting()) {
        @Override public void givePerk(ZAPlayer zap) {
            zap.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, getDuration(), 1));
            zap.addToPerkList(this);
        }
    };
    private int id, duration, cost, level;
    private String label;
    private final static Map<Integer, ZAPerk> BY_ID = Maps.newHashMap();
    static {
        for (ZAPerk perk : values()) {
            BY_ID.put(perk.id, perk);
        }
    }

    public static ZAPerk getById(int id) {
        return BY_ID.get(id);
    }

    ZAPerk(int id, String label, int duration, int cost, int level) {
        this.id = id;
        this.label = label;
        this.duration = duration;
        this.cost = cost;
        this.level = level;
        if (duration == -1) {
            duration = Integer.MAX_VALUE;
        }
    }

    public int getCost() {
        return cost;
    }

    public int getDuration() {
        return duration;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public int getLevel() {
        return level;
    }

    public abstract void givePerk(ZAPlayer zap);
}
