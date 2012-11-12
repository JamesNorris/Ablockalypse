package com.github.JamesNorris.Interface;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface MysteryChest extends GameObject {
	/**
	 * Gets the uses that this chest has before the mystery chest moves to another location.
	 * 
	 * @return The uses before movement
	 */
	public int getActiveUses();

	/**
	 * Gets the chest associated with this instance.
	 * 
	 * @return The chest associated with this instance
	 */
	public Object getChest();

	/**
	 * Gets the game that this MysteryChest is attached to.
	 * 
	 * @return The game that uses this chest
	 */
	@Override public ZAGame getGame();

	/**
	 * Gets the location that the chest is located at.
	 * 
	 * @return The location of the chest
	 */
	public Location getLocation();

	/**
	 * Randomizes the contents of the MysteryChest.
	 */
	public void giveRandomItem(Player p);

	/**
	 * Checks if the chest is active or not.
	 * 
	 * @return Whether or not the chest is active and can be used
	 */
	public boolean isActive();

	/**
	 * Checks if the BlinkerThread is running.
	 * 
	 * @return Whether or not the BlinkerThread is running
	 */
	public boolean isBlinking();

	/**
	 * Changes whether or not the chest will be active.
	 * 
	 * @param tf Whether or not the chest should be active
	 */
	public void setActive(boolean tf);

	/**
	 * Sets the uses before the mystery chest moves.
	 * 
	 * @param i The uses before movement
	 */
	public void setActiveUses(int i);

	/**
	 * Sets whether or not the chest should blink.
	 * 
	 * @param tf Whether or not the chest should blink
	 */
	public void setBlinking(boolean tf);
}
