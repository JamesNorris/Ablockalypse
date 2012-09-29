package com.github.JamesNorris;

import java.io.File;

import com.github.Ablockalypse;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Threading.MainThreading;

/**
 * The class used to easily access most of the information about Ablockalypse.
 * This is the most powerful class in the plugin, and should not be nulled out, for it will break the entire plugin.
 */
public class PluginMaster {
	private String ablockalypse = "Ablockalypse";
	private String address = "http://api.bukget.org/api2/bukkit/plugin/" + ablockalypse + "/latest";
	private Data d;
	private Ablockalypse instance;
	private String issues = "https://github.com/JamesNorris/Ablockalypse/issues";
	private MainThreading mt;
	private String path = "plugins" + File.separator + "Ablockalypse.jar";

	/**
	 * Creates a new PluginMaster instance for Ablockalypse.
	 * 
	 * @param instance The Ablockalypse instance to associate with this instance
	 */
	public PluginMaster(Ablockalypse instance) {
		this.instance = instance;
	}

	/**
	 * Adds the 3 main types of data to this manager.
	 * 
	 * @param d The Data instance
	 * @param mt The MainThreading instance
	 */
	public void addData(Data d, MainThreading mt) {
		this.d = d;
		this.mt = mt;
	}

	/**
	 * Called when something that is in the breakable category breaks.
	 * 
	 * @param reason The reason for the exception
	 * @param disable Whether or not the Ablockalypse plugin should stop working
	 */
	public void crash(Ablockalypse instance, String reason, boolean disable) {
		/* Everything in this method should be static, except for strings */
		System.err.println("An aspect of Ablockalypse is broken, please report at:");
		System.err.println(getIssuesURL());
		System.err.println("--------------------------[ERROR REPORT]--------------------------");
		// System.err.println("VERSION: " + Data.version);//TODO see Data.java
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
	 * Gets the primary Data instance
	 * 
	 * @return The primary Data instance
	 */
	public Data getData() {
		return d;
	}

	/**
	 * Gets the Ablockalypse instance.
	 * 
	 * @return The Ablockalypse instance
	 */
	public Ablockalypse getInstance() {
		return instance;
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
	public String getPath() {
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
