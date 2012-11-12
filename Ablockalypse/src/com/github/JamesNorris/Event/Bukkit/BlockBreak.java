package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.github.JamesNorris.Data.GlobalData;

public class BlockBreak implements Listener {
	/*
	 * Called when a player breaks a block.
	 * Mainly used for preventing ZA Players from breaking blocks while in-game.
	 */
	@EventHandler public void BBE(BlockBreakEvent event) {
		Player p = event.getPlayer();
		if (GlobalData.playerExists(p))
			event.setCancelled(true);
	}
}
