package com.github.jamesnorris;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.plugin.Plugin;

import com.github.Ablockalypse;

public class Update {
    private String finished = "[Ablockalypse] Update completed, please restart the server!";
    private String started = "[Ablockalypse] Update found, please wait while the new version is downloaded...";
    private Ablockalypse plugin;
    public int updatePercent = 0;
    public boolean isUpdating = false;

    /**
     * Creates a new instance of the Auto-Updater for Ablockalypse.
     * This should only be used to download from dev.bukkit.org, otherwise it is against the policy of curse, and
     * the plugin will not be accepted.
     * 
     * @param instance The instance of Ablockalypse to use for the Updater
     */
    public Update(Plugin instance) {
        plugin = (Ablockalypse) instance;
    }

    /*
     * Downloads an update from the update site.
     */
    public void download() {
        plugin.getServer().notify();
        System.out.println(started);
        isUpdating = true;
        InputStream in = null;
        OutputStream out = null;
        try {
            URL url = new URL(Ablockalypse.getUpdateURL());
            out = new BufferedOutputStream(new FileOutputStream(Ablockalypse.getJARPath()));
            URLConnection connection = url.openConnection();
            in = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int counter = 0;
            int read;
            while ((read = in.read(buffer)) != -1) {
                counter += read;
                updatePercent = (int) (counter * 100 / buffer.length);
                out.write(buffer, 0, read);
            }
            System.out.println(finished);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isUpdating = false;
    }

    public boolean updateAvailable() {
        try {
            URL url = new URL(Ablockalypse.getUpdateURL());
            URLConnection connection = url.openConnection();
            File local = new File(Ablockalypse.getJARPath());
            long lastmodURL = connection.getLastModified();
            long lastmodFile = local.lastModified();
            return (lastmodURL > lastmodFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
