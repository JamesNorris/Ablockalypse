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

public class DoubleMysteryChest extends DataManipulator implements MysteryChest, GameObject {// TODO annotations
	private Location loc1, loc2;
	private Object chest;
	private Random rand;
	private Item item;
	private boolean active = true;
	private int uses;
	private ZAGame game;
	private ArrayList<ItemStack> its = new ArrayList<ItemStack>();
	private ItemManager im;
	private BlinkerThread bt;

	public DoubleMysteryChest(Object chest, ZAGame game, Location loc1, Location loc2, boolean active) {
		this.loc1 = loc1;
		this.loc2 = loc2;
		data.objects.add(this);
		data.chests.put(loc1, this);
		data.chests.put(loc2, this);
		this.chest = chest;
		im = new ItemManager();
		rand = new Random();
		this.game = game;
		this.active = active;
		uses = rand.nextInt(8) + 2;
		game.addMysteryChest(this);
		ArrayList<Block> blocks = new ArrayList<Block>();
		blocks.add(loc1.getBlock());
		blocks.add(loc2.getBlock());
		boolean blinkers = (Boolean) Setting.BLINKERS.getSetting();
		bt = new BlinkerThread(blocks, ZAColor.BLUE, blinkers, blinkers, 30, this);
	}

	/**
	 * Gets the blocks that defines this object as an object.
	 * 
	 * @return The blocks assigned to this object
	 */
	@Override public ArrayList<Block> getDefiningBlocks() {
		ArrayList<Block> blocks = new ArrayList<Block>();
		blocks.add(loc1.getBlock());
		blocks.add(loc2.getBlock());
		return blocks;
	}

	/**
	 * Gets the BlinkerThread attached to this instance.
	 * 
	 * @return The BlinkerThread attached to this instance
	 */
	@Override public BlinkerThread getBlinkerThread() {
		return bt;
	}

	@Override public int getActiveUses() {
		return uses;
	}

	@Override public Object getChest() {
		if (chest instanceof Chest)
			return chest;
		return null;
	}

	@Override public ZAGame getGame() {
		return game;
	}

	@Override public Location getLocation() {
		return loc1;
	}

	@Override public void giveRandomItem(Player p) {
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
				MiscUtil.dropItemAtPlayer(loc1, it, p);
			if ((Boolean) Setting.EXTRAEFFECTS.getSetting()) {
				SoundUtil.generateSound(p, ZASound.ACHIEVEMENT);
				EffectUtil.generateEffect(p, ZAEffect.FLAMES);
			}
			if (uses == 0)
				if ((Boolean) Setting.MOVINGCHESTS.getSetting()) {
					setActive(false);
					game.setActiveMysteryChest(game.getMysteryChests().get(rand.nextInt(game.getMysteryChests().size())));
				}
		} else
			p.sendMessage(ChatColor.RED + "This chest is currently inactive!");
	}

	@Override public boolean isActive() {
		return active;
	}

	@Override public void remove() {
		setActive(false);
		game.removeMysteryChest(this);
		data.objects.remove(this);
		data.chests.remove(loc1);
		data.chests.remove(loc2);
		int size = game.getMysteryChests().size();
		if (size >= 1)
			game.setActiveMysteryChest(game.getMysteryChests().get(rand.nextInt(size)));
		setBlinking(false);
		bt.cancel();
		data.blinkers.remove(bt);
		game = null;
	}

	@Override public void setActive(boolean tf) {
		if (tf) {
			if (uses == 0)
				uses = rand.nextInt(8) + 2;
			// if (cd.beacons)//TODO beacons
			if (chest instanceof Chest) {
				EffectUtil.generateEffect(((Chest) chest).getWorld(), loc1, ZAEffect.BEACON);
				EffectUtil.generateEffect(((Chest) chest).getWorld(), loc2, ZAEffect.BEACON);
			}
		}
		active = tf;
	}

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
