package com.github.jamesnorris.implementation;

import java.util.ArrayList;
import java.util.UUID;

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
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.GameObjectType;
import com.github.jamesnorris.event.bukkit.EntityExplode;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.threading.ClaymoreTriggerThread;

public class Claymore implements GameObject {
    private Location beamLoc = null;
    private Block block;
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private ZAPlayer placer;
    private ClaymoreTriggerThread trigger = null;

    public Claymore(Block block, Game game, ZAPlayer placer, boolean placeBeam) {
        this.game = game;
        this.block = block;
        this.placer = placer;
        data.claymores.add(this);
        game.addObject(this);
        if (placeBeam) {
            placeBeam();
        }
    }

    @Override public Block getDefiningBlock() {
        return block;
    }

    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(block);
        return blocks;
    }

    @Override public Game getGame() {
        return game;
    }

    @Override public GameObjectType getObjectType() {
        return GameObjectType.CLAYMORE;
    }

    public ZAPlayer getPlacer() {
        return placer;
    }

    public boolean isWithinExplosionDistance(Location loc) {
        return loc.distance(beamLoc) <= 2;
    }

    @Override public void remove() {
        block.setType(Material.AIR);
        if (beamLoc != null) {
            beamLoc.getBlock().setType(Material.AIR);
            if (trigger != null) {
                trigger.remove();
            }
        }
        game.removeObject(this);
        data.claymores.remove(this);
    }

    public void trigger() {
        Location loc = block.getLocation();
        World world = loc.getWorld();
        Entity ent = world.spawnEntity(loc, EntityType.FIREBALL);
        Fireball f = (Fireball) ent;
        UUID uuid = f.getUniqueId();
        EntityExplode.preventBlockDestructionWithPoints(placer, uuid);// gives kill points when exploding and hitting a mob
        f.setDirection(new Vector(0, -world.getHighestBlockYAt(loc), 0));
        f.setYield(2);
        f.setIsIncendiary(true);
        f.setTicksLived(1);
        remove();
    }

    private void placeBeam() {
        Player player = placer.getPlayer();
        Location pLoc = player.getLocation();
        Location bLoc = block.getLocation();
        int modX = 0, modZ = 0;
        int Xchange = bLoc.getBlockX() - pLoc.getBlockX();
        int Zchange = bLoc.getBlockZ() - pLoc.getBlockZ();
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
        Location attempt = bLoc.add(modX, 0, modZ);
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
}
