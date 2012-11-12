package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.github.JamesNorris.Data.GlobalData;
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
		if (GlobalData.players.containsKey(p)) {
			event.getDrops().clear();
			event.setKeepLevel(true);
			ZAPlayerBase zap = GlobalData.players.get(p);
			zap.setLimbo(true);
			ZAGame zag = zap.getGame();
			if (zag.getRemainingPlayers() > 0) {
				if (zap.isInLastStand())
					zap.toggleLastStand();
			} else
				zag.end();
		}
	}
}
