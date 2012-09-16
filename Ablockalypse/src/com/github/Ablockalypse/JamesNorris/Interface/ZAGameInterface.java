package com.github.Ablockalypse.JamesNorris.Interface;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.Ablockalypse.JamesNorris.Implementation.ZASpawner;

public interface ZAGameInterface {
	public void addMob(ZASpawner zas, EntityType entity);

	public void addPlayer(Player player);

	public int getLevel();

	public String getName();

	public Set<String> getPlayers();

	public Player getRandomPlayer();

	public int getRemainingMobs();

	public Location getSpawn();

	public boolean isWolfRound();

	public void loadSpawners();

	public void nextLevel();

	public void removePlayer(Player player);

	public void setLevel(int i);

	public void setSpawn(Location location);
}
