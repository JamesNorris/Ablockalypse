package com.github.JamesNorris;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.github.Ablockalypse;

public class Update {
	private Ablockalypse plugin;
	private PluginMaster pm;

	/**
	 * Creates a new instance of the Auto-Updater for Ablockalypse.
	 * This should only be used to download from dev.bukkit.org, otherwise it is against the policy of curse, and
	 * the plugin will not be accepted.
	 * 
	 * @param instance The instance of Ablockalypse to use for the Updater
	 */
	public Update(Ablockalypse instance) {
		plugin = instance;
		this.pm = Ablockalypse.getMaster();
	}

	/*
	 * Downloads the next version of Ablockalypse from dev.bukkit.org
	 */
	@SuppressWarnings("unused") private void download() {
		OutputStream out = null;
		URLConnection connection = null;
		InputStream in = null;
		try {
			URL url = new URL(pm.getUpdateURL());
			out = new BufferedOutputStream(new FileOutputStream(pm.getPath()));
			connection = url.openConnection();
			in = connection.getInputStream();
			byte[] buffer = new byte[1024];
			int numRead;
			long numWritten = 0;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
				numWritten += numRead;
			}
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
				System.out.println("[Ablockalypse] Update completed, please restart the server!");
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * Runs a check for updates, and if there is an update available, runs the download method to download the newest version from dev.bukkit.org.
	 * 
	 * @return Whether or not there is an update available.
	 */
	public boolean updateCheck() {
		URLConnection connection = null;
		try {
			URL url = new URL(pm.getUpdateURL());
			connection = url.openConnection();
			File localfile = new File(pm.getPath());
			long lastmodifiedurl = connection.getLastModified();
			long lastmodifiedfile = localfile.lastModified();
			if (lastmodifiedurl > lastmodifiedfile) {
				System.out.println("[Ablockalypse] Update found! Updating...");
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
}
