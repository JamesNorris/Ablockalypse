package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Enumerated.Local;
import com.github.JamesNorris.Enumerated.ZAEffect;
import com.github.JamesNorris.Util.EffectUtil;

public class BlockPlace extends DataManipulator implements Listener {
	/*
	 * Called when a player places a block.
	 * Used mainly for avoiding unwanted players from placing ZASigns.
	 */
	@EventHandler(priority = EventPriority.HIGHEST) public void BPE(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();
		if (b instanceof Sign) {
			Sign s = (Sign) b;
			if (data.players.containsKey(p) && !p.hasPermission("za.sign") && s.getLine(1).equalsIgnoreCase(Local.BASESTRING.getSetting())) {
				event.setCancelled(true);
				EffectUtil.generateEffect(p, ZAEffect.FLAMES);
				p.sendMessage(ChatColor.RED + "You do not have permissions to place ZA signs!");
				return;
			}
		}
	}
}
