package com.github.JamesNorris.Interface;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public interface Undead {
	/**
	 * Gets the target of the zombie.
	 * 
	 * @return The zombies' target
	 */
	public Player getTarget();

	/**
	 * Gets the world this zombie is located in.
	 * 
	 * @return The world the zombie is located in
	 */
	public World getWorld();

	/**
	 * Gets the zombie associated with this instance.
	 */
	public Zombie getZombie();

	/**
	 * Increases the speed of the zombie.
	 * 
	 * @category breakable This is subject to break
	 */
	public void increaseSpeed();

	/**
	 * Checks if the zombie in this instance is on fire or not.
	 * 
	 * @return Whether or not the zombie is on fire
	 */
	public boolean isOnFire();

	/**
	 * Sets the zombie health. Mostly used for increasing health through the levels.
	 * 
	 * @param amt The amount of health to give to the zombie
	 */
	public void setHealth(int amt);

	/**
	 * Changes whether or not the zombie should be immune to fire.
	 */
	public void setFireProof(boolean tf);

	/**
	 * Gets the game this zombie is in.
	 * 
	 * @return The game the zombie is in.
	 */
	public ZAGame getGame();
}
