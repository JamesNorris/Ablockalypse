package com.github.jamesnorris.implementation;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.Block;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.implementation.serialized.SerialMainframe;
import com.github.jamesnorris.inter.Blinkable;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.inter.Permadata;
import com.github.jamesnorris.inter.Permadatable;
import com.github.jamesnorris.threading.BlinkerThread;
import com.github.jamesnorris.threading.LinkedTeleporterEffectThread;
import com.github.jamesnorris.util.MiscUtil;

public class Mainframe implements GameObject, Blinkable, Permadatable {
    private BlinkerThread bt;
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private ArrayList<Location> linked = new ArrayList<Location>();
    private Location location;
    private LinkedTeleporterEffectThread ltet;

    public Mainframe(Game game, Location loc) {
        this.game = game;
        this.location = loc;
        data.gameObjects.add(this);
        game.addObject(this);
        ltet = new LinkedTeleporterEffectThread(this, 60, new ZAEffect[] {ZAEffect.TELEPORTATION, ZAEffect.FLAMES}, true);
        initBlinker();
    }

    public void clearLinks() {
        linked.clear();
    }

    @Override public BlinkerThread getBlinkerThread() {
        return bt;
    }

    @Override public Block getDefiningBlock() {
        return location.getBlock();
    }

    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(location.getBlock());
        return blocks;
    }

    @Override public Game getGame() {
        return game;
    }

    public ArrayList<Location> getLinks() {
        return linked;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isLinked(Location location) {
        for (Location loc : getLinks()) {
            if (MiscUtil.locationMatch(location, loc)) {
                return true;
            }
        }
        return false;
    }

    public void link(Location teleporter) {
        linked.add(teleporter);
        if (ltet == null) {
            ltet = new LinkedTeleporterEffectThread(this, 60, new ZAEffect[] {ZAEffect.TELEPORTATION, ZAEffect.FLAMES}, true);
        }
    }

    @Override public void remove() {
        setBlinking(false);
        ltet.remove();
        bt.remove();
        game.removeObject(this);
        data.threads.remove(bt);
        data.gameObjects.remove(this);
        game = null;
    }

    @Override public void setBlinking(boolean tf) {
        bt.setRunThrough(tf);
    }

    private void initBlinker() {
        ArrayList<Block> blockArray = new ArrayList<Block>();
        blockArray.add(location.getBlock());
        boolean blinkers = (Boolean) Setting.BLINKERS.getSetting();
        bt = new BlinkerThread(blockArray, Color.BLUE, blinkers, 30, this);
    }

    @Override public Permadata getSerializedVersion() {
        return new SerialMainframe(this);
    }
}
