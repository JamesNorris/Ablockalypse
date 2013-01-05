package com.github.JamesNorris;

import java.io.File;

import org.bukkit.plugin.Plugin;

import com.github.Ablockalypse;
import com.github.JamesNorris.Threading.MainThreading;

/**
 * The class used to easily access most of the information about Ablockalypse.
 * This is the most powerful class in the plugin, and should not be nulled out, for it will break the entire plugin.
 */
public class PluginMaster extends DataManipulator {
	private String ablockalypse = "Ablockalypse";
	private String address = "http://api.bukget.org/api2/bukkit/plugin/" + ablockalypse + "/latest";
	private String issues = "https://github.com/JamesNorris/Ablockalypse/issues";
	private MainThreading mt;
	private String path = "plugins" + File.separator + "Ablockalypse.jar";

	/**
	 * Creates a new PluginMaster instance for Ablockalypse.
	 */
	public PluginMaster() {}

	/**
	 * Called when something that is in the breakable category breaks.
	 * 
	 * @param reason The reason for the exception
	 * @param disable Whether or not the Ablockalypse plugin should stop working
	 */
	public void crash(Plugin instance, String reason, boolean disable) {
		/* Everything in this method should be static, except for strings */
		System.err.println("An aspect of Ablockalypse is broken, please report at:");
		System.err.println(getIssuesURL());
		System.err.println("--------------------------[ERROR REPORT]--------------------------");
		System.err.println("VERSION: " + data.version);
		System.err.println("BREAK REASON: " + reason);
		System.err.println("---------------------------[END REPORT]---------------------------");
		if (!disable)
			System.err.println("The plugin will now continue working...");
		else {
			System.err.println("FATAL ERROR, the plugin will now shut down!");
			Ablockalypse.kill();
		}
	}

	/**
	 * Gets the Ablockalypse instance.
	 * 
	 * @return The Ablockalypse instance
	 */
	public Ablockalypse getInstance() {
		return Ablockalypse.instance;
	}

	/**
	 * Gets the URL for issues to be sent to github.
	 * 
	 * @return The github issues URL
	 */
	public String getIssuesURL() {
		return issues;
	}

	/**
	 * Gets the primary MainThreading instance
	 * 
	 * @return The primary MainThreading instance
	 */
	public MainThreading getMainThreading() {
		return mt;
	}

	/**
	 * Gets the path from the plugins folder to the data folder.
	 * 
	 * @return The data folder path
	 */
	public String getJARPath() {
		return path;
	}

	/**
	 * Gets the URL from bukget for updating.
	 * 
	 * @return The bukget URL
	 */
	public String getUpdateURL() {
		return address;
	}
}
