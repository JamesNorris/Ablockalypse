package com.github.JamesNorris.Implementation;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Enumerated.Setting;
import com.github.JamesNorris.Enumerated.ZAColor;
import com.github.JamesNorris.Enumerated.ZAEffect;
import com.github.JamesNorris.Enumerated.ZASound;
import com.github.JamesNorris.Interface.GameObject;
import com.github.JamesNorris.Interface.MysteryChest;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Manager.ItemManager;
import com.github.JamesNorris.Threading.BlinkerThread;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.MiscUtil;
import com.github.JamesNorris.Util.SoundUtil;

public class GameMysteryChest extends DataManipulator implements MysteryChest, GameObject {
	private boolean active = true;
	private BlinkerThread bt;
	private Object chest;
	private ZAGame game;
	private ItemManager im;
	private Item item;
	private ArrayList<ItemStack> its = new ArrayList<ItemStack>();
	private Location[] locs;
	private Random rand;
	private int uses;

	/**
	 * Creates a new instance of the GameMysteryChest.
	 * 
	 * @param chest The chest to be made into this instance
	 */
	public GameMysteryChest(Object chest, ZAGame game, Location loc, boolean active) {
		data.objects.add(this);
		Block b2 = MiscUtil.getSecondChest(loc.getBlock());
		this.locs = (b2 != null) ? new Location[] {loc, b2.getLocation()} : new Location[] {loc};
		for (Location location : locs)
			data.chests.put(location, this);
		this.chest = chest;
		im = new ItemManager();
		rand = new Random();
		this.game = game;
		game.addMysteryChest(this);
		this.active = active;
		uses = rand.nextInt(8) + 2;
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
	@Override public int getActiveUses() {
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
	@Override public Object getChest() {
		if (chest instanceof Chest)
			return chest;
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
	@Override public ZAGame getGame() {
		return game;
	}

	/**
	 * Gets the location that the chest is located at.
	 * 
	 * @return The location of the chest
	 */
	@Override public Location getLocation() {
		return locs[1];
	}

	/**
	 * Randomizes the contents of the MysteryChest.
	 */
	@SuppressWarnings("deprecation") @Override public void giveRandomItem(final Player p) {
		if (active) {
			--uses;
			int i = rand.nextInt(100);
			if (!its.isEmpty())
				its.clear();
			if (item != null) {
				item.remove();
				item = null;
			}
			if (i >= 95) {
				ItemStack it = new ItemStack(Material.BOW, 1);
				im.addEnchantment(it, Enchantment.ARROW_INFINITE, 1);
				its.add(it);
				its.add(new ItemStack(Material.ARROW, 1));
			} else if (i >= 92)
				its.add(new ItemStack(Material.GOLD_SWORD, 1));
			else if (i >= 67)
				its.add(new ItemStack(Material.DIAMOND_SWORD, 1));
			else if (i >= 46)
				its.add(new ItemStack(Material.IRON_SWORD, 1));
			else if (i >= 28)
				its.add(new ItemStack(Material.STONE_SWORD, 1));
			else if (i >= 10)
				its.add(new ItemStack(Material.WOOD_SWORD, 1));
			else
				its.add(new ItemStack(Material.ENDER_PEARL, 10));
			for (ItemStack it : its)
				MiscUtil.dropItemAtPlayer(locs[1], it, p);
			if ((Boolean) Setting.EXTRAEFFECTS.getSetting()) {
				SoundUtil.generateSound(p, ZASound.ACHIEVEMENT);
				EffectUtil.generateEffect(p, ZAEffect.FLAMES);
			}
			p.updateInventory();
			if (uses == 0)
				if ((Boolean) Setting.MOVINGCHESTS.getSetting()) {
					setActive(false);
					game.setActiveMysteryChest(game.getMysteryChests().get(rand.nextInt(game.getMysteryChests().size())));
				}
		} else
			MiscUtil.sendPlayerMessage(p, ChatColor.RED + "This chest is currently inactive!");
	}

	/**
	 * Checks if the chest is active or not.
	 * 
	 * @return Whether or not the chest is active and can be used
	 */
	@Override public boolean isActive() {
		return active;
	}

	/**
	 * Removes the mystery chest completely.
	 */
	@Override public void remove() {
		setActive(false);
		game.removeMysteryChest(this);
		data.objects.remove(this);
		for (Location loc : locs)
			data.chests.remove(loc);
		int size = game.getMysteryChests().size();
		if (size >= 1)
			game.setActiveMysteryChest(game.getMysteryChests().get(rand.nextInt(size)));
		setBlinking(false);
		bt.remove();
		data.blinkers.remove(bt);
		game = null;
	}

	/**
	 * Changes whether or not the chest will be active.
	 * 
	 * @param tf Whether or not the chest should be active
	 */
	@Override public void setActive(boolean tf) {
		if (tf) {
			if (uses == 0)
				uses = rand.nextInt(8) + 2;
			if ((Boolean) Setting.BEACONS.getSetting() && chest instanceof Chest)
				EffectUtil.generateEffect(((Chest) chest).getWorld(), ((Chest) chest).getLocation(), ZAEffect.BEACON);
		}
		active = tf;
	}

	/**
	 * Sets the uses before the mystery chest moves.
	 * 
	 * @param i The uses before movement
	 */
	@Override public void setActiveUses(int i) {
		uses = i;
	}

	@Override public void setBlinking(boolean tf) {
		if (bt.isRunning())
			bt.remove();
		if (tf) {
			if (!data.thread.contains(bt))
				initBlinker();
			bt.setRunThrough(true);
		}
	}
}
