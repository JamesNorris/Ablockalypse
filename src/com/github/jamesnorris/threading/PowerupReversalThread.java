package com.github.jamesnorris.threading;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.enumerated.PowerupType;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.inter.ZADelayedThread;

public class PowerupReversalThread extends DataManipulator implements ZADelayedThread {
    private int delay, countup = 0;
    private Game game;
    private Player player;
    private Entity cause;
    private PowerupType type;

    public PowerupReversalThread(Game game, Player player, Entity cause, PowerupType type, int delay) {
        this.game = game;
        this.player = player;
        this.cause = cause;
        this.type = type;
        this.delay = delay;
        addToThreads();
    }

    private synchronized void addToThreads() {
        data.threads.add(this);
    }

    @Override public void run() {
        type.reverse(game, player, cause, data);
    }

    @Override public void remove() {
        data.threads.remove(this);
    }

    @Override public int getDelay() {
        return delay;
    }

    @Override public int getCountup() {
        return countup;
    }

    @Override public void setCountup(int countup) {
        this.countup = countup;
    }
}
