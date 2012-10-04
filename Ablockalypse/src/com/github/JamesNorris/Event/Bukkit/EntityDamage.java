package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Util.Breakable;

public class EntityDamage implements Listener {
	/*
	 * Called when an entity is damaged.
	 * Used mostly for cancelling fire damage to ZA mobs.
	 */
	@EventHandler public void EDE(EntityDamageEvent event) {
		Entity e = event.getEntity();
		if (e != null && e instanceof CraftZombie)
			e = (Zombie) e;
		if (e != null && Data.isZAMob(e)) {
			ZAMob zam = (ZAMob) Data.getUndead(e);
			zam.attemptHealthIncrease();
			if (event.getCause() == DamageCause.ENTITY_EXPLOSION)
				zam.setHealth(2);
			else if ((event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK) && e instanceof Zombie) {
				Breakable.getNMSEntity(e).extinguish();
				event.setCancelled(true);
			} else if (event.getCause() == DamageCause.SUFFOCATION || event.getCause() == DamageCause.FALL) {
				event.setCancelled(true);
			}
		}
	}
}
