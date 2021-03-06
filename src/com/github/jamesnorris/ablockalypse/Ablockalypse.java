package com.github.jamesnorris.ablockalypse;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jamesnorris.ablockalypse.aspect.Game;
import com.github.jamesnorris.ablockalypse.aspect.PermanentAspect;
import com.github.jamesnorris.ablockalypse.aspect.PlayerState;
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
import com.github.jamesnorris.ablockalypse.threading.MainThread;
import com.github.jamesnorris.ablockalypse.threading.inherent.ServerBarrierActionTask;
import com.github.jamesnorris.ablockalypse.threading.inherent.ServerHellhoundActionTask;
import com.github.jamesnorris.ablockalypse.threading.inherent.ServerMobSpawnPreventionTask;
import com.github.jamesnorris.ablockalypse.threading.inherent.ServerTeleporterActionTask;

public class Ablockalypse extends JavaPlugin {
    private static Ablockalypse instance;
    private static DataContainer data = new DataContainer();
    private static MainThread mainThread;
    private static External external;
    private static BaseCommand commandInstance;
    private static Tracker tracker;

    public static BaseCommand getBaseCommandInstance() {
        return commandInstance;
    }

    public static DataContainer getData() {
        return data;
    }

    public static External getExternal() {
        return external;
    }

    public static Ablockalypse getInstance() {
        return instance;
    }

    public static MainThread getMainThread() {
        return mainThread;
    }

    public static Tracker getTracker() {
        return tracker;
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

    @SuppressWarnings("unchecked") @Override public void onEnable() {
        Ablockalypse.instance = this;
        external = new External(this);
        tracker = new Tracker("Ablockalypse", getDescription().getVersion(), "https://github.com/JamesNorris/Ablockalypse/issues", (Integer) Setting.MAX_FATALITY.getSetting());
        commandInstance = new BaseCommand();
        register();
        initServerThreads(false);
        try {
            for (File file : external.getSavedDataFolder().listFiles()) {
                if (file != null && !file.isDirectory()) {
                    Map<String, Object> save = (Map<String, Object>) External.load(file);
                    PermanentAspect.load(Game.class, save);
                }
            }
            for (File file : external.getSavedPlayerDataFolder().listFiles()) {
                if (file != null && !file.isDirectory()) {
                    Map<String, Object> save = (Map<String, Object>) External.load(file);
                    PlayerJoin.toLoad.add((PlayerState) PermanentAspect.load(PlayerState.class, save));
                    file.delete();
                }
            }
        } catch (EOFException e) {
            // nothing, the file is empty and no data was saved
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        mainThread.run();// starts the main thread, which was told not to autorun earlier. Done to prevent NPEs with loading saves and running threads.
    }

    protected void initServerThreads(boolean startMain) {
        mainThread = new MainThread(startMain);
        new ServerBarrierActionTask(true);
        new ServerMobSpawnPreventionTask(360, (Boolean) Setting.CLEAR_MOBS.getSetting());
        new ServerHellhoundActionTask(20, true);
        new ServerTeleporterActionTask(20, true);
    }

    protected void register() {
        PluginManager pm = getServer().getPluginManager();
        /* EVENTS */
        for (Listener listener : new Listener[] {new EntityDamage(), new PlayerDeath(), new PlayerInteract(),
                new SignChange(), new EntityDeath(), new PlayerPickupItem(), new ProjectileHit(), new PlayerMove(),
                new EntityBreakDoor(), new PlayerTeleport(), new PlayerQuit(), new EntityTarget(), new PlayerRespawn(),
                new EntityExplode(), new EntityDamageByEntity(), new PlayerJoin(), new BlockBreak(),
                new PlayerDropItem(), new PlayerKick(), new BlockPlace(), new BlockRedstone(), new PlayerToggleSneak()}) {
            pm.registerEvents(listener, instance);
        }
        /* COMMANDS */
        instance.getCommand("za").setExecutor(commandInstance);
    }
}
