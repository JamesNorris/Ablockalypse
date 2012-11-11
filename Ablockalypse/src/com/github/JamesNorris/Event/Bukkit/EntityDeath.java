package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Util.MiscUtil;

public class EntityDeath implements Listener {
	private ConfigurationData cd;

	public EntityDeath() {
		cd = External.ym.getConfigurationData();
	}

	/*
	 * Called when an Entity is killed.
	 * Used for adding points when a player kills an entity, while they are in-game.
	 */
	@EventHandler public void EDE(EntityDeathEvent event) {
		Entity e = event.getEntity();
		Player p = event.getEntity().getKiller();
		if (Data.isZAMob(e)) {
			event.getDrops().clear();
			event.setDroppedExp(0);
			Data.getZAMob(e).kill();
			if (Data.players.containsKey(p)) {
				ZAPlayerBase zap = Data.players.get(p);
				zap.addPoints(cd.pointincrease);
				int food = p.getFoodLevel();
				if (food < 20)
					p.setFoodLevel(20);
				MiscUtil.randomPowerup(zap, e);
			}
		}
	}
}
