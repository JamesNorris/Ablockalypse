package com.github.jamesnorris;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.bukkit.plugin.Plugin;

import com.github.Ablockalypse;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.manager.ItemFileManager;
import com.github.jamesnorris.storage.PerGameDataStorage;

public class External {
    public static String filelocation = "plugins" + File.separatorChar + "Ablockalypse" + File.separatorChar;
    public static ItemFileManager itemManager;
    public static String items = "items.yml";
    public static String local = "local.yml";
    public static File localizationFile, configFile, itemsFile;
    public static String mapdatafolderlocation = "map_data" + File.separatorChar;
    private static String config = "config.yml";
    private static DataContainer data = Ablockalypse.getData();
    @SuppressWarnings("unused") private static File gameDataFile;
    private static Ablockalypse instance;
    private static String savedDataFolderLocation = "saved_data" + File.separatorChar;
    private static String gameData = savedDataFolderLocation + "game_data.bin";
    
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
        } catch (EOFException e) {
            System.err.println("[Ablockalypse] The game_data.bin file could not be found!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            itemManager = new ItemFileManager(itemsFile);
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
            if (!configFile.exists()) {
                instance.saveDefaultConfig();
            }
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
     * Saves all of the information to separate files to be retrieved onEnable().
     */
    public static void saveData() {
        try {
            /* game_data.bin */
            ArrayList<PerGameDataStorage> pgds = new ArrayList<PerGameDataStorage>();
            for (Game zag : data.games.values()) {
                pgds.add(new PerGameDataStorage(zag));
            }
            External.save(pgds, filelocation + gameData);
        } catch (EOFException e) {
            System.err.println("[Ablockalypse] The game_data.bin file could not be found!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Gets/Saves the defined resource. */
    protected static File loadResource(String path) {
        File a = new File(instance.getDataFolder(), path);
        if (!a.exists()) {
            try {
                a.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                instance.saveResource(path, true);
            }
        }
        return a;
    }
}
