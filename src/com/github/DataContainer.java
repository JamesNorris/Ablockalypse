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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.aspect.block.Barrier;
import com.github.aspect.block.Claymore;
import com.github.aspect.block.MobSpawner;
import com.github.aspect.block.MysteryBox;
import com.github.aspect.block.Passage;
import com.github.aspect.block.Teleporter;
import com.github.aspect.entity.Grenade;
import com.github.aspect.entity.Hellhound;
import com.github.aspect.entity.ZAMob;
import com.github.aspect.entity.ZAPlayer;
import com.github.aspect.entity.Zombie;
import com.github.aspect.intelligent.Game;
import com.github.behavior.GameObject;
import com.github.threading.Task;
import com.github.utility.BukkitUtility;

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
    
    public <O extends GameObject> O getClosest(Class<O> type, Location loc) {
        return getClosest(type, loc, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    }
    
    public <O extends GameObject> O getClosest(Class<O> type, Location loc, double distX, double distY, double distZ) {
        O object = null;
        double lowestDist = Double.MAX_VALUE;
        for (O obj : getObjectsOfType(type)) {
            if (obj.getDefiningBlocks() == null) {
                continue;
            }
            Location objLoc = obj.getDefiningBlock().getLocation();
            double xDif = Math.abs(objLoc.getX() - loc.getX());
            double yDif = Math.abs(objLoc.getY() - loc.getY());
            double zDif = Math.abs(objLoc.getZ() - loc.getZ());
            if (xDif < distX && yDif < distY && zDif < distZ && xDif + yDif + zDif < lowestDist) {
                object = obj;
            }
        }
        return object;
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
            if (obj.getDefiningBlocks() == null) {
                continue;
            }
            for (Block matchBlock : obj.getDefiningBlocks()) {
                if (BukkitUtility.locationMatch(matchBlock.getLocation(), loc)) {
                    return obj;
                }
            }
        }
        return null;
    }

    public GameObject getGameObjectByLocation(Location loc) {
        return getGameObjectByLocation(GameObject.class, loc);
    }
    
    public Grenade getGrenade(Entity entity) {
        for (Grenade grenade : getObjectsOfType(Grenade.class)) {
            if (grenade.getLocation() != null && grenade.getGrenadeEntity() != null && grenade.getGrenadeEntity().getUniqueId().compareTo(entity.getUniqueId()) == 0) {
                return grenade;
            }
        }
        return null;
    }

    public Hellhound getHellhound(LivingEntity e) {
        return (Hellhound) getZAMobByEntity(e);
    }

    public Teleporter getMainframe(Location loc) {
        return getGameObjectByLocation(Teleporter.class, loc);
    }

    public MobSpawner getMobSpawner(Location loc) {
        return getGameObjectByLocation(MobSpawner.class, loc);
    }

    public MysteryBox getMysteryChest(Location loc) {
        return getGameObjectByLocation(MysteryBox.class, loc);
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

    public ArrayList<MobSpawner> getSpawns(String gamename) {
        ArrayList<MobSpawner> spawners = new ArrayList<MobSpawner>();
        for (MobSpawner spawn : getObjectsOfType(MobSpawner.class)) {
            if (spawn.getGame().getName().equalsIgnoreCase(gamename)) {
                spawners.add(spawn);
            }
        }
        return spawners;
    }

    public Teleporter getTeleporter(Location loc) {
        return getGameObjectByLocation(Teleporter.class, loc);
    }

    @SuppressWarnings("unchecked") public <T extends Task> List<T> getTasksOfType(Class<T> type) {
        ArrayList<T> list = new ArrayList<T>();
        for (Task thread : getObjectsOfType(Task.class)) {
            if (thread.getClass().isInstance(type)) {
                list.add((T) thread);
            }
        }
        return list;
    }

    public Zombie getZombie(LivingEntity e) {
        return (Zombie) getZAMobByEntity(e);
    }

    public ZAMob getZAMob(LivingEntity e) {
        return getZAMobByEntity(e);
    }

    public ZAMob getZAMobByEntity(LivingEntity ent) {
        for (ZAMob mob : getObjectsOfType(ZAMob.class)) {
            if (mob.getEntity().getUniqueId().compareTo(ent.getUniqueId()) == 0) {
                return mob;
            }
        }
        return null;
    }

    public ZAPlayer getZAPlayer(Player player) {
        for (ZAPlayer zap : getObjectsOfType(ZAPlayer.class)) {
            if (zap.getPlayer().getName().equals(player.getName())) {
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
    
    public boolean isGrenade(Entity entity) {
        return getGrenade(entity) != null;
    }

    public boolean isHellhound(LivingEntity e) {
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

    public boolean isTeleporter(Location loc) {
        return getTeleporter(loc) != null;
    }

    public boolean isUndead(LivingEntity e) {
        return getZombie(e) != null;
    }

    public boolean isZAMob(LivingEntity e) {
        return getZAMob(e) != null;
    }

    public boolean isZAPlayer(Player player) {
        return getZAPlayer(player) != null;
    }

    public void setModifiableMaterials(Material[] materials) {
        modifiableMaterials = materials;
    }
}
