package com.github.threading.inherent;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.Game;
import com.github.aspect.ZAPlayer;
import com.github.behavior.ZARepeatingTask;
import com.github.enumerated.PlayerStatus;

public class RespawnThread implements ZARepeatingTask {
    private DataContainer data = Ablockalypse.getData();
    private Player player;
    private boolean runThrough = false, messageSent = false, respawn = true;
    private int time, level, count = 0, interval = 20;
    private Location spawn;
    private ZAPlayer zap;

    /**
     * Creates a new RespawnThread instance.
     * 
     * @param player The player to wait for
     * @param time The time to count down
     * @param autorun Whether or not to automatically run the thread
     */
    public RespawnThread(Player player, int time, boolean autorun, boolean respawn) {
        this.player = player;
        this.time = time;
        zap = data.getZAPlayer(player);
        level = zap.getGame().getLevel();
        spawn = zap.getGame().getMainframe().getLocation();
        runThrough = autorun;
        this.respawn = respawn;
        addToThreads();
    }

    @Override public int getCount() {
        return count;
    }

    @Override public int getInterval() {
        return interval;
    }

    @Override public void remove() {
        data.objects.remove(this);
    }

    @Override public void run() {
        if (!data.isZAPlayer(player)) {
            remove();
            return;
        }
        if (player.isDead() || !player.isOnline()) {
            return;
        }
        if (!messageSent) {
            player.sendMessage(ChatColor.GRAY + (respawn ? "You will respawn at the beginning of the next level." : "You will rejoin your game in " + time + " seconds."));
            messageSent = true;
            if (!respawn) {
                time -= 1;
            }
        } else if (zap.getGame().getLevel() > level || !respawn) {
            if (time == 0) {
                ZAPlayer zap = data.getZAPlayer(player);
                if (zap.getGame() == null) {
                    remove();
                    return;
                }
                if (!zap.hasBeenSentIntoGame()) {
                    Game game = zap.getGame();
                    String gameName = game.getName();
                    zap.removeFromGame();
                    zap.setStatus(PlayerStatus.NORMAL);
                    data.objects.add(zap);
                    zap.loadPlayerToGame(gameName, false);
                    if (game.isPaused()) {
                        game.pause(false);
                    }
                }
                zap.teleport(spawn, "Respawn");
                zap.setStatus(PlayerStatus.NORMAL);//just in case the player had already been sent into the game
                remove();
                return;
            } else {
                player.sendMessage(ChatColor.GRAY + "Waiting to " + (respawn ? "respawn" : "rejoin") + "... " + time);
            }
            --time;
        }
    }

    @Override public boolean runThrough() {
        return runThrough;
    }

    @Override public void setCount(int i) {
        count = i;
    }

    @Override public void setInterval(int i) {
        interval = i;
    }

    public void setItemsSpawnedInWith() {
        // TODO make this work
        // TODO use when loading players to allow their items to be given back on respawn
    }

    @Override public void setRunThrough(boolean tf) {
        runThrough = tf;
    }

    public void setSpawnLocation(Location spawn) {
        this.spawn = spawn;
    }

    private synchronized void addToThreads() {
        data.objects.add(this);
    }
}
