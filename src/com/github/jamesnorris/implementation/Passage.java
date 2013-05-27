package com.github.jamesnorris.implementation;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.enumerated.ZASound;
import com.github.jamesnorris.implementation.serialized.SerialPassage;
import com.github.jamesnorris.inter.Blinkable;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.inter.Permadata;
import com.github.jamesnorris.inter.Permadatable;
import com.github.jamesnorris.inter.Powerable;
import com.github.jamesnorris.threading.BlinkerThread;
import com.github.jamesnorris.util.Rectangle;

public class Passage implements GameObject, Blinkable, Powerable, Permadatable {
    private BlinkerThread bt;
    private DataContainer data = Ablockalypse.getData();
    private Location loc1, loc2;
    private ArrayList<BlockState> states = new ArrayList<BlockState>();
    private boolean opened, blinkers, requiresPower = false;
    private Rectangle rectangle;
    private Game zag;

    /**
     * Creates a new Passage instance that is represented by a rectangular prism.
     * 
     * @param zag The game that should use this passage
     * @param loc1 The first corner of the rectangular prism
     * @param loc2 The second corner of the rectangular prism
     */
    public Passage(Game game, Location loc1, Location loc2) {
        data.gameObjects.add(this);
        this.loc1 = loc1;
        this.loc2 = loc2;
        this.zag = game;
        opened = false;
        data.passages.add(this);
        rectangle = new Rectangle(loc1, loc2);
        for (Location l : rectangle.getLocations()) {
            // locs.put(l, l.getBlock().getType());
            // locdata.put(l, l.getBlock().getData());
            states.add(l.getBlock().getState());
        }
        zag.addObject(this);
        initBlinker();
    }

    /**
     * Replaces the passage.
     */
    public void close() {
        for (BlockState state : states) {
            // Block b = l.getBlock();
            // b.setType(locs.get(l));
            // b.setData(locdata.get(l));
            state.update();
            ZAEffect.SMOKE.play(state.getLocation());
        }
        opened = false;
    }

    /**
     * Gets the BlinkerThread attached to this instance.
     * 
     * @return The BlinkerThread attached to this instance
     */
    @Override public BlinkerThread getBlinkerThread() {
        return bt;
    }

    /**
     * Gets a list of blocks for this passage.
     * 
     * @return A list of blocks for this passage
     */
    public ArrayList<Block> getBlocks() {
        ArrayList<Block> bls = new ArrayList<Block>();
        for (BlockState state : states) {
            bls.add(state.getBlock());
        }
        return bls;
    }

    /**
     * Gets the blocks around the border of the passage.
     * 
     * @return The blocks around the border
     */
    @SuppressWarnings("deprecation") public ArrayList<Location> getBorderBlocks() {
        return rectangle.get3DBorder();
    }

    @Override public Block getDefiningBlock() {
        return loc1.getBlock();
    }

    /**
     * Gets the blocks that defines this object as an object.
     * 
     * @return The blocks assigned to this object
     */
    @Override public ArrayList<Block> getDefiningBlocks() {
        return getBlocks();
    }

    /**
     * Gets the game this passage is assigned to.
     * 
     * @return The game this passage is assigned to
     */
    @Override public Game getGame() {
        return zag;
    }

    /**
     * Gets a point from the passage. This must be between 1 and 2.
     * 
     * @param i The point to get
     * @return The location of the point
     */
    public Location getPoint(int i) {
        Location loc = i == 1 ? loc1 : loc2;
        return loc;
    }

    /**
     * Returns if the passage is opened or not.
     * 
     * @return Whether or not the passage has been opened
     */
    public boolean isOpened() {
        return opened;
    }

    /**
     * Removes the passage.
     */
    public void open() {
        for (BlockState state : states) {
            state.getBlock().setType(Material.AIR);
            ZAEffect.SMOKE.play(state.getLocation());
        }
        opened = true;
        ZASound.AREA_BUY.play(loc1);
    }

    /**
     * Removes the passage.
     */
    @Override public void remove() {
        close();
        zag.removeObject(this);
        setBlinking(false);
        data.passages.remove(this);
        data.gameObjects.remove(this);
        data.threads.remove(bt);
        zag = null;
    }

    /**
     * Stops/Starts the blinker for this passage.
     * 
     * @param tf Whether or not this passage should blink
     */
    @Override public void setBlinking(boolean tf) {
        if (bt.isRunning()) {
            bt.remove();
        }
        if (tf) {
            if (!data.threads.contains(bt)) {
                initBlinker();
            }
            bt.setRunThrough(true);
        }
    }

    /**
     * Sets the first or second location of the passage.
     * 
     * @param loc The location to set
     * @param n A number between 1 and 2
     */
    public void setLocation(Location loc, int n) {
        if (n == 1) {
            loc1 = loc;
        } else if (n == 2) {
            loc2 = loc;
        }
        if (n == 1 || n == 2) {
            rectangle = new Rectangle(loc1, loc2);
        }
    }

    @SuppressWarnings("deprecation") private void initBlinker() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        for (Location l : rectangle.get3DBorder()) {
            blocks.add(l.getBlock());
        }
        blinkers = (Boolean) Setting.BLINKERS.getSetting();
        bt = new BlinkerThread(blocks, Color.BLUE, blinkers, 30, this);
    }

    @Override public void powerOn() {
        open();
    }

    @Override public void powerOff() {
        close();
    }

    @Override public boolean requiresPower() {
        return requiresPower;
    }

    @Override public void setRequiresPower(boolean required) {
        requiresPower = required;
        bt.setColor((required) ? Color.ORANGE : Color.BLUE);
    }

    @Override public boolean isPowered() {
        return opened;
    }

    @Override public Permadata getSerializedVersion() {
        return new SerialPassage(this);
    }

    @Override public void power(boolean power) {
        if (power) {
            open();
        } else {
            close();
        }
    }
}
