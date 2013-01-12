package com.github.JamesNorris.Event.Bukkit;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Data.PerPlayerDataStorage;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Implementation.ZAPlayerBase;

public class PlayerJoin extends DataManipulator implements Listener {
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
				new ZAPlayerBase(p, data.findGame(spds.getGameName()));
			if (data.playerExists(p)) {
				ZAPlayerBase zap = (ZAPlayerBase) data.getZAPlayer(p);
				ZAGameBase zag = (ZAGameBase) data.findGame(spds.getGameName());
				if (zag.getLevel() < spds.getGameLevel()) {
					zag.setLevel(spds.getGameLevel());
					spds.loadToPlayer(zap);
				}
			}
		}
	}
}
