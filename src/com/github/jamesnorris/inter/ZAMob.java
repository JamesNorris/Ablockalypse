package com.github.jamesnorris.inter;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;

import com.github.jamesnorris.enumerated.GameEntityType;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.threading.MobTargettingThread;

public interface ZAMob {
    /**
     * Gets the creature associated with this mob.
     * 
     * @return The creature associated with this mob
     */
    public Creature getCreature();

    /**
     * Gets the Entity instance of the mob.
     * 
     * @return The Entity associated with this instance
     */
    public Entity getEntity();

    /**
     * Gets the speed of the entity.
     * 
     * @return The speed of the entity as an integer
     */
    public double getSpeed();

    /**
     * Gets the target of the mob.
     * 
     * @return The mobs' target as a location
     */
    public Location getTargetLocation();

    /**
     * Gets the targetter for this mob.
     * 
     * @return The targetter attached to this instance
     */
    public MobTargettingThread getTargetter();

    /**
     * Kills the undead and finalized the instance.
     */
    public void kill();

    /**
     * Gets the game that this mob is in.
     * 
     * @return The game that this mob is in
     */
    public Game getGame();

    /**
     * Sets the zombie health. Mostly used for increasing health through the levels.
     * 
     * @param amt The amount of health to give to the zombie
     */
    public void setHealth(int amt);

    /**
     * Sets the speed of the entity.
     * 
     * @param nodesPerTick The speed to set the entity to
     */
    public void setSpeed(double nodesPerTick);

    /**
     * Sets the target of this instance.
     * 
     * @param loc The location to target
     */
    public void setTargetLocation(Location loc);

    public void setHitAbsorption(int absorption);

    public int getHitAbsorption();
    
    public GameEntityType getType();
}
