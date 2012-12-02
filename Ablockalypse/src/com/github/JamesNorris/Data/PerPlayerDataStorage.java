package com.github.JamesNorris.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Util.SerializableLocation;

public class PerPlayerDataStorage implements Serializable {// TODO annotations
	private static final long serialVersionUID = -243572474641181441L;
	private final String name, gamename;
	private final int points, kills, health, food, fire, gm, gameLevel;
	private final float saturation, fall, exhaust;
	private final boolean sleepingignored;
	private final SerializableLocation location;
	private final ArrayList<Map<String, Object>> inventory = new ArrayList<Map<String, Object>>();
	private final ArrayList<Map<String, Object>> armor = new ArrayList<Map<String, Object>>();

	public PerPlayerDataStorage(ZAPlayerBase zap) {
		this.name = zap.getName();
		this.gamename = zap.getGame().getName();
		this.points = zap.getPoints();
		this.kills = zap.getKills();
		this.gameLevel = zap.getGame().getLevel();
		Player player = zap.getPlayer();
		this.location = new SerializableLocation(player.getLocation());
		ItemStack[] inv = player.getInventory().getContents();
		inv = player.getInventory().getContents();
		for (ItemStack is : inv)
			if (is != null)
				inventory.add(is.serialize());
		ItemStack[] ar = player.getInventory().getArmorContents();
		for (ItemStack is : ar)
			armor.add(is.serialize());
		this.health = player.getHealth();
		this.food = player.getFoodLevel();
		this.saturation = player.getSaturation();
		this.sleepingignored = player.isSleepingIgnored();
		this.fire = player.getFireTicks();
		this.fall = player.getFallDistance();
		this.exhaust = player.getExhaustion();
		this.gm = player.getGameMode().getValue();
	}

	public void loadToPlayer(ZAPlayerBase zap) {
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
		for (Map<String, Object> is : inventory)
			p.getInventory().addItem(ItemStack.deserialize(is));
		for (Map<String, Object> is : armor)
			p.getInventory().addItem(ItemStack.deserialize(is));
	}

	public int getGameLevel() {
		return gameLevel;
	}

	public String getName() {
		return name;
	}

	public String getGameName() {
		return gamename;
	}

	public int getPoints() {
		return points;
	}

	public int getKills() {
		return kills;
	}

	public int getHealth() {
		return health;
	}

	public int getFood() {
		return food;
	}

	public int getFire() {
		return fire;
	}

	public int getGameModeValue() {
		return gm;
	}

	public float getSaturation() {
		return saturation;
	}

	public float getFall() {
		return fall;
	}

	public float getExhaust() {
		return exhaust;
	}

	public boolean isSleepingignored() {
		return sleepingignored;
	}

	public SerializableLocation getLocation() {
		return location;
	}

	public ArrayList<Map<String, Object>> getInventory() {
		return inventory;
	}

	public ArrayList<Map<String, Object>> getArmor() {
		return armor;
	}
}
