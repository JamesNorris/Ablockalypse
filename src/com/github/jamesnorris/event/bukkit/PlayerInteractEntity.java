package com.github.jamesnorris.event.bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.threading.LastStandPickupThread;

public class PlayerInteractEntity extends DataManipulator implements Listener {
    /*
     * The event called when a player hits another entity.
     * 
     * Used for picking a player up out of last stand.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void PIEE(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        Entity e = event.getRightClicked();
        if (data.players.containsKey(p) && data.players.containsKey(e)) {
            ZAPlayer zap = data.players.get(e);
            ZAPlayer zap2 = data.players.get(p);
            if (zap.isInLastStand()) {
                new LastStandPickupThread(zap2, zap, 20, 5, true);
            }
        }
    }
}
