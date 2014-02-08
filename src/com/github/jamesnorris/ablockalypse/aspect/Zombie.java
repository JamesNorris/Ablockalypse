package com.github.jamesnorris.ablockalypse.aspect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.World;

import com.github.jamesnorris.ablockalypse.utility.AblockalypseUtility;

public class Zombie extends ZAMob {
    private boolean fireproof;

    public Zombie(Map<String, Object> savings) {
        super(savings);
        setDefaults();
        fireproof = (Boolean) savings.get("is_fireproof");
    }

    /**
     * Creates a new instance of the zombie for ZA.
     * 
     * @param bukkit_zombie The zombie to be made into this instance
     * @param game The game to involve this zombie in
     */
    public Zombie(World world, UUID entityUUID, Game game) {
        super((org.bukkit.entity.Zombie) data.getEntityByUUID(world, entityUUID), game);
        setDefaults();
    }

    public org.bukkit.entity.Zombie getBukkitZombie() {
        return (org.bukkit.entity.Zombie) entity;
    }

    @Override public double getHeight() {
        return 2D;
    }

    @Override public double getLength() {
        return .891D;
    }

    @Override public Map<String, Object> getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.putAll(super.getSave());
        savings.put("is_fireproof", fireproof);
        return savings;
    }

    @Override public double getSpeed() {
        return .08D;
    }

    @Override public double getWidth() {
        return 1.031D;
    }

    /**
     * Checks whether or not the zombies is fireproof.
     * 
     * @return Whether or not the zombie is fireproof
     */
    public boolean isFireproof() {
        return fireproof;
    }

    /**
     * Changes the fireproof ability of the zombie.
     * 
     * @param tf Whether or not the zombie should be fireproof
     */
    public void setFireproof(boolean tf) {
        fireproof = tf;
    }

    private void setDefaults() {
        setHitAbsorption(game.getLevel() / Math.sqrt(5 * game.getLevel()));// less than a hellhound
        setHitBox(AblockalypseUtility.createZombieHitBox(this));
        fireproof = true;
    }
}
