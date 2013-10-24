package com.github.aspect.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.intelligent.Game;
import com.github.behavior.Blinkable;
import com.github.behavior.GameObject;
import com.github.behavior.MapDatable;
import com.github.enumerated.Setting;
import com.github.enumerated.ZAEffect;
import com.github.threading.Task;
import com.github.threading.inherent.BlinkerTask;
import com.github.utility.AblockalypseUtility;
import com.github.utility.Pathfinder;
import com.github.utility.serial.SavedVersion;
import com.github.utility.serial.SerialLocation;

public class Teleporter extends Powerable implements GameObject, Blinkable, MapDatable {
    public static final ZAEffect[] LINKED_EFFECTS = new ZAEffect[] {ZAEffect.TELEPORTATION, ZAEffect.FLAMES};
    public static final ZAEffect[] TELEPORT_EFFECTS = new ZAEffect[] {ZAEffect.POTION_BREAK, ZAEffect.TELEPORTATION, ZAEffect.SMOKE};
    private Game game;
    private Location location;
    private double linkTime = Double.MAX_VALUE;
    private boolean linked = false;
    private BlinkerTask bt;
    private DataContainer data = Ablockalypse.getData();
    private UUID uuid = UUID.randomUUID();
    private Task warning;

    public Teleporter(Game game, Location location) {
        this(game, location, false);
    }

    public Teleporter(Game game, final Location location, boolean linked) {
        super(new ArrayList<Block>() {
            private static final long serialVersionUID = 3801965797131713804L;
            {
                add(location.getBlock());
            }
        });
        this.game = game;
        this.location = location;
        data.objects.add(this);
        game.addObject(this);
        setLinked(linked);// so the warning can be instantiated
        initBlinker();
    }

    public Teleporter(SavedVersion savings) {
        this(Ablockalypse.getData().getGame((String) savings.get("game_name"), true), SerialLocation.returnLocation((SerialLocation) savings.get("location")), (Boolean) savings.get("is_linked"));
        linkTime = (Double) savings.get("link_time");
        uuid = savings.get("uuid") == null ? uuid : (UUID) savings.get("uuid");
    }

    @Override public String getHeader() {
        return this.getClass().getSimpleName() + " <UUID: " + getUUID().toString() + ">";
    }

    @Override public SavedVersion getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("uuid", getUUID());
        savings.put("game_name", game.getName());
        savings.put("is_linked", linked);
        savings.put("link_time", linkTime);
        savings.put("location", location == null ? null : new SerialLocation(location));
        return new SavedVersion(getHeader(), savings, getClass());
    }

    @Override public UUID getUUID() {
        return uuid;
    }

    private void initBlinker() {
        ArrayList<Block> blockArray = new ArrayList<Block>();
        blockArray.add(location.getBlock());
        boolean blinkers = (Boolean) Setting.BLINKERS.getSetting();
        bt = new BlinkerTask(blockArray, DyeColor.BLUE, 30, blinkers);
    }

    public void playEffects(ZAEffect[] effects) {
        for (ZAEffect effect : effects) {
            effect.play(location.clone().add(0, 1.5, 0));
        }
    }

    @SuppressWarnings("deprecation") protected void calculateApproximateRequiredLinkTime() {
        linkTime = Math.sqrt(Pathfinder.calculate(location, game.getMainframe().getLocation()).getTotalHeuristic()) / 4;// 1/4 second per block
    }

    public boolean isLinked() {
        return linked;
    }

    public void setLinked(boolean linked) {
        this.linked = linked;
        refresh();
    }
    
    public void refresh() {
        if (warning != null) {
            data.objects.remove(warning);
        }
        Teleporter mainframe = game.getMainframe();
        if (mainframe == null || !mainframe.equals(this)) {
            if (!isPowered()) {
                warning = AblockalypseUtility.scheduleNearbyWarning(location, ChatColor.GRAY + "The teleporter is not powered.", 1, 2.5, 1, 10000);
                return;
            }
            if (linked) {
                warning = AblockalypseUtility.scheduleNearbyWarning(location, ChatColor.GRAY + "Press " + ChatColor.AQUA + "SHIFT" + ChatColor.GRAY + " to teleport.", 1, 2.5, 1, 10000);
            } else {
                warning = AblockalypseUtility.scheduleNearbyWarning(location, ChatColor.GRAY + "Click on the teleporter to link it to the mainframe.", 2, 3.5, 2, 10000);
            }
        }
    }

    public Game getGame() {
        return game;
    }

    public double getExactLinkTime() {
        calculateApproximateRequiredLinkTime();
        return linkTime;
    }

    public int getLinkTime() {
        return (int) Math.round(getExactLinkTime());
    }

    public void setLinkTime(double linkTime) {
        this.linkTime = linkTime;
    }

    public Location getLocation() {
        return location;
    }

    @Override public Location getPointClosestToOrigin() {
        return location;
    }

    @Override public void paste(Location pointClosestToOrigin) {
        location = pointClosestToOrigin;
        bt.cancel();
        initBlinker();
    }

    @Override public BlinkerTask getBlinkerThread() {
        return bt;
    }

    /**
     * Stops/Starts the blinker for this barrier.
     * 
     * @param tf Whether or not this barrier should blink
     */
    @Override public void setBlinking(boolean tf) {
        bt.setRunning(tf);
    }

    @Override public Block getDefiningBlock() {
        return location.getBlock();
    }

    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(location.getBlock());
        return blocks;
    }

    @Override public void remove() {
        setBlinking(false);
        bt.cancel();
        if (warning != null) {
            data.objects.remove(warning);
        }
        game.removeObject(this);
        data.objects.remove(bt);
        data.objects.remove(this);
    }

    @Override public void onGameEnd() {
        setLinked(false);
        setBlinking(true);
    }

    @Override public void onGameStart() {
        setBlinking(false);
    }

    @Override public void onNextLevel() {}

    @Override public void onLevelEnd() {}

    @Override public boolean isPowered() {
        return super.isPowered() || !((Boolean) Setting.TELEPORTERS_REQUIRE_POWER.getSetting());
    }

    @Override public int getLoadPriority() {
        return 2;
    }
}
