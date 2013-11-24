package com.github.jamesnorris.ablockalypse.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.entity.ZAPlayer;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;
import com.github.jamesnorris.ablockalypse.event.GameCreateEvent;
import com.github.jamesnorris.ablockalypse.event.PlayerLeaveGameEvent;
import com.github.jamesnorris.ablockalypse.event.bukkit.PlayerInteract;
import com.github.jamesnorris.ablockalypse.manager.PermissionManager;
import com.github.jamesnorris.ablockalypse.queue.inherent.QueuedBarrierCreation;
import com.github.jamesnorris.ablockalypse.queue.inherent.QueuedGameObjectRemoval;
import com.github.jamesnorris.ablockalypse.queue.inherent.QueuedMobSpawnerCreation;
import com.github.jamesnorris.ablockalypse.queue.inherent.QueuedMysteryBoxCreation;
import com.github.jamesnorris.ablockalypse.queue.inherent.QueuedPassageCreation;
import com.github.jamesnorris.ablockalypse.queue.inherent.QueuedTeleporterCreation;

public class BaseCommand extends CommandUtil implements CommandExecutor {
    private DataContainer data = Ablockalypse.getData();

    @Override public boolean onCommand(CommandSender sender, Command cmd, String inf, String[] args) {
        if (cmd.getName().equalsIgnoreCase("za")) {
            if (!sender.hasPermission(PermissionManager.BASE_COMMAND)) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use Ablockalypse commands!");
                return true;
            }
            boolean args0Digit = args != null && args.length == 1 && Character.isDigit(args[0].charAt(0));
            boolean args1Digit = args != null && args.length == 2 && Character.isDigit(args[1].charAt(0));
            boolean args2Digit = args != null && args.length == 3 && Character.isDigit(args[2].charAt(0));
            boolean helpCommand = args != null && (args.length == 1 || args.length == 2 && args1Digit) && args[0].equalsIgnoreCase("help");
            if (args == null || args.length == 0 || helpCommand || args0Digit) {
                BASE_MENU.showPage(sender, args0Digit ? Integer.parseInt(args[0]) : helpCommand ? args1Digit ? Integer.parseInt(args[1]) : 1 : 1);
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                if ((args.length == 2 || args.length == 3 && args2Digit) && args[1].equalsIgnoreCase("games")) {
                    updateGameListMenu();
                    GAME_LIST.showPage(sender, args2Digit ? Integer.parseInt(args[2]) : 1);
                } else if ((args.length == 2 || args.length == 3 && args2Digit) && args[1].equalsIgnoreCase("signs")) {
                    SIGN_LIST.showPage(sender, args2Digit ? Integer.parseInt(args[2]) : 1);
                } else if ((args.length == 2 || args.length == 3 && args2Digit) && args[1].equalsIgnoreCase("objects")) {
                    OBJECT_LIST.showPage(sender, args2Digit ? Integer.parseInt(args[2]) : 1);
                } else {
                    LIST_MENU.showPage(sender, args1Digit ? Integer.parseInt(args[1]) : 1);
                }
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
                if (!sender.hasPermission(PermissionManager.JOIN_GAMES)) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to join games!");
                    return true;
                }
                String gameName = args[1];
                if (!data.gameExists(gameName)) {
                    sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za game create <game> to create one.");
                    return true;
                }
                Player player = (Player) sender;
                if (data.isZAPlayer(player)) {
                    sender.sendMessage(ChatColor.RED + "You are already in a game!");
                    return true;
                }
                ZAPlayer zap = data.getZAPlayer(player, gameName, true);
                zap.loadPlayerToGame(gameName, true);
                return true;
            } else if (args[0].equalsIgnoreCase("quit")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(requiresPlayer);
                    return true;
                }
                Player player = (Player) sender;
                if (!data.isZAPlayer(player)) {
                    sender.sendMessage(ChatColor.RED + "You must be in a game to do that!");
                    return true;
                }
                ZAPlayer zap = data.getZAPlayer(player);
                Game zag = zap.getGame();
                PlayerLeaveGameEvent GPLE = new PlayerLeaveGameEvent(zap, zag);
                Bukkit.getPluginManager().callEvent(GPLE);
                if (!GPLE.isCancelled()) {
                    sender.sendMessage(ChatColor.AQUA + "Successfully quit the Ablockalypse game: " + ChatColor.GOLD + zag.getName());
                    zag.removePlayer(player);
                    return true;
                }
                return true;
            } else if (args[0].equalsIgnoreCase("game")) {
                if (!sender.hasPermission(PermissionManager.CREATE_GAMES)) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                if (args.length == 3) {
                    String gameName = args[2];
                    if (args[1].equalsIgnoreCase("create")) {
                        if (data.gameExists(gameName)) {
                            sender.sendMessage(ChatColor.RED + "That game already exists!");
                            return true;
                        }
                        if (!sender.hasPermission(PermissionManager.CREATE_GAMES)) {
                            sender.sendMessage(noMaintainPerms);
                            return true;
                        }
                        Game zag = new Game(gameName);
                        GameCreateEvent gce = new GameCreateEvent(zag, sender, null);
                        Bukkit.getServer().getPluginManager().callEvent(gce);
                        if (!gce.isCancelled()) {
                            sender.sendMessage(ChatColor.GRAY + "You have created a new ZA game called " + gameName);
                        } else {
                            zag.remove(true);
                        }
                        return true;
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        if (!data.gameExists(gameName)) {
                            sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za game create <game> to create one.");
                            return true;
                        }
                        Game zag = data.getGame(gameName, true);
                        zag.remove(true);
                        sender.sendMessage(ChatColor.GRAY + "You have removed the game " + gameName);
                        return true;
                    }
                } else if (args.length == 4 && args[1].equalsIgnoreCase("mapdata")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "You must be a player to save/load mapdata! (A location on the map is needed)");
                        return true;
                    }
                    String gameName = args[2];
                    Player player = (Player) sender;
                    if (args[3].equalsIgnoreCase("load")) {
                        PlayerInteract.mapDataLoadPlayers.put(player.getName(), gameName);
                        sender.sendMessage(ChatColor.GRAY + "Please click a block to load the mapdata at. This must be the bottom-left corner where you want the data loaded.");
                        return true;
                    } else if (args[3].equalsIgnoreCase("save")) {
                        PlayerInteract.mapDataSavePlayers.put(player.getName(), gameName);
                        sender.sendMessage(ChatColor.GRAY + "Please click a corner of the map to begin.");
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "You must provide the argument 'load/save'!");
                        return true;
                    }
                }
                GAME_MENU.showPage(sender, args1Digit ? Integer.parseInt(args[1]) : 1);
                return true;
            } else if (args[0].equalsIgnoreCase("object")) {
                if (!sender.hasPermission(PermissionManager.CREATE_GAMES)) {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a player to use selection!");
                    return true;
                }
                Player player = (Player) sender;
                if (args.length == 4 && args[1].equalsIgnoreCase("create")) {
                    String gameName = args[3];
                    if (!data.gameExists(gameName)) {
                        sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za game create <game> to create one.");
                        return true;
                    }
                    if (args[2].equalsIgnoreCase("barrier")) {
                        PlayerInteract.queue.add(new QueuedBarrierCreation(player.getName(), gameName));
                    } else if (args[2].equalsIgnoreCase("mainframe")) {
                        PlayerInteract.queue.add(new QueuedTeleporterCreation(player.getName(), gameName, true));
                    } else if (args[2].equalsIgnoreCase("teleporter")) {
                        PlayerInteract.queue.add(new QueuedTeleporterCreation(player.getName(), gameName, false));
                    } else if (args[2].equalsIgnoreCase("mobspawner")) {
                        PlayerInteract.queue.add(new QueuedMobSpawnerCreation(player.getName(), gameName));
                    } else if (args[2].equalsIgnoreCase("passage")) {
                        PlayerInteract.queue.add(new QueuedPassageCreation(player.getName(), gameName));
                    } else if (args[2].equalsIgnoreCase("mysterybox") || args[2].equalsIgnoreCase("mysterychest")) {
                        // accepts mysterychest, just in case they don't know about the switch in v1.2.9.2
                        PlayerInteract.queue.add(new QueuedMysteryBoxCreation(player.getName(), gameName));
                    } else {
                        sender.sendMessage(ChatColor.RED + "That is not a valid object! Please try \'/za list objects\'.");
                    }
                    return true;
                } else if (args.length == 2 && args[1].equalsIgnoreCase("remove")) {
                    PlayerInteract.queue.add(new QueuedGameObjectRemoval(player.getName()));
                    return true;
                }
                OBJECT_MENU.showPage(sender, args1Digit ? Integer.parseInt(args[1]) : 1);
                return true;
            }
            BASE_MENU.showPage(sender, args0Digit ? Integer.parseInt(args[0]) : helpCommand ? args1Digit ? Integer.parseInt(args[1]) : 1 : 1);
            return true;
        }
        return true;
    }
}
