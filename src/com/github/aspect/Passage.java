package com.github.aspect;

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
import com.github.behavior.Blinkable;
import com.github.behavior.Buyable;
import com.github.behavior.GameObject;
import com.github.behavior.MapDatable;
import com.github.behavior.Powerable;
import com.github.enumerated.Setting;
import com.github.enumerated.ZAEffect;
import com.github.enumerated.ZASound;
import com.github.threading.inherent.BlinkerThread;
import com.github.utility.selection.Rectangle;
import com.github.utility.serial.SavedVersion;
import com.github.utility.serial.SerialLocation;

public class Passage extends PermanentAspect implements GameObject, Blinkable, Powerable, Buyable, MapDatable {
    private BlinkerThread bt;
    private DataContainer data = Ablockalypse.getData();
    private Location loc1, loc2;
    private ArrayList<BlockState> states = new ArrayList<BlockState>();
    private boolean opened, requiresPower = false;
    private Rectangle rectangle;
    private Game zag;
    private final UUID uuid = UUID.randomUUID();

    /**
     * Creates a new Passage instance that is represented by a rectangular prism.
     * 
     * @param zag The game that should use this passage
     * @param loc1 The first corner of the rectangular prism
     * @param loc2 The second corner of the rectangular prism
     */
    public Passage(Game game, Location loc1, Location loc2) {
        data.objects.add(this);
        this.loc1 = loc1;
        this.loc2 = loc2;
        zag = game;
        opened = false;
        rectangle = new Rectangle(loc1, loc2);
        createStates();
        zag.addObject(this);
        initBlinker();
    }

    public Passage(SavedVersion savings) {
        this(Ablockalypse.getData().getGame((String) savings.get("game_name"), true), SerialLocation.returnLocation((SerialLocation) savings.get("location_1")), SerialLocation.returnLocation((SerialLocation) savings.get("location_2")));
        opened = (Boolean) savings.get("is_open");
        requiresPower = (Boolean) savings.get("requires_power");
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
            state.update();
            ZAEffect.SMOKE.play(state.getLocation());
        }
    }

    /**
     * Gets the BlinkerThread attached to this instance.
     * 
     * @return The BlinkerThread attached to this instance
     */
    @Override public BlinkerThread getBlinkerThread() {
        return bt;
    }

    /**
     * Gets a list of blocks for this passage.
     * 
     * @return A list of blocks for this passage
     */
    public ArrayList<Block> getBlocks() {
        ArrayList<Block> bls = new ArrayList<Block>();
        for (BlockState state : states) {
            bls.add(state.getBlock());
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

    @Override @Deprecated public int getCost() {
        return 1500;
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
        return this.getClass().getSimpleName() + " <UUID: " + getUUID() + ">";
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
        savings.put("location_1", loc1 == null ? null : new SerialLocation(loc1));
        savings.put("location_2", loc2 == null ? null : new SerialLocation(loc2));
        savings.put("is_open", opened);
        savings.put("requires_power", requiresPower);
        savings.put("game_name", zag.getName());
        return new SavedVersion(getHeader(), savings, getClass());
    }

    @Override public UUID getUUID() {
        return uuid;
    }

    @Override public boolean isBought() {
        return opened;
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
        opened = true;
        for (BlockState state : states) {
            state.getBlock().setType(Material.AIR);
            ZAEffect.SMOKE.play(state.getLocation());
        }
        ZASound.AREA_BUY.play(loc1);
    }

    @Override public void paste(Location pointClosestToOrigin) {
        Location old = getPointClosestToOrigin();
        Location toLoc1 = pointClosestToOrigin.add(loc1.getX() - old.getX(), loc1.getY() - old.getY(), loc1.getZ() - old.getZ());
        Location toLoc2 = pointClosestToOrigin.add(loc2.getX() - old.getX(), loc2.getY() - old.getY(), loc2.getZ() - old.getZ());
        loc1 = toLoc1;
        loc2 = toLoc2;
        close();
        createStates();
        bt.remove();
        initBlinker();
    }

    @Override public void power(boolean power) {
        if (power) {
            open();
        } else {
            close();
        }
    }

    @Override public void powerOff() {
        close();
    }

    @Override public void powerOn() {
        open();
    }

    /**
     * Removes the passage.
     */
    @Override public void remove() {
        close();
        if (zag != null) {
            zag.removeObject(this);
        }
        bt.remove();
        data.objects.remove(this);
        data.objects.remove(bt);
        zag = null;
    }

    @Override public boolean requiresPower() {
        return requiresPower;
    }

    @Override public boolean requiresPurchaseFirst() {
        return false;
    }

    /**
     * Stops/Starts the blinker for this passage.
     * 
     * @param tf Whether or not this passage should blink
     */
    @Override public void setBlinking(boolean tf) {
        if (bt.isRunning()) {
            bt.remove();
        }
        if (tf) {
            if (!data.objects.contains(bt)) {
                initBlinker();
            }
            bt.setRunThrough(true);
        }
    }

    @Override public void setBought(boolean bought) {
        power(bought);
    }

    @Override @Deprecated public void setCost(int cost) {
        // nothing
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

    @Override public void setRequiresPower(boolean required) {
        requiresPower = required;
        bt.setColor(required ? DyeColor.ORANGE : DyeColor.BLUE);
    }

    @Override public void setRequiresPurchaseFirst(boolean purchase) {
        // nothing, not implemented
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
        bt = new BlinkerThread(blocks, DyeColor.BLUE, (Boolean) Setting.BLINKERS.getSetting(), 30, this);
    }
}
