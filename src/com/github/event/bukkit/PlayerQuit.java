package com.github.event.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.Game;
import com.github.aspect.ZAPlayer;
import com.github.event.GamePlayerLeaveEvent;

public class PlayerQuit implements Listener {
    private DataContainer data = Ablockalypse.getData();

    /* Called when a player leaves the server.
     * Used for removing a player from the ZAGame when they leave. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PQE(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (data.isZAPlayer(p)) {
            ZAPlayer zap = data.getZAPlayer(p);
            Game zag = zap.getGame();
            GamePlayerLeaveEvent GPLE = new GamePlayerLeaveEvent(zap, zag);
            Bukkit.getPluginManager().callEvent(GPLE);
            if (!GPLE.isCancelled()) {
                zag.removePlayer(p);
            }
        }
    }
}
