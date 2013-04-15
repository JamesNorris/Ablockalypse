package com.github.jamesnorris.event.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.threading.RespawnThread;

public class PlayerRespawn extends DataManipulator implements Listener {
    /*
     * Called when a player respawns.
     * Mainly used for sending the player back to the mainframe.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void PRE(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        if (data.players.containsKey(p))
            new RespawnThread(p, 5, true);
    }
}
