package com.github.iKeirNez.Util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class StartingItems {
	/***
	 * Seperate's item data from a config String and returns it as an ItemStack.
	 * 
	 * @param s A string with itemstack arguments
	 * @return The ItemStack from the string given
	 */
	public static ItemStack seperateStartingItemsData(String s) {
		Material item = null;
		int amount = 1;
		short damage = 0;
		Enchantment ench = null;
		int enchLevel = 1;
		String[] data = s.split(" ");
		if (data[0].contains(":")) {
			String[] itemInfo = data[0].split(":");
			item = Material.getMaterial(itemInfo[0]);
			damage = Short.valueOf(itemInfo[1]);
		} else {
			item = Material.getMaterial(data[0]);
		}
		amount = Integer.valueOf(data[1]);
		if (data.length == 4) {
			String[] enchInfo = {data[2], data[3]};
			// TODO add multiple enchantment support
			try {
				ench = Enchantment.getById(Integer.valueOf(enchInfo[0]));
			} catch (Exception e) {
				ench = Enchantment.getByName(enchInfo[0]);
			}
			enchLevel = Integer.valueOf(enchInfo[1]);
		}
		ItemStack toReturn = new ItemStack(item, amount, damage);
		if (ench != null) {
			toReturn.addUnsafeEnchantment(ench, enchLevel);
		}
		return toReturn;
	}
}
