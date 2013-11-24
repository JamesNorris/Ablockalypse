package com.github.jamesnorris.ablockalypse.event.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;

public class PlayerDropItem implements Listener {
    private DataContainer data = Ablockalypse.getData();

    /* Called when an item is dropped by a player.
     * Used mainly for disabling drops for players in games. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PDIE(PlayerDropItemEvent event) {
        if (data.isZAPlayer(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
