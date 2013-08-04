package com.github;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.plugin.Plugin;

import com.github.manager.ItemFileManager;

public class External {
    public static boolean download(InputStream in, OutputStream out) {
        if (in == null || out == null) {
            return false;
        }
        try {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.close();
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    public static <O extends Object> O load(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
        return load(is);
    }

    public static <O extends Object> O load(String path) throws IOException, ClassNotFoundException {
        ObjectInputStream is = new ObjectInputStream(new FileInputStream(path));
        return load(is);
    }

    public static boolean newVersionAvailable(URL url, File file) {
        try {
            URLConnection connection = url.openConnection();
            long lastmodURL = connection.getLastModified();
            long lastmodFile = file.lastModified();
            return lastmodURL > lastmodFile;
        } catch (Exception e) {
            return false;
        }
    }

    public static <O extends Object> void save(O object, File file) throws IOException {
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
        save(object, os);
    }

    public static <O extends Object> void save(O object, String path) throws IOException {
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path));
        save(object, os);
    }

    private static <O extends Object> O load(ObjectInputStream is) throws IOException, ClassNotFoundException {
        @SuppressWarnings("unchecked") O end = (O) is.readObject();
        is.close();
        return end;
    }

    private static <O extends Object> void save(O object, ObjectOutputStream os) throws IOException {
        os.writeObject(object);
        os.flush();
        os.close();
    }

    private File configuration, localization, items, savedData, mapData;
    private ItemFileManager itemsManager;

    public External(Plugin instance) {
        File dataFolder = instance.getDataFolder();
        configuration = new File(dataFolder, "config.yml");
        ensureExistence(instance.getResource("config.yml"), configuration);
        localization = new File(dataFolder, "local.yml");
        ensureExistence(instance.getResource("local.yml"), localization);
        items = new File(dataFolder, "items.yml");
        ensureExistence(instance.getResource("items.yml"), items);
        savedData = new File(dataFolder, "saved_data");
        ensureDirectory(savedData);
        mapData = new File(dataFolder, "map_data");
        ensureDirectory(mapData);
        itemsManager = new ItemFileManager(items);
    }

    public File getConfigurationFile() {
        return configuration;
    }

    public ItemFileManager getItemFileManager() {
        return itemsManager;
    }

    public File getItemsFile() {
        return items;
    }

    public File getLocalizationFile() {
        return localization;
    }

    public File getMapDataFile(String mapname, boolean force) {
        try {
            String path = "mapdata" + File.separatorChar + mapname + ".map";
            File saveFile = new File(Ablockalypse.getInstance().getDataFolder(), path);
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
            Ablockalypse.crash("The block mapdata file: <mapdata" + File.separatorChar + mapname + ".map> could not be found or created.", 5);
        }
        return null;
    }

    public File getMapDataFolder() {
        return mapData;
    }

    public File getMapGameObjectDataFile(String mapname, boolean force) {
        try {
            String path = "mapdata" + File.separatorChar + mapname + ".objects";
            File saveFile = new File(Ablockalypse.getInstance().getDataFolder(), path);
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
            Ablockalypse.crash("The game object mapdata file: <mapdata" + File.separatorChar + mapname + ".objects> could not be found or created.", 5);
        }
        return null;
    }

    public File getSavedDataFile(String gamename, boolean force) {
        try {
            String path = "saved_data" + File.separatorChar + gamename + ".bin";
            File saveFile = new File(Ablockalypse.getInstance().getDataFolder(), path);
            if (force) {
                if (!saveFile.getParentFile().exists()) {
                    saveFile.getParentFile().mkdirs();
                }
                if (!saveFile.exists()) {
                    saveFile.createNewFile();
                }
            }
            return saveFile;
        } catch (IOException e) {
            Ablockalypse.crash("The save file: <saved_data" + File.separatorChar + gamename + ".bin> could not be found or created.", 5);
        }
        return null;
    }

    public File getSavedDataFolder() {
        return savedData;
    }

    protected void ensureDirectory(File file) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.mkdir();
    }

    protected void ensureExistence(InputStream resource, File file) {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
                download(resource, new FileOutputStream(file));
            }
        } catch (IOException e) {
            Ablockalypse.crash("Existence could not be ensured for file: <" + file.getAbsolutePath() + ">. This is probably due to an InputStream issue.", 5);
        }
    }
}
