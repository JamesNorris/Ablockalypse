package com.github.jamesnorris.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.util.SerializableLocation;

public class PerPlayerDataStorage implements Serializable {// TODO annotations
    private static final long serialVersionUID = -243572474641181441L;
    private final ArrayList<Map<String, Object>> armor = new ArrayList<Map<String, Object>>();
    private final ArrayList<Map<String, Object>> inventory = new ArrayList<Map<String, Object>>();
    private final SerializableLocation location;
    private final String name, gamename;
    private final int points, kills, health, food, fire, gm, gameLevel;
    private final float saturation, fall, exhaust;
    private final boolean sleepingignored;

    public PerPlayerDataStorage(ZAPlayer zap) {
        name = zap.getName();
        gamename = zap.getGame().getName();
        points = zap.getPoints();
        kills = zap.getKills();
        gameLevel = zap.getGame().getLevel();
        Player player = zap.getPlayer();
        location = new SerializableLocation(player.getLocation());
        ItemStack[] inv = player.getInventory().getContents();
        inv = player.getInventory().getContents();
        for (ItemStack is : inv) {
            if (is != null) {
                inventory.add(is.serialize());
            }
        }
        ItemStack[] ar = player.getInventory().getArmorContents();
        for (ItemStack is : ar) {
            armor.add(is.serialize());
        }
        health = player.getHealth();
        food = player.getFoodLevel();
        saturation = player.getSaturation();
        sleepingignored = player.isSleepingIgnored();
        fire = player.getFireTicks();
        fall = player.getFallDistance();
        exhaust = player.getExhaustion();
        gm = player.getGameMode().getValue();
    }

    public ArrayList<Map<String, Object>> getArmor() {
        return armor;
    }

    public float getExhaust() {
        return exhaust;
    }

    public float getFall() {
        return fall;
    }

    public int getFire() {
        return fire;
    }

    public int getFood() {
        return food;
    }

    public int getGameLevel() {
        return gameLevel;
    }

    public int getGameModeValue() {
        return gm;
    }

    public String getGameName() {
        return gamename;
    }

    public int getHealth() {
        return health;
    }

    public ArrayList<Map<String, Object>> getInventory() {
        return inventory;
    }

    public int getKills() {
        return kills;
    }

    public SerializableLocation getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public float getSaturation() {
        return saturation;
    }

    public boolean isSleepingignored() {
        return sleepingignored;
    }

    public void loadToPlayer(ZAPlayer zap) {
        Player p = zap.getPlayer();
        zap.setPoints(points);
        zap.setKills(kills);
        p.setHealth(health);
        p.setFoodLevel(food);
        p.setFireTicks(fire);
        p.setGameMode(GameMode.getByValue(gm));
        p.setSaturation(saturation);
        p.setFallDistance(fall);
        p.setExhaustion(exhaust);
        p.setSleepingIgnored(sleepingignored);
        p.teleport(SerializableLocation.returnLocation(location));
        for (Map<String, Object> is : inventory) {
            p.getInventory().addItem(ItemStack.deserialize(is));
        }
        for (Map<String, Object> is : armor) {
            p.getInventory().addItem(ItemStack.deserialize(is));
        }
    }
}
