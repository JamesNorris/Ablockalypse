package com.github.aspect.block;

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
import com.github.aspect.PermanentAspect;
import com.github.aspect.entity.ZAPlayer;
import com.github.aspect.intelligent.BuyableItemData;
import com.github.aspect.intelligent.Game;
import com.github.behavior.Blinkable;
import com.github.behavior.GameObject;
import com.github.behavior.MapDatable;
import com.github.enumerated.Setting;
import com.github.enumerated.ZAEffect;
import com.github.enumerated.ZASound;
import com.github.threading.DelayedTask;
import com.github.threading.inherent.BlinkerTask;
import com.github.utility.BukkitUtility;
import com.github.utility.selection.Region;
import com.github.utility.serial.SavedVersion;
import com.github.utility.serial.SerialLocation;

public class MysteryBox extends PermanentAspect implements GameObject, Blinkable, MapDatable {
    private boolean active = true;
    private BlinkerTask bt;
    private Object chest;
    private DataContainer data = Ablockalypse.getData();
    private Game game;
    private Location loc;
    private Location[] locs;
    private Random rand = new Random();
    private int uses, cost = (Integer) Setting.MYSTERY_BOX_COST.getSetting();
    private UUID uuid = UUID.randomUUID();

    /**
     * Creates a new instance of the MysteryBox.
     * 
     * @param chest The chest to be made into this instance
     * @param game The game to involve this mystery chest in
     * @param loc A location on the chest
     * @param active Whether or not this chest should be active
     */
    public MysteryBox(Game game, Location loc, boolean active) {
        this.loc = loc;
        data.objects.add(this);
        Block b2 = BukkitUtility.getSecondChest(loc.getBlock());
        locs = b2 != null ? new Location[] {loc, b2.getLocation()} : new Location[] {loc};
        chest = loc.getBlock().getState();
        this.game = game;
        game.addObject(this);
        this.active = active;
        uses = rand.nextInt(8) + 2;
        initBlinker();
    }

    public MysteryBox(SavedVersion savings) {
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
     * Gets the BlinkerThread attached to this instance.
     * 
     * @return The BlinkerThread attached to this instance
     */
    @Override public BlinkerTask getBlinkerThread() {
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
        return this.getClass().getSimpleName() + " <UUID: " + getUUID().toString() + ">";
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
        savings.put("uuid", getUUID());
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
            HashMap<Integer, BuyableItemData> maps = Ablockalypse.getExternal().getItemFileManager().getSignItemMaps();
            ArrayList<Integer> idList = new ArrayList<Integer>();
            for (int testId : maps.keySet()) {
                idList.add(testId);
            }
            int id = idList.get(rand.nextInt(idList.size()));// Solution for the lack of integer positions in item maps
            BuyableItemData map = maps.get(id);
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
            BukkitUtility.setChestOpened(players, loc.getBlock(), true);
            final Item i = topView.getWorld().dropItem(topView, item);
            i.setPickupDelay(Integer.MAX_VALUE);
            new DelayedTask(50, true) {
                @Override public void run() {
                    BukkitUtility.setChestOpened(players, loc.getBlock(), false);
                }
            };
            BukkitUtility.dropItemAtPlayer(topView, item, p, 40, 60);
            new DelayedTask(40, true) {
                @Override public void run() {
                    i.remove();
                }
            };
            --uses;
            zap.subtractPoints(cost);
            if ((Boolean) Setting.EXTRA_EFFECTS.getSetting()) {
                ZASound.ACHIEVEMENT.play(p.getLocation());
                ZAEffect.FLAMES.play(p.getLocation());
            }
            p.updateInventory();
            if (uses == 0) {
                if ((Boolean) Setting.MOVING_MYSTERY_BOXES.getSetting()) {
                    setActive(false);
                    List<MysteryBox> chests = game.getObjectsOfType(MysteryBox.class);
                    game.setActiveMysteryChest(chests.get(rand.nextInt(chests.size())));
                }
            }
        } else {
            p.sendMessage(ChatColor.RED + "This box is currently inactive!");
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
        bt.cancel();
        initBlinker();
    }

    /**
     * Removes the mystery chest completely.
     */
    @Override public void remove() {
        setActive(false);
        game.removeObject(this);
        data.objects.remove(this);
        List<MysteryBox> chests = game.getObjectsOfType(MysteryBox.class);
        int size = chests.size();
        if (size >= 1) {
            game.setActiveMysteryChest(chests.get(rand.nextInt(size)));
        }
        setBlinking(false);
        bt.cancel();
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

    /**
     * Stops/Starts the blinker for this barrier.
     * 
     * @param tf Whether or not this barrier should blink
     */
    @Override public void setBlinking(boolean tf) {
        bt.setRunning(tf);
    }

    private void initBlinker() {
        ArrayList<Block> blocks = getDefiningBlocks();
        boolean blinkers = (Boolean) Setting.BLINKERS.getSetting();
        bt = new BlinkerTask(blocks, DyeColor.BLUE, 30, blinkers);
    }
    
    @Override public void onGameEnd() {
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
