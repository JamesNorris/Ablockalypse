package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Interface.ZAGame;

public class PlayerDeath extends DataManipulator implements Listener {
	/*
	 * Called when a player is killed.
	 * 
	 * Used for respawning the player after the current level.
	 */
	@EventHandler(priority = EventPriority.HIGHEST) public void PDE(PlayerDeathEvent event) {
		Player p = event.getEntity();
		if (data.players.containsKey(p)) {
			event.getDrops().clear();
			event.setKeepLevel(true);
			ZAPlayerBase zap = data.players.get(p);
			zap.setLimbo(true);
			ZAGame zag = zap.getGame();
				p.getActivePotionEffects().clear();
				zap.setHitAbsorption(0);
			if (zag.getRemainingPlayers() > 0) {
				if (zap.isInLastStand())
					zap.toggleLastStand();
			} else
				zag.end();
		}
	}
}
