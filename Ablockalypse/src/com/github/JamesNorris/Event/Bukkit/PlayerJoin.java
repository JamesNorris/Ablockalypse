package com.github.JamesNorris.Event.Bukkit;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAPlayer;

public class PlayerJoin implements Listener {
	public static HashMap<String, String> offlinePlayers = new HashMap<String, String>();
	public static HashMap<String, Integer> offlinePlayerPoints = new HashMap<String, Integer>();
	public static HashMap<String, Integer> gameLevels = new HashMap<String, Integer>();

	/*
	 * Called when a player joins the server.
	 * Used mainly for loading game data if it has not already been loaded.
	 */
	@EventHandler public void PJE(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		String name = p.getName();
		if (offlinePlayers.containsKey(name)) {
			String gamename = offlinePlayers.get(name);
			ZAGame zag = Data.findGame(gamename);
			zag.addPlayer(p);
			ZAPlayer zap = Data.findZAPlayer(p, gamename);
			if (offlinePlayerPoints != null && offlinePlayerPoints.get(name) != null) {
				int i = offlinePlayerPoints.get(name);
				int current = zap.getPoints();
				if (current != 0)
					zap.subtractPoints(current);
				zap.addPoints(i);
			}
			if (zag.getPlayers().size() == 0)
				zag.setLevel(gameLevels.get(gamename));
		}
	}
}
