package com.github.JamesNorris;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.github.Ablockalypse;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Data.GameData;
import com.github.JamesNorris.Data.LocalizationData;
import com.github.JamesNorris.Implementation.GameArea;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Manager.YamlManager;
import com.github.JamesNorris.Util.SerializableLocation;

public class External {
	private static HashMap<SerializableLocation, Boolean> areaSavings = new HashMap<SerializableLocation, Boolean>();
	private static ArrayList<SerializableLocation> barrierSavings = new ArrayList<SerializableLocation>();
	public static Plugin CommandsEX = Bukkit.getPluginManager().getPlugin("CommandsEX");
	public static boolean CommandsEXPresent = (CommandsEX != null && CommandsEX.isEnabled());
	private static FileConfiguration fc;
	public static Ablockalypse instance;
	public static File l, g, f;
	private static List<String> loadedGames;
	public static String local = "local.yml", players = "players.bin", games = "games.yml", mainframes = "mainframes.bin", config = "config.yml", points = "points.bin", levels = "levels.bin", barriers = "barriers.bin", areas = "areas.bin";
	private static HashMap<String, SerializableLocation> mainframeSavings = new HashMap<String, SerializableLocation>();
	public static YamlManager ym;

	/**
	 * Gets the configuration specified
	 * 
	 * @param f The File to get
	 * @param fc The FileConfiguration to use
	 * @param path The path of the file
	 */
	public static FileConfiguration getConfig(File f, String path) {
		fc = null;
		reloadConfig(f, path);
		return fc;
	}

	/**
	 * Gets the YamlManager for this plugin if it is not null.
	 * 
	 * @return The YamlManager for this plugin
	 */
	public static YamlManager getYamlManager() {
		return ym;
	}

	/**
	 * Load an object from a file
	 * 
	 * @param path The path to retrieve
	 * @return The object inside of this path
	 * @throws Exception
	 */
	public static <O extends Object> O load(String path) throws Exception {
		ObjectInputStream is = new ObjectInputStream(new FileInputStream(path));
		@SuppressWarnings("unchecked") O end = (O) is.readObject();
		is.close();
		return end;
	}

