package com.github.Ablockalypse.JamesNorris.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.github.Ablockalypse.JamesNorris.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayer;
import com.github.Ablockalypse.JamesNorris.Util.External;

public class EntityDeath implements Listener {
	private ConfigurationData cd;

	/*
	 * Called when an Entity is killed.
	 * Used for adding points when a player kills an entity, while they are in-game.
	 */
	@EventHandler public void EDE(EntityDeathEvent event) {
		if (cd == null)
			cd = External.ym.getConfigurationData();
		Player p = event.getEntity().getKiller();
		if (Data.players.containsKey(p)) {
			ZAPlayer zap = Data.players.get(p);
			zap.addPoints(cd.pointincrease);
		}
	}
}
