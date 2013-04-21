package com.github.jamesnorris.implementation;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.GameObjectType;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZAColor;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.enumerated.ZASound;
import com.github.jamesnorris.inter.Blinkable;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.threading.BarrierBreakThread;
import com.github.jamesnorris.threading.BarrierFixThread;
import com.github.jamesnorris.threading.BlinkerThread;
import com.github.jamesnorris.util.Square;

public class Barrier implements GameObject, Blinkable {
    private DataContainer data = DataContainer.data;
    private CopyOnWriteArrayList<Block> blocks = new CopyOnWriteArrayList<Block>();
    private BlinkerThread bt;
    private Location center, spawnloc;
    private boolean correct;
    private int fixtimes;
    private Game game;
    private int hittimes;
    private int hittimesoriginal = 5, fixtimesoriginal = 3;
    private int radius, blockamt;

    /**
     * Creates a new instance of a Barrier, where center is the center of the 3x3 barrier.
     * 
     * @param center The center of the barrier
     * @param game The game to involve this barrier in
     */
    public Barrier(Block center, Game game) {
        data.gameObjects.add(this);
        this.center = center.getLocation();
        this.game = game;
        radius = 2;
        fixtimes = fixtimesoriginal;
        hittimes = hittimesoriginal;
        Random rand = new Random();
        /* finding spawnloc */
        int chance = rand.nextInt(4);
        World w = this.center.getWorld();
        int x = this.center.getBlockX();
        int y = this.center.getBlockY();
        int z = this.center.getBlockZ();
        int modX = rand.nextInt(2) + 3;
        int modZ = rand.nextInt(2) + 3;
        if (chance == 1) {
            x = x - modX;
            z = z - modZ;
        } else if (chance == 2) {
            x = x - modX;
        } else if (chance == 3) {
            z = z - modZ;
        }
        spawnloc = w.getBlockAt(x, y, z).getLocation();
        if (spawnloc == null)
            Ablockalypse.crash("A barrier has been created that doesn't have a suitable mob spawn location nearby. This could cause NullPointerExceptions in the future!", false);
        /* end finding spawnloc */
        game.addObject(this);
        Square s = new Square(center.getLocation(), 1);
        for (Location loc : s.getLocations()) {
            Block b = loc.getBlock();
            if (b != null && !b.isEmpty() && b.getType() != null && b.getType() == Material.FENCE) {
                blocks.add(b);
                ++blockamt;
            }
        }
        data.barriers.add(this);
        initBlinker();
    }

    private void initBlinker() {
        ArrayList<Block> blockArray = new ArrayList<Block>();
        blockArray.add(this.center.getBlock());
        boolean blinkers = (Boolean) Setting.BLINKERS.getSetting();
        bt = new BlinkerThread(blockArray, ZAColor.BLUE, blinkers, 30, this);
        ZAColor color = (blockamt == 9) ? ZAColor.BLUE : ZAColor.RED;
        correct = color == ZAColor.BLUE;
        bt.setColor(color);
    }

    /**
     * Slowly breaks the blocks of the barrier.
     * 
     * @param liveEntity The entityliving that is breaking the barrier
     */
    public void breakBarrier(LivingEntity liveEntity) {
        if (bt.isRunning())
            bt.remove();
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
        if (bt.isRunning())
            bt.remove();
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

    /**
     * Gets the game this barrier is involved in.
     * 
     * @return The game this barrier is attached to
     */
    @Override public Game getGame() {
        return game;
    }

    /**
     * Gets the radius of the barrier as an integer.
     * 
     * @return The radius of the barrier
     */
    public int getRadius() {
        return radius;
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
        if (center.getBlock().isEmpty())
            return true;
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
    public boolean isWithinRadius(Entity e) {
        int distance = (int) e.getLocation().distance(center);
        if (distance <= radius)
            return true;
        return false;
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
        if (bt.isRunning())
            bt.remove();
        if (tf) {
            if (!data.threads.contains(bt))
                initBlinker();
            bt.setRunThrough(true);
        }
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

    public int getFixTimes() {
        return fixtimes;
    }

    public int getFixRequirement() {
        return fixtimesoriginal;
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

    public int getHitTimes() {
        return hittimes;
    }

    public int getHitRequirement() {
        return hittimesoriginal;
    }

    /**
     * Sets the radius of the barrier to be broken.
     * 
     * @param i The radius
     */
    public void setRadius(int i) {
        radius = i;
    }

    @Override public Block getDefiningBlock() {
        return center.getBlock();
    }

    @Override public GameObjectType getObjectType() {
        return GameObjectType.BARRIER;
    }
}
