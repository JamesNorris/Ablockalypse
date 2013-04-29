package com.github.jamesnorris.inter;

import java.util.ArrayList;

import org.bukkit.block.Block;

import com.github.jamesnorris.enumerated.GameObjectType;
import com.github.jamesnorris.implementation.Game;

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

    public GameObjectType getObjectType();

    /**
     * Removes the game object completely.
     */
    public void remove();
}
