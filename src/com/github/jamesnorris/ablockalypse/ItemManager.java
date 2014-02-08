package com.github.jamesnorris.ablockalypse;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.jamesnorris.ablockalypse.utility.BuyableItemData;

public class ItemManager {
    private Material[] autoRename = new Material[] {Material.ENDER_PEARL, Material.BOW, Material.FLOWER_POT_ITEM,
            Material.FLOWER_POT};
    private File file;
    private FileConfiguration fileconfig;
    private Map<Integer, BuyableItemData> ammoItems = new HashMap<Integer, BuyableItemData>();
    private Map<Integer, BuyableItemData> signItemMap = new HashMap<Integer, BuyableItemData>();
    private Map<Integer, BuyableItemData> startingItems = new HashMap<Integer, BuyableItemData>();

    public ItemManager(File file) {
        this.file = file;
        fileconfig = YamlConfiguration.loadConfiguration(file);
        // AMMO
        ammoItems.putAll(parseIdAmountFormat("ammo"));
        // STARTING ITEMS
        startingItems.putAll(parseIdAmountFormat("start"));
        // SIGN ITEMS
        ConfigurationSection signItemsSection = fileconfig.getConfigurationSection("sign");
        for (String key : signItemsSection.getKeys(false)) {
            String[] splitKey = key.split(Pattern.quote(":"));
            int id = Integer.parseInt(splitKey[0]);// will throw a NumberFormatException if a number is not given
            short data = key.lastIndexOf(":") != -1 ? Short.parseShort(splitKey[1]) : 0;
            String name = fileconfig.getString("sign." + key + ".name");
            String rename = fileconfig.getString("sign." + key + ".upgraded_name");
            int cost = fileconfig.getInt("sign." + key + ".cost");
            int amount = fileconfig.getInt("sign." + key + ".amount");
            int level = fileconfig.getInt("sign." + key + ".level");
            signItemMap.put(id, new BuyableItemData(id, data, name, rename, cost, amount, level));
        }
    }

    public BuyableItemData findItemInSignItemMaps(ItemStack item) {
        for (int id : signItemMap.keySet()) {
            if (id == item.getTypeId()) {
                return signItemMap.get(id);
            }
        }
        return null;
    }

    public Map<Integer, BuyableItemData> getAmmoItemMap() {
        return ammoItems;
    }

    public File getFile() {
        return file;
    }

    public BuyableItemData getItemInSignItemMapsById(int id) {
        return signItemMap.get(id);
    }

    public Map<Integer, BuyableItemData> getSignItemMap() {
        return signItemMap;
    }

    public Map<Integer, BuyableItemData> getStartingItemsMap() {
        return startingItems;
    }

    @SuppressWarnings("deprecation") public void giveItem(Player player, BuyableItemData item) {
        giveItem(player, new ItemStack(item.getId(), item.getAmount()));
    }

    public void giveItem(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() == -1) {
            return;// cannot give the item
        }
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

    public Map<Integer, BuyableItemData> parseIdAmountFormat(String section) {
        Map<Integer, BuyableItemData> items = new HashMap<Integer, BuyableItemData>();
        ConfigurationSection configSection = fileconfig.getConfigurationSection(section);
        for (String key : configSection.getKeys(false)) {
            String[] splitKey = key.split(Pattern.quote(":"));
            int id = Integer.parseInt(splitKey[0]);// will throw a NumberFormatException if a number is not given
            short data = key.lastIndexOf(":") != -1 ? Short.parseShort(splitKey[1]) : 0;
            int amount = fileconfig.getInt(section + "." + key + ".amount");
            items.put(id, new BuyableItemData(id, data, amount));
        }
        return items;
    }
}
