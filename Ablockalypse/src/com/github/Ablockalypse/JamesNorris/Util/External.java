package com.github.Ablockalypse.JamesNorris.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.Ablockalypse.Ablockalypse;
import com.github.Ablockalypse.JamesNorris.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data;
import com.github.Ablockalypse.JamesNorris.Implementation.Area;
import com.github.Ablockalypse.JamesNorris.Implementation.Barrier;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAGame;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayer;

public class External {
	private static HashMap<SerializableLocation, Boolean> areaSavings;
	private static ArrayList<SerializableLocation> barrierSavings;
	public static ConfigurationData cd;
	public static Ablockalypse instance;
	private static ArrayList<ZAGame> loadedGames;
	public static String games = "games.yml", mainframes = "mainframes.bin", config = "config.yml", points = "points.bin", levels = "levels.bin", spawners = "spawners.bin", barriers = "barriers.bin", areas = "areas.bin";
	private static HashMap<String, SerializableLocation> mainframeSavings, spawnerSavings;

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
			// TODO load game names from a .yml - If they no longer exist, prevent data from being loaded for those games
			/* mainframes.bin */
			HashMap<String, SerializableLocation> save = External.load(mainframes);
			for (String s : save.keySet()) {
				SerializableLocation loc = save.get(s);
				Location l = SerializableLocation.returnLocation(loc);
				for (ZAGame zag : loadedGames)
					zag.setSpawn(l);
			}
			/* spawners.bin */
			HashMap<String, SerializableLocation> save2 = External.load(spawners);
			for (String s : save2.keySet()) {
				SerializableLocation sl = save2.get(s);
				Location l = SerializableLocation.returnLocation(sl);
				Data.spawners.put(s, l);
			}
			for (ZAGame zag : loadedGames)
				zag.loadSpawners();
			/* barriers.bin */
			ArrayList<SerializableLocation> save3 = External.load(barriers);
			for (SerializableLocation sl : save3) {
				Location l = SerializableLocation.returnLocation(sl);
				if (l.getBlock().getType() == Material.FENCE)
					new Barrier(l.getBlock());
			}
			/* areas.bin */
			HashMap<SerializableLocation, Boolean> save4 = External.load(areas);
			for (SerializableLocation sl : save4.keySet()) {
				Location l = SerializableLocation.returnLocation(sl);
				Block b = l.getBlock();
				if (b.getType() == Material.WOOD_DOOR || b.getType() == Material.IRON_DOOR) {
					Area a = new Area(b);
					if (save4.get(sl))
						a.purchaseArea();
				}
			}
			/* points.bin */
			HashMap<String, HashMap<String, Integer>> save5 = External.load(points);
			for (String s : save5.keySet()) {
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
			}
			/* levels.bin */
			HashMap<String, Integer> save6 = External.load(levels);
			for (String s : save6.keySet()) {
				if (loadedGames.contains(s)) {
					int i = save6.get(s);
					ZAGame zag = Data.findGame(s, true);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Gets/Saves the defined resource.
	 */
	private static void loadResource(String path) {
		File a = new File(instance.getDataFolder(), path);
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
	public static void runConfig(Ablockalypse instance) {
		External.instance = instance;
		try {
			/* GET THE FILES */
			/* config.yml */
			File f = new File(instance.getDataFolder(), config);
			if (!f.exists())
				instance.saveDefaultConfig();
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
			/* GET THE VALUES FROM THE CONFIGURATION */
			cd = new ConfigurationData(instance);
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
	 */
	public static void saveBinaries() {
		try {
			/* mainframes.bin */
			HashMap<String, Location> save = Data.mainframes;
			for (String s : save.keySet()) {
				Location l = save.get(s);
				SerializableLocation loc = new SerializableLocation(l);
				mainframeSavings.put(s, loc);
			}
			External.save(mainframeSavings, mainframes);// TODO figure out a way to NOT save anything that has already been saved.
			/* spawners.bin */
			HashMap<String, Location> save2 = Data.spawners;
			for (String s : save2.keySet()) {
				Location l = save2.get(s);
				SerializableLocation loc = new SerializableLocation(l);
				spawnerSavings.put(s, loc);
			}
			External.save(spawnerSavings, spawners);
			/* barriers.bin */
			ArrayList<Location> save3 = Data.barriers;
			for (Location l : save3) {
				SerializableLocation loc = new SerializableLocation(l);
				barrierSavings.add(loc);
			}
			External.save(barrierSavings, barriers);
			/* areas.bin */
			HashMap<Location, Boolean> save4 = Data.loadedareas;
			for (Location l : save4.keySet()) {
				boolean tf = save4.get(l);
				SerializableLocation loc = new SerializableLocation(l);
				areaSavings.put(loc, tf);
			}
			/* points.bin */
			External.save(Data.playerPoints, points);
			/* levels.bin */
			External.save(Data.gameLevels, levels);
			/* Make all physical data safe, by replacing all broken game items */
			for (Area a : Data.areas.values())
				a.safeReplace();
			for (Barrier b : Data.gamebarriers)
				b.replaceBarrier();
			External.save(areaSavings, areas);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
