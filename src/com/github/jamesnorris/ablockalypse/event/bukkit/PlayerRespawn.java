package com.github.jamesnorris.ablockalypse.event.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.threading.inherent.RespawnTask;

public class PlayerRespawn implements Listener {
    private DataContainer data = Ablockalypse.getData();

    /* Called when a player respawns.
     * Mainly used for sending the player back to the mainframe. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PRE(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        if (data.isZAPlayer(p)) {
            new RespawnTask(p, 5, true, true);
        }
    }
}
