package com.github.utility;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

public class PlayerState implements Cloneable {
    private int level, foodLevel, fireTicks, maxAir, maxNoDamageTicks, noDamageTicks, remainingAir;
    private double health, lastDamage, maxHealth;
    private PlayerInventory inventory;
    private float exp, saturation, fallDistance, exhaustion, flySpeed, walkSpeed;
    private Location location, bedSpawn, compassTarget;
    private boolean sleepingIgnored, allowFlight, canPickupItems, flying, banned, op, sneaking, sprinting, whitelisted;
    private Collection<PotionEffect> potionEffects;
    private String name, customName, displayName, listName;
    private ItemStack itemInHand;
    private EntityDamageEvent lastDamageCause;
    private long time;
    private WeatherType weather;
    private Scoreboard scoreboard;
    private Vector velocity;
    private GameMode gameMode;

    public PlayerState(Player player) {
        level = player.getLevel();
        health = player.getHealth();
        foodLevel = player.getFoodLevel();
        fireTicks = player.getFireTicks();
        lastDamage = player.getLastDamage();
        maxHealth = player.getMaxHealth();
        maxAir = player.getMaximumAir();
        maxNoDamageTicks = player.getMaximumNoDamageTicks();
        noDamageTicks = player.getNoDamageTicks();
        remainingAir = player.getRemainingAir();
        inventory = player.getInventory();
        exp = player.getExp();
        saturation = player.getSaturation();
        fallDistance = player.getFallDistance();
        exhaustion = player.getExhaustion();
        flySpeed = player.getFlySpeed();
        walkSpeed = player.getWalkSpeed();
        location = player.getLocation();
        bedSpawn = player.getBedSpawnLocation();
        compassTarget = player.getCompassTarget();
        sleepingIgnored = player.isSleepingIgnored();
        allowFlight = player.getAllowFlight();
        canPickupItems = player.getCanPickupItems();
        flying = player.isFlying();
        banned = player.isBanned();
        op = player.isOp();
        sneaking = player.isSneaking();
        sprinting = player.isSprinting();
        whitelisted = player.isWhitelisted();
        potionEffects = player.getActivePotionEffects();
        name = player.getName();
        customName = player.getCustomName();
        displayName = player.getDisplayName();
        listName = player.getPlayerListName();
        itemInHand = player.getItemInHand();
        lastDamageCause = player.getLastDamageCause();
        time = player.getPlayerTime();
        weather = player.getPlayerWeather();
        scoreboard = player.getScoreboard();
        velocity = player.getVelocity();
        gameMode = player.getGameMode();
    }

    @Override public PlayerState clone() {
        return new PlayerState(Bukkit.getPlayer(name));
    }

    public void update() {
        Player player = Bukkit.getPlayer(name);
        update(player);
    }

    public void update(Player player) {
        player.setLevel(level);
        player.setHealth(health);
        player.setFoodLevel(foodLevel);
        player.setFireTicks(fireTicks);
        player.setLastDamage(lastDamage);
        player.setMaxHealth(maxHealth);
        player.setMaximumAir(maxAir);
        player.setMaximumNoDamageTicks(maxNoDamageTicks);
        player.setNoDamageTicks(noDamageTicks);
        player.setRemainingAir(remainingAir);
        player.getInventory().clear();
        player.getInventory().setContents(inventory.getContents());
        player.setExp(exp);
        player.setSaturation(saturation);
        player.setFallDistance(fallDistance);
        player.setExhaustion(exhaustion);
        player.setFlySpeed(flySpeed);
        player.setWalkSpeed(walkSpeed);
        player.teleport(location);
        player.setBedSpawnLocation(bedSpawn);
        player.setCompassTarget(compassTarget);
        player.setSleepingIgnored(sleepingIgnored);
        player.setAllowFlight(allowFlight);
        player.setCanPickupItems(canPickupItems);
        player.setFlying(flying);
        player.setBanned(banned);
        player.setOp(op);
        player.setSneaking(sneaking);
        player.setSprinting(sprinting);
        player.setWhitelisted(whitelisted);
        player.addPotionEffects(potionEffects);
        player.setCustomName(customName);
        player.setDisplayName(displayName);
        player.setPlayerListName(listName);
        player.setItemInHand(itemInHand);
        player.setLastDamageCause(lastDamageCause);
        player.setPlayerTime(time, true);
        player.setPlayerWeather(weather);
        player.setScoreboard(scoreboard);
        player.setVelocity(velocity);
        player.setGameMode(gameMode);
        updateInventory(player);
    }

    @SuppressWarnings("deprecation") protected void updateInventory(Player player) {
        player.updateInventory();
    }
}
