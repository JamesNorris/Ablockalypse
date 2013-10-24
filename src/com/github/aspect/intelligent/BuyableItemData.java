package com.github.aspect.intelligent;

import org.bukkit.inventory.ItemStack;

public class BuyableItemData {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getRequiredLevel() {
        return level;
    }

    public void setRequiredLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpgName() {
        return upgName;
    }

    public void setUpgName(String upgName) {
        this.upgName = upgName;
    }

    private int id, data, cost, amount, level;
    private String name, upgName;
    
    public BuyableItemData(int id, int data, int amount) {
        this(id, data, new ItemStack(id, 1).getItemMeta().getDisplayName(), new ItemStack(id, 1).getItemMeta().getDisplayName(), 0, amount, 0);
    }
    
    public BuyableItemData(int id, String name, int cost) {
        this(id, name, cost, 1);
    }
    
    public BuyableItemData(int id, String name, int cost, int level) {
        this(id, name, name + " - UPG", cost, level);
    }
    
    public BuyableItemData(int id, String name, String upgName, int cost, int level) {
        this(id, 0, name, upgName, cost, 1, level);
    }
    
    public BuyableItemData(int id, int data, String name, String upgName, int cost, int amount, int level) {
        this.id = id;
        this.data = data;
        this.name = name;
        this.upgName = upgName;
        this.cost = cost;
        this.amount = amount;
        this.level = level;
    }
    
    public ItemStack toItemStack() {
        return toItemStack(false);
    }
    
    public ItemStack toItemStack(boolean upgraded) {
        ItemStack stack = new ItemStack(id, amount);
        stack.setDurability((short) data);
        stack.getItemMeta().setDisplayName(upgraded ? upgName : name);       
        return stack;
    }
}
