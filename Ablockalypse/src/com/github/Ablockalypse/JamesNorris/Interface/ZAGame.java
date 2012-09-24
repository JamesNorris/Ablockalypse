package com.github.Ablockalypse.JamesNorris.Interface;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.Ablockalypse.JamesNorris.Implementation.GameBlockSpawner;

public interface ZAGame {
	public void addMob(GameBlockSpawner zas, EntityType entity);

	public void addPlayer(Player player);

	public void endGame();

	public int getLevel();

	public String getName();

	public Set<String> getPlayers();

	public Player getRandomPlayer();

	public int getRemainingMobs();

	public int getRemainingPlayers();

	public Location getSpawn();

	public boolean isWolfRound();

	public void loadSpawners();

	public void nextLevel();

	public void removeMob();

	public void removePlayer(Player player);

	public void setLevel(int i);

	public void setRemainingMobs(int i);

	public void setSpawn(Location location);
}
