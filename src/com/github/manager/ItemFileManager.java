package com.github.manager;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.utility.BuyableItem;

public class ItemFileManager {
    private Material[] autoRename = new Material[] {Material.ENDER_PEARL, Material.BOW, Material.FLOWER_POT_ITEM, Material.FLOWER_POT};
    private File file;
    private FileConfiguration fileconfig;
    private HashMap<Integer, BuyableItem> signItemMaps = new HashMap<Integer, BuyableItem>();
    private HashMap<Integer, Integer> startingItems = new HashMap<Integer, Integer>();

    public ItemFileManager(File file) {
        this.file = file;
        fileconfig = YamlConfiguration.loadConfiguration(file);
        // STARTING ITEMS
        ConfigurationSection startingItemsSection = fileconfig.getConfigurationSection("gameStartItems");
        for (String key : startingItemsSection.getKeys(false)) {
            int id = Integer.parseInt(key);// will throw a NumberFormatException if a number is not given
            int amount = fileconfig.getInt("gameStartItems." + key + ".amount");
            startingItems.put(id, amount);
        }
        // SIGN ITEMS
        ConfigurationSection signItemsSection = fileconfig.getConfigurationSection("signItems");
        for (String key : signItemsSection.getKeys(false)) {
            int id = Integer.parseInt(key);// will throw a NumberFormatException if a number is not given
            String name = fileconfig.getString("signItems." + key + ".name");
            String rename = fileconfig.getString("signItems." + key + ".upgraded_name");
            int cost = fileconfig.getInt("signItems." + key + ".cost");
            int amount = fileconfig.getInt("signItems." + key + ".amount");
            int level = fileconfig.getInt("signItems." + key + ".level");
            signItemMaps.put(id, new BuyableItem(id, name, rename, cost, amount, level));
        }
    }

    public BuyableItem findItemInSignItemMaps(ItemStack item) {
        for (int id : signItemMaps.keySet()) {
            if (id == item.getTypeId()) {
                return signItemMaps.get(id);
            }
        }
        return null;
    }

    public File getFile() {
        return file;
    }

    public BuyableItem getItemInSignItemMapsById(int id) {
        return signItemMaps.get(id);
    }

    public HashMap<Integer, BuyableItem> getSignItemMaps() {
        return signItemMaps;
    }

    public HashMap<Integer, Integer> getStartingItemsMap() {
        return startingItems;
    }

    public void giveItem(Player player, ItemStack item) {
        BuyableItem map = findItemInSignItemMaps(item);
        if (map != null && (map.getUpgName() != null && !item.getEnchantments().isEmpty() || isAutoRenamed(item.getType()))) {
            item.getItemMeta().setDisplayName(map.getUpgName());
        }
        if (item.getType() == Material.BOW) {
            player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
            item.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        }
        player.getInventory().addItem(item);
    }

    public boolean isAutoRenamed(Material test) {
        for (Material mat : autoRename) {
            if (mat == test) {
                return true;
            }
        }
        return false;
    }
}
