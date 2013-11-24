package com.github.jamesnorris.ablockalypse;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jamesnorris.ablockalypse.Updater.UpdateType;
import com.github.jamesnorris.ablockalypse.aspect.PermanentAspect;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.PlayerState;
import com.github.jamesnorris.ablockalypse.command.BaseCommand;
import com.github.jamesnorris.ablockalypse.enumerated.Setting;
import com.github.jamesnorris.ablockalypse.event.bukkit.BlockBreak;
import com.github.jamesnorris.ablockalypse.event.bukkit.BlockPlace;
import com.github.jamesnorris.ablockalypse.event.bukkit.BlockRedstone;
import com.github.jamesnorris.ablockalypse.event.bukkit.EntityBreakDoor;
import com.github.jamesnorris.ablockalypse.event.bukkit.EntityDamage;
import com.github.jamesnorris.ablockalypse.event.bukkit.EntityDamageByEntity;
import com.github.jamesnorris.ablockalypse.event.bukkit.EntityDeath;
import com.github.jamesnorris.ablockalypse.event.bukkit.EntityExplode;
import com.github.jamesnorris.ablockalypse.event.bukkit.EntityTarget;
import com.github.jamesnorris.ablockalypse.event.bukkit.PlayerDeath;
import com.github.jamesnorris.ablockalypse.event.bukkit.PlayerDropItem;
import com.github.jamesnorris.ablockalypse.event.bukkit.PlayerInteract;
import com.github.jamesnorris.ablockalypse.event.bukkit.PlayerJoin;
import com.github.jamesnorris.ablockalypse.event.bukkit.PlayerKick;
import com.github.jamesnorris.ablockalypse.event.bukkit.PlayerMove;
import com.github.jamesnorris.ablockalypse.event.bukkit.PlayerPickupItem;
import com.github.jamesnorris.ablockalypse.event.bukkit.PlayerQuit;
import com.github.jamesnorris.ablockalypse.event.bukkit.PlayerRespawn;
import com.github.jamesnorris.ablockalypse.event.bukkit.PlayerTeleport;
import com.github.jamesnorris.ablockalypse.event.bukkit.PlayerToggleSneak;
import com.github.jamesnorris.ablockalypse.event.bukkit.ProjectileHit;
import com.github.jamesnorris.ablockalypse.event.bukkit.SignChange;
import com.github.jamesnorris.ablockalypse.event.bukkit.VehicleExit;
import com.github.jamesnorris.ablockalypse.threading.MainThread;
import com.github.jamesnorris.ablockalypse.threading.inherent.ServerBarrierActionTask;
import com.github.jamesnorris.ablockalypse.threading.inherent.ServerHellhoundActionTask;
import com.github.jamesnorris.ablockalypse.threading.inherent.ServerMobClearingTask;
import com.github.jamesnorris.ablockalypse.threading.inherent.ServerTeleporterActionTask;
import com.github.jamesnorris.ablockalypse.utility.serial.SavedVersion;

/* MAIN TODO 's
 * 
 * 1) Fix MapData problems -Error messages added
 * 2) Replace the Gravity Updater.java with my own version
 * 3) A spectator ability (invisible and uncollidable players) -WIP, FUTURE
 * 4) Firesale opening all mystery chests
 * 5) Improved mob movement
 * 6) Slowly crawling last stand -TEST
 * 7) Update to MCShot 
 * 8) Custom ammo and fix durability settings for max ammo */
public class Ablockalypse extends JavaPlugin {
    private static Ablockalypse instance;
    private static DataContainer data = new DataContainer();
    private static MainThread mainThread;
    private static External external;
    private static BaseCommand commandInstance;
    private static ErrorTracker errorTracker;

    public static BaseCommand getBaseCommandInstance() {
        return commandInstance;
    }

    public static DataContainer getData() {
        return data;
    }

    public static ErrorTracker getErrorTracker() {
        return errorTracker;
    }

    public static External getExternal() {
        return external;
    }

    public static Ablockalypse getInstance() {
        return Ablockalypse.instance;
    }

    public static MainThread getMainThread() {
        return mainThread;
    }

    // Kills the plugin.
    protected static void kill() {
        Ablockalypse.instance.setEnabled(false);
    }

