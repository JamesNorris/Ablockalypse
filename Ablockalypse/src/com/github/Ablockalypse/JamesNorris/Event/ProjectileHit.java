package com.github.Ablockalypse.JamesNorris.Event;

import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import com.github.Ablockalypse.JamesNorris.Data.Data;

public class ProjectileHit implements Listener {
	/*
	 * Called when a player throws an object.
	 * Used for changing ender pearls to grenades for ZAPlayers.
	 */
	@EventHandler public void PHE(final ProjectileHitEvent event) {
		final Entity e = event.getEntity();
		if (e instanceof EnderPearl) {
			final EnderPearl ep = (EnderPearl) e;
			final LivingEntity le = ep.getShooter();
			final Player p = (Player) le;// TODO add a cooldown for each player, so less lag is created
			if (Data.players.containsKey(p)) {
				ep.getWorld().createExplosion(ep.getLocation(), (float) 2.5);
				ep.remove();
			}
		}
	}
}
