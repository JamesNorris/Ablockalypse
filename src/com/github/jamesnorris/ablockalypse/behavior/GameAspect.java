package com.github.jamesnorris.ablockalypse.behavior;

import java.util.List;

import org.bukkit.block.Block;

import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;
import com.github.jamesnorris.ablockalypse.event.GameEndEvent;

public interface GameAspect {
    /**
     * Gets the singular block that defines this aspect as just that.
     * If there are many blocks that define the aspect, and none of them can be considered "main" blocks,
     * then the first block in the list of blocks from the GameAspect method {@code getDefiningBlocks()} is returned.
     * 
     * @return The main block assigned to this aspect
     */
    public Block getDefiningBlock();

    /**
     * Gets the blocks that defines this aspect as just that.
     * 
     * @return All blocks assigned to this aspect
     */
    public List<Block> getDefiningBlocks();

    /**
     * Gets the game that this aspect is involved in.
     * 
     * @return The game that this aspect is involved in
     */
    public Game getGame();

    /**
     * Gets the load priority of this aspect.
     * The load priority is defined as an {@literal 0<X<Integer.MAX_VALUE}, where X is the load priority.
     * 1 is the first aspect loaded, while Integer.MAX_VALUE is last.
     * The load priority allows an aspect to be loaded before or after its counterparts, possibly preventing a conflict with an unloaded counterpart.
     * 
     * @return The load priority of this aspect
     */
    public int getLoadPriority();

    /**
     * Called when the game ends.<br>
     * This is called before any changes are made to the game.
     * 
     * @see {@link GameEndEvent.java}
     */
    public void onGameEnd();

    /**
     * Called when the game starts.<br>
     * This is called before the game begins searching for the next level ({@link NextLevelThread.java}),
     * and before the first wave of mobs begins.
     */
    public void onGameStart();

    /**
     * Called when the current level ends.<br>
     * This is called before the level has changed, and before all other updates have been made.
     */
    public void onLevelEnd();

    /**
     * Called when the game progresses to the next level.<br>
     * This is called after the level has changed, and all other updates have been made.
     */
    public void onNextLevel();

    /**
     * Removes the aspect from the data.objects list, which controls the use of the aspect by all objects pertaining to Ablockalypse.
     * After the method is called, it is likely that no references will remain in Ablockalypse itself, therefore it will be garbage collected
     * if no references remain otherwise.
     */
    public void remove();
}
