package com.github.jamesnorris.ablockalypse.aspect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.behavior.MapDatable;
import com.github.jamesnorris.ablockalypse.enumerated.Setting;
import com.github.jamesnorris.ablockalypse.enumerated.ZAEffect;
import com.github.jamesnorris.ablockalypse.enumerated.ZASound;
import com.github.jamesnorris.ablockalypse.threading.DelayedTask;
import com.github.jamesnorris.ablockalypse.threading.RepeatingTask;
import com.github.jamesnorris.ablockalypse.utility.AblockalypseUtility;
import com.github.jamesnorris.ablockalypse.utility.BukkitUtility;
import com.github.jamesnorris.ablockalypse.utility.BuyableItemData;
import com.github.jamesnorris.ablockalypse.utility.Region;
import com.github.jamesnorris.ablockalypse.utility.SerialLocation;

public class MysteryBox extends SpecificGameAspect implements MapDatable {
    private boolean active = true, showing;
    private Object chest;
    private Game game;
    private Location loc;
    private Location[] locs;
    private Random rand = new Random();
    private int uses, cost = (Integer) Setting.MYSTERY_BOX_COST.getSetting();
    private UUID uuid = UUID.randomUUID();
    private ItemStack lastShownItem;
    private ZAPlayer lastShownZAPlayer;

    /**
     * Creates a new instance of the MysteryBox.
     * 
     * @param game The game to involve this mystery chest in
     * @param loc A location on the chest
     */
    public MysteryBox(Game game, Location loc) {
        this(game, loc, game.getActiveMysteryChest() == null);
    }

    /**
     * Creates a new instance of the MysteryBox.
     * 
     * @param game The game to involve this mystery chest in
     * @param loc A location on the chest
     * @param active Whether or not this chest should be active
     */
    public MysteryBox(Game game, Location loc, boolean active) {
        super(game, loc, !game.hasStarted());
        this.loc = loc;
        Block b2 = BukkitUtility.getSecondChest(loc.getBlock());
        locs = b2 != null ? new Location[] {loc, b2.getLocation()} : new Location[] {loc};
        chest = loc.getBlock().getState();
        this.game = game;
        this.active = active;
        uses = rand.nextInt(8) + 2;
        load();
    }

    public MysteryBox(Map<String, Object> savings) {
        this(Ablockalypse.getData().getGame((String) savings.get("game_name"), true), SerialLocation.returnLocation((SerialLocation) savings.get("location")), (Boolean) savings.get("is_active"));
        uses = (Integer) savings.get("uses_left");
        cost = (Integer) savings.get("cost");
        uuid = savings.get("uuid") == null ? uuid : (UUID) savings.get("uuid");
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

    @Override public List<Block> getDefiningBlocks() {
        List<Block> blocks = new ArrayList<Block>();
        for (Location location : locs) {
            blocks.add(location.getBlock());
        }
        return blocks;
    }

    public ItemStack getLastShownItem() {
        return lastShownItem;
    }

    public ZAPlayer getLastShownZAPlayer() {
        return lastShownZAPlayer;
    }

    @Override public int getLoadPriority() {
        return 2;
    }

    /**
     * Gets the location that the chest is located at.
     * 
     * @return The location of the chest
     */
    @Override public Location getLocation() {
        return locs[0];
    }

    @Override public Location getPointClosestToOrigin() {
        return loc;
    }

    @Override public Map<String, Object> getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("uuid", getUUID());
        savings.put("is_active", active);
        savings.put("game_name", game.getName());
        savings.put("location", loc == null ? null : new SerialLocation(loc));
        savings.put("uses_left", uses);
        savings.put("cost", cost);
        return savings;
    }

    @Override public UUID getUUID() {
        return uuid;
    }

    /**
     * Checks if the chest is active or not.
     * 
     * @return Whether or not the chest is active and can be used
     */
    public boolean isActive() {
        return active;
    }

    public boolean isShowingItem() {
        return showing;
    }

    @Override public void onGameEnd() {
        setBlinking(true);
    }

