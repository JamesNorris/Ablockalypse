package com.github.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.behavior.Blinkable;
import com.github.behavior.GameObject;
import com.github.behavior.MapDatable;
import com.github.enumerated.Setting;
import com.github.enumerated.ZAEffect;
import com.github.threading.inherent.BlinkerThread;
import com.github.threading.inherent.LinkedTeleporterEffectThread;
import com.github.utility.MiscUtil;
import com.github.utility.serial.SavedVersion;
import com.github.utility.serial.SerialLocation;

public class Mainframe extends PermanentAspect implements GameObject, Blinkable, MapDatable {
    private BlinkerThread bt;
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private ArrayList<Location> linked = new ArrayList<Location>();
    private Location location;
    private LinkedTeleporterEffectThread ltet;
    private final UUID uuid = UUID.randomUUID();

    public Mainframe(Game game, Location loc) {
        this.game = game;
        location = loc;
        data.objects.add(this);
        game.addObject(this);
        ltet = new LinkedTeleporterEffectThread(this, 60, new ZAEffect[] {ZAEffect.TELEPORTATION, ZAEffect.FLAMES}, true);
        initBlinker();
    }

    public Mainframe(SavedVersion savings) {
        this(Ablockalypse.getData().getGame((String) savings.get("game_name"), true), SerialLocation.returnLocation((SerialLocation) savings.get("location")));
        @SuppressWarnings("unchecked") List<SerialLocation> serialLinkedLocations = (List<SerialLocation>) savings.get("linked_teleporter_locations");
        for (SerialLocation serialLink : serialLinkedLocations) {
            linked.add(SerialLocation.returnLocation(serialLink));
        }
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

    @Override public String getHeader() {
        return this.getClass().getSimpleName() + " <UUID: " + getUUID() + ">";
    }

    public ArrayList<Location> getLinks() {
        return linked;
    }

    public Location getLocation() {
        return location;
    }

    @Override public Location getPointClosestToOrigin() {
        return location;
    }

    @Override public SavedVersion getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("game_name", game.getName());
        List<SerialLocation> serialLinkedLocations = new ArrayList<SerialLocation>();
        for (Location link : linked) {
            serialLinkedLocations.add(new SerialLocation(link));
        }
        savings.put("linked_teleporter_locations", serialLinkedLocations);
        savings.put("location", location == null ? null : new SerialLocation(location));
        return new SavedVersion(getHeader(), savings, getClass());
    }

    @Override public UUID getUUID() {
        return uuid;
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

    @Override public void paste(Location pointClosestToOrigin) {
        location = pointClosestToOrigin;
        bt.remove();
        initBlinker();
    }

    @Override public void remove() {
        setBlinking(false);
        ltet.remove();
        bt.remove();
        game.removeObject(this);
        data.objects.remove(bt);
        data.objects.remove(this);
        game = null;
    }

    @Override public void setBlinking(boolean tf) {
        bt.setRunThrough(tf);
    }

    private void initBlinker() {
        ArrayList<Block> blockArray = new ArrayList<Block>();
        blockArray.add(location.getBlock());
        boolean blinkers = (Boolean) Setting.BLINKERS.getSetting();
        bt = new BlinkerThread(blockArray, DyeColor.BLUE, blinkers, 30, this);
    }
}
