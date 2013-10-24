package com.github.aspect.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.PermanentAspect;
import com.github.aspect.entity.ZAPlayer;
import com.github.aspect.intelligent.Game;
import com.github.behavior.Blinkable;
import com.github.behavior.GameObject;
import com.github.behavior.MapDatable;
import com.github.enumerated.Setting;
import com.github.enumerated.ZAEffect;
import com.github.enumerated.ZASound;
import com.github.threading.Task;
import com.github.threading.inherent.BarrierBreakTask;
import com.github.threading.inherent.BarrierFixTask;
import com.github.threading.inherent.BlinkerTask;
import com.github.utility.AblockalypseUtility;
import com.github.utility.BukkitUtility;
import com.github.utility.selection.Cube;
import com.github.utility.serial.SavedVersion;
import com.github.utility.serial.SerialLocation;

public class Barrier extends PermanentAspect implements GameObject, Blinkable, MapDatable {
    private CopyOnWriteArrayList<BlockState> states = new CopyOnWriteArrayList<BlockState>();
    private BlinkerTask bt;
    private Location center, spawnloc;
    private boolean correct;
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private int hp = 5;
    private UUID uuid = UUID.randomUUID();
    private Task warning;

    /**
     * Creates a new instance of a Barrier, where center is the center of the 3x3 barrier.
     * 
     * @param center The center of the barrier
     * @param game The game to involve this barrier in
     */
    public Barrier(Location center, Game game) {
        this.center = center;
        this.game = game;
        findSpawnLoc();
        game.addObject(this);
        for (Location loc : new Cube(center, 1).getLocations()) {
            Block b = loc.getBlock();
            if (b != null && !b.isEmpty() && b.getType() != null) {
                states.add(b.getState());
            }
        }
        initBlinker();
        data.objects.add(this);
    }

    public Barrier(SavedVersion savings) {
        this(SerialLocation.returnLocation((SerialLocation) savings.get("center_location")), Ablockalypse.getData().getGame((String) savings.get("game_name"), true));
        List<BlockState> blocks = new CopyOnWriteArrayList<BlockState>();
        @SuppressWarnings("unchecked") List<SerialLocation> serialBlockLocations = (List<SerialLocation>) savings.get("all_block_locations");
        for (SerialLocation serialLoc : serialBlockLocations) {
            blocks.add(serialLoc.getWorld().getBlockAt(SerialLocation.returnLocation(serialLoc)).getState());
        }
        this.states = (CopyOnWriteArrayList<BlockState>) blocks;
        center = SerialLocation.returnLocation((SerialLocation) savings.get("center_location"));
        spawnloc = SerialLocation.returnLocation((SerialLocation) savings.get("spawn_location"));
        correct = (Boolean) savings.get("setup_is_correct");
        game = Ablockalypse.getData().getGame((String) savings.get("game_name"), true);
        uuid = savings.get("uuid") == null ? uuid : (UUID) savings.get("uuid");
    }

    /**
     * Slowly breaks the blocks of the barrier.
     * 
     * @param liveEntity The entityliving that is breaking the barrier
     */
    public void breakBarrier(LivingEntity liveEntity) {
        if (bt.isRunning()) {
            bt.cancel();
            return;
        }
        new BarrierBreakTask(this, liveEntity, true);
    }

    /**
     * Changes all blocks within the barrier to air.
     */
    public void breakPanels() {
        for (BlockState state : states) {
            state.getBlock().setType(Material.AIR);
            ZAEffect.SMOKE.play(state.getLocation());
        }
        hp = 0;
    }

    /**
     * Slowly fixes the blocks of the barrier.
     * 
     * @param zap The ZAPlayer that is going to be fixing this barrier
     */
    public void fixBarrier(ZAPlayer zap) {
        if (bt.isRunning()) {
            bt.cancel();
            return;
        }
        new BarrierFixTask(this, zap, true);
    }

    /**
     * Gets the BlinkerThread attached to this instance.
     * 
     * @return The BlinkerThread attached to this instance
     */
    @Override public BlinkerTask getBlinkerThread() {
        return bt;
    }

    /**
     * Returns the list of blocks in the barrier.
     * 
     * @return A list of blocks located in the barrier
     */
    public CopyOnWriteArrayList<Block> getBlocks() {
        CopyOnWriteArrayList<Block> blocks = new CopyOnWriteArrayList<Block>();
        for (BlockState state : this.states) {
            blocks.add(state.getBlock());
        }
        return blocks;
    }

    /**
     * Gets the center location of the barrier.
     * 
     * @return The center of the barrier
     */
    public Location getCenter() {
        return center;
    }

    @Override public Block getDefiningBlock() {
        return center.getBlock();
    }

