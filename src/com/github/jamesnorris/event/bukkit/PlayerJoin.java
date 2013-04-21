package com.github.jamesnorris.event.bukkit;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.storage.PerPlayerDataStorage;

public class PlayerJoin implements Listener {
    private DataContainer data = DataContainer.data;
    public static HashMap<String, Integer> gameLevels = new HashMap<String, Integer>();
    public static HashMap<String, PerPlayerDataStorage> offlinePlayers = new HashMap<String, PerPlayerDataStorage>();

    /*
     * Called when a player joins the server.
     * Used mainly for loading game data if it has not already been loaded.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void PJE(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        String name = p.getName();
        if (offlinePlayers.containsKey(name)) {
            PerPlayerDataStorage spds = offlinePlayers.get(name);
            if (!data.playerExists(p))
                new ZAPlayer(p, data.getGame(spds.getGameName(), true));
            if (data.playerExists(p)) {
                ZAPlayer zap = (ZAPlayer) data.getZAPlayer(p);
                Game zag = (Game) data.getGame(spds.getGameName(), true);
                if (zag.getLevel() < spds.getGameLevel()) {
                    zag.setLevel(spds.getGameLevel());
                    spds.loadToPlayer(zap);
                }
            }
        }
    }
}
