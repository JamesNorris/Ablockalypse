package com.github.JamesNorris.Manager;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.LocalizationData;
import com.github.JamesNorris.Util.Breakable;
import com.github.JamesNorris.Util.Breakable.ItemNameManager;

public class ItemManager {
	private Breakable b;
	private LocalizationData ld;

	/**
	 * The class that all items should run through for this game.
	 * Some items have names that are given in this class.
	 */
	public ItemManager() {
		b = new Breakable();
		ld = External.getYamlManager().getLocalizationData();
	}

	public void addEnchantment(ItemStack is, Enchantment e, int level) {
		is.addEnchantment(e, level);
	}

	public void giveItem(Player p, ItemStack is) {
		Material m = is.getType();
		ItemNameManager inm = b.new ItemNameManager(is);
		if (is.getType() == Material.ENDER_PEARL)
			inm.setName("Grenade");
		else if (!is.getEnchantments().isEmpty())
			if (m == Material.DIAMOND_SWORD)
				inm.setName(ld.diamondsword);
			else if (m == Material.GOLD_SWORD)
				inm.setName(ld.goldsword);
			else if (m == Material.IRON_SWORD)
				inm.setName(ld.ironsword);
			else if (m == Material.STONE_SWORD)
				inm.setName(ld.stonesword);
			else if (m == Material.WOOD_SWORD)
				inm.setName(ld.woodsword);
			else if (m == Material.BOW)
				inm.setName(ld.bow);
		p.getInventory().addItem(is);
	}

	public void giveItem(Player p, Material m, int amount) {
		giveItem(p, new ItemStack(m, amount));
	}
}
