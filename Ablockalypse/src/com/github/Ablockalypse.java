package com.github;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.External;
import com.github.JamesNorris.Update;
import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Enumerated.Setting;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Manager.RegistrationManager;
import com.github.JamesNorris.Threading.BlinkerThread;
import com.github.JamesNorris.Threading.MainThreading;

public class Ablockalypse extends JavaPlugin {
	private static String address = "http://api.bukget.org/api2/bukkit/plugin/Ablockalypse/latest";
	protected static DataManipulator dm;
	public static Ablockalypse instance;
	private static String issues = "https://github.com/JamesNorris/Ablockalypse/issues";
	private static MainThreading mt;
	private static String path = "plugins" + File.separator + "Ablockalypse.jar";
	private static Update upd;
	/**
	 * Called when something that is in the breakable category breaks.
	 * 
	 * @param reason The reason for the exception
	 * @param disable Whether or not the Ablockalypse plugin should stop working
	 */
	public static void crash(String reason, boolean disable) {
		/* Everything in this method should be static, except for strings */
		System.err.println("An aspect of Ablockalypse is broken, please report at:");
		System.err.println(getIssuesURL());
		System.err.println("--------------------------[ERROR REPORT]--------------------------");
		Ablockalypse.getData();
		System.err.println("VERSION: " + DataManipulator.data.version);
		System.err.println("BREAK REASON: " + reason);
		System.err.println("---------------------------[END REPORT]---------------------------");
		if (!disable)
			System.err.println("The plugin will now continue working...");
		else {
			System.err.println("FATAL ERROR, the plugin will now shut down!");
			Ablockalypse.kill();
		}
	}

	public static DataManipulator getData() {
		return dm;
	}

	/**
	 * Gets the Ablockalypse instance.
	 * 
	 * @return The Ablockalypse instance
	 */
	public static Ablockalypse getInstance() {
		return Ablockalypse.instance;
	}

	/**
	 * Gets the URL for issues to be sent to github.
	 * 
	 * @return The github issues URL
	 */
	public static String getIssuesURL() {
		return issues;
	}

	/**
	 * Gets the path from the plugins folder to the data folder.
	 * 
	 * @return The data folder path
	 */
	public static String getJARPath() {
		return path;
	}

	/**
	 * Gets the primary MainThreading instance
	 * 
	 * @return The primary MainThreading instance
	 */
	public static MainThreading getMainThreading() {
		return mt;
	}

	public static Update getUpdater() {
		return upd;
	}

	/**
	 * Gets the URL from bukget for updating.
	 * 
	 * @return The bukget URL
	 */
	public static String getUpdateURL() {
		return address;
	}

	/**
	 * Kills the plugin.
	 */
	public static void kill() {
		Ablockalypse.instance.setEnabled(false);
	}

	public GlobalData data;

	@Override public void onDisable() {
		External.saveData();
		for (BlinkerThread bt : data.blinkers)
			bt.cancel();
		if (data.games != null)
			for (ZAGameBase gb : data.games.values())
				gb.remove();
		data = null;
		instance = null;
		this.getClassLoader().clearAssertionStatus();// Added to prevent lingering static objects.
	}

	@Override public void onEnable() {
		Ablockalypse.instance = this;
		External.loadExternalFiles(this);
		upd = new Update(this);
		data = new GlobalData(this);
		dm = new DataManipulator();
		System.out.println("[Ablockalypse] Checking for updates...");
		if (!(Boolean) Setting.ENABLEAUTOUPDATE.getSetting() && upd.check()) {
			this.getServer().getPluginManager().disablePlugin(this);
			System.out.println("[Ablockalypse] An update has occurred, please restart the server to enable it!");
		} else {
			System.out.println("[Ablockalypse] No updates found.");
			RegistrationManager.register(this);
			External.loadData();
			new MainThreading(this, true, true, true);
		}
	}
}
