package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Implementation.GameUndead;
import com.github.JamesNorris.Util.Breakable;

public class EntityDamage implements Listener {
	/*
	 * Called when an entity is damaged.
	 * Used mostly for cancelling fire damage to ZA mobs.
	 */
	@EventHandler public void EDE(EntityDamageEvent event) {
		Entity e = event.getEntity();
		if (e != null && GlobalData.isZAMob(e))
			if ((event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK) && e instanceof Zombie) {
				GameUndead gu = (GameUndead) GlobalData.getUndead(e);
				if (gu.isFireproof()) {
					Breakable.getNMSEntity(e).extinguish();
					event.setCancelled(true);
				}
			} else if (event.getCause() == DamageCause.SUFFOCATION || event.getCause() == DamageCause.FALL)
				event.setCancelled(true);
	}
}
