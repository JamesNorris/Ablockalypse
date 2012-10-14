package com.github.JamesNorris.Interface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Manager.SpawnManager;

public interface ZAGame {
	public void broadcastPoints();

	public void setWolfRound(boolean tf);

	public GameBarrier getRandomBarrier();

	public void addBarrier(GameBarrier gb);

	public void addMobCount();

	public void addPlayer(Player player);

	public void remove();

	public int getLevel();

	public String getName();

	public Set<String> getPlayers();

	public Player getRandomLivingPlayer();

	public Player getRandomPlayer();

	public int getRemainingMobs();

	public int getRemainingPlayers();

	public Location getMainframe();

	public SpawnManager getSpawnManager();

	public boolean isWolfRound();

	public void nextLevel();

	public void removePlayer(Player player);

	public void setLevel(int i);

	public void setRemainingMobs(int i);

	public void setMainframe(Location location);

	public void subtractMobCount();

	public void spawnWave();

	public void spawn(Location l, boolean closespawn);

	public boolean hasStarted();

	public void pause(boolean tf);

	public boolean isPaused();

	public List<GameBarrier> getBarriers();

	public ArrayList<Location> getMobSpawners();

	public void addMobSpawner(Location l);
}
