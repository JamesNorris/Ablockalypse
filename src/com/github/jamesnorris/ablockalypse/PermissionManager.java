package com.github.jamesnorris.ablockalypse;

import java.util.HashMap;

import org.bukkit.permissions.Permission;

public class PermissionManager {
    public static final Permission BASE_COMMAND = new Permission("za.base");
    public static final Permission CREATE_GAMES = new Permission("za.create");
    public static final Permission PLACE_SIGNS = new Permission("za.sign");
    public static final Permission JOIN_GAMES = new Permission("za.join");
    public static HashMap<String, Permission> permissions = new HashMap<String, Permission>();
    static {
        permissions.put(BASE_COMMAND.getName(), BASE_COMMAND);
        permissions.put(CREATE_GAMES.getName(), CREATE_GAMES);
        permissions.put(PLACE_SIGNS.getName(), PLACE_SIGNS);
        permissions.put(JOIN_GAMES.getName(), JOIN_GAMES);
    }

    public static Permission getPermission(String permission) {
        for (String name : permissions.keySet()) {
            if (name.equalsIgnoreCase(permission)) {
                return permissions.get(name);
            }
        }
        return null;
    }
}
