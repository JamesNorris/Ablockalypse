package com.github.JamesNorris.Implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.MessageTransfer;
import com.github.JamesNorris.Enumerated.MessageDirection;
import com.github.JamesNorris.Enumerated.Setting;
import com.github.JamesNorris.Enumerated.ZASound;
import com.github.JamesNorris.Event.GameEndEvent;
import com.github.JamesNorris.Interface.Blinkable;
import com.github.JamesNorris.Interface.GameObject;
import com.github.JamesNorris.Interface.MysteryChest;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Manager.SpawnManager;
import com.github.JamesNorris.Threading.NextLevelThread;
import com.github.JamesNorris.Util.MiscUtil;
import com.github.JamesNorris.Util.SoundUtil;
import com.github.JamesNorris.Util.SpecificMessage;

public class ZAGameBase extends DataManipulator implements ZAGame {
    private MysteryChest active;
    private ArrayList<GameArea> areas = new ArrayList<GameArea>();
    private ArrayList<GameBarrier> barriers = new ArrayList<GameBarrier>();
    private ArrayList<Blinkable> blinkable = new ArrayList<Blinkable>();
    private ArrayList<MysteryChest> chests = new ArrayList<MysteryChest>();
    private int level, mobcount, startpoints;
    private Location mainframe;
    private String name;
    private NextLevelThread nlt;
    private HashMap<String, Integer> players = new HashMap<String, Integer>();
    private Random rand;
    private ArrayList<GameMobSpawner> spawners = new ArrayList<GameMobSpawner>();
    private SpawnManager spawnManager;
    private List<Integer> wolfLevels = new ArrayList<Integer>();
    private boolean wolfRound, paused, started, friendlyFire;

    /**
     * Creates a new instance of a game.
     * 
     * @param name The name of the ZAGame
     * @param cd The ConfigurationData instance used
     * @param spawners Whether or not spawners should be loaded automatically
     */
    @SuppressWarnings("unchecked") public ZAGameBase(String name) {
        this.name = name;
        rand = new Random();
        paused = false;
        started = false;
        wolfLevels = (List<Integer>) Setting.WOLFLEVELS.getSetting();
        startpoints = (Integer) Setting.STARTINGPOINTS.getSetting();
        friendlyFire = (Boolean) Setting.DEFAULTFRIENDLYFIREMODE.getSetting();
        data.games.put(name, this);
    }

    /**
     * Attaches an area to this game.
     */
    @Override public void addArea(GameArea ga) {
        areas.add(ga);
        blinkable.add(ga);
    }

    /**
     * Attaches a barrier to this game.
     */
    @Override public void addBarrier(GameBarrier gb) {
        barriers.add(gb);
        blinkable.add(gb);
    }

    /**
     * Adds a spawner to the game
     * 
     * @param l The location to put the spawner at
     */
    @Override public void addMobSpawner(GameMobSpawner l) {
        spawners.add(l);
        blinkable.add(l);
        data.spawns.put(this, l);
    }

    /**
     * Adds a chest to the game.
     * 
     * @param mc The chest to add to the game
     */
    @Override public void addMysteryChest(MysteryChest mc) {
        chests.add(mc);
        blinkable.add(mc);
    }

    /**
     * Adds a player to the game.
     * NOTE: This does not change a players' status at all, that must be done through the ZAPlayer instance.
     * 
     * @param player The player to be added to the game
     */
    @Override public void addPlayer(Player player) {
        if (!data.players.containsKey(player)) {
            ZAPlayerBase zap = new ZAPlayerBase(player, this);
            zap.loadPlayerToGame(name);
            zap.addPoints(startpoints);
        }
        players.put(player.getName(), startpoints);
        broadcast(ChatColor.RED + player.getName() + ChatColor.GRAY + " has joined the game!", player);
        if (paused)
            pause(false);
        if (!started)
            nextLevel();
    }

    /**
     * Sends a message to all players in the game.
     * 
     * @param message The message to send
     * @param exception A player to be excluded from the broadcast
     */
    @Override public void broadcast(String message, Player exception) {
        SpecificMessage sm = new SpecificMessage(MessageDirection.PLAYER_BROADCAST, message);
        sm.setExceptionBased(true);
        sm.addException(exception.getName());
        MessageTransfer.sendMessage(sm);
    }

    /**
     * Sends all players in the game the points of all players.
     */
    @Override public void broadcastPoints() {
        for (String s : getPlayers()) {
            Player p = Bukkit.getPlayer(s);
            for (String s2 : getPlayers()) {
                Player p2 = Bukkit.getPlayer(s2);
                ZAPlayer zap = data.getZAPlayer(p2);
                MiscUtil.sendPlayerMessage(p, ChatColor.RED + s2 + ChatColor.RESET + " - " + ChatColor.GRAY + zap.getPoints());
            }
        }
    }

