package com.github.jamesnorris;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.jamesnorris.implementation.Area;
import com.github.jamesnorris.implementation.Barrier;
import com.github.jamesnorris.implementation.Claymore;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.Hellhound;
import com.github.jamesnorris.implementation.Mainframe;
import com.github.jamesnorris.implementation.MobSpawner;
import com.github.jamesnorris.implementation.MysteryChest;
import com.github.jamesnorris.implementation.Undead;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.inter.ZAMob;
import com.github.jamesnorris.inter.ZAThread;
import com.github.jamesnorris.util.MiscUtil;

public class DataManipulator {
    public static DataManipulator data;
    public String version = Ablockalypse.instance.getDescription().getVersion();
    public List<String> authors = Ablockalypse.instance.getDescription().getAuthors();
    // AREAS
    public CopyOnWriteArrayList<Area> areas = new CopyOnWriteArrayList<Area>();

    public Area getArea(Location loc) {
        for (Area area : areas) {
            if (area.getBlocks().contains(loc.getBlock())) {
                return area;
            }
        }
        return null;
    }

    public boolean isArea(Location loc) {
        return getArea(loc) != null;
    }

    // BARRIERS
    public CopyOnWriteArrayList<Barrier> barriers = new CopyOnWriteArrayList<Barrier>();

    public Barrier getBarrier(Location loc) {
        for (Barrier barrier : barriers) {
            for (Block block : barrier.getBlocks()) {
                if (MiscUtil.locationMatch(block.getLocation(), loc)) {
                    return barrier;
                }
            }
        }
        return null;
    }

    public boolean isBarrier(Location loc) {
        return getBarrier(loc) != null;
    }

    // CLAYMORES
    public CopyOnWriteArrayList<Claymore> claymores = new CopyOnWriteArrayList<Claymore>();

    public Claymore getClaymore(Location loc) {
        for (Claymore more : claymores) {
            if (MiscUtil.locationMatch(more.getDefiningBlock().getLocation(), loc)) {
                return more;
            }
        }
        return null;
    }

    public boolean isClaymore(Location loc) {
        return getClaymore(loc) != null;
    }

    // GAME OBJECTS
    public CopyOnWriteArrayList<GameObject> gameObjects = new CopyOnWriteArrayList<GameObject>();

    public GameObject getObject(Location loc) {
        for (GameObject obj : gameObjects) {
            if (obj.getDefiningBlocks().contains(loc.getBlock())) {
                return obj;
            }
        }
        return null;
    }

    public boolean isObject(Location loc) {
        return getObject(loc) != null;
    }

    // GAMES
    public ConcurrentHashMap<String, Game> games = new ConcurrentHashMap<String, Game>();

    public Game getGame(String name, boolean force) {
        if (games.containsKey(name)) {
            return games.get(name);
        } else if (force) {
            return new Game(name);
        }
        return null;
    }

    public boolean gameExists(String gamename) {
        return games.containsKey(gamename);
    }

    // HELLHOUNDS
    public CopyOnWriteArrayList<Hellhound> hellhounds = new CopyOnWriteArrayList<Hellhound>();

    public Hellhound getHellhound(Entity e) {
        for (Hellhound hh : hellhounds)
            if (hh.getWolf().getEntityId() == e.getEntityId())
                return hh;
        return null;
    }

    public boolean isHellhound(Entity e) {
        return getHellhound(e) != null;
    }

    // MAINFRAMES
    public ConcurrentHashMap<String, Mainframe> mainframes = new ConcurrentHashMap<String, Mainframe>();

    public Mainframe getMainframe(Location loc) {
        for (Mainframe frame : mainframes.values()) {
            if (MiscUtil.locationMatch(frame.getLocation(), loc)) {
                return frame;
            }
        }
        return null;
    }

    public boolean isMainframe(Location loc) {
        return getMainframe(loc) != null;
    }

