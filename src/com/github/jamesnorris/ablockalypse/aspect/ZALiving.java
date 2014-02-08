package com.github.jamesnorris.ablockalypse.aspect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.behavior.Targettable;

public class ZALiving extends PermanentAspect implements Targettable {
    private static DataContainer data = Ablockalypse.getData();
    private LivingEntity entity;

    public ZALiving(LivingEntity entity) {
        this.entity = entity;
    }

    public ZALiving(Map<String, Object> savings) {
        this((LivingEntity) data.getEntityByUUID(Bukkit.getWorld((UUID) savings.get("world_uuid")), (UUID) savings.get("entity_uuid")));
    }

    public LivingEntity getEntity() {
        return entity;
    }

    @Override public Map<String, Object> getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("entity_uuid", entity.getUniqueId());
        savings.put("world_uuid", entity.getWorld().getUID());
        return savings;
    }

    @Override public boolean isResponsive() {
        return entity != null && !entity.isDead();
    }

    @Override public boolean isTargettedBy(ZAMob mob) {
        LivingEntity other = mob.getEntity();
        if (!(other instanceof Creature)) {
            return false;
        }
        LivingEntity target = ((Creature) other).getTarget();
        return target != null && target.equals(entity);
    }

    @Override public Location updateTarget() {
        return entity.getLocation();
    }
}
