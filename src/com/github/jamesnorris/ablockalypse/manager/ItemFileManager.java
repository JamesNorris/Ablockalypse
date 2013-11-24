package com.github.jamesnorris.ablockalypse.manager;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.jamesnorris.ablockalypse.aspect.intelligent.BuyableItemData;

public class ItemFileManager {
    private Material[] autoRename = new Material[] {Material.ENDER_PEARL, Material.BOW, Material.FLOWER_POT_ITEM, Material.FLOWER_POT};
    private File file;
    private FileConfiguration fileconfig;
    private HashMap<Integer, BuyableItemData> signItemMaps = new HashMap<Integer, BuyableItemData>();
    private HashMap<Integer, BuyableItemData> startingItems = new HashMap<Integer, BuyableItemData>();

    public ItemFileManager(File file) {
        this.file = file;
        fileconfig = YamlConfiguration.loadConfiguration(file);
        // STARTING ITEMS
        ConfigurationSection startingItemsSection = fileconfig.getConfigurationSection("gameStartItems");
        for (String key : startingItemsSection.getKeys(false)) {
            String[] splitKey = key.split(Pattern.quote(":"));
            int id = Integer.parseInt(splitKey[0]);// will throw a NumberFormatException if a number is not given
            short data = key.lastIndexOf(":") != -1 ? Short.parseShort(splitKey[1]) : 0;
            int amount = fileconfig.getInt("gameStartItems." + key + ".amount");
            startingItems.put(id, new BuyableItemData(id, data, amount));
        }
        // SIGN ITEMS
        ConfigurationSection signItemsSection = fileconfig.getConfigurationSection("signItems");
        for (String key : signItemsSection.getKeys(false)) {
            String[] splitKey = key.split(Pattern.quote(":"));
            int id = Integer.parseInt(splitKey[0]);// will throw a NumberFormatException if a number is not given
            short data = key.lastIndexOf(":") != -1 ? Short.parseShort(splitKey[1]) : 0;
            String name = fileconfig.getString("signItems." + key + ".name");
            String rename = fileconfig.getString("signItems." + key + ".upgraded_name");
            int cost = fileconfig.getInt("signItems." + key + ".cost");
            int amount = fileconfig.getInt("signItems." + key + ".amount");
            int level = fileconfig.getInt("signItems." + key + ".level");
            signItemMaps.put(id, new BuyableItemData(id, data, name, rename, cost, amount, level));
        }
    }

    public BuyableItemData findItemInSignItemMaps(ItemStack item) {
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

    public BuyableItemData getItemInSignItemMapsById(int id) {
        return signItemMaps.get(id);
    }

    public HashMap<Integer, BuyableItemData> getSignItemMaps() {
        return signItemMaps;
    }

    public HashMap<Integer, BuyableItemData> getStartingItemsMap() {
        return startingItems;
    }

    public void giveItem(Player player, ItemStack item) {
        BuyableItemData map = findItemInSignItemMaps(item);
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
