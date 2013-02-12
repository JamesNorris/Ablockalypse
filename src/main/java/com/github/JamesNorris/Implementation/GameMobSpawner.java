package com.github.JamesNorris.Implementation;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Enumerated.Setting;
import com.github.JamesNorris.Enumerated.ZAColor;
import com.github.JamesNorris.Enumerated.ZAEffect;
import com.github.JamesNorris.Interface.Blinkable;
import com.github.JamesNorris.Interface.GameObject;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZALocation;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Threading.BlinkerThread;
import com.github.JamesNorris.Util.EffectUtil;

public class GameMobSpawner extends DataManipulator implements ZALocation, Blinkable, GameObject {// TODO annotations
    private boolean blinkers;
    private Block block;
    private BlinkerThread bt;
    private ZAGame game;
    private Location loc;
    private World world;
    private int x, y, z;
    private double X, Y, Z;

    public GameMobSpawner(Location loc, ZAGame game) {
        this.loc = loc;
        this.game = game;
        block = loc.getBlock();
        world = loc.getWorld();
        X = loc.getX();
        Y = loc.getY();
        Z = loc.getZ();
        x = loc.getBlockX();
        y = loc.getBlockY();
        z = loc.getBlockZ();
        data.objects.add(this);
        blinkers = (Boolean) Setting.BLINKERS.getSetting();
        initBlinker();
        game.addMobSpawner(this);
    }

    private void initBlinker() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(block);
        bt = new BlinkerThread(blocks, ZAColor.BLUE, blinkers, 30, this);
    }

    @Override public ZALocation add(double x, double y, double z) {
        loc = loc.add(x, y, z);
        return this;
    }

    @Override public BlinkerThread getBlinkerThread() {
        return bt;
    }

    @Override public int getBlockX() {
        return x;
    }

    @Override public int getBlockY() {
        return y;
    }

    @Override public int getBlockZ() {
        return z;
    }

    @Override public Block getBukkitBlock() {
        return block;
    }

    @Override public Location getBukkitLocation() {
        return loc;
    }

    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(block);
        return blocks;
    }

    public ZAGame getGame() {
        return game;
    }

    @Override public double getX() {
        return X;
    }

    @Override public double getY() {
        return Y;
    }

    @Override public double getZ() {
        return Z;
    }

    @Override public void playEffect(ZAEffect effect) {
        EffectUtil.generateEffect(world, loc, effect);
    }

    @Override public void remove() {
        setBlinking(false);
        bt.remove();
        game.removeMobSpawner(this);
        data.blinkers.remove(bt);
        data.objects.remove(this);
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

    @Override public void setBlock(Material m) {
        block.setType(m);
    }

    @Override public void spawn(ZAMob mob) {
        game.getSpawnManager().spawn(loc, true);
    }

    @Override public ZALocation subtract(double x, double y, double z) {
        loc = loc.subtract(x, y, z);
        return this;
    }

    @Override public String getType() {
        return "GameMobSpawner";
    }

    @Override public Block getDefiningBlock() {
        return block;
    }
}
