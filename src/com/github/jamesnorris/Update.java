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
    public boolean isUpdating = false;
    public int updatePercent = 0;
    private String finished = "[Ablockalypse] Update completed, please restart the server!";
    private Ablockalypse plugin;
    private String started = "[Ablockalypse] Update found, please wait while the new version is downloaded...";

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

    /* Downloads an update from the update site. */
    public void download(String address, String pathTo) {
        plugin.getServer().notify();
        System.out.println(started);
        isUpdating = true;
        InputStream in = null;
        OutputStream out = null;
        try {
            URL url = new URL(address);
            out = new BufferedOutputStream(new FileOutputStream(pathTo));
            URLConnection connection = url.openConnection();
            in = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int counter = 0;
            int read;
            while ((read = in.read(buffer)) != -1) {
                counter += read;
                updatePercent = counter * 100 / buffer.length;
                out.write(buffer, 0, read);
            }
            System.out.println(finished);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isUpdating = false;
    }

    public boolean updateAvailable(String address, String pathTo) {
        try {
            URL url = new URL(address);
            URLConnection connection = url.openConnection();
            File local = new File(pathTo);
            long lastmodURL = connection.getLastModified();
            long lastmodFile = local.lastModified();
            return lastmodURL > lastmodFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
