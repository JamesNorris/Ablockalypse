package com.github.Ablockalypse.JamesNorris.Implementation;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Interface.BarrierInterface;

public class Barrier implements BarrierInterface {
	private List<Block> blocks;
	private Location center;

	/**
	 * Creates a new instance of a Barrier, where center is the center of the 3x3 barrier.
	 * 
	 * @param center The center of the barrier
	 */
	public Barrier(Block center) {
		this.center = center.getLocation();
		Location l = center.getLocation();
		if (!Data.barriers.contains(l))
			Data.barriers.add(center.getLocation());
		if (!Data.gamebarriers.contains(this))
			Data.gamebarriers.add(this);
		for (BlockFace bf : new BlockFace[] {BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_WEST}) {
			Block b = center.getRelative(bf);
			if (b.getType() == Material.FENCE) {
				blocks.add(b);
				Data.barrierpanels.put(this, b.getLocation());
			}
		}
	}

	/**
	 * Changes all blocks within the barrier to air.
	 */
	@Override public void breakBarrier() {
		for (Block block : blocks) {
			blocks.remove(block);
			block.setType(Material.AIR);
			blocks.add(block);
		}
	}

	/**
	 * Returns the list of blocks in the barrier.
	 * 
	 * @return A list of blocks located in the barrier
	 */
	@Override public List<Block> getBlocks() {
		return blocks;
	}

	/**
	 * Gets the center location of the barrier.
	 * 
	 * @return The center of the barrier
	 */
	@Override public Location getCenter() {
		return center;
	}

	/**
	 * Tells whether or not the barrier has any missing fence blocks.
	 * 
	 * @return Whether or not the barrier is broken
	 */
	@Override public boolean isBroken() {
		for (Block b : blocks) {
			if (b.getType() != Material.FENCE)
				return true;
		}
		return false;
	}

	/**
	 * Replaces all holes in the barrier.
	 */
	@Override public void replaceBarrier() {
		for (Block b : blocks) {
			if (b.getType() != Material.FENCE) {
				blocks.remove(b);
				b.setType(Material.FENCE);
				blocks.add(b);
			}
		}
	}
}
