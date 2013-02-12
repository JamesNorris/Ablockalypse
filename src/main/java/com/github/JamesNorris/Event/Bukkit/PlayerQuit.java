package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Event.GamePlayerLeaveEvent;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Interface.ZAGame;

public class PlayerQuit extends DataManipulator implements Listener {
    /*
     * Called when a player leaves the server.
     * Used for removing a player from the ZAGame when they leave.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void PQE(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (data.players.containsKey(p)) {
            ZAPlayerBase zap = data.players.get(p);
            ZAGame zag = zap.getGame();
            GamePlayerLeaveEvent GPLE = new GamePlayerLeaveEvent(zap, zag);
            Bukkit.getPluginManager().callEvent(GPLE);
            if (!GPLE.isCancelled())
                zag.removePlayer(p);
        }
    }
}
