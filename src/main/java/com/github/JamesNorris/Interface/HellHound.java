package com.github.JamesNorris.Interface;

import org.bukkit.entity.Wolf;

public interface HellHound extends ZAMob {
    /**
     * Adds the mobspawner flames effect to the GameWolf for 1 second.
     */
    public void addFlames();

    /**
     * Gets the Wolf instance associated with this instance.
     * 
     * @return The Wolf instance associated with this instance
     */
    public Wolf getWolf();

    /**
     * Changes the wolfs' state to angry.
     * 
     * @param tf Whether or not to make the wolf aggressive
     */
    public void setAggressive(boolean tf);
}
