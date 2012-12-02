package com.github.JamesNorris.Event.Bukkit;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Data.PerPlayerDataStorage;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Implementation.ZAPlayerBase;

public class PlayerJoin implements Listener {
	public static HashMap<String, PerPlayerDataStorage> offlinePlayers = new HashMap<String, PerPlayerDataStorage>();
	public static HashMap<String, Integer> gameLevels = new HashMap<String, Integer>();

	/*
	 * Called when a player joins the server.
	 * Used mainly for loading game data if it has not already been loaded.
	 */
	@EventHandler public void PJE(PlayerJoinEvent event) {// TODO
		Player p = event.getPlayer();
		String name = p.getName();
		if (offlinePlayers.containsKey(name)) {
			PerPlayerDataStorage spds = offlinePlayers.get(name);
			if (!GlobalData.playerExists(p))
				new ZAPlayerBase(p, GlobalData.findGame(spds.getGameName()));
			if (GlobalData.playerExists(p)) {
				ZAPlayerBase zap = (ZAPlayerBase) GlobalData.getZAPlayer(p);
				ZAGameBase zag = (ZAGameBase) GlobalData.findGame(spds.getGameName());
				if (zag.getLevel() < spds.getGameLevel()) {
					zag.setLevel(spds.getGameLevel());
					spds.loadToPlayer(zap);
				}
			}
		}
	}
}