    /**
     * Ends the game, repairs all barriers, closes all areas, and removes all players.
     */
    @Override public void end() {
        if (started) {
            int points = 0;
            for (int i : players.values())
                points = points + i;
            GameEndEvent GEE = new GameEndEvent(this, points);
            Bukkit.getPluginManager().callEvent(GEE);
            if (!GEE.isCancelled()) {
                paused = true;
                started = false;
                mobcount = 0;
                if (nlt != null && nlt.isRunning())
                    nlt.remove();
                for (Blinkable b : blinkable)
                    if (b.getBlinkerThread() != null)
                        b.getBlinkerThread().setRunThrough(true);
                for (GameUndead gu : data.undead)
                    if (gu.getGame() == this)
                        gu.kill();
                for (GameHellHound ghh : data.hellhounds)
                    if (ghh.getGame() == this)
                        ghh.kill();
                for (String name : getPlayers()) {
                    Player player = Bukkit.getServer().getPlayer(name);
                    ZAPlayerBase zap = data.players.get(player);
                    MiscUtil.sendPlayerMessage(player, ChatColor.BOLD + "" + ChatColor.GRAY + "The game has ended. You made it to level " + level);
                    SoundUtil.generateSound(zap.getPlayer(), ZASound.END);
                    removePlayer(player);
                }
            }
        }
    }

    /**
     * Checks the friendly fire mode of this game.
     * 
     * @return Whether or not friendly fire is enabled
     */
    @Override public boolean friendlyFireEnabled() {
        return friendlyFire;
    }

    /**
     * Gets the currently active chest for this game.
     * 
     * @return The currently active chest for this game
     */
    @Override public MysteryChest getActiveMysteryChest() {
        return active;
    }

    /**
     * Gets a list of areas connected to this game.
     * 
     * @return A list of areas in this game
     */
    @Override public ArrayList<GameArea> getAreas() {
        return areas;
    }

    /**
     * Gets a list of barriers connected to this game.
     * 
     * @return A list of barriers in this game
     */
    @Override public ArrayList<GameBarrier> getBarriers() {
        return barriers;
    }

    /**
     * Gets the current level that the game is on.
     * 
     * @return The current level of the game
     */
    @Override public int getLevel() {
        return level;
    }

    /**
     * Gets the spawn location for this game.
     * 
     * @return The location of the spawn
     */
    @Override public Location getMainframe() {
        return mainframe;
    }

    /**
     * Gets the remaining custom mobs in the game.
     * 
     * @return The amount of remaining mobs in this game
     */
    @Override public int getMobCount() {
        return mobcount;
    }

    /**
     * Gets all mobs spawned in this game.
     * 
     * @return All mobs currently alive in this game
     */
    @Override public ArrayList<ZAMob> getMobs() {
        return spawnManager.getLivingMobs();
    }

    /**
     * Gets all the spawners for this game.
     * 
     * @return The spawn locations as an arraylist for this game
     */
    @Override public ArrayList<GameMobSpawner> getMobSpawners() {
        return spawners;
    }

    /**
     * Gets an arraylist of chests that are attached to this game.
     * 
     * @return The chests in this game
     */
    @Override public ArrayList<MysteryChest> getMysteryChests() {
        return chests;
    }

    /**
     * Gets the name of this game.
     * 
     * @return The name of the ZAGame
     */
    @Override public String getName() {
        return name;
    }

    /**
     * Returns a set of players currently in the game.
     */
    @Override public Set<String> getPlayers() {
        return players.keySet();
    }

    /**
     * Returns a random area from this game.
     * 
     * @return The random area from this game
     */
    @Override public GameArea getRandomArea() {
        if (areas != null && areas.size() >= 1)
            return areas.get(rand.nextInt(areas.size()));
        return null;
    }

    /**
     * Returns a random barrier from this game.
     * 
     * @return The random barrier from this game
     */
    @Override public GameBarrier getRandomBarrier() {
        if (barriers != null && barriers.size() >= 1)
            return barriers.get(rand.nextInt(barriers.size()));
        return null;
    }

