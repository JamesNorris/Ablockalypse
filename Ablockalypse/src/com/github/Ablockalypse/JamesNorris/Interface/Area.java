package com.github.Ablockalypse.JamesNorris.Interface;

import org.bukkit.block.Block;

public interface Area {
	/**
	 * Returns the block that is the sign clicked to buy the area.
	 * 
	 * @return The sign of the area in Block form
	 */
	public Block getSignBlock();

	/**
	 * Returns if the area is purchased or not.
	 * 
	 * @return Whether or not the area has been purchased
	 */
	public boolean isPurchased();

	/**
	 * Returns true if the area door is wood.
	 * 
	 * @return Whether or not the area is wooden
	 */
	public boolean isWood();

	/**
	 * Removes the area.
	 */
	public void purchaseArea();

	/**
	 * Replaces the area.
	 */
	public void replaceArea();

	/**
	 * Safely closes the area on restart/stop, without changing the status of the area.
	 */
	public void safeReplace();

	/**
	 * Changes the type of the door when it is reloaded.
	 * NOTE: This will not change the physical appearance until the area is purchased and restored.
	 * 
	 * WOOD = true;
	 * IRON = false;
	 * 
	 * @param tf Whether or not to set the status of the door to wood
	 */
	public void setWood(boolean tf);
}
