package com.github.jamesnorris.event.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.event.GameMobDeathEvent;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.ZAMob;
import com.github.jamesnorris.util.MiscUtil;

public class EntityDeath implements Listener {
    private static DataContainer data = Ablockalypse.getData();

    public static void behaveLikeKill(Player killer, Entity entityKilled) {
        ZAPlayer zap = data.players.get(killer);
        EntityDamageEvent damage = entityKilled.getLastDamageCause();
        boolean exploded = damage != null && damage.getCause() != null && damage.getCause() == DamageCause.ENTITY_EXPLOSION;
        int pay = !exploded ? (Integer) Setting.KILL_PAY.getSetting() : (Integer) Setting.EXPLOSIVE_KILL_PAY.getSetting();
        zap.addPoints(pay);
        zap.setKills(zap.getKills() + 1);
        int food = killer.getFoodLevel();
        if (food < 20) {
            killer.setFoodLevel(20);
        }
        MiscUtil.randomPowerup(zap, entityKilled);
    }

    /* Called when an Entity is killed.
     * Used for adding points when a player kills an entity, while they are in-game. */
    @EventHandler(priority = EventPriority.HIGHEST) public void EDE(EntityDeathEvent event) {
        Entity e = event.getEntity();
        Player p = event.getEntity().getKiller();
        if (data.isZAMob(e)) {
            DamageCause cause = DamageCause.CUSTOM;
            if (e.getLastDamageCause() != null) {
                cause = e.getLastDamageCause().getCause();
            }
            ZAMob mob = data.getZAMob(e);
            GameMobDeathEvent gmde = new GameMobDeathEvent(e, mob.getGame(), cause);
            Bukkit.getPluginManager().callEvent(gmde);
            event.getDrops().clear();
            event.setDroppedExp(0);
            mob.kill();
            if (p != null && data.players.containsKey(p)) {
                behaveLikeKill(p, e);
            }
        }
    }
}
