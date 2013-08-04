package com.github;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.aspect.Barrier;
import com.github.aspect.Claymore;
import com.github.aspect.Game;
import com.github.aspect.Hellhound;
import com.github.aspect.Mainframe;
import com.github.aspect.MobSpawner;
import com.github.aspect.MysteryChest;
import com.github.aspect.Passage;
import com.github.aspect.PowerSwitch;
import com.github.aspect.Undead;
import com.github.aspect.ZAPlayer;
import com.github.behavior.GameObject;
import com.github.behavior.ZAMob;
import com.github.behavior.ZAThread;
import com.github.utility.MiscUtil;

public class DataContainer {
    public static DataContainer fromObject(Object obj) {
        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                if (field.getType() == DataContainer.class) {
                    return (DataContainer) field.get(obj);
                }
            }
            return (DataContainer) obj.getClass().getDeclaredField("data").get(obj);// if a DataContainer field is not found otherwise...
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Material[] modifiableMaterials = new Material[] {Material.FLOWER_POT, Material.FLOWER_POT_ITEM};// default materials
    public CopyOnWriteArrayList<Object> objects = new CopyOnWriteArrayList<Object>();

    public boolean gameExists(String gamename) {
        return getObjectsOfType(Game.class).contains(getGame(gamename, false));
    }

    public Barrier getBarrier(Location loc) {
        return getGameObjectByLocation(Barrier.class, loc);
    }

    public Claymore getClaymore(Location loc) {
        return getGameObjectByLocation(Claymore.class, loc);
    }

    public Entity getEntityByUUID(World world, UUID uuid) {
        for (Entity entity : world.getEntities()) {
            if (entity.getUniqueId().compareTo(uuid) == 0) {
                return entity;
            }
        }
        return null;
    }

    public Game getGame(String name, boolean force) {
        List<Game> games = getObjectsOfType(Game.class);
        Game correctGame = null;
        for (Game game : games) {
            if (game.getName().equalsIgnoreCase(name)) {
                correctGame = game;
            }
        }
        return correctGame != null ? correctGame : force ? new Game(name) : null;
    }

    public <O extends GameObject> O getGameObjectByLocation(Class<O> type, Location loc) {
        for (O obj : getObjectsOfType(type)) {
            for (Block matchBlock : obj.getDefiningBlocks()) {
                Location match = matchBlock.getLocation();
                if (MiscUtil.locationMatch(match, loc)) {
                    return obj;
                }
            }
        }
        return null;
    }

    public GameObject getGameObjectByLocation(Location loc) {
        return getGameObjectByLocation(GameObject.class, loc);
    }

    public Hellhound getHellhound(Entity e) {
        return getZAMobByEntity(Hellhound.class, e);
    }

    public Mainframe getMainframe(Location loc) {
        return getGameObjectByLocation(Mainframe.class, loc);
    }

    public MobSpawner getMobSpawner(Location loc) {
        return getGameObjectByLocation(MobSpawner.class, loc);
    }

    public MysteryChest getMysteryChest(Location loc) {
        return getGameObjectByLocation(MysteryChest.class, loc);
    }

    @SuppressWarnings("unchecked") public <O extends Object> List<O> getObjectsOfType(Class<O> type) {
        ArrayList<O> list = new ArrayList<O>();
        for (Object obj : objects) {
            if (type.isAssignableFrom(obj.getClass())) {
                list.add((O) obj);
            }
        }
        return list;
    }

    public Passage getPassage(Location loc) {
        return getGameObjectByLocation(Passage.class, loc);
    }

    public PowerSwitch getPowerSwitch(Location loc) {
        return getGameObjectByLocation(PowerSwitch.class, loc);
    }

    public ArrayList<MobSpawner> getSpawns(String gamename) {
        ArrayList<MobSpawner> spawners = new ArrayList<MobSpawner>();
        for (MobSpawner spawn : getObjectsOfType(MobSpawner.class)) {
            if (spawn.getGame().getName().equalsIgnoreCase(gamename)) {
                spawners.add(spawn);
            }
        }
        return spawners;
    }

    @SuppressWarnings("unchecked") public <T extends ZAThread> List<T> getThreadsOfType(Class<T> type) {
        ArrayList<T> list = new ArrayList<T>();
        for (ZAThread thread : getObjectsOfType(ZAThread.class)) {
            if (thread.getClass().isInstance(type)) {
                list.add((T) thread);
            }
        }
        return list;
    }

    public Undead getUndead(Entity e) {
        return getZAMobByEntity(Undead.class, e);
    }

    public ZAMob getZAMob(Entity e) {
        return getZAMobByEntity(ZAMob.class, e);
    }

    public <Z extends ZAMob> Z getZAMobByEntity(Class<Z> clazz, Entity ent) {
        for (Z mob : getObjectsOfType(clazz)) {
            if (mob.getEntity().getUniqueId().compareTo(ent.getUniqueId()) == 0) {
                return mob;
            }
        }
        return null;
    }

    public ZAPlayer getZAPlayer(Player player) {
        for (ZAPlayer zap : getObjectsOfType(ZAPlayer.class)) {
            if (zap.getName().equals(player.getName())) {
                return zap;
            }
        }
        return null;
    }

    public ZAPlayer getZAPlayer(Player player, String gamename, boolean force) {
        if (getZAPlayer(player) != null) {
            return getZAPlayer(player);
        } else if (getGame(gamename, false) != null && force) {
            return new ZAPlayer(player, getGame(gamename, false));
        } else if (force) {
            return new ZAPlayer(player, getGame(gamename, true));
        }
        return null;
    }

    public boolean isBarrier(Location loc) {
        return getBarrier(loc) != null;
    }

    public boolean isClaymore(Location loc) {
        return getClaymore(loc) != null;
    }

    public boolean isGame(String name) {
        return getGame(name, false) != null;
    }

    public boolean isGameObject(Location loc) {
        return getGameObjectByLocation(loc) != null;
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
        return getMysteryChest(loc) != null;
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
        return getZAPlayer(player) != null;
    }

    public void setModifiableMaterials(Material[] materials) {
        modifiableMaterials = materials;
    }
}
