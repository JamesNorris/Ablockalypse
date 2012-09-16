package com.github.Ablockalypse;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.Ablockalypse.JamesNorris.Data;
import com.github.Ablockalypse.JamesNorris.EventManager;
import com.github.Ablockalypse.JamesNorris.Threading.MainThreading;
import com.github.Ablockalypse.JamesNorris.Util.External;
import com.github.Ablockalypse.JamesNorris.Util.Update;

public class Ablockalypse extends JavaPlugin {
	public static Ablockalypse instance;
	public String ablockalypse = "Ablockalypse";
	public String address = "http://api.bukget.org/api2/bukkit/plugin/" + ablockalypse + "/latest";
	public String path = "plugins" + File.separator + "Ablockalypse.jar";

	@Override public void onDisable() {
		External.saveBinaries();
	}

	/* JAVAPLUGIN METHODS */
	@Override public void onEnable() {
		/* UPDATE RUN */
		Update upd = new Update(this);
		System.out.println("[Ablockalypse] Checking for updates...");
		if (upd.updateCheck()) {
			getServer().getPluginManager().disablePlugin(this);
			System.out.println("[Ablockalypse] An update has occurred, please restart the server to enable it!");
		} else {
			/* REGULAR RUN */
			new Data(this);
			External.runConfig(this);
			EventManager.registerEvents(this);
			External.loadBinaries();
			MainThreading t = new MainThreading(this);
			t.wolfFlames();
			t.barrier();
		}
	}
}
