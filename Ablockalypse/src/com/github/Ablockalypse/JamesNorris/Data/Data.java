package com.github.Ablockalypse.JamesNorris.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Implementation.Area;
import com.github.Ablockalypse.JamesNorris.Implementation.Barrier;
import com.github.Ablockalypse.JamesNorris.Implementation.GameWolf;
import com.github.Ablockalypse.JamesNorris.Implementation.GameZombie;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAGame;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayer;
import com.github.Ablockalypse.JamesNorris.Implementation.ZASpawner;
import com.github.Ablockalypse.JamesNorris.Util.External;
import com.github.Ablockalypse.JamesNorris.Util.Square;

public class Data {
	public static HashMap<Block, Area> areas = new HashMap<Block, Area>();;
	public static List<String> authors;
	public static ArrayList<Location> barriers = new ArrayList<Location>();
	public static HashMap<Barrier, Location> barrierpanels = new HashMap<Barrier, Location>();
	public static String description;
	public static ArrayList<Barrier> gamebarriers = new ArrayList<Barrier>();
	public static HashMap<String, Integer> gameLevels = new HashMap<String, Integer>();
	public static HashMap<String, ZAGame> games = new HashMap<String, ZAGame>();
	public static HashMap<Location, Boolean> loadedareas = new HashMap<Location, Boolean>();
	public static HashMap<Location, ZASpawner> loadedspawners = new HashMap<Location, ZASpawner>();
	public static HashMap<String, Location> mainframes = new HashMap<String, Location>();
	public static HashMap<String, HashMap<String, Integer>> playerPoints = new HashMap<String, HashMap<String, Integer>>();
	public static HashMap<Player, ZAPlayer> players = new HashMap<Player, ZAPlayer>();
	public static Ablockalypse plugin;
	public static HashMap<String, Location> spawners = new HashMap<String, Location>();
	public static HashMap<Barrier, Square> squares = new HashMap<Barrier, Square>();
	public static String version;
	public static ArrayList<GameWolf> wolves = new ArrayList<GameWolf>();
	public static ArrayList<GameZombie> zombies = new ArrayList<GameZombie>();

	/**
	 * Clears all data from the main data cache of the Ablockalypse plugin.
	 */
	@SuppressWarnings("unused") @Override public void finalize() {
		for (Method m : this.getClass().getDeclaredMethods())
			m = null;
		for (Field f : this.getClass().getDeclaredFields())
			f = null;
	}

	/**
	 * Checks if the game exists, if not, creates a new game.
	 * 
	 * @param name The name of the ZAGame
	 * @param spawners Whether or not spawners should be immediately loaded
	 * @return The ZAGame found from the name given
	 */
	public static ZAGame findGame(String name, boolean spawners) {
		ZAGame zag;
		if (Data.games.containsKey(name))
			zag = Data.games.get(name);
		else
			zag = new ZAGame(name, External.ym.getConfigurationData(), spawners);
		return zag;
	}

	/**
	 * Checks if the square exists, if not, creates a new square with the specified location and radius.
	 * 
	 * @param b The barrier to find the square around
	 * @param l The center of the square
	 * @param radius The radius of the square
	 * @return The square around the barrier
	 */
	public static Square findBarrierSquare(Barrier b, Location l, int radius) {
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
	 * Finds a ZAPlayer, with the specified Player instance.
	 */
	public static ZAPlayer findZAPlayer(Player player, String gamename) {
		ZAPlayer zap;
		if (Data.players.containsKey(player))
			zap = Data.players.get(player);
		else if (Data.games.containsKey(gamename))
			zap = new ZAPlayer(player, Data.games.get(gamename));
		else
			zap = new ZAPlayer(player, new ZAGame(gamename, External.ym.getConfigurationData(), true));
		return zap;
	}

	/**
	 * Creates a new instance of a ZASpawner.
	 * 
	 * @param loc The location to load the spawner from
	 * @param game The game to load the spawner for
	 */
	public static void loadSpawner(Location loc, ZAGame game) {
		World w = loc.getWorld();
		new ZASpawner(w.getBlockAt(loc), game);
	}

	/**
	 * Creates new data storage for Ablockalypse.
	 * 
	 * @param plugin The instance of the Ablockalypse plugin
	 */
	public Data(Ablockalypse plugin) {
		Data.plugin = plugin;
		Data.authors = plugin.getDescription().getAuthors();
		Data.description = plugin.getDescription().getDescription();
		Data.version = plugin.getDescription().getVersion();
	}
}
