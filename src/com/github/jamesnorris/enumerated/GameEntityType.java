package com.github.jamesnorris.enumerated;

import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.Hellhound;
import com.github.jamesnorris.implementation.Undead;
import com.github.jamesnorris.inter.ZAMob;
import com.google.common.collect.Maps;

public enum GameEntityType {
    //@formatter:off
	HELLHOUND(EntityType.WOLF) {
        @Override public ZAMob instantiate(Entity entity, Game game) {
            return new Hellhound((Wolf) entity, game);
        }	    
	},
	UNDEAD(EntityType.ZOMBIE) {
        @Override public ZAMob instantiate(Entity entity, Game game) {
            return new Undead((Zombie) entity, game);
        }	    
	};
	//@formatter:on
    //
    private final static Map<EntityType, GameEntityType> BY_ENTITY_TYPE = Maps.newHashMap();

    public static GameEntityType translate(EntityType type) {
        return BY_ENTITY_TYPE.get(type);
    }

    public static EntityType translate(GameEntityType type) {
        return type.type;
    }

    public abstract ZAMob instantiate(Entity entity, Game game);

    private EntityType type;

    GameEntityType(EntityType type) {
        this.type = type;
    }

    static {
        for (GameEntityType setting : values()) {
            BY_ENTITY_TYPE.put(setting.type, setting);
        }
    }
}
