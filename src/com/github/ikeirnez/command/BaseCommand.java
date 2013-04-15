package com.github.ikeirnez.command;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.ikeirnez.util.CommandUtil;
import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.External;
import com.github.jamesnorris.event.GameCreateEvent;
import com.github.jamesnorris.event.GamePlayerLeaveEvent;
import com.github.jamesnorris.event.bukkit.PlayerInteract;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.Mainframe;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.storage.MapDataStorage;

public class BaseCommand extends CommandUtil implements CommandExecutor {
    DataManipulator dm;

    @Override public boolean onCommand(CommandSender sender, Command cmd, String inf, String[] args) {
        if (cmd.getName().equalsIgnoreCase("za")) {
            if (dm == null)
                dm = Ablockalypse.getData();
            String alias = cmd.getLabel();
            if (args.length == 0 || args[0].equalsIgnoreCase("help") || (args.length == 2 && args[1].equalsIgnoreCase("sign"))) {
                showHelp(sender, args, alias);
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                list(sender);
                return true;
            } else if (args[0].equalsIgnoreCase("join")) {
                if (args.length == 2) {
                    if (sender.hasPermission("za.join")) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            String gameName = args[1];
                            if (data.gameExists(gameName)) {
                                ZAPlayer zap = data.getZAPlayer(player, gameName, true);
                                zap.loadPlayerToGame(gameName);
                                return true;
                            } else {
                                sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                                return true;
                            }
                        } else {
                            sender.sendMessage(notPlayer);
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to join games!");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("quit")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (data.players.containsKey(player)) {
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
                    } else {
                        sender.sendMessage(ChatColor.RED + "You must be in a game to do that!");
                        return true;
                    }
                } else {
                    sender.sendMessage(notPlayer);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("create")) {
                if (args.length == 2) {
                    String gameName = args[1];
                    if (!data.gameExists(gameName)) {
                        if (!sender.hasPermission("za.create")) {
                            sender.sendMessage(noMaintainPerms);
                            return true;
                        } else {
                            Game zag = new Game(gameName);
                            GameCreateEvent gce = new GameCreateEvent(zag, sender, null);
                            Bukkit.getServer().getPluginManager().callEvent(gce);
                            if (!gce.isCancelled())
                                sender.sendMessage(ChatColor.GRAY + "You have created a new ZA game called " + gameName);
                            else
                                zag.remove();
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "That game already exists!");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("barrier")) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("za.create")) {
                        if (args.length == 2) {
                            String gameName = args[1];
                            if (!data.gameExists(gameName)) {
                                sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                                return true;
                            }
                            Player player = (Player) sender;
                            PlayerInteract.barrierPlayers.put(player.getName(), data.getGame(gameName, true));
                            player.sendMessage(ChatColor.GRAY + "Click the center of a 3x3 section of fence to make a barrier.");
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                            return true;
                        }
                    } else {
                        sender.sendMessage(noMaintainPerms);
                        return true;
                    }
                } else {
                    sender.sendMessage(notPlayer);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("mainframe")) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (args.length == 2) {
                        String gameName = args[1];
                        if (!data.gameExists(gameName)) {
                            sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                            return true;
                        }
                        if (sender.hasPermission("za.create")) {
                            Game zag = data.getGame(gameName, false);
                            zag.setMainframe(new Mainframe(zag, p.getLocation()));
                            sender.sendMessage(ChatColor.GRAY + "You have set the mainframe for " + gameName);
                            return true;
                        } else {
                            sender.sendMessage(noMaintainPerms);
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                        return true;
                    }
                } else {
                    sender.sendMessage(notPlayer);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (sender.hasPermission("za.create")) {
                    if (args.length == 2) {
                        String gameName = args[1];
                        if (!data.gameExists(gameName)) {
                            sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                            return true;
                        }
                        Game zag = data.getGame(gameName, false);
                        zag.remove();
                        sender.sendMessage(ChatColor.GRAY + "You have removed the game " + gameName);
                        return true;
                    } else if (args.length == 1) {
                        Player p = (Player) sender;
                        sender.sendMessage(ChatColor.GRAY + "Click a ZA object to remove it.");
                        PlayerInteract.removers.add(p.getName());
                    }
                } else {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("spawner")) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("za.create")) {
                        if (args.length == 2) {
                            String gameName = args[1];
                            if (!data.gameExists(gameName)) {
                                sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                                return true;
                            }
                            Player player = (Player) sender;
                            PlayerInteract.spawnerPlayers.put(player.getName(), data.getGame(gameName, true));
                            sender.sendMessage(ChatColor.GRAY + "Click a block to create a spawner.");
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                            return true;
                        }
                    } else {
                        sender.sendMessage(noMaintainPerms);
                        return true;
                    }
                } else {
                    sender.sendMessage(notPlayer);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("area")) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("za.create")) {
                        if (args.length == 2) {
                            String gameName = args[1];
                            if (!data.gameExists(gameName)) {
                                sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                                return true;
                            }
                            Player player = (Player) sender;
                            PlayerInteract.areaPlayers.put(player.getName(), data.getGame(gameName, true));
                            sender.sendMessage(ChatColor.GRAY + "Click a block to select point 1.");
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                            return true;
                        }
                    } else {
                        sender.sendMessage(noMaintainPerms);
                        return true;
                    }
                } else {
                    sender.sendMessage(notPlayer);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("chest")) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("za.create")) {
                        if (args.length == 2) {
                            String gameName = args[1];
                            if (!data.gameExists(gameName)) {
                                sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                                return true;
                            }
                            Player player = (Player) sender;
                            PlayerInteract.chestPlayers.put(player.getName(), data.getGame(gameName, true));
                            sender.sendMessage(ChatColor.GRAY + "Click a chest to turn it into a mystery chest.");
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                            return true;
                        }
                    } else {
                        sender.sendMessage(noMaintainPerms);
                        return true;
                    }
                } else {
                    sender.sendMessage(notPlayer);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("settings")) {
                if (sender.hasPermission("za.create")) {
                    if (args.length == 3) {
                        String gameName = args[1];
                        if (!data.gameExists(gameName)) {
                            sender.sendMessage(ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                            return true;
                        }
                        boolean setting = Boolean.parseBoolean(args[3]);
                        if (args[2].equalsIgnoreCase("FF")) {// Friendly fire
                            data.getGame(gameName, true).setFriendlyFire(setting);
                            sender.sendMessage(settingChanged);
                        } else {
                            sender.sendMessage(invalidSetting);
                        }
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "Incorrect syntax! You must provide the name of a game, a setting, and a boolean!");
                        return true;
                    }
                } else {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("mapdata")) {
                if (sender.hasPermission("za.create")) {
                    if (args.length == 3) {
                        String gameName = args[1];
                        if (args[2].equalsIgnoreCase("load")) {
                            if (sender instanceof Player) {
                                Player player = (Player) sender;
                                String oldFile = gameName + "_mapdata.bin";
                                File saveFile = new File(Ablockalypse.instance.getDataFolder(), File.separatorChar + "map_data" + File.separatorChar + oldFile);
                                try {
                                    MapDataStorage mds = (MapDataStorage) External.load(saveFile.getPath());
                                    if (mds.possibleKey(player.getLocation())) {
                                        mds.loadToGame(player.getLocation());
                                        sender.sendMessage(ChatColor.GRAY + "Map data loaded! \nPlease note that this is a single event, and is not constantly updated/loaded.");
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "You are not in the correct location to load mapdata! \nPlease make sure you are in the same spot where the mapdata was saved.");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "You must be a player to load mapdata! (A location on the map is needed)");
                            }
                        } else if (args[2].equalsIgnoreCase("save")) {
                            if (sender instanceof Player) {
                                Player player = (Player) sender;
                                String newFile = gameName + "_mapdata.bin";
                                try {
                                    File saveFile = new File(Ablockalypse.instance.getDataFolder(), File.separatorChar + External.mapdatafolderlocation + newFile);
                                    if (!saveFile.exists())
                                        saveFile.createNewFile();
                                    External.save(new MapDataStorage(player.getLocation(), gameName), External.filelocation + External.mapdatafolderlocation + newFile);
                                    sender.sendMessage(ChatColor.GRAY + "Map data snapshot saved! \nPlease note that this data is only a snapshot, and never updates automatically.");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "You must be a player to save mapdata! (A location on the map is needed)");
                            }
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "You must provide the final argument 'load/save'!");
                            return true;
                        }
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "Incorrect syntax! /za mapdata <game> load - is correct!");
                        return true;
                    }
                } else {
                    sender.sendMessage(noMaintainPerms);
                    return true;
                }
            }
            return true;
        }
        return true;
    }
}
