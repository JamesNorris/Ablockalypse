package com.github.jamesnorris.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZASound;
import com.github.jamesnorris.event.GameEndEvent;
import com.github.jamesnorris.event.bukkit.PlayerJoin;
import com.github.jamesnorris.implementation.serialized.SerialGame;
import com.github.jamesnorris.inter.Blinkable;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.inter.Permadata;
import com.github.jamesnorris.inter.Permadatable;
import com.github.jamesnorris.inter.ZAMob;
import com.github.jamesnorris.manager.SpawnManager;
import com.github.jamesnorris.threading.ChestFakeBeaconThread;
import com.github.jamesnorris.threading.NextLevelThread;
import com.github.jamesnorris.util.MiscUtil;

public class Game implements Permadatable {
    private MysteryChest active;
    private ChestFakeBeaconThread beacons;
    private DataContainer data = Ablockalypse.getData();
    private int level = 1, mobcount, startpoints, spawnedInThisRound = 0;
    private Mainframe mainframe;
    private String name;
    private NextLevelThread nlt;
    private CopyOnWriteArrayList<GameObject> objects = new CopyOnWriteArrayList<GameObject>();
    private ArrayList<Integer> wolfLevels = new ArrayList<Integer>();
    private boolean wolfRound, paused, started;
    private Random rand = new Random();

    /**
     * Creates a new instance of a game.
     * 
     * @param name The name of the ZAGame
     */
    @SuppressWarnings("unchecked") public Game(String name) {
        this.name = name;
        paused = false;
        started = false;
        beacons = new ChestFakeBeaconThread(this, 200, (Boolean) Setting.BEACONS.getSetting());
        wolfLevels = (ArrayList<Integer>) Setting.WOLF_LEVELS.getSetting();
        startpoints = (Integer) Setting.STARTING_POINTS.getSetting();
        data.games.put(name, this);
    }

    public int getMobCountSpawnedInThisRound() {
        return spawnedInThisRound;
    }

