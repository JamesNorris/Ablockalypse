package com.github.jamesnorris.event.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.event.GamePlayerLeaveEvent;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.ZAPlayer;

public class PlayerQuit implements Listener {
    private DataContainer data = DataContainer.data;
    
    /*
     * Called when a player leaves the server.
     * Used for removing a player from the ZAGame when they leave.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void PQE(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (data.players.containsKey(p)) {
            ZAPlayer zap = data.players.get(p);
            Game zag = zap.getGame();
            GamePlayerLeaveEvent GPLE = new GamePlayerLeaveEvent(zap, zag);
            Bukkit.getPluginManager().callEvent(GPLE);
            if (!GPLE.isCancelled())
                zag.removePlayer(p);
        }
    }
}
