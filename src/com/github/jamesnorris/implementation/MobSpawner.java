package com.github.jamesnorris.implementation;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.enumerated.GameObjectType;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZAColor;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.inter.Blinkable;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.inter.ZAMob;
import com.github.jamesnorris.threading.BlinkerThread;

public class MobSpawner extends DataManipulator implements Blinkable, GameObject {// TODO annotations
    private boolean blinkers;
    private Block block;
    private BlinkerThread bt;
    private Game game;
    private Location loc;
    private int x, y, z;
    private double X, Y, Z;

    public MobSpawner(Location loc, Game game) {
        this.loc = loc;
        this.game = game;
        block = loc.getBlock();
        X = loc.getX();
        Y = loc.getY();
        Z = loc.getZ();
        x = loc.getBlockX();
        y = loc.getBlockY();
        z = loc.getBlockZ();
        data.gameObjects.add(this);
        blinkers = (Boolean) Setting.BLINKERS.getSetting();
        initBlinker();
        game.addMobSpawner(this);
    }

    private void initBlinker() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(block);
        bt = new BlinkerThread(blocks, ZAColor.BLUE, blinkers, 30, this);
    }

    @Override public BlinkerThread getBlinkerThread() {
        return bt;
    }

    public int getBlockX() {
        return x;
    }

    public int getBlockY() {
        return y;
    }

    public int getBlockZ() {
        return z;
    }

    public Block getBukkitBlock() {
        return block;
    }

    public Location getBukkitLocation() {
        return loc;
    }

    public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(block);
        return blocks;
    }

    public Game getGame() {
        return game;
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    public double getZ() {
        return Z;
    }

    public void playEffect(ZAEffect effect) {
        effect.play(loc);
    }

    @Override public void remove() {
        setBlinking(false);
        bt.remove();
        game.removeMobSpawner(this);
        data.threads.remove(bt);
        data.gameObjects.remove(this);
        game = null;
    }

    /**
     * Stops/Starts the blinker for this location.
     * 
     * @param tf Whether or not this location should blink
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

    public void setBlock(Material m) {
        block.setType(m);
    }

    public void spawn(ZAMob mob) {
        game.getSpawnManager().spawn(loc, true);
    }

    @Override public Block getDefiningBlock() {
        return block;
    }

    @Override public GameObjectType getObjectType() {
        return GameObjectType.MOB_SPAWNER;
    }
}
