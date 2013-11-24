package com.github.jamesnorris.ablockalypse.aspect.intelligent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.aspect.PermanentAspect;
import com.github.jamesnorris.ablockalypse.utility.serial.SavedVersion;
import com.github.jamesnorris.ablockalypse.utility.serial.SerialLocation;

public class PlayerState extends PermanentAspect {
    private int level, foodLevel, fireTicks, maxAir, maxNoDamageTicks, noDamageTicks, remainingAir, heldSlot;
    private double health, lastDamage, maxHealth;
    private ItemStack[] inventoryContents, armorContents;
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
        heldSlot = player.getInventory().getHeldItemSlot();
        inventoryContents = player.getInventory().getContents();
        armorContents = player.getInventory().getArmorContents();
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

    @SuppressWarnings("unchecked") public PlayerState(SavedVersion save) {
        level = (Integer) save.get("level");
        foodLevel = (Integer) save.get("food_level");
        fireTicks = (Integer) save.get("fire_ticks");
        maxAir = (Integer) save.get("max_air");
        maxNoDamageTicks = (Integer) save.get("max_no_damage_ticks");
        noDamageTicks = (Integer) save.get("no_damage_ticks");
        remainingAir = (Integer) save.get("remaining_air");
        heldSlot = (Integer) save.get("held_slot");
        health = (Double) save.get("health");
        lastDamage = (Double) save.get("last_damage");
        maxHealth = (Double) save.get("max_health");
        List<Map<String, Object>> serialInventoryContents = (List<Map<String, Object>>) save.get("inventory_contents");
        ItemStack[] contents = new ItemStack[serialInventoryContents.size()];
        for (int i = 0; i < serialInventoryContents.size(); i++) {
            contents[i] = ItemStack.deserialize(serialInventoryContents.get(i));
        }
        inventoryContents = contents;
        List<Map<String, Object>> serialArmorContents = (List<Map<String, Object>>) save.get("armor_contents");
        ItemStack[] contents2 = new ItemStack[serialArmorContents.size()];
        for (int i = 0; i < serialArmorContents.size(); i++) {
            contents2[i] = ItemStack.deserialize(serialArmorContents.get(i));
        }
        armorContents = contents2;
        exp = (Float) save.get("exp");
        saturation = (Float) save.get("saturation");
        fallDistance = (Float) save.get("fall_distance");
        exhaustion = (Float) save.get("exhaustion");
        flySpeed = (Float) save.get("fly_speed");
        walkSpeed = (Float) save.get("walk_speed");
        location = SerialLocation.returnLocation((SerialLocation) save.get("location"));
        if (save.get("bed_spawn") != null) {
            bedSpawn = SerialLocation.returnLocation((SerialLocation) save.get("bed_spawn"));
        }
        if (save.get("compass_target") != null) {
            compassTarget = SerialLocation.returnLocation((SerialLocation) save.get("compass_target"));
        }
        sleepingIgnored = (Boolean) save.get("sleeping_ignored");
        allowFlight = (Boolean) save.get("allow_flight");
        canPickupItems = (Boolean) save.get("can_pickup_items");
        flying = (Boolean) save.get("flying");
        banned = (Boolean) save.get("banned");
        op = (Boolean) save.get("op");
        sneaking = (Boolean) save.get("sneaking");
        sprinting = (Boolean) save.get("sprinting");
        whitelisted = (Boolean) save.get("whitelisted");
        potionEffects = new ArrayList<PotionEffect>();
        List<Map<String, Object>> serialPotionEffects = (List<Map<String, Object>>) save.get("potion_effects");
        for (Map<String, Object> serialEffect : serialPotionEffects) {
            potionEffects.add(new PotionEffect(serialEffect));
        }
        name = (String) save.get("name");
        customName = (String) save.get("custom_name");
        displayName = (String) save.get("display_name");
        listName = (String) save.get("list_name");
        itemInHand = ItemStack.deserialize((Map<String, Object>) save.get("item_in_hand"));
        time = (Long) save.get("time");
        weather = save.get("weather_name") == null ? WeatherType.CLEAR : WeatherType.valueOf((String) save.get("weather_name"));// common null pointer
        gameMode = GameMode.getByValue((Integer) save.get("game_mode"));
    }

