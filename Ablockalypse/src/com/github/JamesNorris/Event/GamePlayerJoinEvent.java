package com.github.JamesNorris.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAPlayer;

public class GamePlayerJoinEvent extends Event {
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
	private ZAPlayer zap;
	private ZAGame game;

	/**
	 * Called when a player joins a game.
	 * 
	 * @param zap The player that joins the game
	 * @param game The game that the player is joining
	 */
	public GamePlayerJoinEvent(ZAPlayer zap, ZAGame game) {
		this.zap = zap;
		this.game = game;
	}

	/**
	 * Finds the game that the player is joining.
	 * 
	 * @return The game that the player is joining
	 */
	public ZAGame getGame() {
		return game;
	}

	@Override public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * Gets the player that joined the game.
	 * 
	 * @return The player that joined the game
	 */
	public Player getPlayer() {
		return zap.getPlayer();
	}

	/**
	 * Gets the zaplayer that joined the game.
	 * 
	 * @return The zaplayer that joined the game
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
