package com.github.jamesnorris.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.github.jamesnorris.enumerated.GameEntityType;
import com.github.jamesnorris.implementation.Game;

public class GameMobSpawnEvent extends AblockalypseEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /**
     * Gets the handlerlist for this event.
     * 
     * @return The handlers for this event, in a list
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    private boolean cancel;
    private Entity e;
    private Game game;
    private GameEntityType get;

    /**
     * The event called when an Ablockalypse mob is spawned into the game.
     * 
     * @param e The entity that has been spawned
     * @param game The game that the entity has been spawned into
     * @param get The type of entity that is spawned
     */
    public GameMobSpawnEvent(Entity e, Game game, GameEntityType get) {
        this.e = e;
        this.game = game;
        this.get = get;
        cancel = false;
    }

    /**
     * Gets the entity associated with this event.
     * 
     * @return The entity that calls this event
     */
    public Entity getEntity() {
        return e;
    }

    /**
     * Gets the type of game-specific entity that has been spawned into the game.
     * 
     * @return The type of game mob spawned into the game
     */
    public GameEntityType getEntityType() {
        return get;
    }

    /**
     * Gets the game the entity is involved in.
     * 
     * @return The game the entity is involved in
     */
    public Game getGame() {
        return game;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Checks if the event is cancelled.
     * 
     * @return Whether or not this event is cancelled
     */
    @Override public boolean isCancelled() {
        return cancel;
    }

    /**
     * Cancels the event.
     * 
     * @param arg Whether or not to cancel the event
     */
    @Override public void setCancelled(boolean arg) {
        cancel = arg;
    }
}
