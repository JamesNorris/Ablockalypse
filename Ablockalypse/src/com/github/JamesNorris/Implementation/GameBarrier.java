package com.github.JamesNorris.Implementation;

import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.Barrier;
import com.github.JamesNorris.Util.ControlledEffect;
import com.github.JamesNorris.Util.Square;

public class GameBarrier implements Barrier {
	private List<Block> blocks;
	private ConfigurationData cd;
	private Location center;

	/**
	 * Creates a new instance of a Barrier, where center is the center of the 3x3 barrier.
	 * 
	 * @param center The center of the barrier
	 */
	public GameBarrier(Block center) {
		this.center = center.getLocation();
		cd = External.getYamlManager().getConfigurationData();
		Location l = center.getLocation();
		if (!Data.barriers.contains(l))
			Data.barriers.add(center.getLocation());
		if (!Data.gamebarriers.contains(this))
			Data.gamebarriers.add(this);
		Square s = new Square(center.getLocation(), 1);
		for (Location loc : s.getLocations()) {
			Block b = loc.getBlock();
			if (b.getType() == Material.FENCE) {
				blocks.add(b);
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
