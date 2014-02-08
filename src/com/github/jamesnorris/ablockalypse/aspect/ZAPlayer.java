package com.github.jamesnorris.ablockalypse.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.ItemManager;
import com.github.jamesnorris.ablockalypse.enumerated.PlayerStatus;
import com.github.jamesnorris.ablockalypse.enumerated.PowerupType;
import com.github.jamesnorris.ablockalypse.enumerated.Setting;
import com.github.jamesnorris.ablockalypse.enumerated.ZAPerk;
import com.github.jamesnorris.ablockalypse.enumerated.ZASound;
import com.github.jamesnorris.ablockalypse.event.LastStandEvent;
import com.github.jamesnorris.ablockalypse.event.PlayerJoinGameEvent;
import com.github.jamesnorris.ablockalypse.event.bukkit.PlayerJoin;
import com.github.jamesnorris.ablockalypse.threading.inherent.LastStandFallenTask;
import com.github.jamesnorris.ablockalypse.utility.BuyableItemData;
import com.github.jamesnorris.ablockalypse.utility.SerialLocation;

public class ZAPlayer extends ZACharacter {
    private Player player;
    private Game game;
    private PlayerStatus status = PlayerStatus.NORMAL;
    private List<ZAPerk> perks = new ArrayList<ZAPerk>();
    private double pointGainModifier = 1, pointLossModifier = 1;
    private PlayerState beforeGame, beforeLS;
    private int points, kills;
    private boolean sentIntoGame, instakill, removed;

    @SuppressWarnings("unchecked") public ZAPlayer(Map<String, Object> savings) {
        super(savings);
        player = super.getPlayer();
        game = super.getGame();
        status = super.getStatus();
        Map<String, Object> beforeGameState = (Map<String, Object>) savings.get("before_game_state");
        if (beforeGameState != null) {
            beforeGame = new PlayerState(beforeGameState);
        }
        Map<String, Object> beforeLSState = (Map<String, Object>) savings.get("before_last_stand_state");
        if (beforeLSState != null) {
            beforeLS = new PlayerState(beforeLSState);
        }
        if (!player.isOnline()) {
            PlayerJoin.queuePlayer(this, SerialLocation.returnLocation((SerialLocation) savings.get("player_in_game_location")), savings);
            return;
        }
        removed = false;
        loadSavedVersion(savings);
    }

    public ZAPlayer(Player player, Game game) {
        super(player, game);
        this.player = player;
        this.game = game;
    }

    public void addKills(int kills) {
        this.kills += kills;
    }

    public void addPerk(ZAPerk perk) {
        if (hasPerk(perk)) {
            return;// prevents recursion
        }
        perks.add(perk);
        perk.givePerk(this);
    }

    public void addPoints(int points) {
        this.points += points * pointGainModifier;
    }

    public void clearPerks() {
        for (ZAPerk perk : perks) {
            perk.removePerk(this);
        }
        perks.clear();
    }

    public void decrementKills() {
        subtractKills(1);
    }

    public int getKills() {
        return kills;
    }

    @Override public int getLoadPriority() {
        return 1;
    }

    public List<ZAPerk> getPerks() {
        return perks;
    }

    public double getPointGainModifier() {
        return pointGainModifier;
    }

    public double getPointLossModifier() {
        return pointLossModifier;
    }

    public int getPoints() {
        return points;
    }

