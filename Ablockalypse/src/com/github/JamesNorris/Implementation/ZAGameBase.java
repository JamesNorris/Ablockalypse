package com.github.JamesNorris.Implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Manager.SpawnManager;
import com.github.JamesNorris.Threading.NextLevelThread;
import com.github.JamesNorris.Util.SoundUtil;
import com.github.JamesNorris.Util.SoundUtil.ZASound;
import com.github.iKeirNez.Util.XMPP;
import com.github.iKeirNez.Util.XMPP.XMPPType;

public class ZAGameBase implements ZAGame {
	private ConfigurationData cd;
	private int level, mobs;
	private String name;
	private HashMap<String, Integer> players = new HashMap<String, Integer>();
	private ArrayList<GameBarrier> barriers = new ArrayList<GameBarrier>();
	private ArrayList<Location> spawners = new ArrayList<Location>();
	private Random rand;
	private Location mainframe;
	private SpawnManager spawnManager;
	private boolean wolfRound, paused;
	public boolean started;

	/**
	 * Creates a new instance of a game.
	 * 
	 * @param name The name of the ZAGame
	 * @param cd The ConfigurationData instance used
	 * @param spawners Whether or not spawners should be loaded automatically
	 */
	public ZAGameBase(String name, ConfigurationData cd) {
		this.name = name;
		this.cd = cd;
		this.rand = new Random();
		this.paused = false;
		this.started = false;
		Data.games.put(name, this);
		XMPP.sendMessage("A new game of Zombie Ablockalypse (" + name + ") has been started.", XMPPType.ZA_GAME_START);
	}

	/**
	 * Adds one to the mob count.
	 */
	@Override public void addMobCount() {
		++mobs;
	}

	/**
	 * Adds a player to the game.
	 * NOTE: This does not change a players' status at all, that must be done through the ZAPlayer instance.
	 * 
	 * @param player The player to be added to the game
	 */
	@Override public void addPlayer(Player player) {
		players.put(player.getName(), cd.startpoints);
		if (paused)
			pause(false);
	}

