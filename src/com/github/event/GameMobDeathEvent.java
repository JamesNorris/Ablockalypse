package com.github.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.aspect.Game;

public class GameMobDeathEvent extends AblockalypseEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private DamageCause cause;
    private Entity entity;
    private Game game;

    public GameMobDeathEvent(Entity entity, Game game, DamageCause cause) {
        this.entity = entity;
        this.cause = cause;
        this.game = game;
    }

    public DamageCause getCause() {
        return cause;
    }

    public Entity getEntity() {
        return entity;
    }

    public Game getGame() {
        return game;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }
}
