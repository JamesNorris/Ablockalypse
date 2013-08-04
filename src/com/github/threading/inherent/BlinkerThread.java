package com.github.threading.inherent;

import java.util.ArrayList;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.Game;
import com.github.behavior.GameObject;
import com.github.behavior.ZARepeatingTask;
import com.github.enumerated.Setting;

public class BlinkerThread implements ZARepeatingTask {
    private ArrayList<BlockState> blockStates = new ArrayList<BlockState>();
    private DyeColor color;
    private boolean colored = false, running, runThrough;
    private DataContainer data = Ablockalypse.getData();
    private Game game = null;
    private GameObject gameObj;
    private int interval, count = 0;
    private Object type;

    /**
     * Creates a new thread that makes a block blink a colored wool.
     * 
     * @param blocks The blocks to flicker
     * @param color The color to blink
     * @param autorun Whether or not to automatically run the thread
     * @param interval The delay between blinks
     * @param type The type of object that will be blinking
     */
    public BlinkerThread(ArrayList<Block> blocks, DyeColor color, final boolean autorun, int interval, GameObject type) {
        data.objects.add(this);
        for (Block b : blocks) {
            blockStates.add(b.getState());
        }
        this.interval = interval;
        this.color = color;
        this.type = type;
        gameObj = type;
        game = gameObj.getGame();
        colored = false;
        runThrough = autorun;
        addToThreads();
    }

    /**
     * Gets the associated type to this blinker.
     * 
     * @return The associated object
     */
    public Object getAssociate() {
        return type;
    }

    @Override public int getCount() {
        return count;
    }

    @Override public int getInterval() {
        return interval;
    }

    /**
     * Checks if the thread is currently running.
     * 
     * @return Whether or not the thread is making a block blink
     */
    public boolean isRunning() {
        return running;
    }

    @Override public void remove() {
        revertBlocks();
        runThrough = false;
        running = false;
        count = 0;
        data.objects.remove(this);
    }

    /**
     * Reverts the blocks to original state.
     */
    public void revertBlocks() {
        colored = false;
        for (BlockState state : blockStates) {
            state.update(true);
        }
    }

    /**
     * Makes the blinker blink in an alternating way.
     */
    @Override public void run() {
        if ((Boolean) Setting.BLINKERS.getSetting()) {
            running = true;
            if (game.hasStarted()) {
                if (colored) {
                    revertBlocks();
                }
                runThrough = false;
                return;
            }
            if (colored) {
                revertBlocks();
            } else {
                setBlocks(Material.WOOL);
                setBlocksData(color.getWoolData());
            }
        }
    }

    @Override public boolean runThrough() {
        return runThrough;
    }

    /**
     * Sets the material of all blocks.
     * 
     * @param m The material to set the blocks to
     */
    public void setBlocks(Material m) {
        if ((Boolean) Setting.BLINKERS.getSetting()) {
            for (BlockState state : blockStates) {
                colored = true;
                state.getBlock().setType(m);
            }
        }
    }

    /**
     * Sets the data of all blocks.
     * 
     * @param by The data of the blocks
     */
    public void setBlocksData(byte by) {
        if ((Boolean) Setting.BLINKERS.getSetting()) {
            for (BlockState state : blockStates) {
                colored = true;
                state.getBlock().setData(by);
            }
        }
    }

    /**
     * Sets the color of the blinker.
     * 
     * @param color The color to blink
     */
    public void setColor(DyeColor color) {
        this.color = color;
    }

    @Override public void setCount(int i) {
        count = i;
    }

    @Override public void setInterval(int i) {
        interval = i;
    }

    @Override public void setRunThrough(boolean tf) {
        runThrough = tf;
    }

    private synchronized void addToThreads() {
        data.objects.add(this);
    }
}
