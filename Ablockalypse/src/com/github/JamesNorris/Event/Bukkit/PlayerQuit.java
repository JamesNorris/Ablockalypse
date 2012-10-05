package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Interface.ZAGame;

public class PlayerQuit implements Listener {
	/*
	 * Called when a player leaves the server.
	 * Used for removing a player from the ZAGame when they leave.
	 */
	@EventHandler public void PQE(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if (Data.players.containsKey(p)) {
			ZAPlayerBase zap = Data.players.get(p);
			ZAGame zag = zap.getGame();
			zag.removePlayer(p);
			zap.finalize();
			return;
		}
	}
}
