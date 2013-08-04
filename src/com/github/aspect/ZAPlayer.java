package com.github.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.behavior.GameObject;
import com.github.enumerated.PlayerStatus;
import com.github.enumerated.PowerupType;
import com.github.enumerated.Setting;
import com.github.enumerated.ZAPerk;
import com.github.enumerated.ZASound;
import com.github.event.GamePlayerJoinEvent;
import com.github.event.LastStandEvent;
import com.github.event.bukkit.PlayerJoin;
import com.github.manager.ItemFileManager;
import com.github.threading.inherent.LastStandFallenThread;
import com.github.utility.PlayerState;
import com.github.utility.Seat;
import com.github.utility.Shot;
import com.github.utility.ShotResult;
import com.github.utility.serial.SavedVersion;
import com.github.utility.serial.SerialLocation;

public class ZAPlayer extends PermanentAspect implements GameObject {
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private String name;
    private int points = 0, kills = 0, pointGainMod = 1;
    private double absorption = 0;
    private ArrayList<ZAPerk> perks = new ArrayList<ZAPerk>();
    private Player player;
    private boolean sentIntoGame, instakill;
    private PlayerState playerState;
    private PlayerStatus status = PlayerStatus.NORMAL;
    private Seat seat;
    private SavedVersion savings = null;

    /**
     * Creates a new instance of a ZAPlayer, using an instance of a Player.
     * 
     * NOTE: This instance comes with a built-in ZASoundManager.
     * 
     * @param player The player to be made into this instance
     * @param game The game this player should be in
     */
    public ZAPlayer(Player player, Game game) {
        data.objects.add(this);
        game.addObject(this);
        this.player = player;
        this.name = player.getName();
        this.game = game;
        this.seat = new Seat(player.getLocation());
    }

    public void loadSavedVersion() {
        player = Bukkit.getPlayer((String) savings.get("name"));
        points = (Integer) savings.get("points");
        kills = (Integer) savings.get("kills");
        pointGainMod = (Integer) savings.get("point_gain_modifier");
        absorption = (Double) savings.get("hit_absorption");
        List<ItemStack> stacks = new ArrayList<ItemStack>();
        @SuppressWarnings("unchecked") List<Map<String, Object>> serialStacks = (List<Map<String, Object>>) savings.get("inventory");
        for (Map<String, Object> serialStack : serialStacks) {
            stacks.add(ItemStack.deserialize(serialStack));
        }
        player.getInventory().setContents(stacks.toArray(new ItemStack[stacks.size()]));
        List<ZAPerk> loadedPerks = new ArrayList<ZAPerk>();
        @SuppressWarnings("unchecked") List<Integer> perkIds = (List<Integer>) savings.get("perk_ids");
        for (Integer id : perkIds) {
            loadedPerks.add(ZAPerk.getById(id));
        }
        perks = (ArrayList<ZAPerk>) loadedPerks;
        sentIntoGame = (Boolean) savings.get("has_been_sent_into_the_game");
        instakill = (Boolean) savings.get("has_instakill");
        status = PlayerStatus.getById((Integer) savings.get("status_id"));
    }

    public ZAPlayer(SavedVersion savings) {
        data.objects.add(this);
        game = Ablockalypse.getData().getGame((String) savings.get("game_name"), true);
        game.addObject(this);
        this.savings = savings;
        this.name = (String) savings.get("name");
        if (player == null && !PlayerJoin.isQueued(this)) {
            PlayerJoin.queuePlayer(this, SerialLocation.returnLocation((SerialLocation) savings.get("player_in_game_location")));
        }
    }

    /**
     * Gives points to the player.
     * 
     * @param i The amount of points to give the player
     */
    public void addPoints(int i) {
        points += i * pointGainMod;
        rename(player.getName(), "" + points);
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
        for (int i = 0; i <= runThrough && (returned == null || returned.getLocation().distanceSquared(loc) <= Math.pow(distance, 2)); ++i) {
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

    @Override public String getHeader() {
        return this.getClass().getSimpleName() + " <UUID: " + getUUID() + ">";
    }

    /**
     * Gets the hit damage that can be absorbed by this player.
     * 
     * @return The amount of damage to be absorbed each time this player is hit
     */
    public double getHitAbsorption() {
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

    @Override public SavedVersion getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("game_name", game.getName());
        savings.put("points", points);
        savings.put("kills", kills);
        savings.put("point_gain_modifier", pointGainMod);
        savings.put("hit_absorption", absorption);
        savings.put("name", player.getName());
        List<Map<String, Object>> serialized = new ArrayList<Map<String, Object>>();
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack != null) {
                serialized.add(stack.serialize());
            }
        }
        savings.put("inventory", serialized);
        List<Integer> perkIds = new ArrayList<Integer>();
        for (ZAPerk perk : perks) {
            perkIds.add(perk.getId());
        }
        savings.put("perk_ids", perkIds);
        savings.put("has_been_sent_into_the_game", sentIntoGame);
        savings.put("has_instakill", instakill);
        savings.put("status_id", status.getId());
        savings.put("player_in_game_location", player == null ? null : new SerialLocation(player.getLocation()));
        return new SavedVersion(getHeader(), savings, getClass());
    }

