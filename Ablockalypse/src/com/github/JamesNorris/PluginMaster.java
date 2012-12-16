package com.github.JamesNorris;

import java.io.File;
import java.util.HashMap;

import org.bukkit.plugin.Plugin;

import com.github.Ablockalypse;
import com.github.JamesNorris.Manager.YamlManager;
import com.github.JamesNorris.Threading.MainThreading;

/**
 * The class used to easily access most of the information about Ablockalypse.
 * This is the most powerful class in the plugin, and should not be nulled out, for it will break the entire plugin.
 */
public class PluginMaster extends DataManipulator {
	private String ablockalypse = "Ablockalypse";
	private String address = "http://api.bukget.org/api2/bukkit/plugin/" + ablockalypse + "/" + getDynamicVersion();
	private String issues = "https://github.com/JamesNorris/Ablockalypse/issues";
	private MainThreading mt;
	private String path = "plugins" + File.separator + "Ablockalypse.jar";
	private HashMap<String, String> versions = new HashMap<String, String>();

	/**
	 * Creates a new PluginMaster instance for Ablockalypse.
	 * 
	 * @param instance The Ablockalypse instance to associate with this instance
	 */
	public PluginMaster() {
		setupVersions();
	}

	private void setupVersions() {
		versions = new HashMap<String, String>();
		// updating to the correct version for OBC/NMS compatibility.
		versions.put("", "v1.1.7");// non-versioned packages
		versions.put("v1_4_5", "v1.2.0");// versioned packages
	}

	private String getDynamicVersion() {
		String mcVer = pl.getNMSPackageVersion();
		if (versions == null)
			setupVersions();
		if (versions.containsKey(mcVer))
			return versions.get(mcVer);
		return "latest";
	}

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

	/**
	 * Gets the YamlManager of the plugin.
	 * 
	 * @return The main YamlManager of the plugin
	 */
	public YamlManager getYamlManager() {
		return ym;
	}
}
