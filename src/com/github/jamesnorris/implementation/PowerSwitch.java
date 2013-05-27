package com.github.jamesnorris.implementation;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.Block;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.implementation.serialized.SerialPowerSwitch;
import com.github.jamesnorris.inter.Blinkable;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.inter.Permadata;
import com.github.jamesnorris.inter.Permadatable;
import com.github.jamesnorris.inter.Powerable;
import com.github.jamesnorris.threading.BlinkerThread;

public class PowerSwitch implements GameObject, Blinkable, Permadatable {
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
        data.switches.add(this);
        game.addObject(this);
        initBlinker();
    }
    
    public boolean isOn() {
        return on;
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
    
    public void setOn(boolean on) {
        if (on) {
            on();
        } else {
            off();
        }
    }

    public void off() {
        if (!on) {
            return;
        }
        for (Powerable power : game.getObjectsOfType(Powerable.class)) {
            if (power.isPowered()) {
                power.powerOff();
            }
        }
        on = false;
    }

    public void on() {
        if (on) {
            return;
        }
        for (Powerable power : game.getObjectsOfType(Powerable.class)) {
            if (power.requiresPower()) {
                power.powerOn();
            }
        }
        on = true;
    }

    @Override public void remove() {
        data.switches.remove(this);
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
        bt = new BlinkerThread(blockArray, Color.ORANGE, blinkers, 30, this);
    }

    @Override public Permadata getSerializedVersion() {
        return new SerialPowerSwitch(this);
    }
}
