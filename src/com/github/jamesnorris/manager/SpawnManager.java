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
import com.github.jamesnorris.implementation.MobSpawner;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.inter.ZAMob;
import com.github.jamesnorris.threading.MobSpawningThread;
import com.github.jamesnorris.util.MathAssist;

public class SpawnManager {
    private Game game;
    public ArrayList<ZAMob> mobs = new ArrayList<ZAMob>();
    private Random rand;
    private int amt = 0, threadsQueued = 0;

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
        } else if (chance == 2)
            x = x - modX;
        else if (chance == 3)
            z = z - modZ;
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
        int distance = Integer.MAX_VALUE;
        Barrier lp = null;// low priority
        Barrier hp = null;// high priority
        for (Barrier gb : game.getBarriers()) {
            Location l = gb.getCenter();
            int current = (int) MathAssist.distance(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
            if (current < distance) {
                distance = current;
                lp = gb;
                if (pathIsClear(loc, l, current))
                    hp = gb;
            }
        }
        return (hp != null) ? hp : lp;
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
        int distance = Integer.MAX_VALUE;
        MobSpawner lp = null;// low priority
        MobSpawner hp = null;// high priority
        for (MobSpawner l1 : game.getMobSpawners()) {
            Location l = l1.getBukkitLocation();
            int current = (int) MathAssist.distance(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
            if (current < distance) {
                distance = current;
                lp = l1;
                if (pathIsClear(loc, l, current))
                    hp = l1;
            }
        }
        return (hp != null) ? hp : lp;
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
        int amt = (int) Math.round(MathAssist.line(m, x, b));
        if (game.isWolfRound())
            amt = amt / 3;
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
        int amt = (int) Math.round(MathAssist.line(m, x, b));
        if (wolfround)
            amt = amt / 3;
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
    @Deprecated public boolean pathIsClear(Location start, Location end, int distance) {// TODO get this working
        // ArrayList<Block> blocks = new ArrayList<Block>();
        // World w = start.getWorld();
        // for (int i2 = 1; i2 <= distance; i2++) {
        // int pX = start.getBlockX();
        // int pY = start.getBlockY();
        // int pZ = start.getBlockZ();
        // int eX = end.getBlockX();
        // int eY = end.getBlockY();
        // int eZ = end.getBlockZ();
        // int movX = eX - i2;
        // int movY = eY;
        // int movZ = eZ - i2;
        // if ((eX - pX) < 0)
        // movX = eX + i2;
        // if ((eZ - pZ) < 0)
        // movZ = eZ + i2;
        // Block block = w.getBlockAt(movX, movY, movZ);
        // Block block2 = block.getLocation().add(0, 1, 0).getBlock();
        // Block block3 = block.getLocation().subtract(0, 1, 0).getBlock();
        // if ((!block.isEmpty() && block2.isEmpty() && pY > eY))
        // block = block2;
        // if ((!block.isEmpty() && block3.isEmpty() && pY < eY))
        // block = block3;
        // blocks.add(block);
        // }
        // int size = blocks.size();
        // int known = 0;
        // for (Block b2 : blocks)
        // if (b2.isEmpty())
        // ++known;
        // return known == size;
        return false;
    }

    /**
     * Gamespawns a mob at the specified location.
     * 
     * @param l The location to spawn the mob at
     * @param closespawn Whether or not to spawn right next to the target
     */
    public void spawn(Location l, boolean closespawn) {
        if (!closespawn)
            l = findSpawnLocation(l, 7, 4);
        gameSpawn(l, (game.isWolfRound()) ? EntityType.WOLF : EntityType.ZOMBIE);
    }
    
    public boolean allSpawnedIn() {
        return threadsQueued >= amt;
    }

    /**
     * Spawns a wave of mobs around random living players in this game.
     * If barriers are present and accessible, spawns the mobs at the barriers.
     * If mob spawners are set in the game and are accessible, spawns the mobs at a mob spawner.
     * This will only spawn mobs if the game has a mob count of 0.
     */
    public void spawnWave() {
        threadsQueued = 0;
        amt = getCurrentSpawnAmount();
        if ((Boolean) Setting.DEBUG.getSetting())
            System.out.println("[Ablockalypse] [DEBUG] Amount of zombies in this wave: (" + game.getName() + ") " + amt);
        if (game.getRemainingPlayers() >= 1 && game.getMobCount() <= 0) {
            for (int i = 0; i <= amt; i++) {
                ++threadsQueued;
                new MobSpawningThread(this, game, (i * 80 /* the delay between each mob is 4 seconds */));
            }
        } else {
            game.end();
        }
    }
}
