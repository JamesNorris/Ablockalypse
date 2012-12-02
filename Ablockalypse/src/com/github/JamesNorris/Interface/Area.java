package com.github.JamesNorris.Interface;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.github.JamesNorris.Implementation.ZAGameBase;

public interface Area {
	/**
	 * Closes the area.
	 */
	public void close();

	/**
	 * Gets a list of blocks for this area.
	 * 
	 * @return A list of blocks for this area
	 */
	public ArrayList<Block> getBlocks();

	/**
	 * Gets the blocks around the border of the Area.
	 * 
	 * @return The blocks around the border
	 */
	public ArrayList<Location> getBorderBlocks();

	/**
	 * Gets the game this area is assigned to.
	 * 
	 * @return The game this area is assigned to
	 */
	public ZAGameBase getGame();

	/**
	 * Gets a point from the area. This must be between 1 and 2.
	 * 
	 * @param i The point to get
	 * @return The location of the point
	 */
	public Location getPoint(int i);

	/**
	 * Returns if the area is purchased or not.
	 * 
	 * @return Whether or not the area has been purchased
	 */
	public boolean isOpened();

	/**
	 * Opens the area.
	 */
	public void open();

	/**
	 * Removes the area.
	 */
	public void remove();

	/**
	 * Sets the first or second location of the area.
	 * 
	 * @param loc The location to set
	 * @param n A number between 1 and 2
	 */
	public void setLocation(Location loc, int n);
}
