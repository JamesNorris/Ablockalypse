package com.github.jamesnorris.ablockalypse.threading.inherent;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.entity.ZAPlayer;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;
import com.github.jamesnorris.ablockalypse.enumerated.PlayerStatus;
import com.github.jamesnorris.ablockalypse.threading.RepeatingTask;

public class RespawnTask extends RepeatingTask {
    private static final int INTERVAL = 20;
    private DataContainer data = Ablockalypse.getData();
    private Player player;
    private boolean messageSent = false, respawn = true;
    private int time, level;
    private Location spawn;
    private ZAPlayer zap;

    /**
     * Creates a new RespawnThread instance.
     * 
     * @param player The player to wait for
     * @param time The time to count down
     * @param autorun Whether or not to automatically run the thread
     */
    public RespawnTask(Player player, int time, boolean autorun, boolean respawn) {
        super(INTERVAL, autorun);
        this.player = player;
        this.time = time;
        zap = data.getZAPlayer(player);
        level = zap.getGame().getLevel();
        spawn = zap.getGame().getMainframe().getLocation();
        this.respawn = respawn;
    }

    @Override public void run() {
        if (!data.isZAPlayer(player)) {
            cancel();
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
                    cancel();
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
                zap.setStatus(PlayerStatus.NORMAL);// just in case the player had already been sent into the game
                cancel();
                return;
            } else {
                player.sendMessage(ChatColor.GRAY + "Waiting to " + (respawn ? "respawn" : "rejoin") + "... " + time);
            }
            --time;
        }
    }

    public void setItemsSpawnedInWith() {
        // TODO use when loading players to allow their items to be given back on respawn
    }

    public void setSpawnLocation(Location spawn) {
        this.spawn = spawn;
    }
}
