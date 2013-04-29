package com.github;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ikeirnez.command.BaseCommand;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.External;
import com.github.jamesnorris.Update;
import com.github.jamesnorris.XMPP;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.event.bukkit.BlockBreak;
import com.github.jamesnorris.event.bukkit.BlockPlace;
import com.github.jamesnorris.event.bukkit.EntityBreakDoor;
import com.github.jamesnorris.event.bukkit.EntityDamage;
import com.github.jamesnorris.event.bukkit.EntityDamageByEntity;
import com.github.jamesnorris.event.bukkit.EntityDeath;
import com.github.jamesnorris.event.bukkit.EntityExplode;
import com.github.jamesnorris.event.bukkit.EntityTarget;
import com.github.jamesnorris.event.bukkit.PlayerDeath;
import com.github.jamesnorris.event.bukkit.PlayerDropItem;
import com.github.jamesnorris.event.bukkit.PlayerInteract;
import com.github.jamesnorris.event.bukkit.PlayerInteractEntity;
import com.github.jamesnorris.event.bukkit.PlayerJoin;
import com.github.jamesnorris.event.bukkit.PlayerKick;
import com.github.jamesnorris.event.bukkit.PlayerMove;
import com.github.jamesnorris.event.bukkit.PlayerPickupItem;
import com.github.jamesnorris.event.bukkit.PlayerQuit;
import com.github.jamesnorris.event.bukkit.PlayerRespawn;
import com.github.jamesnorris.event.bukkit.PlayerTeleport;
import com.github.jamesnorris.event.bukkit.PlayerToggleSneak;
import com.github.jamesnorris.event.bukkit.ProjectileHit;
import com.github.jamesnorris.event.bukkit.SignChange;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.inter.ZAThread;
import com.github.jamesnorris.threading.BarrierBreakTriggerThread;
import com.github.jamesnorris.threading.HellhoundMaintenanceThread;
import com.github.jamesnorris.threading.MainThread;
import com.github.jamesnorris.threading.MobClearingThread;
import com.github.zathrus_writer.commandsex.api.XMPPAPI;

public class Ablockalypse extends JavaPlugin {
    public static Ablockalypse instance;
    private static DataContainer data = new DataContainer();
    private static MainThread mt;
    private static Update upd;

    /**
     * Called when something is not working, and the plugin needs to be monitored.
     * 
     * @param reason The reason for the exception
     * @param disable Whether or not the Ablockalypse plugin should stop working
     */
    public static void crash(String reason, boolean disable) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        /* Everything in this method should be static, except for strings */
        StringBuilder sb = new StringBuilder();
        sb.append("An aspect of Ablockalypse is broken, please report at: \n");
        sb.append("https://github.com/JamesNorris/Ablockalypse/issues\n");
        sb.append("--------------------------[ERROR REPORT]--------------------------\n");
        sb.append("VERSION: " + instance.getDescription().getVersion() + "\n");
        sb.append("BREAK REASON: " + reason + "\n");
        for (int i = 1; i <= 5; i++) {
            sb.append("CRASH CALL " + i + ": " + stackTraceElements[i] + "\n");
        }
        sb.append("---------------------------[END REPORT]---------------------------\n");
        if (!disable) {
            sb.append("The error was NOT FATAL, therefore Ablockalypse will continue running.");
        } else {
            sb.append("The error was FATAL! Ablockalypse will now shut down.");
            Ablockalypse.kill();
        }
        System.err.println(sb.toString());
        Plugin comex = Bukkit.getPluginManager().getPlugin("CommandsEX");
        if (comex != null && comex.isEnabled()) {
            XMPPAPI.sendMessage("An error occurred!\n" + sb.toString());
        }
    }

    /**
     * Gets a single DataManipulator instance to prevent duplication.
     * 
     * @return The DataManipulator used by Ablockalypse
     */
    public static DataContainer getData() {
        return data;
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
     * Gets the main thread of this plugin, which ticks every 20th of a second, and is in sync with the MC ticks.
     * 
     * @return The MainThread run by Ablockalypse
     */
    public static MainThread getMainThread() {
        return mt;
    }

    /**
     * Gets the Update instance of Ablockalypse, which auto-updates ths plugin.
     * 
     * @return The Update instance to Ablockalypse
     */
    public static Update getUpdater() {
        return upd;
    }

    /**
     * Kills the plugin.
     */
    public static void kill() {
        Ablockalypse.instance.setEnabled(false);
    }

    @Override public void onDisable() {
        External.saveData();
        for (ZAThread thread : data.threads) {
            thread.remove();
        }
        for (String name : data.games.keySet()) {
            Game zag = data.games.get(name);
            if (zag != null) {
                zag.remove();
            }
        }
    }

    @Override public void onEnable() {
        Ablockalypse.instance = this;
        External.loadExternalFiles(this);
        upd = new Update(this);
        System.out.println("[Ablockalypse] Checking for updates...");
        String address = "http://api.bukget.org/3/plugins/bukkit/zombie-ablockalypse/" + (String) Setting.UPDATE_VERSION.getSetting() + "/download";
        String pathTo = "plugins" + File.separator + "Ablockalypse.jar";
        if ((Boolean) Setting.ENABLE_AUTO_UPDATE.getSetting() && upd.updateAvailable(address, pathTo)) {
            upd.download(address, pathTo);
            System.out.println("[Ablockalypse] An update has occurred, please restart the server to enable it!");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            System.out.println("[Ablockalypse] No updates found.");
            register();
            External.loadData();
            mt = new MainThread(true);
            new BarrierBreakTriggerThread(true, 20);
            new MobClearingThread((Boolean) Setting.CLEAR_MOBS.getSetting(), 360);
            new HellhoundMaintenanceThread(true, 20);
        }
    }

    protected void register() {
        PluginManager pm = getServer().getPluginManager();
        /* EVENTS */
        //@formatter:off
        Listener[] registrations = new Listener[] {
                new EntityDamage(),
                new PlayerDeath(),
                new PlayerInteract(),
                new PlayerInteractEntity(),
                new SignChange(),
                new EntityDeath(),
                new PlayerPickupItem(),
                new ProjectileHit(),
                new PlayerMove(),
                new PlayerToggleSneak(),
                new EntityBreakDoor(),
                new PlayerTeleport(),
                new PlayerQuit(),
                new EntityTarget(),
                new PlayerRespawn(),
                new EntityExplode(),
                new EntityDamageByEntity(),
                new PlayerJoin(),
                new BlockBreak(),
                new PlayerDropItem(),
                new PlayerKick(),
                new BlockPlace(),
                new XMPP(/*non-bukkit event*/)
        };
        //@formatter:on
        for (Listener listener : registrations) {
            pm.registerEvents(listener, instance);
        }
        /* COMMANDS */
        instance.getCommand("za").setExecutor(new BaseCommand());
    }
}
