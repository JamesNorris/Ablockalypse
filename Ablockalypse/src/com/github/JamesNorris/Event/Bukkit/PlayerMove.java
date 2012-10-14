package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.JamesNorris.Data.Data;

public class PlayerMove implements Listener {
	/*
	 * Called whenever a player moves.
	 * Mostly used for preventing players from going through barriers.
	 */
	@EventHandler public void PME(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (Data.players.containsKey(p)) {
			if (Data.players.get(p).isInLastStand() && p.getFallDistance() <= 0 && ((event.getFrom().getPitch() - event.getTo().getPitch()) == 0) && ((event.getFrom().getYaw() - event.getTo().getYaw()) == 0))
				event.setCancelled(true);
			if (Data.barrierpanels.containsValue(event.getTo()))
				event.setCancelled(true);
		}
	}
}
