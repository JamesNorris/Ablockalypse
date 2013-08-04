package com.github.behavior;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;

import com.github.aspect.Game;
import com.github.enumerated.GameEntityType;
import com.github.threading.inherent.MobTargettingThread;

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
     * Gets the game that this mob is in.
     * 
     * @return The game that this mob is in
     */
    public Game getGame();

    public double getHitAbsorption();

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

    public GameEntityType getType();

    /**
     * Kills the undead and finalized the instance.
     */
    public void kill();

    /**
     * Sets the zombie health. Mostly used for increasing health through the levels.
     * 
     * @param amt The amount of health to give to the zombie
     */
    public void setHealth(double amt);

    public void setHitAbsorption(double absorption);

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
}
