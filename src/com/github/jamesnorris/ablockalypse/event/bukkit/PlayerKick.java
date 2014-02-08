package com.github.jamesnorris.ablockalypse.event.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.Game;
import com.github.jamesnorris.ablockalypse.aspect.ZAPlayer;
import com.github.jamesnorris.ablockalypse.event.PlayerLeaveGameEvent;

public class PlayerKick implements Listener {
    private DataContainer data = Ablockalypse.getData();

    /* Called when a player is kicked from the game.
     * Used mostly to prevent multiple level gains after a player is kicked. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PKE(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (data.isZAPlayer(player)) {
            ZAPlayer zap = data.getZAPlayer(player);
            Game zag = zap.getGame();
            PlayerLeaveGameEvent GPLE = new PlayerLeaveGameEvent(zap, zag);
            Bukkit.getPluginManager().callEvent(GPLE);
            if (!GPLE.isCancelled()) {
                zag.removePlayer(player);
                zap.remove();
            }
        }
    }
}
