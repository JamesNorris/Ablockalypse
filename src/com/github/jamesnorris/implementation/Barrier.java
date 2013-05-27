package com.github.jamesnorris.implementation;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.enumerated.ZASound;
import com.github.jamesnorris.implementation.serialized.SerialBarrier;
import com.github.jamesnorris.inter.Blinkable;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.inter.Permadata;
import com.github.jamesnorris.inter.Permadatable;
import com.github.jamesnorris.threading.BarrierBreakThread;
import com.github.jamesnorris.threading.BarrierFixThread;
import com.github.jamesnorris.threading.BlinkerThread;
import com.github.jamesnorris.util.MiscUtil;
import com.github.jamesnorris.util.Square;

public class Barrier implements GameObject, Blinkable, Permadatable {
    private CopyOnWriteArrayList<Block> blocks = new CopyOnWriteArrayList<Block>();
    private BlinkerThread bt;
    private Location center, spawnloc;
    private boolean correct;
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private int hittimesoriginal = 5, fixtimesoriginal = 3, fixtimes, hittimes, blockamt;

    /**
     * Creates a new instance of a Barrier, where center is the center of the 3x3 barrier.
     * 
     * @param center The center of the barrier
     * @param game The game to involve this barrier in
     */
    public Barrier(Location center, Game game) {
        data.gameObjects.add(this);
        this.center = center;
        this.game = game;
        fixtimes = fixtimesoriginal;
        hittimes = hittimesoriginal;
        findSpawnLoc();
        if (spawnloc == null) {
            Ablockalypse.crash("A barrier has been created that doesn't have a suitable mob spawn location nearby. This could cause NullPointerExceptions in the future!", false);
        }
        game.addObject(this);
        for (Location loc : new Square(center, 1).getLocations()) {
            Block b = loc.getBlock();
            if (b != null && !b.isEmpty() && b.getType() != null && b.getType() == Material.FENCE) {
                blocks.add(b);
                ++blockamt;
            }
        }
        data.barriers.add(this);
        initBlinker();
    }

    private void findSpawnLoc() {
        spawnloc = MiscUtil.findLocationNear(this.center, 2, 4);
        if (!spawnloc.getBlock().isEmpty()) {
            findSpawnLoc();
        }
    }

    /**
     * Slowly breaks the blocks of the barrier.
     * 
     * @param liveEntity The entityliving that is breaking the barrier
     */
    public void breakBarrier(LivingEntity liveEntity) {
        if (bt.isRunning()) {
            bt.remove();
            return;
        }
        new BarrierBreakThread(this, liveEntity, 100, true);
    }

    /**
     * Changes all blocks within the barrier to air.
     */
    public void breakPanels() {
        for (Block block : blocks) {
            blocks.remove(block);
            block.setType(Material.AIR);
            ZAEffect.SMOKE.play(block.getLocation());
            blocks.add(block);
        }
    }

    /**
     * Slowly fixes the blocks of the barrier.
     * 
     * @param zap The ZAPlayer that is going to be fixing this barrier
     */
    public void fixBarrier(ZAPlayer zap) {
        if (bt.isRunning()) {
            bt.remove();
            return;
        }
        new BarrierFixThread(this, zap, 20, true);
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
     * Returns the list of blocks in the barrier.
     * 
     * @return A list of blocks located in the barrier
     */
    public CopyOnWriteArrayList<Block> getBlocks() {
        return blocks;
    }

    /**
     * Gets the center location of the barrier.
     * 
     * @return The center of the barrier
     */
    public Location getCenter() {
        return center;
    }

    @Override public Block getDefiningBlock() {
        return center.getBlock();
    }

    /**
     * Gets the blocks that defines this object as an object.
     * 
     * @return The blocks assigned to this object
     */
    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blockArray = new ArrayList<Block>();
        for (Block block : blocks) {
            blockArray.add(block);
        }
        return blockArray;
    }

    public int getFixRequirement() {
        return fixtimesoriginal;
    }

    public int getFixTimes() {
        return fixtimes;
    }

    /**
     * Gets the game this barrier is involved in.
     * 
     * @return The game this barrier is attached to
     */
    @Override public Game getGame() {
        return game;
    }

    public int getHitRequirement() {
        return hittimesoriginal;
    }

    public int getHitTimes() {
        return hittimes;
    }

    /**
     * Gets the mob spawn location for this barrier.
     * 
     * @return The mob spawn location around this barrier
     */
    public Location getSpawnLocation() {
        return spawnloc;
    }

    /**
     * Tells whether or not the barrier has any missing blocks.
     * 
     * @return Whether or not the barrier is broken
     */
    public boolean isBroken() {
        if (center.getBlock().isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the barrier is setup correctly or not.
     * 
     * @return Whether or not the barrier is setup correctly
     */
    public boolean isCorrect() {
        return correct;
    }

    /**
     * Checks if the entity is within the radius of the barrier.
     * 
     * @param e The entity to check for
     * @return Whether or not the entity is within the radius
     */
    public boolean isWithinRadius(Entity e, double radius) {
        return e.getLocation().distance(center) <= radius;
    }

    /**
     * Removes the barrier.
     */
    @Override public void remove() {
        replacePanels();
        setBlinking(false);
        game.removeObject(this);
        data.barriers.remove(this);
        data.gameObjects.remove(this);
        data.threads.remove(bt);
        game = null;
    }

    /**
     * Replaces all holes in the barrier.
     */
    public void replacePanels() {
        for (Block block : blocks) {
            blocks.remove(block);
            block.setType(Material.FENCE);
            ZAEffect.SMOKE.play(block.getLocation());
            blocks.add(block);
        }
        ZASound.BARRIER_REPAIR.play(center);
    }

    /**
     * Stops/Starts the blinker for this barrier.
     * 
     * @param tf Whether or not this barrier should blink
     */
    @Override public void setBlinking(boolean tf) {
        if (bt.isRunning()) {
            bt.remove();
            return;
        }
        if (!tf) {
            return;
        }
        if (!data.threads.contains(bt)) {
            initBlinker();
        }
        bt.setRunThrough(true);
    }

    /**
     * Sets the amount of fix rounds to wait before fixing the barrier.
     * Fix rounds are called once every 20 ticks by all sneaking players near barriers.
     * 
     * @param i The amount of fix rounds to wait
     */
    public void setFixRequirement(int i) {
        fixtimesoriginal = i;
        fixtimes = i;
    }

    public void setFixTimes(int i) {
        fixtimes = i;
    }

    /**
     * Sets the amount of hit rounds to wait before breaking the barrier.
     * Hit rounds are called once every 100 ticks by all mobs close to barriers.
     * 
     * @param i The amount of hit rounds to wait
     */
    public void setHitRequirement(int i) {
        hittimesoriginal = i;
        hittimes = i;
    }

    public void setHitTimes(int i) {
        hittimes = i;
    }

    private void initBlinker() {
        ArrayList<Block> blockArray = new ArrayList<Block>();
        blockArray.add(center.getBlock());
        boolean blinkers = (Boolean) Setting.BLINKERS.getSetting();
        bt = new BlinkerThread(blockArray, Color.BLUE, blinkers, 30, this);
        Color color = blockamt == 9 ? Color.BLUE : Color.RED;
        correct = color == Color.BLUE;
        bt.setColor(color);
    }

    @Override public Permadata getSerializedVersion() {
        return new SerialBarrier(this);
    }
}
