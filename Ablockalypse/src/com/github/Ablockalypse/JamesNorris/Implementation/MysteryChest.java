package com.github.Ablockalypse.JamesNorris.Implementation;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.Ablockalypse.JamesNorris.Data.ConfigurationData;
import com.github.Ablockalypse.JamesNorris.Interface.MysteryChestInterface;
import com.github.Ablockalypse.JamesNorris.Util.External;

public class MysteryChest implements MysteryChestInterface {
	private final Chest chest;
	private final Random rand;
	private final ConfigurationData cd;

	/**
	 * Creates a new instance of the MysteryChest.
	 * 
	 * @param chest The chest to be made into this instance
	 */
	public MysteryChest(final Chest chest) {
		this.chest = chest;
		rand = new Random();
		cd = External.getYamlManager().getConfigurationData();
	}

	/**
	 * Gets the chest associated with this instance.
	 * 
	 * @return The chest associated with this instance
	 */
	@Override public Chest getChest() {
		return chest;
	}

	/**
	 * Randomizes the contents of the MysteryChest.
	 */
	@Override public void randomize() {
		final Inventory inv = chest.getBlockInventory();
		inv.clear();
		final int i = rand.nextInt(1000) + 1;
		if (i >= 950) {
			final ItemStack it = new ItemStack(Material.BOW, 1);
			it.addEnchantment(Enchantment.ARROW_INFINITE, 1);
			inv.addItem(it);
		} else if (i >= 920)
			inv.addItem(new ItemStack(Material.GOLD_SWORD, 1));
		else if (i >= 670)
			inv.addItem(new ItemStack(Material.DIAMOND_SWORD, 1));
		else if (i >= 460)
			inv.addItem(new ItemStack(Material.IRON_SWORD, 1));
		else if (i >= 280)
			inv.addItem(new ItemStack(Material.STONE_SWORD, 1));
		else if (i >= 100)
			inv.addItem(new ItemStack(Material.WOOD_SWORD, 1));
		else
			inv.addItem(new ItemStack(Material.ENDER_PEARL, 10));
		if (cd.effects)
			chest.getWorld().playEffect(chest.getLocation(), Effect.POTION_BREAK, 1);
	}
}
