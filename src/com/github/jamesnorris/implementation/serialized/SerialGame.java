package com.github.jamesnorris.implementation.serialized;

import java.util.ArrayList;

import org.bukkit.Location;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.MysteryChest;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.inter.Permadata;
import com.github.jamesnorris.inter.Permadatable;

public class SerialGame implements Permadata {
    private static final long serialVersionUID = -5703559404060456450L;
    private final SerialMysteryChest serialActiveChest;
    private final int level, mobcount, spawnedInThisRound;
    private final SerialMainframe serialMainframe;
    private final String name;
    private final ArrayList<Permadata> serialObjects = new ArrayList<Permadata>();
    private final ArrayList<Integer> wolfLevels;
    private final boolean wolfRound, paused, started;

    public SerialGame(Game game) {
        this.serialActiveChest = (SerialMysteryChest) game.getActiveMysteryChest().getSerializedVersion();
        this.level = game.getLevel();
        this.mobcount = game.getMobCount();
        this.spawnedInThisRound = game.getMobCountSpawnedInThisRound();
        this.serialMainframe = (SerialMainframe) game.getMainframe().getSerializedVersion();
        this.name = game.getName();
        for (Permadatable obj : game.getObjectsOfType(Permadatable.class)) {
            serialObjects.add(obj.getSerializedVersion());
        }
        wolfLevels = game.getWolfLevels();
        this.wolfRound = game.isWolfRound();
        this.paused = game.isPaused();
        this.started = game.hasStarted();
    }
    
    public String getName() {
        return name;
    }

    public Game load() {
        Location chestLoc = serialActiveChest.getLocation();
        DataContainer data = Ablockalypse.getData();
        MysteryChest activeChest = (data.isMysteryChest(chestLoc)) ? data.getMysteryChest(chestLoc) : serialActiveChest.load();
        Game game = Ablockalypse.getData().getGame(name, true);
        game.setActiveMysteryChest(activeChest);
        int startBackup = (started) ? 1 : 0;
        game.setLevel(level - startBackup);
        if (started) {
            game.nextLevel();// starts the NextLevelThread, increases level by 1
        }
        game.setMobCount(mobcount);
        game.setMobCountSpawnedInThisRound(spawnedInThisRound);
        game.setMainframe(serialMainframe.load());
        for (Permadata serial : serialObjects) {
            game.addObject((GameObject) serial.load());//only works if the constructor of the class used during load() does not work
        }
        game.setWolfLevels(wolfLevels);
        game.setWolfRound(wolfRound);
        game.pause(paused);
        return game;
    }
}
