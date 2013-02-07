package com.github.JamesNorris.Event.Bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Implementation.GameUndead;
import com.github.JamesNorris.Util.Breakable;

public class EntityDamage extends DataManipulator implements Listener {
    /*
     * Called when an entity is damaged.
     * Used mostly for cancelling fire damage to ZA mobs.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void EDE(EntityDamageEvent event) {
        Entity e = event.getEntity();
        if (e != null && data.isZAMob(e))
            if ((event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK) && e instanceof Zombie) {
                GameUndead gu = (GameUndead) data.getUndead(e);
                if (gu.isFireproof()) {
                    Breakable.getNMSEntity(e).extinguish();
                    event.setCancelled(true);
                }
            } else if (event.getCause() == DamageCause.SUFFOCATION || event.getCause() == DamageCause.FALL)
                event.setCancelled(true);
    }
}
