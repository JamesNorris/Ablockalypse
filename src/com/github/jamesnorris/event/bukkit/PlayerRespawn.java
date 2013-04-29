package com.github.jamesnorris.event.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.threading.RespawnThread;

public class PlayerRespawn implements Listener {
    private DataContainer data = Ablockalypse.getData();

    /* Called when a player respawns.
     * Mainly used for sending the player back to the mainframe. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PRE(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        if (data.players.containsKey(p)) {
            new RespawnThread(p, 5, true);
        }
    }
}
