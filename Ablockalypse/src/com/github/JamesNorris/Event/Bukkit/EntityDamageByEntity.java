package com.github.JamesNorris.Event.Bukkit;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Interface.ZAPlayer;

public class EntityDamageByEntity implements Listener {
	private ConfigurationData cd;
	public static ArrayList<UUID> instakillids = new ArrayList<UUID>();

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
		int evtdmg = event.getDamage();
		if (GlobalData.isZAMob(e)) {
			ZAMob zam = GlobalData.getZAMob(e);
			if (damager instanceof Fireball) {
				Fireball f = (Fireball) damager;
				if (instakillids.contains(f.getUniqueId()))
					event.setDamage(zam.getCreature().getHealth() * 10);
				else {
					int dmg = 40 - zam.getHitAbsorption();
					if (dmg < 9)
						dmg = 9;
					event.setDamage(dmg);
				}
			} else if (damager instanceof Arrow) {
				Arrow a = (Arrow) damager;
				if (instakillids.contains(a.getUniqueId()))
					event.setDamage(zam.getCreature().getHealth() * 10);
				else {
					int dmg = 25 - zam.getHitAbsorption();
					if (dmg <= 8)
						dmg = 8;
					event.setDamage(dmg);
				}
			} else if (damager instanceof Player) {
				Player p = (Player) damager;
				if (GlobalData.playerExists(p)) {
					ZAPlayer zap = GlobalData.getZAPlayer(p);
					if (zap.hasInstaKill())
						event.setDamage(zam.getCreature().getHealth() * 5);
					else {
						int dmg = evtdmg - zam.getHitAbsorption();
						if (dmg <= 4)
							dmg = 4;
						event.setDamage(dmg);
					}
				}
			} else if (GlobalData.isZAMob(damager) && damager instanceof Wolf)
				event.setDamage(evtdmg / 3);
		} else if (e instanceof Player) {
			Player p = (Player) e;
			if (GlobalData.players.containsKey(p)) {
				ZAPlayerBase zap = GlobalData.players.get(p);
				ZAGameBase zag = (ZAGameBase) zap.getGame();
				if (damager instanceof Player) {
					Player p2 = (Player) damager;
					if (GlobalData.playerExists(p2)) {
						if (zap.isInLastStand())
							zap.toggleLastStand();
						else if (!zag.friendlyFireEnabled())
							event.setCancelled(true);
					} else
						event.setCancelled(true);
				} else if (p.getHealth() <= cd.lsthresh && !zap.isInLastStand() && !zap.isInLimbo()) {
					p.setHealth(cd.lsthresh);
					zap.toggleLastStand();
				} else if (zap.isInLastStand())
					event.setCancelled(true);
				if (damager instanceof Fireball)
					if (zag.friendlyFireEnabled())
						event.setCancelled(true);
					else if (GlobalData.isZAMob(damager) && damager instanceof Wolf)
						event.setDamage((int) (evtdmg * 2.5));
			}
		}
	}
}
