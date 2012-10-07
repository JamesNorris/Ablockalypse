package com.github.JamesNorris.Interface;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;

public interface Barrier {
	/**
	 * Changes all blocks within the barrier to air.
	 * 
	 * @param c The creature that is breaking the barrier
	 */
	public void breakBarrier(Creature c);

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
