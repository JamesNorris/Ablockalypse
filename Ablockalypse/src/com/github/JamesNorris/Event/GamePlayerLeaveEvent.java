package com.github.JamesNorris.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAPlayer;

public class GamePlayerLeaveEvent extends Event implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	/**
	 * Gets the handlerlist for this event.
	 * 
	 * @return The handlers for this event, in a list
	 */
	public static HandlerList getHandlerList() {
		return handlers;
	}

	private boolean cancel;
	private ZAGame game;
	private ZAPlayer zap;

	/**
	 * Called when a player leaves a game.
	 * 
	 * @param zap The player that leaves the game
	 * @param game The game that the player is leaving
	 */
	public GamePlayerLeaveEvent(ZAPlayer zap, ZAGame game) {
		this.zap = zap;
		this.game = game;
	}

	/**
	 * Finds the game that the player is leaving.
	 * 
	 * @return The game that the player is leaving
	 */
	public ZAGame getGame() {
		return game;
	}

	@Override public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * Gets the player that left the game.
	 * 
	 * @return The player that left the game
	 */
	public Player getPlayer() {
		return zap.getPlayer();
	}

	/**
	 * Gets the zaplayer that left the game.
	 * 
	 * @return The zaplayer that left the game
	 */
	public ZAPlayer getZAPlayer() {
		return zap;
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