    public void setMobCountSpawnedInThisRound(int amt) {
        spawnedInThisRound = amt;
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

    /**
     * Adds a player to the game.
     * NOTE: This does not change a players' status at all, that must be done through the ZAPlayer instance.
     * 
     * @param player The player to be added to the game
     */
    public void addPlayer(Player player) {
        ZAPlayer zap = data.getZAPlayer(player, name, true);
        if (!player.isOnline()) {
            PlayerJoin.offlinePlayers.add(zap);
        }
        if (!data.players.containsKey(player)) {
            zap.setPoints(startpoints);
        }
        if (!getObjectsOfType(ZAPlayer.class).contains(zap)) {
            broadcast(ChatColor.RED + player.getName() + ChatColor.GRAY + " has joined the game!", player);
            if (paused) {
                pause(false);
            }
            if (!started) {
                nextLevel();
            }
        }
    }

    /**
     * Sends a message to all players in the game.
     * 
     * @param message The message to send
     * @param exception A player to be excluded from the broadcast
     */
    public void broadcast(String message, Player... exceptions) {
        for (ZAPlayer zap : getPlayers()) {
            boolean contained = false;
            if (exceptions != null) {
                for (Player except : exceptions) {
                    if (zap.getPlayer() == except) {
                        contained = true;
                    }
                }
            }
            if (!contained) {
                zap.getPlayer().sendMessage(message);
            }
        }
    }

    public void broadcastSound(ZASound sound, Player... exceptions) {
        for (ZAPlayer zap : getPlayers()) {
            boolean contained = false;
            if (exceptions != null) {
                for (Player except : exceptions) {
                    if (zap.getPlayer() == except) {
                        contained = true;
                    }
                }
            }
            if (!contained) {
                sound.play(zap.getPlayer().getLocation());
            }
        }
    }

    /**
     * Sends all players in the game the points of all players.
     */
    public void broadcastPoints() {
        for (ZAPlayer zap : getPlayers()) {
            Player p = zap.getPlayer();
            for (ZAPlayer zap2 : getPlayers()) {
                Player p2 = zap2.getPlayer();
                p.sendMessage(ChatColor.RED + p2.getName() + ChatColor.RESET + " - " + ChatColor.GRAY + zap2.getPoints());
            }
        }
    }

    /**
     * Ends the game, repairs all barriers, closes all areas, and removes all players.
     */
    public void end() {
        if (started) {
            int points = 0;
            for (ZAPlayer zap : getObjectsOfType(ZAPlayer.class)) {
                points += zap.getPoints();
            }
            GameEndEvent GEE = new GameEndEvent(this, points);
            Bukkit.getPluginManager().callEvent(GEE);
            if (!GEE.isCancelled()) {
                spawnedInThisRound = 0;
                paused = true;
                started = false;
                mainframe.clearLinks();
                level = 1;
                if (nlt != null && nlt.isRunning()) {
                    nlt.remove();
                }
                for (Blinkable b : getObjectsOfType(Blinkable.class)) {
                    b.setBlinking(true);
                }
                for (Undead gu : data.undead) {
                    if (gu.getGame() == this) {
                        gu.kill();
                    }
                }
                for (Hellhound ghh : data.hellhounds) {
                    if (ghh.getGame() == this) {
                        ghh.kill();
                    }
                }
                for (Barrier barrier : getObjectsOfType(Barrier.class)) {
                    barrier.replacePanels();
                }
                for (Passage passage : getObjectsOfType(Passage.class)) {
                    passage.close();
                }
                for (Claymore more : getObjectsOfType(Claymore.class)) {
                    more.remove();
                }
                for (ZAPlayer zap : getPlayers()) {
                    Player player = zap.getPlayer();
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

    public CopyOnWriteArrayList<GameObject> getAllPhysicalObjects() {
        return objects;
    }

    public ChestFakeBeaconThread getFakeBeaconThread() {
        return beacons;
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
    public List<ZAMob> getMobs() {
        return getObjectsOfType(ZAMob.class);
    }

    public boolean hasMob(ZAMob mob) {
        return getMobs().contains(mob);
    }

    /**
     * Gets the name of this game.
     * 
     * @return The name of the ZAGame
     */
    public String getName() {
        return name;
    }

    @SuppressWarnings("unchecked") public <T extends Object> List<T> getObjectsOfType(Class<T> type) {
        ArrayList<T> list = new ArrayList<T>();
        for (Object obj : objects) {
            if (type.isAssignableFrom(obj.getClass())) {
                list.add((T) obj);
            }
        }
        return list;
    }

    /**
     * Returns a list of players currently in the game.
     * 
     * @return A list of player names that are involved in this game
     */
    public List<ZAPlayer> getPlayers() {
        return getObjectsOfType(ZAPlayer.class);
    }

    public ArrayList<Integer> getWolfLevels() {
        return wolfLevels;
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
            for (ZAPlayer zap : getPlayers()) {
                if (!zap.getPlayer().isDead() && !zap.isInLastStand() && !zap.isInLimbo()) {
                    zaps.add(zap);
                }
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
    public ZAPlayer getRandomPlayer() {
        return getPlayers().get(rand.nextInt(getPlayers().size()));
    }

    /**
     * Gets the players still in the game.
     * This only counts players that are living and not in last stand or limbo.
     * 
     * @return How many living players are in the game
     */
    public int getRemainingPlayers() {
        int i = 0;
        for (ZAPlayer zap : getPlayers()) {
            if (!zap.getPlayer().isDead() && !zap.isInLimbo() && !zap.isInLastStand()) {
                ++i;
            }
        }
        return i;
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
        spawnedInThisRound = 0;
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
            for (ZAPlayer zap : getPlayers()) {
                Player p = zap.getPlayer();
                p.setLevel(level);
                p.sendMessage(ChatColor.BOLD + "Level " + ChatColor.RESET + ChatColor.RED + level + ChatColor.RESET + ChatColor.BOLD + " has started.");
            }
            if (level != 1) {
                broadcastPoints();
            }
        }
        if (wolfLevels != null && wolfLevels.contains(level)) {
            wolfRound = true;
        }
        if (paused) {
            pause(false);
        }
        nlt = new NextLevelThread(this, true);
        if (getMobCount() <= 0) {
            spawnWave();
        }
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
        for (GameObject object : getAllPhysicalObjects()) {
            object.remove();
        }
        if (beacons != null) {
            beacons.remove();
        }
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

    /**
     * Removes a player from the game.
     * 
     * @param player The player to be removed from the game
     */
    public void removePlayer(Player player) {
        if (getPlayers().contains(data.getZAPlayer(player))) {
            objects.remove(data.getZAPlayer(player));
        }
        ZAPlayer zap = data.getZAPlayer(player);
        if (zap != null) {
            zap.removeFromGame();
            data.players.get(player).removeFromGame();
            data.players.remove(player);
            if (getPlayers().isEmpty()) {
                pause(true);
                end();
            }
        }
    }

    public void setWolfLevels(ArrayList<Integer> levels) {
        wolfLevels = levels;
    }

    /**
     * Sets the active chest that can be used during this game.
     * 
     * @param mc The chest to be made active
     */
    public void setActiveMysteryChest(MysteryChest mc) {
        if ((Boolean) Setting.MOVING_CHESTS.getSetting()) {
            active = mc;
            for (MysteryChest chest : getObjectsOfType(MysteryChest.class)) {
                chest.setActive(false);
            }
            if (!mc.isActive()) {
                mc.setActive(true);
                mc.setActiveUses(rand.nextInt(8) + 2);
            }
        }
    }

    public void setFakeBeaconThread(ChestFakeBeaconThread thread) {
        beacons = thread;
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
        if (mobcount < 0) {
            mobcount = 0;
        }
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
    public void spawn(Location l, boolean closeSpawn) {
        SpawnManager.spawn(this, l, closeSpawn);
    }

    /**
     * Spawns a wave of mobs around random living players in this game.
     * If barriers are present and acessible, spawns the mobs at the barriers.
     */
    public void spawnWave() {
        if (mainframe == null && getRandomLivingPlayer() != null) {
            Location playerLoc = getRandomLivingPlayer().getLocation();
            mainframe = new Mainframe(this, playerLoc);
        }
        SpawnManager.spawnWave(this);
        started = true;
    }

    private boolean overlapsAnotherObject(GameObject obj) {
        for (GameObject object : getAllPhysicalObjects()) {
            for (Block block : object.getDefiningBlocks()) {
                if (block != null) {
                    Location bLoc = block.getLocation();
                    for (Block otherBlock : obj.getDefiningBlocks()) {
                        if (MiscUtil.locationMatch(otherBlock.getLocation(), bLoc)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override public Permadata getSerializedVersion() {
        return new SerialGame(this);
    }
}
