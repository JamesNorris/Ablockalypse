package com.github.Ablockalypse.JamesNorris.Interface;

import org.bukkit.block.Chest;

public interface MysteryChestInterface {
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
