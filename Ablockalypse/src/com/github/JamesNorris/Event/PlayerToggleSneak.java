package com.github.JamesNorris.Event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Util.Square;

public class PlayerToggleSneak implements Listener {
	/*
	 * Called when a player changes from walking to sneaking.
	 * Used mostly for repairing broken barriers.
	 */
	@EventHandler public void PTSE(PlayerToggleSneakEvent event) {
		Player p = event.getPlayer();
		if (Data.players.containsKey(p)) {
			for (GameBarrier b : Data.barrierpanels.keySet()) {
				Square s = Data.findBarrierSquare(b, b.getCenter(), 3);
				for (Location l : s.getLocations()) {
					if (p.getLocation() == l)
						b.replaceBarrier();
				}
			}
		}
	}
}
