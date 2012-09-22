package com.github.Ablockalypse;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.Ablockalypse.JamesNorris.RegistrationManager;
import com.github.Ablockalypse.JamesNorris.Data.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Threading.MainThreading;
import com.github.Ablockalypse.JamesNorris.Util.External;
import com.github.Ablockalypse.JamesNorris.Util.Update;

public class Ablockalypse extends JavaPlugin {
	public static Ablockalypse instance;
	public String ablockalypse = "Ablockalypse";
	public String address = "http://api.bukget.org/api2/bukkit/plugin/" + ablockalypse + "/latest";
	public String path = "plugins" + File.separator + "Ablockalypse.jar";
	public static String issues = "https://github.com/JamesNorris/Ablockalypse/issues";

	@Override public void onDisable() {
		External.saveBinaries();
	}

	/* JAVAPLUGIN METHODS */
	@Override public void onEnable() {
		new Data(this);
		External.runResources(this);
		final ConfigurationData cd = External.getYamlManager().getConfigurationData();
		/* AUTO UPDATER */
		final Update upd = new Update(this);
		System.out.println("[Ablockalypse] Checking for updates...");
		if (!cd.ENABLE_AUTO_UPDATE && upd.updateCheck()) {
			getServer().getPluginManager().disablePlugin(this);
			System.out.println("[Ablockalypse] An update has occurred, please restart the server to enable it!");
		} else {
			/* STARTUP */
			RegistrationManager.register(this);
			External.loadBinaries();
			new MainThreading(this, true, true);
		}
	}

	/**
	 * Called when something that is in the breakable category breaks.
	 * 
	 * @param reason The reason for the exception
	 * @param disable Whether or not the Ablockalypse plugin should stop working
	 */
	public static void crash(final String reason, final boolean disable) {
		System.err.println("An aspect of Ablockalypse is broken, please report at:");
		System.err.println(Ablockalypse.issues);
		System.err.println("--------------------------[ERROR REPORT]--------------------------");
		System.err.println("VERSION: " + Data.version);
		System.err.println("BREAK REASON: " + reason);
		System.err.println("---------------------------[END REPORT]---------------------------");
		if (!disable)
			System.err.println("The plugin will now continue working...");
		else {
			System.err.println("FATAL ERROR, the plugin will now shut down!");
			Ablockalypse.instance.setEnabled(false);
		}
	}
}
