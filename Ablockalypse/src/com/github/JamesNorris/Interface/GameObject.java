package com.github.JamesNorris.Interface;

import java.util.ArrayList;

import org.bukkit.block.Block;

public interface GameObject {
    /**
     * Gets the blocks that defines this object as an object.
     * 
     * @return The blocks assigned to this object
     */
    public ArrayList<Block> getDefiningBlocks();

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

    /**
     * Gets the type of object.
     * 
     * @return The name of this type of object
     */
    public String getType();
}
