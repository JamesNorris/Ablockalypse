package com.github.event.bukkit;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.entity.Grenade;
import com.github.aspect.entity.ZAMob;
import com.github.aspect.entity.ZAPlayer;
import com.github.enumerated.ZAEffect;

public class ProjectileHit implements Listener {
    private DataContainer data = Ablockalypse.getData();

    /* Called when a player throws an object.
     * Used for changing ender pearls to grenades for ZAPlayers. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PHE(ProjectileHitEvent event) {
        Entity e = event.getEntity();
        if (e instanceof EnderPearl) {
            EnderPearl ep = (EnderPearl) e;
            Player p = (Player) ep.getShooter();
            if (data.isZAPlayer(p) && !data.isGrenade(e)) {
                Location loc = ep.getLocation();
                Location pLoc = p.getLocation();
                ZAPlayer zap = data.getZAPlayer(p);
                double xDiffForce = (loc.getX() - pLoc.getX()) / 20;
                double zDiffForce = (loc.getZ() - pLoc.getZ()) / 20;
                new Grenade(e, zap, 60, true, false, new Vector(xDiffForce, .1, zDiffForce));
            }
        } else if (e instanceof Arrow && ((Arrow) e).getShooter() instanceof Player) {
            Arrow a = (Arrow) e;
            Player p = (Player) a.getShooter();
            if (data.isZAPlayer(p)) {
                ZAPlayer zap = data.getZAPlayer(p);
                UUID uuid = a.getUniqueId();
                if (zap.hasInstaKill()) {
                    EntityDamageByEntity.instakillids.add(uuid);
                }
                Location loc2 = e.getLocation();
                /* through-barrier damage */
                for (ZAMob zam : zap.getGame().getMobs()) {
                    LivingEntity c = zam.getEntity();
                    Location loc3 = c.getLocation();
                    if (loc3.distanceSquared(loc2) <= 3) {// within 1.5 blocks (2.25 approx 1.5 squared)
                        double dmg = 40;
                        EntityDamageByEntityEvent EDBE = new EntityDamageByEntityEvent(p, c, DamageCause.CUSTOM, dmg);
                        Bukkit.getPluginManager().callEvent(EDBE);
                        if (!EDBE.isCancelled()) {
                            ZAEffect.IRON_BREAK.play(loc3);
                            c.damage(EDBE.getDamage());
                        }
                        break;
                    }
                }
            }
        }
    }
}
