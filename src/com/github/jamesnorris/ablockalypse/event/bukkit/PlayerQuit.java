package com.github.jamesnorris.ablockalypse.event.bukkit;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.entity.ZAPlayer;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.PlayerState;
import com.github.jamesnorris.ablockalypse.event.PlayerLeaveGameEvent;

public class PlayerQuit implements Listener {
    private DataContainer data = Ablockalypse.getData();
    public static Map<Player, PlayerState> playerSaves = new HashMap<Player, PlayerState>();

    /* Called when a player leaves the server.
     * Used for removing a player from the ZAGame when they leave. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PQE(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (data.isZAPlayer(p)) {
            ZAPlayer zap = data.getZAPlayer(p);
            Game zag = zap.getGame();
            PlayerLeaveGameEvent GPLE = new PlayerLeaveGameEvent(zap, zag);
            Bukkit.getPluginManager().callEvent(GPLE);
            if (!GPLE.isCancelled()) {
                playerSaves.put(p, zap.getState());
                zag.removePlayer(p);
                zap.remove();
            }
        }
    }
}
