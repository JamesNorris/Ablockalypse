package com.github.Ablockalypse.JamesNorris.Event;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.github.Ablockalypse.JamesNorris.Data.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayer;
import com.github.Ablockalypse.JamesNorris.Util.External;
import com.github.Ablockalypse.JamesNorris.Util.Util;

public class EntityDeath implements Listener {
	private ConfigurationData cd;
	private Random rand;

	/*
	 * Called when an Entity is killed.
	 * Used for adding points when a player kills an entity, while they are in-game.
	 */
	@EventHandler public void EDE(EntityDeathEvent event) {
		if (cd == null)
			cd = External.ym.getConfigurationData();
		if (rand == null)
			rand = new Random();
		Player p = event.getEntity().getKiller();
		if (Data.players.containsKey(p)) {
			ZAPlayer zap = Data.players.get(p);
			zap.addPoints(cd.pointincrease);
			zap.getGame().removeMob();
			Util.randomPowerup(zap);
		}
	}
}
