package com.github.JamesNorris.Interface;

public interface ZALiving {
	/**
	 * Gets the game the entity is currently in
	 * 
	 * @return The game the entity is in
	 */
	public ZAGame getGame();

	/**
	 * Gets the hit damage that can be absorbed by this entity.
	 * 
	 * @return The amount of damage to be absorbed each time this entity is hit
	 */
	public int getHitAbsorption();

	/**
	 * Sets the amount of damage that the entity can absorb each hit, before it hurts the entity.
	 * NOTE: If this nulls out the damage, the damage will automatically be set to 1.
	 * 
	 * @param i The damage absorption of this entity
	 */
	public void setHitAbsorption(int i);
}