    /**
     * Gets the blocks that defines this object as an object.
     * 
     * @return The blocks assigned to this object
     */
    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blockArray = new ArrayList<Block>();
        for (BlockState state : states) {
            blockArray.add(state.getBlock());
        }
        return blockArray;
    }

    /**
     * Gets the game this barrier is involved in.
     * 
     * @return The game this barrier is attached to
     */
    @Override public Game getGame() {
        return game;
    }

    @Override public String getHeader() {
        return this.getClass().getSimpleName() + " <UUID: " + getUUID().toString() + ">";
    }

    @Override public Location getPointClosestToOrigin() {
        Location origin = new Location(center.getWorld(), 0, 0, 0, 0, 0);
        Location loc = null;
        for (BlockState state : states) {
            Block block = state.getBlock();
            if (loc == null || block.getLocation().distanceSquared(origin) <= loc.distanceSquared(origin)) {
                loc = block.getLocation();
            }
        }
        return loc;
    }

    @Override public SavedVersion getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("uuid", getUUID());
        List<SerialLocation> serialBlocks = new ArrayList<SerialLocation>();
        for (BlockState state : states) {
            serialBlocks.add(new SerialLocation(state.getBlock().getLocation()));
        }
        savings.put("all_block_locations", serialBlocks);
        savings.put("center_location", center == null ? null : new SerialLocation(center));
        savings.put("spawn_location", spawnloc == null ? null : new SerialLocation(spawnloc));
        savings.put("setup_is_correct", correct);
        savings.put("game_name", game.getName());
        return new SavedVersion(getHeader(), savings, getClass());
    }

    /**
     * Gets the mob spawn location for this barrier.
     * 
     * @return The mob spawn location around this barrier
     */
    public Location getSpawnLocation() {
        return spawnloc;
    }

    @Override public UUID getUUID() {
        return uuid;
    }

    /**
     * Tells whether or not the barrier has any missing blocks.
     * 
     * @return Whether or not the barrier is broken
     */
    public boolean isBroken() {
        if (center.getBlock().isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the barrier is setup correctly or not.
     * 
     * @return Whether or not the barrier is setup correctly
     */
    public boolean isCorrect() {
        return correct;
    }

    @Override public void paste(Location pointClosestToOrigin) {
        Location old = getPointClosestToOrigin();
        Location toLoc = pointClosestToOrigin.add(center.getX() - old.getX(), center.getY() - old.getY(), center.getZ() - old.getZ());
        center = toLoc;
        findSpawnLoc();
        bt.cancel();
        initBlinker();
    }

    /**
     * Removes the barrier.
     */
    @Override public void remove() {
        replacePanels();
        setBlinking(false);
        if (warning != null) {
            data.objects.remove(warning);
        }
        game.removeObject(this);
        data.objects.remove(this);
        game = null;
    }

    /**
     * Replaces all holes in the barrier.
     */
    public void replacePanels() {
        for (BlockState state : states) {
            state.update(true);
            ZAEffect.SMOKE.play(state.getLocation());
        }
        ZASound.BARRIER_REPAIR.play(center);
        hp = 5;
    }

    /**
     * Stops/Starts the blinker for this barrier.
     * 
     * @param tf Whether or not this barrier should blink
     */
    @Override public void setBlinking(boolean tf) {
        bt.setRunning(tf);
    }

    public int getHP() {
        return hp;
    }

    public void setHP(int hp) {
        if (hp < 0) {
            hp = 0;
            return;
        }
        if (warning != null) {
            data.objects.remove(warning);
        }
        if (hp < 5) {
            warning = AblockalypseUtility.scheduleNearbyWarning(center, ChatColor.GRAY + "Hold " + ChatColor.AQUA + "SHIFT" + ChatColor.GRAY + " to fix barrier.", 2, 3, 2, 10000);
        }
        this.hp = hp;
    }

    private void findSpawnLoc() {
        spawnloc = BukkitUtility.getNearbyLocation(center, 2, 5, 0, 0, 2, 5);
        if (!spawnloc.getBlock().isEmpty() && spawnloc.getBlock().getType() != Material.AIR) {
            findSpawnLoc();
        }
    }

    private void initBlinker() {
        ArrayList<Block> blockArray = new ArrayList<Block>();
        blockArray.add(center.getBlock());
        boolean blinkers = (Boolean) Setting.BLINKERS.getSetting();
        bt = new BlinkerTask(blockArray, DyeColor.BLUE, 30, blinkers);
        DyeColor color = states.size() >= 9 ? DyeColor.BLUE : DyeColor.RED;
        correct = color == DyeColor.BLUE;
        bt.setColor(color);
    }

    @Override public void onGameEnd() {
        replacePanels();
        setBlinking(true);
    }

    @Override public void onGameStart() {
        setBlinking(false);
    }

    @Override public void onNextLevel() {}

    @Override public void onLevelEnd() {}
    
    @Override public int getLoadPriority() {
        return 2;
    }
}
