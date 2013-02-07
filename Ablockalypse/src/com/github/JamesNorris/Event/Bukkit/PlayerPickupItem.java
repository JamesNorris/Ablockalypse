package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.github.JamesNorris.DataManipulator;

public class PlayerPickupItem extends DataManipulator implements Listener {
    /*
     * Called when an item is picked up by a player.
     * Used mainly for disabling pickups for players in games.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void PPIE(PlayerPickupItemEvent event) {
        if (data.players.containsKey(event.getPlayer()))
            event.setCancelled(true);
    }
}
