package com.github.JamesNorris.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.Plugin;

import com.github.Ablockalypse;
import com.github.JamesNorris.External;
import com.github.JamesNorris.Implementation.GameArea;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Implementation.GameHellHound;
import com.github.JamesNorris.Implementation.GameUndead;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Interface.HellHound;
import com.github.JamesNorris.Interface.Undead;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Util.Square;

public class Data {
	public static HashMap<Block, GameArea> areas = new HashMap<Block, GameArea>();;
	public static List<String> authors;
	public static HashMap<GameBarrier, Location> barrierpanels = new HashMap<GameBarrier, Location>();
	public static HashMap<Location, String> barriers = new HashMap<Location, String>();
	public static String description;
	public static ArrayList<GameBarrier> gamebarriers = new ArrayList<GameBarrier>();
	public static HashMap<String, Integer> gameLevels = new HashMap<String, Integer>();
	public static HashMap<String, ZAGameBase> games = new HashMap<String, ZAGameBase>();
	public static ArrayList<GameHellHound> hellhounds = new ArrayList<GameHellHound>();
	public static HashMap<Location, Boolean> loadedareas = new HashMap<Location, Boolean>();
	public static HashMap<String, Location> mainframes = new HashMap<String, Location>();
	public static ArrayList<ZAMob> mobs = new ArrayList<ZAMob>();
	public static HashMap<String, String> playergames = new HashMap<String, String>();
	public static HashMap<String, HashMap<String, Integer>> playerPoints = new HashMap<String, HashMap<String, Integer>>();
	public static HashMap<Player, ZAPlayerBase> players = new HashMap<Player, ZAPlayerBase>();
	public static Ablockalypse plugin;
	public static HashMap<GameBarrier, Square> squares = new HashMap<GameBarrier, Square>();
	public static ArrayList<GameUndead> undead = new ArrayList<GameUndead>();
	public static String version;

	/**
	 * Checks if the game exists, if not, creates a new game.
	 * 
	 * @param name The name of the ZAGame
	 * @param spawners Whether or not spawners should be immediately loaded
	 * @return The ZAGame found from the name given
	 */
	public static ZAGame findGame(String name) {
		ZAGameBase zag;
		if (Data.games.containsKey(name))
			zag = Data.games.get(name);
		else
			zag = new ZAGameBase(name, External.ym.getConfigurationData());
		return zag;
	}

	/**
	 * Finds a ZAPlayer, with the specified Player instance.
	 */
	public static ZAPlayer findZAPlayer(Player player, String gamename) {
		ZAPlayerBase zap;
		if (Data.players.containsKey(player))
			zap = Data.players.get(player);
		else if (Data.games.containsKey(gamename))
			zap = new ZAPlayerBase(player, Data.games.get(gamename));
		else
			zap = new ZAPlayerBase(player, new ZAGameBase(gamename, External.ym.getConfigurationData()));
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
	 * Gets a HellHound instance associated with the provided entity.
	 * 
	 * @param e The entity to check for
	 * @return The HellHound instance of the entity
	 */
	public static HellHound getHellHound(Entity e) {
		for (HellHound hh : Data.hellhounds)
			if (hh.getWolf().getEntityId() == e.getEntityId())
				return hh;
		return null;
	}

	/**
	 * Gets a GameUndead instance associated with the provided entity.
	 * 
	 * @param e The entity to check for
	 * @return The GameUndead instance of the entity
	 */
	public static Undead getUndead(Entity e) {
		for (GameUndead gu : Data.undead)
			if (gu.getZombie().getEntityId() == e.getEntityId())
				return gu;
		return null;
	}

	public static ZAMob getZAMob(Entity e) {
		if (e instanceof Zombie) {
			for (GameUndead gu : Data.undead)
				if (gu.getZombie().getEntityId() == e.getEntityId())
					return gu;
		} else if (e instanceof Wolf)
			for (GameHellHound ghh : Data.hellhounds)
				if (ghh.getWolf().getEntityId() == e.getEntityId())
					return ghh;
		return null;
	}

	/**
	 * Gets the mobs currently alive on the server.
	 * 
	 * @return All ZA mobs
	 */
	public static ArrayList<ZAMob> getZAMobs() {
		return mobs;
	}

	/**
	 * Checks if the specified entity is a ZA entity
	 * 
	 * @param e The entity to check for
	 * @return Whether or not the entity is a ZA entity
	 */
	public static boolean isZAMob(Entity e) {
		if (e != null)
			if ((e instanceof Wolf || e instanceof CraftWolf) && Data.hellhounds != null) {
				for (GameHellHound gh : Data.hellhounds)
					if (gh.getWolf().getEntityId() == e.getEntityId())
						return true;
			} else if ((e instanceof Zombie || e instanceof CraftZombie) && Data.undead != null)
				for (GameUndead gu : Data.undead)
					if (gu.getZombie().getEntityId() == e.getEntityId())
						return true;
		return false;
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
	 * Refreshes the data in the Data class.
	 */
	public static void refresh() {
		for (ZAPlayerBase zap : Data.players.values()) {
			String s1 = zap.getGame().getName();
			String s2 = zap.getName();
			if (!playergames.containsValue(s2))
				playergames.put(s1, s2);
		}
	}

	/**
	 * Creates new data storage for Ablockalypse.
	 * 
	 * @param plugin The instance of the Ablockalypse plugin
	 */
	public Data(Plugin plugin) {
		Data.plugin = (Ablockalypse) plugin;
		Data.authors = plugin.getDescription().getAuthors();
		Data.description = plugin.getDescription().getDescription();
		Data.version = plugin.getDescription().getVersion();
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
