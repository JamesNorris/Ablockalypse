package com.github.JamesNorris.Event;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.github.JamesNorris.Interface.ZAGame;

public class GameCreateEvent extends AblockalypseEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

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
    private Player player;
    private CommandSender sender;

    /**
     * Called when a game is created. Games can either be created by a CommandSender or a Player.
     * If only a CommandSender or Player created the game, leave the one that didn't null when called.
     * 
     * @param game The game that has been created
     * @param sender The sender that may have created the game
     * @param player The player that may have created the game
     */
    public GameCreateEvent(ZAGame game, CommandSender sender, Player player) {
        this.game = game;
        this.sender = sender;
        cancel = false;
    }

    /**
     * Gets the game that was created.
     * 
     * @return The game that was created
     */
    public ZAGame getGame() {
        return game;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Gets the creator of the game as a player.
     * If the sender is a commandsender, this may be null.
     * 
     * @return The player creator of the game
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the creator of the game as a commandsender.
     * If the sender is a player, this may be null.
     * 
     * @return The commandsender creator of the game
     */
    public CommandSender getSender() {
        return sender;
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
     * Checks if the creator of the game was a commandsender.
     * 
     * @return Whether or not the creator of the game was a commandsender
     */
    public boolean isCommandSender() {
        return (sender != null) ? true : false;
    }

    /**
     * Checks if the creator of the game was a player.
     * 
     * @return Whether or not the creator of the game was a player
     */
    public boolean isPlayer() {
        return (player != null) ? true : false;
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
