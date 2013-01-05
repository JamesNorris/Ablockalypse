package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreakDoorEvent;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Implementation.GameUndead;

public class EntityBreakDoor extends DataManipulator implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST) public void EBDE(EntityBreakDoorEvent event) {
		LivingEntity e = event.getEntity();
		if (e instanceof Zombie) {
			Zombie z = (Zombie) e;
			for (GameUndead gz : data.undead)
				if (gz.getZombie() == z)
					event.setCancelled(true);
		}
	}
}
