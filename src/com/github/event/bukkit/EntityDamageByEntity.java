package com.github.event.bukkit;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.entity.ZAMob;
import com.github.aspect.entity.ZAPlayer;
import com.github.enumerated.Setting;
import com.github.threading.inherent.LastStandPickupTask;

public class EntityDamageByEntity implements Listener {
    public static ArrayList<UUID> instakillids = new ArrayList<UUID>();
    private DataContainer data = Ablockalypse.getData();
    public static final double MIN_FIREBALL_DMG = 5, MIN_ARROW_DMG = 8, MIN_HIT_DMG = 1.5, MIN_FRIENDLY_FIRE_DMG = 1;

    /* Called when an entity damaged another entity.
     * Used mostly for picking someone out of last stand, changing damage, and cancelling damage. */
    @EventHandler(priority = EventPriority.HIGHEST) public void EDBEE(EntityDamageByEntityEvent event) {
        Entity d = event.getDamager();
        Entity entity = event.getEntity();
        double evtdmg = event.getDamage();
        if (!(entity instanceof LivingEntity) || !(d instanceof LivingEntity)) {
            return;
        }
        LivingEntity e = (LivingEntity) entity;
        LivingEntity damager = (LivingEntity) d;
        if (data.isZAMob(e)) {
            mobDamage(event, damager, e, evtdmg);
        } else if (e instanceof Player) {
            playerDamage(event, damager, e, evtdmg);
        }
    }

    /* Used to separate mob damage from player damage.
     * This is the mob version. */
    public void mobDamage(EntityDamageByEntityEvent event, LivingEntity damager, LivingEntity e, double evtdmg) {
        ZAMob zam = data.getZAMob(e);
        if (damager instanceof Fireball) {
            Fireball f = (Fireball) damager;
            if (instakillids.contains(f.getUniqueId())) {
                event.setDamage(zam.getEntity().getHealth() * 10);
            } else {
                double dmg = 40 - zam.getHitAbsorption();// fireball damage
                if (dmg <= MIN_FIREBALL_DMG) {
                    dmg = MIN_FIREBALL_DMG;
                }
                event.setDamage(dmg);
            }
        } else if (damager instanceof Arrow) {
            Arrow a = (Arrow) damager;
            if (instakillids.contains(a.getUniqueId())) {
                event.setDamage(zam.getEntity().getHealth() * 10);
            } else {
                double dmg = 50 - zam.getHitAbsorption();// arrow damage
                if (dmg <= MIN_ARROW_DMG) {
                    dmg = MIN_ARROW_DMG;
                }
                event.setDamage(dmg);
            }
        } else if (damager instanceof Player) {
            Player p = (Player) damager;
            if (data.isZAPlayer(p)) {
                ZAPlayer zap = data.getZAPlayer(p);
                if (zap.hasInstaKill()) {
                    event.setDamage(zam.getEntity().getHealth() * 5);
                } else {
                    double dmg = evtdmg - zam.getHitAbsorption();// regular hit damage
                    if (dmg <= MIN_HIT_DMG) {
                        dmg = MIN_HIT_DMG;
                    }
                    event.setDamage(dmg);
                }
            }
        } else if (data.isZAMob(damager) && damager instanceof Wolf) {
            event.setDamage((evtdmg - zam.getHitAbsorption()) / 2);
        }
    }

    /* Used to separate mob damage from player damage.
     * This is the player version. */
    public void playerDamage(EntityDamageByEntityEvent event, Entity damager, Entity e, double evtdmg) {
        Player p = (Player) e;
        if (data.isZAPlayer(p)) {
            ZAPlayer zap = data.getZAPlayer(p);
            if (damager instanceof Player) {
                Player p2 = (Player) damager;
                if (data.isZAPlayer(p2)) {
                    ZAPlayer hitter = data.getZAPlayer(p2);
                    if (zap.isInLastStand()) {
                        new LastStandPickupTask(hitter, zap, 20, 5, true);
                        event.setCancelled(true);
                    }
                    if (!(Boolean) Setting.DEFAULT_FRIENDLY_FIRE_MODE.getSetting()) {
                        event.setCancelled(true);
                    } else {
                        double dmg = evtdmg - zap.getHitAbsorption();
                        if (dmg < MIN_FRIENDLY_FIRE_DMG) {
                            dmg = MIN_FRIENDLY_FIRE_DMG;
                        }
                        event.setDamage(dmg);
                    }
                } else {
                    event.setCancelled(true);
                }
            } else if (p.getHealth() <= (Integer) Setting.LAST_STAND_HEALTH_THRESHOLD.getSetting() && !zap.isInLastStand() && !zap.isInLimbo()) {
                p.setHealth((Integer) Setting.LAST_STAND_HEALTH_THRESHOLD.getSetting());
                zap.toggleLastStand();
            } else if (zap.isInLastStand()) {
                event.setCancelled(true);
            }
            if (damager instanceof Fireball) {
                if (!(Boolean) Setting.DEFAULT_FRIENDLY_FIRE_MODE.getSetting()) {
                    event.setCancelled(true);
                } else {
                    double dmg = evtdmg - zap.getHitAbsorption();
                    if (dmg < MIN_FIREBALL_DMG) {
                        dmg = MIN_FIREBALL_DMG;
                    }
                    event.setDamage(dmg);
                }
            }
        }
    }
}
