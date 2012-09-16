package com.github.Ablockalypse.JamesNorris.Event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.github.Ablockalypse.JamesNorris.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayer;
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
			cd = External.cd;
		Entity e = event.getEntity();
		Player p = (Player) e;
		if (Data.players.containsKey(p)) {
			ZAPlayer zap = Data.players.get(p);
			if ((p.getHealth() / 20) * 100 >= cd.lsthresh && !zap.isInLastStand()) {
				zap.toggleLastStand();
			}
		}
	}
}
