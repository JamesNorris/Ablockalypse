package com.github.jamesnorris.ablockalypse.event.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;

public class PlayerPickupItem implements Listener {
    private DataContainer data = Ablockalypse.getData();

    /* Called when an item is picked up by a player.
     * Used mainly for disabling pickups for players in games. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PPIE(PlayerPickupItemEvent event) {
        if (data.isZAPlayer(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
