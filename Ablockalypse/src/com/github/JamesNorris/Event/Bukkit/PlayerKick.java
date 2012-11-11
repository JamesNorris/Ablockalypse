package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Event.GamePlayerLeaveEvent;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAPlayer;

public class PlayerKick implements Listener {
	/*
	 * Called when a player is kicked from the game.
	 * Usedx mostly to prevent multiple level gains after a player is kicked.
	 */
	@EventHandler public void PKE(PlayerKickEvent event) {
		Player p = event.getPlayer();
		if (Data.playerExists(p)) {
			ZAPlayer zap = Data.getZAPlayer(p);
			ZAGame zag = zap.getGame();
			GamePlayerLeaveEvent GPLE = new GamePlayerLeaveEvent(zap, zag);
			Bukkit.getPluginManager().callEvent(GPLE);
			if (!GPLE.isCancelled())
				zag.removePlayer(p);
		}
	}
}