	/**
	 * Ends the game, removes all attached instances, and finalizes this instance.
	 */
	@Override public void remove() {
		for (GameUndead gu : Data.undead)
			if (gu.getGame() == this)
				gu.kill();
		for (GameHellHound ghh : Data.hellhounds)
			if (ghh.getGame() == this)
				ghh.kill();
		for (GameArea a : Data.areas)
			if (a.getGame() == this)
				a.close();
		for (String s : getPlayers()) {
			Player p = Bukkit.getPlayer(s);
			ZAPlayer zap = Data.getZAPlayer(p);
			removePlayer(p);
			zap.removeFromGame();
		}
		Data.games.remove(name);
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
	 * Gets a random living player.
	 * Living is considered as not in limbo, last stand, respawn thread, or death.
	 * 
	 * @return The random living player
	 */
	@Override public Player getRandomLivingPlayer() {
		if (getRemainingPlayers() >= 1) {
			ArrayList<ZAPlayerBase> zaps = new ArrayList<ZAPlayerBase>();
			for (String s : getPlayers()) {
				Player p = Bukkit.getServer().getPlayer(s);
				ZAPlayerBase zap = (ZAPlayerBase) Data.getZAPlayer(p);
				if (!p.isDead() && !zap.isInLastStand() && !zap.isInLimbo())
					zaps.add(zap);
			}
			return zaps.get(rand.nextInt(zaps.size())).getPlayer();
		}
		return null;
	}

	/**
	 * Returns a random player from this game.
	 * 
	 * @return The random player from this game
	 */
	@Override public Player getRandomPlayer() {
		if (getRemainingPlayers() >= 1) {
			ArrayList<Player> ps = new ArrayList<Player>();
			for (String s : getPlayers()) {
				Player p = Bukkit.getServer().getPlayer(s);
				ps.add(p);
			}
			return ps.get(rand.nextInt(ps.size())).getPlayer();
		}
		return null;
	}

	/**
	 * Returns a random barrier from this game.
	 * 
	 * @return The random barrier from this game
	 */
	@Override public GameBarrier getRandomBarrier() {
		if (barriers != null && barriers.size() >= 1)
			return barriers.get(rand.nextInt(barriers.size()));
		return null;
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
	 * Gets the players still in the game.
	 * 
	 * @return How many players are in the game
	 */
	@Override public int getRemainingPlayers() {
		int i = 0;
		for (String s : getPlayers()) {
			Player p = Bukkit.getPlayer(s);
			if (!p.isDead() && !Data.players.get(p).isInLimbo() && !Data.players.get(p).isInLastStand())
				++i;
		}
		return i;
	}

	/**
	 * Gets the spawn location for this game.
	 * 
	 * @return The location of the spawn
	 */
	@Override public Location getMainframe() {
		return mainframe;
	}

	/**
	 * Gets the manager that affects spawn for this game.
	 * 
	 * @return The SpawnManager instance associated with this game
	 */
	@Override public SpawnManager getSpawnManager() {
		return spawnManager;
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
	 * Starts the next level for the game, and adds a level to all players in this game.
	 * Then, spawns a wave of zombies, and starts the thread for the next level.
	 */
	@Override public void nextLevel() {
		level = level + 1;
		if (Data.gameLevels.containsKey(getName()))
			Data.gameLevels.remove(getName());
		Data.gameLevels.put(getName(), level);
		if (level != 1) {
			for (String s : players.keySet()) {
				Player p = Bukkit.getServer().getPlayer(s);
				p.setLevel(level);
				p.sendMessage(ChatColor.BOLD + "Level " + ChatColor.RESET + ChatColor.RED + level + ChatColor.RESET + ChatColor.BOLD + " has started.");
			}
			broadcastPoints();
		}
		if (cd.wolfLevels != null && cd.wolfLevels.contains(level))
			wolfRound = true;
		new NextLevelThread(this, true);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Ablockalypse.instance, new Runnable() {
			public void run() {
				spawnWave();
			}
		}, 40);
	}

	/**
	 * Removes a player from the game.
	 * 
	 * @param player The player to be removed from the game
	 */
	@Override public void removePlayer(Player player) {
		players.remove(player.getName());
		Data.players.get(player).removeFromGame();
		Data.players.remove(player);
		if (players.size() == 0) {
			pause(true);
			setLevel(0);
		}
	}

	/**
	 * Sets the game to the specified level.
	 * If the level is set to 0, the game will restart.
	 * 
	 * @param i The level the game will be set to
	 */
	@Override public void setLevel(int i) {
		if (i == 0) {
			for (String name : getPlayers()) {
				Player player = Bukkit.getServer().getPlayer(name);
				ZAPlayerBase zap = Data.players.get(player);
				player.sendMessage(ChatColor.BOLD + "" + ChatColor.GRAY + "The game has restarted. You made it to level " + level);
				SoundUtil.generateSound(zap.getPlayer(), ZASound.END);
				removePlayer(player);
			}
			for (GameBarrier gb : barriers)
				gb.replaceBarrier();
			for (GameArea ga : Data.areas) {
				if (ga.getGame() == this)
					ga.close();
			}
		}
		level = i;
	}

	/**
	 * Sets the remaining custom mobs in the game.
	 * 
	 * @param i The amount to be set to
	 */
	@Override public void setRemainingMobs(int i) {
		mobs = i;
	}

	/**
	 * Sets the spawn location of the game.
	 * 
	 * @param location The location to be made into the spawn
	 */
	@Override public void setMainframe(Location location) {
		if (!Data.mainframes.containsValue(location)) {
			mainframe = location;
			Data.mainframes.put(getName(), location);
		} else {
			Data.mainframes.remove(location);
			mainframe = location;
			Data.mainframes.put(getName(), location);
		}
		if (spawnManager == null || spawnManager.getWorld() != location.getWorld())
			spawnManager = new SpawnManager(this, location.getWorld());
	}

	/**
	 * Removes one from the mob count.
	 */
	@Override public void subtractMobCount() {
		--mobs;
	}

	/**
	 * Spawns a wave of mobs around random living players in this game.
	 * If barriers are present and acessible, spawns the mobs at the barriers.
	 */
	@Override public void spawnWave() {
		spawnManager.spawnWave();
		started = true;
	}

	/**
	 * Gamespawns a mob at the specified location.
	 * 
	 * @param l The location to spawn the mob at
	 * @param closespawn Whether or not to spawn right next to the target
	 */
	@Override public void spawn(Location l, boolean closespawn) {
		spawnManager.spawn(l, closespawn);
	}

	/**
	 * Returns whether or not the game has started.
	 * 
	 * @return Whether or not the game has been started, and mobs are spawning
	 */
	@Override public boolean hasStarted() {
		return started;
	}

	/**
	 * Attaches a barrier to this game.
	 */
	@Override public void addBarrier(GameBarrier gb) {
		this.barriers.add(gb);
	}

	/**
	 * Sets the game to pause or not.
	 * 
	 * @param tf Whether or not to pause or un-pause the game
	 */
	@Override public void pause(boolean tf) {
		paused = tf;
	}

	/**
	 * Checks if the game is paused or not.
	 * 
	 * @return Whether or not the game is paused
	 */
	@Override public boolean isPaused() {
		return paused;
	}

	/**
	 * Makes the game a wolf round.
	 * 
	 * @param tf Whether or not the game should be a wolf round
	 */
	@Override public void setWolfRound(boolean tf) {
		wolfRound = tf;
	}

	/**
	 * Gets a list of barriers connected to this game.
	 * 
	 * @return A list of barriers in this game
	 */
	@Override public List<GameBarrier> getBarriers() {
		return barriers;
	}

	/**
	 * Gets all the spawners for this game.
	 * 
	 * @return The spawn locations as an arraylist for this game
	 */
	@Override public ArrayList<Location> getMobSpawners() {
		return spawners;
	}

	/**
	 * Adds a spawner to the game
	 * 
	 * @param l The location to put the spawner at
	 */
	@Override public void addMobSpawner(Location l) {
		Location loc = l.add(0, 2, 0);
		spawners.add(loc);
		Data.spawns.put(this, loc);
	}

	/**
	 * Sends all players in the game the points of all players.
	 */
	@Override public void broadcastPoints() {
		for (String s : getPlayers()) {
			Player p = Bukkit.getPlayer(s);
			for (String s2 : getPlayers()) {
				Player p2 = Bukkit.getPlayer(s2);
				ZAPlayer zap = Data.getZAPlayer(p2);
				p.sendMessage(ChatColor.RED + s2 + ChatColor.RESET + " - " + ChatColor.GRAY + zap.getPoints());
			}
		}
	}
}
