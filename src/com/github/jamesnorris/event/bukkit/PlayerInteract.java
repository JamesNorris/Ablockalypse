package com.github.jamesnorris.event.bukkit;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.External;
import com.github.jamesnorris.enumerated.Local;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.enumerated.ZAEnchantment;
import com.github.jamesnorris.enumerated.ZAPerk;
import com.github.jamesnorris.event.GameCreateEvent;
import com.github.jamesnorris.event.GameSignClickEvent;
import com.github.jamesnorris.implementation.Area;
import com.github.jamesnorris.implementation.Barrier;
import com.github.jamesnorris.implementation.Mainframe;
import com.github.jamesnorris.implementation.MobSpawner;
import com.github.jamesnorris.implementation.MysteryChest;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.threading.TeleportThread;
import com.github.jamesnorris.threading.TeleporterLinkageTimerThread;
import com.github.jamesnorris.util.ItemInfoMap;
import com.github.jamesnorris.util.MathAssist;
import com.github.jamesnorris.util.MiscUtil;
import com.github.jamesnorris.util.ShotResult;

public class PlayerInteract extends DataManipulator implements Listener {
    public static HashMap<String, Game> areaPlayers = new HashMap<String, Game>();
    public static HashMap<String, Game> barrierPlayers = new HashMap<String, Game>();
    public static HashMap<String, Game> chestPlayers = new HashMap<String, Game>();
    public static HashMap<String, Game> locdataPlayers = new HashMap<String, Game>();
    public static HashMap<String, String> locdataTemp = new HashMap<String, String>();
    public static HashMap<String, Location> locClickers = new HashMap<String, Location>();
    public static ArrayList<String> removers = new ArrayList<String>();
    public static HashMap<String, Game> spawnerPlayers = new HashMap<String, Game>();
    public static HashMap<ZAPlayer, Location> mainframeLinkers = new HashMap<ZAPlayer, Location>();
    public static HashMap<ZAPlayer, TeleporterLinkageTimerThread> mainframeLinkers_Timers = new HashMap<ZAPlayer, TeleporterLinkageTimerThread>();

    private void buyArea(Sign sign, Player player, ZAPlayer zap, String l3, int points) {
        int cost = 1500;
        try {
            cost = Integer.parseInt(l3);
        } catch (Exception e) {
            Location l = sign.getLocation();
            Ablockalypse.crash("The sign at " + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ() + " is incorrectly formatted!", false);
        }
        sign.setLine(3, " " + cost + " ");
        if (zap.getPoints() >= cost) {
            Area a = getClosestArea(sign.getBlock(), (Game) zap.getGame());
            if (a != null) {
                if (!a.isOpened()) {
                    a.open();
                    ZAEffect.POTION_BREAK.play(sign.getLocation());
                    zap.subtractPoints(cost);
                    player.sendMessage(ChatColor.BOLD + "You have bought an area for " + cost + " points.");
                    return;
                } else
                    player.sendMessage(ChatColor.RED + "This area has already been purchased!");
            } else
                player.sendMessage(ChatColor.RED + "There is no area close to this sign!");
            return;
        } else {
            player.sendMessage(ChatColor.RED + "You have " + zap.getPoints() + " / " + cost + " points to buy this.");
            return;
        }
    }

