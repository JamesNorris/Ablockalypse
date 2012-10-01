package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Implementation.ZAPlayerBase;

public class EntityDamageByEntity implements Listener {
	private ConfigurationData cd;

	/*
	 * Called when an entity damaged another entity.
	 * Used mostly for picking someone out of last stand.
	 */
	@EventHandler public void EDBEE(EntityDamageByEntityEvent event) {
		if (cd == null)
			cd = External.ym.getConfigurationData();
		Entity damager = event.getDamager();
		Entity e = event.getEntity();
		if (e instanceof Player) {
			Player p = (Player) e;
			if (Data.players.containsKey(p)) {
				ZAPlayerBase zap = Data.players.get(p);
				if (damager instanceof Player) {
					Player p2 = (Player) damager;
					if (Data.playerExists(p2)) {
						if (zap.isInLastStand())
							zap.toggleLastStand();
					}// TODO add friendly fire option.
				} else if (p.getHealth() <= cd.lsthresh && !zap.isInLastStand() && !zap.isInLimbo()) {
					p.setHealth(cd.lsthresh);
					zap.toggleLastStand();
				} else if (zap.isInLastStand())
					event.setCancelled(true);
			}
		}
	}
}
