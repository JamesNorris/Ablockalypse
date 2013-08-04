package com.github.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.behavior.Blinkable;
import com.github.behavior.GameObject;
import com.github.behavior.MapDatable;
import com.github.behavior.Shop;
import com.github.behavior.ZAScheduledTask;
import com.github.enumerated.Setting;
import com.github.enumerated.ZAEffect;
import com.github.enumerated.ZASound;
import com.github.threading.inherent.BlinkerThread;
import com.github.utility.BuyableItem;
import com.github.utility.MiscUtil;
import com.github.utility.selection.Region;
import com.github.utility.serial.SavedVersion;
import com.github.utility.serial.SerialLocation;

public class MysteryChest extends PermanentAspect implements GameObject, Blinkable, Shop, MapDatable {
    private boolean active = true;
    private BlinkerThread bt;
    private Object chest;
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private Location loc;
    private Location[] locs;
    private Random rand = new Random();
    private int uses, cost = (Integer) Setting.CHEST_COST.getSetting();
    private final UUID uuid = UUID.randomUUID();

    /**
     * Creates a new instance of the GameMysteryChest.
     * 
     * @param chest The chest to be made into this instance
     * @param game The game to involve this mystery chest in
     * @param loc A location on the chest
     * @param active Whether or not this chest should be active
     */
    public MysteryChest(Game game, Location loc, boolean active) {
        this.loc = loc;
        data.objects.add(this);
        Block b2 = MiscUtil.getSecondChest(loc.getBlock());
        locs = b2 != null ? new Location[] {loc, b2.getLocation()} : new Location[] {loc};
        chest = loc.getBlock().getState();
        this.game = game;
        game.addObject(this);
        this.active = active;
        uses = rand.nextInt(8) + 2;
        initBlinker();
    }

    public MysteryChest(SavedVersion savings) {
        this(Ablockalypse.getData().getGame((String) savings.get("game_name"), true), SerialLocation.returnLocation((SerialLocation) savings.get("location")), (Boolean) savings.get("is_active"));
        uses = (Integer) savings.get("uses_left");
        cost = (Integer) savings.get("cost");
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
        if (chest instanceof Chest) {
            return (Chest) chest;
        }
        return null;
    }

    @Override public int getCost() {
        return cost;
    }

    @Override public Block getDefiningBlock() {
        return loc.getBlock();
    }

    /**
     * Gets the blocks that defines this object as an object.
     * 
     * @return The blocks assigned to this object
     */
    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        for (Location loc : locs) {
            blocks.add(loc.getBlock());
        }
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

    @Override public String getHeader() {
        return this.getClass().getSimpleName() + " <UUID: " + getUUID() + ">";
    }

    /**
     * Gets the location that the chest is located at.
     * 
     * @return The location of the chest
     */
    public Location getLocation() {
        return locs[0];
    }

    @Override public Location getPointClosestToOrigin() {
        return loc;
    }

    @Override public SavedVersion getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("is_active", active);
        savings.put("game_name", game.getName());
        savings.put("location", loc == null ? null : new SerialLocation(loc));
        savings.put("uses_left", uses);
        savings.put("cost", cost);
        return new SavedVersion(getHeader(), savings, getClass());
    }

    @Override public UUID getUUID() {
        return uuid;
    }

    /**
     * Randomizes the contents of the MysteryChest.
     * 
     * @param zap The zaplayer to give the random item to
     */
    @SuppressWarnings("deprecation") public void giveRandomItem(final ZAPlayer zap) {
        Player p = zap.getPlayer();
        if (active) {
            HashMap<Integer, BuyableItem> maps = Ablockalypse.getExternal().getItemFileManager().getSignItemMaps();
            ArrayList<Integer> idList = new ArrayList<Integer>();
            for (int testId : maps.keySet()) {
                idList.add(testId);
            }
            int id = idList.get(rand.nextInt(idList.size()));// Solution for the lack of integer positions in item maps
            BuyableItem map = maps.get(id);
            ItemStack item = map.toItemStack();
            if (zap.getPoints() < cost) {
                zap.getPlayer().sendMessage(ChatColor.RED + "You have " + zap.getPoints() + " / " + cost + " points to buy this.");
                return;
            }
            Location topView = locs[1] != null ? new Region(locs[0], locs[1]).getCenter().clone().add(0, 1, 0) : locs[0].clone().add(0, 1, 0);
            final ArrayList<Player> players = new ArrayList<Player>();
            for (ZAPlayer zap2 : game.getPlayers()) {
                players.add(zap2.getPlayer());
            }
            MiscUtil.setChestOpened(players, loc.getBlock(), true);
            final Item i = topView.getWorld().dropItem(topView, item);
            i.setPickupDelay(Integer.MAX_VALUE);
            Ablockalypse.getMainThread().scheduleDelayedTask(new ZAScheduledTask() {
                @Override public void run() {
                    MiscUtil.setChestOpened(players, loc.getBlock(), false);
                }
            }, 50);
            MiscUtil.dropItemAtPlayer(topView, item, p, 40, 60);
            Ablockalypse.getMainThread().scheduleDelayedTask(new ZAScheduledTask() {
                @Override public void run() {
                    i.remove();
                }
            }, 40);
            --uses;
            zap.subtractPoints(cost);
            if ((Boolean) Setting.EXTRA_EFFECTS.getSetting()) {
                ZASound.ACHIEVEMENT.play(p.getLocation());
                ZAEffect.FLAMES.play(p.getLocation());
            }
            p.updateInventory();
            if (uses == 0) {
                if ((Boolean) Setting.MOVING_CHESTS.getSetting()) {
                    setActive(false);
                    List<MysteryChest> chests = game.getObjectsOfType(MysteryChest.class);
                    game.setActiveMysteryChest(chests.get(rand.nextInt(chests.size())));
                }
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

    @Override public void paste(Location pointClosestToOrigin) {
        loc = pointClosestToOrigin;
        bt.remove();
        initBlinker();
    }

    /**
     * Removes the mystery chest completely.
     */
    @Override public void remove() {
        setActive(false);
        game.removeObject(this);
        data.objects.remove(this);
        List<MysteryChest> chests = game.getObjectsOfType(MysteryChest.class);
        int size = chests.size();
        if (size >= 1) {
            game.setActiveMysteryChest(chests.get(rand.nextInt(size)));
        }
        setBlinking(false);
        bt.remove();
        data.objects.remove(bt);
        game = null;
    }

    /**
     * Changes whether or not the chest will be active.
     * 
     * @param tf Whether or not the chest should be active
     */
    public void setActive(boolean tf) {
        if (tf) {
            if (uses == 0) {
                uses = rand.nextInt(8) + 2;
            }
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

    @Override public void setCost(int cost) {
        this.cost = cost;
    }

    private void initBlinker() {
        ArrayList<Block> blocks = getDefiningBlocks();
        boolean blinkers = (Boolean) Setting.BLINKERS.getSetting();
        bt = new BlinkerThread(blocks, DyeColor.BLUE, blinkers, 30, this);
    }
}
