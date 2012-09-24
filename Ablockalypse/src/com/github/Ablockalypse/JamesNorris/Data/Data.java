package com.github.Ablockalypse.JamesNorris.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Implementation.GameArea;
import com.github.Ablockalypse.JamesNorris.Implementation.GameBarrier;
import com.github.Ablockalypse.JamesNorris.Implementation.GameHellHound;
import com.github.Ablockalypse.JamesNorris.Implementation.GameUndead;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAGameBase;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayerBase;
import com.github.Ablockalypse.JamesNorris.Implementation.GameBlockSpawner;
import com.github.Ablockalypse.JamesNorris.Util.External;
import com.github.Ablockalypse.JamesNorris.Util.Square;

public class Data {
	public static HashMap<Block, GameArea> areas = new HashMap<Block, GameArea>();;
	public static HashMap<GameBarrier, Location> barrierpanels = new HashMap<GameBarrier, Location>();
	// public static List<String> authors;
	public static ArrayList<Location> barriers = new ArrayList<Location>();
	// public static String description;
	public static ArrayList<GameBarrier> gamebarriers = new ArrayList<GameBarrier>();
	public static HashMap<String, Integer> gameLevels = new HashMap<String, Integer>();
	public static HashMap<String, ZAGameBase> games = new HashMap<String, ZAGameBase>();
	public static HashMap<Location, Boolean> loadedareas = new HashMap<Location, Boolean>();
	public static HashMap<Location, GameBlockSpawner> loadedspawners = new HashMap<Location, GameBlockSpawner>();
	public static HashMap<String, Location> mainframes = new HashMap<String, Location>();
	public static HashMap<String, HashMap<String, Integer>> playerPoints = new HashMap<String, HashMap<String, Integer>>();
	public static HashMap<Player, ZAPlayerBase> players = new HashMap<Player, ZAPlayerBase>();
	public static Ablockalypse plugin;
	public static HashMap<String, Location> spawners = new HashMap<String, Location>();
	public static HashMap<GameBarrier, Square> squares = new HashMap<GameBarrier, Square>();
	// public static String version;
	public static ArrayList<GameHellHound> wolves = new ArrayList<GameHellHound>();
	public static ArrayList<GameUndead> zombies = new ArrayList<GameUndead>();

	/**
	 * Checks if the square exists, if not, creates a new square with the specified location and radius.
	 * 
	 * @param b The barrier to find the square around
	 * @param l The center of the square
	 * @param radius The radius of the square
	 * @return The square around the barrier
	 */
	public static Square findBarrierSquare(GameBarrier b, Location l, int radius) {
		Square s;
		if (Data.squares.containsKey(b))
			s = Data.squares.get(b);
		else {
			s = new Square(l, radius);
			Data.squares.put(b, s);
		}
		return s;
	}

	/**
	 * Checks if the game exists, if not, creates a new game.
	 * 
	 * @param name The name of the ZAGame
	 * @param spawners Whether or not spawners should be immediately loaded
	 * @return The ZAGame found from the name given
	 */
	public static ZAGameBase findGame(String name, boolean spawners) {
		ZAGameBase zag;
		if (Data.games.containsKey(name))
			zag = Data.games.get(name);
		else
			zag = new ZAGameBase(name, External.ym.getConfigurationData(), spawners);
		return zag;
	}

	/**
	 * Finds a ZAPlayer, with the specified Player instance.
	 */
	public static ZAPlayerBase findZAPlayer(Player player, String gamename) {
		ZAPlayerBase zap;
		if (Data.players.containsKey(player))
			zap = Data.players.get(player);
		else if (Data.games.containsKey(gamename))
			zap = new ZAPlayerBase(player, Data.games.get(gamename));
		else
			zap = new ZAPlayerBase(player, new ZAGameBase(gamename, External.ym.getConfigurationData(), true));
		return zap;
	}

	/**
	 * Checks if the specified game exists.
	 * 
	 * @param gamename The game name to check for
	 * @return Whether or not the game exists
	 */
	public static boolean gameExists(String gamename) {
		if (Data.games.containsKey(gamename))
			return true;
		return false;
	}

	/**
	 * Creates a new instance of a ZASpawner.
	 * 
	 * @param loc The location to load the spawner from
	 * @param game The game to load the spawner for
	 */
	public static void loadSpawner(Location loc, ZAGameBase game) {
		World w = loc.getWorld();
		new GameBlockSpawner(w.getBlockAt(loc), game);
	}

	/**
	 * Checks if the specified player exists.
	 * 
	 * @param player The player to check for
	 * @return Whether or not the player exists
	 */
	public static boolean playerExists(Player player) {
		if (Data.players.containsKey(player))
			return true;
		return false;
	}

	/**
	 * Creates new data storage for Ablockalypse.
	 * 
	 * @param plugin The instance of the Ablockalypse plugin
	 */
	public Data(Ablockalypse plugin) {
		Data.plugin = plugin;
		// Data.authors = plugin.getDescription().getAuthors();//TODO fix NPE - The plugin doesn't have a description?
		// Data.description = plugin.getDescription().getDescription();
		// Data.version = plugin.getDescription().getVersion();
	}

	/**
	 * Clears all data from the main data cache of the Ablockalypse plugin.
	 */
	@SuppressWarnings("unused") @Override public void finalize() {
		for (Method m : this.getClass().getDeclaredMethods())
			m = null;
		for (Field f : this.getClass().getDeclaredFields())
			f = null;
	}
}
