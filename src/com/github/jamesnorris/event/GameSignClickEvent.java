package com.github.jamesnorris.event;

import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class GameSignClickEvent extends AblockalypseEvent implements Cancellable {
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
    private Player player;
    private Sign sign;

    /**
     * An event called when a player clicks a sign with the default required first line.
     * 
     * @param sign The sign clicked
     * @param player The player that clicks the sign
     */
    public GameSignClickEvent(Sign sign, Player player) {
        this.sign = sign;
        this.player = player;
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
        return player;
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
