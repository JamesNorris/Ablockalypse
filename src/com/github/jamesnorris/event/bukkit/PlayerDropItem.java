package com.github.jamesnorris.event.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.github.jamesnorris.DataContainer;

public class PlayerDropItem implements Listener {
    private DataContainer data = DataContainer.data;
    
    /*
     * Called when an item is dropped by a player.
     * Used mainly for disabling drops for players in games.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void PDIE(PlayerDropItemEvent event) {
        if (data.players.containsKey(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
