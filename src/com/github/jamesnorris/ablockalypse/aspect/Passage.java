package com.github.jamesnorris.ablockalypse.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.behavior.MapDatable;
import com.github.jamesnorris.ablockalypse.enumerated.ZAEffect;
import com.github.jamesnorris.ablockalypse.enumerated.ZASound;
import com.github.jamesnorris.ablockalypse.utility.Cuboid;
import com.github.jamesnorris.ablockalypse.utility.SerialLocation;

public class Passage extends SpecificGameAspect implements MapDatable {
    private Location loc1, loc2;
    private ArrayList<BlockState> states = new ArrayList<BlockState>();
    private boolean opened;
    private Cuboid rectangle;
    private Game game;
    private UUID uuid = UUID.randomUUID();

    /**
     * Creates a new Passage instance that is represented by a rectangular prism.
     * 
     * @param game The game that should use this passage
     * @param loc1 The first corner of the rectangular prism
     * @param loc2 The second corner of the rectangular prism
     */
    @SuppressWarnings("serial") public Passage(Game game, final Location loc1, final Location loc2) {
        super(game, new ArrayList<Location>() {
            {
                add(loc1);
                add(loc2);
            }
        }, !game.hasStarted());
        rectangle = new Cuboid(loc1, loc2);
        this.loc1 = loc1;
        this.loc2 = loc2;
        this.game = game;
        opened = false;
        for (Location l : rectangle.getLocations()) {
            states.add(l.getBlock().getState());
        }
        load();
    }

    public Passage(Map<String, Object> savings) {
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
            ZAEffect.SMOKE.play(state.getLocation());
            state.update(true, true);
        }
    }

    @Override public List<Block> getBlinkerBlocks() {
        List<Block> blocks = new ArrayList<Block>();
        for (Location location : new Cuboid(loc1, loc2).getBorder()) {
            blocks.add(location.getBlock());
        }
        return blocks;
    }

    /**
     * Gets the blocks around the border of the passage.
     * 
     * @return The blocks around the border
     */
    public List<Location> getBorder() {
        return rectangle.getBorder();
    }

    @Override public List<Block> getDefiningBlocks() {
        List<Block> blocks = new ArrayList<Block>();
        for (Location location : new Cuboid(loc1, loc2).getLocations()) {
            blocks.add(location.getBlock());
        }
        return blocks;
    }

    @Override public int getLoadPriority() {
        return 2;
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

    @Override public Map<String, Object> getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("uuid", getUUID());
        savings.put("location_1", loc1 == null ? null : new SerialLocation(loc1));
        savings.put("location_2", loc2 == null ? null : new SerialLocation(loc2));
        savings.put("is_open", opened);
        savings.put("game_name", game.getName());
        return savings;
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

    @Override public void onGameEnd() {
        close();
        setBlinking(true);
    }

    @Override public void onGameStart() {
        setBlinking(false);
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
        loc1 = pointClosestToOrigin.add(loc1.getX() - old.getX(), loc1.getY() - old.getY(), loc1.getZ() - old.getZ());
        loc2 = pointClosestToOrigin.add(loc2.getX() - old.getX(), loc2.getY() - old.getY(), loc2.getZ() - old.getZ());
        close();
        states.clear();
        for (Location l : rectangle.getLocations()) {
            states.add(l.getBlock().getState());
        }
        refreshBlinker();
    }

    /**
     * Removes the passage.
     */
    @Override public void remove() {
        close();
        super.remove();
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
            rectangle = new Cuboid(loc1, loc2);
        }
    }

    @Override public void setPowered(boolean power) {
        if (!opened && power) {
            open();
        } else if (opened && !power) {
            close();
        }
        super.setPowered(power);
    }
}
