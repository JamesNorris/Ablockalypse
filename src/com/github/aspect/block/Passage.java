package com.github.aspect.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.intelligent.Game;
import com.github.behavior.Blinkable;
import com.github.behavior.GameObject;
import com.github.behavior.MapDatable;
import com.github.enumerated.Setting;
import com.github.enumerated.ZAEffect;
import com.github.enumerated.ZASound;
import com.github.threading.inherent.BlinkerTask;
import com.github.utility.selection.Rectangle;
import com.github.utility.serial.SavedVersion;
import com.github.utility.serial.SerialLocation;

public class Passage extends Powerable implements GameObject, Blinkable, MapDatable {
    private BlinkerTask bt;
    private DataContainer data = Ablockalypse.getData();
    private Location loc1, loc2;
    private ArrayList<BlockState> states = new ArrayList<BlockState>();
    private boolean opened;
    private Rectangle rectangle;
    private Game zag;
    private UUID uuid = UUID.randomUUID();

    /**
     * Creates a new Passage instance that is represented by a rectangular prism.
     * 
     * @param zag The game that should use this passage
     * @param loc1 The first corner of the rectangular prism
     * @param loc2 The second corner of the rectangular prism
     */
    public Passage(Game game, Location loc1, Location loc2) {
        super(new Rectangle(loc1, loc2).getBlocks());
        rectangle = new Rectangle(loc1, loc2);
        data.objects.add(this);
        this.loc1 = loc1;
        this.loc2 = loc2;
        zag = game;
        opened = false;
        createStates();
        zag.addObject(this);
        initBlinker();
    }

    public Passage(SavedVersion savings) {
        this(Ablockalypse.getData().getGame((String) savings.get("game_name"), true), SerialLocation.returnLocation((SerialLocation) savings.get("location_1")), SerialLocation.returnLocation((SerialLocation) savings.get("location_2")));
        opened = (Boolean) savings.get("is_open");
        if (opened) {
            open();
        } else {
            close();
        }
        uuid = savings.get("uuid") == null ? uuid : (UUID) savings.get("uuid");
    }

    /**
     * Replaces the passage.
     */
    public void close() {
        opened = false;
        for (BlockState state : states) {
            // Block b = l.getBlock();
            // b.setType(locs.get(l));
            // b.setData(locdata.get(l));
            ZAEffect.SMOKE.play(state.getLocation());
            state.update(true, true);
        }
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
     * Gets a list of blocks for this passage.
     * 
     * @return A list of blocks for this passage
     */
    public ArrayList<Block> getBlocks() {
        ArrayList<Block> bls = new ArrayList<Block>();
        for (Location loc : rectangle.getLocations()) {
            bls.add(loc.getBlock());
        }
        return bls;
    }

    /**
     * Gets the blocks around the border of the passage.
     * 
     * @return The blocks around the border
     */
    @SuppressWarnings("deprecation") public ArrayList<Location> getBorderBlocks() {
        return rectangle.get3DBorder();
    }

    @Override public Block getDefiningBlock() {
        return loc1.getBlock();
    }

    /**
     * Gets the blocks that defines this object as an object.
     * 
     * @return The blocks assigned to this object
     */
    @Override public ArrayList<Block> getDefiningBlocks() {
        return getBlocks();
    }

    /**
     * Gets the game this passage is assigned to.
     * 
     * @return The game this passage is assigned to
     */
    @Override public Game getGame() {
        return zag;
    }

    @Override public String getHeader() {
        return this.getClass().getSimpleName() + " <UUID: " + getUUID().toString() + ">";
    }

    /**
     * Gets a point from the passage. This must be between 1 and 2.
     * 
     * @param i The point to get
     * @return The location of the point
     */
    public Location getPoint(int i) {
        Location loc = i == 1 ? loc1 : loc2;
        return loc;
    }

    @Override public Location getPointClosestToOrigin() {
        Location origin = new Location(loc1.getWorld(), 0, 0, 0, 0, 0);
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
        savings.put("location_1", loc1 == null ? null : new SerialLocation(loc1));
        savings.put("location_2", loc2 == null ? null : new SerialLocation(loc2));
        savings.put("is_open", opened);
        savings.put("game_name", zag.getName());
        return new SavedVersion(getHeader(), savings, getClass());
    }

    @Override public UUID getUUID() {
        return uuid;
    }

    /**
     * Returns if the passage is opened or not.
     * 
     * @return Whether or not the passage has been opened
     */
    public boolean isOpened() {
        return opened;
    }

    @Override public boolean isPowered() {
        return opened;
    }

    /**
     * Removes the passage.
     */
    public void open() {
        for (BlockState state : states) {
            state.getBlock().setType(Material.AIR);
            ZAEffect.SMOKE.play(state.getLocation());
        }
        ZASound.AREA_BUY.play(loc1);
        opened = true;
    }

    @Override public void paste(Location pointClosestToOrigin) {
        Location old = getPointClosestToOrigin();
        Location toLoc1 = pointClosestToOrigin.add(loc1.getX() - old.getX(), loc1.getY() - old.getY(), loc1.getZ() - old.getZ());
        Location toLoc2 = pointClosestToOrigin.add(loc2.getX() - old.getX(), loc2.getY() - old.getY(), loc2.getZ() - old.getZ());
        loc1 = toLoc1;
        loc2 = toLoc2;
        close();
        createStates();
        bt.cancel();
        initBlinker();
    }

    @Override public void setPowered(boolean power) {
        if (!opened && power) {
            open();
        } else if (opened && !power) {
            close();
        }
        super.setPowered(power);
    }

    /**
     * Removes the passage.
     */
    @Override public void remove() {
        close();
        if (zag != null) {
            zag.removeObject(this);
        }
        bt.cancel();
        data.objects.remove(this);
        data.objects.remove(bt);
        zag = null;
    }

    /**
     * Stops/Starts the blinker for this barrier.
     * 
     * @param tf Whether or not this barrier should blink
     */
    @Override public void setBlinking(boolean tf) {
        bt.setRunning(tf);
    }

    /**
     * Sets the first or second location of the passage.
     * 
     * @param loc The location to set
     * @param n A number between 1 and 2
     */
    public void setLocation(Location loc, int n) {
        if (n == 1) {
            loc1 = loc;
        } else if (n == 2) {
            loc2 = loc;
        }
        if (n == 1 || n == 2) {
            rectangle = new Rectangle(loc1, loc2);
        }
    }

    private void createStates() {
        for (Location l : rectangle.getLocations()) {
            // locs.put(l, l.getBlock().getType());
            // locdata.put(l, l.getBlock().getData());
            states.add(l.getBlock().getState());
        }
    }

    @SuppressWarnings("deprecation") private void initBlinker() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        for (Location l : rectangle.get3DBorder()) {
            blocks.add(l.getBlock());
        }
        bt = new BlinkerTask(blocks, DyeColor.BLUE, 30, (Boolean) Setting.BLINKERS.getSetting());
    }

    @Override public void onGameEnd() {
        close();
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
