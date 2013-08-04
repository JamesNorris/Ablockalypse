package com.github.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.behavior.GameObject;
import com.github.event.bukkit.EntityExplode;
import com.github.threading.inherent.ClaymoreTriggerThread;
import com.github.utility.serial.SavedVersion;
import com.github.utility.serial.SerialLocation;

public class Claymore extends PermanentAspect implements GameObject {
    private Location beamLoc = null;
    private Location location;
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private ZAPlayer placer;
    private ClaymoreTriggerThread trigger = null;
    private final UUID uuid = UUID.randomUUID();

    public Claymore(Location location, Game game, ZAPlayer placer) {
        this.game = game;
        this.location = location;
        this.placer = placer;
        data.objects.add(this);
        game.addObject(this);
        placeBeam();
    }

    public Claymore(SavedVersion savings) {
        this(SerialLocation.returnLocation((SerialLocation) savings.get("location")), Ablockalypse.getData().getGame((String) savings.get("game_name"), true), Ablockalypse.getData().getZAPlayer(Bukkit.getPlayer((String) savings.get("placer_name")), (String) savings.get("game_name"), true));
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
            attempt.getBlock().setType(Material.REDSTONE_TORCH_ON);// TODO settable
            if (trigger != null) {
                trigger.remove();
            }
            trigger = new ClaymoreTriggerThread(this, 5, true);
        }
    }

    public Location getBeamLocation() {
        return beamLoc;
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

    public Location getLocation() {
        return location;
    }

    public ZAPlayer getPlacer() {
        return placer;
    }

    @Override public SavedVersion getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        // savings.put("beam_location", new SerialLocation(beamLoc)); since the beam is placed in a valid location on instantiation, this is not required
        savings.put("location", location == null ? null : new SerialLocation(location));
        savings.put("game_name", game.getName());
        savings.put("placer_name", placer.getName());
        return new SavedVersion(getHeader(), savings, getClass());
    }

    @Override public UUID getUUID() {
        return uuid;
    }

    public boolean isWithinExplosionDistance(Location loc) {
        return loc.distanceSquared(beamLoc) <= 4;// 2 blocks (squared)
    }

    @Override public void remove() {
        location.getBlock().setType(Material.AIR);
        if (beamLoc != null) {
            beamLoc.getBlock().setType(Material.AIR);
            if (trigger != null) {
                trigger.remove();
            }
        }
        game.removeObject(this);
        data.objects.remove(this);
    }

    public void trigger() {
        World world = location.getWorld();
        Entity ent = world.spawnEntity(location, EntityType.FIREBALL);
        Fireball f = (Fireball) ent;
        UUID uuid = f.getUniqueId();
        EntityExplode.preventBlockDestructionWithPoints(placer, uuid);// gives kill points when exploding and hitting a mob
        f.setDirection(new Vector(0, -world.getHighestBlockYAt(location), 0));
        f.setYield(2);
        f.setIsIncendiary(true);
        f.setTicksLived(1);
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
