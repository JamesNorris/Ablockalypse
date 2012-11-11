package com.github.JamesNorris.Interface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.JamesNorris.Implementation.GameArea;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Implementation.GameMysteryChest;
import com.github.JamesNorris.Manager.SpawnManager;

public interface ZAGame {
	public void addArea(GameArea ga);

	public void addBarrier(GameBarrier gb);

	public void addMobSpawner(ZALocation l);

	public void addMysteryChest(GameMysteryChest mc);

	public void addPlayer(Player player);

	public void broadcast(String message, Player exception);

	public void broadcastPoints();

	public void end();

	public MysteryChest getActiveMysteryChest();

	public ArrayList<GameArea> getAreas();

	public List<GameBarrier> getBarriers();

	public int getLevel();

	public Location getMainframe();

	public int getMobCount();

	public ArrayList<ZAMob> getMobs();

	public ArrayList<ZALocation> getMobSpawners();

	public ArrayList<GameMysteryChest> getMysteryChests();

	public String getName();

	public Set<String> getPlayers();

	public GameArea getRandomArea();

	public GameBarrier getRandomBarrier();

	public Player getRandomLivingPlayer();

	public Player getRandomPlayer();

	public int getRemainingPlayers();

	public SpawnManager getSpawnManager();

	public boolean hasStarted();

	public boolean isPaused();

	public boolean isWolfRound();

	public void nextLevel();

	public void pause(boolean tf);

	public void remove();

	public void removeArea(GameArea ga);

	public void removeBarrier(GameBarrier gb);

	public void removeMobSpawner(ZALocation l);

	public void removePlayer(Player player);

	public void setActiveMysteryChest(MysteryChest mc);

	public void setLevel(int i);

	public void setMainframe(Location location);

	public void setMobCount(int i);

	public void setRemainingMobs(int i);

	public void setWolfRound(boolean tf);

	public void spawn(Location l, boolean closespawn);

	public void spawnWave();
}
