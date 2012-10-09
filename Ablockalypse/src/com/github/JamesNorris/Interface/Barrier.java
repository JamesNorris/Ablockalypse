package com.github.JamesNorris.Interface;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;

import com.github.JamesNorris.Util.Square;

public interface Barrier {
	/**
	 * Gets the radius of the barrier as an integer.
	 * 
	 * @return The radius of the barrier
	 */
	public int getRadius();

	/**
	 * Sets the radius of the barrier to be broken.
	 * 
	 * @param i The radius
	 */
	public void setRadius(int i);

	/**
	 * Checks if the entity is within the radius of the barrier.
	 * 
	 * @param e The entity to check for
	 * @return Whether or not the entity is within the radius
	 */
	public boolean withinRadius(Entity e);

	/**
	 * Gets the square surrounding this barrier for 2 blocks.
	 * 
	 * @return The barriers' surrounding square
	 */
	public Square getSquare();

	/**
	 * Gets the mob spawn location for this barrier.
	 * 
	 * @return The mob spawn location around this barrier
	 */
	public Location getSpawnLocation();

	/**
	 * Gets the game this barrier is involved in.
	 * 
	 * @return The game this barrier is attached to
	 */
	public ZAGame getGame();

	/**
	 * Slowly breaks the blocks of the barrier.
	 * 
	 * @param c The creature that is breaking the barrier
	 */
	public void breakBarrier(Creature c);

	/**
	 * Changes all blocks within the barrier to air.
	 */
	public void breakPanels();

	/**
	 * Returns the list of blocks in the barrier.
	 * 
	 * @return A list of blocks located in the barrier
	 */
	public List<Block> getBlocks();

	/**
	 * Gets the center location of the barrier.
	 * 
	 * @return The center of the barrier
	 */
	public Location getCenter();

	/**
	 * Tells whether or not the barrier has any missing fence blocks.
	 * 
	 * @return Whether or not the barrier is broken
	 */
	public boolean isBroken();

	/**
	 * Replaces all holes in the barrier.
	 */
	public void replaceBarrier();
}
