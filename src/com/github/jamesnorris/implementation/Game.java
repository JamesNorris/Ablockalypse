package com.github.jamesnorris.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZASound;
import com.github.jamesnorris.event.GameEndEvent;
import com.github.jamesnorris.inter.Blinkable;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.inter.ZAMob;
import com.github.jamesnorris.manager.SpawnManager;
import com.github.jamesnorris.threading.ChestFakeBeaconThread;
import com.github.jamesnorris.threading.NextLevelThread;
import com.github.jamesnorris.util.MiscUtil;

public class Game extends DataManipulator {
    private MysteryChest active;
    private ArrayList<Area> areas = new ArrayList<Area>();
    private ArrayList<Barrier> barriers = new ArrayList<Barrier>();
    private ArrayList<Blinkable> blinkable = new ArrayList<Blinkable>();
    private ArrayList<MysteryChest> chests = new ArrayList<MysteryChest>();
    private CopyOnWriteArrayList<Claymore> claymores = new CopyOnWriteArrayList<Claymore>();
    private int level, mobcount, startpoints;
    private Mainframe mainframe;
    private String name;
    private NextLevelThread nlt;
    private HashMap<String, Integer> players = new HashMap<String, Integer>();
    private Random rand;
    private ArrayList<MobSpawner> spawners = new ArrayList<MobSpawner>();
    private SpawnManager spawnManager;
    private List<Integer> wolfLevels = new ArrayList<Integer>();
    private boolean wolfRound, paused, started, friendlyFire;
    private ChestFakeBeaconThread beacons;

    /**
     * Creates a new instance of a game.
     * 
     * @param name The name of the ZAGame
     */
    @SuppressWarnings("unchecked") public Game(String name) {
        this.name = name;
        rand = new Random();
        paused = false;
        started = false;
        beacons = new ChestFakeBeaconThread(this, 200, (Boolean) Setting.BEACONS.getSetting());
        wolfLevels = (List<Integer>) Setting.WOLF_LEVELS.getSetting();
        startpoints = (Integer) Setting.STARTING_POINTS.getSetting();
        friendlyFire = (Boolean) Setting.DEFAULT_FRIENDLY_FIRE_MODE.getSetting();
        data.games.put(name, this);
    }

    public ChestFakeBeaconThread getFakeBeaconThread() {
        return beacons;
    }

    public void setFakeBeaconThread(ChestFakeBeaconThread thread) {
        beacons = thread;
    }

    /**
     * Attaches an area to this game.
     * 
     * @param ga The area to load into this game
     */
    public void addArea(Area ga) {
        if (!overlapsAnotherObject(ga.getPoint(1)) && !overlapsAnotherObject(ga.getPoint(2))) {
            areas.add(ga);
            blinkable.add(ga);
        }
    }

    /**
     * Attaches a barrier to this game.
     * 
     * @param gb The barrier to load into this game
     */
    public void addBarrier(Barrier gb) {
        if (!overlapsAnotherObject(gb.getCenter())) {
            barriers.add(gb);
            blinkable.add(gb);
        }
    }

    public void addClaymore(Claymore more) {
        claymores.add(more);
    }

    /**
     * Adds a spawner to the game
     * 
     * @param l The location to put the spawner at
     */
    public void addMobSpawner(MobSpawner l) {
        if (!overlapsAnotherObject(l.getBukkitLocation())) {
            spawners.add(l);
            blinkable.add(l);
            data.mobSpawners.put(this, l);
        }
    }

    /**
     * Adds a chest to the game.
     * 
     * @param mc The chest to add to the game
     */
    public void addMysteryChest(MysteryChest mc) {
        if (!overlapsAnotherObject(mc.getLocation())) {
            chests.add(mc);
            blinkable.add(mc);
        }
    }

