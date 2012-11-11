package com.github.JamesNorris.Interface;

public interface GameObject {
	/**
	 * Gets the game that this object is in.
	 * 
	 * @return The game that this object is in
	 */
	public ZAGame getGame();

	/**
	 * Removes the game object completely.
	 */
	public void remove();
}
