package com.github.JamesNorris.Event;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.JamesNorris.Data.Data;

public class PlayerMove implements Listener {
	private int time;

	/*
	 * Called whenever a player moves.
	 * Mostly used for preventing players from going through barriers.
	 */
	@EventHandler public void PME(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (++time <= 5 && Data.players.containsKey(p)) {
			time = 0;
			for (Location l : Data.barrierpanels.values()) {
				if (l == p.getLocation()) {
					p.sendMessage(ChatColor.GRAY + "To replace a barrier, hold SHIFT when nearby.");
					event.setCancelled(true);
				}
			}
		}
	}
}
