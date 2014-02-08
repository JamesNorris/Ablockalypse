package com.github.jamesnorris.ablockalypse.aspect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.event.bukkit.EntityExplode;
import com.github.jamesnorris.ablockalypse.threading.Task;
import com.github.jamesnorris.ablockalypse.threading.inherent.ClaymoreActionTask;
import com.github.jamesnorris.ablockalypse.utility.AblockalypseUtility;
import com.github.jamesnorris.ablockalypse.utility.SerialLocation;

public class Claymore extends SpecificGameAspect {
    private Location beamLoc = null;
    private Location location;
    private Game game;
    private ZAPlayer placer;
    private ClaymoreActionTask trigger = null;
    private UUID uuid = UUID.randomUUID();
    private Task warning;

    public Claymore(Location location, Game game, ZAPlayer placer) {
        super(game, location);
        this.game = game;
        this.location = location;
        this.placer = placer;
        placeBeam();
        warning = AblockalypseUtility.scheduleNearbyWarning(location, ChatColor.GRAY + "Press " + ChatColor.AQUA + "SHIFT" + ChatColor.GRAY + " to pick up claymore.", 2, 2, 2, 10000);
        load();
    }

    public Claymore(Map<String, Object> savings) {
        this(SerialLocation.returnLocation((SerialLocation) savings.get("location")), Ablockalypse.getData().getGame((String) savings.get("game_name"), true), Ablockalypse.getData().getZAPlayer(Bukkit.getPlayer((String) savings.get("placer_name")), (String) savings.get("game_name"), true));
        uuid = savings.get("uuid") == null ? uuid : (UUID) savings.get("uuid");
    }

    public void attemptBeamPlacement(Location attempt) {
        attemptBeamPlacement(attempt, placer.getPlayer());
    }

    public void attemptBeamPlacement(Location attempt, Player player) {
        if (!attempt.getBlock().isEmpty()) {
            player.sendMessage(ChatColor.RED + "You cannot place a claymore there!");
            Ablockalypse.getExternal().getItemFileManager().giveItem(player, new ItemStack(Material.FLOWER_POT_ITEM, 1));
            remove();
        } else {
            beamLoc = attempt;
            beamLoc.getBlock().setType(Material.REDSTONE_TORCH_ON);
            if (trigger != null) {
                trigger.cancel();
            }
            trigger = new ClaymoreActionTask(this, true);
        }
    }

    public Location getBeamLocation() {
        return beamLoc;
    }

    @Override public int getLoadPriority() {
        return 2;
    }

    @Override public Location getLocation() {
        return location;
    }

    public ZAPlayer getPlacer() {
        return placer;
    }

    @Override public Map<String, Object> getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("uuid", getUUID());
        // savings.put("beam_location", new SerialLocation(beamLoc)); since the beam is placed in a valid location on instantiation, this is not required
        savings.put("location", location == null ? null : new SerialLocation(location));
        savings.put("game_name", game.getName());
        savings.put("placer_name", placer.getPlayer().getName());
        return savings;
    }

    @Override public UUID getUUID() {
        return uuid;
    }

    public boolean isWithinExplosionDistance(Location loc) {
        return loc.distanceSquared(beamLoc) <= 4;// 2 blocks (squared)
    }

    @Override public void onGameEnd() {
        remove();
    }

    @Override public void remove() {
        if (beamLoc != null) {
            beamLoc.getBlock().setType(Material.AIR);
        }
        if (trigger != null) {
            trigger.cancel();
        }
        if (warning != null) {
            warning.cancel();
        }
        super.remove();
        location.getBlock().setType(Material.AIR);
    }

    public void trigger() {
        EntityExplode.createNonBlockDestructionExplosionWithPoints(placer, location, 2F);
        remove();
    }

    private void placeBeam() {
        Player player = placer.getPlayer();
        Location pLoc = player.getLocation();
        int modX = 0, modZ = 0;
        int Xchange = location.getBlockX() - pLoc.getBlockX();
        int Zchange = location.getBlockZ() - pLoc.getBlockZ();
        if (Xchange > .3) {
            modX = 1;
        } else if (Xchange < -.3) {
            modX = -1;
        }
        if (Zchange > .3) {
            modZ = 1;
        } else if (Zchange < -.3) {
            modZ = -1;
        }
        Location attempt = location.clone().add(modX, 0, modZ);
        attemptBeamPlacement(attempt);
    }
}
