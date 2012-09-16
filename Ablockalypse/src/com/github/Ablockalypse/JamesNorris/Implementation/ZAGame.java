package com.github.Ablockalypse.JamesNorris.Implementation;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.Ablockalypse.JamesNorris.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data;
import com.github.Ablockalypse.JamesNorris.Interface.ZAGameInterface;
import com.github.Ablockalypse.JamesNorris.Threading.MobSpawnThread;
import com.github.Ablockalypse.JamesNorris.Threading.NextLevelThread;

public class ZAGame implements ZAGameInterface {
	private ConfigurationData cd;
	private int level, mobs;
	private String name;
	private HashMap<String, Integer> players = new HashMap<String, Integer>();
	private Random rand;
	private Location spawn;
	private boolean wolfRound;

	/**
	 * Creates a new instance of a game.
	 * 
	 * @param name The name of the ZAGame
	 * @param cd The ConfigurationData instance used
	 * @param spawners Whether or not spawners should be loaded automatically
	 */
	public ZAGame(String name, ConfigurationData cd, boolean spawners) {
		this.name = name;
		this.cd = cd;
		this.rand = new Random();
		Data.games.put(name, this);
		if (spawners)
			loadSpawners();
	}

	/**
	 * Adds one to the count of mobs in the game, and spawns a zombie at the specified spawner.
	 * 
	 * @param zas The spawner to spawn the entity from
	 * @param entity The type of entity to spawn from the spawner
	 */
	@Override public void addMob(ZASpawner zas, EntityType entity) {
		mobs = mobs + 1;
		zas.spawnEntity(entity, this);
	}

	/**
	 * Adds a player to the game.
	 * NOTE: This does not change a players' status at all, that must be done through the ZAPlayer instance.
	 * 
	 * @param player The player to be added to the game
	 */
	@Override public void addPlayer(Player player) {
		players.put(player.getName(), cd.startpoints);
	}

	/**
	 * Gets the current level that the game is on.
	 * 
	 * @return The current level of the game
	 */
	@Override public int getLevel() {
		return level;
	}

	/**
	 * Gets the name of this game.
	 * 
	 * @return The name of the ZAGame
	 */
	@Override public String getName() {
		return name;
	}

	/**
	 * Returns a set of players currently in the game.
	 */
	@Override public Set<String> getPlayers() {
		return players.keySet();
	}

	/**
	 * Returns a random player from this game.
	 * 
	 * @return The random player from this game
	 */
	@Override public Player getRandomPlayer() {
		int i = rand.nextInt(players.size()) + 1;
		Player p = null;
		for (int j = 0; j <= i; j++) {
			p = Bukkit.getServer().getPlayer(getPlayers().iterator().next());
		}
		return p;
	}

	/**
	 * Gets the remaining custom mobs in the game.
	 * 
	 * @return The amount of remaining mobs in this game
	 */
	@Override public int getRemainingMobs() {
		return mobs;
	}

	/**
	 * Gets the spawn location for this game.
	 * 
	 * @return The location of the spawn
	 */
	@Override public Location getSpawn() {
		return spawn;
	}

	/**
	 * Returns true if the current round is a wolf round.
	 * 
	 * @return Whether or not the current round is a wolf round
	 */
	@Override public boolean isWolfRound() {
		return wolfRound;
	}

	/**
	 * Load all spawners for this game
	 */
	@Override public void loadSpawners() {
		for (String s : Data.spawners.keySet()) {
			if (s == getName()) {
				Location l = Data.spawners.get(s);
				new ZASpawner(l.getWorld().getBlockAt(l), this);
			}
		}
	}

	/**
	 * Starts the next level for the game, and adds a level to all players in this game.
	 */
	@Override public void nextLevel() {
		this.level = level + 1;
		if (Data.gameLevels.containsKey(getName()))
			Data.gameLevels.remove(getName());
		Data.gameLevels.put(getName(), level);
		for (String s : players.keySet()) {
			Player p = Bukkit.getServer().getPlayer(s);
			p.setLevel(level);
			p.sendMessage(ChatColor.GRAY + "You now have: " + Data.players.get(p).getPoints());
		}
		if (cd.wolfLevels.contains(level))
			wolfRound = true;
		NextLevelThread nlt = new NextLevelThread(this);
		nlt.waitForNextLevel();
		MobSpawnThread mst = new MobSpawnThread(this);
		mst.mobSpawn();
	}

	/**
	 * Removes a player from the game.
	 * NOTE: This does not change a players' status at all, that must be done throught the ZAPlayer instance.
	 * 
	 * @param player The player to be removed from the game
	 */
	@Override public void removePlayer(Player player) {
		players.remove(player.getName());
		Data.players.remove(player);
	}

	/**
	 * Sets the game to the specified level.
	 * 
	 * @param i The level the game will be set to
	 */
	@Override public void setLevel(int i) {
		this.level = i - 1;
		nextLevel();
	}

	/**
	 * Sets the spawn location of the game.
	 * 
	 * @param location The location to be made into the spawn
	 */
	@Override public void setSpawn(Location location) {
		if (!Data.mainframes.containsValue(location)) {
			spawn = location;
			Data.mainframes.put(getName(), location);
		}
	}
}
