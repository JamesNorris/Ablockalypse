package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Data.LocalizationData;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.Enumerated.ZAEffect;

public class BlockPlace implements Listener {
	private LocalizationData ld;

	public BlockPlace() {
		ld = External.ym.getLocalizationData();
	}

	/*
	 * Called when a player places a block.
	 * Used mainly for avoiding unwanted players from placing ZASigns.
	 */
	@EventHandler public void BPE(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();
		if (b instanceof Sign) {
			Sign s = (Sign) b;
			if (Data.players.containsKey(p) && !p.hasPermission("za.sign") && s.getLine(1).equalsIgnoreCase(ld.first)) {
				event.setCancelled(true);
				EffectUtil.generateEffect(p, ZAEffect.FLAMES);
				p.sendMessage(ChatColor.RED + "You do not have permissions to place ZA signs!");
				return;
			}
		}
	}
}
