package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Interface.ZAPlayer;

public class PlayerMove implements Listener {
	/*
	 * Called whenever a player moves.
	 * Mostly used for preventing players from going through barriers.
	 */
	@EventHandler public void PME(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (GlobalData.players.containsKey(p)) {
			ZAPlayer zap = GlobalData.getZAPlayer(p);
			if (GlobalData.players.get(p).isInLastStand() && p.getFallDistance() <= 0 && ((event.getFrom().getPitch() - event.getTo().getPitch()) == 0) && ((event.getFrom().getYaw() - event.getTo().getYaw()) == 0))
				event.setCancelled(true);
			for (GameBarrier gb : zap.getGame().getBarriers())
				for (Block b : gb.getBlocks()) {
					Location l = b.getLocation();
					if (l.distance(event.getTo()) <= 1.1) {
						p.teleport(event.getFrom());
						event.setCancelled(true);
					}
				}
		}
	}
}
