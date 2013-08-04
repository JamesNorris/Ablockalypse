package com.github.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.behavior.Blinkable;
import com.github.behavior.GameObject;
import com.github.behavior.MapDatable;
import com.github.behavior.Powerable;
import com.github.behavior.ZAMob;
import com.github.enumerated.Setting;
import com.github.enumerated.ZAEffect;
import com.github.manager.SpawnManager;
import com.github.threading.inherent.BlinkerThread;
import com.github.utility.serial.SavedVersion;
import com.github.utility.serial.SerialLocation;

public class MobSpawner extends PermanentAspect implements Blinkable, GameObject, Powerable, MapDatable {
    private boolean requiresPower = false, active = true;
    private BlinkerThread bt;
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private Location loc;
    private final UUID uuid = UUID.randomUUID();

    public MobSpawner(Location loc, Game game) {
        this.loc = loc;
        this.game = game;
        data.objects.add(this);
        game.addObject(this);
        initBlinker();
        game.addObject(this);
    }

    public MobSpawner(SavedVersion savings) {
        this(SerialLocation.returnLocation((SerialLocation) savings.get("location")), Ablockalypse.getData().getGame((String) savings.get("game_name"), true));
    }

    @Override public BlinkerThread getBlinkerThread() {
        return bt;
    }

    public Block getBukkitBlock() {
        return loc.getBlock();
    }

    public Location getBukkitLocation() {
        return loc;
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

    @Override public Location getPointClosestToOrigin() {
        return loc;
    }

    @Override public SavedVersion getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("requires_power", requiresPower);
        savings.put("is_active", active);
        savings.put("game_name", game.getName());
        savings.put("location", loc == null ? null : new SerialLocation(loc));
        return new SavedVersion(getHeader(), savings, getClass());
    }

    @Override public UUID getUUID() {
        return uuid;
    }

    public boolean isActive() {
        return active;
    }

    @Override public boolean isPowered() {
        return active;
    }

    @Override public void paste(Location pointClosestToOrigin) {
        loc = pointClosestToOrigin;
        bt.remove();
        initBlinker();
    }

    public void playEffect(ZAEffect effect) {
        effect.play(loc);
    }

    @Override public void power(boolean power) {
        active = power;
    }

    @Override public void powerOff() {
        active = false;
    }

    @Override public void powerOn() {
        active = true;
    }

    @Override public void remove() {
        setBlinking(false);
        bt.remove();
        game.removeObject(this);
        data.objects.remove(bt);
        data.objects.remove(this);
        game = null;
    }

    @Override public boolean requiresPower() {
        return requiresPower;
    }

    @Override public boolean requiresPurchaseFirst() {
        return false;
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
            if (!data.objects.contains(bt)) {
                initBlinker();
            }
            bt.setRunThrough(true);
        }
    }

    public void setBlock(Material m) {
        loc.getBlock().setType(m);
    }

    @Override public void setRequiresPower(boolean required) {
        requiresPower = required;
        bt.setColor(required ? DyeColor.ORANGE : DyeColor.BLUE);
    }

    @Override @Deprecated public void setRequiresPurchaseFirst(boolean purchase) {
        // nothing
    }

    public void spawn(ZAMob mob) {
        SpawnManager.spawn(game, loc.clone().add(0, 2, 0), true);
    }

    private void initBlinker() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(loc.getBlock());
        bt = new BlinkerThread(blocks, DyeColor.BLUE, (Boolean) Setting.BLINKERS.getSetting(), 30, this);
    }
}
