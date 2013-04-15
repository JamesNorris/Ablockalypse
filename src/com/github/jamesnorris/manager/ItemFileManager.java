package com.github.jamesnorris.manager;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.jamesnorris.External;
import com.github.jamesnorris.util.ItemInfoMap;

public class ItemFileManager {
    private File file;
    private FileConfiguration fileconfig;
    private HashMap<Integer, ItemInfoMap> signItemMaps = new HashMap<Integer, ItemInfoMap>();
    private HashMap<Integer, Integer> startingItems = new HashMap<Integer, Integer>();
    private Material[] autoRename = new Material[] {Material.ENDER_PEARL, Material.BOW, Material.FLOWER_POT_ITEM, Material.FLOWER_POT};

    public ItemFileManager(File file) {
        this.file = file;
        this.fileconfig = External.getConfig(file, External.items);
        // STARTING ITEMS
        ConfigurationSection startingItemsSection = fileconfig.getConfigurationSection("gameStartItems");
        for (String key : startingItemsSection.getKeys(false)) {
            int id = Integer.parseInt(key);//will throw a NumberFormatException if a number is not given
            int amount = fileconfig.getInt("gameStartItems." + key + ".amount");
            startingItems.put(id, amount);
        }
        // SIGN ITEMS
        ConfigurationSection signItemsSection = fileconfig.getConfigurationSection("signItems");
        for (String key : signItemsSection.getKeys(false)) {
            int id = Integer.parseInt(key);//will throw a NumberFormatException if a number is not given
            String name = fileconfig.getString("signItems." + key + ".name");
            String rename = fileconfig.getString("signItems." + key + ".upgraded_name");
            int cost = fileconfig.getInt("signItems." + key + ".cost");
            int amount = fileconfig.getInt("signItems." + key + ".amount");
            int level = fileconfig.getInt("signItems." + key + ".level");
            signItemMaps.put(id, new ItemInfoMap(id, name, rename, cost, amount, level));
        }
    }

    public boolean isAutoRenamed(Material test) {
        for (Material mat : autoRename) {
            if (mat == test)
                return true;
        }
        return false;
    }

    public void giveItem(Player player, ItemStack item) {
        ItemInfoMap map = findItemInSignItemMaps(item);
        if (map != null && ((map.rename != null && !item.getEnchantments().isEmpty()) || isAutoRenamed(item.getType()))) {
            item.getItemMeta().setDisplayName(map.rename);
        }
        if (item.getType() == Material.BOW) {
            player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
            item.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        }
        player.getInventory().addItem(item);
    }
    
    public HashMap<Integer, Integer> getStartingItemsMap() {
        return startingItems;
    }

    public ItemInfoMap findItemInSignItemMaps(ItemStack item) {
        for (int id : signItemMaps.keySet()) {
            if (id == item.getTypeId()) {
                return signItemMaps.get(id);
            }
        }
        return null;
    }

    public HashMap<Integer, ItemInfoMap> getSignItemMaps() {
        return signItemMaps;
    }

    public ItemInfoMap getItemInSignItemMapsById(int id) {
        return signItemMaps.get(id);
    }

    public File getFile() {
        return file;
    }
}
