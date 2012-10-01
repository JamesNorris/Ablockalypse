package com.github.JamesNorris.Interface;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.JamesNorris.Manager.SpawnManager;

public interface ZAGame {
	public void addMobCount();

	public void addPlayer(Player player);

	public void endGame();

	public int getLevel();

	public String getName();

	public Set<String> getPlayers();

	public Player getRandomLivingPlayer();

	public Player getRandomPlayer();

	public int getRemainingMobs();

	public int getRemainingPlayers();

	public Location getSpawn();

	public SpawnManager getSpawnManager();

	public boolean isWolfRound();

	public void nextLevel();

	public void removePlayer(Player player);

	public void setLevel(int i);

	public void setRemainingMobs(int i);

	public void setSpawn(Location location);

	public void subtractMobCount();
}
