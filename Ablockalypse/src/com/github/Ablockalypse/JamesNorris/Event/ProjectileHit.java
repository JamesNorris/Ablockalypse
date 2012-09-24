package com.github.Ablockalypse.JamesNorris.Event;

import java.util.List;

import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import com.github.Ablockalypse.JamesNorris.Data.Data;

public class ProjectileHit implements Listener {
	public static List<Integer> pearlids;

	/*
	 * Called when a player throws an object.
	 * Used for changing ender pearls to grenades for ZAPlayers.
	 */
	@EventHandler public void PHE(ProjectileHitEvent event) {
		Entity e = event.getEntity();
		if (e instanceof EnderPearl) {
			EnderPearl ep = (EnderPearl) e;
			LivingEntity le = ep.getShooter();
			Player p = (Player) le;// TODO add a cooldown for each player, so less lag is created
			if (Data.players.containsKey(p)) {
				pearlids.add(e.getEntityId());
				ep.getWorld().createExplosion(ep.getLocation(), (float) 100);
				ep.remove();
			}
		}
	}
}
