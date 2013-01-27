package com.github.JamesNorris.Event;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.JamesNorris.Interface.ZAGame;

public class GameMobDeathEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private boolean cancel;
	private DamageCause cause;
	private Entity entity;

	private ZAGame game;

	public GameMobDeathEvent(Entity entity, ZAGame game, DamageCause cause) {
		this.cancel = false;
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

	public ZAGame getGame() {
		return game;
	}

	@Override public HandlerList getHandlers() {
		return null;
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
