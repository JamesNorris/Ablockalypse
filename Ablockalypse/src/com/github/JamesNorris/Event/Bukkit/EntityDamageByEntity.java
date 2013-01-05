package com.github.JamesNorris.Event.Bukkit;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Enumerated.Setting;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Interface.ZAPlayer;

public class EntityDamageByEntity extends DataManipulator implements Listener {
	public static ArrayList<UUID> instakillids = new ArrayList<UUID>();

	/*
	 * Used to separate mob damage from player damage.
	 * This is the mob version.
	 */
	public void mobDamage(EntityDamageByEntityEvent event, Entity damager, Entity e, int evtdmg) {
		ZAMob zam = data.getZAMob(e);
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
			if (data.playerExists(p)) {
				ZAPlayer zap = data.getZAPlayer(p);
				if (zap.hasInstaKill())
					event.setDamage(zam.getCreature().getHealth() * 5);
				else {
					int dmg = evtdmg - zam.getHitAbsorption();
					if (dmg <= 4)
						dmg = 4;
					event.setDamage(dmg);
				}
			}
		} else if (data.isZAMob(damager) && damager instanceof Wolf) {
			event.setDamage(((evtdmg - zam.getHitAbsorption()) / 2));
		}
	}

	/*
	 * Used to separate mob damage from player damage.
	 * This is the player version.
	 */
	public void playerDamage(EntityDamageByEntityEvent event, Entity damager, Entity e, int evtdmg) {
		Player p = (Player) e;
		if (data.players.containsKey(p)) {
			ZAPlayerBase zap = data.players.get(p);
			ZAGameBase zag = (ZAGameBase) zap.getGame();
			if (damager instanceof Player) {
				Player p2 = (Player) damager;
				if (data.playerExists(p2)) {
					if (zap.isInLastStand())
						zap.toggleLastStand();
					else if (!zag.friendlyFireEnabled())
						event.setCancelled(true);
					else if (zag.friendlyFireEnabled())
						event.setDamage(evtdmg - zap.getHitAbsorption());
				} else
					event.setCancelled(true);
			} else if (p.getHealth() <= (Integer) Setting.LASTSTANDTHRESHOLD.getSetting() && !zap.isInLastStand() && !zap.isInLimbo()) {
				p.setHealth((Integer) Setting.LASTSTANDTHRESHOLD.getSetting());
				zap.toggleLastStand();
			} else if (zap.isInLastStand())
				event.setCancelled(true);
			if (damager instanceof Fireball)
				if (!zag.friendlyFireEnabled())
					event.setCancelled(true);
				else if (zag.friendlyFireEnabled())
					event.setDamage(evtdmg - zap.getHitAbsorption());
		}
	}

	/*
	 * Called when an entity damaged another entity.
	 * Used mostly for picking someone out of last stand, changing damage, and cancelling damage.
	 */
	@EventHandler(priority = EventPriority.HIGHEST) public void EDBEE(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity e = event.getEntity();
		int evtdmg = event.getDamage();
		if (data.isZAMob(e)) {
			mobDamage(event, damager, e, evtdmg);
		} else if (e instanceof Player) {
			playerDamage(event, damager, e, evtdmg);
		}
	}
}
