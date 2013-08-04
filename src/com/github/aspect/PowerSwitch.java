package com.github.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.behavior.Blinkable;
import com.github.behavior.GameObject;
import com.github.behavior.MapDatable;
import com.github.enumerated.Setting;
import com.github.threading.inherent.BlinkerThread;
import com.github.utility.serial.SavedVersion;
import com.github.utility.serial.SerialLocation;

public class PowerSwitch extends PermanentAspect implements GameObject, Blinkable, MapDatable {
    private BlinkerThread bt;
    private int cost = 0;
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private BlockState state;
    private Location loc;
    private boolean on = false;
    private final UUID uuid = UUID.randomUUID();

    public PowerSwitch(Game game, Location loc) {
        this(game, loc, 0);
    }

    public PowerSwitch(Game game, Location loc, int cost) {// TODO has a cost argument, but no cost is yet used.
        this.game = game;
        this.loc = loc;
        this.cost = cost;
        state = loc.getBlock().getState();
        data.objects.add(this);
        game.addObject(this);
        initBlinker();
    }

    public PowerSwitch(SavedVersion savings) {
        this(Ablockalypse.getData().getGame((String) savings.get("game_name"), true), SerialLocation.returnLocation((SerialLocation) savings.get("location")), (Integer) savings.get("cost"));
        setOn((Boolean) savings.get("is_switched_on"));
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

    @Override public String getHeader() {
        return this.getClass().getSimpleName() + " <UUID: " + getUUID() + ">";
    }

    public Location getLocation() {
        return loc;
    }

    @Override public Location getPointClosestToOrigin() {
        return loc;
    }

    @Override public SavedVersion getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("cost", cost);
        savings.put("game_name", game.getName());
        savings.put("location", loc == null ? null : new SerialLocation(loc));
        savings.put("is_switched_on", on);
        return new SavedVersion(getHeader(), savings, getClass());
    }

    @Override public UUID getUUID() {
        return uuid;
    }

    public boolean isOn() {
        return on;
    }

    public void off() {
        if (!on) {
            return;
        }
        on = false;
        state.update();
    }

    public void on() {
        if (on) {
            return;
        }
        on = true;
    }

    @Override public void paste(Location pointClosestToOrigin) {
        off();
        loc = pointClosestToOrigin;
        bt.remove();
        initBlinker();
    }

    @Override public void remove() {
        state.update();
        bt.remove();
        data.objects.remove(this);
        data.objects.remove(bt);
    }

    @Override public void setBlinking(boolean tf) {
        bt.setRunThrough(tf);
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setOn(boolean on) {
        if (on) {
            on();
        } else {
            off();
        }
    }

    public void turnOn(ZAPlayer player) {
        player.subtractPoints(cost);
        on();
    }

    private void initBlinker() {
        ArrayList<Block> blockArray = new ArrayList<Block>();
        blockArray.add(loc.getBlock());
        boolean blinkers = (Boolean) Setting.BLINKERS.getSetting();
        bt = new BlinkerThread(blockArray, DyeColor.ORANGE, blinkers, 30, this);
    }
}
