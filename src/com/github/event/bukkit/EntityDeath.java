package com.github.event.bukkit;

import java.util.Random;

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
import com.github.DataContainer;
import com.github.aspect.ZAPlayer;
import com.github.behavior.ZAMob;
import com.github.enumerated.PowerupType;
import com.github.enumerated.Setting;
import com.github.event.GameMobDeathEvent;

public class EntityDeath implements Listener {
    private static DataContainer data = Ablockalypse.getData();
    private static Random rand = new Random();

    public static void behaveLikeKill(Player killer, Entity entityKilled) {
        ZAPlayer zap = data.getZAPlayer(killer);
        EntityDamageEvent damage = entityKilled.getLastDamageCause();
        boolean exploded = damage != null && damage.getCause() != null && damage.getCause() == DamageCause.ENTITY_EXPLOSION;
        int pay = !exploded ? (Integer) Setting.KILL_PAY.getSetting() : (Integer) Setting.EXPLOSIVE_KILL_PAY.getSetting();
        zap.addPoints(pay);
        zap.setKills(zap.getKills() + 1);
        int food = killer.getFoodLevel();
        if (food < 20) {
            killer.setFoodLevel(20);
        }
        int chance = rand.nextInt(100) + 1;
        if (chance <= (Integer) Setting.POWERUP_CHANCE.getSetting()) {
            zap.givePowerup(PowerupType.getById(rand.nextInt(5) + 1), killer);
        }
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
            if (p != null && data.isZAPlayer(p)) {
                behaveLikeKill(p, e);
            }
        }
    }
}
