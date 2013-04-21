package com.github.jamesnorris.event.bukkit;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.ZAMob;

public class ProjectileHit implements Listener {
    private DataContainer data = DataContainer.data;
    public static HashMap<UUID, String> uuids;
    private int yield = 1;// Can be changed to make a larger explosion.

    public ProjectileHit() {
        ProjectileHit.uuids = new HashMap<UUID, String>();
    }

    protected static void preventBlockDestructionWithPoints(ZAPlayer zap, UUID uuid) {
        uuids.put(uuid, zap.getPlayer().getName());
    }

    /*
     * Called when a player throws an object.
     * Used for changing ender pearls to grenades for ZAPlayers.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void PHE(ProjectileHitEvent event) {
        Entity e = event.getEntity();
        if (e instanceof EnderPearl) {
            EnderPearl ep = (EnderPearl) e;
            Player p = (Player) ep.getShooter();
            if (data.players.containsKey(p)) {
                Location loc = ep.getLocation();
                World w = loc.getWorld();
                Entity ent = w.spawnEntity(loc, EntityType.FIREBALL);
                Fireball f = (Fireball) ent;
                ZAPlayer zap = data.getZAPlayer(p);
                UUID uuid = f.getUniqueId();
                if (zap.hasInstaKill())
                    EntityDamageByEntity.instakillids.add(uuid);
                uuids.put(uuid, p.getName());
                f.setDirection(new Vector(0, -(w.getHighestBlockYAt(loc)), 0));
                ep.setBounce(true);
                ep.remove();
                f.setYield(yield);
                f.setIsIncendiary(true);
                f.setTicksLived(1);
            }
        } else if (e instanceof Arrow) {
            Arrow a = (Arrow) e;
            Player p = (Player) a.getShooter();
            if (data.players.containsKey(p)) {
                ZAPlayer zap = data.getZAPlayer(p);
                UUID uuid = a.getUniqueId();
                if (zap.hasInstaKill())
                    EntityDamageByEntity.instakillids.add(uuid);
                Location loc2 = e.getLocation();
                /* through-barrier damage */
                for (ZAMob zam : zap.getGame().getMobs()) {
                    Creature c = zam.getCreature();
                    Location loc3 = c.getLocation();
                    if (loc3.distance(loc2) <= 1.5) {
                        int dmg = 16;
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
