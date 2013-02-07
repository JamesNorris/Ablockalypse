package com.github.JamesNorris.Interface;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.JamesNorris.Enumerated.PlayerStatus;
import com.github.JamesNorris.Enumerated.PowerupType;
import com.github.JamesNorris.Enumerated.ZAPerk;

public interface ZAPlayer extends ZALiving {
    /**
     * Adds a perk and effect to the player.
     * 
     * @param perk The type of perk to add to the player
     * @param duration The duration of the perk
     * @param power The power of the perk
     */
    public void addPerk(ZAPerk perk, int duration, int power);

    /**
     * Gives points to the player.
     * 
     * @param i The amount of points to give the player
     */
    public void addPoints(int i);

    /**
     * Gets the kills the player has.
     * 
     * @return The amount of kills the player has
     */
    public int getKills();

    /**
     * Returns the players' name.
     * 
     * @return The name of the player
     */
    public String getName();

    /**
     * Gets a list of perks that the player has attached to them.
     * 
     * @return A list of perks used by the player
     */
    public ArrayList<ZAPerk> getPerks();

    /**
     * Gets the Player instance of this ZAPlayer.
     * 
     * @return The player instance involved with this instance
     */
    public Player getPlayer();

    /**
     * Gets the points the player currently has.
     * 
     * @return The amount of points the player has
     */
    public int getPoints();

    /**
     * Gets the status of the player.
     * 
     * @return The current status of the player
     */
    public PlayerStatus getStatus();

    /**
     * Gives the player the specified powerup.
     * 
     * @param type The type of powerup to give the player
     * @param cause The entity that originated this event
     */
    public void givePowerup(PowerupType type, Entity cause);

    /**
     * Checks if the player has insta-kill enabled.
     * 
     * @return Whether or not the player has insta-kill
     */
    public boolean hasInstaKill();

    /**
     * Returns true if the player is in last stand
     * 
     * @return Whether or not the player is in last stand
     */
    public boolean isInLastStand();

    /**
     * Gets whether or not the player is in limbo.
     * 
     * @return Whether or not the player is in limbo
     */
    public boolean isInLimbo();

    /**
     * Checks if the player is teleporting or not.
     * 
     * @return Whether or not the player is teleporting
     */
    public boolean isTeleporting();

    /**
     * Checks if the name given is the name of a game. If not, creates a new game.
     * Then, adds the player to that game with all settings completed.
     * 
     * @param name The name of the player to be loaded into the game
     */
    public void loadPlayerToGame(String name);

    /**
     * Removes the player from the game, and removes all data from the player.
     */
    public void removeFromGame();

    /**
     * Teleports the player to the mainframe of the game.
     * 
     * @param reason The reason for teleportation for the debug mode
     */
    public void sendToMainframe(String reason);

    /**
     * Enables insta-kill for this player.
     * 
     * @param tf Whether or not to start/cancel insta-kill
     */
    public void setInstaKill(boolean tf);

    /**
     * Sets the amount of kills that the player has.
     * NOTE: This does not affect score.
     * 
     * @param i The amount of kills to set the player to
     */
    public void setKills(int i);

    /**
     * Changes the player limbo status.
     */
    public void setLimbo(boolean tf);

    /**
     * Sets the amount of points the player has.
     * 
     * @param i The amount of points to set the player to
     */
    public void setPoints(int i);

    /**
     * Changes the teleportation status of the player.
     * 
     * @param tf What to change the status to
     */
    public void setTeleporting(boolean tf);

    /**
     * Removes points from the player.
     * 
     * @param i The amount of points to remove from the player
     */
    public void subtractPoints(int i);

    /**
     * Teleport the player to the specified location, with the specified reason for the debug mode.
     * 
     * @param location The location to teleport to
     * @param reason The reason for teleportation
     */
    public void teleport(Location location, String reason);

    /**
     * Teleports the player to the specified location,
     * with the specified arguments, and the specified reason for the debug mode.
     * 
     * @param world The world to teleport in
     * @param x The x coord to teleport to
     * @param y The y coord to teleport to
     * @param z The z coord to teleport to
     * @param reason The reason for teleportation
     */
    public void teleport(World world, int x, int y, int z, String reason);

    /**
     * Toggles sitting for the player.
     */
    public void toggleLastStand();

    /**
     * Gets the block that the player is looking at, within the given distance.
     * If the player is looking at a block farther than the given distance, this will return null.
     * The higher the distance, the slower the method will be.
     * 
     * @param distance The maximum distance to check for the block
     * @return The block that the player is looking at
     */
    public Block getAim(int distance);
}
