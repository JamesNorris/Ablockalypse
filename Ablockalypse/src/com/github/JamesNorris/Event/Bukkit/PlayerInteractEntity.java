package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Implementation.ZAPlayerBase;

public class PlayerInteractEntity extends DataManipulator implements Listener {
	/*
	 * The event called when a player hits another entity.
	 * 
	 * Used for picking a player up out of last stand.
	 */
	@EventHandler public void PIEE(PlayerInteractEntityEvent event) {
		Player p = event.getPlayer();
		Entity e = event.getRightClicked();
		if (data.players.containsKey(p) && data.players.containsKey(e)) {
			ZAPlayerBase zap = data.players.get(e);
			ZAPlayerBase zap2 = data.players.get(p);
			if (zap.isInLastStand()) {
				zap.toggleLastStand();
				zap2.addPoints(cd.helppoints);
				p.sendMessage(ChatColor.GRAY + "You helped up " + zap.getName() + "!");
			}
		}
	}
}