    /*
     * Gets the closest game area to the given block.
     */
    private Area getClosestArea(Block b, Game zag) {
        int distance = Integer.MAX_VALUE;
        Location loc = b.getLocation();
        Area lp = null;
        for (Area a : data.areas)
            if (a.getGame() == zag) {
                Location l = a.getPoint(1);
                int current = (int) MathAssist.distance(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
                if (current < distance) {
                    distance = current;
                    lp = a;
                }
            }
        if (lp != null)
            return lp;
        return null;
    }

    private void giveEnchantment(Sign sign, Player player, ZAPlayer zap, String l3, int points) {
        for (ZAEnchantment ench : ZAEnchantment.values()) {
            if (l3.equalsIgnoreCase(ench.getLabel())) {
                if (zap.getPoints() < ench.getCost()) {
                    player.sendMessage(ChatColor.RED + "You have " + points + " / " + ench.getCost() + " points to buy this.");
                    return;
                }
                ItemStack hand = player.getItemInHand();
                player.getInventory().remove(hand);
                hand.addEnchantment(ench.getEnchantment(), 3);
                zap.subtractPoints(ench.getCost());
                player.sendMessage(ChatColor.BOLD + "You have bought " + l3 + " for " + ench.getCost() + " points!");
                ZAEffect.POTION_BREAK.play(sign.getLocation());
                MiscUtil.dropItemAtPlayer(sign.getLocation(), hand, player, 1);
                return;
            }
        }
    }

    private void givePerk(Sign sign, Player player, ZAPlayer zap, String l3, int points) {
        for (ZAPerk perk : ZAPerk.values()) {
            if (l3.equalsIgnoreCase(perk.getLabel()) && zap.getGame().getLevel() >= perk.getLevel()) {
                if (zap.getPoints() < perk.getCost()) {
                    player.sendMessage(ChatColor.RED + "You have " + points + " / " + perk.getCost() + " points to buy this.");
                    return;
                }
                perk.givePerk(zap);
                zap.subtractPoints(perk.getCost());
                player.sendMessage(ChatColor.BOLD + "You have bought " + l3 + " for " + perk.getCost() + " points!");
                ZAEffect.POTION_BREAK.play(sign.getLocation());
                return;
            }
        }
    }
    
    public static ArrayList<Game> fireSale = new ArrayList<Game>();

    private void giveItem(Sign sign, Player player, ZAPlayer zap, String l3, int points) {
        HashMap<Integer, ItemInfoMap> maps = External.itemManager.getSignItemMaps();
        for (int id : maps.keySet()) {
            ItemInfoMap map = maps.get(id);
            int cost = (sign.getLine(3).isEmpty()) ? map.cost : Integer.parseInt(sign.getLine(3));
            if (l3.equalsIgnoreCase(map.name) && zap.getGame().getLevel() >= map.level) {
                if (MiscUtil.anyItemRegulationsBroken(zap, map.id, cost))
                    return;
                Material type = Material.getMaterial(map.id);
                MiscUtil.dropItemAtPlayer(sign.getLocation(), new ItemStack(type, map.dropamount), player, 1);
                if (fireSale.contains(zap.getGame())) {
                    cost = 10;
                }
                zap.subtractPoints(cost);
                player.sendMessage(ChatColor.BOLD + "You have bought " + l3 + " for " + cost + " points!");
                ZAEffect.POTION_BREAK.play(sign.getLocation());
                return;
            }
        }
    }

    private void joinGame(Sign sign, Player player, String l3) {
        if (player.hasPermission("za.create") && !data.games.containsKey(l3)) {
            setupPlayerWithGame(l3, player);
            player.sendMessage(ChatColor.RED + "This game does not have any barriers. Ignoring...");
            return;
        } else if (data.games.containsKey(l3)) {
            setupPlayerWithGame(l3, player);
            ZAEffect.POTION_BREAK.play(sign.getLocation());
            return;
        } else {
            player.sendMessage(ChatColor.RED + "That game does not exist!");
            return;
        }
    }

    public boolean isArea(Block block) {
        for (Area area : data.areas) {
            if (area.getBlocks().contains(block))
                return true;
        }
        return false;
    }

    /*
     * The event called when a player clicks a block.
     * 
     * USED:
     * *When a ZAPlayer clicks a sign, to check the lines for strings that trigger a response.
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void PIE(PlayerInteractEvent event) {
        Block b = event.getClickedBlock();
        Player p = event.getPlayer();
        Action a = event.getAction();
        if (b != null) {
            if ((!data.playerExists(p) && barrierPlayers.containsKey(p.getName()) && b.getType() == Material.FENCE) && a == Action.RIGHT_CLICK_BLOCK) {
                if (data.isBarrier(b.getLocation())) {
                    p.sendMessage(ChatColor.RED + "That is already a barrier!");
                    return;
                }
                new Barrier(b, barrierPlayers.get(p.getName()));
                p.sendMessage(ChatColor.GRAY + "Barrier created successfully!");
                barrierPlayers.remove(p.getName());
            } else if ((!data.playerExists(p) && spawnerPlayers.containsKey(p.getName())) && a == Action.RIGHT_CLICK_BLOCK) {
                if (data.isMobSpawner(b.getLocation()) && data.mobSpawners.containsKey(spawnerPlayers.get(p.getName()))) {
                    p.sendMessage(ChatColor.RED + "That is already a mob spawner for this game!");
                    return;
                }
                spawnerPlayers.get(p.getName()).addMobSpawner(new MobSpawner(b.getLocation(), spawnerPlayers.get(p.getName())));
                p.sendMessage(ChatColor.GRAY + "Spawner created successfully!");
                spawnerPlayers.remove(p.getName());
            } else if ((!data.playerExists(p) && chestPlayers.containsKey(p.getName())) && a == Action.RIGHT_CLICK_BLOCK && b.getType() == Material.CHEST) {
                event.setUseInteractedBlock(Result.DENY);
                Game zag = chestPlayers.get(p.getName());
                if (!data.isMysteryChest(b.getLocation())) {
                    chestPlayers.get(p.getName()).addMysteryChest(new MysteryChest(b.getState(), zag, b.getLocation(), zag.getActiveMysteryChest() == null));
                    p.sendMessage(ChatColor.GRAY + "Mystery chest created successfully!");
                } else
                    p.sendMessage(ChatColor.RED + "That is already a mystery chest!");
                chestPlayers.remove(p.getName());
            } else if (!data.playerExists(p) && removers.contains(p.getName()) && a == Action.RIGHT_CLICK_BLOCK) {
                event.setUseInteractedBlock(Result.DENY);
                GameObject removal = null;
                for (GameObject o : data.gameObjects) {
                    for (Block block : o.getDefiningBlocks()) {
                        if (block != null && block.getLocation().distance(b.getLocation()) <= 1) {
                            removal = o;
                            break;
                        }
                    }
                }
                if (removal != null) {
                    removal.remove();
                    p.sendMessage(ChatColor.GRAY + "Removal " + ChatColor.GREEN + "successful");
                } else {
                    p.sendMessage(ChatColor.GRAY + "Removal " + ChatColor.RED + "unsuccessful");
                }
                removers.remove(p.getName());
            } else if ((!data.playerExists(p) && areaPlayers.containsKey(p.getName())) && a == Action.RIGHT_CLICK_BLOCK) {
                if (isArea(b)) {
                    p.sendMessage(ChatColor.RED + "That is already an area!");
                    return;
                }
                if (!locClickers.containsKey(p.getName())) {
                    locClickers.put(p.getName(), b.getLocation());
                    p.sendMessage(ChatColor.GRAY + "Click another block to select point 2.");
                } else {
                    new Area(areaPlayers.get(p.getName()), b.getLocation(), locClickers.get(p.getName()));
                    locClickers.remove(p.getName());
                    areaPlayers.remove(p.getName());
                    p.sendMessage(ChatColor.GRAY + "Area created!");
                }
            } else if ((b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN) && a == Action.RIGHT_CLICK_BLOCK) {
                Sign s = (Sign) b.getState();
                if (s.getLine(0).equalsIgnoreCase(Local.BASE_STRING.getSetting())) {
                    event.setUseInteractedBlock(Result.DENY);
                    runLines(s, p);
                    return;
                }
                return;
            } else if (data.players.containsKey(p)) {
                if (!BlockPlace.shouldBePlaced(p.getItemInHand().getType())) {
                    event.setUseInteractedBlock(Result.DENY);
                }
                ZAPlayer zap = data.players.get(p);
                Game game = zap.getGame();
                if (b.getType() == Material.ENDER_PORTAL_FRAME && a == Action.RIGHT_CLICK_BLOCK) {
                    if (MiscUtil.locationMatch(b.getLocation(), game.getMainframe().getLocation(), 2)) {
                        // mainframe
                        Mainframe frame = game.getMainframe();
                        if (PlayerInteract.mainframeLinkers.containsKey(zap)) {
                            TeleporterLinkageTimerThread tltt = PlayerInteract.mainframeLinkers_Timers.get(zap);
                            if (tltt.canBeLinked()) {
                                p.sendMessage(ChatColor.GREEN + "Teleporter linked!");
                                tltt.setLinked(true);
                                frame.link(PlayerInteract.mainframeLinkers.get(zap));
                            }
                            PlayerInteract.mainframeLinkers.remove(zap);
                            PlayerInteract.mainframeLinkers_Timers.remove(zap);
                        }
                    } else {
                        // not mainframe
                        if (game.getMainframe().isLinked(b.getLocation())) {
                            // this teleporter linked to the mainframe...
                            if (!zap.isTeleporting()) {
                                p.sendMessage(ChatColor.GRAY + "Teleportation sequence started...");
                                new TeleportThread(zap, (Integer) Setting.TELEPORT_TIME.getSetting(), true, 20);
                                return;
                            } else {
                                p.sendMessage(ChatColor.GRAY + "You are already teleporting!");
                                return;
                            }
                        } else {
                            // this teleporter is not linked to the mainframe...
                            Location loc = b.getLocation();
                            Location below = loc.clone().subtract(0, 1, 0);
                            int time = (int) loc.distance(game.getMainframe().getLocation()) * 20;// 1 second per block difference
                            if (below.getBlock().getState() instanceof Sign) {
                                Sign sign = (Sign) below.getBlock().getState();
                                try {
                                    time = Integer.parseInt(sign.getLine(0));
                                } catch (Exception e) {
                                    //nothing
                                }
                            }
                            PlayerInteract.mainframeLinkers.put(zap, loc);
                            PlayerInteract.mainframeLinkers_Timers.put(zap, new TeleporterLinkageTimerThread(game.getMainframe(), zap, time)); // difference
                            p.sendMessage(ChatColor.GRAY + "You now have " + time / 20 + " seconds to link the teleporter to the mainframe!");
                        }
                    }
                } else if (b.getType() == Material.CHEST && a == Action.RIGHT_CLICK_BLOCK) {
                    Location l = b.getLocation();
                    if (data.isMysteryChest(l)) {
                        MysteryChest mc = data.getMysteryChest(l);
                        if (mc != null && mc.isActive()) {
                            if (zap.getPoints() >= (Integer) Setting.CHEST_COST.getSetting()) {
                                mc.giveRandomItem(zap);
                                zap.subtractPoints((Integer) Setting.CHEST_COST.getSetting());
                                return;
                            } else {
                                p.sendMessage(ChatColor.RED + "You have " + zap.getPoints() + " / " + (Integer) Setting.CHEST_COST.getSetting() + " points to buy this.");
                                event.setCancelled(true);
                                return;
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "That chest is not active!");
                            event.setCancelled(true);
                            return;
                        }
                    }
                } else if (b.getType() == Material.WOOD_DOOR || b.getType() == Material.IRON_DOOR) {
                    event.setCancelled(true);
                } else if (b.getType() == Material.FENCE && a == Action.LEFT_CLICK_BLOCK) {
                    // through-barrier damage
                    if (data.isBarrier(b.getLocation())) {
                        short damage = p.getItemInHand().getDurability();
                        ShotResult result = zap.shoot(3, 1, damage, false, true);
                        for (LivingEntity le : result.getLivingEntitiesHit()) {
                            EntityDeath.behaveLikeKill(p, le);
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks the lines of the sign for strings in the config, that enable changes to be made to the player.
     * 
     * @param sign The sign to run lines from
     * @param player The player to affect if the lines are run through
     */
    @SuppressWarnings("deprecation") public void runLines(Sign sign, Player player) {
        String l1 = sign.getLine(0);
        String l2 = sign.getLine(1);
        String l3 = sign.getLine(2);
        // String l4 = sign.getLine(3);//UNUSED
        if (l1.equalsIgnoreCase(Local.BASE_STRING.getSetting())) {
            GameSignClickEvent gsce = new GameSignClickEvent(sign, player);
            Bukkit.getPluginManager().callEvent(gsce);
            if (!gsce.isCancelled()) {
                if (!data.players.containsKey(player)) {// JOIN
                    if (l2.equalsIgnoreCase(Local.BASE_JOIN_STRING.getSetting())) {
                        joinGame(sign, player, l3);
                    } else if (l2.equalsIgnoreCase(Local.BASE_PERK_STRING.getSetting())) {
                        player.sendMessage(ChatColor.GRAY + "This sign can be used in-game to purchase the " + l3 + " perk.");
                    } else if (l2.equalsIgnoreCase(Local.BASE_ENCHANTMENT_STRING.getSetting())) {
                        player.sendMessage(ChatColor.GRAY + "This sign can be used in-game to purchase the " + l3 + " enchantment for any weapon that behaves like a sword.");
                    } else if (l2.equalsIgnoreCase(Local.BASE_WEAPON_STRING.getSetting())) {
                        player.sendMessage(ChatColor.GRAY + "This sign can be used in-game to purchase the " + l3 + " weapon.");
                    } else if (l2.equalsIgnoreCase(Local.BASE_AREA_STRING.getSetting())) {
                        player.sendMessage(ChatColor.GRAY + "This sign can be used in-game to open the " + l3 + " area.");
                    }
                } else if (data.players.containsKey(player)) {
                    ZAPlayer zap = data.players.get(player);
                    int points = zap.getPoints();
                    if (l2.equalsIgnoreCase(Local.BASE_PERK_STRING.getSetting()))// PERK
                        givePerk(sign, player, zap, l3, points);
                    else if (l2.equalsIgnoreCase(Local.BASE_ENCHANTMENT_STRING.getSetting()) && MiscUtil.isEnchantableLikeSwords(player.getItemInHand()))// ENCHANTMENT
                        giveEnchantment(sign, player, zap, l3, points);
                    else if (l2.equalsIgnoreCase(Local.BASE_WEAPON_STRING.getSetting()))// WEAPON
                        giveItem(sign, player, zap, l3, points);
                    else if (l2.equalsIgnoreCase(Local.BASE_AREA_STRING.getSetting()))// AREA
                        buyArea(sign, player, zap, l3, points);
                    player.updateInventory();
                }
            }
        }
    }

    /*
     * Checks for the game and player to create a new game instance and player instance.
     */
    private void setupPlayerWithGame(String name, Player player) {
        boolean exists = data.gameExists(name);
        Game zag = data.getGame(name, true);
        ZAPlayer zap = data.getZAPlayer(player, name, true);
        if (zag.getMainframe() == null)
            zag.setMainframe(new Mainframe(zag, player.getLocation()));
        if (!exists) {
            GameCreateEvent gce = new GameCreateEvent(zag, null, player);
            Bukkit.getServer().getPluginManager().callEvent(gce);
            if (!gce.isCancelled())
                zap.loadPlayerToGame(name);
            else
                zag.remove();
        } else
            zap.loadPlayerToGame(name);
    }
}
