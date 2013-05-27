package com.github.jamesnorris.implementation;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.implementation.serialized.SerialMobSpawner;
import com.github.jamesnorris.inter.Blinkable;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.inter.Permadata;
import com.github.jamesnorris.inter.Permadatable;
import com.github.jamesnorris.inter.Powerable;
import com.github.jamesnorris.inter.ZAMob;
import com.github.jamesnorris.manager.SpawnManager;
import com.github.jamesnorris.threading.BlinkerThread;

public class MobSpawner implements Blinkable, GameObject, Powerable, Permadatable {
    private boolean blinkers, requiresPower = false, active = true;
    private Block block;
    private BlinkerThread bt;
    private DataContainer data = Ablockalypse.getData();
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
        game.addObject(this);
        blinkers = (Boolean) Setting.BLINKERS.getSetting();
        initBlinker();
        game.addObject(this);
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

    @Override public Block getDefiningBlock() {
        return block;
    }

    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(block);
        return blocks;
    }

    @Override public Game getGame() {
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
        game.removeObject(this);
        data.threads.remove(bt);
        data.gameObjects.remove(this);
        game = null;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean tf) {
        active = tf;
    }

    /**
     * Stops/Starts the blinker for this location.
     * 
     * @param tf Whether or not this location should blink
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

    public void setBlock(Material m) {
        block.setType(m);
    }

    public void spawn(ZAMob mob) {
        SpawnManager.spawn(game, loc.clone().add(0, 2, 0), true);
    }

    private void initBlinker() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(block);
        bt = new BlinkerThread(blocks, Color.BLUE, blinkers, 30, this);
    }

    @Override public void powerOn() {
        active = true;
    }

    @Override public void powerOff() {
        active = false;
    }

    @Override public boolean requiresPower() {
        return requiresPower;
    }

    @Override public void setRequiresPower(boolean required) {
        requiresPower = required;
        bt.setColor((required) ? Color.ORANGE : Color.BLUE);
    }

    @Override public boolean isPowered() {
        return active;
    }

    @Override public Permadata getSerializedVersion() {
        return new SerialMobSpawner(this);
    }

    @Override public void power(boolean power) {
        active = power;
    }
}
