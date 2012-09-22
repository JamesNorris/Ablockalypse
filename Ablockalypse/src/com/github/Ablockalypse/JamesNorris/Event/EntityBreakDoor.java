package com.github.Ablockalypse.JamesNorris.Event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreakDoorEvent;

import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.GameZombie;

public class EntityBreakDoor implements Listener {
	@EventHandler public void EBDE(final EntityBreakDoorEvent event) {
		final LivingEntity e = event.getEntity();
		if (e instanceof Zombie) {
			final Zombie z = (Zombie) e;
			for (final GameZombie gz : Data.zombies) {
				if (gz.getZombie() == z)
					event.setCancelled(true);
			}
		}
	}
}
