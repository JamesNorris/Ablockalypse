package com.github.JamesNorris.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
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
import com.github.JamesNorris.Interface.GameObject;
import com.github.JamesNorris.Interface.HellHound;
import com.github.JamesNorris.Interface.MysteryChest;
import com.github.JamesNorris.Interface.Undead;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZALocation;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Util.Square;

public class GlobalData {
	public static ArrayList<GameArea> areas = new ArrayList<GameArea>();
	public static List<String> authors;
	public static HashMap<GameBarrier, Location> barrierpanels = new HashMap<GameBarrier, Location>();
	public static HashMap<Location, String> barriers = new HashMap<Location, String>();
	protected static String description;
	public static ArrayList<GameBarrier> gamebarriers = new ArrayList<GameBarrier>();
	public static HashMap<String, Integer> gameLevels = new HashMap<String, Integer>();
	public static HashMap<String, ZAGameBase> games = new HashMap<String, ZAGameBase>();
	public static ArrayList<GameHellHound> hellhounds = new ArrayList<GameHellHound>();
	public static HashMap<String, Location> mainframes = new HashMap<String, Location>();
	public static ArrayList<ZAMob> mobs = new ArrayList<ZAMob>();
	public static HashMap<String, String> playergames = new HashMap<String, String>();
	public static HashMap<String, HashMap<String, Integer>> playerPoints = new HashMap<String, HashMap<String, Integer>>();
	public static HashMap<Player, ZAPlayerBase> players = new HashMap<Player, ZAPlayerBase>();
	protected static Ablockalypse plugin;
	protected static HashMap<GameBarrier, Square> squares = new HashMap<GameBarrier, Square>();
	public static ArrayList<GameUndead> undead = new ArrayList<GameUndead>();
	public static HashMap<ZAGameBase, ZALocation> spawns = new HashMap<ZAGameBase, ZALocation>();
	public static HashMap<Location, MysteryChest> chests = new HashMap<Location, MysteryChest>();
	public static ArrayList<GameObject> objects = new ArrayList<GameObject>();
	public static HashMap<Location, Object> removallocs = new HashMap<Location, Object>();
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
		if (GlobalData.games.containsKey(name))
			zag = GlobalData.games.get(name);
		else
			zag = new ZAGameBase(name, External.ym.getConfigurationData());
		return zag;
	}

	/**
	 * Finds a ZAPlayer, with the specified Player instance.
	 */
	public static ZAPlayer findZAPlayer(Player player, String gamename) {
		ZAPlayerBase zap;
		if (GlobalData.players.containsKey(player))
			zap = GlobalData.players.get(player);
		else if (GlobalData.games.containsKey(gamename))
			zap = new ZAPlayerBase(player, GlobalData.games.get(gamename));
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
		if (GlobalData.games.containsKey(gamename))
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
		for (HellHound hh : GlobalData.hellhounds)
			if (hh.getWolf().getEntityId() == e.getEntityId())
				return hh;
		return null;
	}

	/**
	 * Gets the chest attached to this block.
	 * 
	 * @param b The block to check for
	 * @return The MysteryChest that is at the same location as this block
	 */
	public static MysteryChest getMysteryChest(Location loc) {
		MysteryChest mc = null;
		if (chests.containsKey(loc))
			mc = chests.get(loc);
		return mc;
	}

	/**
	 * Gets the spawns for all games in a hashmap.
	 * 
	 * @return All spawns in all games
	 */
	public static HashMap<ZAGameBase, ZALocation> getSpawns() {
		return spawns;
	}

	/**
	 * Gets an arraylist of spawning locations for the game provided.
	 * 
	 * @param gamename The game to look for
	 * @return The arraylist of spawners for the provided game
	 */
	public static ArrayList<ZALocation> getSpawns(String gamename) {
		ArrayList<ZALocation> locs = new ArrayList<ZALocation>();
		for (ZAGameBase zag : spawns.keySet())
			if (zag.getName() == gamename)
				locs.add(spawns.get(zag));
		return locs;
	}

	/**
	 * Gets a GameUndead instance associated with the provided entity.
	 * 
	 * @param e The entity to check for
	 * @return The GameUndead instance of the entity
	 */
	public static Undead getUndead(Entity e) {
		for (GameUndead gu : GlobalData.undead)
			if (gu.getZombie().getEntityId() == e.getEntityId())
				return gu;
		return null;
	}

	/**
	 * Gets the ZAMob linked to the provided entity if one is present.
	 * 
	 * @param e The entity to check for
	 * @return The ZAMob linked to the entity
	 */
	public static ZAMob getZAMob(Entity e) {
		if (e instanceof Zombie) {
			for (GameUndead gu : GlobalData.undead)
				if (gu.getZombie().getEntityId() == e.getEntityId())
					return gu;
		} else if (e instanceof Wolf)
			for (GameHellHound ghh : GlobalData.hellhounds)
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
	 * Gets a ZAPlayer from a player without using a string, if the player exists.
	 * 
	 * @param p The player to check for
	 * @return The ZAPlayer instance connected to that player
	 */
	public static ZAPlayer getZAPlayer(Player p) {
		for (ZAPlayerBase zap : GlobalData.players.values())
			if (zap.getName() == p.getName())
				return zap;
		return null;
	}

	/**
	 * Checks if the block given is a MysteryChest instance.
	 * 
	 * @param b The block to check for
	 * @return Whether or not this block is a mystery chest
	 */
	public static boolean isMysteryChest(Location loc) {
		return chests.keySet().contains(loc);
	}

	/**
	 * Checks if the specified entity is a ZA entity
	 * 
	 * @param e The entity to check for
	 * @return Whether or not the entity is a ZA entity
	 */
	public static boolean isZAMob(Entity e) {
		if (e != null)
			if ((e instanceof Wolf || e instanceof CraftWolf) && GlobalData.hellhounds != null) {
				for (GameHellHound gh : GlobalData.hellhounds)
					if (gh.getWolf().getEntityId() == e.getEntityId())
						return true;
			} else if ((e instanceof Zombie || e instanceof CraftZombie) && GlobalData.undead != null)
				for (GameUndead gu : GlobalData.undead)
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
		if (GlobalData.players.containsKey(player))
			return true;
		return false;
	}

	/**
	 * Refreshes the refreshable data in the Data class.
	 */
	public static void refresh() {
		for (ZAPlayerBase zap : GlobalData.players.values()) {
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
	public GlobalData(Plugin plugin) {
		GlobalData.plugin = (Ablockalypse) plugin;
		GlobalData.authors = plugin.getDescription().getAuthors();
		GlobalData.description = plugin.getDescription().getDescription();
		GlobalData.version = plugin.getDescription().getVersion();
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
