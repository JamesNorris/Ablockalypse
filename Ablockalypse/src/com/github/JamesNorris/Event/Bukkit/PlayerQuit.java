package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Event.GamePlayerLeaveEvent;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Interface.ZAGame;

public class PlayerQuit implements Listener {
	/*
	 * Called when a player leaves the server.
	 * Used for removing a player from the ZAGame when they leave.
	 */
	@EventHandler public void PQE(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if (GlobalData.players.containsKey(p)) {
			ZAPlayerBase zap = GlobalData.players.get(p);
			ZAGame zag = zap.getGame();
			GamePlayerLeaveEvent GPLE = new GamePlayerLeaveEvent(zap, zag);
			Bukkit.getPluginManager().callEvent(GPLE);
			if (!GPLE.isCancelled())
				zag.removePlayer(p);
		}
	}
}
