package com.github.Ablockalypse.JamesNorris.Event;

import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import com.github.Ablockalypse.JamesNorris.Data;

public class ProjectileHit implements Listener {
	/*
	 * Called when a player throws an object.
	 * Used for changing ender pearls to grenades for ZAPlayers.
	 */
	@EventHandler public void PHE(ProjectileHitEvent event) {
		Entity e = event.getEntity();
		if (e instanceof EnderPearl) {
			EnderPearl ep = (EnderPearl) e;
			LivingEntity le = ep.getShooter();
			Player p = (Player) le;
			if (Data.players.containsKey(p)) {
				ep.setBounce(true);
				ep.getWorld().createExplosion(ep.getLocation(), 3);
				ep.remove();
			}
		}
	}
}
