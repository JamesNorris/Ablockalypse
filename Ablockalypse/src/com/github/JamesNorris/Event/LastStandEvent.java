package com.github.JamesNorris.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAPlayer;

public class LastStandEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	/**
	 * Gets the handlerlist for this event.
	 * 
	 * @return The handlers for this event, in a list
	 */
	public static HandlerList getHandlerList() {
		return handlers;
	}

	private ZAGame game;
	private Player p;
	private boolean sitting, cancel;
	private ZAPlayer zap;

	/**
	 * The instance for the LastStandEvent, called when a player is put into a sitting position.
	 * 
	 * @param p The player for this event
	 * @param zap The ZAPlayer instance for the player in this event
	 * @param sitting Whether or not this event is making the player sit or not
	 */
	public LastStandEvent(Player p, ZAPlayer zap, boolean sitting) {
		this.p = p;
		this.zap = zap;
		this.sitting = sitting;
		game = zap.getGame();
		cancel = false;
	}

	@Override public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * Gets the player associated with this event.
	 * 
	 * @return The player that calls this event
	 */
	public Player getPlayer() {
		return p;
	}

	/**
	 * Gets the game the player is involved in.
	 * 
	 * @return The game the player is involved in
	 */
	public ZAGame getPlayerGame() {
		return game;
	}

	/**
	 * Gets the ZAPlayer instance attached to this event.
	 * 
	 * @return The ZAPlayer instance attached to this event
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
	 * Checks if the action toggled to is the player sitting down or not.
	 * 
	 * @return Whether or not the player is being sit down
	 */
	public boolean isSitDown() {
		return sitting;
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
