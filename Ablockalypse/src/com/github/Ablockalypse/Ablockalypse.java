package com.github.Ablockalypse;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.Ablockalypse.JamesNorris.PluginMaster;
import com.github.Ablockalypse.JamesNorris.RegistrationManager;
import com.github.Ablockalypse.JamesNorris.Data.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Manager.TickManager;
import com.github.Ablockalypse.JamesNorris.Threading.MainThreading;
import com.github.Ablockalypse.JamesNorris.Util.External;
import com.github.Ablockalypse.JamesNorris.Util.Update;

public class Ablockalypse extends JavaPlugin {
	public static Ablockalypse instance;
	private static PluginMaster pm;
	private static TickManager tm;
	private static MainThreading mt;
	private static Data d;

	@Override public void onEnable() {
		pm = new PluginMaster(this);
		d = new Data(instance);
		External.runResources(instance);
		final ConfigurationData cd = External.getYamlManager().getConfigurationData();
		final Update upd = new Update(instance);
		System.out.println("[Ablockalypse] Checking for updates...");
		if (!cd.ENABLE_AUTO_UPDATE && upd.updateCheck()) {
			instance.getServer().getPluginManager().disablePlugin(instance);
			System.out.println("[Ablockalypse] An update has occurred, please restart the server to enable it!");
		} else {
			RegistrationManager.register(instance);
			tm = new TickManager(instance);
			External.loadBinaries();
			mt = new MainThreading(instance, true, true);
			pm.addData(d, mt, tm);
		}
	}

	@Override public void onDisable() {
		External.saveBinaries();
	}

	/**
	 * Kills the plugin.
	 */
	public static void kill() {
		instance.setEnabled(false);
	}

	/**
	 * Gets the PluginMaster instance for Ablockalypse. The PluginMaster instance is what manages the entire plugin.
	 * 
	 * @return The PluginMaster instance for Ablockalypse
	 */
	public static PluginMaster getMaster() {
		return pm;
	}
}