    /**
     * Gets a random living player.
     * Living is considered as not in limbo, last stand, respawn thread, or death.
     * 
     * @return The random living player
     */
    @Override public Player getRandomLivingPlayer() {
        if (getRemainingPlayers() >= 1) {
            ArrayList<ZAPlayerBase> zaps = new ArrayList<ZAPlayerBase>();
            for (String s : getPlayers()) {
                Player p = Bukkit.getServer().getPlayer(s);
                ZAPlayerBase zap = (ZAPlayerBase) data.getZAPlayer(p);
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
    @Override public Player getRandomPlayer() {
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
     * 
     * @return How many players are in the game
     */
    @Override public int getRemainingPlayers() {
        int i = 0;
        for (String s : getPlayers()) {
            Player p = Bukkit.getPlayer(s);
            if (!p.isDead() && !data.players.get(p).isInLimbo() && !data.players.get(p).isInLastStand())
                ++i;
        }
        return i;
    }

    /**
     * Gets the manager that affects spawn for this game.
     * 
     * @return The SpawnManager instance associated with this game
     */
    @Override public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    /**
     * Returns whether or not the game has started.
     * 
     * @return Whether or not the game has been started, and mobs are spawning
     */
    @Override public boolean hasStarted() {
        return started;
    }

    /**
     * Checks if the game is paused or not.
     * 
     * @return Whether or not the game is paused
     */
    @Override public boolean isPaused() {
        return paused;
    }

    /**
     * Returns true if the current round is a wolf round.
     * 
     * @return Whether or not the current round is a wolf round
     */
    @Override public boolean isWolfRound() {
        return wolfRound;
    }

    /**
     * Starts the next level for the game, and adds a level to all players in this game.
     * Then, spawns a wave of zombies, and starts the thread for the next level.
     */
    @Override public void nextLevel() {
        ++level;
        mobcount = 0;
        if (!started) {
            level = 0;
            if (chests != null && chests.size() > 0) {
                MysteryChest mc = chests.get(rand.nextInt(chests.size()));
                setActiveMysteryChest(mc);
            }
        }
        if (data.gameLevels.containsKey(getName()))
            data.gameLevels.remove(getName());
        data.gameLevels.put(getName(), level);
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
        if (paused == true)
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
    @Override public void pause(boolean tf) {
        paused = tf;
    }

    /**
     * Ends the game, removes all attached instances, and finalizes this instance.
     */
    @Override public void remove() {
        end();
        data.games.remove(name);
    }

    /**
     * Removes an area from this game.
     */
    @Override public void removeArea(GameArea ga) {
        areas.remove(ga);
        blinkable.remove(ga);
    }

    /**
     * Removes a barrier from this game.
     */
    @Override public void removeBarrier(GameBarrier gb) {
        barriers.remove(gb);
        blinkable.remove(gb);
    }

    /**
     * Removes a mob spawner from this game.
     */
    @Override public void removeMobSpawner(GameMobSpawner l) {
        spawners.remove(l);
        blinkable.remove(l);
        data.spawns.remove(this);
    }

    @Override public void removeMysteryChest(MysteryChest mc) {
        chests.remove(mc);
        blinkable.remove(mc);
    }

    /**
     * Removes a player from the game.
     * 
     * @param player The player to be removed from the game
     */
    @Override public void removePlayer(Player player) {
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
    @Override public void setActiveMysteryChest(MysteryChest mc) {
        if ((Boolean) Setting.MOVINGCHESTS.getSetting()) {
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
    @Override public void setFriendlyFire(boolean tf) {
        friendlyFire = tf;
    }

    /**
     * Sets the game to the specified level.
     * 
     * @param i The level the game will be set to
     */
    @Override public void setLevel(int i) {
        level = i;
    }

    /**
     * Sets the spawn location of the game.
     * 
     * @param location The location to be made into the spawn
     */
    @Override public void setMainframe(Location location) {
        if (!data.mainframes.containsValue(location)) {
            mainframe = location;
            data.mainframes.put(getName(), location);
        } else {
            data.mainframes.remove(location);
            mainframe = location;
            data.mainframes.put(getName(), location);
        }
        if (spawnManager == null || spawnManager.getWorld() != location.getWorld())
            spawnManager = new SpawnManager(this, location.getWorld());
    }

    /**
     * Sets the mob count.
     */
    @Override public void setMobCount(int i) {
        mobcount = i;
    }

    /**
     * Sets the remaining custom mobs in the game.
     * 
     * @param i The amount to be set to
     */
    @Override public void setRemainingMobs(int i) {
        mobcount = i;
    }

    /**
     * Makes the game a wolf round.
     * 
     * @param tf Whether or not the game should be a wolf round
     */
    @Override public void setWolfRound(boolean tf) {
        wolfRound = tf;
    }

    /**
     * Gamespawns a mob at the specified location.
     * 
     * @param l The location to spawn the mob at
     * @param closespawn Whether or not to spawn right next to the target
     */
    @Override public void spawn(Location l, boolean closespawn) {
        spawnManager.spawn(l, closespawn);
    }

    /**
     * Spawns a wave of mobs around random living players in this game.
     * If barriers are present and acessible, spawns the mobs at the barriers.
     */
    @Override public void spawnWave() {
        if (mainframe == null && getRandomLivingPlayer() != null)
            mainframe = getRandomLivingPlayer().getLocation();
        if (spawnManager == null || spawnManager.getWorld() != mainframe.getWorld())
            spawnManager = new SpawnManager(this, mainframe.getWorld());
        spawnManager.spawnWave();
        started = true;
    }

    @Override public ArrayList<GameObject> getAllPhysicalObjects() {
        ArrayList<GameObject> all = new ArrayList<GameObject>();
        all.addAll(areas);
        all.addAll(chests);
        all.addAll(barriers);
        all.addAll(spawners);
        return all;
    }
}
