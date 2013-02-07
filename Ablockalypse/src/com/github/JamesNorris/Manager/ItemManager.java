package com.github.JamesNorris.Manager;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.JamesNorris.Enumerated.Local;
import com.github.JamesNorris.Util.Breakable;
import com.github.JamesNorris.Util.Breakable.ItemNameManager;

public class ItemManager {
    private Breakable b;

    /**
     * The class that all items should run through for this game.
     * Some items have names that are given in this class.
     */
    public ItemManager() {
        b = new Breakable();
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
                inm.setName(Local.NAMEDDIAMONDSWORD.getSetting());
            else if (m == Material.GOLD_SWORD)
                inm.setName(Local.NAMEDGOLDSWORD.getSetting());
            else if (m == Material.IRON_SWORD)
                inm.setName(Local.NAMEDIRONSWORD.getSetting());
            else if (m == Material.STONE_SWORD)
                inm.setName(Local.NAMEDSTONESWORD.getSetting());
            else if (m == Material.WOOD_SWORD)
                inm.setName(Local.NAMEDWOODSWORD.getSetting());
            else if (m == Material.BOW)
                inm.setName(Local.NAMEDBOW.getSetting());
        p.getInventory().addItem(is);
    }

    public void giveItem(Player p, Material m, int amount) {
        giveItem(p, new ItemStack(m, amount));
    }
}
