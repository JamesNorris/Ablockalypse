package com.github.JamesNorris.Event;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Implementation.GameMysteryChest;
import com.github.JamesNorris.Implementation.GameWallSign;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Manager.YamlManager;
import com.github.JamesNorris.Threading.TeleportThread;

public class PlayerInteract implements Listener {
	private ConfigurationData cd;
	private YamlManager ym;

	public PlayerInteract() {
		ym = External.getYamlManager();
		cd = ym.getConfigurationData();
	}

	/*
	 * The event called when a player clicks a block.
	 * 
	 * USED:
	 * *When a ZAPlayer clicks a sign, to check the lines for strings that trigger a response.
	 */
	@EventHandler public void PIE(PlayerInteractEvent event) {
		Block b = event.getClickedBlock();
		Player p = event.getPlayer();
		if (b != null) {
			if (b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN) {
				event.setUseInteractedBlock(Result.DENY);
				Sign s = (Sign) b.getState();
				GameWallSign zas;
				if (!(s instanceof GameWallSign)) {
					zas = new GameWallSign(s, ym);
				} else {
					zas = (GameWallSign) s;
				}
				zas.runLines(p);
				return;
			} else if (Data.players.containsKey(p)) {
				event.setUseInteractedBlock(Result.DENY);
				ZAPlayerBase zap = Data.players.get(p);
				if (b.getType() == Material.ENDER_PORTAL_FRAME) {
					p.sendMessage(ChatColor.GRAY + "Teleportation sequence started...");
					new TeleportThread(zap, 5, true);
					return;
				} else if (b.getType() == Material.CHEST) {
					if (zap.getPoints() >= cd.mccost) {
						Chest c = (Chest) b.getState();
						GameMysteryChest mb = new GameMysteryChest(c);
						mb.randomize(p);
						zap.subtractPoints(cd.mccost);
						return;
					} else {
						p.sendMessage(ChatColor.RED + "You have " + zap.getPoints() + " points out of the " + cd.mccost + " points to buy this");
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}
}
