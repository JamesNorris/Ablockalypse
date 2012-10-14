package com.github.JamesNorris.Event.Bukkit;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Door;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.Data;
import com.github.JamesNorris.Data.LocalizationData;
import com.github.JamesNorris.Implementation.GameArea;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Implementation.GameMysteryChest;
import com.github.JamesNorris.Implementation.GameWallSign;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Manager.YamlManager;
import com.github.JamesNorris.Threading.TeleportThread;

public class PlayerInteract implements Listener {
	public static HashMap<String, ZAGameBase> barrierPlayers = new HashMap<String, ZAGameBase>();
	public static HashMap<String, ZAGameBase> spawnerPlayers = new HashMap<String, ZAGameBase>();
	public static HashMap<String, ZAGameBase> areaPlayers = new HashMap<String, ZAGameBase>();
	public static HashMap<String, GameArea> locClickers = new HashMap<String, GameArea>();
	private ConfigurationData cd;
	private YamlManager ym;
	private LocalizationData ld;

	public PlayerInteract() {
		ym = External.getYamlManager();
		cd = ym.getConfigurationData();
		ld = ym.getLocalizationData();
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
		Action a = event.getAction();
		if (b != null) {
			if ((!Data.playerExists(p) && barrierPlayers.containsKey(p.getName()) && b.getType() == Material.FENCE) && a == Action.RIGHT_CLICK_BLOCK) {
				new GameBarrier(b, barrierPlayers.get(p.getName()));
				p.sendMessage(ChatColor.GRAY + "Barrier created successfully!");
				barrierPlayers.remove(p.getName());
			} else if ((!Data.playerExists(p) && spawnerPlayers.containsKey(p.getName())) && a == Action.RIGHT_CLICK_BLOCK) {
				spawnerPlayers.get(p.getName()).addMobSpawner(b.getLocation());
				p.sendMessage(ChatColor.GRAY + "Spawner created successfully!");
				spawnerPlayers.remove(p.getName());
			} else if ((!Data.playerExists(p) && areaPlayers.containsKey(p.getName())) && a == Action.RIGHT_CLICK_BLOCK) {
				if (!locClickers.containsKey(p.getName())) {
					GameArea ga = new GameArea(areaPlayers.get(p.getName()));
					ga.setLocation(b.getLocation(), 1);
					locClickers.put(p.getName(), ga);
					p.sendMessage(ChatColor.GRAY + "Click another block to select point 2.");
				} else {
					GameArea ga = locClickers.get(p.getName());
					ga.setLocation(b.getLocation(), 2);
					locClickers.remove(p.getName());
					areaPlayers.remove(p.getName());
					p.sendMessage(ChatColor.GRAY + "Area created!");
				}
			} else if ((b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN) && a == Action.RIGHT_CLICK_BLOCK) {
				Sign s = (Sign) b.getState();
				if (s.getLine(0).equalsIgnoreCase(ld.first)) {
					event.setUseInteractedBlock(Result.DENY);
					GameWallSign zas;
					if (!(s instanceof GameWallSign))
						zas = new GameWallSign(s, ym);
					else
						zas = (GameWallSign) s;
					zas.runLines(p);
					return;
				}
				return;
			} else if (Data.players.containsKey(p)) {
				event.setUseInteractedBlock(Result.DENY);
				ZAPlayerBase zap = Data.players.get(p);
				if (b.getType() == Material.ENDER_PORTAL_FRAME && a == Action.RIGHT_CLICK_BLOCK) {
					if (!zap.isTeleporting()) {
						p.sendMessage(ChatColor.GRAY + "Teleportation sequence started...");
						new TeleportThread(zap, cd.teleportTime, true);
						return;
					} else {
						p.sendMessage(ChatColor.GRAY + "You are already teleporting!");
						return;
					}
				} else if (b.getType() == Material.CHEST && a == Action.RIGHT_CLICK_BLOCK) {
					if (zap.getPoints() >= cd.mccost) {
						Chest c = (Chest) b.getState();
						GameMysteryChest mb = new GameMysteryChest(c);
						mb.giveItem(p);
						zap.subtractPoints(cd.mccost);
						return;
					} else {
						p.sendMessage(ChatColor.RED + "You have " + zap.getPoints() + " / " + cd.mccost + " points to buy this.");
						event.setCancelled(true);
						return;
					}
				} else if (b instanceof Door) {
					event.setCancelled(true);
				}
			}
		}
	}
}
