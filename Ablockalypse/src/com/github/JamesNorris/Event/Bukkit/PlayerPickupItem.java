package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.github.JamesNorris.Data.GlobalData;

public class PlayerPickupItem implements Listener {
	/*
	 * Called when an item is picked up by a player.
	 * Used mainly for disabling pickups for players in games.
	 */
	@EventHandler public void PPIE(PlayerPickupItemEvent event) {
		if (GlobalData.players.containsKey(event.getPlayer()))
			event.setCancelled(true);
	}
}
