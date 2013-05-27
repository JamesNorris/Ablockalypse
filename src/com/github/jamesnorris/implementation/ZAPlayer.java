package com.github.jamesnorris.implementation;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.PlayerStatus;
import com.github.jamesnorris.enumerated.PowerupType;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZAPerk;
import com.github.jamesnorris.enumerated.ZASound;
import com.github.jamesnorris.event.GamePlayerJoinEvent;
import com.github.jamesnorris.event.LastStandEvent;
import com.github.jamesnorris.implementation.serialized.SerialZAPlayer;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.inter.Permadata;
import com.github.jamesnorris.inter.Permadatable;
import com.github.jamesnorris.manager.ItemFileManager;
import com.github.jamesnorris.threading.LastStandFallenThread;
import com.github.jamesnorris.util.Breakable;
import com.github.jamesnorris.util.PlayerState;
import com.github.jamesnorris.util.ShotResult;

public class ZAPlayer implements GameObject, Permadatable {
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private int points = 0, kills = 0, pointGainMod = 1, absorption = 0, status = 3;
    private String name;
    private ArrayList<ZAPerk> perks = new ArrayList<ZAPerk>();
    private Player player;
    private boolean sentIntoGame, instakill;
    private PlayerState playerState;

    /**
     * Creates a new instance of a ZAPlayer, using an instance of a Player.
     * 
     * NOTE: This instance comes with a built-in ZASoundManager.
     * 
     * @param player The player to be made into this instance
     * @param game The game this player should be in
     */
    public ZAPlayer(Player player, Game game) {
        data.gameObjects.add(this);
        game.addObject(this);
        this.player = player;
        name = player.getName();
        this.game = game;
        data.players.put(player, this);
        player.setLevel(game.getLevel());
    }
    
    public boolean hasBeenSentIntoGame() {
        return sentIntoGame;
    }
    
    public void setSentIntoGame(boolean sent) {
        sentIntoGame = sent;
    }
    
    public void setState(PlayerState state) {
        playerState = state;
    }
    
    public PlayerState getState() {
        return playerState;
    }

    /**
     * Gives points to the player.
     * 
     * @param i The amount of points to give the player
     */
    public void addPoints(int i) {
        points += i * pointGainMod;
        rename(name, "" + points);
    }

    public void addToPerkList(ZAPerk perk) {
        perks.add(perk);
    }

    public void clearPerks() {
        perks.clear();
        player.getActivePotionEffects().clear();
        setHitAbsorption(0);
    }

    /**
     * Gets the block that the player is looking at, within the given distance.
     * If the player is looking at a block farther than the given distance, this will return null.
     * The higher the distance, the slower the method will be.
     * 
     * @param distance The maximum distance to check for the block
     * @return The block that the player is looking at
     */
    public Block getAim(int distance) {// TODO test
        Location loc = player.getLocation();
        World world = loc.getWorld();
        float x = loc.getBlockX();
        float y = loc.getBlockY();
        float z = loc.getBlockZ();
        float pitch = loc.getPitch();
        float yaw = loc.getYaw();
        Block returned = null;
        double XZslope = Math.tan(Math.toRadians(yaw));
        double Yslope = Math.tan(Math.toRadians(pitch));
        long runThrough = distance * (1 + (Math.round(XZslope) + Math.round(Yslope)) / 2);
        for (int i = 0; i <= runThrough && (returned == null || returned.getLocation().distance(loc) <= distance); ++i) {
            x += XZslope;
            y += Yslope;
            z += XZslope;
            Block b = world.getBlockAt(Math.round(x), Math.round(y), Math.round(z));
            returned = b;
            if (!returned.isEmpty()) {
                return returned;
            }
        }
        return null;
    }

    @Override public Block getDefiningBlock() {
        if (player != null) {
            return player.getLocation().clone().getBlock();
        } else {
            return null;
        }
    }

