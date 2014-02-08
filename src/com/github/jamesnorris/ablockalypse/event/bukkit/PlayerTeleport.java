package com.github.jamesnorris.ablockalypse.event.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.ZAPlayer;

public class PlayerTeleport implements Listener {
    private DataContainer data = Ablockalypse.getData();

    /* Called when a player teleports from one location to the other.
     * Used mainly for preventing teleportation for ZAPlayers when an ender pearl is thrown. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PTE(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (data.isZAPlayer(player)) {
            ZAPlayer zap = data.getZAPlayer(player);
            if (event.getCause() == TeleportCause.ENDER_PEARL) {
                event.setCancelled(true);
            } else if (zap.isInLastStand()) {
                zap.getSeat().sit(player);
                event.setCancelled(true);
            }
        }
    }
}
