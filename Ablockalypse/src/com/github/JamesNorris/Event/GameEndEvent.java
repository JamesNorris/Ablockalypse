package com.github.JamesNorris.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.github.JamesNorris.Interface.ZAGame;

public class GameEndEvent extends Event {
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
	private int score;

	/**
	 * Called when a game ends.
	 * 
	 * @param game The game that is being ended
	 * @param score The final score of the game
	 */
	public GameEndEvent(ZAGame game, int score) {
		this.game = game;
		this.score = score;
	}

	/**
	 * Finds the game that has just ended.
	 * 
	 * @return The game that ended
	 */
	public ZAGame getGame() {
		return game;
	}

	@Override public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * Gets the score that the game ended with.
	 * 
	 * @return The score the game ended with
	 */
	public int getScore() {
		return score;
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
