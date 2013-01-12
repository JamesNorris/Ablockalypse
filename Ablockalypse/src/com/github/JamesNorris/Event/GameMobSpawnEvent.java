package com.github.JamesNorris.Event;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.github.JamesNorris.Enumerated.GameEntityType;
import com.github.JamesNorris.Interface.ZAGame;

public class GameMobSpawnEvent extends Event {
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
	private ZAGame game;
	private GameEntityType get;

	/**
	 * The event called when an Ablockalypse mob is spawned into the game.
	 * 
	 * @param e The entity that has been spawned
	 * @param game The game that the entity has been spawned into
	 */
	public GameMobSpawnEvent(Entity e, ZAGame game, GameEntityType get) {
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
	public ZAGame getGame() {
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
	public boolean isCancelled() {
		return cancel;
	}

	/**
	 * Cancels the event.
	 * 
	 * @param arg Whether or not to cancel the event
	 */
	public void setCancelled(boolean arg) {
		cancel = arg;
	}
}