    private boolean overlapsAnotherObject(Location loc) {
        for (GameObject object : getAllPermanentPhysicalObjects()) {
            if (object instanceof Area) {
                Area ga = (Area) object;
                if (MiscUtil.locationMatch(loc, ga.getPoint(1)) || MiscUtil.locationMatch(loc, ga.getPoint(2)))
                    return true;
            } else {
                for (Block block : object.getDefiningBlocks()) {
                    Location bLoc = block.getLocation();
                    if (MiscUtil.locationMatch(loc, bLoc))
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Adds a player to the game.
     * NOTE: This does not change a players' status at all, that must be done through the ZAPlayer instance.
     * 
     * @param player The player to be added to the game
     */
    public void addPlayer(Player player) {
        if (!data.players.containsKey(player)) {
            ZAPlayer zap = new ZAPlayer(player, this);
            zap.loadPlayerToGame(name);
            zap.addPoints(startpoints);
        }
        if (!players.containsKey(player.getName())) {
            players.put(player.getName(), startpoints);
            broadcast(ChatColor.RED + player.getName() + ChatColor.GRAY + " has joined the game!", player);
            if (paused)
                pause(false);
            if (!started)
                nextLevel();
        }
    }

    /**
     * Sends a message to all players in the game.
     * 
     * @param message The message to send
     * @param exception A player to be excluded from the broadcast
     */
    public void broadcast(String message, Player exception) {
        for (String name : getPlayers()) {
            Bukkit.getPlayer(name).sendMessage(message);
        }
    }

    /**
     * Sends all players in the game the points of all players.
     */
    public void broadcastPoints() {
        for (String s : getPlayers()) {
            Player p = Bukkit.getPlayer(s);
            for (String s2 : getPlayers()) {
                Player p2 = Bukkit.getPlayer(s2);
                ZAPlayer zap = data.getZAPlayer(p2);
                p.sendMessage(ChatColor.RED + s2 + ChatColor.RESET + " - " + ChatColor.GRAY + zap.getPoints());
            }
        }
    }

    /**
     * Ends the game, repairs all barriers, closes all areas, and removes all players.
     */
    public void end() {
        if (started) {
            int points = 0;
            for (int i : players.values())
                points = points + i;
            GameEndEvent GEE = new GameEndEvent(this, points);
            Bukkit.getPluginManager().callEvent(GEE);
            if (!GEE.isCancelled()) {
                paused = true;
                started = false;
                mainframe.clearLinks();
                if (nlt != null && nlt.isRunning())
                    nlt.remove();
                for (Blinkable b : blinkable)
                    if (b.getBlinkerThread() != null)
                        b.getBlinkerThread().setRunThrough(true);
                for (Undead gu : data.undead)
                    if (gu.getGame() == this)
                        gu.kill();
                for (Hellhound ghh : data.hellhounds)
                    if (ghh.getGame() == this)
                        ghh.kill();
                for (Barrier barrier : barriers)
                    barrier.replacePanels();
                for (Claymore more : claymores) {
                    more.remove();
                }
                for (String name : getPlayers()) {
                    Player player = Bukkit.getServer().getPlayer(name);
                    ZAPlayer zap = data.players.get(player);
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.GRAY + "The game has ended. You made it to level " + level);
                    ZASound.END.play(zap.getPlayer().getLocation());
                    removePlayer(player);
                }
                mobcount = 0;
            }
        }
    }

    /**
     * Checks the friendly fire mode of this game.
     * 
     * @return Whether or not friendly fire is enabled
     */
    public boolean friendlyFireEnabled() {
        return friendlyFire;
    }

    /**
     * Gets the currently active chest for this game.
     * 
     * @return The currently active chest for this game
     */
    public MysteryChest getActiveMysteryChest() {
        return active;
    }

    /**
     * Gets a list of areas connected to this game.
     * 
     * @return A list of areas in this game
     */
    public ArrayList<Area> getAreas() {
        return areas;
    }

    /**
     * Gets a list of barriers connected to this game.
     * 
     * @return A list of barriers in this game
     */
    public ArrayList<Barrier> getBarriers() {
        return barriers;
    }

    /**
     * Gets a list of claymores connected to this game.
     * 
     * @return A list of claymores in this game
     */
    public CopyOnWriteArrayList<Claymore> getClaymores() {
        return claymores;
    }

    /**
     * Gets the current level that the game is on.
     * 
     * @return The current level of the game
     */
    public int getLevel() {
        return level;
    }

    /**
     * Gets the spawn location for this game.
     * 
     * @return The location of the spawn
     */
    public Mainframe getMainframe() {
        return mainframe;
    }

    /**
     * Gets the remaining custom mobs in the game.
     * 
     * @return The amount of remaining mobs in this game
     */
    public int getMobCount() {
        return mobcount;
    }

    /**
     * Gets all mobs spawned in this game.
     * 
     * @return All mobs currently alive in this game
     */
    public ArrayList<ZAMob> getMobs() {
        return spawnManager.getLivingMobs();
    }

    /**
     * Gets all the spawners for this game.
     * 
     * @return The spawn locations as an arraylist for this game
     */
    public ArrayList<MobSpawner> getMobSpawners() {
        return spawners;
    }

    /**
     * Gets an arraylist of chests that are attached to this game.
     * 
     * @return The chests in this game
     */
    public ArrayList<MysteryChest> getMysteryChests() {
        return chests;
    }

    /**
     * Gets the name of this game.
     * 
     * @return The name of the ZAGame
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a set of players currently in the game.
     * 
     * @return A set of player names that are involved in this game
     */
    public Set<String> getPlayers() {
        return players.keySet();
    }

    /**
     * Returns a random area from this game.
     * 
     * @return The random area from this game
     */
    public Area getRandomArea() {
        if (areas != null && areas.size() >= 1)
            return areas.get(rand.nextInt(areas.size()));
        return null;
    }

    /**
     * Returns a random barrier from this game.
     * 
     * @return The random barrier from this game
     */
    public Barrier getRandomBarrier() {
        if (barriers != null && barriers.size() >= 1)
            return barriers.get(rand.nextInt(barriers.size()));
        return null;
    }

    public MysteryChest getRandomMysteryChest() {
        if (chests != null && chests.size() >= 1)
            return chests.get(rand.nextInt(chests.size()));
        return null;
    }

    /**
     * Gets a random living player.
     * Living is considered as not in limbo, last stand, respawn thread, or death.
     * 
     * @return The random living player
     */
    public Player getRandomLivingPlayer() {
        if (getRemainingPlayers() >= 1) {
            ArrayList<ZAPlayer> zaps = new ArrayList<ZAPlayer>();
            for (String s : getPlayers()) {
                Player p = Bukkit.getServer().getPlayer(s);
                ZAPlayer zap = (ZAPlayer) data.getZAPlayer(p);
                if (!p.isDead() && !zap.isInLastStand() && !zap.isInLimbo())
                    zaps.add(zap);
            }
            return zaps.get(rand.nextInt(zaps.size())).getPlayer();
        }
        return null;
    }

    /**
     * Returns a random player from this game.
     * 
     * @return The random player from this game
     */
    public Player getRandomPlayer() {
        if (getRemainingPlayers() >= 1) {
            ArrayList<Player> ps = new ArrayList<Player>();
            for (String s : getPlayers()) {
                Player p = Bukkit.getServer().getPlayer(s);
                ps.add(p);
            }
            return ps.get(rand.nextInt(ps.size())).getPlayer();
        }
        return null;
    }

    /**
     * Gets the players still in the game.
     * This only counts players that are living and not in last stand or limbo.
     * 
     * @return How many living players are in the game
     */
    public int getRemainingPlayers() {
        int i = 0;
        for (String s : getPlayers()) {
            Player p = Bukkit.getPlayer(s);
            ZAPlayer zap = data.players.get(p);
            if (!p.isDead() && !zap.isInLimbo() && !zap.isInLastStand()) {
                ++i;
            }
        }
        return i;
    }

    /**
     * Gets the manager that affects spawn for this game.
     * 
     * @return The SpawnManager instance associated with this game
     */
    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    /**
     * Returns whether or not the game has started.
     * 
     * @return Whether or not the game has been started, and mobs are spawning
     */
    public boolean hasStarted() {
        return started;
    }

    /**
     * Checks if the game is paused or not.
     * 
     * @return Whether or not the game is paused
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Returns true if the current round is a wolf round.
     * 
     * @return Whether or not the current round is a wolf round
     */
    public boolean isWolfRound() {
        return wolfRound;
    }

    /**
     * Starts the next level for the game, and adds a level to all players in this game.
     * Then, spawns a wave of zombies, and starts the thread for the next level.
     */
    public void nextLevel() {
        ++level;
        started = true;
        int maxWave = (Integer) Setting.MAX_WAVE.getSetting();
        if (level >= maxWave && maxWave != -1) {
            end();
            return;
        }
        mobcount = 0;
        if (!started) {
            level = 0;
            if (chests != null && chests.size() > 0) {
                MysteryChest mc = chests.get(rand.nextInt(chests.size()));
                setActiveMysteryChest(mc);
            }
        }
        if (level != 0) {
            for (String s : players.keySet()) {
                Player p = Bukkit.getServer().getPlayer(s);
                p.setLevel(level);
                p.sendMessage(ChatColor.BOLD + "Level " + ChatColor.RESET + ChatColor.RED + level + ChatColor.RESET + ChatColor.BOLD + " has started.");
            }
            if (level != 1)
                broadcastPoints();
        }
        if (wolfLevels != null && wolfLevels.contains(level))
            wolfRound = true;
        if (paused)
            pause(false);
        nlt = new NextLevelThread(this, true, 40);
        if (getMobCount() <= 0)
            spawnWave();
    }

    /**
     * Sets the game to pause or not.
     * 
     * @param tf Whether or not to pause or un-pause the game
     */
    public void pause(boolean tf) {
        paused = tf;
    }

    /**
     * Ends the game, removes all attached instances, and finalizes this instance.
     */
    public void remove() {
        end();
        for (GameObject object : getAllPermanentPhysicalObjects())
            object.remove();
        if (beacons != null)
            beacons.remove();
        data.games.remove(name);
    }

    /**
     * Removes an area from this game.
     * 
     * @param ga The area to be unloaded from this game
     */
    public void removeArea(Area ga) {
        areas.remove(ga);
        blinkable.remove(ga);
    }

    /**
     * Removes a barrier from this game.
     * 
     * @param gb The barrier to be unloaded from this game
     */
    public void removeBarrier(Barrier gb) {
        barriers.remove(gb);
        blinkable.remove(gb);
    }

    /**
     * Removes a claymore from this game.
     * 
     * @param more The claymore to be unloaded from this game
     */
    public void removeClaymore(Claymore more) {
        claymores.remove(more);
    }

    /**
     * Removes a mob spawner from this game.
     * 
     * @param l The mob spawner to be unloaded from this game
     */
    public void removeMobSpawner(MobSpawner l) {
        spawners.remove(l);
        blinkable.remove(l);
        data.mobSpawners.remove(this);
    }

    public void removeMysteryChest(MysteryChest mc) {
        chests.remove(mc);
        blinkable.remove(mc);
    }

    /**
     * Removes a player from the game.
     * 
     * @param player The player to be removed from the game
     */
    public void removePlayer(Player player) {
        if (players.containsKey(player.getName()))
            players.remove(player.getName());
        ZAPlayer zap = data.getZAPlayer(player);
        if (zap != null) {
            zap.removeFromGame();
            data.players.get(player).removeFromGame();
            data.players.remove(player);
            if (players.size() == 0) {
                pause(true);
                end();
            }
        }
    }

    /**
     * Sets the active chest that can be used during this game.
     * 
     * @param mc The chest to be made active
     */
    public void setActiveMysteryChest(MysteryChest mc) {
        if ((Boolean) Setting.MOVING_CHESTS.getSetting()) {
            active = mc;
            for (MysteryChest chest : chests)
                chest.setActive(false);
            if (!mc.isActive()) {
                mc.setActive(true);
                mc.setActiveUses(rand.nextInt(8) + 2);
            }
        }
    }

    /**
     * Sets the friendly fire mode for this game.
     * 
     * @param tf The new setting of friendly fire
     */
    public void setFriendlyFire(boolean tf) {
        friendlyFire = tf;
    }

    /**
     * Sets the game to the specified level.
     * 
     * @param i The level the game will be set to
     */
    public void setLevel(int i) {
        level = i;
    }

    /**
     * Sets the spawn location and central teleporter of the game.
     * 
     * @param mf The mainframe to be set for the game
     */
    public void setMainframe(Mainframe mf) {
        if (!data.mainframes.containsKey(getName())) {
            mainframe = mf;
            data.mainframes.put(getName(), mf);
        } else {
            data.mainframes.remove(getName());
            mainframe = mf;
            data.mainframes.put(getName(), mf);
        }
        if (spawnManager == null)
            spawnManager = new SpawnManager(this);
    }

    /**
     * Sets the mob count.
     * If raised higher than the count currently is, this will require more mobs spawned.
     * If lower than the count currently is, this will require more mob removals.
     * 
     * @param i The count to set the current amount of mobs to.
     */
    public void setMobCount(int i) {
        mobcount = i;
        if (mobcount < 0)
            mobcount = 0;
    }

    /**
     * Makes the game a wolf round.
     * 
     * @param tf Whether or not the game should be a wolf round
     */
    public void setWolfRound(boolean tf) {
        wolfRound = tf;
    }

    /**
     * Gamespawns a mob at the specified location.
     * 
     * @param l The location to spawn the mob at
     * @param closespawn Whether or not to spawn right next to the target
     */
    public void spawn(Location l, boolean closespawn) {
        spawnManager.spawn(l, closespawn);
    }

    /**
     * Spawns a wave of mobs around random living players in this game.
     * If barriers are present and acessible, spawns the mobs at the barriers.
     */
    public void spawnWave() {
        if (mainframe == null && getRandomLivingPlayer() != null)
            mainframe = new Mainframe(this, getRandomLivingPlayer().getLocation());
        if (spawnManager == null)
            spawnManager = new SpawnManager(this);
        spawnManager.spawnWave();
        started = true;
    }

    public ArrayList<GameObject> getAllPhysicalObjects() {
        ArrayList<GameObject> all = new ArrayList<GameObject>();
        all.addAll(areas);
        all.addAll(chests);
        all.addAll(barriers);
        all.addAll(spawners);
        all.addAll(claymores);
        return all;
    }

    public ArrayList<GameObject> getAllPermanentPhysicalObjects() {
        ArrayList<GameObject> all = new ArrayList<GameObject>();
        all.addAll(areas);
        all.addAll(chests);
        all.addAll(barriers);
        all.addAll(spawners);
        return all;
    }
}
