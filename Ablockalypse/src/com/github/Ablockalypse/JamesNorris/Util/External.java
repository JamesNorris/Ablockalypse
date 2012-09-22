package com.github.Ablockalypse.JamesNorris.Util;

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

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.Data.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Data.GameData;
import com.github.Ablockalypse.JamesNorris.Data.LocalizationData;
import com.github.Ablockalypse.JamesNorris.Implementation.Area;
import com.github.Ablockalypse.JamesNorris.Implementation.Barrier;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAGame;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayer;
import com.github.Ablockalypse.JamesNorris.Manager.YamlManager;

public class External {
	public static Plugin CommandsEX = Bukkit.getPluginManager().getPlugin("CommandsEX");
	public static boolean CommandsEXPresent = (CommandsEX == null && CommandsEX.isEnabled());// TODO fix NPE
	private static HashMap<SerializableLocation, Boolean> areaSavings;
	private static ArrayList<SerializableLocation> barrierSavings;
	public static YamlManager ym;
	public static Ablockalypse instance;
	private static List<String> loadedGames;
	public static String local = "local.yml", games = "games.yml", mainframes = "mainframes.bin", config = "config.yml", points = "points.bin", levels = "levels.bin", spawners = "spawners.bin", barriers = "barriers.bin", areas = "areas.bin";
	private static HashMap<String, SerializableLocation> mainframeSavings, spawnerSavings;
	@SuppressWarnings("unused") private final FileConfiguration localConfig = null;
	@SuppressWarnings("unused") private final File localf = null;
	public static File l, g;

