package com.github.jamesnorris.enumerated;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.github.ikeirnez.command.CommandUtil;

public enum ManagementHelpTopic {
    MAPDATA {
        @Override public String getDescription() {
            return "Shows help on saving map information";
        }

        @Override public String getName() {
            return "mapdata";
        }

        @Override public boolean matches(CommandSender sender, String[] args, String alias) {
            return args.length == 2 && args[1].equalsIgnoreCase(getName()) && sender.hasPermission("za.create");
        }

        @Override public void showHelp(CommandSender sender, String[] args, String alias) {
            String aliasView = "/" + alias;
            StringBuilder sb = new StringBuilder();
            sb.append(red + "----- Ablockalypse MapData Help -----");
            //@formatter:off
            sb.append(gray + "Mapdata is an innovative new way to deliver maps from one server to another. " +
                    "\nWhen Ablockalypse was first created so that map packs would be released, but we ran into a problem. " +
                    "\nThe problem was that physical game objects (barriers, etc) and maps could not be moved to other games. " +
                    "\nMapdata is a solution to that, and loads physical game objects and structures to the new server/game." +
                    reset + "\n---------------------------------------------------------------------------------------");
            //@formatter:on
            sb.append(gold + aliasView + bold + " mapdata <game> save - " + reset + gray + "Saves a mapdata file for the game given");
            sb.append(gold + aliasView + bold + " mapdata <game> load - " + reset + gray + "Loads a mapdata file to a game map");
            sender.sendMessage(sb.toString());
        }
    },
    OBJECTS {
        @Override public String getDescription() {
            return "Shows a list of available game objects";
        }

        @Override public String getName() {
            return "objects";
        }

        @Override public boolean matches(CommandSender sender, String[] args, String alias) {
            return args.length == 2 && args[1].equalsIgnoreCase(getName()) && sender.hasPermission("za.create");
        }

        @Override public void showHelp(CommandSender sender, String[] args, String alias) {
            StringBuilder sb = new StringBuilder();
            sb.append(red + "----- Ablockalypse Objects Help -----");
            sb.append(ChatColor.GOLD + "Available Game Objects: " + ChatColor.YELLOW + CommandUtil.implode(OBJECT_TYPES, ChatColor.GOLD + ", " + ChatColor.YELLOW, ChatColor.GOLD + " and " + ChatColor.YELLOW));
            sender.sendMessage(sb.toString());
        }
    },
    SETUP {
        @Override public String getDescription() {
            return "Shows how to setup games";
        }

        @Override public String getName() {
            return "setup";
        }

        @Override public boolean matches(CommandSender sender, String[] args, String alias) {
            return args.length == 2 && args[1].equalsIgnoreCase(getName()) && sender.hasPermission("za.create");
        }

        @Override public void showHelp(CommandSender sender, String[] args, String alias) {
            String aliasView = "/" + alias;
            StringBuilder sb = new StringBuilder();
            sb.append(red + "----- Ablockalypse Setup Help -----");
            sb.append(gold + aliasView + bold + " create <game> - " + reset + gray + "Creates a new game");
            sb.append(gold + aliasView + bold + " <object> <game> - " + reset + gray + "Creates an object for this game");
            sb.append(gold + aliasView + bold + " remove <game> - " + reset + gray + "Removes an entire game");
            sb.append(gold + aliasView + bold + " remove - " + reset + gray + "Removes a clicked object");
            sender.sendMessage(sb.toString());
        }
    },
    SIGNS {
        @Override public String getDescription() {
            return "Shows sign commandline requirements";
        }

        @Override public String getName() {
            return "signs";
        }

        @Override public boolean matches(CommandSender sender, String[] args, String alias) {
            return args.length == 2 && args[1].equalsIgnoreCase(getName()) && sender.hasPermission("za.create");
        }

        @Override public void showHelp(CommandSender sender, String[] args, String alias) {
            StringBuilder sb = new StringBuilder();
            sb.append(red + "----- Ablockalypse Sign Lines -----");
            sb.append(red + Local.BASE_STRING.getSetting() + " " + gray + "This must be at the top of any ZA command sign");
            sb.append(red + Local.BASE_JOIN_STRING.getSetting() + " " + gray + "When clicked adds a player to a ZA game");
            sb.append(red + Local.BASE_PASSAGE_STRING.getSetting() + " " + gray + "When clicked opens a ZA passage");
            sb.append(red + Local.BASE_PERK_STRING.getSetting() + " " + gray + "When clicked gives a perk");
            sb.append(red + Local.BASE_ENCHANTMENT_STRING.getSetting() + " " + gray + "When clicked enchants an item in your hand");
            sb.append(red + Local.BASE_WEAPON_STRING.getSetting() + " " + gray + "When clicked gives a weapon");
            sender.sendMessage(sb.toString());
        }
    },
    POWER {

        @Override public String getDescription() {
            return "Shows commands that can enable power for objects.";
        }

        @Override public String getName() {
            return "power";
        }

        @Override public boolean matches(CommandSender sender, String[] args, String alias) {
            return args.length == 2 && args[1].equalsIgnoreCase(getName()) && sender.hasPermission("za.create");
        }

        @Override public void showHelp(CommandSender sender, String[] args, String alias) {
            String aliasView = "/" + alias;
            StringBuilder sb = new StringBuilder();
            sb.append(red + "----- Ablockalypse Sign Lines -----");//TODO add this to the base command
            sb.append(gold + aliasView + bold + " power <game> <true/false> - " + reset + gray + "Allows you to enable/disable power for an object");
            sender.sendMessage(sb.toString());
        }
        
    };
    ChatColor bold = ChatColor.BOLD;
    ChatColor gold = ChatColor.GOLD;
    ChatColor gray = ChatColor.GRAY;
    ChatColor red = ChatColor.RED;
    ChatColor reset = ChatColor.RESET;
    public static final String[] OBJECT_TYPES = new String[] {"Barrier", "Mainframe", "Mob Spawner", "Mystery Chest", "Passage", "Power Switch"};

    public abstract String getDescription();

    public abstract String getName();

    public abstract boolean matches(CommandSender sender, String[] args, String alias);

    public abstract void showHelp(CommandSender sender, String[] args, String alias);
}
