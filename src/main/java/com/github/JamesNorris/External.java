package com.github.JamesNorris;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.github.Ablockalypse;
import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Data.PerGameDataStorage;
import com.github.JamesNorris.Enumerated.MessageDirection;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Util.SpecificMessage;

public class External {
    public static Plugin CommandsEX = Bukkit.getPluginManager().getPlugin("CommandsEX");
    public static boolean CommandsEXPresent = (CommandsEX != null && CommandsEX.isEnabled());
    public static String config = "config.yml";
    private static FileConfiguration fc;
    public static String filelocation = "plugins" + File.separatorChar + "Ablockalypse" + File.separatorChar;
    /* .bin paths */
    public static String folderlocation = "saved_data" + File.separatorChar;
    public static String gameData = folderlocation + "game_data.bin";
    public static Ablockalypse instance;
    public static String local = "local.yml";
    public static String mapdatafolderlocation = "map_data" + File.separatorChar;
    public static File localizationFile, configFile, gameDataFile;

    /**
     * Deletes the file given.
     * 
     * @param file The file to delete
     */
    public static void deleteFile(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFile(f);
                } else {
                    f.delete();
                }
            }
        }
        file.delete();
    }

    /* end .bin paths */
    /**
     * Gets the configuration specified
     * 
     * @param f The File to get
     * @param path The path of the file
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
            GlobalData data = DataManipulator.data;
            /* game_data.bin */
            ArrayList<PerGameDataStorage> saved_data = External.load(filelocation + gameData);
            for (PerGameDataStorage pgds : saved_data) {
                pgds.load(data);
            }
        } catch (EOFException e) {
            System.err.println("The saved_data.bin file could not be found while loading!");
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
    public static void reloadConfig(File f, String path) {
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
            MessageTransfer.sendMessage(new SpecificMessage(MessageDirection.CONSOLE_ERROR, "Could not save " + path + "!"));
        }
    }

    /**
     * Saves all of the information to separate files to be retrieved onEnable().
     */
    public static void saveData() {
        try {
            GlobalData data = Ablockalypse.instance.data;
            /* game_data.bin */
            ArrayList<PerGameDataStorage> pgds = new ArrayList<PerGameDataStorage>();
            for (ZAGame zag : data.games.values())
                pgds.add(new PerGameDataStorage(zag));
            External.save(pgds, filelocation + gameData);
        } catch  (EOFException e) {
            System.err.println("The saved_data.bin file could not be found while saving!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
