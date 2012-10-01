package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Interface.ZAGame;

public class PlayerDeath implements Listener {
	/*
	 * Called when a player is killed.
	 * 
	 * Used for respawning the player after the current level.
	 */
	@EventHandler public void PDE(PlayerDeathEvent event) {
		Player p = event.getEntity();
		if (Data.players.containsKey(p)) {
			event.getDrops().clear();
			ZAPlayerBase zap = Data.players.get(p);
			if (!zap.isInLimbo())
				zap.toggleLimbo();
			ZAGame zag = zap.getGame();
			if (zag.getRemainingPlayers() > 0) {
				if (zap.isInLastStand())
					zap.toggleLastStand();
			} else {
				zag.endGame();
			}
		}
	}
}
