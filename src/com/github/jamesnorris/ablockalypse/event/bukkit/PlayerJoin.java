package com.github.jamesnorris.ablockalypse.event.bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.jamesnorris.ablockalypse.aspect.Game;
import com.github.jamesnorris.ablockalypse.aspect.PlayerState;
import com.github.jamesnorris.ablockalypse.aspect.ZAPlayer;
import com.github.jamesnorris.ablockalypse.enumerated.PlayerStatus;
import com.github.jamesnorris.ablockalypse.threading.inherent.RespawnTask;

public class PlayerJoin implements Listener {
    public static HashMap<String, Integer> gameLevels = new HashMap<String, Integer>();
    private static HashMap<String, ZAPlayer> offlinePlayers = new HashMap<String, ZAPlayer>();
    private static HashMap<String, Location> spawnLocations = new HashMap<String, Location>();
    private static HashMap<String, Map<String, Object>> playerSavings = new HashMap<String, Map<String, Object>>();
    public static List<PlayerState> toLoad = new ArrayList<PlayerState>();

    public static List<ZAPlayer> getQueues(Game game) {
        List<ZAPlayer> queues = new ArrayList<ZAPlayer>();
        for (String name : offlinePlayers.keySet()) {
            ZAPlayer zap = offlinePlayers.get(name);
            if (zap.getGame().getName().equals(game.getName())) {
                queues.add(zap);
            }
        }
        return queues;
    }

    public static boolean isQueued(ZAPlayer offlineZAPlayer) {
        return offlinePlayers.containsKey(offlineZAPlayer.getState().getSave().get("name"));
    }

    public static void queuePlayer(ZAPlayer offlineZAPlayer, Location spawn, Map<String, Object> savings) {
        if (offlineZAPlayer.hasBeenSentIntoGame()) {
            return;
        }
        Map<String, Object> save = offlineZAPlayer.getState().getSave();
        offlinePlayers.put((String) save.get("name"), offlineZAPlayer);
        spawnLocations.put((String) save.get("name"), spawn);
        playerSavings.put((String) save.get("name"), savings);
    }

    /* Called when a player joins the server.
     * Used mainly for loading game data if it has not already been loaded. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PJE(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        for (PlayerState state : toLoad) {
            if (state.getPlayer().getPlayer().getUniqueId().equals(p.getUniqueId())) {
                state.update();
                toLoad.remove(state);
                break;
            }
        }
        if (offlinePlayers.containsKey(p.getName()) && spawnLocations.containsKey(p.getName())) {
            ZAPlayer zap = offlinePlayers.get(p.getName());
            zap.loadSavedVersion(playerSavings.get(p.getName()));
            zap.setStatus(PlayerStatus.LIMBO);
            zap.getState().update();
            RespawnTask rt = new RespawnTask(zap.getPlayer(), 5, true, false);
            rt.setSpawnLocation(spawnLocations.get(p.getName()));
            offlinePlayers.remove(p.getName());
            spawnLocations.remove(p.getName());
            playerSavings.remove(p.getName());
        }
    }
}
