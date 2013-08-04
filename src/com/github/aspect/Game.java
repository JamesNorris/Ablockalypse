package com.github.aspect;

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
import com.github.behavior.Blinkable;
import com.github.behavior.GameObject;
import com.github.behavior.ZAMob;
import com.github.enumerated.Setting;
import com.github.enumerated.ZASound;
import com.github.event.GameEndEvent;
import com.github.event.bukkit.PlayerJoin;
import com.github.manager.SpawnManager;
import com.github.threading.inherent.ChestFakeBeaconThread;
import com.github.threading.inherent.NextLevelThread;
import com.github.utility.MiscUtil;
import com.github.utility.serial.SavedVersion;
import com.github.utility.serial.SerialLocation;

public class Game extends PermanentAspect {
    private MysteryChest active;
    private ChestFakeBeaconThread beacons;
    private DataContainer data = Ablockalypse.getData();
    private int level = 0, mobcount, startpoints, spawnedInThisRound = 0;
    private Mainframe mainframe;
    private String name;
    private NextLevelThread nlt;
    private CopyOnWriteArrayList<GameObject> objects = new CopyOnWriteArrayList<GameObject>();
    private HashMap<Integer, Integer> wolfLevels = new HashMap<Integer, Integer>();
    private boolean wolfRound, armorRound, paused, started;// TODO armorRound not used
    private Random rand = new Random();
    private final UUID uuid = UUID.randomUUID();

    public Game(SavedVersion savings) {
        this((String) savings.get("game_name"));
        // SerialLocation.returnLocation((SerialLocation) savings.get("active_chest_location"));
        active = null;// automatically set by loading the available mystery chests
        level = (Integer) savings.get("level");
        mobcount = (Integer) savings.get("mob_count");
        startpoints = (Integer) savings.get("starting_points");
        mainframe = savings.get("mainframe_location") != null ? new Mainframe(this, SerialLocation.returnLocation((SerialLocation) savings.get("mainframe_location"))) : null;
        @SuppressWarnings("unchecked") List<SavedVersion> savedVersions = (List<SavedVersion>) savings.get("game_objects");
        for (SavedVersion save : savedVersions) {
            PermanentAspect.load(save.getVersionClass(), save);
        }
        @SuppressWarnings("unchecked") HashMap<Integer, Integer> hashMap = (HashMap<Integer, Integer>) savings.get("wolf_levels");
        wolfLevels = hashMap;
        wolfRound = (Boolean) savings.get("wolf_round");
        armorRound = (Boolean) savings.get("armor_round");
        paused = (Boolean) savings.get("paused");
        started = (Boolean) savings.get("started");
        spawnedInThisRound = (Integer) savings.get("mobs_spawned_this_round");
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
            for (Integer level : MiscUtil.parseIntervalNotation(line)) {
                wolfLevels.put(level, MiscUtil.parsePercentage(line));
            }
        }
        startpoints = (Integer) Setting.STARTING_POINTS.getSetting();
        beacons = new ChestFakeBeaconThread(this, 200, (Boolean) Setting.BEACONS.getSetting());
        data.objects.add(this);
    }

    /**
     * Attaches an area to this game.
     * 
     * @param ga The area to load into this game
     */
    public void addObject(GameObject obj) {
        if (obj != null && !overlapsAnotherObject(obj) && !objects.contains(obj)) {
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
        addObject(zap);
        if (!player.isOnline() && !PlayerJoin.isQueued(zap)) {
            PlayerJoin.queuePlayer(zap, mainframe.getLocation());
        }
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
            nextLevel();
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
            Player p = zap.getPlayer();
            for (ZAPlayer zap2 : getPlayers()) {
                Player p2 = zap2.getPlayer();
                p.sendMessage(ChatColor.RED + p2.getName() + ChatColor.RESET + " - " + ChatColor.GRAY + zap2.getPoints());
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
     * Ends the game, repairs all barriers, closes all areas, and removes all players.
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
                for (Undead gu : data.getObjectsOfType(Undead.class)) {
                    if (gu.getGame() == this) {
                        gu.kill();
                    }
                }
                for (Hellhound ghh : data.getObjectsOfType(Hellhound.class)) {
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

    @Override public String getHeader() {
        return this.getClass().getSimpleName() + " <UUID: " + getUUID() + ">";
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

    @Override public SavedVersion getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
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
        started = true;
        int maxWave = (Integer) Setting.MAX_WAVE.getSetting();
        if (level >= maxWave && maxWave != -1) {
            end(false);
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
        if (wolfLevels != null && wolfLevels.containsKey(level)) {
            wolfRound = true;
        }
        paused = false;
        nlt = new NextLevelThread(this, true);
        spawnWave(SpawnManager.getCurrentSpawnAmount(this) - spawnedInThisRound);
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
    public void remove() {
        end(false);
        for (GameObject object : getAllPhysicalObjects()) {
            object.remove();
        }
        if (beacons != null) {
            beacons.remove();
        }
        data.objects.remove(this);
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
            data.objects.remove(player);
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

    public void setArmorRound(boolean tf) {
        armorRound = tf;
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
        if (data.getObjectsOfType(Mainframe.class).contains(mf)) {
            data.objects.remove(getName());
        }
        mainframe = mf;
        data.objects.add(mf);
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
    public void spawnWave(int amount) {
        if (mainframe == null && getRandomLivingPlayer() != null) {
            Location playerLoc = getRandomLivingPlayer().getLocation();
            mainframe = new Mainframe(this, playerLoc);
        }
        SpawnManager.spawnWave(this, amount);
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

    private boolean playerIsInGame(ZAPlayer zap) {
        for (ZAPlayer zap2 : getObjectsOfType(ZAPlayer.class)) {
            if (zap.getName().equalsIgnoreCase(zap2.getName())) {
                return true;
            }
        }
        return false;
    }
}
