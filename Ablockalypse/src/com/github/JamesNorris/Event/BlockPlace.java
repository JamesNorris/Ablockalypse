package com.github.JamesNorris.Event;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Data.LocalizationData;
import com.github.JamesNorris.Util.External;

public class BlockPlace implements Listener {
	private ConfigurationData cd;
	private LocalizationData ld;

	/*
	 * Called when a player places a block.
	 * Used mainly for avoiding unwanted players from placing ZASigns.
	 */
	@EventHandler public void BPE(BlockPlaceEvent event) {
		if (ld == null)
			ld = External.ym.getLocalizationData();
		if (cd == null)
			cd = External.ym.getConfigurationData();
		Player p = event.getPlayer();
		Block b = event.getBlock();
		if (b instanceof Sign) {
			Sign s = (Sign) b;
			if (Data.players.containsKey(p) && !p.hasPermission("za.sign") && s.getLine(1).equalsIgnoreCase(ld.first)) {
				event.setCancelled(true);
				if (cd.effects)
					b.getWorld().playEffect(b.getLocation(), Effect.EXTINGUISH, 1);
				p.sendMessage(ChatColor.RED + "You do not have permissions to place ZA signs!");
				return;
			}
		}
	}
}
