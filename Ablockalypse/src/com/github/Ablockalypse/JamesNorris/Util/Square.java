package com.github.Ablockalypse.JamesNorris.Util;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Square {
	private final int x, y, z, radius;
	private int i;
	private int j;
	private int k;
	private final World world;
	private Location loc;
	private List<Location> locs;

	/**
	 * Creates a new square, with the center being the given location,
	 * and the radius being the given radius.
	 * 
	 * @param centerLocation The center of the square
	 * @param radius The radius of the square
	 */
	public Square(final Location centerLocation, final int radius) {
		x = centerLocation.getBlockX();
		y = centerLocation.getBlockY();
		z = centerLocation.getBlockZ();
		this.radius = radius;
		world = centerLocation.getWorld();
		for (i = -radius; i <= radius; i++) {
			for (j = -radius; j <= radius; j++) {
				for (k = -radius; k <= radius; k++) {
					loc = world.getBlockAt(x + i, y + j, z + k).getLocation();
					locs.add(loc);
				}
			}
		}
	}

	/**
	 * Changes the type of all blocks in the square to the specified material.
	 * 
	 * @param material The type to change the blocks to
	 */
	public void changeType(final Material material) {
		for (final Location l : locs) {
			l.getBlock().setType(material);
		}
	}

	/**
	 * Changes the type of all blocks in a square from the specified fromMaterial,
	 * to the specified toMaterial.
	 * 
	 * @param fromMaterial The type to change from
	 * @param toMaterial The type to change to
	 */
	public void changeFromToType(final Material fromMaterial, final Material toMaterial) {
		for (final Location l : locs) {
			final Block b = l.getBlock();
			if (b.getType() == fromMaterial) {
				b.setType(toMaterial);
			}
		}
	}

	/**
	 * Checks if the square contains the defined material.
	 * 
	 * @param material The type to check for
	 * @return Whether or not the material is in the square
	 */
	public boolean contains(final Material material) {
		for (final Location l : locs) {
			if (l.getBlock().getType() == material) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets all locations within the square.
	 * 
	 * @return A list of locations within the square
	 */
	public List<Location> getLocations() {
		return locs;
	}

	/**
	 * Gets the radius of the square.
	 * 
	 * @return The radius of the square
	 */
	public int getRadius() {
		return radius;
	}
}
