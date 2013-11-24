package com.github.jamesnorris.ablockalypse.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.github.jamesnorris.ablockalypse.aspect.entity.ZAPlayer;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;

public class PlayerLeaveGameEvent extends AblockalypseEvent implements Cancellable {
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
    private Game game;
    private ZAPlayer zap;

    /**
     * Called when a player leaves a game.
     * 
     * @param zap The player that leaves the game
     * @param game The game that the player is leaving
     */
    public PlayerLeaveGameEvent(ZAPlayer zap, Game game) {
        this.zap = zap;
        this.game = game;
    }

    /**
     * Finds the game that the player is leaving.
     * 
     * @return The game that the player is leaving
     */
    public Game getGame() {
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
    public ZAPlayer getZAPlayerBase() {
        return zap;
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
