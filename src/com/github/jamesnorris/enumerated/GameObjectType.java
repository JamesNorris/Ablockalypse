package com.github.jamesnorris.enumerated;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

import com.github.jamesnorris.implementation.Passage;
import com.github.jamesnorris.implementation.Barrier;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.Hellhound;
import com.github.jamesnorris.implementation.Mainframe;
import com.github.jamesnorris.implementation.MobSpawner;
import com.github.jamesnorris.implementation.MysteryChest;
import com.github.jamesnorris.implementation.Undead;
import com.github.jamesnorris.inter.GameObject;
import com.google.common.collect.Maps;

public enum GameObjectType {
    PASSAGE {
        @Override public String getSerialization() {
            return "Passage";
        }

        @Override public void loadToGame(Game game, Location loc1, Location loc2) {
            game.addObject(new Passage((Game) game, loc1, loc2));
        }

        @Override public boolean requiresSecondLocation() {
            return true;
        }

        @Override public Location getSecondLocationIfApplicable(GameObject obj) {
            return ((Passage) obj).getPoint(2);
        }
    },
    BARRIER {
        @Override public String getSerialization() {
            return "Barrier";
        }

        @Override public void loadToGame(Game game, Location loc1, Location loc2) {
            game.addObject(new Barrier(loc1.getBlock(), (Game) game));
        }
        
        @Override public boolean requiresSecondLocation() {
            return false;
        }
        
        @Override public Location getSecondLocationIfApplicable(GameObject obj) {
            return null;
        }
    },
    CLAYMORE {
        @Override public String getSerialization() {
            return "Claymore";
        }

        @Override public void loadToGame(Game game, Location loc1, Location loc2) {
            // TODO find the ZAPlayer closest to loc1?
            System.err.println("[Ablockalypse] GameObjectType.CLAYMORE - Cannot load a Claymore instance that is not specified with a ZAPlayer instance!");
        }
        
        @Override public boolean requiresSecondLocation() {
            return false;
        }
        
        @Override public Location getSecondLocationIfApplicable(GameObject obj) {
            return null;
        }
    },
    HELLHOUND {
        @Override public String getSerialization() {
            return "HellHound";
        }

        @Override public void loadToGame(Game game, Location loc1, Location loc2) {
            new Hellhound((Wolf) loc1.getWorld().spawnEntity(loc1, EntityType.WOLF), game);
        }
        
        @Override public boolean requiresSecondLocation() {
            return false;
        }
        
        @Override public Location getSecondLocationIfApplicable(GameObject obj) {
            return null;
        }
    },
    MOB_SPAWNER {
        @Override public String getSerialization() {
            return "MobSpawner";
        }

        @Override public void loadToGame(Game game, Location loc1, Location loc2) {
            game.addObject(new MobSpawner(loc1, game));
        }
        
        @Override public boolean requiresSecondLocation() {
            return false;
        }
        
        @Override public Location getSecondLocationIfApplicable(GameObject obj) {
            return null;
        }
    },
    MYSTERY_CHEST {
        @Override public String getSerialization() {
            return "MysteryChest";
        }

        @Override public void loadToGame(Game game, Location loc1, Location loc2) {
            game.addObject(new MysteryChest(loc1.getBlock(), game, loc1, game.getActiveMysteryChest() == null));
        }
        
        @Override public boolean requiresSecondLocation() {
            return false;
        }
        
        @Override public Location getSecondLocationIfApplicable(GameObject obj) {
            return null;
        }
    },
    UNDEAD {
        @Override public String getSerialization() {
            return "Undead";
        }

        @Override public void loadToGame(Game game, Location loc1, Location loc2) {
            new Undead((Zombie) loc1.getWorld().spawnEntity(loc1, EntityType.ZOMBIE), game);
        }
        
        @Override public boolean requiresSecondLocation() {
            return false;
        }
        
        @Override public Location getSecondLocationIfApplicable(GameObject obj) {
            return null;
        }
    },
    ZAPLAYER {
        @Override public String getSerialization() {
            return "ZAPlayer";
        }

        @Override public void loadToGame(Game game, Location loc1, Location loc2) {
            //TODO find the nearest player to loc1?
            System.err.println("[Ablockalypse] GameObjectType.ZAPLAYER - Cannot load a ZAPlayer instance that is not specified with a Player instance!");
        }
        
        @Override public boolean requiresSecondLocation() {
            return false;
        }
        
        @Override public Location getSecondLocationIfApplicable(GameObject obj) {
            return null;
        }
    },
    MAINFRAME {
        @Override public String getSerialization() {
            return "Mainframe";
        }

        @Override public void loadToGame(Game game, Location loc1, Location loc2) {
            game.setMainframe(new Mainframe(game, loc1));
        }
        
        @Override public boolean requiresSecondLocation() {
            return false;
        }
        
        @Override public Location getSecondLocationIfApplicable(GameObject obj) {
            return null;
        }
    },
    POWER_SWITCH {
        @Override public String getSerialization() {
            return "PowerSwitch";
        }

        @Override public void loadToGame(Game game, Location loc1, Location loc2) {
            //TODO there should be a different way of loading stuff to a game, serializing a bunch of data required for each
        }

        @Override public boolean requiresSecondLocation() {
            return false;
        }

        @Override public Location getSecondLocationIfApplicable(GameObject obj) {
            return null;
        }
        
    };
    
    private final static Map<String, GameObjectType> BY_SERIAL = Maps.newHashMap();

    public static GameObjectType bySerialization(String serialization) {
        return BY_SERIAL.get(serialization);
    }

    public abstract String getSerialization();

    public abstract void loadToGame(Game game, Location loc1, Location loc2);
    
    public abstract boolean requiresSecondLocation();
    
    public abstract Location getSecondLocationIfApplicable(GameObject obj);

    static {
        for (GameObjectType type : values()) {
            BY_SERIAL.put(type.getSerialization(), type);
        }
    }
}
