package com.github.JamesNorris.Implementation;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.Area;

public class GameArea implements Area {
	private Block block;
	private Location location;
	private List<Location> locs;
	private boolean wood, opened;

	/**
	 * Creates a new Area instance, that can be bought for a set price.
	 * 
	 * @param block The sign directly facing the area door
	 */
	public GameArea(Block block) {
		this.block = block;
		opened = false;
		location = block.getLocation();
		for (BlockFace bf : new BlockFace[] {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST}) {
			Block b = block.getRelative(bf);
			if (b.getType() == Material.WOOD_DOOR || b.getType() == Material.IRON_DOOR) {
				if (b.getType() == Material.WOOD)
					wood = true;
				else
					wood = false;
				locs.add(b.getLocation());
			}
		}
		if (!Data.areas.containsKey(block))
			Data.areas.put(block, this);
		if (!Data.loadedareas.containsKey(location))
			Data.loadedareas.put(location, opened);
	}

	/**
	 * Returns the block that is the sign clicked to buy the area.
	 * 
	 * @return The sign of the area in Block form
	 */
	@Override public Block getSignBlock() {
		return block;
	}

	/**
	 * Returns if the area is purchased or not.
	 * 
	 * @return Whether or not the area has been purchased
	 */
	@Override public boolean isPurchased() {
		for (Location loc : locs) {
			Block b = loc.getBlock();
			if (b.getType() == Material.AIR || b.getType() == null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the area door is wood.
	 * 
	 * @return Whether or not the area is wooden
	 */
	@Override public boolean isWood() {
		return wood;
	}

	/**
	 * Removes the area.
	 */
	@Override public void purchaseArea() {
		for (Location loc : locs) {
			Block b = loc.getBlock();
			b.setType(Material.AIR);
			opened = true;
			toggleOpenedStatus();
		}
	}

	/**
	 * Replaces the area.
	 */
	@Override public void replaceArea() {
		for (Location loc : locs) {
			Block b = loc.getBlock();
			if (b.getType() == Material.AIR || b.getType() == null) {
				if (wood)
					b.setType(Material.WOOD_DOOR);
				else
					b.setType(Material.IRON_DOOR);
				opened = false;
				toggleOpenedStatus();
			}
		}
	}

	/**
	 * Safely closes the area on restart/stop, without changing the status of the area.
	 */
	@Override public void safeReplace() {
		for (Location loc : locs) {
			Block b = loc.getBlock();
			if (b.getType() == Material.AIR || b.getType() == null) {
				if (wood)
					b.setType(Material.WOOD_DOOR);
				else
					b.setType(Material.IRON_DOOR);
				opened = false;
			}
		}
	}

	/**
	 * Changes the type of the door when it is reloaded.
	 * NOTE: This will not change the physical appearance until the area is purchased and restored.
	 * 
	 * WOOD = true;
	 * IRON = false;
	 * 
	 * @param tf Whether or not to set the status of the door to wood
	 */
	@Override public void setWood(boolean tf) {
		wood = tf;
		if (Data.loadedareas.containsKey(location)) {
			Data.loadedareas.remove(location);
			Data.loadedareas.put(location, wood);
		}
	}

	/*
	 * Changes the opened status of the area.
	 * NOTE: This does not physically change the area!
	 */
	private void toggleOpenedStatus() {
		if (Data.loadedareas.containsKey(location)) {
			Data.loadedareas.remove(location);
			Data.loadedareas.put(location, opened);
		}
	}
}
