package com.github.jamesnorris.event.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.jamesnorris.DataContainer;

public class PlayerTeleport implements Listener {
    private DataContainer data = DataContainer.data;
    
    /*
     * Called when a player teleports from one location to the other.
     * Used mainly for preventing teleportation for ZAPlayers when an ender pearl is thrown.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void PTE(PlayerTeleportEvent event) {
        if (event.getCause() == TeleportCause.ENDER_PEARL && data.players.containsKey(event.getPlayer()))
            event.setCancelled(true);
    }
}
