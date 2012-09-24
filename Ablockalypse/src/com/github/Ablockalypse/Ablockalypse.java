package com.github.Ablockalypse;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.Ablockalypse.JamesNorris.PluginMaster;
import com.github.Ablockalypse.JamesNorris.RegistrationManager;
import com.github.Ablockalypse.JamesNorris.Data.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Threading.MainThreading;
import com.github.Ablockalypse.JamesNorris.Util.External;
import com.github.Ablockalypse.JamesNorris.Util.Update;

public class Ablockalypse extends JavaPlugin {
	private static Data d;
	public static Ablockalypse instance;
	private static MainThreading mt;
	private static PluginMaster pm;

	/**
	 * Gets the PluginMaster instance for Ablockalypse. The PluginMaster instance is what manages the entire plugin.
	 * 
	 * @return The PluginMaster instance for Ablockalypse
	 */
	public static PluginMaster getMaster() {
		return pm;
	}

	/**
	 * Kills the plugin.
	 */
	public static void kill() {
		Ablockalypse.instance.setEnabled(false);
	}

	@Override public void onDisable() {
		External.saveBinaries();
	}

	@Override public void onEnable() {
		Ablockalypse.instance = this;
		External.runResources(this);
		pm = new PluginMaster(this);
		Update upd = new Update(this);
		d = new Data(this);
		ConfigurationData cd = External.getYamlManager().getConfigurationData();
		System.out.println("[Ablockalypse] Checking for updates...");
		if (!cd.ENABLE_AUTO_UPDATE && upd.updateCheck()) {
			this.getServer().getPluginManager().disablePlugin(this);
			System.out.println("[Ablockalypse] An update has occurred, please restart the server to enable it!");
		} else {
			RegistrationManager.register(this);
			External.loadBinaries();
			mt = new MainThreading(this, true, true);
			pm.addData(d, mt);
		}
	}
}
