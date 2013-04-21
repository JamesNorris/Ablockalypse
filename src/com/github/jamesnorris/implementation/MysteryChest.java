package com.github.jamesnorris.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.External;
import com.github.jamesnorris.enumerated.GameObjectType;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZAColor;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.enumerated.ZASound;
import com.github.jamesnorris.inter.Blinkable;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.inter.ZAScheduledTask;
import com.github.jamesnorris.threading.BlinkerThread;
import com.github.jamesnorris.util.ItemInfoMap;
import com.github.jamesnorris.util.MiscUtil;
import com.github.jamesnorris.util.Region;

public class MysteryChest implements GameObject, Blinkable {
    private DataContainer data = DataContainer.data;
    private boolean active = true;
    private BlinkerThread bt;
    private Object chest;
    private Game game;
    private Location[] locs;
    private Random rand;
    private int uses;
    private Location loc;

    /**
     * Creates a new instance of the GameMysteryChest.
     * 
     * @param chest The chest to be made into this instance
     * @param game The game to involve this mystery chest in
     * @param loc A location on the chest
     * @param active Whether or not this chest should be active
     */
    public MysteryChest(Object chest, Game game, Location loc, boolean active) {
        this.loc = loc;
        data.gameObjects.add(this);
        Block b2 = MiscUtil.getSecondChest(loc.getBlock());
        this.locs = (b2 != null) ? new Location[] {loc, b2.getLocation()} : new Location[] {loc};
        for (Location location : locs)
            data.chests.put(location, this);
        this.chest = chest;
        rand = new Random();
        this.game = game;
        game.addObject(this);
        this.active = active;
        uses = rand.nextInt(8) + 2;
        initBlinker();
    }

    private void initBlinker() {
        ArrayList<Block> blocks = getDefiningBlocks();
        boolean blinkers = (Boolean) Setting.BLINKERS.getSetting();
        bt = new BlinkerThread(blocks, ZAColor.BLUE, blinkers, 30, this);
    }

    /**
     * Gets the uses that this chest has before the mystery chest moves to another location.
     * 
     * @return The uses before movement
     */
    public int getActiveUses() {
        return uses;
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
     * Gets the chest associated with this instance.
     * 
     * @return The chest associated with this instance
     */
    public Chest getChest() {
        if (chest instanceof Chest)
            return (Chest) chest;
        return null;
    }

    /**
     * Gets the blocks that defines this object as an object.
     * 
     * @return The blocks assigned to this object
     */
    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        for (Location loc : locs)
            blocks.add(loc.getBlock());
        return blocks;
    }

    /**
     * Gets the game that this MysteryChest is attached to.
     * 
     * @return The game that uses this chest
     */
    @Override public Game getGame() {
        return game;
    }

    /**
     * Gets the location that the chest is located at.
     * 
     * @return The location of the chest
     */
    public Location getLocation() {
        return locs[1];
    }

    /**
     * Randomizes the contents of the MysteryChest.
     * 
     * @param zap The zaplayer to give the random item to
     */
    @SuppressWarnings("deprecation") public void giveRandomItem(final ZAPlayer zap) {
        Player p = zap.getPlayer();
        if (active) {
            --uses;
            HashMap<Integer, ItemInfoMap> maps = External.itemManager.getSignItemMaps();
            ArrayList<Integer> idList = new ArrayList<Integer>();
            for (int testId : maps.keySet()) {
                idList.add(testId);
            }
            int id = idList.get(rand.nextInt(idList.size()));// Solution for the lack of integer positions in item maps
            int cost = (Integer) Setting.CHEST_COST.getSetting();
            ItemInfoMap map = maps.get(id);
            if (MiscUtil.anyItemRegulationsBroken(zap, map.id, cost))
                return;
            ItemStack item = new ItemStack(Material.getMaterial(map.id), map.dropamount);
            Location topView = (locs[2] != null) ? new Region(locs[1], locs[2]).getCenter().clone().add(0, 1, 0) : locs[1].clone().add(0, 1, 0);
            final ArrayList<Player> players = new ArrayList<Player>();
            for (String name : game.getPlayers()) {
                players.add(Bukkit.getPlayer(name));
            }
            MiscUtil.setChestOpened(players, loc.getBlock(), true);
            Ablockalypse.getMainThread().scheduleDelayedTask(new ZAScheduledTask() {
                @Override public void run() {
                    MiscUtil.setChestOpened(players, loc.getBlock(), false);
                }
            }, 50);
            MiscUtil.dropItemAtPlayer(topView, item, p, 10);
            zap.subtractPoints(cost);
            if ((Boolean) Setting.EXTRA_EFFECTS.getSetting()) {
                ZASound.ACHIEVEMENT.play(p.getLocation());
                ZAEffect.FLAMES.play(p.getLocation());
            }
            p.updateInventory();
            if (uses == 0)
                if ((Boolean) Setting.MOVING_CHESTS.getSetting()) {
                    setActive(false);
                    List<MysteryChest> chests = game.getObjectsOfType(MysteryChest.class);
                    game.setActiveMysteryChest(chests.get(rand.nextInt(chests.size())));
                }
        } else {
            p.sendMessage(ChatColor.RED + "This chest is currently inactive!");
        }
    }

    /**
     * Checks if the chest is active or not.
     * 
     * @return Whether or not the chest is active and can be used
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Removes the mystery chest completely.
     */
    public void remove() {
        setActive(false);
        game.removeObject(this);
        data.gameObjects.remove(this);
        for (Location loc : locs)
            data.chests.remove(loc);
        List<MysteryChest> chests = game.getObjectsOfType(MysteryChest.class);
        int size = chests.size();
        if (size >= 1) {
            game.setActiveMysteryChest(chests.get(rand.nextInt(size)));
        }
        setBlinking(false);
        bt.remove();
        data.threads.remove(bt);
        game = null;
    }

    /**
     * Changes whether or not the chest will be active.
     * 
     * @param tf Whether or not the chest should be active
     */
    public void setActive(boolean tf) {
        if (tf) {
            if (uses == 0)
                uses = rand.nextInt(8) + 2;
        }
        active = tf;
    }

    /**
     * Sets the uses before the mystery chest moves.
     * 
     * @param i The uses before movement
     */
    public void setActiveUses(int i) {
        uses = i;
    }

    @Override public void setBlinking(boolean tf) {
        if (bt.isRunning())
            bt.remove();
        if (tf) {
            if (!data.threads.contains(bt))
                initBlinker();
            bt.setRunThrough(true);
        }
    }

    @Override public Block getDefiningBlock() {
        return loc.getBlock();
    }

    @Override public GameObjectType getObjectType() {
        return GameObjectType.MYSTERY_CHEST;
    }
}
