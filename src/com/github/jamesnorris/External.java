package com.github.jamesnorris;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.bukkit.plugin.Plugin;

import com.github.Ablockalypse;
import com.github.jamesnorris.manager.ItemFileManager;

public class External {
    private File configuration, localization, items, savedData, mapData;
    private ItemFileManager itemsManager;

    public External(Plugin instance) {
        File dataFolder = instance.getDataFolder();
        configuration = new File(dataFolder, "config.yml");
        ensureExistence(configuration, false);
        localization = new File(dataFolder, "local.yml");
        ensureExistence(localization, false);
        items = new File(dataFolder, "items.yml");
        ensureExistence(items, false);
        savedData = new File(dataFolder, "saved_data");
        ensureExistence(savedData, true);
        mapData = new File(dataFolder, "mapdata");
        ensureExistence(mapData, true);
        itemsManager = new ItemFileManager(items);
    }

    protected void ensureExistence(File file, boolean directory) {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }            
            if (!file.exists()) {
                file.createNewFile();
            }
            if (directory) {
                file.mkdir();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <O extends Object> O load(String path) throws IOException, ClassNotFoundException {
        ObjectInputStream is = new ObjectInputStream(new FileInputStream(path));
        return load(is);
    }

    public static <O extends Object> O load(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
        return load(is);
    }

    private static <O extends Object> O load(ObjectInputStream is) throws IOException, ClassNotFoundException {
        @SuppressWarnings("unchecked") O end = (O) is.readObject();
        is.close();
        return end;
    }

    public File getConfigurationFile() {
        return configuration;
    }

    public ItemFileManager getItemFileManager() {
        return itemsManager;
    }

    public File getLocalizationFile() {
        return localization;
    }

    public File getItemsFile() {
        return items;
    }

    public File getMapDataFile(String mapname, boolean force) {
        try {
            String path = "mapdata" + File.separatorChar + mapname + ".map";
            File saveFile = new File(Ablockalypse.instance.getDataFolder(), path);
            if (force) {
                if (!saveFile.getParentFile().exists()) {
                    saveFile.getParentFile().mkdirs();
                }
                if (!saveFile.exists()) {// new file given the game name with the suffix ".dat"
                    saveFile.createNewFile();
                }
            }
            return saveFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public File getSavedDataFile(String gamename, boolean force) {
        try {
            File thisSavedFile = new File(Ablockalypse.getInstance().getDataFolder(), "saved_data" + File.separatorChar + gamename);
            if (force) {
                if (!thisSavedFile.getParentFile().exists()) {
                    thisSavedFile.getParentFile().mkdirs();
                }
                if (!thisSavedFile.exists()) {
                    thisSavedFile.createNewFile();
                }
            }
            return thisSavedFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public File getMapDataFolder() {
        return mapData;
    }

    public File getSavedDataFolder() {
        return savedData;
    }

    public static <O extends Object> void save(O object, String path) throws IOException {
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path));
        save(object, os);
    }

    public static <O extends Object> void save(O object, File file) throws IOException {
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
        save(object, os);
    }

    private static <O extends Object> void save(O object, ObjectOutputStream os) throws IOException {
        os.writeObject(object);
        os.flush();
        os.close();
    }
}
