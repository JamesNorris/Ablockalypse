package com.github.behavior;

import java.util.ArrayList;

import org.bukkit.block.Block;

import com.github.aspect.Game;

public interface GameObject {
    /**
     * Gets the singular block that defines this object as an object.
     * 
     * @return The block assigned to this object
     */
    public Block getDefiningBlock();

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
    public Game getGame();

    /**
     * Removes the game object completely.
     */
    public void remove();
}
