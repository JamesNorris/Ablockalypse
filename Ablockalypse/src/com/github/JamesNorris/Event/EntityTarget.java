package com.github.JamesNorris.Event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Util.Util;

public class EntityTarget implements Listener {
	/*
	 * Called when an entity targets another entity.
	 * Mostly used for making sure non-supported entites do not attack ZA players.
	 */
	@EventHandler public void ETE(EntityTargetEvent event) {
		Entity target = event.getTarget();
		Entity entity = event.getEntity();
		if (target instanceof Player) {
			Player p = (Player) target;
			if (Data.players.containsKey(p)) {
				ZAPlayerBase zap = Data.players.get(p);
				if (zap.isInLastStand() || !Util.isAcceptedMob(entity)) {
					event.setCancelled(true);
				}
			}
		}
	}
}
