package com.github.aspect.block;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.SpecificGameAspect;
import com.github.aspect.entity.ZAPlayer;
import com.github.aspect.intelligent.Game;
import com.github.event.bukkit.EntityExplode;
import com.github.threading.Task;
import com.github.threading.inherent.ClaymoreActionTask;
import com.github.utility.AblockalypseUtility;
import com.github.utility.serial.SavedVersion;
import com.github.utility.serial.SerialLocation;

public class Claymore extends SpecificGameAspect {
    private Location beamLoc = null;
    private Location location;
    private DataContainer data = Ablockalypse.getData();
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
    }

    public Claymore(SavedVersion savings) {
        this(SerialLocation.returnLocation((SerialLocation) savings.get("location")), Ablockalypse.getData().getGame((String) savings.get("game_name"), true), Ablockalypse.getData().getZAPlayer(Bukkit.getPlayer((String) savings.get("placer_name")), (String) savings.get("game_name"), true));
        uuid = savings.get("uuid") == null ? uuid : (UUID) savings.get("uuid");
    }

    public void attemptBeamPlacement(Location attempt) {
        attemptBeamPlacement(attempt, placer.getPlayer());
    }

    public void attemptBeamPlacement(Location attempt, Player player) {
        if (!attempt.getBlock().isEmpty()) {
            player.sendMessage(ChatColor.RED + "You cannot place a claymore there!");
            remove();
        } else {
            beamLoc = attempt;
            attempt.getBlock().setType(Material.REDSTONE_TORCH_ON);
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

    @Override public SavedVersion getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("uuid", getUUID());
        // savings.put("beam_location", new SerialLocation(beamLoc)); since the beam is placed in a valid location on instantiation, this is not required
        savings.put("location", location == null ? null : new SerialLocation(location));
        savings.put("game_name", game.getName());
        savings.put("placer_name", placer.getPlayer().getName());
        return new SavedVersion(getHeader(), savings, getClass());
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
        location.getBlock().setType(Material.AIR);
        if (beamLoc != null) {
            beamLoc.getBlock().setType(Material.AIR);
            if (trigger != null) {
                trigger.cancel();
            }
        }
        if (warning != null) {
            data.objects.remove(warning);
        }
        super.remove();
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
