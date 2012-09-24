package com.github.Ablockalypse.JamesNorris.Interface;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

public interface HellHound {
	/**
	 * Adds the mobspawner flames effect to the GameWolf for 1 second.
	 */
	public void addEffect();

	/**
	 * Adds health to the wolf, mostly used in progressive health addition.
	 * 
	 * @param amt The amount of health to add to the wolf
	 */
	public void addHealth(int amt);

	/**
	 * Gets the Wolf instance associated with this instance.
	 * 
	 * @return The Wolf instance associated with this instance
	 */
	public Wolf getWolf();

	/**
	 * Gets the world the wolf is in.
	 * 
	 * @return The world the wolf is in
	 */
	public World getWorld();

	/**
	 * Increases the speed of the wolf.
	 */
	public void increaseSpeed();

	/**
	 * Changes the GameWolfs' state to angry.
	 * 
	 * @param tf Whether or not to make the wolf aggressive
	 */
	public void setAggressive(boolean tf);

	/**
	 * Sets the GameWolfs' target.
	 * 
	 * @param player The player to be made into the target
	 */
	public void setTarget(Player player);
}
