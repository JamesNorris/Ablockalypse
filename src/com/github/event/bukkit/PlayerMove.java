package com.github.event.bukkit;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.block.Barrier;
import com.github.aspect.entity.ZAPlayer;
import com.github.enumerated.ZAPerk;
import com.github.utility.BukkitUtility;

public class PlayerMove implements Listener {
    private DataContainer data = Ablockalypse.getData();
    private HashMap<String, Double> PHDPlayers = new HashMap<String, Double>();

    public boolean isMoving(PlayerMoveEvent event) {
        return isMoving(event, 0);
    }
    
    public boolean isMoving(PlayerMoveEvent event, double threshold) {
        Location from = event.getFrom();
        Location to = event.getTo();
        boolean anyX = Math.abs(to.getX() - from.getX()) <= threshold;
        boolean upY = from.getY() - to.getY() < threshold;
        boolean anyZ = Math.abs(to.getZ() - from.getZ()) <= threshold;
        return !anyX || !upY || !anyZ;
    }

    /* Called whenever a player moves.
     * Mostly used for preventing players from going through barriers. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PME(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if (data.isZAPlayer(p)) {
            String name = p.getName();
            ZAPlayer zap = data.getZAPlayer(p);
            if (zap.getPerks().contains(ZAPerk.PHD_FLOPPER)) {
                if (from.getY() - to.getY() < 0 && PHDPlayers.containsKey(name)) {
                    PHDPlayers.put(name, from.getY());
                } else if (PHDPlayers.containsKey(name) && PHDPlayers.get(name) - to.getY() >= 2) {
                    EntityExplode.createNonBlockDestructionExplosionWithPoints(zap, to, 1F);
                    PHDPlayers.remove(name);
                }
            }
            if (data.getZAPlayer(p).isInLastStand() && isMoving(event, .2)) {//TODO test threshold on moving speed
                event.setCancelled(true);
            }
            for (Barrier gb : zap.getGame().getObjectsOfType(Barrier.class)) {
                for (Block b : gb.getBlocks()) {
                    Location l = b.getLocation();
                    if (BukkitUtility.locationMatch(l, to)) {
//                        p.teleport(from);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
