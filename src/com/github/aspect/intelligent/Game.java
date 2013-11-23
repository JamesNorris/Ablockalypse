package com.github.aspect.intelligent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.PermanentAspect;
import com.github.aspect.block.MysteryBox;
import com.github.aspect.block.Teleporter;
import com.github.aspect.entity.ZAMob;
import com.github.aspect.entity.ZAPlayer;
import com.github.behavior.GameAspect;
import com.github.enumerated.Setting;
import com.github.enumerated.ZASound;
import com.github.event.GameEndEvent;
import com.github.event.bukkit.PlayerJoin;
import com.github.manager.SpawnManager;
import com.github.threading.inherent.MysteryBoxFakeBeaconTask;
import com.github.threading.inherent.NextLevelTask;
import com.github.utility.BukkitUtility;
import com.github.utility.MathUtility;
import com.github.utility.serial.SavedVersion;
import com.github.utility.serial.SerialLocation;

public class Game extends PermanentAspect {
    private MysteryBox active;
    private MysteryBoxFakeBeaconTask beacons;
    private DataContainer data = Ablockalypse.getData();
    private int level = 0, mobcount, startpoints, spawnedInThisRound = 0;
    private Teleporter mainframe;
    private GameScoreboard scoreBoard;
    private String name;
    private NextLevelTask nlt;
    private CopyOnWriteArrayList<GameAspect> objects = new CopyOnWriteArrayList<GameAspect>();
    private HashMap<Integer, Integer> wolfLevels = new HashMap<Integer, Integer>();
    private boolean wolfRound, armorRound, paused, started;// TODO armorRound not used (should it be?)
    private Random rand = new Random();
    private UUID uuid = UUID.randomUUID();

    public Game(SavedVersion savings) {
        this((String) savings.get("game_name"));
        // SerialLocation.returnLocation((SerialLocation) savings.get("active_chest_location"));
        active = null;// automatically set by loading the available mystery chests
        level = (Integer) savings.get("level");
        mobcount = (Integer) savings.get("mob_count");
        startpoints = (Integer) savings.get("starting_points");
        @SuppressWarnings("unchecked") List<SavedVersion> savedVersions = (List<SavedVersion>) savings.get("game_objects");
        for (SavedVersion save : savedVersions) {
            if (save == null) {
                continue;
            }
            PermanentAspect.load(save.getVersionClass(), save);
        }
        if (savings.get("mainframe_location") != null) {
            mainframe = data.getTeleporter(SerialLocation.returnLocation((SerialLocation) savings.get("mainframe_location")));
            mainframe.refresh();
        }
        @SuppressWarnings("unchecked") HashMap<Integer, Integer> hashMap = (HashMap<Integer, Integer>) savings.get("wolf_levels");
        wolfLevels = hashMap;
        wolfRound = (Boolean) savings.get("wolf_round");
        armorRound = (Boolean) savings.get("armor_round");
        paused = (Boolean) savings.get("paused");
        started = (Boolean) savings.get("started");
        spawnedInThisRound = (Integer) savings.get("mobs_spawned_this_round");
        uuid = savings.get("uuid") == null ? uuid : (UUID) savings.get("uuid");
    }

    /**
     * Creates a new instance of a game.
     * 
     * @param name The name of the ZAGame
     */
    public Game(String name) {
        this.name = name;
        paused = false;
        started = false;
        @SuppressWarnings("unchecked") ArrayList<String> wolfLevelsList = (ArrayList<String>) Setting.WOLF_LEVELS.getSetting();
        for (String line : wolfLevelsList.toArray(new String[wolfLevelsList.size()])) {
            for (Integer level : MathUtility.parseIntervalNotation(line)) {
                wolfLevels.put(level, MathUtility.parsePercentage(line));
            }
        }
        startpoints = (Integer) Setting.STARTING_POINTS.getSetting();
        beacons = new MysteryBoxFakeBeaconTask(this, 200, (Boolean) Setting.BEACONS.getSetting());
        scoreBoard = new GameScoreboard(this);
        data.objects.add(this);
    }