	/**
	 * Retrieves all of the information from the .bin files.
	 */
	public static void loadBinaries() {
		try {
			loadedGames = ym.getGameData().getSavedGames();
			/* mainframes.bin */
			HashMap<String, SerializableLocation> save = External.load(mainframes);
			if (save != null)
				for (String s : save.keySet()) {
					SerializableLocation loc = save.get(s);
					Location l = SerializableLocation.returnLocation(loc);
					for (String s2 : loadedGames) {
						ZAGame zag = Data.findGame(s2);
						zag.setSpawn(l);
					}
				}
			/* players.bin */
			HashMap<String, String> save2 = External.load(players);
			if (save2 != null)
				for (String s : save2.keySet())
					if (Data.gameExists(s)) {
						ZAGame zag = Data.findGame(s);
						zag.addPlayer(Bukkit.getServer().getPlayer(save2.get(s)));
					}
			/* barriers.bin */
			ArrayList<SerializableLocation> save3 = External.load(barriers);
			if (save3 != null)
				for (SerializableLocation sl : save3) {
					Location l = SerializableLocation.returnLocation(sl);
					if (l.getBlock().getType() == Material.FENCE)
						new GameBarrier(l.getBlock());
				}
			/* areas.bin */
			HashMap<SerializableLocation, Boolean> save4 = External.load(areas);
			if (save4 != null)
				for (SerializableLocation sl : save4.keySet()) {
					Location l = SerializableLocation.returnLocation(sl);
					Block b = l.getBlock();
					if (b.getType() == Material.WOOD_DOOR || b.getType() == Material.IRON_DOOR) {
						GameArea a = new GameArea(b);
						if (save4.get(sl))
							a.purchaseArea();
					}
				}
			/* points.bin */
			HashMap<String, HashMap<String, Integer>> save5 = External.load(points);
			if (save5 != null)
				for (String s : save5.keySet())
					if (loadedGames.contains(s)) {
						HashMap<String, Integer> values = save5.get(s);
						for (String s2 : values.keySet()) {
							int i = values.get(s2);
							ZAPlayer zap = Data.findZAPlayer(Bukkit.getPlayer(s2), s);
							int current = zap.getPoints();
							if (current != 0)
								zap.subtractPoints(current);
							zap.addPoints(i);
						}
					}
			/* levels.bin */
			HashMap<String, Integer> save6 = External.load(levels);
			if (save6 != null)
				for (String s : save6.keySet())
					if (loadedGames.contains(s)) {
						int i = save6.get(s);
						ZAGame zag = Data.findGame(s);
						zag.setLevel(i);
					}
			/* CLEARING */
			if (save != null)
				save.clear();
			if (save2 != null)
				save2.clear();
			if (save3 != null)
				save3.clear();
			if (save4 != null)
				save4.clear();
			if (save5 != null)
				save5.clear();
			if (save6 != null)
				save6.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Gets/Saves the defined configuration.
	 * NOTE: This does not work for the default config.
	 */
	protected static void loadConfig(File f, String path) {
		if (!f.exists()) {
			FileConfiguration l2 = getConfig(f, path);
			saveConfig(f, l2, path);
		}
	}

	/*
	 * Gets/Saves the defined resource.
	 */
	protected static void loadResource(String path) {
		File a = new File(instance.getDataFolder(), path);
		if (!a.exists())
			instance.saveResource(path, true);
		else
			instance.getResource(path);
	}

	/**
	 * Reloads the configuration specified.
	 * 
	 * @param f The File to reload
	 * @param fc The FileConfiguration to use
	 * @param path The path of the file
	 */
	public static void reloadConfig(File f, String path) {
		fc = YamlConfiguration.loadConfiguration(f);
		InputStream defStream = instance.getResource(path);
		if (defStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defStream);
			fc.setDefaults(defConfig);
		}
	}

	/**
	 * Runs through all config values and resources to allow for changes to be made to the plugin via external files.
	 * 
	 * @param instance The instance of the Ablockalypse plugin to be used in this method
	 */
	public static void runResources(Ablockalypse instance) {
		External.instance = instance;
		try {
			/* GET THE FILES */
			/* config.yml */
			f = new File(instance.getDataFolder(), config);
			if (!f.exists())
				instance.saveDefaultConfig();
			/* local.yml */
			l = new File(instance.getDataFolder(), local);
			loadConfig(l, local);
			/* games.yml */
			g = new File(instance.getDataFolder(), games);
			loadConfig(g, games);
			/* mainframes.bin */
			loadResource(mainframes);
			/* players.bin */
			loadResource(players);
			/* barriers.bin */
			loadResource(barriers);
			/* areas.bin */
			loadResource(areas);
			/* points.bin */
			loadResource(points);
			/* levels.bin */
			loadResource(levels);
			/* CREATE DATA AND DATA MANAGERS */
			ConfigurationData cd = new ConfigurationData(instance);
			LocalizationData ld = new LocalizationData(l, local);
			GameData gd = new GameData();
			ym = new YamlManager(cd, ld, gd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save an object to a file
	 * 
	 * @param object The object to be saved to the file
	 * @param path The file path to save to
	 * @throws Exception
	 */
	public static <O extends Object> void save(O object, String path) throws Exception {
		ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path));
		os.writeObject(object);
		os.flush();
		os.close();
	}

	/**
	 * Saves all of the information to the .bin files.
	 * 
	 * @param restore Whether or not to reset everything to make it save for server stop/restart.
	 */
	public static void saveBinaries(boolean restore) {
		try {
			Data.refresh();
			/* mainframes.bin */
			if (Data.mainframes != null) {
				HashMap<String, Location> save = Data.mainframes;
				if (save != null) {
					for (String s : save.keySet()) {
						Location l = save.get(s);
						SerializableLocation loc = new SerializableLocation(l);
						mainframeSavings.put(s, loc);
					}
					External.save(mainframeSavings, mainframes);// TODO figure out a way to NOT save anything that has already been saved.
				}
			}
			/* players.bin */
			if (Data.playergames != null)
				External.save(Data.playergames, players);
			/* barriers.bin */
			if (Data.barriers != null) {
				ArrayList<Location> save3 = Data.barriers;
				if (save3 != null) {
					for (Location l : save3) {
						SerializableLocation loc = new SerializableLocation(l);
						barrierSavings.add(loc);
					}
					External.save(barrierSavings, barriers);
				}
			}
			/* areas.bin */
			if (Data.loadedareas != null) {
				HashMap<Location, Boolean> save4 = Data.loadedareas;
				if (save4 != null)
					for (Location l : save4.keySet()) {
						boolean tf = save4.get(l);
						SerializableLocation loc = new SerializableLocation(l);
						areaSavings.put(loc, tf);
					}
			}
			/* points.bin */
			if (Data.playerPoints != null)
				External.save(Data.playerPoints, points);
			/* levels.bin */
			if (Data.gameLevels != null)
				External.save(Data.gameLevels, levels);
			/* Make all physical data safe, by replacing all broken game items */
			if (Data.areas != null)
				for (GameArea a : Data.areas.values())
					if (restore)
						a.safeReplace();
			if (Data.gamebarriers != null)
				for (GameBarrier b : Data.gamebarriers)
					if (restore)
						b.replaceBarrier();
			if (areaSavings != null && areas != null)
				External.save(areaSavings, areas);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves the configuration specified.
	 * 
	 * @param f The File to save
	 * @param fc The FileConfiguration to use
	 * @param path The path of the file
	 */
	public static void saveConfig(File f, FileConfiguration fc, String path) {
		if (fc == null || f == null)
			return;
		try {
			getConfig(f, path).save(f);
		} catch (IOException ex) {
			System.err.println("Could not save " + path + "!");
		}
	}

	@SuppressWarnings("unused") private FileConfiguration localConfig = null;
	@SuppressWarnings("unused") private File localf = null;
}
