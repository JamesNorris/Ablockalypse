package com.github;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.External;
import com.github.JamesNorris.PackageLoader;
import com.github.JamesNorris.PluginMaster;
import com.github.JamesNorris.Update;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Manager.RegistrationManager;
import com.github.JamesNorris.Threading.BlinkerThread;
import com.github.JamesNorris.Threading.MainThreading;

public class Ablockalypse extends JavaPlugin {
	public GlobalData data;
	public static Ablockalypse instance;
	private static PluginMaster pm;
	protected static PackageLoader pl;
	protected static DataManipulator dm;

	/**
	 * Gets the PluginMaster instance for Ablockalypse. The PluginMaster instance is what manages the entire plugin.
	 * 
	 * @return The PluginMaster instance for Ablockalypse
	 */
	public static PluginMaster getMaster() {
		return pm;
	}

	public static PackageLoader getPackageLoader() {
		return pl;
	}

	public static DataManipulator getData() {
		return dm;
	}

	/**
	 * Kills the plugin.
	 */
	public static void kill() {
		Ablockalypse.instance.setEnabled(false);
	}

	@Override public void onDisable() {
		External.saveData();
		for (BlinkerThread bt : data.blinkers)
			bt.cancel();
		if (data.games != null)
			for (ZAGameBase gb : data.games.values())
				gb.remove();
		data = null;
		pm = null;
		instance = null;
		this.getClassLoader().clearAssertionStatus();
	}

	@Override public void onEnable() {
		pl = new PackageLoader();
		Ablockalypse.instance = this;
		External.loadExternalFiles(this);
		pm = new PluginMaster();
		Update upd = new Update(this);
		data = new GlobalData(this);
		dm = new DataManipulator();
		ConfigurationData cd = External.getYamlManager().getConfigurationData();
		System.out.println("[Ablockalypse] Checking for updates...");
		if (!cd.ENABLE_AUTO_UPDATE && upd.check()) {
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
