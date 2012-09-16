package com.github.Ablockalypse.JamesNorris.Implementation;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.Ablockalypse.JamesNorris.Interface.MysteryChestInterface;

public class MysteryChest implements MysteryChestInterface {
	private Chest chest;
	private Random rand;

	/**
	 * Creates a new instance of the MysteryChest.
	 * 
	 * @param chest The chest to be made into this instance
	 */
	public MysteryChest(Chest chest) {
		this.chest = chest;
		rand = new Random();
	}

	@Override public Chest getChest() {
		return chest;
	}

	/**
	 * Randomizes the contents of the MysteryChest.
	 */
	@Override public void randomize() {
		Inventory inv = chest.getBlockInventory();
		inv.clear();
		int i = rand.nextInt(100) + 1;
		if (i >= 90)
			inv.addItem(new ItemStack(Material.GOLD_SWORD, 1));
		else if (i >= 60)
			inv.addItem(new ItemStack(Material.DIAMOND_SWORD, 1));
		else if (i >= 40)
			inv.addItem(new ItemStack(Material.IRON_SWORD, 1));
		else if (i >= 20)
			inv.addItem(new ItemStack(Material.STONE_SWORD, 1));
		else
			inv.addItem(new ItemStack(Material.WOOD_SWORD, 1));
	}
}
