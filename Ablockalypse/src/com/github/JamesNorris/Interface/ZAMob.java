package com.github.JamesNorris.Interface;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.JamesNorris.Threading.MobTargettingThread;

public interface ZAMob {
	/**
	 * Gets the game this mob is in.
	 * 
	 * @return The game the mob is in.
	 */
	public ZAGame getGame();

	/**
	 * Gets the speed of the entity.
	 * 
	 * @return The speed of the entity as a double
	 */
	public double getSpeed();

	/**
	 * Gets the target of the mob.
	 * 
	 * @return The mobs' target as a player
	 */
	public Player getTargetPlayer();

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
	 * Gets the world this mob is located in.
	 * 
	 * @return The world the mob is located in
	 */
	public World getWorld();

	/**
	 * Kills the undead and finalized the instance.
	 */
	public void kill();

	/**
	 * Sets the zombie health. Mostly used for increasing health through the levels.
	 * 
	 * @param amt The amount of health to give to the zombie
	 */
	public void setHealth(int amt);

	/**
	 * Sets the speed of the entity.
	 * 
	 * @param speed The speed to set the entity to
	 */
	public void setSpeed(float speed);

	/**
	 * Sets the target of this instance.
	 * 
	 * @param p The player to target
	 */
	public void setTargetPlayer(Player p);

	/**
	 * Sets the target of this instance.
	 * 
	 * @param loc The location to target
	 */
	public void setTargetLocation(Location loc);

	/**
	 * Gets the Entity instance of the mob.
	 * 
	 * @return The Entity associated with this instance
	 */
	public Entity getEntity();
}