    @Override public void onGameStart() {
        setBlinking(false);
    }

    /**
     * Randomizes the contents of the MysteryChest.
     * 
     * @param zap The zaplayer to give the random item to
     */
    @SuppressWarnings("deprecation") public void openToRandomItem(ZAPlayer zap) {
        final Player player = zap.getPlayer();
        if (!active) {
            player.sendMessage(ChatColor.RED + "This box is currently inactive!");
            return;
        }
        Map<Integer, BuyableItemData> maps = Ablockalypse.getExternal().getItemFileManager().getSignItemMap();
        ArrayList<Integer> idList = new ArrayList<Integer>();
        for (int testId : maps.keySet()) {
            idList.add(testId);
        }
        int id = idList.get(rand.nextInt(idList.size()));// Solution for the lack of integer positions in item maps
        BuyableItemData map = maps.get(id);
        if (zap.getPoints() < cost) {
            zap.getPlayer().sendMessage(ChatColor.RED + "You have " + zap.getPoints() + " / " + cost + " points to buy this.");
            return;
        }
        final Location topView = locs[1] != null ? new Region(locs[0], locs[1]).getCenter().clone().add(0, 1, 0) : locs[0].clone().add(0, 1, 0);
        final Item dropped = showItem(new ItemStack(Material.SKULL_ITEM, 1, (short) 4), 140);
        lastShownItem = map.toItemStack();
        lastShownZAPlayer = zap;
        if ((Boolean) Setting.EXTRA_EFFECTS.getSetting()) {
            ZASound.ACHIEVEMENT.play(topView);
        }
        AblockalypseUtility.scheduleNearbyWarning(topView, ChatColor.GRAY + "Press " + ChatColor.AQUA + "SHIFT" + ChatColor.GRAY + " to take your item.", 2, 3.5, 2, 7000);
        new RepeatingTask(20, (Boolean) Setting.EXTRA_EFFECTS.getSetting()) {
            @Override public void run() {
                ZAEffect.FLAMES.play(topView);
            }
        };
        new DelayedTask(140, true) {
            @Override public void run() {
                dropped.remove();
            }
        };
        --uses;
        zap.subtractPoints(cost);
        player.updateInventory();
        if (uses == 0 && (Boolean) Setting.MOVING_MYSTERY_BOXES.getSetting()) {
            setActive(false);
            List<MysteryBox> chests = game.getObjectsOfType(MysteryBox.class);
            game.setActiveMysteryChest(chests.get(rand.nextInt(chests.size())));
        }
    }

    @Override public void paste(Location pointClosestToOrigin) {
        loc = pointClosestToOrigin;
        refreshBlinker();
    }

    /**
     * Removes the mystery chest completely.
     */
    @Override public void remove() {
        setActive(false);
        List<MysteryBox> chests = game.getObjectsOfType(MysteryBox.class);
        int size = chests.size();
        if (size >= 1) {
            game.setActiveMysteryChest(chests.get(rand.nextInt(size)));
        }
        super.remove();
    }

    /**
     * Changes whether or not the chest will be active.
     * 
     * @param tf Whether or not the chest should be active
     */
    public void setActive(boolean tf) {
        if (tf && uses == 0) {
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

    public Item showItem(ItemStack stack, int duration) {
        showing = true;
        Location topView = locs[1] != null ? new Region(locs[0], locs[1]).getCenter().clone().add(0, 1, 0) : locs[0].clone().add(0, 1, 0);
        BukkitUtility.setChestOpened(Arrays.asList(Bukkit.getOnlinePlayers()), loc.getBlock(), true);
        Item item = topView.getWorld().dropItem(topView, stack);
        item.setPickupDelay(Integer.MAX_VALUE);
        new DelayedTask(duration, true) {
            @Override public void run() {
                stopShowing();
            }
        };
        return item;
    }

    public void stopShowing() {
        BukkitUtility.setChestOpened(Arrays.asList(Bukkit.getOnlinePlayers()), loc.getBlock(), false);
        showing = false;
    }
}
