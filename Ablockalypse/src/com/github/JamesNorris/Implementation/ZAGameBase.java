package com.github.JamesNorris.Implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Interface.Barrier;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Manager.SpawnManager;
import com.github.JamesNorris.Threading.NextLevelThread;
import com.github.JamesNorris.Util.MathAssist;
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
	private Random rand;
	private Location spawn;
	private SpawnManager spawnManager;
	private boolean wolfRound, started, paused;

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
		Data.games.put(name, this);
		this.started = false;
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
	@Override public void endGame() {
		for (GameUndead gu : Data.undead)
			if (gu.getGame() == this)
				gu.kill();
		for (GameHellHound ghh : Data.hellhounds)
			if (ghh.getGame() == this)
				ghh.kill();
		Data.games.remove(getName());
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
			int i = rand.nextInt(getRemainingPlayers()) + 1;
			Player p = null;
			for (int j = 0; j <= i; j++)
				p = Bukkit.getServer().getPlayer(getPlayers().iterator().next());
			return p;
		}
		return null;
	}

	/**
	 * Returns a random player from this game.
	 * 
	 * @return The random player from this game
	 */
	@Override public Player getRandomPlayer() {
		if (players != null && players.size() >= 1) {
			int i = rand.nextInt(players.size()) + 1;
			Player p = null;
			for (int j = 0; j <= i; j++)
				p = Bukkit.getServer().getPlayer(getPlayers().iterator().next());
			return p;
		}
		return null;
	}

	/**
	 * Returns a random barrier from this game.
	 * 
	 * @return The random barrier from this game
	 */
	@Override public GameBarrier getRandomBarrier() {
		if (barriers != null && barriers.size() >= 1) {
			int i = rand.nextInt(barriers.size()) + 1;
			GameBarrier g = null;
			for (int j = 0; j <= i; j++)
				g = barriers.iterator().next();
			return g;
		}
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
	@Override public Location getSpawn() {
		return spawn;
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
		int prev = level;
		level = level + 1;
		if (Data.gameLevels.containsKey(getName()))
			Data.gameLevels.remove(getName());
		Data.gameLevels.put(getName(), level);
		if (level != 1)
			for (String s : players.keySet()) {
				Player p = Bukkit.getServer().getPlayer(s);
				p.setLevel(level);
				p.sendMessage(ChatColor.BOLD + "Level " + ChatColor.RESET + ChatColor.RED + prev + ChatColor.RESET + ChatColor.BOLD + " over... Next level: " + ChatColor.RED + level);
				p.sendMessage(ChatColor.GRAY + "You now have " + ChatColor.RED + Data.players.get(p).getPoints() + ChatColor.RESET + ChatColor.GRAY + " points.");
			}
		if (cd.wolfLevels != null && cd.wolfLevels.contains(level))
			wolfRound = true;
		new NextLevelThread(this, true);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Ablockalypse.instance, new Runnable() {
			public void run() {
				spawnWave();
			}
		}, 80);
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
			for (GameBarrier gb : barriers) {
				gb.replaceBarrier();
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
	@Override public void setSpawn(Location location) {
		if (!Data.mainframes.containsValue(location)) {
			spawn = location.add(0, 1, 0);
			Data.mainframes.put(getName(), location);
		} else {
			Data.mainframes.remove(location);
			spawn = location.add(0, 1, 0);
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
	 */
	@Override public void spawnWave() {
		double m = 0.53;
		double x = level;
		double b = getPlayers().size();
		int amt = (int) Math.round(MathAssist.line(m, x, b));
		if (External.getYamlManager().getConfigurationData().DEBUG)
			System.out.println("[Ablockalypse] [DEBUG] Amount of zombies in this wave: (" + getName() + ") " + amt);
		if (getRemainingPlayers() >= 1) {
			for (int i = 0; i <= amt; i++) {
				Player p = Bukkit.getServer().getPlayer(getPlayers().iterator().next());
				Barrier barrier = null;
				if (barriers.size() >= 1) {
					int randomint = rand.nextInt(barriers.size()) + 1;
					int current = 0;
					for (GameBarrier gb : barriers) {
						++current;
						if (current == randomint) {
							barrier = gb;
							break;
						} else {
							break;
						}
					}
				}
				if (barrier != null) {
					spawn(barrier.getSpawnLocation());
				} else {
					spawn(p.getLocation());
				}
			}
			this.started = true;
		}
	}

	/**
	 * Gamespawns a mob at the specified location.
	 * 
	 * @param l The location to spawn the mob at
	 */
	@Override public void spawn(Location l) {
		if (isWolfRound()) {
			Location loc = spawnManager.findSpawnLocation(l, 7, 4);
			spawnManager.gameSpawn(loc, EntityType.WOLF);
		} else {
			Location loc = spawnManager.findSpawnLocation(l, 16, 10);
			spawnManager.gameSpawn(loc, EntityType.ZOMBIE);
		}
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
		if (tf)
			paused = true;
		else
			paused = false;
	}

	/**
	 * Checks if the game is paused or not.
	 * 
	 * @return Whether or not the game is paused
	 */
	@Override public boolean isPaused() {
		return paused;
	}
}
