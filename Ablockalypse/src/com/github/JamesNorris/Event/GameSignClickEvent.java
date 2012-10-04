package com.github.JamesNorris.Event;

import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameSignClickEvent extends Event {
	private static HandlerList handlers = new HandlerList();
	private boolean cancel;
	private Sign sign;

	/**
	 * An event called when a player clicks a sign with the default required first line.
	 * 
	 * @param sign The sign clicked
	 */
	public GameSignClickEvent(Sign sign) {
		this.sign = sign;
	}

	/**
	 * Gets the sign involved in this event.
	 * 
	 * @return The sign clicked
	 */
	public Sign getSign() {
		return sign;
	}

	/**
	 * Gets the world that this event takes place in.
	 * 
	 * @return The world of the sign clicked
	 */
	public World getWorld() {
		return sign.getWorld();
	}

	/**
	 * Gets the handlerlist for this event.
	 * 
	 * @return The handlers for this event, in a list
	 */
	public static HandlerList getHandlerList() {
		return handlers;
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
