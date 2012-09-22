package com.github.Ablockalypse.JamesNorris.Implementation;

import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.github.Ablockalypse.JamesNorris.Data.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Interface.BarrierInterface;
import com.github.Ablockalypse.JamesNorris.Util.ControlledEffect;
import com.github.Ablockalypse.JamesNorris.Util.External;

public class Barrier implements BarrierInterface {
	private List<Block> blocks;
	private final Location center;
	private final ConfigurationData cd;

	/**
	 * Creates a new instance of a Barrier, where center is the center of the 3x3 barrier.
	 * 
	 * @param center The center of the barrier
	 */
	public Barrier(final Block center) {
		this.center = center.getLocation();
		cd = External.getYamlManager().getConfigurationData();
		final Location l = center.getLocation();
		if (!Data.barriers.contains(l))
			Data.barriers.add(center.getLocation());
		if (!Data.gamebarriers.contains(this))
			Data.gamebarriers.add(this);
		for (final BlockFace bf : new BlockFace[] {BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_WEST}) {
			final Block b = center.getRelative(bf);
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
		for (final Block block : blocks) {
			blocks.remove(block);
			block.setType(Material.AIR);
			blocks.add(block);
		}
		if (cd.effects)
			new ControlledEffect(center.getWorld(), Effect.SMOKE, 3, 1, center, true);
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
		for (final Block b : blocks) {
			if (b.getType() != Material.FENCE)
				return true;
		}
		return false;
	}

	/**
	 * Replaces all holes in the barrier.
	 */
	@Override public void replaceBarrier() {
		for (final Block b : blocks) {
			if (b.getType() != Material.FENCE) {
				blocks.remove(b);
				b.setType(Material.FENCE);
				blocks.add(b);
			}
		}
	}
}
