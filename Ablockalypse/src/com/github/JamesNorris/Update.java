package com.github.JamesNorris;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.plugin.Plugin;

import com.github.Ablockalypse;
import com.github.JamesNorris.Enumerated.MessageDirection;
import com.github.JamesNorris.Util.SpecificMessage;

public class Update {
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

    /**
     * Checks if an update is needed.
     * If an update is needed, updates.
     * 
     * @return Whether or not an update is started
     */
    public boolean check() {
        try {
            URL url = new URL(Ablockalypse.getUpdateURL());
            URLConnection connection = url.openConnection();
            File local = new File(Ablockalypse.getJARPath());
            long lastmodURL = connection.getLastModified();
            long lastmodFile = local.lastModified();
            if (lastmodURL > lastmodFile) {
                MessageTransfer.sendMessage(new SpecificMessage(MessageDirection.CONSOLE_OUTPUT, started));
                download();
                return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        plugin.getServer().notify();
        return false;
    }

    /*
     * Downloads an update from the update site.
     */
    private void download() {
        InputStream in = null;
        OutputStream out = null;
        try {
            URL url = new URL(Ablockalypse.getUpdateURL());
            out = new BufferedOutputStream(new FileOutputStream(Ablockalypse.getJARPath()));
            URLConnection connection = url.openConnection();
            in = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1)
                out.write(buffer, 0, read);
            MessageTransfer.sendMessage(new SpecificMessage(MessageDirection.CONSOLE_OUTPUT, finished));
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
