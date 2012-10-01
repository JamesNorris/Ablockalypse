package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplode implements Listener {
	/*
	 * Called when an entity explodes.
	 * Used mainly to prevent grenades from doing damage to land.
	 */
	@EventHandler public void EEE(EntityExplodeEvent event) {
		Entity e = event.getEntity();
		if (e.getType() == EntityType.FIREBALL && ProjectileHit.uuids.contains(e.getUniqueId())) {
			event.blockList().clear();
			ProjectileHit.uuids.remove(e.getUniqueId());
		}
	}
}
