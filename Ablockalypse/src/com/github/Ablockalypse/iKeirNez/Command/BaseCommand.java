package com.github.Ablockalypse.iKeirNez.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.Ablockalypse.JamesNorris.Data.Data;
import com.github.Ablockalypse.JamesNorris.Data.LocalizationData;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAGameBase;
import com.github.Ablockalypse.JamesNorris.Implementation.ZAPlayerBase;
import com.github.Ablockalypse.JamesNorris.Interface.ZAPlayer;
import com.github.Ablockalypse.JamesNorris.Util.External;
import com.github.Ablockalypse.iKeirNez.Util.CommandUtil;
import com.github.Ablockalypse.iKeirNez.Util.CommonMsg;
import com.github.Ablockalypse.iKeirNez.Util.StringFunctions;

// TODO can you use local.yml and LocalizationData.java to change all strings?
public class BaseCommand extends CommandUtil implements CommandExecutor {
	private LocalizationData ld;

	@Override public boolean onCommand(CommandSender sender, Command cmd, String inf, String[] args) {
		if (cmd.getName().equalsIgnoreCase("za")) {
			if (ld == null)
				ld = External.ym.getLocalizationData();
			String alias = cmd.getLabel();
			if (args.length == 0 || args[0].equalsIgnoreCase("help") || (args.length == 2 && args[1].equalsIgnoreCase("sign"))) {
				showHelp(sender, args, alias);
			} else if (args[0].equalsIgnoreCase("list")) {
				list(sender);
			} else if (args[0].equalsIgnoreCase("join")) {
				if (args.length == 2 && sender.hasPermission("za.join")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						String gameName = args[1];
						if (!Data.gameExists(gameName)) {
							if (!player.hasPermission("za.create")) {
								sender.sendMessage(ChatColor.RED + "That game was not found");
								return true;
							} else {
								new ZAGameBase(gameName, External.getYamlManager().getConfigurationData(), true);
								sender.sendMessage(ChatColor.GRAY + "You have created a new ZA game!");
								return true;
							}
						}
						ZAPlayer zap = (ZAPlayer) Data.findZAPlayer(player, gameName);// TODO make sure the interface cast works!
						zap.loadPlayerToGame(gameName);
					} else {
						sender.sendMessage(CommonMsg.notPlayer);
						return true;
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You do not have permission to join games");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("info")) {
				// sender.sendMessage(ChatColor.GOLD + "Zombie Ablockalypse version " + Data.version);//TODO see Data.java
				// sender.sendMessage(ChatColor.GOLD + "Developed by " + StringFunctions.implode(Data.authors.toArray(), ", ", " and "));
				return true;
			} else if (args[0].equalsIgnoreCase("quit")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (Data.players.containsKey(player)) {
						ZAPlayerBase zap = Data.players.get(player);
						ZAGameBase zag = zap.getGame();
						zag.removePlayer(player);
						sender.sendMessage(ChatColor.AQUA + "Successfully quit the Ablockalypse game.");
						return true;
					} else {
						sender.sendMessage(ChatColor.RED + "You must be in an Ablockalypse game to do that.");
						return true;
					}
				} else {
					sender.sendMessage(CommonMsg.notPlayer);
					return true;
				}
			}
			return true;
		}
		return true;
	}
}
