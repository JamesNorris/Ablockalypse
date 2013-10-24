package com.github.aspect.block;

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
import com.github.aspect.intelligent.Game;
import com.github.behavior.Blinkable;
import com.github.behavior.GameObject;
import com.github.behavior.MapDatable;
import com.github.enumerated.Setting;
import com.github.enumerated.ZAEffect;
import com.github.threading.inherent.BlinkerTask;
import com.github.utility.serial.SavedVersion;
import com.github.utility.serial.SerialLocation;

public class MobSpawner extends Powerable implements Blinkable, GameObject, MapDatable {
    private BlinkerTask bt;
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private Location loc;
    private UUID uuid = UUID.randomUUID();

    public MobSpawner(final Location loc, Game game) {
        super(new ArrayList<Block>() {
            private static final long serialVersionUID = -1904862275383939842L;
            {
                add(loc.getBlock());
            }
        });
        this.loc = loc;
        this.game = game;
        data.objects.add(this);
        game.addObject(this);
        initBlinker();
        game.addObject(this);
    }

    public MobSpawner(SavedVersion savings) {
        this(SerialLocation.returnLocation((SerialLocation) savings.get("location")), Ablockalypse.getData().getGame((String) savings.get("game_name"), true));
        uuid = savings.get("uuid") == null ? uuid : (UUID) savings.get("uuid");
    }

    @Override public BlinkerTask getBlinkerThread() {
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
        return this.getClass().getSimpleName() + " <UUID: " + getUUID().toString() + ">";
    }

    @Override public Location getPointClosestToOrigin() {
        return loc;
    }

    @Override public SavedVersion getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("uuid", getUUID());
        savings.put("is_active", super.isPowered());
        savings.put("game_name", game.getName());
        savings.put("location", loc == null ? null : new SerialLocation(loc));
        return new SavedVersion(getHeader(), savings, getClass());
    }

    @Override public UUID getUUID() {
        return uuid;
    }

    public boolean isActive() {
        return super.isPowered();
    }

    @Override public void paste(Location pointClosestToOrigin) {
        loc = pointClosestToOrigin;
        bt.cancel();
        initBlinker();
    }

    public void playEffect(ZAEffect effect) {
        effect.play(loc);
    }

    @Override public void remove() {
        setBlinking(false);
        bt.cancel();
        game.removeObject(this);
        data.objects.remove(bt);
        data.objects.remove(this);
        game = null;
    }

    public void setActive(boolean tf) {
        super.setPowered(tf);
    }

    /**
     * Stops/Starts the blinker for this barrier.
     * 
     * @param tf Whether or not this barrier should blink
     */
    @Override public void setBlinking(boolean tf) {
        bt.setRunning(tf);
    }

    public void setBlock(Material m) {
        loc.getBlock().setType(m);
    }

    private void initBlinker() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(loc.getBlock());
        bt = new BlinkerTask(blocks, DyeColor.BLUE, 30, (Boolean) Setting.BLINKERS.getSetting());
    }

    @Override public void onGameEnd() {
        setBlinking(true);
    }

    @Override public void onGameStart() {
        setBlinking(false);
    }

    @Override public void onNextLevel() {}

    @Override public void onLevelEnd() {}

    @Override public int getLoadPriority() {
        return 2;
    }
}