    /**
     * Gets the blocks that defines this object as an object.
     * 
     * @return The blocks assigned to this object
     */
    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        if (player != null) {
            blocks.add(player.getLocation().clone().subtract(0, 1, 0).getBlock());
        }
        return blocks;
    }

    /**
     * Gets the game the player is currently in
     * 
     * @return The game the player is in
     */
    @Override public Game getGame() {
        return game;
    }

    /**
     * Gets the hit damage that can be absorbed by this player.
     * 
     * @return The amount of damage to be absorbed each time this player is hit
     */
    public int getHitAbsorption() {
        return absorption;
    }

    /**
     * Gets the kills the player has.
     * 
     * @return The amount of kills the player has
     */
    public int getKills() {
        return kills;
    }

    /**
     * Returns the players' name.
     * 
     * @return The name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Gets a list of perks that the player has attached to them.
     * 
     * @return A list of perks used by the player
     */
    public ArrayList<ZAPerk> getPerks() {
        return perks;
    }

    /**
     * Gets the Player instance of this ZAPlayer.
     * 
     * @return The player instance involved with this instance
     */
    public Player getPlayer() {
        return player;
    }

    public int getPointGainMod() {
        return pointGainMod;
    }

    /**
     * Gets the points the player currently has.
     * 
     * @return The amount of points the player has
     */
    public int getPoints() {
        return points;
    }

    public PlayerStatus getStatus() {
        return PlayerStatus.getById(status);
    }

    /**
     * Gives the player the specified powerup.
     * 
     * @param type The type of powerup to give the player
     * @param cause The entity that originated this event
     */
    public void givePowerup(PowerupType type, Entity cause) {
        type.play(game, player, cause, data);
    }

    /**
     * Checks if the player has insta-kill enabled.
     * 
     * @return Whether or not the player has insta-kill
     */
    public boolean hasInstaKill() {
        return instakill;
    }

    /**
     * Returns true if the player is in last stand
     * 
     * @return Whether or not the player is in last stand
     */
    public boolean isInLastStand() {
        return status == 1;
    }

    /**
     * Gets whether or not the player is in limbo.
     * 
     * @return Whether or not the player is in limbo
     */
    public boolean isInLimbo() {
        return status == 2;
    }

    /**
     * Checks if the player is teleporting or not.
     * 
     * @return Whether or not the player is teleporting
     */
    public boolean isTeleporting() {
        return status == 4;
    }

    /**
     * Checks if the name given is the name of a game. If not, creates a new game.
     * Then, adds the player to that game with all settings completed.
     * 
     * @param name The name of the player to be loaded into the game
     */
    public void loadPlayerToGame(String name) {
        /* Use an old game to add the player to the game */
        if (data.games.containsKey(name)) {
            Game zag = data.games.get(name);
            GamePlayerJoinEvent GPJE = new GamePlayerJoinEvent(this, zag);
            Bukkit.getPluginManager().callEvent(GPJE);
            if (!GPJE.isCancelled()) {
                int max = (Integer) Setting.MAX_PLAYERS.getSetting();
                if (zag.getPlayers().size() < max) {
                    zag.addPlayer(player);
                    prepare();
                    if (game.getMainframe() == null) {
                        Location pLoc = player.getLocation();
                        game.setMainframe(new Mainframe(game, pLoc));
                    }
                    sendToMainframe("Loading player to a game");
                    player.sendMessage(ChatColor.GRAY + "You have joined the game: " + name);
                    return;
                } else {
                    player.sendMessage(ChatColor.RED + "This game has " + max + "/" + max + " players!");
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "That game does not exist!");
        }
    }

    /**
     * Removes the player completely.
     */
    @Override public void remove() {
        removeFromGame();
        game = null;
    }

    /**
     * Removes the player from the game, and removes all data from the player.
     */
    public void removeFromGame() {
        restoreStatus();
        if (game.getPlayers().contains(this)) {
            game.removePlayer(player);
        }
        data.gameObjects.remove(this);
    }

    /**
     * Teleports the player to the mainframe of the game.
     * 
     * @param reason The reason for teleportation for the debug mode
     */
    public void sendToMainframe(String reason) {
        player.sendMessage(ChatColor.GRAY + "Teleporting to mainframe...");
        Location loc = game.getMainframe().getLocation().clone().add(0, 1, 0);
        Chunk c = loc.getChunk();
        if (!c.isLoaded()) {
            c.load();
        }
        player.teleport(loc);
        if (sentIntoGame) {
            ZASound.START.play(loc);
            sentIntoGame = true;
        } else {
            ZASound.TELEPORT.play(loc);
        }
        if ((Boolean) Setting.DEBUG.getSetting()) {
            System.out.println("[Ablockalypse] [DEBUG] Mainframe TP reason: (" + game.getName() + ") " + reason);
        }
    }

    /**
     * Sets the amount of damage that the player can absorb each hit, before it hurts the player.
     * NOTE: If this nulls out the damage, the damage will automatically be set to 1 or higher.
     * 
     * @param i The damage absorption of this player
     */
    public void setHitAbsorption(int i) {
        absorption = i;
    }

    /**
     * Enables insta-kill for this player.
     * 
     * @param tf Whether or not to start/cancel insta-kill
     */
    public void setInstaKill(boolean tf) {
        instakill = tf;
    }

    /**
     * Sets the amount of kills that the player has.
     * NOTE: This does not affect score.
     * 
     * @param i The amount of kills to set the player to
     */
    public void setKills(int i) {
        kills = i;
    }

    /**
     * Changes the player limbo status.
     * 
     * @param tf Whether or not the player should be put in limbo mode
     */
    public void setLimbo(boolean tf) {
        status = tf ? 2 : 3;
    }

    public void setPointGainMod(int i) {
        pointGainMod = i;
    }

    /**
     * Sets the amount of points the player has.
     * 
     * @param i The amount of points to set the player to
     */
    public void setPoints(int i) {
        int difference = i - points;
        if (difference > 0) {
            difference *= pointGainMod;
        }
        points += difference;
    }

    public void setStatus(PlayerStatus status) {
        status.set(this);
    }

    /**
     * Changes the teleportation status of the player.
     * 
     * @param tf What to change the status to
     */
    public void setTeleporting(boolean tf) {
        status = tf ? 4 : 3;
    }

    public ShotResult shoot(int distance, int penetration, int damage, boolean wallsAffectPenetration, boolean hitsAffectPenetration) {
        HashMap<LivingEntity, Location> hits = new HashMap<LivingEntity, Location>();
        ArrayList<Location> nonEntityHits = new ArrayList<Location>();
        Location ownerLoc = player.getEyeLocation();
        Vector direction = ownerLoc.getDirection();
        int lastSince = 15;
        Location chunkLoc = null;
        for (int i = 0; i < distance; i++) {
            chunkLoc = ownerLoc.clone().add(direction);
            ++lastSince;
            if (lastSince >= 16) {
                lastSince = 0;
            }
            for (int div = 5; div > 0; div--) {// the higher that starting number, the greater the accuracy
                Vector shot = direction.clone().multiply(i + (1 / div));
                if (!shot.toLocation(ownerLoc.getWorld()).getBlock().isEmpty() && wallsAffectPenetration && !nonEntityHits.contains(shot.toLocation(ownerLoc.getWorld()))) {
                    --penetration;
                    nonEntityHits.add(shot.toLocation(ownerLoc.getWorld()));
                }
                if (chunkLoc != null && penetration > 0) {
                    for (Entity e : chunkLoc.getChunk().getEntities()) {
                        if (e instanceof LivingEntity) {
                            LivingEntity ent = (LivingEntity) e;
                            Location loc = ent.getEyeLocation();
                            double height = ent.getEyeHeight();
                            double width = Breakable.getNMSEntity(e).width;
                            double length = Breakable.getNMSEntity(e).length;
                            float thetaOne = Math.abs(loc.getYaw() - ownerLoc.getYaw());
                            float thetaTwo = 90 - thetaOne;
                            double viewWidth = width * Math.cos(thetaOne) + length * Math.cos(thetaTwo);
                            double Xdif = loc.getX() - ownerLoc.getX();
                            double Ydif = loc.getY() - ownerLoc.getY() - height;// foot Y
                            double Zdif = loc.getZ() - ownerLoc.getZ();
                            boolean Xhit = shot.getX() <= Xdif + viewWidth && shot.getX() >= Xdif - viewWidth;
                            boolean Yhit = shot.getY() <= Ydif + height && shot.getY() >= Ydif;
                            boolean Zhit = shot.getZ() <= Zdif + viewWidth && shot.getZ() >= Zdif - viewWidth;
                            if (Xhit && Yhit && Zhit && !hits.containsValue(ent.getLocation())) {
                                hits.put(ent, ent.getLocation());
                                ent.damage(damage, player);
                                if (hitsAffectPenetration) {
                                    --penetration;
                                }
                            }
                        }
                    }
                }
            }
        }
        return new ShotResult(hits, direction);
    }

    /**
     * Removes points from the player.
     * 
     * @param i The amount of points to remove from the player
     */
    public void subtractPoints(int i) {
        points -= i;
    }

    /**
     * Teleport the player to the specified location, with the specified reason for the debug mode.
     * 
     * @param location The location to teleport to
     * @param reason The reason for teleportation
     */
    public void teleport(Location location, String reason) {
        player.teleport(location);
        if ((Boolean) Setting.DEBUG.getSetting()) {
            System.out.println("[Ablockalypse] [DEBUG] TP reason: (" + game.getName() + ") " + reason);
        }
    }

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
    public void teleport(World world, int x, int y, int z, String reason) {
        teleport(world.getBlockAt(x, y, z).getLocation(), reason);
    }

    /**
     * Toggles sitting for the player.
     */
    public void toggleLastStand() {
        if (status != 1) {
            sitDown();
        } else {
            pickUp();
        }
    }

    private void pickUp() {
        LastStandEvent lse = new LastStandEvent(player, this, false);
        Bukkit.getServer().getPluginManager().callEvent(lse);
        if (!lse.isCancelled()) {
            for (PotionEffect pe : player.getActivePotionEffects()) {
                if (pe.getType() == PotionEffectType.CONFUSION) {
                    player.removePotionEffect(pe.getType());
                }
            }
            player.sendMessage(ChatColor.GRAY + "You have been picked up!");
            game.broadcast(ChatColor.RED + name + ChatColor.GRAY + " has been revived.", player);
            status = 3;
            Breakable.setSitting(player, false);
            if (player.getVehicle() != null) {
                player.getVehicle().remove();
            }
            player.setFoodLevel(20);
            Entity v = player.getVehicle();
            if (v != null) {
                v.remove();
            }
        }
    }

    /* Checks that the name and suffix are lower than 16 chars.
     * Any higher and the name is truncated. */
    private void rename(String name, String suffix) {
        String mod = name;
        int cutoff = 16 - (suffix.length() + 1);
        if (name.length() > cutoff) {
            mod = name.substring(0, cutoff);
        }
        player.setDisplayName(mod + " " + suffix);
    }

    /* Restoring the player status to the last saved status before the game. */
    private void restoreStatus() {
        if (status == 1) {
            toggleLastStand();
        }
        for (PotionEffect pe : player.getActivePotionEffects()) {
            PotionEffectType pet = pe.getType();
            player.removePotionEffect(pet);
        }
        playerState.update();
    }

    /* Saving the player status, so when the player is removed from the game, they are set back to where they were before. */
    @SuppressWarnings("deprecation") private void prepare() {
        playerState = new PlayerState(player);
        ZASound.START.play(player.getLocation());
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.setLevel(0);
        player.setExp(0);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(0);
        player.getActivePotionEffects().clear();
        player.getInventory().setArmorContents(null);
        player.setSleepingIgnored(true);
        player.setFireTicks(0);
        player.setFallDistance(0F);
        player.setExhaustion(0F);
        ItemFileManager itemManager = Ablockalypse.getExternal().getItemFileManager();
        if (itemManager != null && itemManager.getStartingItemsMap() != null) {
            HashMap<Integer, Integer> startingItems = itemManager.getStartingItemsMap();
            for (int id : startingItems.keySet()) {
                int amount = startingItems.get(id);
                itemManager.giveItem(player, new ItemStack(id, amount));
            }
        }
        rename(name, "0");
        player.updateInventory();
    }

    private void sitDown() {
        LastStandEvent lse = new LastStandEvent(player, this, true);
        Bukkit.getServer().getPluginManager().callEvent(lse);
        if (!lse.isCancelled()) {
            player.sendMessage(ChatColor.GRAY + "You have been knocked down!");
            if (getGame().getRemainingPlayers() >= 1 || !(Boolean) Setting.END_ON_LAST_PLAYER_LAST_STAND.getSetting()) {
                status = 1;
                Entity v = player.getVehicle();
                if (v != null) {
                    v.remove();
                }
                rename(name, "[LS]");
                player.setFoodLevel(0);
                player.setHealth((Integer) Setting.LAST_STAND_HEALTH_THRESHOLD.getSetting());
                ZASound.LAST_STAND.play(player.getLocation());
                Breakable.setSitting(player, true);
                game.broadcast(ChatColor.RED + name + ChatColor.GRAY + " is down and needs revival", player);
                new LastStandFallenThread(this, 240, true);
                if ((Boolean) Setting.LOSE_PERKS_ON_LAST_STAND.getSetting()) {
                    clearPerks();
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Integer.MAX_VALUE, 1));// TODO test
            } else {
                removeFromGame();
            }
        }
    }

    @Override public Permadata getSerializedVersion() {
        return new SerialZAPlayer(this);
    }
}
