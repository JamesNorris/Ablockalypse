package com.github.JamesNorris.Event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.github.JamesNorris.Enumerated.MessageDirection;
import com.github.JamesNorris.Util.SpecificMessage;

public class MessageTransferEvent extends AblockalypseEvent implements Cancellable {
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
    private SpecificMessage message;
    private MessageDirection direction;

    /**
     * Called when a message is transferred from Ablockalypse to an outside source.
     * 
     * @param message The message being sent
     * @param direction The direction of the message
     */
    public MessageTransferEvent(SpecificMessage message, MessageDirection direction) {
        this.message = message;
        this.direction = direction;
    }

    public SpecificMessage getMessage() {
        return message;
    }

    public MessageDirection getDirection() {
        return direction;
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
