package com.github.jamesnorris.implementation;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.GameObjectType;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZAColor;
import com.github.jamesnorris.inter.Blinkable;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.threading.BlinkerThread;

public class PowerSwitch implements GameObject, Blinkable {
    private DataContainer data = DataContainer.data;
    private Game game;
    private Location loc;
    private int cost = 0;
    private boolean on = false;
    private BlinkerThread bt;

    public PowerSwitch(Game game, Location loc, int cost) {
        this.game = game;
        this.loc = loc;
        this.cost = cost;
        data.gameObjects.add(this);
        initBlinker();
    }
    
    private void initBlinker() {
        ArrayList<Block> blockArray = new ArrayList<Block>();
        blockArray.add(loc.getBlock());
        boolean blinkers = (Boolean) Setting.BLINKERS.getSetting();
        bt = new BlinkerThread(blockArray, ZAColor.BLUE, blinkers, 30, this);
    }
    
    public Location getLocation() {
        return loc;
    }
    
    public void setCost(int cost) {
        this.cost = cost;
    }
    
    public int getCost() {
        return cost;
    }
    
    public void turnOn(ZAPlayer player) {
        player.subtractPoints(cost);
        on();
    }

    public void on() {
        if (on) {
            return;
        }
        on = true;
        //TODO things that require power, allow interaction
    }

    public void off() {
        if (!on) {
            return;
        }
        on = false;
      //TODO things that require power, disallow interaction
    }

    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(loc.getBlock());
        return blocks;
    }

    @Override public Game getGame() {
        return game;
    }

    @Override public void remove() {
        data.gameObjects.remove(this);
    }

    @Override public Block getDefiningBlock() {
        return loc.getBlock();
    }

    @Override public GameObjectType getObjectType() {
        return GameObjectType.POWER_SWITCH;
    }

    @Override public BlinkerThread getBlinkerThread() {
        return bt;
    }

    @Override public void setBlinking(boolean tf) {
        bt.setRunThrough(tf);
    }
}
