package com.github;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.External;
import com.github.JamesNorris.MessageTransfer;
import com.github.JamesNorris.Update;
import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Enumerated.MessageDirection;
import com.github.JamesNorris.Enumerated.Setting;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Manager.RegistrationManager;
import com.github.JamesNorris.Threading.BlinkerThread;
import com.github.JamesNorris.Threading.BarrierBreakThread;
import com.github.JamesNorris.Threading.MainThread;
import com.github.JamesNorris.Threading.MobClearingThread;
import com.github.JamesNorris.Threading.MobFlamesThread;
import com.github.JamesNorris.Util.SpecificMessage;

public class Ablockalypse extends JavaPlugin {
    private static String address = "http://api.bukget.org/api2/bukkit/plugin/Ablockalypse/latest";
    protected static DataManipulator dm;
    public static Ablockalypse instance;
    private static String issues = "https://github.com/JamesNorris/Ablockalypse/issues";
    private static String path = "plugins" + File.separator + "Ablockalypse.jar";
    private static Update upd;
    private static MainThread mt;

    /**
     * Called when something is not working, and the plugin needs to be monitored.
     * 
     * @param reason The reason for the exception
     * @param disable Whether or not the Ablockalypse plugin should stop working
     */
    public static void crash(String reason, boolean disable) {
        /* Everything in this method should be static, except for strings */
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb.append("An aspect of Ablockalypse is broken, please report at: \n");
        sb.append(getIssuesURL() + "\n");
        sb.append("--------------------------[ERROR REPORT]--------------------------\n");
        sb.append("VERSION: " + DataManipulator.data.version + "\n");
        sb.append("BREAK REASON: " + reason + "\n");
        sb.append("---------------------------[END REPORT]---------------------------\n");
        if (!disable) {
            sb.append("The plugin will now continue working...\n");
            sb2.append("The error was NOT FATAL, therefore Ablockalypse will continue running.");
        } else {
            sb.append("FATAL ERROR, the plugin will now shut down!\n");
            sb2.append("The error was FATAL, therefore Ablockalypse will shut down.");
            Ablockalypse.kill();
        }
        MessageTransfer.sendMessage(new SpecificMessage(MessageDirection.CONSOLE_ERROR, sb.toString()));
        MessageTransfer.sendMessage(new SpecificMessage(MessageDirection.XMPP, "An error occurred! " + sb2.toString()));
    }

    /**
     * Gets a single DataManipulator instance to prevent duplication.
     * 
     * @return The DataManipulator used by Ablockalypse
     */
    public static DataManipulator getData() {
        return dm;
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
     * Gets the URL for issues to be sent to github.
     * 
     * @return The github issues URL
     */
    public static String getIssuesURL() {
        return issues;
    }

    /**
     * Gets the path from the plugins folder to the data folder.
     * 
     * @return The data folder path
     */
    public static String getJARPath() {
        return path;
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
     * Gets the URL from bukget for updating.
     * 
     * @return The bukget URL
     */
    public static String getUpdateURL() {
        return address;
    }

    /**
     * Kills the plugin.
     */
    public static void kill() {
        Ablockalypse.instance.setEnabled(false);
    }

    public GlobalData data;

    @Override public void onDisable() {
        External.saveData();
        for (BlinkerThread bt : data.blinkers)
            bt.remove();
        if (data.games != null) {
            for (int i = 0; i < data.games.size(); i++) {
                ZAGameBase zag = data.games.get(i);
                if (zag != null)
                    zag.remove();
            }
        }
        data = null;
        instance = null;
        this.getClassLoader().clearAssertionStatus();// Added to prevent lingering static objects.
    }

    @Override public void onEnable() {
        Ablockalypse.instance = this;
        External.loadExternalFiles(this);
        upd = new Update(this);
        data = new GlobalData(this);
        dm = new DataManipulator();
        MessageTransfer.sendMessage(new SpecificMessage(MessageDirection.CONSOLE_OUTPUT, "[Ablockalypse] Checking for updates..."));
        if (!(Boolean) Setting.ENABLEAUTOUPDATE.getSetting() && upd.check()) {
            this.getServer().getPluginManager().disablePlugin(this);
            MessageTransfer.sendMessage(new SpecificMessage(MessageDirection.CONSOLE_OUTPUT, "[Ablockalypse] An update has occurred, please restart the server to enable it!"));
        } else {
            MessageTransfer.sendMessage(new SpecificMessage(MessageDirection.CONSOLE_OUTPUT, "[Ablockalypse] No updates found."));
            RegistrationManager.register(this);
            External.loadData();
            mt = new MainThread(true);
            new BarrierBreakThread(true, 20);
            new MobClearingThread((Boolean) Setting.CLEARMOBS.getSetting(), 360);
            new MobFlamesThread(true, 20);
        }
    }
}
