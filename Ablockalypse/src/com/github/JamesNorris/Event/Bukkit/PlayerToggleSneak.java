package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Implementation.GameBarrier;

public class PlayerToggleSneak implements Listener {
	/*
	 * Called when a player changes from walking to sneaking.
	 * Used mostly for repairing broken barriers.
	 */
	@EventHandler public void PTSE(PlayerToggleSneakEvent event) {
		Player p = event.getPlayer();
		if (Data.players.containsKey(p)) {
			if (Data.players.get(p).isInLastStand())
				event.setCancelled(true);
			for (GameBarrier b : Data.barrierpanels.keySet())
				if (b.isWithinRadius(p) && b.isBroken()) {
					b.fixBarrier(p);
					break;
				}
		}
	}
}
