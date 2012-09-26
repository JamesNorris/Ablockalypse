package com.github.JamesNorris.Interface;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;

public interface Barrier {
	/**
	 * Changes all blocks within the barrier to air.
	 */
	public void breakBarrier();

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
