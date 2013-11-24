package com.github.jamesnorris.ablockalypse.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.github.jamesnorris.ablockalypse.aspect.entity.ZAPlayer;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;

public class PlayerJoinGameEvent extends AblockalypseEvent implements Cancellable {
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
     * Called when a player joins a game.
     * 
     * @param zap The player that joins the game
     * @param game The game that the player is joining
     */
    public PlayerJoinGameEvent(ZAPlayer zap, Game game) {
        this.zap = zap;
        this.game = game;
    }

    /**
     * Finds the game that the player is joining.
     * 
     * @return The game that the player is joining
     */
    public Game getGame() {
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
