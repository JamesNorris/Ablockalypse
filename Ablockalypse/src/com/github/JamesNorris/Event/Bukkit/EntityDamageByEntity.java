package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Interface.ZAMob;

public class EntityDamageByEntity implements Listener {
	private ConfigurationData cd;

	public EntityDamageByEntity() {
		cd = External.ym.getConfigurationData();
	}

	/*
	 * Called when an entity damaged another entity.
	 * Used mostly for picking someone out of last stand.
	 */
	@EventHandler public void EDBEE(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity e = event.getEntity();
		if (Data.isZAMob(e)) {
			if (damager instanceof Fireball) {
				ZAMob zam = Data.getZAMob(e);
				int dmg = 40 - (zam.getGame().getLevel());
				if (dmg <= 4)
					dmg = 4;
				event.setDamage(dmg);
			} else if (damager instanceof Player) {
				ZAMob zam = Data.getZAMob(e);
				int dmg = event.getDamage() - zam.getGame().getLevel();
				if (dmg < 2)
					dmg = 2;
				event.setDamage(dmg);
			}
		} else if (e instanceof Player) {
			Player p = (Player) e;
			if (Data.players.containsKey(p)) {
				ZAPlayerBase zap = Data.players.get(p);
				if (damager instanceof Player) {
					Player p2 = (Player) damager;// TODO add a friendly fire option
					if (Data.playerExists(p2)) {
						if (zap.isInLastStand())
							zap.toggleLastStand();
					} else
						event.setCancelled(true);
				} else if (p.getHealth() <= cd.lsthresh && !zap.isInLastStand() && !zap.isInLimbo()) {
					p.setHealth(cd.lsthresh);
					zap.toggleLastStand();
				} else if (zap.isInLastStand())
					event.setCancelled(true);
			}
		}
	}
}
