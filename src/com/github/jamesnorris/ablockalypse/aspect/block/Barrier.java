package com.github.jamesnorris.ablockalypse.aspect.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.SpecificGameAspect;
import com.github.jamesnorris.ablockalypse.aspect.entity.ZAPlayer;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;
import com.github.jamesnorris.ablockalypse.behavior.MapDatable;
import com.github.jamesnorris.ablockalypse.enumerated.ZAEffect;
import com.github.jamesnorris.ablockalypse.enumerated.ZASound;
import com.github.jamesnorris.ablockalypse.threading.Task;
import com.github.jamesnorris.ablockalypse.threading.inherent.BarrierBreakTask;
import com.github.jamesnorris.ablockalypse.threading.inherent.BarrierFixTask;
import com.github.jamesnorris.ablockalypse.threading.inherent.BlinkerTask;
import com.github.jamesnorris.ablockalypse.utility.AblockalypseUtility;
import com.github.jamesnorris.ablockalypse.utility.BukkitUtility;
import com.github.jamesnorris.ablockalypse.utility.selection.Cube;
import com.github.jamesnorris.ablockalypse.utility.serial.SavedVersion;
import com.github.jamesnorris.ablockalypse.utility.serial.SerialLocation;

public class Barrier extends SpecificGameAspect implements MapDatable {
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
        super(game, new Cube(center, 1).getLocations(), !game.hasStarted());
        this.center = center;
        this.game = game;
        spawnloc = BukkitUtility.getNearbyLocation(center, 2, 5, 0, 0, 2, 5);
        for (Location loc : new Cube(center, 1).getLocations()) {
            Block b = loc.getBlock();
            if (b != null && !b.isEmpty() && b.getType() != null) {
                states.add(b.getState());
            }
        }
        setIsCorrectlySetup(states.size() >= 9);
    }

    public Barrier(SavedVersion savings) {
        this(SerialLocation.returnLocation((SerialLocation) savings.get("center_location")), Ablockalypse.getData().getGame((String) savings.get("game_name"), true));
        List<BlockState> blocks = new CopyOnWriteArrayList<BlockState>();
        @SuppressWarnings("unchecked") List<SerialLocation> serialBlockLocations = (List<SerialLocation>) savings.get("all_block_locations");
        for (SerialLocation serialLoc : serialBlockLocations) {
            blocks.add(serialLoc.getWorld().getBlockAt(SerialLocation.returnLocation(serialLoc)).getState());
        }
        states = (CopyOnWriteArrayList<BlockState>) blocks;
        center = SerialLocation.returnLocation((SerialLocation) savings.get("center_location"));
        spawnloc = SerialLocation.returnLocation((SerialLocation) savings.get("spawn_location"));
        correct = (Boolean) savings.get("setup_is_correct");
        game = Ablockalypse.getData().getGame((String) savings.get("game_name"), true);
        uuid = savings.get("uuid") == null ? uuid : (UUID) savings.get("uuid");
    }
    
    @SuppressWarnings("serial") @Override public List<Block> getBlinkerBlocks() {
        if (center == null) {
            return super.getBlinkerBlocks();
        }
        return new ArrayList<Block>() {{add(center.getBlock());}};
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
     * Returns the list of blocks in the barrier.
     * 
     * @return A list of blocks located in the barrier
     */
    public CopyOnWriteArrayList<Block> getBlocks() {
        CopyOnWriteArrayList<Block> blocks = new CopyOnWriteArrayList<Block>();
        for (BlockState state : states) {
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
        return getDefiningBlock().getLocation();
    }

    @Override public Block getDefiningBlock() {
        if (center == null) {
            return super.getDefiningBlock();
        }
        return center.getBlock();
    }

    /**
     * Gets the blocks that defines this object as an object.
     * 
     * @return The blocks assigned to this object
     */
    @Override public List<Block> getDefiningBlocks() {
        if (states == null) {
            return super.getDefiningBlocks();
        }
        ArrayList<Block> blockArray = new ArrayList<Block>();
        for (BlockState state : states) {
            blockArray.add(state.getBlock());
        }
        return blockArray;
    }

    public int getHP() {
        return hp;
    }

    @Override public int getLoadPriority() {
        return 2;
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

    @Override public void onGameEnd() {
        replacePanels();
        setBlinking(true);
    }

    @Override public void onGameStart() {
        setBlinking(false);
    }

    @Override public void paste(Location pointClosestToOrigin) {
        Location old = getPointClosestToOrigin();
        Location toLoc = pointClosestToOrigin.add(center.getX() - old.getX(), center.getY() - old.getY(), center.getZ() - old.getZ());
        center = toLoc;
        spawnloc = BukkitUtility.getNearbyLocation(center, 2, 5, 0, 0, 2, 5);
        refreshBlinker();
    }

    /**
     * Removes the barrier.
     */
    @Override public void remove() {
        replacePanels();
        if (warning != null) {
            data.objects.remove(warning);
        }
        super.remove();
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
}
