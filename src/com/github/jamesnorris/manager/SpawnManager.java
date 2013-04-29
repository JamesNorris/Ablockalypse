package com.github.jamesnorris.manager;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.jamesnorris.enumerated.GameEntityType;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.event.GameMobSpawnEvent;
import com.github.jamesnorris.implementation.Barrier;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.MobSpawner;
import com.github.jamesnorris.inter.ZAMob;
import com.github.jamesnorris.threading.MobSpawningThread;

public class SpawnManager {
    public ArrayList<ZAMob> mobs = new ArrayList<ZAMob>();
    public int spawnedIn = 0;
    private int amt = 0;
    private Game game;
    private Random rand;

    /**
     * Creates a new spawn manager for spawning mobs in a game.
     * This instance is used for getting a valid point to spawn mobs at,
     * and also for spawning the mob with all the game changes necessary.
     * 
     * @param game The game to spawn the mobs in
     */
    public SpawnManager(Game game) {
        this.game = game;
        rand = new Random();
    }

    public boolean allSpawnedIn() {
        return spawnedIn >= amt;
    }

    /**
     * Finds a good location to spawn a mob, relative to the location given.
     * 
     * @param l The location to relate to
     * @param max The maximum distance in blocks from the location
     * @param min The minimum distance in blocks from the location
     * @return A valid location to spawn the mob from
     */
    public Location findSpawnLocation(Location l, int max, int min) {
        int chance = rand.nextInt(4);
        World w = l.getWorld();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        int modX = rand.nextInt(max - min) + min;
        int modZ = rand.nextInt(max - min) + min;
        float pitch = l.getPitch();
        float yaw = l.getYaw();
        if (chance == 1) {
            x = x - modX;
            z = z - modZ;
        } else if (chance == 2) {
            x = x - modX;
        } else if (chance == 3) {
            z = z - modZ;
        }
        return new Location(w, x, y, z, yaw, pitch);
    }

    /**
     * Spawns the specified entity, and makes it into a ZAMob.
     * 
     * @param l The location to spawn from
     * @param et The EntityType to spawn
     */
    public void gameSpawn(Location l, EntityType et) {
        Entity e = l.getWorld().spawnEntity(l, et);
        GameEntityType type = GameEntityType.translate(et);
        GameMobSpawnEvent gmse = new GameMobSpawnEvent(e, game, type);
        Bukkit.getServer().getPluginManager().callEvent(gmse);
        if (!gmse.isCancelled()) {
            type.instantiate(e, game);
        } else {
            e.remove();
        }
    }

    /**
     * Gets the barrier closest to the location.
     * 
     * @param loc The location to check for
     * @return The closest barrier
     */
    public Barrier getClosestBarrier(Location loc) {
        double distance = Double.MAX_VALUE;
        Barrier lp = null;// low priority
        Barrier hp = null;// high priority
        for (Barrier gb : game.getObjectsOfType(Barrier.class)) {
            Location l = gb.getCenter();
            double current = loc.distance(l);
            if (current < distance) {
                distance = current;
                lp = gb;
                if (pathIsClear(loc, l)) {
                    hp = gb;
                }
            }
        }
        return hp != null ? hp : lp;
    }

    /**
     * Gets the barrier closest to the player.
     * 
     * @param p The player to check for
     * @return The closest barrier
     */
    public Barrier getClosestBarrier(Player p) {
        return getClosestBarrier(p.getLocation());
    }

    /**
     * Gets the closest spawner to the location.
     * 
     * @param loc The location to check for
     * @return The closest spawner
     */
    public MobSpawner getClosestSpawner(Location loc) {
        double distance = Double.MAX_VALUE;
        MobSpawner lp = null;// low priority
        MobSpawner hp = null;// high priority
        for (MobSpawner l1 : game.getObjectsOfType(MobSpawner.class)) {
            Location l = l1.getBukkitLocation();
            double current = loc.distance(l);
            if (current < distance) {
                distance = current;
                lp = l1;
                if (pathIsClear(loc, l)) {
                    hp = l1;
                }
            }
        }
        return hp != null ? hp : lp;
    }

    /**
     * Gets the closest spawner to the player.
     * 
     * @param p The player to check for
     * @return The closest spawner
     */
    public MobSpawner getClosestSpawner(Player p) {
        return getClosestSpawner(p.getLocation());
    }

    /**
     * Gets the amount of spawns in the current level of the game.
     * 
     * @return The amount of spawns in the current level
     */
    public int getCurrentSpawnAmount() {
        double m = 1.2;
        double x = game.getLevel();
        double b = game.getPlayers().size();
        int amt = (int) Math.round(m * x + b);
        if (game.isWolfRound()) {
            amt = amt / 3;
        }
        return amt;
    }

    /**
     * Gets all living mobs that have been spawned through this manager.
     * 
     * @return All mobs spawned by this manager, in an arraylist
     */
    public ArrayList<ZAMob> getLivingMobs() {
        return mobs;
    }

    /**
     * Gets the amount of spawns in the given level, with the given amount of players.
     * 
     * @param level The level you are looking for
     * @param playeramt The amount of players you are looking for
     * @param wolfround Whether or not the game is a wolf round or not
     * @return The amount of spawns in this hypathetical level
     */
    public int getSpawnAmount(int level, int playeramt, boolean wolfround) {
        double m = 1.2;
        double x = level;
        double b = playeramt;
        int amt = (int) Math.round(m * x + b);
        if (wolfround) {
            amt = amt / 3;
        }
        return amt;
    }

    /**
     * @deprecated Only checks for a direct path
     * 
     * Checks that the blocks from the one location to the next in the distance are empty.
     * 
     * @param start The starting location
     * @param end The ending location
     * @param distance The distance from the start to end
     * @return Whether or not all blocks from start to end are empty
     */
    @Deprecated public boolean pathIsClear(Location start, Location end) {// TODO get this working
        return false;
    }

    /**
     * Gamespawns a mob at the specified location.
     * 
     * @param l The location to spawn the mob at
     * @param closespawn Whether or not to spawn right next to the target
     */
    public void spawn(Location l, boolean closespawn) {
        if (!closespawn) {
            l = findSpawnLocation(l, 7, 4);
        }
        gameSpawn(l, game.isWolfRound() ? EntityType.WOLF : EntityType.ZOMBIE);
    }

    /**
     * Spawns a wave of mobs around random living players in this game.
     * If barriers are present and accessible, spawns the mobs at the barriers.
     * If mob spawners are set in the game and are accessible, spawns the mobs at a mob spawner.
     * This will only spawn mobs if the game has a mob count of 0.
     */
    public void spawnWave() {
        amt = getCurrentSpawnAmount();
        spawnedIn = 0;
        if ((Boolean) Setting.DEBUG.getSetting()) {
            System.out.println("[Ablockalypse] [DEBUG] Amount of zombies in this wave: (" + game.getName() + ") " + amt);
        }
        if (game.getRemainingPlayers() >= 1 && game.getMobCount() <= 0) {
            for (int i = 0; i <= amt; i++) {
                new MobSpawningThread(this, game, i * 80);
            }
        } else {
            game.end();
        }
    }
}
