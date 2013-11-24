package com.github.jamesnorris.ablockalypse.threading.inherent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.entity.ZAPlayer;
import com.github.jamesnorris.ablockalypse.enumerated.PlayerStatus;
import com.github.jamesnorris.ablockalypse.enumerated.ZAEffect;
import com.github.jamesnorris.ablockalypse.threading.RepeatingTask;
import com.github.jamesnorris.ablockalypse.utility.BukkitUtility;

public class TeleportTask extends RepeatingTask {
    private List<UUID> teleportingPlayers = new ArrayList<UUID>();
    private static final int INTERVAL = 20;
    private DataContainer data = Ablockalypse.getData();
    private Location loc;
    private int time;
    private ZAPlayer zaplayer;

    /**
     * Creates an instance of the thread for teleporting a player.
     * 
     * @param zaplayer The player to countdown for, as a ZAPlayer instance
     * @param time The time before the countdown stops
     * @param autorun Whether or not to run the thread automatically
     */
    public TeleportTask(ZAPlayer zaplayer, int time, boolean autorun) {
        super(INTERVAL, autorun);
        if (teleportingPlayers.contains(zaplayer.getUUID())) {
            return;
        }
        teleportingPlayers.add(zaplayer.getUUID());
        this.zaplayer = zaplayer;
        this.time = time;
        loc = zaplayer.getPlayer().getLocation();
        zaplayer.setStatus(PlayerStatus.TELEPORTING);
    }

    @Override public void cancel() {
        zaplayer.setStatus(PlayerStatus.NORMAL);
        teleportingPlayers.remove(zaplayer.getUUID());
        data.objects.remove(this);
    }

    @Override public void run() {
        Player player = zaplayer.getPlayer();
        if (time != 0) {
            if (!BukkitUtility.locationMatch(player.getLocation(), loc)) {
                cancel();
                player.sendMessage(ChatColor.GRAY + "Teleportation cancelled!");
                zaplayer.setStatus(PlayerStatus.NORMAL);
                return;
            }
            player.sendMessage(ChatColor.GRAY + "" + time + " seconds to teleport...");
            ZAEffect.TELEPORTATION.play(player.getLocation());
            --time;
        } else if (time <= 0) {
            ZAEffect.SMOKE.play(player.getLocation());
            zaplayer.sendToMainframe(ChatColor.GRAY + "Teleporting to mainframe...", "Teleport");
            ZAEffect.SMOKE.play(player.getLocation());
            cancel();
        }
    }
}
