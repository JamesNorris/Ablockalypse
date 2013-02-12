package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Enumerated.Setting;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Util.MiscUtil;

public class EntityDeath extends DataManipulator implements Listener {
    /*
     * Called when an Entity is killed.
     * Used for adding points when a player kills an entity, while they are in-game.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void EDE(EntityDeathEvent event) {
        Entity e = event.getEntity();
        Player p = event.getEntity().getKiller();
        if (data.isZAMob(e)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
            data.getZAMob(e).kill();
            if (data.players.containsKey(p)) {
                ZAPlayerBase zap = data.players.get(p);
                zap.addPoints((Integer) Setting.KILLPOINTINCREASE.getSetting());
                zap.setKills(zap.getKills() + 1);
                int food = p.getFoodLevel();
                if (food < 20)
                    p.setFoodLevel(20);
                MiscUtil.randomPowerup(zap, e);
            }
        }
    }
}
