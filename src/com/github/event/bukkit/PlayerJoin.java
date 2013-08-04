package com.github.event.bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.Ablockalypse;
import com.github.aspect.Game;
import com.github.aspect.ZAPlayer;
import com.github.behavior.ZAScheduledTask;
import com.github.enumerated.PlayerStatus;
import com.github.threading.inherent.RespawnThread;

public class PlayerJoin implements Listener {
    public static HashMap<String, Integer> gameLevels = new HashMap<String, Integer>();
    private static HashMap<String, ZAPlayer> offlinePlayers = new HashMap<String, ZAPlayer>();
    private static HashMap<String, Location> spawnLocations = new HashMap<String, Location>();

    public static boolean isQueued(ZAPlayer offlineZAPlayer) {
        return offlinePlayers.containsKey(offlineZAPlayer.getName());
    }
    
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

    public static void queuePlayer(ZAPlayer offlineZAPlayer, Location spawn) {
        if (offlineZAPlayer.hasBeenSentIntoGame()) {
            return;
        }
        offlinePlayers.put(offlineZAPlayer.getName(), offlineZAPlayer);
        spawnLocations.put(offlineZAPlayer.getName(), spawn);
    }

    /* Called when a player joins the server.
     * Used mainly for loading game data if it has not already been loaded. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PJE(PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        if (offlinePlayers.containsKey(p.getName()) && spawnLocations.containsKey(p.getName())) {
            ZAPlayer zap = offlinePlayers.get(p.getName());
            zap.loadSavedVersion();
            zap.setStatus(PlayerStatus.LIMBO);
            final RespawnThread rt = new RespawnThread(zap.getPlayer(), 5, true, false);
            rt.setSpawnLocation(spawnLocations.get(p.getName()));
            Ablockalypse.getMainThread().scheduleRepeatingTask(new ZAScheduledTask() {

                @Override public void run() {
                    if (!Ablockalypse.getData().objects.contains(rt)) {
                        offlinePlayers.remove(p.getName());
                        spawnLocations.remove(p.getName());
                    }
                }
                
            }, 1);
        }
    }
}
