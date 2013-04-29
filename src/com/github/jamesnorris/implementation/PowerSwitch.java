package com.github.jamesnorris.implementation;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.GameObjectType;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZAColor;
import com.github.jamesnorris.inter.Blinkable;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.threading.BlinkerThread;

public class PowerSwitch implements GameObject, Blinkable {
    private BlinkerThread bt;
    private int cost = 0;
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private Location loc;
    private boolean on = false;

    public PowerSwitch(Game game, Location loc, int cost) {
        this.game = game;
        this.loc = loc;
        this.cost = cost;
        data.gameObjects.add(this);
        game.addObject(this);
        initBlinker();
    }

    @Override public BlinkerThread getBlinkerThread() {
        return bt;
    }

    public int getCost() {
        return cost;
    }

    @Override public Block getDefiningBlock() {
        return loc.getBlock();
    }

    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(loc.getBlock());
        return blocks;
    }

    @Override public Game getGame() {
        return game;
    }

    public Location getLocation() {
        return loc;
    }

    @Override public GameObjectType getObjectType() {
        return GameObjectType.POWER_SWITCH;
    }

    public void off() {
        if (!on) {
            return;
        }
        on = false;
        // TODO things that require power, disallow interaction
    }

    public void on() {
        if (on) {
            return;
        }
        on = true;
        // TODO things that require power, allow interaction
    }

    @Override public void remove() {
        data.gameObjects.remove(this);
    }

    @Override public void setBlinking(boolean tf) {
        bt.setRunThrough(tf);
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void turnOn(ZAPlayer player) {
        player.subtractPoints(cost);
        on();
    }

    private void initBlinker() {
        ArrayList<Block> blockArray = new ArrayList<Block>();
        blockArray.add(loc.getBlock());
        boolean blinkers = (Boolean) Setting.BLINKERS.getSetting();
        bt = new BlinkerThread(blockArray, ZAColor.BLUE, blinkers, 30, this);
    }
}
