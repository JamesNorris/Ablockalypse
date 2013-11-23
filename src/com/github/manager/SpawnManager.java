package com.github.manager;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.aspect.block.Barrier;
import com.github.aspect.block.MobSpawner;
import com.github.aspect.entity.Hellhound;
import com.github.aspect.entity.ZAMob;
import com.github.aspect.entity.Zombie;
import com.github.aspect.intelligent.Game;
import com.github.enumerated.Setting;
import com.github.threading.inherent.MobSpawningTask;
import com.github.utility.BukkitUtility;
import com.github.utility.Pathfinder;

public class SpawnManager {
    private static Random random = new Random();

    /**
     * Checks if all of the mobs of a game are spawned in to the game for this round.
     * This does not check if they are all alive, just if they have spawned.
     * 
     * @param game The game to check the mobs for
     * @return Whether or not all mobs have been spawned for this round
     */
    public static boolean allSpawnedIn(Game game) {
        return game.getMobCountSpawnedInThisRound() >= getCurrentSpawnAmount(game);
    }

    /**
     * Gets the barrier closest to the location.
     * 
     * @param loc The location to check for
     * @return The closest barrier
     */
    public static Barrier getClosestBarrier(Game game, Location loc) {
        double distanceSquared = Double.MAX_VALUE;
        Barrier lp = null;// low priority
        Barrier hp = null;// high priority
        for (Barrier gb : game.getObjectsOfType(Barrier.class)) {
            Location l = gb.getCenter();
            double current = loc.distanceSquared(l);
            if (current < distanceSquared) {
                distanceSquared = current;
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
    public static Barrier getClosestBarrier(Game game, Player p) {
        return getClosestBarrier(game, p.getLocation());
    }

    /**
     * Gets the closest spawner to the location.
     * 
     * @param loc The location to check for
     * @return The closest spawner
     */
    public static MobSpawner getClosestSpawner(Game game, Location loc, boolean active) {
        double distanceSquared = Double.MAX_VALUE;
        MobSpawner lp = null;// low priority
        MobSpawner hp = null;// high priority
        for (MobSpawner l1 : game.getObjectsOfType(MobSpawner.class)) {
            Location l = l1.getBukkitLocation();
            double current = loc.distanceSquared(l);
            if (current < distanceSquared && (!active || l1.isActive())) {
                distanceSquared = current;
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
    public static MobSpawner getClosestSpawner(Game game, Player p, boolean active) {
        return getClosestSpawner(game, p.getLocation(), active);
    }

    /**
     * Gets the amount of spawns in the current level of the game.
     * 
     * @return The amount of spawns in the current level
     */
    public static int getCurrentSpawnAmount(Game game) {
        return getSpawnAmount(game.getLevel(), game.getPlayers().size(), game.isWolfRound());
    }

    /**
     * Gets the amount of spawns in the given level, with the given amount of players.
     * 
     * @param level The level you are looking for
     * @param playeramt The amount of players you are looking for
     * @param wolfround Whether or not the game is a wolf round or not
     * @return The amount of spawns in this hypathetical level
     */
    public static int getSpawnAmount(int level, int playeramt, boolean wolfround) {
        /* The mob spawn amount equation for the entire game */
        return (int) ((wolfround ? 1 / 3 : 1) * (Math.sqrt(10 * level) + Math.sqrt(level) * playeramt));
    }

    /**
     * Checks that the blocks from the one location to the next in the distance are empty.
     * 
     * @param start The starting location
     * @param end The ending location
     * @param distance The distance from the start to end
     * @return Whether or not all blocks from start to end are empty
     */
    public static boolean pathIsClear(Location start, Location end) {
        return Pathfinder.pathReaches(Pathfinder.calculate(start, end), end, 2);
    }

    /**
     * Gamespawns a mob at the specified location.
     * 
     * @param game The game to spawn the mob in
     * @param loc The location to spawn the mob at
     * @param exactLocation Whether or not to spawn right next to the target, or to find a closeby location.
     * @param percentage The percent chance of a hellhound spawning
     */
    public static ZAMob spawn(Game game, Location loc, boolean exactLocation, int percentage) {
        return spawn(game, exactLocation ? loc : BukkitUtility.getNearbyLocation(loc, 10, 20, 0, 0, 10, 20), percentage);
    }

    public static ZAMob spawn(Game game, Location loc, int percentage) {
        if (random.nextInt(99) + 1 <= percentage) {
            return new Hellhound(loc.getWorld(), loc.getWorld().spawnEntity(loc, EntityType.WOLF).getUniqueId(), game);
        }
        return new Zombie(loc.getWorld(), loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE).getUniqueId(), game);
    }

    /**
     * Spawns a wave of mobs around random living players in this game.
     * If barriers are present and accessible, spawns the mobs at the barriers.
     * If mob spawners are set in the game and are accessible, spawns the mobs at a mob spawner.
     * This will only spawn mobs if the game has a mob count of 0.
     */
    public static void spawnWave(Game game) {
        spawnWave(game, getCurrentSpawnAmount(game));
    }

    public static void spawnWave(Game game, int amt) {
        if (game.getRemainingPlayers().size() < 1 && game.getMobCount() <= 0) {
            game.end(true);
            return;
        }
        if ((Boolean) Setting.DEBUG.getSetting()) {
            System.out.println("[Ablockalypse] [DEBUG] Amount of zombies in this wave: (" + game.getName() + ") " + amt);
        }
        for (int i = 1; i <= amt; i++) {
            new MobSpawningTask(game, i * 80, true);
        }
    }
}