    /**
     * Attaches an area to this game.
     * 
     * @param ga The area to load into this game
     */
    public void addObject(GameAspect obj) {
        if (obj != null && !objects.contains(obj) && !overlapsAnotherObject(obj)) {
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
        if (!player.isOnline() && !PlayerJoin.isQueued(zap)) {
            return;
        }
        addObject(zap);
        if (!data.isZAPlayer(player)) {
            zap.setPoints(startpoints);
        }
        if (!playerIsInGame(zap)) {
            broadcast(ChatColor.RED + player.getName() + ChatColor.GRAY + " has joined the game!", player);
        }
        if (paused) {
            pause(false);
        }
        if (!started) {
            start();
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

    /**
     * Sends all players in the game the points of all players.
     */
    public void broadcastPoints() {
        for (ZAPlayer zap : getPlayers()) {
            zap.showPoints();
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
     * Ends the game, repairs all barriers, closes all areas, and removes all players.
     * @param countQueue If the player queue for offline players (see PlayerJoin.java) should be
     * checked before ending the game. If the queue is not empty, the game will then not start.
     */
    public void end(boolean countQueue) {
        if (started) {
            if (countQueue && PlayerJoin.getQueues(this).size() != 0) {
                return;
            }
            int points = 0;
            for (ZAPlayer zap : getObjectsOfType(ZAPlayer.class)) {
                points += zap.getPoints();
            }
            GameEndEvent GEE = new GameEndEvent(this, points);
            Bukkit.getPluginManager().callEvent(GEE);
            if (!GEE.isCancelled()) {
                for (GameAspect obj : objects) {
                    obj.onGameEnd();
                }
                spawnedInThisRound = 0;
                paused = true;
                started = false;
                level = 1;
                if (nlt != null && nlt.isRunning()) {
                    nlt.cancel();
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
    public MysteryBox getActiveMysteryChest() {
        return active;
    }

    public Player getClosestLivingPlayer(Location location) {
        if (getRemainingPlayers().size() >= 1) {
            Player closest = getRandomLivingPlayer();
            Double distanceSquared = Double.MAX_VALUE;
            for (ZAPlayer zap : getPlayers()) {
                double currentDSq = zap.getPlayer().getLocation().distanceSquared(location);
                if (!zap.getPlayer().isDead() && !zap.isInLastStand() && !zap.isInLimbo() && currentDSq < distanceSquared) {
                    closest = zap.getPlayer();
                    distanceSquared = currentDSq;
                }
            }
            return closest;
        }
        return null;
    }

    public MysteryBoxFakeBeaconTask getFakeBeaconThread() {
        return beacons;
    }

    public GameScoreboard getGameScoreboard() {
        return scoreBoard;
    }

    @Override public String getHeader() {
        return this.getClass().getSimpleName() + " <UUID: " + getUUID().toString() + ">";
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
    public Teleporter getMainframe() {
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

    public int getMobCountSpawnedInThisRound() {
        return spawnedInThisRound;
    }

    /**
     * Gets all mobs spawned in this game.
     * 
     * @return All mobs currently alive in this game
     */
    public List<ZAMob> getMobs() {
        return getObjectsOfType(ZAMob.class);
    }

    /**
     * Gets the name of this game.
     * 
     * @return The name of the ZAGame
     */
    public String getName() {
        return name;
    }

    public CopyOnWriteArrayList<GameAspect> getObjects() {
        return objects;
    }

    @SuppressWarnings("unchecked") public <T extends Object> List<T> getObjectsOfType(Class<T> type) {
        ArrayList<T> list = new ArrayList<T>();
        for (Object obj : objects) {
            if (obj != null && type != null) {
                if (type.isAssignableFrom(obj.getClass())) {
                    list.add((T) obj);
                }
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

    /**
     * Gets a random living player.
     * Living is considered as not in limbo, last stand, respawn thread, or death.
     * 
     * @return The random living player
     */
    public Player getRandomLivingPlayer() {
        if (getRemainingPlayers().size() >= 1) {
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
    public List<ZAPlayer> getRemainingPlayers() {
        List<ZAPlayer> remaining = new ArrayList<ZAPlayer>();
        for (ZAPlayer zap : getPlayers()) {
            if (!zap.getPlayer().isDead() && !zap.isInLimbo() && !zap.isInLastStand()) {
                remaining.add(zap);
            }
        }
        return remaining;
    }

    @Override public SavedVersion getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("uuid", getUUID());
        savings.put("active_chest_location", active == null ? null : new SerialLocation(active.getLocation()));
        savings.put("level", level);
        savings.put("mob_count", mobcount);
        savings.put("starting_points", startpoints);
        savings.put("mobs_spawned_this_round", spawnedInThisRound);
        savings.put("mainframe_location", mainframe == null ? null : new SerialLocation(mainframe.getLocation()));
        savings.put("game_name", name);
        List<SavedVersion> savedVersions = new ArrayList<SavedVersion>();
        for (PermanentAspect aspect : getObjectsOfType(PermanentAspect.class)) {
            savedVersions.add(aspect.getSave());
        }
        savings.put("game_objects", savedVersions);
        savings.put("wolf_levels", wolfLevels);
        savings.put("wolf_round", wolfRound);
        savings.put("armor_round", armorRound);
        savings.put("paused", paused);
        savings.put("started", started);
        savings.put("mobs_spawned_this_round", spawnedInThisRound);
        return new SavedVersion(getHeader(), savings, getClass());
    }

    @Override public UUID getUUID() {
        return uuid;
    }

    public int getWolfPercentage() {
        return getWolfPercentage(level);
    }

    public int getWolfPercentage(int level) {
        if (!wolfLevels.containsKey(level)) {
            return 0;
        }
        return wolfLevels.get(level);
    }

    public boolean hasMob(ZAMob mob) {
        return getMobs().contains(mob);
    }

    /**
     * Returns whether or not the game has started.
     * 
     * @return Whether or not the game has been started, and mobs are spawning
     */
    public boolean hasStarted() {
        return started;
    }

    public boolean isArmorRound() {
        return armorRound;
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
        int maxWave = (Integer) Setting.MAX_WAVE.getSetting();
        if (level >= maxWave && maxWave != -1) {
            end(false);
            return;
        }
        spawnedInThisRound = 0;
        mobcount = 0;
        if (!started) {
            level = 1;
            List<MysteryBox> chests = getObjectsOfType(MysteryBox.class);
            if (chests != null && chests.size() > 0) {
                MysteryBox mc = chests.get(rand.nextInt(chests.size()));
                setActiveMysteryChest(mc);
            }
            for (GameAspect obj : objects) {
                obj.onGameStart();
            }
            started = true;
        }
        if (wolfLevels != null && wolfLevels.containsKey(level)) {
            wolfRound = true;
        }
        paused = false;
        nlt = new NextLevelTask(this, true);
        spawnWave(SpawnManager.getCurrentSpawnAmount(this) - spawnedInThisRound);
        for (GameAspect obj : objects) {
            obj.onNextLevel();
        }
    }

    // only to be used onEnable or onDisable
    public void organizeObjects() {
        CopyOnWriteArrayList<GameAspect> newObjects = new CopyOnWriteArrayList<GameAspect>();
        int[][] priorities = new int[objects.size()][2];
        for (int i = 0; i < priorities.length; i++) {
            GameAspect obj = objects.get(i);
            priorities[i][0] = i;
            priorities[i][1] = obj == null ? Integer.MAX_VALUE : obj.getLoadPriority();
        }
        for (int j = 1; j < priorities.length; j++) {
            int[] temp = priorities[j];
            int current = j - 1;
            while (current >= 0 && priorities[current][1] > temp[1]) {
                priorities[current + 1] = priorities[current];
                current--;
            }
            priorities[current + 1] = temp;
        }
        for (int k = 0; k < priorities.length; k++) {
            newObjects.add(objects.get(priorities[k][0]));
        }
        objects = newObjects;
    }

    /**
     * Sets the game to pause or not.
     * 
     * @param tf Whether or not to pause or un-pause the game
     */
    public void pause(boolean tf) {
        if (tf && !paused) {
            // TODO freeze mobs, cancel damage, etc
        } else if (!tf && paused) {
            --level;
            nextLevel();
        }
        paused = tf;
    }

    /**
     * Ends the game, removes all attached instances, and finalizes this instance.
     */
    public void remove(boolean permanently) {
        end(false);
        for (GameAspect object : getObjects()) {
            object.remove();
        }
        if (beacons != null) {
            beacons.cancel();
        }
        File savedData = Ablockalypse.getExternal().getSavedDataFile(getName(), false);
        if (permanently && savedData != null) {
            savedData.delete();
        }
        data.objects.remove(this);
    }

    /**
     * Removes an area from this game.
     * 
     * @param ga The area to be unloaded from this game
     */
    public void removeObject(GameAspect obj) {
        if (obj != null && objects.contains(obj)) {
            objects.remove(obj);
        }
    }

    /**
     * Removes a player from the game.
     * 
     * @param player The player to be removed from the game
     */
    public void removePlayer(Player player) {
        ZAPlayer zap = data.getZAPlayer(player);
        if (zap != null && objects.contains(zap)) {
            objects.remove(zap);
            zap.removeFromGame();// removes zap from data.objects
            if (getPlayers().isEmpty()) {
                pause(true);
                end(true);
            }
        }
    }

    /**
     * Sets the active chest that can be used during this game.
     * 
     * @param mc The chest to be made active
     */
    public void setActiveMysteryChest(MysteryBox mc) {
        if ((Boolean) Setting.MOVING_MYSTERY_BOXES.getSetting()) {
            active = mc;
            for (MysteryBox chest : getObjectsOfType(MysteryBox.class)) {
                chest.setActive(false);
            }
            if (!mc.isActive()) {
                mc.setActive(true);
                mc.setActiveUses(rand.nextInt(8) + 2);
            }
        }
    }

    public void setArmorRound(boolean tf) {
        armorRound = tf;
    }

    public void setFakeBeaconThread(MysteryBoxFakeBeaconTask thread) {
        beacons = thread;
    }

    public void setGameScoreboard(GameScoreboard scoreBoard) {
        this.scoreBoard = scoreBoard;
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
    public void setMainframe(Teleporter mf) {
        mainframe = mf;
        mf.refresh();
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

    public void setMobCountSpawnedInThisRound(int amt) {
        spawnedInThisRound = amt;
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
     * Spawns a wave of mobs around random living players in this game.
     * If barriers are present and acessible, spawns the mobs at the barriers.
     */
    public void spawnWave(int amount) {
        if (mainframe == null && getRandomLivingPlayer() != null) {
            Location playerLoc = getRandomLivingPlayer().getLocation();
            mainframe = new Teleporter(this, playerLoc);
        }
        SpawnManager.spawnWave(this, amount);
        started = true;
    }

    public void start() {
        started = false;
        level = 0;
        nextLevel();
    }

    private boolean overlapsAnotherObject(GameAspect obj) {
        if (obj.getDefiningBlocks() == null) {
            return false;
        }
        for (GameAspect object : getObjects()) {
            if (object == null || object.getDefiningBlocks() == null) {
                continue;
            }
            for (Block block : object.getDefiningBlocks()) {
                if (block == null) {
                    continue;
                }
                Location bLoc = block.getLocation();
                for (Block otherBlock : obj.getDefiningBlocks()) {
                    if (BukkitUtility.locationMatch(otherBlock.getLocation(), bLoc)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean playerIsInGame(ZAPlayer zap) {
        for (ZAPlayer zap2 : getObjectsOfType(ZAPlayer.class)) {
            if (zap.getPlayer().getName().equalsIgnoreCase(zap2.getPlayer().getName())) {
                return true;
            }
        }
        return false;
    }
}
