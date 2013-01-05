package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.github.Ablockalypse;
import com.github.JamesNorris.DataManipulator;

public class BlockBreak implements Listener {
	/*
	 * Called when a player breaks a block.
	 * Mainly used for preventing ZA Players from breaking blocks while in-game.
	 */
	@EventHandler(priority = EventPriority.HIGHEST) public void BBE(BlockBreakEvent event) {
		Player p = event.getPlayer();
		Ablockalypse.getData();
		if (DataManipulator.data.playerExists(p))
			event.setCancelled(true);
	}
}
