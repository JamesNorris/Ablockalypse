package com.github.jamesnorris.implementation;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.GameObjectType;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZAColor;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.inter.Blinkable;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.threading.BlinkerThread;
import com.github.jamesnorris.threading.LinkedTeleporterEffectThread;
import com.github.jamesnorris.util.MiscUtil;

public class Mainframe implements GameObject, Blinkable {
    private BlinkerThread bt;
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private ArrayList<Location> linked = new ArrayList<Location>();
    private Location location;
    private LinkedTeleporterEffectThread ltet;

    public Mainframe(Game game, Location location) {
        this.game = game;
        this.location = location;
        data.gameObjects.add(this);
        ltet = null;
        game.addObject(this);
        initBlinker();
    }

    public void clearLinks() {
        linked.clear();
        if (ltet != null) {
            ltet.remove();
            ltet = null;
        }
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

    @Override public GameObjectType getObjectType() {
        return GameObjectType.MAINFRAME;
    }

    public boolean isLinked(Location location) {
        for (Location loc : linked) {
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
        data.gameObjects.remove(this);
    }

    @Override public void setBlinking(boolean tf) {
        bt.setRunThrough(tf);
    }

    private void initBlinker() {
        ArrayList<Block> blockArray = new ArrayList<Block>();
        blockArray.add(location.getBlock());
        boolean blinkers = (Boolean) Setting.BLINKERS.getSetting();
        bt = new BlinkerThread(blockArray, ZAColor.BLUE, blinkers, 30, this);
    }
}
