package com.github.jamesnorris.enumerated;

import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.Hellhound;
import com.github.jamesnorris.implementation.Undead;
import com.github.jamesnorris.inter.ZAMob;
import com.google.common.collect.Maps;

public enum GameEntityType {
    //@formatter:off
	HELLHOUND(EntityType.WOLF) {
        @Override public ZAMob instantiate(Entity entity, Game game) {
            return new Hellhound(entity.getWorld(), entity.getUniqueId(), game);
        }	    
	},
	UNDEAD(EntityType.ZOMBIE) {
        @Override public ZAMob instantiate(Entity entity, Game game) {
            return new Undead(entity.getWorld(), entity.getUniqueId(), game);
        }	    
	};
	//@formatter:on
    //
    private final static Map<EntityType, GameEntityType> BY_ENTITY_TYPE = Maps.newHashMap();

    static {
        for (GameEntityType setting : values()) {
            BY_ENTITY_TYPE.put(setting.type, setting);
        }
    }

    public static GameEntityType translate(EntityType type) {
        return BY_ENTITY_TYPE.get(type);
    }

    public static EntityType translate(GameEntityType type) {
        return type.type;
    }
    private EntityType type;

    GameEntityType(EntityType type) {
        this.type = type;
    }

    public abstract ZAMob instantiate(Entity entity, Game game);
}
