package com.github.jamesnorris.event.bukkit;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.threading.RespawnThread;

public class PlayerJoin implements Listener {
    public static HashMap<String, Integer> gameLevels = new HashMap<String, Integer>();
    public static ArrayList<ZAPlayer> offlinePlayers = new ArrayList<ZAPlayer>();
    private DataContainer data = Ablockalypse.getData();

    /* Called when a player joins the server.
     * Used mainly for loading game data if it has not already been loaded. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PJE(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (data.isZAPlayer(p) && offlinePlayers.contains(data.getZAPlayer(p))) {
            ZAPlayer zap = data.getZAPlayer(p);
            zap.setLimbo(true);
            new RespawnThread(zap.getPlayer(), 5, true);
            offlinePlayers.remove(zap);
        }
    }
}
