package com.github.JamesNorris.Interface;

import org.bukkit.entity.Zombie;

public interface Undead extends ZAMob {
    /**
     * Gets the zombie associated with this instance.
     */
    public Zombie getZombie();

    /**
     * Checks whether or not the zombies is fireproof.
     * 
     * @return Whether or not the zombie is fireproof
     */
    public boolean isFireproof();

    /**
     * Changes the fireproof ability of the zombie.
     * 
     * @param tf Whether or not the zombie should be fireproof
     */
    public void setFireproof(boolean tf);
}
