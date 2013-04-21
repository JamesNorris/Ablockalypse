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
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.github.jamesnorris.DataContainer;
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

public class Game {
    private MysteryChest active;
    private CopyOnWriteArrayList<GameObject> objects = new CopyOnWriteArrayList<GameObject>();
    private int level, mobcount, startpoints;
    private Mainframe mainframe;
    private String name;
    private NextLevelThread nlt;
    private HashMap<String, Integer> players = new HashMap<String, Integer>();
    private Random rand;
    private SpawnManager spawnManager;
    private List<Integer> wolfLevels = new ArrayList<Integer>();
    private boolean wolfRound, paused, started;
    private ChestFakeBeaconThread beacons;
    private Scoreboard scoreBoard;
    private Team team;
    private DataContainer data = DataContainer.data;

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
        scoreBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        team = scoreBoard.registerNewTeam("Ablockalypse");
        team.setCanSeeFriendlyInvisibles(true);
        team.setAllowFriendlyFire((Boolean) Setting.DEFAULT_FRIENDLY_FIRE_MODE.getSetting());       
        Objective objective = scoreBoard.registerNewObjective("showhealth", "health");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.setDisplayName("/ 20");
        scoreBoard.registerNewObjective("points", "dummy").setDisplaySlot(DisplaySlot.SIDEBAR);//getScoreboard().getObjective("points") - to modify player points
        data.games.put(name, this);
    }
    
    public Scoreboard getScoreboard() {
        return scoreBoard;
    }
    
    public void setScoreboard(Scoreboard board) {
        this.scoreBoard = board;
    }
    
    public Team getTeam() {
        return team;
    }
    
    public void setTeam(Team team) {
        this.team = team;
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
    public void addObject(GameObject obj) {
        if (!overlapsAnotherObject(obj) && !objects.contains(obj)) {
            objects.add(obj);
        }
    }

    private boolean overlapsAnotherObject(GameObject obj) {
        for (GameObject object : getAllPhysicalObjects()) {
            for (Block block : object.getDefiningBlocks()) {
                Location bLoc = block.getLocation();
                for (Block otherBlock : obj.getDefiningBlocks()) {
                    if (MiscUtil.locationMatch(otherBlock.getLocation(), bLoc))
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
                for (Blinkable b : getObjectsOfType(Blinkable.class)) {
                    if (b.getBlinkerThread() != null) {
                        b.getBlinkerThread().setRunThrough(true);
                    }
                }
                for (Undead gu : data.undead)
                    if (gu.getGame() == this)
                        gu.kill();
                for (Hellhound ghh : data.hellhounds)
                    if (ghh.getGame() == this)
                        ghh.kill();
                for (Barrier barrier : getObjectsOfType(Barrier.class))
                    barrier.replacePanels();
                for (Claymore more : getObjectsOfType(Claymore.class)) {
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
     * Gets the currently active chest for this game.
     * 
     * @return The currently active chest for this game
     */
    public MysteryChest getActiveMysteryChest() {
        return active;
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
            List<MysteryChest> chests = getObjectsOfType(MysteryChest.class);
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
        for (GameObject object : getAllPhysicalObjects())
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
    public void removeObject(GameObject obj) {
        if (objects.contains(obj)) {
            objects.remove(obj);
        }
    }

    @SuppressWarnings("unchecked") public <T extends Object> List<T> getObjectsOfType(Class<T> type) {
        ArrayList<T> list = new ArrayList<T>();
        for (Object obj : objects) {
            if (obj.getClass().isInstance(type)) {
                list.add((T) obj);
            }
        }
        return list;
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
            for (MysteryChest chest : getObjectsOfType(MysteryChest.class))
                chest.setActive(false);
            if (!mc.isActive()) {
                mc.setActive(true);
                mc.setActiveUses(rand.nextInt(8) + 2);
            }
        }
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
        if (data.mainframes.containsKey(getName())) {
            data.mainframes.remove(getName());
        }
        mainframe = mf;
        data.mainframes.put(getName(), mf);
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

    public CopyOnWriteArrayList<GameObject> getAllPhysicalObjects() {
        return objects;
    }
}
