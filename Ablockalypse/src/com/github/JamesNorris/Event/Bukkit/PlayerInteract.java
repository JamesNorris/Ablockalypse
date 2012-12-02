package com.github.JamesNorris.Event.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Door;

import com.github.JamesNorris.External;
import com.github.JamesNorris.Data.ConfigurationData;
import com.github.JamesNorris.Data.GlobalData;
import com.github.JamesNorris.Data.LocalizationData;
import com.github.JamesNorris.Implementation.DoubleMysteryChest;
import com.github.JamesNorris.Implementation.GameArea;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Implementation.GameMobSpawner;
import com.github.JamesNorris.Implementation.GameWallSign;
import com.github.JamesNorris.Implementation.SingleMysteryChest;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Interface.GameObject;
import com.github.JamesNorris.Interface.MysteryChest;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Manager.YamlManager;
import com.github.JamesNorris.Threading.TeleportThread;
import com.github.JamesNorris.Util.MiscUtil;

public class PlayerInteract implements Listener {
	public static HashMap<String, ZAGameBase> barrierPlayers = new HashMap<String, ZAGameBase>();
	public static HashMap<String, ZAGameBase> spawnerPlayers = new HashMap<String, ZAGameBase>();
	public static HashMap<String, ZAGameBase> areaPlayers = new HashMap<String, ZAGameBase>();
	public static HashMap<String, ZAGameBase> chestPlayers = new HashMap<String, ZAGameBase>();
	public static HashMap<String, Location> locClickers = new HashMap<String, Location>();
	public static ArrayList<String> removers = new ArrayList<String>();
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
		if (b != null)
			if ((!GlobalData.playerExists(p) && barrierPlayers.containsKey(p.getName()) && b.getType() == Material.FENCE) && a == Action.RIGHT_CLICK_BLOCK) {
				new GameBarrier(b, barrierPlayers.get(p.getName()));
				p.sendMessage(ChatColor.GRAY + "Barrier created successfully!");
				barrierPlayers.remove(p.getName());
			} else if ((!GlobalData.playerExists(p) && spawnerPlayers.containsKey(p.getName())) && a == Action.RIGHT_CLICK_BLOCK) {
				spawnerPlayers.get(p.getName()).addMobSpawner(new GameMobSpawner(b.getLocation(), spawnerPlayers.get(p.getName())));
				p.sendMessage(ChatColor.GRAY + "Spawner created successfully!");
				spawnerPlayers.remove(p.getName());
			} else if ((!GlobalData.playerExists(p) && chestPlayers.containsKey(p.getName())) && a == Action.RIGHT_CLICK_BLOCK && b.getType() == Material.CHEST) {
				event.setUseInteractedBlock(Result.DENY);
				ZAGameBase zag = chestPlayers.get(p.getName());
				if (!GlobalData.isMysteryChest(b.getLocation())) {
					if (MiscUtil.getSecondChest(b.getLocation()) == null)
						chestPlayers.get(p.getName()).addMysteryChest(new SingleMysteryChest(b.getState(), zag, b.getLocation(), zag.getActiveMysteryChest() == null));
					else if (MiscUtil.getSecondChest(b.getLocation()) != null)
						chestPlayers.get(p.getName()).addMysteryChest(new DoubleMysteryChest(b.getState(), zag, b.getLocation(), MiscUtil.getSecondChest(b.getLocation()), zag.getActiveMysteryChest() == null));
					p.sendMessage(ChatColor.GRAY + "Mystery chest created successfully!");
				} else
					p.sendMessage(ChatColor.RED + "That is already a mystery chest!");
				chestPlayers.remove(p.getName());
			} else if (!GlobalData.playerExists(p) && removers.contains(p.getName()) && a == Action.RIGHT_CLICK_BLOCK) {
				boolean worked = false;
				for (GameObject o : GlobalData.objects) {
					for (Block block : o.getDefiningBlocks()) {
						if (block == b) {
							o.remove();
							worked = true;
							break;
						}
					}
				}
				if (worked)
					p.sendMessage(ChatColor.GRAY + "Removal " + ChatColor.GREEN + "sucessful");
				else
					p.sendMessage(ChatColor.GRAY + "Removal " + ChatColor.RED + "unsuccessful");
				removers.remove(p.getName());
			} else if ((!GlobalData.playerExists(p) && areaPlayers.containsKey(p.getName())) && a == Action.RIGHT_CLICK_BLOCK) {
				if (!locClickers.containsKey(p.getName())) {
					locClickers.put(p.getName(), b.getLocation());
					p.sendMessage(ChatColor.GRAY + "Click another block to select point 2.");
				} else {
					new GameArea(areaPlayers.get(p.getName()), b.getLocation(), locClickers.get(p.getName()));
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
			} else if (GlobalData.players.containsKey(p)) {
				event.setUseInteractedBlock(Result.DENY);
				ZAPlayerBase zap = GlobalData.players.get(p);
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
					Location l = b.getLocation();
					if (GlobalData.isMysteryChest(l)) {
						MysteryChest mc = GlobalData.getMysteryChest(l);
						if (mc != null && mc.isActive()) {
							if (zap.getPoints() >= cd.mccost) {
								mc.giveRandomItem(p);
								zap.subtractPoints(cd.mccost);
								return;
							} else {
								p.sendMessage(ChatColor.RED + "You have " + zap.getPoints() + " / " + cd.mccost + " points to buy this.");
								event.setCancelled(true);
								return;
							}
						} else {
							p.sendMessage(ChatColor.RED + "That chest is not active!");
							event.setCancelled(true);
							return;
						}
					}
				} else if (b instanceof Door)
					event.setCancelled(true);
				else if (b.getType() == Material.FENCE && a == Action.LEFT_CLICK_BLOCK) {
					/* through-barrier damage */
					Location loc2 = b.getLocation();
					for (ZAMob zam : zap.getGame().getMobs()) {
						Creature c = zam.getCreature();
						Location loc3 = c.getLocation();
						if (loc3.distance(loc2) <= 1.5) {
							Material m = p.getItemInHand().getType();
							int dmg = 0;
							if (m == null)
								dmg = 1;
							if (m == Material.WOOD_SWORD)
								dmg = 4;
							if (m == Material.STONE_SWORD)
								dmg = 5;
							if (m == Material.IRON_SWORD)
								dmg = 6;
							if (m == Material.GOLD_SWORD)
								dmg = 3;
							if (m == Material.DIAMOND_SWORD)
								dmg = 7;
							EntityDamageByEntityEvent EDBE = new EntityDamageByEntityEvent(p, c, DamageCause.CUSTOM, dmg);
							Bukkit.getPluginManager().callEvent(EDBE);
							c.damage(EDBE.getDamage());
							if (c.isDead()) {
								zap.addPoints(cd.pointincrease);
								int food = p.getFoodLevel();
								if (food < 20)
									p.setFoodLevel(20);
								MiscUtil.randomPowerup(zap, c);
							}
							break;
						}
					}
				}
			}
	}
}
