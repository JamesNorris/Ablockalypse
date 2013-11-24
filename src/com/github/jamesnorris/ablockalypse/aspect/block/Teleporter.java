package com.github.jamesnorris.ablockalypse.aspect.block;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.SpecificGameAspect;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;
import com.github.jamesnorris.ablockalypse.behavior.MapDatable;
import com.github.jamesnorris.ablockalypse.enumerated.Setting;
import com.github.jamesnorris.ablockalypse.enumerated.ZAEffect;
import com.github.jamesnorris.ablockalypse.threading.Task;
import com.github.jamesnorris.ablockalypse.utility.AblockalypseUtility;
import com.github.jamesnorris.ablockalypse.utility.Pathfinder;
import com.github.jamesnorris.ablockalypse.utility.serial.SavedVersion;
import com.github.jamesnorris.ablockalypse.utility.serial.SerialLocation;

public class Teleporter extends SpecificGameAspect implements MapDatable {
    public static final ZAEffect[] LINKED_EFFECTS = new ZAEffect[] {ZAEffect.TELEPORTATION, ZAEffect.FLAMES};
    public static final ZAEffect[] TELEPORT_EFFECTS = new ZAEffect[] {ZAEffect.POTION_BREAK, ZAEffect.TELEPORTATION, ZAEffect.SMOKE};
    private Game game;
    private Location location;
    private double linkTime = Double.MAX_VALUE;
    private boolean linked = false;
    private DataContainer data = Ablockalypse.getData();
    private UUID uuid = UUID.randomUUID();
    private Task warning;

    public Teleporter(Game game, Location location) {
        this(game, location, false);
    }

    public Teleporter(Game game, Location location, boolean linked) {
        super(game, location, !game.hasStarted());
        this.game = game;
        this.location = location;
        setLinked(linked);// so the warning can be instantiated
    }

    public Teleporter(SavedVersion savings) {
        this(Ablockalypse.getData().getGame((String) savings.get("game_name"), true), SerialLocation.returnLocation((SerialLocation) savings.get("location")), (Boolean) savings.get("is_linked"));
        linkTime = (Double) savings.get("link_time");
        uuid = savings.get("uuid") == null ? uuid : (UUID) savings.get("uuid");
    }

    public double getExactLinkTime() {
        calculateApproximateRequiredLinkTime();
        return linkTime;
    }

    public int getLinkTime() {
        return (int) Math.round(getExactLinkTime());
    }

    @Override public int getLoadPriority() {
        return 2;
    }

    @Override public Location getLocation() {
        return location;
    }

    @Override public Location getPointClosestToOrigin() {
        return location;
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

    public boolean isLinked() {
        return linked;
    }

    @Override public boolean isPowered() {
        return super.isPowered() || !((Boolean) Setting.TELEPORTERS_REQUIRE_POWER.getSetting());
    }

    @Override public void onGameEnd() {
        setLinked(false);
        setBlinking(true);
    }

    @Override public void onGameStart() {
        setBlinking(false);
    }

    @Override public void paste(Location pointClosestToOrigin) {
        location = pointClosestToOrigin;
        refreshBlinker();
    }

    public void playEffects(ZAEffect[] effects) {
        for (ZAEffect effect : effects) {
            effect.play(location.clone().add(0, 1.5, 0));
        }
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

    @Override public void remove() {
        if (warning != null) {
            data.objects.remove(warning);
        }
        super.remove();
    }

    public void setLinked(boolean linked) {
        this.linked = linked;
        refresh();
    }

    public void setLinkTime(double linkTime) {
        this.linkTime = linkTime;
    }

    @SuppressWarnings("deprecation") protected void calculateApproximateRequiredLinkTime() {
        linkTime = Math.sqrt(Pathfinder.calculate(location, game.getMainframe().getLocation()).getTotalHeuristic()) / 4;// 1/4 second per block
    }
}
