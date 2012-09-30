package com.github.JamesNorris.Event;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import com.github.JamesNorris.Data.Data;

public class ProjectileHit implements Listener {
	private int yield = 2;// Can be changed to make a larger explosion.
	public static ArrayList<UUID> uuids;

	public ProjectileHit() {
		ProjectileHit.uuids = new ArrayList<UUID>();
	}

	/*
	 * Called when a player throws an object.
	 * Used for changing ender pearls to grenades for ZAPlayers.
	 */
	@EventHandler public void PHE(ProjectileHitEvent event) {
		Entity e = event.getEntity();
		if (e instanceof EnderPearl) {
			EnderPearl ep = (EnderPearl) e;
			Player p = (Player) ep.getShooter();
			if (Data.players.containsKey(p)) {
				Location loc = ep.getLocation();
				World w = loc.getWorld();
				Entity ent = w.spawnEntity(loc, EntityType.FIREBALL);
				Fireball f = (Fireball) ent;
				uuids.add(f.getUniqueId());
				f.setDirection(new Vector(0, -(w.getHighestBlockYAt(loc)), 0));
				ep.setBounce(true);
				ep.remove();
				f.setYield(yield);
				f.setIsIncendiary(true);
				f.setTicksLived(1);
			}
		}
	}
}
