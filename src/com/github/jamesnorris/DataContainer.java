package com.github.jamesnorris;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

import com.github.jamesnorris.implementation.Barrier;
import com.github.jamesnorris.implementation.Claymore;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.Hellhound;
import com.github.jamesnorris.implementation.Mainframe;
import com.github.jamesnorris.implementation.MobSpawner;
import com.github.jamesnorris.implementation.MysteryChest;
import com.github.jamesnorris.implementation.Passage;
import com.github.jamesnorris.implementation.PowerSwitch;
import com.github.jamesnorris.implementation.Undead;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.inter.ZAMob;
import com.github.jamesnorris.inter.ZAThread;
import com.github.jamesnorris.util.MiscUtil;

public class DataContainer {
    public static DataContainer fromObject(Object obj) {
        try {
            return (DataContainer) obj.getClass().getDeclaredField("data").get(obj);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CopyOnWriteArrayList<Barrier> barriers = new CopyOnWriteArrayList<Barrier>();
    public ConcurrentHashMap<Location, MysteryChest> chests = new ConcurrentHashMap<Location, MysteryChest>();
    public CopyOnWriteArrayList<Claymore> claymores = new CopyOnWriteArrayList<Claymore>();
    public CopyOnWriteArrayList<GameObject> gameObjects = new CopyOnWriteArrayList<GameObject>();
    public ConcurrentHashMap<String, Game> games = new ConcurrentHashMap<String, Game>();
    public CopyOnWriteArrayList<Hellhound> hellhounds = new CopyOnWriteArrayList<Hellhound>();
    public ConcurrentHashMap<String, Mainframe> mainframes = new ConcurrentHashMap<String, Mainframe>();
    public CopyOnWriteArrayList<ZAMob> mobs = new CopyOnWriteArrayList<ZAMob>();
    public ConcurrentHashMap<Game, MobSpawner> mobSpawners = new ConcurrentHashMap<Game, MobSpawner>();
    public Material[] modifiableMaterials = new Material[] {Material.FLOWER_POT, Material.FLOWER_POT_ITEM};//default materials
    public CopyOnWriteArrayList<Passage> passages = new CopyOnWriteArrayList<Passage>();
    public ConcurrentHashMap<Player, ZAPlayer> players = new ConcurrentHashMap<Player, ZAPlayer>();
    public CopyOnWriteArrayList<PowerSwitch> switches = new CopyOnWriteArrayList<PowerSwitch>();
    public CopyOnWriteArrayList<ZAThread> threads = new CopyOnWriteArrayList<ZAThread>();
    public CopyOnWriteArrayList<Undead> undead = new CopyOnWriteArrayList<Undead>();

    public boolean gameExists(String gamename) {
        return games.containsKey(gamename);
    }

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

    public Claymore getClaymore(Location loc) {
        for (Claymore more : claymores) {
            if (MiscUtil.locationMatch(more.getDefiningBlock().getLocation(), loc)) {
                return more;
            }
        }
        return null;
    }

    public Game getGame(String name, boolean force) {
        if (games.containsKey(name)) {
            return games.get(name);
        } else if (force) {
            return new Game(name);
        }
        return null;
    }

    public Hellhound getHellhound(Entity e) {
        for (Hellhound hh : hellhounds) {
            Wolf wolf = hh.getWolf();
            if (wolf != null && wolf.getEntityId() == e.getEntityId()) {
                return hh;
            }
        }
        return null;
    }

    public Mainframe getMainframe(Location loc) {
        for (Mainframe frame : mainframes.values()) {
            if (MiscUtil.locationMatch(frame.getLocation(), loc)) {
                return frame;
            }
        }
        return null;
    }
    
    public Entity getEntityByUUID(World world, UUID uuid) {
        for (Entity entity : world.getEntities()) {
            if (entity.getUniqueId().compareTo(uuid) == 0) {
                return entity;
            }
        }
        return null;
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

    public MysteryChest getMysteryChest(Location loc) {
        MysteryChest mc = null;
        if (chests.containsKey(loc)) {
            mc = chests.get(loc);
        }
        return mc;
    }

    public GameObject getObject(Location loc) {
        for (GameObject obj : gameObjects) {
            if (obj.getDefiningBlocks().contains(loc.getBlock())) {
                return obj;
            }
        }
        return null;
    }

    public Passage getPassage(Location loc) {
        for (Passage passage : passages) {
            if (passage.getBlocks().contains(loc.getBlock())) {
                return passage;
            }
        }
        return null;
    }
    
    public PowerSwitch getPowerSwitch(Location loc) {
        for (PowerSwitch power : switches) {
            if (MiscUtil.locationMatch(power.getLocation(), loc)) {
                return power;
            }
        }
        return null;
    }

    public ArrayList<MobSpawner> getSpawns(String gamename) {
        ArrayList<MobSpawner> locs = new ArrayList<MobSpawner>();
        for (Game zag : mobSpawners.keySet()) {
            if (zag.getName().equalsIgnoreCase(gamename)) {
                locs.add(mobSpawners.get(zag));
            }
        }
        return locs;
    }

    @SuppressWarnings("unchecked") public <T extends ZAThread> List<T> getThreadsOfType(Class<T> type) {
        ArrayList<T> list = new ArrayList<T>();
        for (ZAThread thread : threads) {
            if (thread.getClass().isInstance(type)) {
                list.add((T) thread);
            }
        }
        return list;
    }

    public Undead getUndead(Entity e) {
        for (Undead gu : undead) {
            Zombie zomb = gu.getZombie();
            if (zomb != null && zomb.getEntityId() == e.getEntityId()) {
                return gu;
            }
        }
        return null;
    }

    public ZAMob getZAMob(Entity e) {
        for (ZAMob mob : mobs) {
            Entity mobEntity = mob.getEntity();
            if (mobEntity != null && mobEntity.getEntityId() == e.getEntityId()) {
                return mob;
            }
        }
        return null;
    }

    public ZAPlayer getZAPlayer(Player player) {
        return players.get(player);
    }

    public ZAPlayer getZAPlayer(Player player, String gamename, boolean force) {
        ZAPlayer zap = null;
        if (players.containsKey(player)) {
            zap = players.get(player);
        } else if (games.containsKey(gamename) && force) {
            zap = new ZAPlayer(player, getGame(gamename, false));
        } else if (force) {
            zap = new ZAPlayer(player, getGame(gamename, true));
        }
        return zap;
    }

    public boolean isBarrier(Location loc) {
        return getBarrier(loc) != null;
    }

    public boolean isClaymore(Location loc) {
        return getClaymore(loc) != null;
    }

    public boolean isHellhound(Entity e) {
        return getHellhound(e) != null;
    }

    public boolean isMainframe(Location loc) {
        return getMainframe(loc) != null;
    }

    public boolean isMobSpawner(Location loc) {
        return getMobSpawner(loc) != null;
    }

    public boolean isModifiable(Material type) {
        for (Material m : modifiableMaterials) {
            if (m == type) {
                return true;
            }
        }
        return false;
    }

    public boolean isMysteryChest(Location loc) {
        return chests.keySet().contains(loc);
    }

    public boolean isObject(Location loc) {
        return getObject(loc) != null;
    }

    public boolean isPassage(Location loc) {
        return getPassage(loc) != null;
    }
    
    public boolean isPowerSwitch(Location loc) {
        return getPowerSwitch(loc) != null;
    }

    public boolean isUndead(Entity e) {
        return getUndead(e) != null;
    }

    public boolean isZAMob(Entity e) {
        return getZAMob(e) != null;
    }

    public boolean isZAPlayer(Player player) {
        return players != null && players.size() >= 1 && players.containsKey(player);
    }

    public void setModifiableMaterials(Material[] materials) {
        modifiableMaterials = materials;
    }
}