	/**
	 * Reloads the configuration specified.
	 * 
	 * @param f The File to reload
	 * @param fc The FileConfiguration to use
	 * @param path The path of the file
	 */
	public static void reloadConfig(final File f, final String path) {
		final FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		final InputStream defStream = instance.getResource(path);
		if (defStream != null) {
			final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defStream);
			fc.setDefaults(defConfig);
		}
	}

	/**
	 * Gets the configuration specified
	 * 
	 * @param f The File to get
	 * @param fc The FileConfiguration to use
	 * @param path The path of the file
	 */
	public static FileConfiguration getConfig(final File f, final String path) {
		final FileConfiguration fc = null;
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
	 * Saves the configuration specified.
	 * 
	 * @param f The File to save
	 * @param fc The FileConfiguration to use
	 * @param path The path of the file
	 */
	public static void saveConfig(final File f, final FileConfiguration fc, final String path) {
		if (fc == null || f == null) {
			return;
		}
		try {
			getConfig(f, path).save(f);
		} catch (final IOException ex) {
			System.err.println("Could not save " + path + "!");
		}
	}

	/**
	 * Load an object from a file
	 * 
	 * @param path The path to retrieve
	 * @return The object inside of this path
	 * @throws Exception
	 */
	public static <O extends Object> O load(final String path) throws Exception {
		final ObjectInputStream is = new ObjectInputStream(new FileInputStream(path));
		@SuppressWarnings("unchecked") final O end = (O) is.readObject();
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
			final HashMap<String, SerializableLocation> save = External.load(mainframes);
			for (final String s : save.keySet()) {
				final SerializableLocation loc = save.get(s);
				final Location l = SerializableLocation.returnLocation(loc);
				for (final String s2 : loadedGames) {
					final ZAGame zag = Data.findGame(s2, false);
					zag.setSpawn(l);
				}
			}
			/* spawners.bin */
			final HashMap<String, SerializableLocation> save2 = External.load(spawners);
			for (final String s : save2.keySet()) {
				final SerializableLocation sl = save2.get(s);
				final Location l = SerializableLocation.returnLocation(sl);
				Data.spawners.put(s, l);
			}
			for (final String s2 : loadedGames) {
				final ZAGame zag = Data.findGame(s2, false);
				zag.loadSpawners();
			}
			/* barriers.bin */
			final ArrayList<SerializableLocation> save3 = External.load(barriers);
			for (final SerializableLocation sl : save3) {
				final Location l = SerializableLocation.returnLocation(sl);
				if (l.getBlock().getType() == Material.FENCE)
					new Barrier(l.getBlock());
			}
			/* areas.bin */
			final HashMap<SerializableLocation, Boolean> save4 = External.load(areas);
			for (final SerializableLocation sl : save4.keySet()) {
				final Location l = SerializableLocation.returnLocation(sl);
				final Block b = l.getBlock();
				if (b.getType() == Material.WOOD_DOOR || b.getType() == Material.IRON_DOOR) {
					final Area a = new Area(b);
					if (save4.get(sl))
						a.purchaseArea();
				}
			}
			/* points.bin */
			final HashMap<String, HashMap<String, Integer>> save5 = External.load(points);
			for (final String s : save5.keySet()) {
				if (loadedGames.contains(s)) {
					final HashMap<String, Integer> values = save5.get(s);
					for (final String s2 : values.keySet()) {
						final int i = values.get(s2);
						final ZAPlayer zap = Data.findZAPlayer(Bukkit.getPlayer(s2), s);
						final int current = zap.getPoints();
						if (current != 0)
							zap.subtractPoints(current);
						zap.addPoints(i);
					}
				}
			}
			/* levels.bin */
			final HashMap<String, Integer> save6 = External.load(levels);
			for (final String s : save6.keySet()) {
				if (loadedGames.contains(s)) {
					final int i = save6.get(s);
					final ZAGame zag = Data.findGame(s, true);
					zag.setLevel(i);
				}
			}
			/* CLEARING */
			save.clear();
			save2.clear();
			save3.clear();
			save4.clear();
			save5.clear();
			save6.clear();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Gets/Saves the defined resource.
	 */
	private static void loadResource(final String path) {
		final File a = new File(instance.getDataFolder(), path);
		if (!a.exists())
			instance.saveResource(path, true);
		else
			instance.getResource(path);
	}

	/**
	 * Runs through all config values and resources to allow for changes to be made to the plugin via external files.
	 * 
	 * @param instance The instance of the Ablockalypse plugin to be used in this method
	 */
	public static void runResources(final Ablockalypse instance) {
		External.instance = instance;
		try {
			/* GET THE FILES */
			/* config.yml */
			final File f = new File(instance.getDataFolder(), config);
			if (!f.exists())
				instance.saveDefaultConfig();
			/* local.yml */
			l = new File(instance.getDataFolder(), local);
			if (!l.exists()) {
				final FileConfiguration l2 = getConfig(l, local);
				saveConfig(l, l2, local);
			}
			/* games.yml */
			g = new File(instance.getDataFolder(), games);
			if (!g.exists()) {
				final FileConfiguration g2 = getConfig(g, games);
				saveConfig(g, g2, games);
			}
			/* mainframes.bin */
			loadResource(mainframes);
			/* spawners.bin */
			loadResource(spawners);
			/* barriers.bin */
			loadResource(barriers);
			/* areas.bin */
			loadResource(areas);
			/* points.bin */
			loadResource(points);
			/* levels.bin */
			loadResource(levels);
			/* CREATE DATA AND DATA MANAGERS */
			final ConfigurationData cd = new ConfigurationData(instance);
			final LocalizationData ld = new LocalizationData();
			final GameData gd = new GameData();
			ym = new YamlManager(cd, ld, gd);
		} catch (final Exception e) {
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
	public static <O extends Object> void save(final O object, final String path) throws Exception {
		final ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path));
		os.writeObject(object);
		os.flush();
		os.close();
	}

	/**
	 * Saves all of the information to the .bin files.
	 */
	public static void saveBinaries() {
		try {
			/* mainframes.bin */
			final HashMap<String, Location> save = Data.mainframes;
			for (final String s : save.keySet()) {
				final Location l = save.get(s);
				final SerializableLocation loc = new SerializableLocation(l);
				mainframeSavings.put(s, loc);
			}
			External.save(mainframeSavings, mainframes);// TODO figure out a way to NOT save anything that has already been saved.
			/* spawners.bin */
			final HashMap<String, Location> save2 = Data.spawners;
			for (final String s : save2.keySet()) {
				final Location l = save2.get(s);
				final SerializableLocation loc = new SerializableLocation(l);
				spawnerSavings.put(s, loc);
			}
			External.save(spawnerSavings, spawners);
			/* barriers.bin */
			final ArrayList<Location> save3 = Data.barriers;
			for (final Location l : save3) {
				final SerializableLocation loc = new SerializableLocation(l);
				barrierSavings.add(loc);
			}
			External.save(barrierSavings, barriers);
			/* areas.bin */
			final HashMap<Location, Boolean> save4 = Data.loadedareas;
			for (final Location l : save4.keySet()) {
				final boolean tf = save4.get(l);
				final SerializableLocation loc = new SerializableLocation(l);
				areaSavings.put(loc, tf);
			}
			/* points.bin */
			External.save(Data.playerPoints, points);
			/* levels.bin */
			External.save(Data.gameLevels, levels);
			/* Make all physical data safe, by replacing all broken game items */
			for (final Area a : Data.areas.values())
				a.safeReplace();
			for (final Barrier b : Data.gamebarriers)
				b.replaceBarrier();
			External.save(areaSavings, areas);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
