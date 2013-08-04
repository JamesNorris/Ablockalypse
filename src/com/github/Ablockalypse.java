package com.github;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.aspect.Game;
import com.github.aspect.PermanentAspect;
import com.github.command.BaseCommand;
import com.github.enumerated.Setting;
import com.github.event.bukkit.BlockBreak;
import com.github.event.bukkit.BlockPlace;
import com.github.event.bukkit.EntityBreakDoor;
import com.github.event.bukkit.EntityDamage;
import com.github.event.bukkit.EntityDamageByEntity;
import com.github.event.bukkit.EntityDeath;
import com.github.event.bukkit.EntityExplode;
import com.github.event.bukkit.EntityTarget;
import com.github.event.bukkit.PlayerDeath;
import com.github.event.bukkit.PlayerDropItem;
import com.github.event.bukkit.PlayerInteract;
import com.github.event.bukkit.PlayerInteractEntity;
import com.github.event.bukkit.PlayerJoin;
import com.github.event.bukkit.PlayerKick;
import com.github.event.bukkit.PlayerMove;
import com.github.event.bukkit.PlayerPickupItem;
import com.github.event.bukkit.PlayerQuit;
import com.github.event.bukkit.PlayerRespawn;
import com.github.event.bukkit.PlayerTeleport;
import com.github.event.bukkit.PlayerToggleSneak;
import com.github.event.bukkit.ProjectileHit;
import com.github.event.bukkit.SignChange;
import com.github.threading.MainThread;
import com.github.threading.inherent.BarrierBreakTriggerThread;
import com.github.threading.inherent.HellhoundMaintenanceThread;
import com.github.threading.inherent.MobClearingThread;
import com.github.utility.serial.SavedVersion;
import com.github.zathrus_writer.commandsex.api.XMPPAPI;

public class Ablockalypse extends JavaPlugin {
    private static Ablockalypse instance;
    private static DataContainer data = new DataContainer();
    private static MainThread mainThread;
    private static External external;
    private static BaseCommand commandInstance;
    private static int totalFatality = 0, maxFatality = 100;

    /**
     * Called when something is not working, and the plugin needs to be monitored.
     * 
     * @param reason The reason for the exception
     * @param disable Whether or not the Ablockalypse plugin should stop working
     */
    public static void crash(String reason, int fatality) {
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
        totalFatality += fatality;
        sb.append("FATALITY: " + fatality);
        sb.append("\nTOTAL FATALITY: " + totalFatality + "/" + maxFatality);
        if (totalFatality >= maxFatality) {
            Ablockalypse.kill();
            sb.append("\nA TOTAL FATALITY LEVEL OF " + totalFatality + " HAS KILLED ABLOCKALYPSE FOR SAFETY");
        }
        sb.append("\n---------------------------[END REPORT]---------------------------\n");
        System.err.println(sb.toString());
        Plugin comex = Bukkit.getPluginManager().getPlugin("CommandsEX");
        if (comex != null && comex.isEnabled()) {
            XMPPAPI.sendMessage("An error occurred!\n" + sb.toString());
        }
    }

    public static BaseCommand getBaseCommandInstance() {
        return commandInstance;
    }

    /**
     * Gets a single DataManipulator instance to prevent duplication.
     * 
     * @return The DataManipulator used by Ablockalypse
     */
    public static DataContainer getData() {
        return data;
    }

    public static External getExternal() {
        return external;
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
        return mainThread;
    }

    /**
     * Kills the plugin.
     */
    public static void kill() {
        Ablockalypse.instance.setEnabled(false);
    }

    @Override public void onDisable() {
        for (Game game : data.getObjectsOfType(Game.class)) {
            PermanentAspect.save(game, external.getSavedDataFile(game.getName(), true));
            game.remove();
        }
        mainThread.cancel();
    }

    @Override public void onEnable() {
        Ablockalypse.instance = this;
        external = new External(this);
        try {
            update();
        } catch (Exception ex) {
            Ablockalypse.crash("The update either failed to find the version provided in config.yml, or there is an internal issue with the system.", 5);
        }
        commandInstance = new BaseCommand();
        register();
        for (File file : external.getSavedDataFolder().listFiles()) {
            if (file != null) {
                try {
                    SavedVersion save = (SavedVersion) External.load(file);
                    PermanentAspect.load(Game.class, save);
                } catch (EOFException e) {
                    // nothing, the file is empty and no data was saved
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        mainThread = new MainThread(true);
        new BarrierBreakTriggerThread(true, 20);
        new MobClearingThread((Boolean) Setting.CLEAR_MOBS.getSetting(), 360);
        new HellhoundMaintenanceThread(true, 20);
        maxFatality = (Integer) Setting.MAX_FATALITY.getSetting();
    }

    public boolean update() throws FileNotFoundException, IOException {
        String address = "http://api.bukget.org/3/plugins/bukkit/zombie-ablockalypse/" + (String) Setting.UPDATE_VERSION.getSetting() + "/download";
        String pathTo = "plugins" + File.separator + "Ablockalypse.jar";
        URL url = new URL(address);
        File file = new File(pathTo);
        boolean updateAvailable = External.newVersionAvailable(url, file);
        if ((Boolean) Setting.ENABLE_AUTO_UPDATE.getSetting() && updateAvailable) {
            System.out.println("[Ablockalypse] Checking for updates...");
            External.download(url.openConnection().getInputStream(), new FileOutputStream(file));
            System.out.println("[Ablockalypse] An update has occurred, please restart the server to enable it!");
            getServer().getPluginManager().disablePlugin(this);
            return true;
        }
        System.out.println("[Ablockalypse] No updates found.");
        return false;
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
                new BlockPlace()
        };
        //@formatter:on
        for (Listener listener : registrations) {
            pm.registerEvents(listener, instance);
        }
        /* COMMANDS */
        instance.getCommand("za").setExecutor(commandInstance);
    }
}
