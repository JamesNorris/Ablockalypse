package com.github.jamesnorris.util;

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
    private int level, health, foodLevel, fireTicks, lastDamage, maxHealth, maxAir, maxNoDamageTicks, noDamageTicks, remainingAir;
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
        this.level = player.getLevel();
        this.health = player.getHealth();
        this.foodLevel = player.getFoodLevel();
        this.fireTicks = player.getFireTicks();
        this.lastDamage = player.getLastDamage();
        this.maxHealth = player.getMaxHealth();
        this.maxAir = player.getMaximumAir();
        this.maxNoDamageTicks = player.getMaximumNoDamageTicks();
        this.noDamageTicks = player.getNoDamageTicks();
        this.remainingAir = player.getRemainingAir();
        this.inventory = player.getInventory();
        this.exp = player.getExp();
        this.saturation = player.getSaturation();
        this.fallDistance = player.getFallDistance();
        this.exhaustion = player.getExhaustion();
        this.flySpeed = player.getFlySpeed();
        this.walkSpeed = player.getWalkSpeed();
        this.location = player.getLocation();
        this.bedSpawn = player.getBedSpawnLocation();
        this.compassTarget = player.getCompassTarget();
        this.sleepingIgnored = player.isSleepingIgnored();
        this.allowFlight = player.getAllowFlight();
        this.canPickupItems = player.getCanPickupItems();
        this.flying = player.isFlying();
        this.banned = player.isBanned();
        this.op = player.isOp();
        this.sneaking = player.isSneaking();
        this.sprinting = player.isSprinting();
        this.whitelisted = player.isWhitelisted();
        this.potionEffects = player.getActivePotionEffects();
        this.name = player.getName();
        this.customName = player.getCustomName();
        this.displayName = player.getDisplayName();
        this.listName = player.getPlayerListName();
        this.itemInHand = player.getItemInHand();
        this.lastDamageCause = player.getLastDamageCause();
        this.time = player.getPlayerTime();
        this.weather = player.getPlayerWeather();
        this.scoreboard = player.getScoreboard();
        this.velocity = player.getVelocity();
        this.gameMode = player.getGameMode();
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

    public void update() {
        Player player = Bukkit.getPlayer(name);
        update(player);
    }
    
    @Override public PlayerState clone() {
        return new PlayerState(Bukkit.getPlayer(name));
    }
}
