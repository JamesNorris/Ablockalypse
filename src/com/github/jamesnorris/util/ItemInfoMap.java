package com.github.jamesnorris.util;

public class ItemInfoMap {
    public int id, cost, damage, dropamount, level;
    public String name, rename;

    public ItemInfoMap(int id, String name, String rename, int cost, int dropamount, int level) {
        this.id = id;
        this.name = name;
        this.rename = rename;
        this.cost = cost;
        this.dropamount = dropamount;
        this.level = level;
    }
}