    @Override public String getHeader() {
        return this.getClass().getSimpleName() + " <PLAYER: " + name + ">";
    }

    public OfflinePlayer getPlayer() {
        OfflinePlayer player = Bukkit.getPlayer(name);
        if (player == null) {
            player = Bukkit.getOfflinePlayer(name);
        }
        if (player == null || !player.hasPlayedBefore()) {
            // npes will be thrown... player doesnt exist and never did (why was it saved?)
            Ablockalypse.getErrorTracker().crash("A PlayerState attempted to get a player that never existed!", 20);
            return null;
        }
        return player;
    }

    @Override public SavedVersion getSave() {
        Map<String, Object> save = new HashMap<String, Object>();
        save.put("level", level);
        save.put("food_level", foodLevel);
        save.put("fire_ticks", fireTicks);
        save.put("max_air", maxAir);
        save.put("max_no_damage_ticks", maxNoDamageTicks);
        save.put("no_damage_ticks", noDamageTicks);
        save.put("remaining_air", remainingAir);
        save.put("held_slot", heldSlot);
        save.put("health", health);
        save.put("last_damage", lastDamage);
        save.put("max_health", maxHealth);
        List<Map<String, Object>> serialInventory = new ArrayList<Map<String, Object>>();
        for (ItemStack stack : inventoryContents) {
            if (stack == null) {
                continue;
            }
            serialInventory.add(stack.serialize());
        }
        save.put("inventory_contents", serialInventory);
        List<Map<String, Object>> serialArmor = new ArrayList<Map<String, Object>>();
        for (ItemStack stack : armorContents) {
            if (stack == null) {
                continue;
            }
            serialArmor.add(stack.serialize());
        }
        save.put("armor_contents", serialArmor);
        save.put("exp", exp);
        save.put("saturation", saturation);
        save.put("fall_distance", fallDistance);
        save.put("exhaustion", exhaustion);
        save.put("fly_speed", flySpeed);
        save.put("walk_speed", walkSpeed);
        save.put("location", new SerialLocation(location));
        if (bedSpawn != null) {
            save.put("bed_spawn", new SerialLocation(bedSpawn));
        }
        if (compassTarget != null) {
            save.put("compass_target", new SerialLocation(compassTarget));
        }
        save.put("sleeping_ignored", sleepingIgnored);
        save.put("allow_flight", allowFlight);
        save.put("can_pickup_items", canPickupItems);
        save.put("flying", flying);
        save.put("banned", banned);
        save.put("op", op);
        save.put("sneaking", sneaking);
        save.put("sprinting", sprinting);
        save.put("whitelisted", whitelisted);
        List<Map<String, Object>> serialPotionEffects = new ArrayList<Map<String, Object>>();
        for (PotionEffect effect : potionEffects) {
            serialPotionEffects.add(effect.serialize());
        }
        save.put("potion_effects", serialPotionEffects);
        save.put("name", name);
        save.put("custom_name", customName);
        save.put("display_name", displayName);
        save.put("list_name", listName);
        save.put("item_in_hand", itemInHand.serialize());
        save.put("time", time);
        save.put("weather_name", weather != null ? weather.name() : WeatherType.CLEAR.name());
        save.put("game_mode", gameMode.getValue());
        return new SavedVersion(getHeader(), save, getClass());
    }

    public void update() {
        Player player = Bukkit.getPlayer(name);
        update(player);
    }

    @SuppressWarnings("deprecation") public void update(Player player) {
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
        player.getInventory().setContents(inventoryContents);
        player.getInventory().setArmorContents(armorContents);
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
        player.getInventory().setHeldItemSlot(heldSlot);
        player.setItemInHand(itemInHand);// returned to normal by setting the inventory contents, this only messes it up
        player.setLastDamageCause(lastDamageCause);
        player.setPlayerTime(time, true);
        player.setPlayerWeather(weather);
        if (scoreboard != null) {
            player.setScoreboard(scoreboard);
        }
        if (velocity != null) {
            player.setVelocity(velocity);
        }
        player.setGameMode(gameMode);
        player.updateInventory();
    }
}
