package com.github.iKeirNez.Command;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.MapDataStorage;
import com.github.JamesNorris.Event.GameCreateEvent;
import com.github.JamesNorris.Event.GamePlayerLeaveEvent;
import com.github.JamesNorris.Event.Bukkit.PlayerInteract;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Util.MiscUtil;
import com.github.iKeirNez.Util.CommandUtil;

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
                                ZAPlayer zap = data.findZAPlayer(player, gameName);
                                zap.loadPlayerToGame(gameName);
                                return true;
                            } else {
                                replyToSender(sender, ChatColor.RED + "This game does not exist! Use /za create <game> to create one.");
                                return true;
                            }
                        } else {
                            replyToSender(sender, notPlayer);
                            return true;
                        }
                    } else {
                        replyToSender(sender, ChatColor.RED + "You do not have permission to join games!");
                        return true;
                    }
                } else {
                    replyToSender(sender, ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("info")) {
                replyToSender(sender, ChatColor.GOLD + "Zombie Ablockalypse version: " + ChatColor.RED + data.version);
                replyToSender(sender, ChatColor.GOLD + "Developed by: " + ChatColor.RED + implode(data.authors.toArray(), ", ", " and "));
                return true;
            } else if (args[0].equalsIgnoreCase("quit")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (data.players.containsKey(player)) {
                        ZAPlayerBase zap = data.players.get(player);
                        ZAGame zag = zap.getGame();
                        GamePlayerLeaveEvent GPLE = new GamePlayerLeaveEvent(zap, zag);
                        Bukkit.getPluginManager().callEvent(GPLE);
                        if (!GPLE.isCancelled()) {
                            replyToSender(sender, ChatColor.AQUA + "Successfully quit the Ablockalypse game: " + ChatColor.GOLD + zag.getName());
                            zag.removePlayer(player);
                            return true;
                        }
                        return true;
                    } else {
                        replyToSender(sender, ChatColor.RED + "You must be in a game to do that!");
                        return true;
                    }
                } else {
                    replyToSender(sender, notPlayer);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("create")) {
                if (args.length == 2) {
                    String gameName = args[1];
                    if (!data.gameExists(gameName)) {
                        if (!sender.hasPermission("za.create")) {
                            replyToSender(sender, noMaintainPerms);
                            return true;
                        } else {
                            ZAGame zag = new ZAGameBase(gameName);
                            GameCreateEvent gce = new GameCreateEvent(zag, sender, null);
                            Bukkit.getServer().getPluginManager().callEvent(gce);
                            if (!gce.isCancelled())
                                replyToSender(sender, ChatColor.GRAY + "You have created a new ZA game called " + gameName);
                            else
                                zag.remove();
                            return true;
                        }
                    } else {
                        replyToSender(sender, ChatColor.RED + "That game already exists!");
                        return true;
                    }
                } else {
                    replyToSender(sender, ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("barrier")) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("za.create")) {
                        if (args.length == 2) {
                            String gameName = args[1];
                            Player player = (Player) sender;
                            PlayerInteract.barrierPlayers.put(player.getName(), (ZAGameBase) data.findGame(gameName));
                            player.sendMessage(ChatColor.GRAY + "Click the center of a 3x3 section of fence to make a barrier.");
                            return true;
                        } else {
                            replyToSender(sender, ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                            return true;
                        }
                    } else {
                        replyToSender(sender, noMaintainPerms);
                        return true;
                    }
                } else {
                    replyToSender(sender, notPlayer);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("mainframe")) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (args.length == 2) {
                        String gameName = args[1];
                        if (data.gameExists(gameName)) {
                            if (sender.hasPermission("za.create")) {
                                ZAGame zag = data.findGame(gameName);
                                zag.setMainframe(p.getLocation());
                                replyToSender(sender, ChatColor.GRAY + "You have set the mainframe for " + gameName);
                                return true;
                            } else {
                                replyToSender(sender, noMaintainPerms);
                                return true;
                            }
                        } else {
                            replyToSender(sender, ChatColor.RED + "That game does not exist!");
                            return true;
                        }
                    } else {
                        replyToSender(sender, ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                        return true;
                    }
                } else {
                    replyToSender(sender, notPlayer);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (sender.hasPermission("za.create")) {
                    if (args.length == 2) {
                        String gameName = args[1];
                        if (data.gameExists(gameName)) {
                            ZAGame zag = data.findGame(gameName);
                            zag.remove();
                            replyToSender(sender, ChatColor.GRAY + "You have removed the game " + gameName);
                            return true;
                        } else {
                            replyToSender(sender, ChatColor.RED + "That game does not exist!");
                            return true;
                        }
                    } else if (args.length == 1) {
                        Player p = (Player) sender;
                        replyToSender(sender, ChatColor.GRAY + "Click a ZA object to remove it.");
                        PlayerInteract.removers.add(p.getName());
                    }
                } else {
                    replyToSender(sender, noMaintainPerms);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("spawner")) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("za.create")) {
                        if (args.length == 2) {
                            String gameName = args[1];
                            Player player = (Player) sender;
                            PlayerInteract.spawnerPlayers.put(player.getName(), (ZAGameBase) data.findGame(gameName));
                            replyToSender(sender, ChatColor.GRAY + "Click a block to create a spawner.");
                            return true;
                        } else {
                            replyToSender(sender, ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                            return true;
                        }
                    } else {
                        replyToSender(sender, noMaintainPerms);
                        return true;
                    }
                } else {
                    replyToSender(sender, notPlayer);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("area")) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("za.create")) {
                        if (args.length == 2) {
                            String gameName = args[1];
                            Player player = (Player) sender;
                            PlayerInteract.areaPlayers.put(player.getName(), (ZAGameBase) data.findGame(gameName));
                            replyToSender(sender, ChatColor.GRAY + "Click a block to select point 1.");
                            return true;
                        } else {
                            replyToSender(sender, ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                            return true;
                        }
                    } else {
                        replyToSender(sender, noMaintainPerms);
                        return true;
                    }
                } else {
                    replyToSender(sender, notPlayer);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("chest")) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("za.create")) {
                        if (args.length == 2) {
                            String gameName = args[1];
                            Player player = (Player) sender;
                            PlayerInteract.chestPlayers.put(player.getName(), (ZAGameBase) data.findGame(gameName));
                            replyToSender(sender, ChatColor.GRAY + "Click a chest to turn it into a mystery chest.");
                            return true;
                        } else {
                            replyToSender(sender, ChatColor.RED + "Incorrect syntax! You must provide the name of a game!");
                            return true;
                        }
                    } else {
                        replyToSender(sender, noMaintainPerms);
                        return true;
                    }
                } else {
                    replyToSender(sender, notPlayer);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("settings")) {
                if (sender.hasPermission("za.create")) {
                    if (args.length == 3) {
                        String gameName = args[1];
                        boolean setting = Boolean.parseBoolean(args[3]);
                        if (args[2].equalsIgnoreCase("FF")) {// Friendly fire
                            data.findGame(gameName).setFriendlyFire(setting);
                            replyToSender(sender, settingChanged);
                        } else {
                            replyToSender(sender, invalidSetting);
                        }
                        return true;
                    } else {
                        replyToSender(sender, ChatColor.RED + "Incorrect syntax! You must provide the name of a game, a setting, and a boolean!");
                        return true;
                    }
                } else {
                    replyToSender(sender, noMaintainPerms);
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
                                        mds.loadToGame(player.getLocation(), data.findGame(gameName));
                                        replyToSender(sender, ChatColor.GRAY + "Map data loaded! \nPlease note that this is a single event, and is not constantly updated/loaded.");
                                    } else {
                                        replyToSender(sender, ChatColor.RED + "That is not a valid key to load mapdata from! \nPlease move to the location relative to the objects at which the mapdata was saved!");
                                        return true;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                replyToSender(sender, ChatColor.RED + "You must be a player to load mapdata! (A location on the map is needed)");
                            }
                        } else if (args[2].equalsIgnoreCase("save")) {
                            if (sender instanceof Player) {
                                Player player = (Player) sender;
                                data.mapDataSigns.put(player.getLocation(), gameName);
                                replyToSender(sender, ChatColor.GRAY + "Map data queued, all data will be saved to a file on stop. \nPlease note that this data is only a snapshot, and never updates automatically.");
                            } else {
                                replyToSender(sender, ChatColor.RED + "You must be a player to save mapdata! (A location on the map is needed)");
                            }
                            return true;
                        } else {
                            replyToSender(sender, ChatColor.RED + "You must provide the final argument 'load/save'!");
                            return true;
                        }
                        return true;
                    } else {
                        replyToSender(sender, ChatColor.RED + "Incorrect syntax! /za mapdata <game> load - is correct!");
                        return true;
                    }
                } else {
                    replyToSender(sender, noMaintainPerms);
                    return true;
                }
            }
            return true;
        }
        return true;
    }

    private void replyToSender(CommandSender sender, String message) {
        MiscUtil.replyToSender(sender, message);
    }
}
