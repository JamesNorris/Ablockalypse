package com.github.JamesNorris.Event.Bukkit;

import java.util.Random;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Implementation.GameHellHound;
import com.github.JamesNorris.Implementation.GameUndead;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Util.MiscUtil;

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
		Entity e = event.getEntity();
		Player p = event.getEntity().getKiller();
		if (Data.isZAMob(e)) {
			event.getDrops().clear();
			event.setDroppedExp(0);
			if (e instanceof Zombie) {
				ZAMob u = Data.getZAMob(e);
				GameUndead gu = (GameUndead) u;
				gu.killed = true;
				u.getGame().subtractMobCount();
			} else if (e instanceof Wolf) {
				ZAMob h = Data.getZAMob(e);
				GameHellHound gh = (GameHellHound) h;
				gh.killed = true;
				h.getGame().subtractMobCount();
			}
			if (Data.players.containsKey(p)) {
				ZAPlayerBase zap = Data.players.get(p);
				zap.addPoints(cd.pointincrease);
				int food = p.getFoodLevel();
				if (food < 20) {
					p.setFoodLevel(20);
				}
				MiscUtil.randomPowerup(zap);
			}
		}
	}
}
