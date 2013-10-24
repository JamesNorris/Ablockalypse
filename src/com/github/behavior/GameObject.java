package com.github.behavior;

import java.util.ArrayList;

import org.bukkit.block.Block;

import com.github.aspect.intelligent.Game;

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
    
    /**
     * Called when the game ends.<br>
     * This is called before any changes are made to the game.
     */
    public void onGameEnd();
    
    /**
     * Called when the game starts.<br>
     * This is called before the game begins searching for the next level (see NextLevelThread.java),
     * and before the first wave of mobs begins.
     */
    public void onGameStart();
    
    /**
     * Called when the game progresses to the next level.<br>
     * This is called after the level has changed, and all other updates have been made.
     */
    public void onNextLevel();
    
    /**
     * Called when the current level ends.<br>
     * This is called before the level has changed, and before all other updates have been made.
     */
    public void onLevelEnd();
    
    public int getLoadPriority();
}
