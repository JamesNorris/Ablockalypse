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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.Ablockalypse;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Data.LocalizationData;
import com.github.JamesNorris.Event.Bukkit.PlayerJoin;
import com.github.JamesNorris.Implementation.GameArea;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Manager.YamlManager;
import com.github.JamesNorris.Util.SerializableLocation;

public class External {
	private static HashMap<SerializableLocation, Boolean> areaSavings = new HashMap<SerializableLocation, Boolean>();
	private static HashMap<SerializableLocation, String> barrierSavings = new HashMap<SerializableLocation, String>();
	private static HashMap<String, SerializableLocation> spawnSavings = new HashMap<String, SerializableLocation>();
	private static HashMap<SerializableLocation, String> areaGameSavings = new HashMap<SerializableLocation, String>();
	private static HashMap<SerializableLocation, SerializableLocation> areaLocSavings = new HashMap<SerializableLocation, SerializableLocation>();
	public static Plugin CommandsEX = Bukkit.getPluginManager().getPlugin("CommandsEX");
	public static boolean CommandsEXPresent = (CommandsEX != null && CommandsEX.isEnabled());
	private static FileConfiguration fc;
	public static Ablockalypse instance;
	public static File l, f;
	/* .bin paths */
	public static String filelocation = "plugins" + File.separatorChar + "Ablockalypse" + File.separatorChar;
	public static String local = "local.yml";
	public static String players = "players.bin";
	public static String games = "games.bin";
	public static String mainframes = "mainframes.bin";
	public static String config = "config.yml";
	public static String points = "points.bin";
	public static String levels = "levels.bin";
	public static String barriers = "barriers.bin";
	public static String areas = "areas.bin";
	public static String spawns = "spawns.bin";
	public static String areagames = "areagames.bin";
	public static String arealocs = "arealocs.bin";
	/* end .bin paths */
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
	 * Retrieves all of the information from external files.
	 */
	public static void loadData() {
		try {
			/* games.bin */
			ArrayList<String> loadedGames = External.load(filelocation + games);
			/* mainframes.bin */
			HashMap<String, SerializableLocation> save = External.load(filelocation + mainframes);
			if (save != null)
				for (String s : save.keySet()) {
					SerializableLocation loc = save.get(s);
					Location l = SerializableLocation.returnLocation(loc);
					for (String s2 : loadedGames) {
						ZAGame zag = Data.findGame(s2);
						zag.setMainframe(l);
					}
				}
			/* players.bin */
			HashMap<String, String> save2 = External.load(filelocation + players);
			if (save2 != null)
				for (String s : save2.keySet())
					if (Data.gameExists(s)) {
						ZAGame zag = Data.findGame(s);
						Player p = Bukkit.getPlayer(save2.get(s));
						if (p != null && p.isOnline())
							zag.addPlayer(p);
						else {
							PlayerJoin.offlinePlayers.put(save2.get(s), s);
						}
					}
			/* barriers.bin */
			HashMap<SerializableLocation, String> save3 = External.load(filelocation + barriers);
			if (save3 != null)
				for (SerializableLocation sl : save3.keySet()) {
					String gamename = save3.get(sl);
					if (loadedGames.contains(gamename)) {
						Location l = SerializableLocation.returnLocation(sl);
						new GameBarrier(l.getBlock(), (ZAGameBase) Data.findGame(gamename));
					}
				}
			/* areas.bin */
			HashMap<SerializableLocation, SerializableLocation> save9 = External.load(filelocation + arealocs);
			HashMap<SerializableLocation, Boolean> save4 = External.load(filelocation + areas);
			HashMap<SerializableLocation, String> save8 = External.load(filelocation + areagames);
			if (save4 != null)
				for (SerializableLocation sl : save9.keySet()) {
					for (SerializableLocation sl2 : save4.keySet()) {
						for (SerializableLocation sl3 : save8.keySet()) {
							Location l = SerializableLocation.returnLocation(sl);
							Location l2 = SerializableLocation.returnLocation(save9.get(sl));
							Location l3 =  SerializableLocation.returnLocation(sl2);
							Location l4 = SerializableLocation.returnLocation(sl3);
							if ((l.getBlockX() == l3.getBlockX() && l3.getBlockX() == l4.getBlockX()) && (l.getBlockZ() == l3.getBlockZ() && l3.getBlockZ() == l4.getBlockZ()) && (l.getBlockY() == l3.getBlockY() && l3.getBlockY() == l4.getBlockY())){
							if (loadedGames.contains(save8.get(sl3))) {
								GameArea a = new GameArea((ZAGameBase) Data.findGame(save8.get(sl3)));
								if (l != null && l2 != null) {
									a.setLocation(l, 1);
									a.setLocation(l2, 2);
								}
								if (save4.get(sl2))
									a.open();
							}
							}
						}
					}
				}
			/* points.bin */
			HashMap<String, HashMap<String, Integer>> save5 = External.load(filelocation + points);
			if (save5 != null)
				for (String s : save5.keySet())
					if (loadedGames.contains(s)) {
						HashMap<String, Integer> values = save5.get(s);
						for (String s2 : values.keySet()) {
							Player p = Bukkit.getPlayer(s2);
							int i = values.get(s2);
							if (p != null && p.isOnline()) {
								ZAPlayer zap = Data.findZAPlayer(p, s);
								int current = zap.getPoints();
								if (current != 0)
									zap.subtractPoints(current);
								zap.addPoints(i);
							} else {
								PlayerJoin.offlinePlayerPoints.put(s2, i);
							}
						}
					}
			/* levels.bin */
			HashMap<String, Integer> save6 = External.load(filelocation + levels);
			if (save6 != null)
				for (String s : save6.keySet())
					if (loadedGames.contains(s)) {
						int i = save6.get(s);
						ZAGame zag = Data.findGame(s);
						if (zag.getPlayers().size() > 0) {
							zag.setLevel(i);
						} else {
							PlayerJoin.gameLevels.put(s, i);
						}
					}
			/* spawns.bin */
			HashMap<String, SerializableLocation> save7 = External.load(filelocation + spawns);
			if (save7 != null)
				for (String s : save7.keySet())
					if (loadedGames.contains(s)) {
						SerializableLocation sl = save7.get(s);
						Location l = SerializableLocation.returnLocation(sl);
						ZAGameBase zag = (ZAGameBase) Data.findGame(s);
						Data.spawns.put(zag, l);
						zag.addMobSpawner(l);
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
			if (save7 != null)
				save7.clear();
			if (save8 != null)
				save8.clear();
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
	public static void runResources(Plugin instance) {
		External.instance = (Ablockalypse) instance;
		try {
			/* GET THE FILES */
			/* config.yml */
			f = new File(instance.getDataFolder(), config);
			if (!f.exists())
				instance.saveDefaultConfig();
			/* local.yml */
			l = new File(instance.getDataFolder(), local);
			loadConfig(l, local);
			/* games.bin */
			loadResource(games);
			/* mainframes.bin */
			loadResource(mainframes);
			/* players.bin */
			loadResource(players);
			/* barriers.bin */
			loadResource(barriers);
			/* areas.bin */
			loadResource(areas);
			/* arealocs.bin */
			loadResource(arealocs);
			/* areagames.bin */
			loadResource(areagames);
			/* points.bin */
			loadResource(points);
			/* levels.bin */
			loadResource(levels);
			/* spawns.bin */
			loadResource(spawns);
			/* CREATE DATA AND DATA MANAGERS */
			ConfigurationData cd = new ConfigurationData((Ablockalypse) instance);
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
	 * Saves all of the information to separate files to be retrieved onEnable().
	 */
	public static void saveData() {
		try {
			boolean restore = Ablockalypse.disable;
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
					External.save(mainframeSavings, filelocation + mainframes);
				}
			}
			/* players.bin */
			if (Data.playergames != null)
				External.save(Data.playergames, filelocation + players);
			/* barriers.bin */
			if (Data.barriers != null) {
				HashMap<Location, String> save3 = Data.barriers;
				if (save3 != null) {
					for (Location l : save3.keySet()) {
						SerializableLocation loc = new SerializableLocation(l);
						barrierSavings.put(loc, save3.get(l));
					}
					External.save(barrierSavings, filelocation + barriers);
				}
			}
			/* areas.bin, areagames.bin and arealocs.bin */
			if (Data.areas != null) {
				for (GameArea ga : Data.areas) {
					SerializableLocation sl = new SerializableLocation(ga.getPoint(1));
					areaSavings.put(sl, ga.isOpened());
					areaGameSavings.put(sl, ga.getGame().getName());
					areaLocSavings.put(sl, new SerializableLocation(ga.getPoint(2)));
				}
			}
			/* spawns.bin */
			if (Data.getSpawns() != null) {
				HashMap<ZAGameBase, Location> save5 = Data.getSpawns();
				for (ZAGameBase zag : save5.keySet()) {
					String s = zag.getName();
					Location l = save5.get(zag);
					SerializableLocation loc = new SerializableLocation(l);
					spawnSavings.put(s, loc);
				}
				External.save(spawnSavings, filelocation + spawns);
			}
			/* points.bin */
			if (Data.playerPoints != null)
				External.save(Data.playerPoints, filelocation + points);
			/* levels.bin */
			if (Data.gameLevels != null)
				External.save(Data.gameLevels, filelocation + levels);
			/* Make all physical data safe, by replacing all broken game items */
			if (Data.areas != null && restore)
				for (GameArea a : Data.areas)
					a.close();
			if (Data.gamebarriers != null && restore)
				for (GameBarrier b : Data.gamebarriers)
					b.replaceBarrier();
			/* areas.bin, areagames.bin and arealocs.bin saving */
			if (areaSavings != null && areas != null && areaGameSavings != null) {
				External.save(areaSavings, filelocation + areas);
				External.save(areaGameSavings, filelocation + areagames);
				External.save(areaLocSavings, filelocation + arealocs);
			}
			/* games.bin saving */
			if (Data.games != null) {
				ArrayList<String> gamesaves = new ArrayList<String>();
				for (String s : Data.games.keySet())
					gamesaves.add(s);
				External.save(gamesaves, filelocation + games);
			}
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
