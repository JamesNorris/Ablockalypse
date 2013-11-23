package com.github.threading.inherent;

import java.util.ArrayList;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.enumerated.Setting;
import com.github.threading.RepeatingTask;

public class BlinkerTask extends RepeatingTask {
    public static void restartAll() {
        for (BlinkerTask bt : data.getObjectsOfType(BlinkerTask.class)) {
            bt.restart();
        }
    }

    private ArrayList<BlockState> blockStates = new ArrayList<BlockState>();
    private DyeColor color;
    private boolean colored = false;
    private static DataContainer data = Ablockalypse.getData();

    /**
     * Creates a new thread that makes a block blink a colored wool.
     * 
     * @param blocks The blocks to flicker
     * @param color The color to blink
     * @param autorun Whether or not to automatically run the thread
     * @param interval The delay between blinks
     * @param type The type of object that will be blinking
     */
    public BlinkerTask(ArrayList<Block> blocks, DyeColor color, int interval, boolean autorun) {
        super(interval, autorun);
        for (Block b : blocks) {
            blockStates.add(b.getState());
        }
        this.color = color;
        colored = false;
        BlinkerTask.restartAll();
    }

    @Override public void cancel() {
        revertBlocks();
        data.objects.remove(this);
    }

    public void restart() {
        setCount(0);
    }

    /**
     * Reverts the blocks to original state.
     */
    public void revertBlocks() {
        colored = false;
        for (BlockState state : blockStates) {
            state.update(true, false);
        }
    }

    /**
     * Makes the blinker blink in an alternating way.
     */
    @Override public void run() {
        if ((Boolean) Setting.BLINKERS.getSetting()) {
            if (colored) {
                revertBlocks();
            } else {
                setBlocks(Material.WOOL);
                setBlocksData(color.getWoolData());
            }
        }
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

    @Override public void setRunning(boolean running) {
        BlinkerTask.restartAll();
        revertBlocks();
        super.setRunning(running);
    }
}
