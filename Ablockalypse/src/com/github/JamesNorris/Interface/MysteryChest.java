package com.github.JamesNorris.Interface;

import org.bukkit.block.Chest;

public interface MysteryChest {
	/**
	 * Gets the chest associated with this instance.
	 * 
	 * @return The chest associated with this instance
	 */
	public Chest getChest();

	/**
	 * Randomizes the contents of the MysteryChest.
	 */
	public void randomize();
}
