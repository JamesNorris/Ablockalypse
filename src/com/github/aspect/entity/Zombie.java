package com.github.aspect.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.World;

import com.github.aspect.intelligent.Game;
import com.github.utility.AblockalypseUtility;
import com.github.utility.serial.SavedVersion;

public class Zombie extends ZAMob {
    private boolean fireproof;

    public Zombie(SavedVersion savings) {
        super(savings);
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
        setHitAbsorption(game.getLevel() / Math.sqrt(5 * game.getLevel()));// less than a hellhound
        setHitBox(AblockalypseUtility.createZombieHitBox(this));
        getTargetter().setNodesPerTick(.08D);
        fireproof = true;
    }

    @Override public SavedVersion getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.putAll(super.getSave());
        savings.put("is_fireproof", fireproof);
        return new SavedVersion(getHeader(), savings, getClass());
    }
    
    public org.bukkit.entity.Zombie getBukkitZombie() {
        return (org.bukkit.entity.Zombie) entity;
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
}
