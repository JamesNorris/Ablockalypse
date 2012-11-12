package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Util.MiscUtil;

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
			if (GlobalData.players.containsKey(p)) {
				ZAPlayerBase zap = GlobalData.players.get(p);
				if (zap.isInLastStand() || !MiscUtil.isAcceptedMob(entity) || !GlobalData.isZAMob(entity))
					event.setCancelled(true);
			}
		}
	}
}
