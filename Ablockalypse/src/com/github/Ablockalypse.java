package com.github;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.JamesNorris.External;
import com.github.JamesNorris.PluginMaster;
import com.github.JamesNorris.Update;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Implementation.GameArea;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Manager.RegistrationManager;
import com.github.JamesNorris.Threading.MainThreading;

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
		External.saveData();
		if (Data.areas != null)
			for (GameArea a : Data.areas)
				a.close();
		if (Data.gamebarriers != null)
			for (GameBarrier b : Data.gamebarriers) {
				b.replacePanels();
				b.getBlinkerThread().cancel();
			}
		if (Data.games != null)
			for (ZAGameBase gb : Data.games.values())
				gb.pause(true);
	}

	@Override public void onEnable() {
		Ablockalypse.instance = this;
		External.loadExternalFiles(this);
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
			External.loadData();
			mt = new MainThreading(this, true, true, true);
			pm.addData(d, mt);
			pm.addManager(External.ym);
		}
	}
}
