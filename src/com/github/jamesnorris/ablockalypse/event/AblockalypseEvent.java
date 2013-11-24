package com.github.jamesnorris.ablockalypse.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AblockalypseEvent extends Event implements Cancellable {
    private static HandlerList handlers = new HandlerList();

    /**
     * Gets the handlerlist for this event.
     * 
     * @return The handlers for this event, in a list
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    private boolean cancel = false;

    public AblockalypseEvent() {}

    @Override public HandlerList getHandlers() {
        return handlers;
    }

    @Override public boolean isCancelled() {
        return cancel;
    }

    @Override public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