    @Override public void onDisable() {
        for (Game game : data.getObjectsOfType(Game.class)) {
            game.organizeObjects();
            PermanentAspect.save(game, external.getSavedDataFile(game.getName(), true));
            if ((Boolean) Setting.PRINT_STORAGE_DATA_TO_FILE.getSetting()) {
                try {
                    PermanentAspect.printData(game);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            game.remove(false);
        }
        for (PlayerState state : PlayerJoin.toLoad) {
            PlayerQuit.playerSaves.put(state.getPlayer().getPlayer(), state);
        }
        for (Player player : PlayerQuit.playerSaves.keySet()) {
            PlayerState state = PlayerQuit.playerSaves.get(player);
            PermanentAspect.save(state, external.getSavedPlayerDataFile(player, true));
        }
        mainThread.cancel();
    }

    @Override public void onEnable() {
        Ablockalypse.instance = this;
        external = new External(this);
        errorTracker = new ErrorTracker("Ablockalypse", getDescription().getVersion(), "https://github.com/JamesNorris/Ablockalypse/issues", (Integer) Setting.MAX_FATALITY.getSetting());
        if ((Boolean) Setting.ENABLE_UPDATE_CHECK.getSetting()) {
            try {
                update();
            } catch (Exception ex) {
                errorTracker.crash("The update either failed to find the version provided in config.yml, or there is an internal issue with the system.", 5);
            }
        }
        commandInstance = new BaseCommand();
        register();
        initServerThreads(false);
        for (File file : external.getSavedDataFolder().listFiles()) {
            if (file != null && !file.isDirectory()) {
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
        for (File file : external.getSavedPlayerDataFolder().listFiles()) {
            if (file != null && !file.isDirectory()) {
                try {
                    SavedVersion save = (SavedVersion) External.load(file);
                    PlayerJoin.toLoad.add((PlayerState) PermanentAspect.load(PlayerState.class, save));
                    file.delete();
                } catch (EOFException e) {
                    // nothing, the file is empty and no data was saved
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        mainThread.run();// starts the main thread, which was told not to autorun earlier. Done to prevent NPEs with loading saves, but avoid NPEs with threads.
    }

    public boolean update() throws FileNotFoundException, IOException {
        Updater updater = new Updater(this, 42258, getFile(), (Boolean) Setting.ENABLE_AUTO_UPDATE.getSetting() ? UpdateType.DEFAULT : UpdateType.NO_DOWNLOAD, false);
        switch (updater.getResult()) {
            case DISABLED:
                return false;
            case FAIL_APIKEY:
            case FAIL_BADID:
            case FAIL_DBO:
            case FAIL_DOWNLOAD:
            case FAIL_NOVERSION:
                System.err.println("[Ablockalyse] Failed to update! " + updater.getResult().toString());
                return false;
            case NO_UPDATE:
                System.out.println("[Ablockalypse] No updates found.");
                return false;
            case SUCCESS:
                System.out.println("[Ablockalypse] Update success!");
                return true;
            case UPDATE_AVAILABLE:
                System.out.println("[Ablockalypse] Update found, but not completed. If you want to update, please switch 'ENABLE_AUTO_UPDATE' in the configuration to 'true'.");
                return false;
            default:
                return false;
        }
    }

    protected void initServerThreads(boolean startMain) {
        mainThread = new MainThread(startMain);
        new ServerBarrierActionTask(true);
        new ServerMobClearingTask(360, (Boolean) Setting.CLEAR_MOBS.getSetting());
        new ServerHellhoundActionTask(20, true);
        new ServerTeleporterActionTask(20, true);
    }

    protected void register() {
        PluginManager pm = getServer().getPluginManager();
        /* EVENTS */
        //@formatter:off
        Listener[] registrations = new Listener[] {
                new EntityDamage(),
                new PlayerDeath(),
                new PlayerInteract(),
                new SignChange(),
                new EntityDeath(),
                new PlayerPickupItem(),
                new ProjectileHit(),
                new PlayerMove(),
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
                new VehicleExit(),
                new BlockRedstone(),
                new PlayerToggleSneak()
        };
        //@formatter:on
        for (Listener listener : registrations) {
            pm.registerEvents(listener, instance);
        }
        /* COMMANDS */
        instance.getCommand("za").setExecutor(commandInstance);
    }
}
