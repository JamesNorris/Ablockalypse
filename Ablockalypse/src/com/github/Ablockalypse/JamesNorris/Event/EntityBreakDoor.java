package com.github.Ablockalypse.JamesNorris.Event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreakDoorEvent;

import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.GameUndead;

public class EntityBreakDoor implements Listener {
	@EventHandler public void EBDE(EntityBreakDoorEvent event) {
		LivingEntity e = event.getEntity();
		if (e instanceof Zombie) {
			Zombie z = (Zombie) e;
			for (GameUndead gz : Data.zombies) {
				if (gz.getZombie() == z)
					event.setCancelled(true);
			}
		}
	}
}
