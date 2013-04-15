package com.github.jamesnorris.implementation;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.enumerated.GameObjectType;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.threading.LinkedTeleporterEffectThread;
import com.github.jamesnorris.util.MiscUtil;

public class Mainframe extends DataManipulator implements GameObject {
    private Game game;
    private Location location;
    private ArrayList<Location> linked = new ArrayList<Location>();
    private LinkedTeleporterEffectThread ltet;

    public Mainframe(Game game, Location location) {
        this.game = game;
        this.location = location;
        data.gameObjects.add(this);
        ltet = null;
    }

    public void clearLinks() {
        linked.clear();
        if (ltet != null) {
            ltet.remove();
            ltet = null;
        }
    }

    public boolean isLinked(Location location) {
        for (Location loc : linked) {
            if (MiscUtil.locationMatch(location, loc)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Location> getLinks() {
        return linked;
    }

    public void link(Location teleporter) {
        linked.add(teleporter);
        if (ltet == null)
            ltet = new LinkedTeleporterEffectThread(this, 60, new ZAEffect[] {ZAEffect.TELEPORTATION, ZAEffect.FLAMES}, true);
    }

    public Location getLocation() {
        return location;
    }

    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(location.getBlock());
        return blocks;
    }

    @Override public Game getGame() {
        return game;
    }

    @Override public void remove() {
        data.gameObjects.remove(this);

    }

    @Override public Block getDefiningBlock() {
        return location.getBlock();
    }

    @Override public GameObjectType getObjectType() {
        return GameObjectType.MAINFRAME;
    }
}
