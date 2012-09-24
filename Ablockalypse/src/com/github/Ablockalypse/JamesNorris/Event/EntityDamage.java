package com.github.Ablockalypse.JamesNorris.Event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.github.Ablockalypse.JamesNorris.Data.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayerBase;
import com.github.Ablockalypse.JamesNorris.Util.External;

public class EntityDamage implements Listener {
	private ConfigurationData cd;

	/*
	 * Called when an entity is damaged.
	 * 
	 * Used for reviving a player in last stand.
	 */
	@EventHandler public void EDE(EntityDamageEvent event) {
		if (cd == null)
			cd = External.ym.getConfigurationData();
		Entity e = event.getEntity();
		if (e instanceof Player) {
			Player p = (Player) e;
			if (Data.players.containsKey(p)) {
				ZAPlayerBase zap = Data.players.get(p);
				if (p.getHealth() <= cd.lsthresh && !zap.isInLastStand() && !zap.isInLimbo()) {
					p.setHealth(cd.lsthresh);
					zap.toggleLastStand();
				}
			}
		}
	}
}
