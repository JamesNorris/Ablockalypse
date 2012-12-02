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

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Interface.Blinkable;
import com.github.JamesNorris.Interface.GameObject;
import com.github.JamesNorris.Interface.MysteryChest;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Manager.ItemManager;
import com.github.JamesNorris.Threading.BlinkerThread;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.Enumerated.ZAColor;
import com.github.JamesNorris.Util.Enumerated.ZAEffect;
import com.github.JamesNorris.Util.Enumerated.ZASound;
import com.github.JamesNorris.Util.MiscUtil;
import com.github.JamesNorris.Util.SoundUtil;

public class SingleMysteryChest implements MysteryChest, GameObject, Blinkable {
	private ConfigurationData cd;
	private Object chest;
	private Random rand;
	private Item item;
	private boolean active = true;
	private int uses;
	private Location loc;
	private ZAGame game;
	private ArrayList<ItemStack> its = new ArrayList<ItemStack>();
	private ItemManager im;
	private BlinkerThread bt;

	/**
	 * Creates a new instance of the MysteryChest.
	 * 
	 * @param chest The chest to be made into this instance
	 */
	public SingleMysteryChest(Object chest, ZAGame game, Location loc, boolean active) {
		GlobalData.objects.add(this);
		GlobalData.chests.put(loc, this);
		this.chest = chest;
		im = new ItemManager();
		rand = new Random();
		this.game = game;
		game.addMysteryChest(this);
		this.loc = loc;
		this.active = active;
		uses = rand.nextInt(8) + 2;
		cd = External.getYamlManager().getConfigurationData();
		ArrayList<Block> blocks = new ArrayList<Block>();
		blocks.add(loc.getBlock());
		bt = new BlinkerThread(blocks, ZAColor.BLUE, cd.blinkers, 60, this);
	}

	/**
	 * Gets the blocks that defines this object as an object.
	 * 
	 * @return The blocks assigned to this object
	 */
	public Block[] getDefiningBlocks() {
		return new Block[] {loc.getBlock()};
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
	 * Gets the uses that this chest has before the mystery chest moves to another location.
	 * 
	 * @return The uses before movement
	 */
	@Override public int getActiveUses() {
		return uses;
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
		return loc;
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
				MiscUtil.dropItemAtPlayer(loc, it, p);
			if (cd.extraEffects) {
				SoundUtil.generateSound(p, ZASound.ACHIEVEMENT);
				EffectUtil.generateEffect(p, ZAEffect.FLAMES);
			}
			p.updateInventory();
			if (uses == 0)
				if (cd.movingchests) {
					setActive(false);
					game.setActiveMysteryChest(game.getMysteryChests().get(game.getMysteryChests().size()));
				}
		} else
			p.sendMessage(ChatColor.RED + "This chest is currently inactive!");
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
		GlobalData.objects.remove(this);
		GlobalData.chests.remove(loc);
		game.setActiveMysteryChest(game.getMysteryChests().get(game.getMysteryChests().size()));
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
			// if (cd.beacons)//TODO beacons
			if (chest instanceof Chest)
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
			bt.cancel();
		if (tf)
			bt.blink();
	}
}
