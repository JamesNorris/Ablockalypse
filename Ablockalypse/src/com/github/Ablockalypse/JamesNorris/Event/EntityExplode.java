package com.github.Ablockalypse.JamesNorris.Event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplode implements Listener {
	/*
	 * Called when an entity is broken by explosion.
	 * Mostly used for preventing explosions from breaking things in this plugin.
	 */
	@EventHandler public void EEE(EntityExplodeEvent event) {
		int i = event.getEntity().getEntityId();
		if (ProjectileHit.pearlids != null && ProjectileHit.pearlids.contains(i)) {
			ProjectileHit.pearlids.remove(i);
			event.blockList().clear();
		}
	}
}
