package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.github.JamesNorris.DataManipulator;

public class PlayerDropItem extends DataManipulator implements Listener {
	/*
	 * Called when an item is dropped by a player.
	 * Used mainly for disabling drops for players in games.
	 */
	@EventHandler public void PDIE(PlayerDropItemEvent event) {
		if (data.players.containsKey(event.getPlayer()))
			event.setCancelled(true);
	}
}