    public PlayerState getState() {
        return playerState;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    @Override public UUID getUUID() {
        return player.getUniqueId();
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

    public boolean hasBeenSentIntoGame() {
        return sentIntoGame;
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
        return status == PlayerStatus.LAST_STAND;
    }

    /**
     * Gets whether or not the player is in limbo.
     * 
     * @return Whether or not the player is in limbo
     */
    public boolean isInLimbo() {
        return status == PlayerStatus.LIMBO;
    }

    /**
     * Checks if the player is teleporting or not.
     * 
     * @return Whether or not the player is teleporting
     */
    public boolean isTeleporting() {
        return status == PlayerStatus.TELEPORTING;
    }

    /**
     * Checks if the name given is the name of a game. If not, creates a new game.
     * Then, adds the player to that game with all settings completed.
     * 
     * @param name The name of the player to be loaded into the game
     */
    public void loadPlayerToGame(String name, boolean showMessages) {
        /* Use an old game to add the player to the game */
        if (data.isGame(name)) {
            Game zag = data.getGame(name, false);
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
                    sendToMainframe(showMessages ? ChatColor.GRAY + "Teleporting to mainframe..." : null, "Loading player to a game");
                    if (showMessages) {
                        player.sendMessage(ChatColor.GRAY + "You have joined the game: " + name);
                    }
                    return;
                } else {
                    if (showMessages) {
                        player.sendMessage(ChatColor.RED + "This game has " + max + "/" + max + " players!");
                    }
                }
            }
        } else if (showMessages) {
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
        data.objects.remove(this);
    }

    /**
     * Teleports the player to the mainframe of the game.
     * 
     * @param reason The reason for teleportation for the debug mode
     */
    public void sendToMainframe(String message, String reason) {
        if (message != null) {
            player.sendMessage(message);
        }
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
    public void setHitAbsorption(double i) {
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

    public void setSentIntoGame(boolean sent) {
        sentIntoGame = sent;
    }

    public void setState(PlayerState state) {
        playerState = state;
    }

    public void setStatus(PlayerStatus status) {
        if (this.status != status) {
            this.status = status;
            status.set(this);
        }
    }

    public ShotResult shoot(int distance, int damage) {
        return shoot(distance, 1, damage, true, true);
    }

    public ShotResult shoot(int distance, int penetration, int damage, boolean wallsAffectPenetration, boolean hitsAffectPenetration) {
        return new Shot(player, 7).shoot(distance, penetration, damage, wallsAffectPenetration, hitsAffectPenetration);
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
        if (status != PlayerStatus.LAST_STAND) {
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
            game.broadcast(ChatColor.RED + getName() + ChatColor.GRAY + " has been revived.", player);
            status = PlayerStatus.NORMAL;
            // Breakable.setSitting(player, false);
            seat.removePassenger();
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

    /* Saving the player status, so when the player is removed from the game, they are set back to where they were before. */
    @SuppressWarnings("deprecation") private void prepare() {
        playerState = new PlayerState(player);
        ZASound.START.play(player.getLocation());
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.setLevel(game.getLevel());
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
        rename(player.getName(), "0");
        player.updateInventory();
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
        if (status == PlayerStatus.LAST_STAND) {
            toggleLastStand();
        }
        for (PotionEffect pe : player.getActivePotionEffects()) {
            PotionEffectType pet = pe.getType();
            player.removePotionEffect(pet);
        }
        player.setDisplayName(player.getName());
        if (playerState != null) {
            playerState.update();
        }
    }

    private void sitDown() {
        LastStandEvent lse = new LastStandEvent(player, this, true);
        Bukkit.getServer().getPluginManager().callEvent(lse);
        if (!lse.isCancelled()) {
            player.sendMessage(ChatColor.GRAY + "You have been knocked down!");
            if (getGame().getRemainingPlayers() >= 1 || !(Boolean) Setting.END_ON_LAST_PLAYER_LAST_STAND.getSetting()) {
                status = PlayerStatus.LAST_STAND;
                Entity v = player.getVehicle();
                if (v != null) {
                    v.remove();
                }
                rename(player.getName(), "[LS]");
                player.setFoodLevel(0);
                player.setHealth((Integer) Setting.LAST_STAND_HEALTH_THRESHOLD.getSetting());
                ZASound.LAST_STAND.play(player.getLocation());
                seat.moveLocation(player.getLocation());
                seat.sit(player);
                game.broadcast(ChatColor.RED + getName() + ChatColor.GRAY + " is down and needs revival", player);
                new LastStandFallenThread(this, 240, true);
                if ((Boolean) Setting.LOSE_PERKS_ON_LAST_STAND.getSetting()) {
                    clearPerks();
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Integer.MAX_VALUE, 2));// TODO test
            } else {
                removeFromGame();
            }
        }
    }
}