    // MOB SPAWNERS
    public ConcurrentHashMap<Game, MobSpawner> mobSpawners = new ConcurrentHashMap<Game, MobSpawner>();

    public ArrayList<MobSpawner> getSpawns(String gamename) {
        ArrayList<MobSpawner> locs = new ArrayList<MobSpawner>();
        for (Game zag : mobSpawners.keySet())
            if (zag.getName().equalsIgnoreCase(gamename))
                locs.add(mobSpawners.get(zag));
        return locs;
    }

    public MobSpawner getMobSpawner(Location loc) {
        for (Game game : mobSpawners.keySet()) {
            MobSpawner spawn = mobSpawners.get(game);
            if (MiscUtil.locationMatch(spawn.getBukkitLocation(), loc)) {
                return spawn;
            }
        }
        return null;
    }

    public boolean isMobSpawner(Location loc) {
        return getMobSpawner(loc) != null;
    }

    // MODIFIABLE MATERIALS
    //@formatter:off
    public Material[] modifiableMaterials = new Material[] {//default materials
            Material.FLOWER_POT, 
            Material.FLOWER_POT_ITEM
            };
    //@formatter:on
    public void setModifiableMaterials(Material[] materials) {
        modifiableMaterials = materials;
    }

    public boolean isModifiable(Material type) {
        for (Material m : modifiableMaterials) {
            if (m == type) {
                return true;
            }
        }
        return false;
    }

    // MYSTERY CHESTS
    public ConcurrentHashMap<Location, MysteryChest> chests = new ConcurrentHashMap<Location, MysteryChest>();

    public MysteryChest getMysteryChest(Location loc) {
        MysteryChest mc = null;
        if (chests.containsKey(loc))
            mc = chests.get(loc);
        return mc;
    }

    public boolean isMysteryChest(Location loc) {
        return chests.keySet().contains(loc);
    }

    // POWER SWITCHES
    // TODO
    // THREADS
    public CopyOnWriteArrayList<ZAThread> threads = new CopyOnWriteArrayList<ZAThread>();

    @SuppressWarnings("unchecked") public <T extends ZAThread> List<T> getThreadsOfType(Class<T> type) {
        ArrayList<T> list = new ArrayList<T>();
        for (ZAThread thread : threads) {
            if (thread.getClass().isInstance(type)) {
                list.add((T) thread);
            }
        }
        return list;
    }

    // UNDEAD
    public CopyOnWriteArrayList<Undead> undead = new CopyOnWriteArrayList<Undead>();

    public Undead getUndead(Entity e) {
        for (Undead gu : undead)
            if (gu.getZombie().getEntityId() == e.getEntityId())
                return gu;
        return null;
    }

    public boolean isUndead(Entity e) {
        return getUndead(e) != null;
    }

    // ZA MOB
    public CopyOnWriteArrayList<ZAMob> mobs = new CopyOnWriteArrayList<ZAMob>();

    public ZAMob getZAMob(Entity e) {
        for (ZAMob mob : mobs) {
            if (mob.getEntity().getEntityId() == e.getEntityId()) {
                return mob;
            }
        }
        return null;
    }

    public boolean isZAMob(Entity e) {
        return getZAMob(e) != null;
    }

    // ZA PLAYER
    public ConcurrentHashMap<Player, ZAPlayer> players = new ConcurrentHashMap<Player, ZAPlayer>();

    public ZAPlayer getZAPlayer(Player player) {
        return players.get(player);
    }

    public ZAPlayer getZAPlayer(Player player, String gamename, boolean force) {
        ZAPlayer zap = null;
        if (players.containsKey(player))
            zap = players.get(player);
        else if (games.containsKey(gamename) && force)
            zap = new ZAPlayer(player, games.get(gamename));
        else if (force)
            zap = new ZAPlayer(player, new Game(gamename));
        return zap;
    }

    public boolean playerExists(Player player) {
        return players.containsKey(player);
    }
}
