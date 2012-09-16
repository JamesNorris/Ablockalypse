package com.github.Ablockalypse.JamesNorris.Event;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.Ablockalypse.JamesNorris.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data;
import com.github.Ablockalypse.JamesNorris.Util.External;

public class BlockPlace implements Listener {
	private ConfigurationData cd;

	/*
	 * Called when a player places a block.
	 * Used mainly for avoiding unwanted players from placing ZASigns.
	 */
	@EventHandler public void BPE(BlockPlaceEvent event) {
		if (cd == null)
			cd = External.cd;
		Player p = event.getPlayer();
		Block b = event.getBlock();
		if (b instanceof Sign) {
			Sign s = (Sign) b;
			if (Data.players.containsKey(p) && !p.hasPermission("za.sign") && s.getLine(1).equalsIgnoreCase(cd.first)) {
				event.setCancelled(true);
				p.sendMessage(ChatColor.RED + "You do not have permissions to place ZA signs!");
				return;
			}
		}
	}
}
