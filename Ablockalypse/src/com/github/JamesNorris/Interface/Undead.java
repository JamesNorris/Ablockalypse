package com.github.JamesNorris.Interface;

import org.bukkit.entity.Zombie;

public interface Undead {
	/**
	 * Gets the zombie associated with this instance.
	 */
	public Zombie getZombie();

	/**
	 * Changes whether or not the zombie should be immune to fire.
	 */
	public void setFireProof(boolean tf);
}
