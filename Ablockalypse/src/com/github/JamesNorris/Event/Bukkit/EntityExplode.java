package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.GlobalData;

public class EntityExplode implements Listener {
	private ConfigurationData cd = External.getYamlManager().getConfigurationData();

	/*
	 * Called when an entity explodes.
	 * Used mainly to prevent grenades from doing damage to land.
	 */
	@EventHandler public void EEE(EntityExplodeEvent event) {
		Entity e = event.getEntity();
		if (e.getType() == EntityType.FIREBALL && ProjectileHit.uuids.containsKey(e.getUniqueId())) {
			event.blockList().clear();
			Player p = (Bukkit.getPlayer(ProjectileHit.uuids.get(e.getUniqueId())));
			if (GlobalData.playerExists(p))
				GlobalData.getZAPlayer(p).addPoints(cd.pointincrease);
			ProjectileHit.uuids.remove(e.getUniqueId());
		}
	}
}