    @Override public Map<String, Object> getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("uuid", getUUID());
        savings.put("game_name", game.getName());
        savings.put("points", points);
        savings.put("kills", kills);
        savings.put("point_gain_modifier", pointGainModifier);
        savings.put("point_loss_modifier", pointLossModifier);
        if (beforeGame != null) {
            savings.put("before_game_state", beforeGame.getSave());
        }
        if (beforeLS != null) {
            savings.put("before_last_stand_state", beforeLS.getSave());
        }
        List<Integer> perkIds = new ArrayList<Integer>();
        for (ZAPerk perk : perks) {
            perkIds.add(perk.getId());
        }
        savings.put("perk_ids", perkIds);
        savings.put("has_been_sent_into_the_game", sentIntoGame);
        savings.put("has_instakill", instakill);
        savings.put("player_in_game_location", player == null ? null : new SerialLocation(player.getLocation()));
        savings.putAll(super.getSave());
        return savings;
    }

    public void giveItem(ItemStack item) {
        Ablockalypse.getExternal().getItemFileManager().giveItem(player, item);
    }

    public void givePowerup(PowerupType type, Entity cause) {
        type.play(game, player, cause, data);
    }

    public boolean hasBeenSentIntoGame() {
        return sentIntoGame;
    }

    public boolean hasInstaKill() {
        return instakill;
    }

    public boolean hasPerk(ZAPerk perk) {
        return perks.contains(perk);
    }

    public void incrementKills() {
        addKills(1);
    }

    public boolean isInLastStand() {
        return status == PlayerStatus.LAST_STAND;
    }

    public boolean isInLimbo() {
        return status == PlayerStatus.LIMBO;
    }

    public boolean isTeleporting() {
        return status == PlayerStatus.TELEPORTING;
    }

    public void loadPlayerToGame(String name, boolean showMessages) {
        /* Use an old game to add the player to the game */
        if (data.isGame(name)) {
            Game zag = data.getGame(name, false);
            PlayerJoinGameEvent GPJE = new PlayerJoinGameEvent(this, zag);
            Bukkit.getPluginManager().callEvent(GPJE);
            if (!GPJE.isCancelled()) {
                int max = (Integer) Setting.MAX_PLAYERS.getSetting();
                if (zag.getPlayers().size() < max) {
                    if (game.getMainframe() == null) {
                        Teleporter newMainframe = new Teleporter(game, player.getLocation().clone().subtract(0, 1, 0));
                        game.setMainframe(newMainframe);
                        newMainframe.setBlinking(false);
                    }
                    removed = false;
                    zag.addPlayer(player);
                    prepare();
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

    public void loadSavedVersion(Map<String, Object> savings) {
        points = (Integer) savings.get("points");
        kills = (Integer) savings.get("kills");
        pointGainModifier = (Integer) savings.get("point_gain_modifier");
        pointLossModifier = (Integer) savings.get("point_loss_modifier");
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
        perks = loadedPerks;
        sentIntoGame = (Boolean) savings.get("has_been_sent_into_the_game");
        instakill = (Boolean) savings.get("has_instakill");
    }

    @Override public void onGameEnd() {
        player.sendMessage(ChatColor.BOLD + "" + ChatColor.GRAY + "The game has ended. You made it to level " + game.getLevel());
        ZASound.END.play(player.getLocation());
        game.removePlayer(player);
    }

    @Override public void onLevelEnd() {
        ZASound.PREV_LEVEL.play(player.getLocation());
        //@formatter:off
        player.sendMessage(ChatColor.BOLD + "Level " + ChatColor.RESET + ChatColor.RED + game.getLevel() + ChatColor.RESET + ChatColor.BOLD 
        + " over... Next level: " + ChatColor.RED + (game.getLevel() + 1) + "\n" + ChatColor.RESET + ChatColor.BOLD + "Time to next level: "
        + ChatColor.RED + Setting.LEVEL_TRANSITION_TIME.getSetting() + ChatColor.RESET + ChatColor.BOLD + " seconds.");
        //@formatter:on
        // showPoints();//with the new scoreboard, there is not longer any need
    }

    @Override public void onNextLevel() {
        int level = game.getLevel();
        if (level != 0) {
            player.setLevel(level);
            player.sendMessage(ChatColor.BOLD + "Level " + ChatColor.RESET + ChatColor.RED + level + ChatColor.RESET + ChatColor.BOLD + " has started.");
            if (level != 1) {
                showPoints();
            }
        }
    }

    @Override public void remove() {
        if (removed) {
            return;// prevents recursion with ZAMob.kill(), which calls game.removeObject(), which calls this, which calls ZAMob.kill()... you get the idea
        }
        restoreStatus();
        game = null;
        super.remove();
        removed = true;
    }

    public void removeFromGame() {
        if (removed) {
            return;// prevents recursion with remove()
        }
        remove();
    }

    public void removePerk(ZAPerk perk) {
        if (!hasPerk(perk)) {
            return;// prevents recursion
        }
        perks.remove(perk);
        perk.removePerk(this);
    }

    @Override public void sendToMainframe(String message, String reason) {
        if (message != null) {
            player.sendMessage(message);
        }
        Location loc = game.getMainframe().getLocation().clone().add(0, 1, 0);
        Chunk c = loc.getChunk();
        if (!c.isLoaded()) {
            c.load();
        }
        player.teleport(loc);
        if (!sentIntoGame) {
            ZASound.START.play(loc);
            sentIntoGame = true;
        } else {
            ZASound.TELEPORT.play(loc);
        }
        if ((Boolean) Setting.DEBUG.getSetting()) {
            System.out.println("[Ablockalypse] [DEBUG] Mainframe TP reason: (" + game.getName() + ") " + reason);
        }
    }

    public void setInstaKill(boolean instakill) {
        this.instakill = instakill;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setPointGainModifier(double modifier) {
        pointGainModifier = modifier;
    }

    public void setPointLossModifier(double modifier) {
        pointLossModifier = modifier;
    }

    public void setPoints(int points) {
        double modifier = 1;
        if (points > this.points) {
            modifier = pointGainModifier;
        } else if (points < this.points) {
            modifier = pointLossModifier;
        }
        this.points = (int) Math.round(points * modifier);
    }

    public void setSentIntoGame(boolean sent) {
        sentIntoGame = sent;
    }

    public void showPoints() {
        for (ZAPlayer zap2 : game.getPlayers()) {
            Player p2 = zap2.getPlayer();
            player.sendMessage(ChatColor.RED + p2.getName() + ChatColor.RESET + " - " + ChatColor.GRAY + zap2.getPoints());
        }
    }

    public void subtractKills(int kills) {
        if (this.kills < kills) {
            this.kills = 0;
            return;
        }
        this.kills -= kills;
    }

    public void subtractPoints(int points) {
        if (this.points < points * pointLossModifier) {
            this.points = 0;
            return;
        }
        this.points -= points * pointLossModifier;
    }

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
            beforeLS.update();
            player.sendMessage(ChatColor.GRAY + "You have been picked up!");
            game.broadcast(ChatColor.RED + player.getName() + ChatColor.GRAY + " has been revived.", player);
            status = PlayerStatus.NORMAL;
            // Breakable.setSitting(player, false);
            getSeat().removePassenger();
            getSeat().remove();
            player.setCanPickupItems(true);
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
        beforeGame = new PlayerState(player);
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
        ItemManager itemManager = Ablockalypse.getExternal().getItemFileManager();
        if (itemManager != null && itemManager.getStartingItemsMap() != null) {
            Map<Integer, BuyableItemData> startingItems = itemManager.getStartingItemsMap();
            for (int id : startingItems.keySet()) {
                itemManager.giveItem(player, startingItems.get(id).toItemStack());
            }
        }
        rename("", player.getName(), "0");
        player.updateInventory();
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
        if (beforeGame != null) {
            beforeGame.update();
        }
    }

    private void sitDown() {
        LastStandEvent lse = new LastStandEvent(player, this, true);
        Bukkit.getServer().getPluginManager().callEvent(lse);
        if (!lse.isCancelled()) {
            player.sendMessage(ChatColor.GRAY + "You have been knocked down!");
            if (getGame().getRemainingPlayers().size() < 1 && (Boolean) Setting.END_ON_LAST_PLAYER_LAST_STAND.getSetting()) {
                removeFromGame();
                return;
            }
            beforeLS = new PlayerState(player);
            status = PlayerStatus.LAST_STAND;
            Entity v = player.getVehicle();
            if (v != null) {
                v.remove();
            }
            rename("", player.getName(), "[LS]");
            player.setFoodLevel(0);
            player.setHealth((Double) Setting.LAST_STAND_HEALTH_THRESHOLD.getSetting());
            ZASound.LAST_STAND.play(player.getLocation());
            getSeat().moveLocation(player.getLocation());
            getSeat().sit(player);
            player.getInventory().clear();
            player.setCanPickupItems(false);
            game.broadcast(ChatColor.RED + player.getName() + ChatColor.GRAY + " is down and needs revival", player);
            new LastStandFallenTask(this, true);
            if ((Boolean) Setting.LOSE_PERKS_ON_LAST_STAND.getSetting()) {
                clearPerks();
            }
        }
    }
}
