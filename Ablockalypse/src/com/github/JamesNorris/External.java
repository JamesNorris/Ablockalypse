package com.github.JamesNorris;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.Ablockalypse;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Data.LocalizationData;
import com.github.JamesNorris.Data.PerGameDataStorage;
import com.github.JamesNorris.Event.Bukkit.PlayerJoin;
import com.github.JamesNorris.Implementation.GameArea;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Implementation.GameMysteryChest;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Implementation.ZALocationBase;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Manager.YamlManager;

public class External {
	public static Plugin CommandsEX = Bukkit.getPluginManager().getPlugin("CommandsEX");
	public static boolean CommandsEXPresent = (CommandsEX != null && CommandsEX.isEnabled());
	private static FileConfiguration fc;
	public static Ablockalypse instance;
	public static File l, f;
	/* .bin paths */
	public static String folderlocation = "saved_data" + File.separatorChar;
	public static String filelocation = "plugins" + File.separatorChar + "Ablockalypse" + File.separatorChar;
	public static String local = "local.yml";
	public static String config = "config.yml";
	public static String gameData = folderlocation + "game_data.bin";
	/* end .bin paths */
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

	/**
	 * Retrieves all of the information from external files.
	 */
	public static void loadData() {
		try {
			/* game_data.bin */
			ArrayList<PerGameDataStorage> saved_data = External.load(filelocation + gameData);
			for (PerGameDataStorage pgds : saved_data) {
				String name = pgds.getName();
				ZAGame zag = Data.findGame(name);
				zag.setMainframe(pgds.getMainframe());
				for (String s : pgds.getPlayerPoints().keySet()) {
					int points = pgds.getPlayerPoints().get(s);
					Player p = Bukkit.getPlayer(s);
					if (p != null && p.isOnline())
						zag.addPlayer(p);
					else
						PlayerJoin.offlinePlayers.put(s, name);
					ZAPlayer zap = Data.findZAPlayer(p, name);
					zap.addPoints(points);
				}
				for (Location l : pgds.getBarrierLocations())
					new GameBarrier(l.getBlock(), (ZAGameBase) Data.findGame(name));
				for (Location l : pgds.getAreaPoints().keySet()) {
					Location l2 = pgds.getAreaPoints().get(l);
					GameArea a = new GameArea((ZAGameBase) zag, l, l2);
					if (pgds.isAreaOpen(l))
						a.open();
				}
				for (Location l : pgds.getMysteryChestLocations()) {
					Block b = l.getBlock();
					zag.addMysteryChest(new GameMysteryChest((Chest) b.getState(), zag, b.getLocation(), (pgds.getActiveChest() == l && zag.getActiveMysteryChest() == null)));	
				}
				int level = pgds.getLevel();
				if (zag.getPlayers().size() > 0)
					zag.setLevel(level);
				else
					PlayerJoin.gameLevels.put(name, level);
				for (Location l : pgds.getMobSpawnerLocations()) {
					ZALocationBase zaloc = new ZALocationBase(l);
					Data.spawns.put((ZAGameBase) zag, zaloc);
					zag.addMobSpawner(zaloc);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Gets/Saves the defined resource.
	 */
	protected static void loadResource(String path) {
		File a = new File(instance.getDataFolder(), path);
		if (path == local)
			l = a;
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
	public static void loadExternalFiles(Plugin instance) {
		External.instance = (Ablockalypse) instance;
		try {
			/* GET THE FILES */
			/* config.yml */
			f = new File(instance.getDataFolder(), config);
			if (!f.exists())
				instance.saveDefaultConfig();
			/* game_data.bin */
			loadResource(gameData);
			/* local.yml */
			loadResource(local);
			/* CREATE DATA AND DATA MANAGERS */
			ConfigurationData cd = new ConfigurationData(instance);
			LocalizationData ld = new LocalizationData(l, local);
			ym = new YamlManager(cd, ld);
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

	/**
	 * Saves all of the information to separate files to be retrieved onEnable().
	 */
	public static void saveData() {
		try {
			Data.refresh();
			/* game_data.bin */
			ArrayList<PerGameDataStorage> pgds = new ArrayList<PerGameDataStorage>();
			for (ZAGame zag : Data.games.values())
				pgds.add(new PerGameDataStorage(zag));
			External.save(pgds, filelocation + gameData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
