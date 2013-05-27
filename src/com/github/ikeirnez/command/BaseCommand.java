package com.github.ikeirnez.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.event.GameCreateEvent;
import com.github.jamesnorris.event.GamePlayerLeaveEvent;
import com.github.jamesnorris.event.bukkit.PlayerInteract;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.Mainframe;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.util.MiscUtil;

public class BaseCommand extends CommandUtil implements CommandExecutor {
    private DataContainer data = Ablockalypse.getData();

    @Override public boolean onCommand(CommandSender sender, Command cmd, String inf, String[] args) {
        if (cmd.getName().equalsIgnoreCase("za")) {
            if (!sender.hasPermission("za.base")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use Ablockalypse commands!");
                return true;
            }
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                showHelp(sender, args, cmd.getLabel());
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                list(sender);
                return true;
            } else if (args[0].equalsIgnoreCase("join")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(requiresPlayer);
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
                if (!sender.hasPermission("za.join")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to join games!");
                    return true;
                }
                String gameName = args[1];
                if (!data.gameExists(gameName)) {
                    sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                    return true;
                }
                Player player = (Player) sender;
                ZAPlayer zap = data.getZAPlayer(player, gameName, true);
                zap.loadPlayerToGame(gameName);
                return true;
            } else if (args[0].equalsIgnoreCase("quit")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(requiresPlayer);
                    return true;
                }
                Player player = (Player) sender;
                if (!data.players.containsKey(player)) {
                    sender.sendMessage(ChatColor.RED + "You must be in a game to do that!");
                    return true;
                }
                ZAPlayer zap = data.players.get(player);
                Game zag = zap.getGame();
                GamePlayerLeaveEvent GPLE = new GamePlayerLeaveEvent(zap, zag);
                Bukkit.getPluginManager().callEvent(GPLE);
                if (!GPLE.isCancelled()) {
                    sender.sendMessage(ChatColor.AQUA + "Successfully quit the Ablockalypse game: " + ChatColor.GOLD + zag.getName());
                    zag.removePlayer(player);
                    return true;
                }
                return true;
            } else if (args[0].equalsIgnoreCase("create")) {
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
                String gameName = args[1];
                if (data.gameExists(gameName)) {
                    sender.sendMessage(ChatColor.RED + "That game already exists!");
                    return true;
                }
                if (!sender.hasPermission("za.create")) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                Game zag = new Game(gameName);
                GameCreateEvent gce = new GameCreateEvent(zag, sender, null);
                Bukkit.getServer().getPluginManager().callEvent(gce);
                if (!gce.isCancelled()) {
                    sender.sendMessage(ChatColor.GRAY + "You have created a new ZA game called " + gameName);
                } else {
                    zag.remove();
                }
                return true;
            } else if (args[0].equalsIgnoreCase("barrier")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(requiresPlayer);
                    return true;
                }
                if (!sender.hasPermission("za.create")) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
                String gameName = args[1];
                if (!data.gameExists(gameName)) {
                    sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                    return true;
                }
                Player player = (Player) sender;
                PlayerInteract.barrierPlayers.put(player.getName(), data.getGame(gameName, true));
                player.sendMessage(ChatColor.GRAY + "Click the center of a 3x3 section of fence to make a barrier.");
                return true;
            } else if (args[0].equalsIgnoreCase("mainframe")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(requiresPlayer);
                    return true;
                }
                Player p = (Player) sender;
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
                String gameName = args[1];
                if (!data.gameExists(gameName)) {
                    sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                    return true;
                }
                if (!sender.hasPermission("za.create")) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                Game zag = data.getGame(gameName, false);
                Location loc = MiscUtil.getHighestBlockUnder(p.getLocation()).getLocation();
                zag.setMainframe(new Mainframe(zag, loc));
                sender.sendMessage(ChatColor.GRAY + "You have set the mainframe for " + gameName);
                return true;
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (!sender.hasPermission("za.create")) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                if (args.length == 2) {
                    String gameName = args[1];
                    if (!data.gameExists(gameName)) {
                        sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                        return true;
                    }
                    // TODO "are you sure?" command
                    Game zag = data.getGame(gameName, true);
                    zag.remove();
                    sender.sendMessage(ChatColor.GRAY + "You have removed the game " + gameName);
                    return true;
                } else if (args.length == 1) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "You must be a player to remove a game object!");
                        return true;
                    }
                    Player p = (Player) sender;
                    sender.sendMessage(ChatColor.GRAY + "Click a ZA object to remove it.");
                    PlayerInteract.removers.add(p.getName());
                }
            } else if (args[0].equalsIgnoreCase("spawner")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(requiresPlayer);
                    return true;
                }
                if (!sender.hasPermission("za.create")) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
                String gameName = args[1];
                if (!data.gameExists(gameName)) {
                    sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                    return true;
                }
                Player player = (Player) sender;
                PlayerInteract.spawnerPlayers.put(player.getName(), data.getGame(gameName, true));
                sender.sendMessage(ChatColor.GRAY + "Click a block to create a spawner.");
                return true;
            } else if (args[0].equalsIgnoreCase("passage")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(requiresPlayer);
                    return true;
                }
                if (!sender.hasPermission("za.create")) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
                String gameName = args[1];
                if (!data.gameExists(gameName)) {
                    sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                    return true;
                }
                Player player = (Player) sender;
                PlayerInteract.passagePlayers.put(player.getName(), data.getGame(gameName, true));
                sender.sendMessage(ChatColor.GRAY + "Click a block to select point 1.");
                return true;
            } else if (args[0].equalsIgnoreCase("chest")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(requiresPlayer);
                    return true;
                }
                if (!sender.hasPermission("za.create")) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
                String gameName = args[1];
                if (!data.gameExists(gameName)) {
                    sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                    return true;
                }
                Player player = (Player) sender;
                PlayerInteract.chestPlayers.put(player.getName(), data.getGame(gameName, true));
                sender.sendMessage(ChatColor.GRAY + "Click a chest to turn it into a mystery chest.");
                return true;
            } else if (args[0].equalsIgnoreCase("mapdata")) {
                if (!sender.hasPermission("za.create")) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a player to save/load mapdata! (A location on the map is needed)");
                    return true;
                }
                if (args.length != 3) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! /za mapdata <game> <save/load> - is correct!");
                    return true;
                }
                Player player = (Player) sender;
                String gameName = args[1];
                if (args[2].equalsIgnoreCase("load")) {
                    PlayerInteract.mapDataLoadPlayers.put(player.getName(), gameName);
                    sender.sendMessage(ChatColor.GRAY + "Please click a block to load the mapdata at. This must be the bottom-left corner where you want the data loaded.");
                    return true;
                } else if (args[2].equalsIgnoreCase("save")) {
                    PlayerInteract.mapDataSavePlayers.put(player.getName(), gameName);
                    sender.sendMessage(ChatColor.GRAY + "Please click a corner of the map to begin.");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "You must provide the argument 'load/save'!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("power")) {
                if (!sender.hasPermission("za.create")) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a player to enable/disable power! (A location on the map is needed)");
                    return true;
                }
                if (args.length != 3) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! /za power <game> <true/false> - is correct!");
                    return true;
                }
                Player player = (Player) sender;
                String gameName = args[1];
                if (!args[2].equalsIgnoreCase("true") && !args[2].equalsIgnoreCase("false")) {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! /za power <game> <true/false> - is correct!");
                    return true;
                }
                boolean power = Boolean.parseBoolean(args[2]);
                PlayerInteract.powerClickers.put(player.getName(), power);
                PlayerInteract.powerClickerGames.put(player.getName(), gameName);
                player.sendMessage(ChatColor.GRAY + "Please click on a powerable object to " + ((power) ? "enable" : "disable") + " power");
                return true;
            }
            return true;
        }
        return true;
    }
}
