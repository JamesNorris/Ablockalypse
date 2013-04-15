package com.github.jamesnorris;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.github.Ablockalypse;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.manager.ItemFileManager;
import com.github.jamesnorris.storage.PerGameDataStorage;

public class External extends DataManipulator {
    private static String config = "config.yml";
    private static FileConfiguration fc;
    public static String filelocation = "plugins" + File.separatorChar + "Ablockalypse" + File.separatorChar;
    private static String folderlocation = "saved_data" + File.separatorChar;
    private static String gameData = folderlocation + "game_data.bin";
    private static Ablockalypse instance;
    public static String local = "local.yml";
    public static String items = "items.yml";
    public static String mapdatafolderlocation = "map_data" + File.separatorChar;
    public static File localizationFile;
    private static File configFile;
    private static File itemsFile;
    public static ItemFileManager itemManager;
    // start unused
    @SuppressWarnings("unused") private static File gameDataFile;
    // end unused
    
    /**
     * Gets the configuration specified
     * 
     * @param f The File to get
     * @param path The path of the file
     * @return The FileConfiguration of this file
     */
    public static FileConfiguration getConfig(File f, String path) {
        fc = null;
        reloadConfig(f, path);
        return fc;
    }

    /**
     * Load an object from a file
     * 
     * @param path The path to retrieve
     * @return The object inside of this path
     * @throws ClassNotFoundException If the object cannot be found
     * @throws IOException If the object cannot be read
     */
    public static <O extends Object> O load(String path) throws IOException, ClassNotFoundException {
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
                pgds.load(data);
            }
            itemManager = new ItemFileManager(itemsFile);
        } catch (EOFException e) {
            System.err.println("[Ablockalypse] The game_data.bin file could not be found!");
        } catch (Exception e) {
            e.printStackTrace();
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
            configFile = new File(instance.getDataFolder(), config);
            if (!configFile.exists())
                instance.saveDefaultConfig();
            /* game_data.bin */
            gameDataFile = loadResource(gameData);
            /* local.yml */
            localizationFile = loadResource(local);
            /* items.yml */
            itemsFile = loadResource(items);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Gets/Saves the defined resource.
     */
    protected static File loadResource(String path) {
        File a = new File(instance.getDataFolder(), path);
        if (!a.exists())
            instance.saveResource(path, true);
        return a;
    }

    /**
     * Reloads the configuration specified.
     * 
     * @param f The File to reload
     * @param path The path of the file
     */
    private static void reloadConfig(File f, String path) {
        fc = YamlConfiguration.loadConfiguration(f);
        InputStream defStream = instance.getResource(path);
        if (defStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defStream);
            fc.setDefaults(defConfig);
        }
    }

    /**
     * Save an object to a file
     * 
     * @param object The object to be saved to the file
     * @param path The file path to save to
     * @throws IOException If the object cannot be written to the path
     */
    public static <O extends Object> void save(O object, String path) throws IOException {
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
    private static void saveConfig(File f, FileConfiguration fc, String path) {
        if (fc == null || f == null)
            return;
        try {
            getConfig(f, path).save(f);
        } catch (IOException ex) {
            System.out.println("[Ablockalypse] Could not save " + path + "!");
        }
    }

    /**
     * Saves all of the information to separate files to be retrieved onEnable().
     */
    public static void saveData() {
        try {
            /* game_data.bin */
            ArrayList<PerGameDataStorage> pgds = new ArrayList<PerGameDataStorage>();
            for (Game zag : data.games.values())
                pgds.add(new PerGameDataStorage(zag));
            External.save(pgds, filelocation + gameData);
        } catch (EOFException e) {
            System.err.println("[Ablockalypse] The game_data.bin file could not be found!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
