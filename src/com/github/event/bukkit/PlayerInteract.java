package com.github.event.bukkit;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.Barrier;
import com.github.aspect.Game;
import com.github.aspect.Mainframe;
import com.github.aspect.MobSpawner;
import com.github.aspect.MysteryChest;
import com.github.aspect.Passage;
import com.github.aspect.PowerSwitch;
import com.github.aspect.ZAPlayer;
import com.github.behavior.GameObject;
import com.github.behavior.Powerable;
import com.github.enumerated.Local;
import com.github.enumerated.Setting;
import com.github.enumerated.ZAEffect;
import com.github.enumerated.ZAEnchantment;
import com.github.enumerated.ZAPerk;
import com.github.event.GameCreateEvent;
import com.github.event.GameSignClickEvent;
import com.github.storage.MapDataStorage;
import com.github.threading.inherent.TeleportThread;
import com.github.threading.inherent.TeleporterLinkageTimerThread;
import com.github.utility.BuyableItem;
import com.github.utility.MiscUtil;
import com.github.utility.selection.Rectangle;

public class PlayerInteract implements Listener {
    public static HashMap<String, Game> barrierPlayers = new HashMap<String, Game>();
    public static HashMap<String, Game> chestPlayers = new HashMap<String, Game>();
    public static ArrayList<Game> fireSale = new ArrayList<Game>();
    public static HashMap<String, Location> locClickers = new HashMap<String, Location>();
    public static HashMap<String, Game> locdataPlayers = new HashMap<String, Game>();
    public static HashMap<String, String> locdataTemp = new HashMap<String, String>();
    public static HashMap<ZAPlayer, Location> mainframeLinkers = new HashMap<ZAPlayer, Location>();
    public static HashMap<ZAPlayer, TeleporterLinkageTimerThread> mainframeLinkers_Timers = new HashMap<ZAPlayer, TeleporterLinkageTimerThread>();
    public static HashMap<String, Game> passagePlayers = new HashMap<String, Game>();
    public static ArrayList<String> removers = new ArrayList<String>();
    public static HashMap<String, Game> spawnerPlayers = new HashMap<String, Game>();
    public static HashMap<String, String> mapDataSavePlayers = new HashMap<String, String>();
    public static HashMap<String, Location> mapDataPoint1SaveClickers = new HashMap<String, Location>();
    public static HashMap<String, String> mapDataLoadPlayers = new HashMap<String, String>();
    public static HashMap<String, Game> powerSwitchClickers = new HashMap<String, Game>();
    public static HashMap<String, Boolean> powerClickers = new HashMap<String, Boolean>();
    public static HashMap<String, String> powerClickerGames = new HashMap<String, String>();// TODO a better playerinteract queue system
    private DataContainer data = Ablockalypse.getData();

    public boolean isPassage(Block block) {
        for (Passage passage : data.getObjectsOfType(Passage.class)) {
            if (passage.getBlocks().contains(block)) {
                return true;
            }
        }
        return false;
    }

    /* The event called when a player clicks a block.
     * 
     * USED:
     * *When a ZAPlayer clicks a sign, to check the lines for strings that trigger a response. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PIE(PlayerInteractEvent event) {
        Block b = event.getClickedBlock();
        Player p = event.getPlayer();
        Action a = event.getAction();
        if (b != null) {
            Location loc = b.getLocation();
            if (!data.isZAPlayer(p) && barrierPlayers.containsKey(p.getName()) && a == Action.RIGHT_CLICK_BLOCK) {
                if (data.isBarrier(loc)) {
                    p.sendMessage(ChatColor.RED + "That is already a barrier!");
                    return;
                }
                new Barrier(loc, barrierPlayers.get(p.getName()));
                p.sendMessage(ChatColor.GRAY + "Barrier created successfully!");
                barrierPlayers.remove(p.getName());
            } else if (!data.isZAPlayer(p) && powerSwitchClickers.containsKey(p.getName()) && a == Action.RIGHT_CLICK_BLOCK) {
                if (data.isPowerSwitch(loc)) {
                    p.sendMessage(ChatColor.RED + "That is already a power switch!");
                    return;
                }
                new PowerSwitch(powerSwitchClickers.get(p.getName()), loc);
                p.sendMessage(ChatColor.GRAY + "Switch created successfully.");
                powerSwitchClickers.remove(p.getName());
                return;
            } else if (!data.isZAPlayer(p) && powerClickers.containsKey(p.getName()) && a == Action.RIGHT_CLICK_BLOCK) {
                if (data.isGameObject(loc)) {
                    GameObject object = data.getGameObjectByLocation(loc);
                    if (object instanceof Powerable && object.getGame() != null && object.getGame() == data.getGame(powerClickerGames.get(p.getName()), false)) {
                        Powerable powerObj = (Powerable) object;
                        boolean power = powerClickers.get(p.getName());
                        powerObj.setRequiresPower(power);
                        p.sendMessage(ChatColor.GRAY + "Power on this object " + (power ? "enabled" : "disabled") + ".");
                        return;
                    }
                    p.sendMessage(ChatColor.RED + "Either this object is not powerable or it does not match the game entered.");
                    return;
                }
                p.sendMessage(ChatColor.RED + "This is not an object!");
                powerClickers.remove(p.getName());
                powerClickerGames.remove(p.getName());
                return;
            } else if (!data.isZAPlayer(p) && spawnerPlayers.containsKey(p.getName()) && a == Action.RIGHT_CLICK_BLOCK) {
                if (data.isMobSpawner(b.getLocation()) && data.getMobSpawner(b.getLocation()).getGame() == spawnerPlayers.get(p.getName())) {
                    p.sendMessage(ChatColor.RED + "That is already a mob spawner for this game!");
                    return;
                }
                spawnerPlayers.get(p.getName()).addObject(new MobSpawner(loc, spawnerPlayers.get(p.getName())));
                p.sendMessage(ChatColor.GRAY + "Spawner created successfully!");
                spawnerPlayers.remove(p.getName());
            } else if (!data.isZAPlayer(p) && chestPlayers.containsKey(p.getName()) && a == Action.RIGHT_CLICK_BLOCK && b.getType() == Material.CHEST) {
                event.setUseInteractedBlock(Result.DENY);
                Game zag = chestPlayers.get(p.getName());
                if (!data.isMysteryChest(b.getLocation())) {
                    chestPlayers.get(p.getName()).addObject(new MysteryChest(zag, loc, zag.getActiveMysteryChest() == null));
                    p.sendMessage(ChatColor.GRAY + "Mystery chest created successfully!");
                } else {
                    p.sendMessage(ChatColor.RED + "That is already a mystery chest!");
                }
                chestPlayers.remove(p.getName());
            } else if (!data.isZAPlayer(p) && removers.contains(p.getName()) && a == Action.RIGHT_CLICK_BLOCK) {
                event.setUseInteractedBlock(Result.DENY);
                GameObject removal = null;
                for (GameObject o : data.getObjectsOfType(GameObject.class)) {
                    for (Block block : o.getDefiningBlocks()) {
                        if (block != null && block.getLocation().distanceSquared(b.getLocation()) <= 1) {// 1 squared = 1
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
            } else if (!data.isZAPlayer(p) && passagePlayers.containsKey(p.getName()) && a == Action.RIGHT_CLICK_BLOCK) {
                if (isPassage(b)) {
                    p.sendMessage(ChatColor.RED + "That is already a passage!");
                    return;
                }
                if (!locClickers.containsKey(p.getName())) {
                    locClickers.put(p.getName(), b.getLocation());
                    p.sendMessage(ChatColor.GRAY + "Click another block to select point 2.");
                } else {
                    Location loc2 = locClickers.get(p.getName());
                    new Passage(passagePlayers.get(p.getName()), loc, loc2);
                    locClickers.remove(p.getName());
                    passagePlayers.remove(p.getName());
                    p.sendMessage(ChatColor.GRAY + "Passage created!");
                }
            } else if (!data.isZAPlayer(p) && mapDataSavePlayers.containsKey(p.getName()) && a == Action.RIGHT_CLICK_BLOCK) {
                if (!mapDataPoint1SaveClickers.containsKey(p.getName())) {
                    mapDataPoint1SaveClickers.put(p.getName(), b.getLocation());
                    p.sendMessage(ChatColor.GRAY + "Please click the other corner of the map.");
                } else {
                    boolean saved = new MapDataStorage(mapDataSavePlayers.get(p.getName())).save(new Rectangle(mapDataPoint1SaveClickers.get(p.getName()), b.getLocation()));
                    mapDataPoint1SaveClickers.remove(p.getName());
                    mapDataSavePlayers.remove(p.getName());
                    String successful = saved ? ChatColor.GREEN + "successfully" + ChatColor.RESET : ChatColor.RED + "unsuccessfully" + ChatColor.RESET;
                    p.sendMessage(ChatColor.GRAY + "Mapdata saved " + successful + ".");
                }
            } else if (!data.isZAPlayer(p) && mapDataLoadPlayers.containsKey(p.getName()) && a == Action.RIGHT_CLICK_BLOCK) {
                boolean loaded = MapDataStorage.getFromGame(mapDataLoadPlayers.get(p.getName())).load(b.getLocation());
                mapDataLoadPlayers.remove(p.getName());
                String successful = loaded ? ChatColor.GREEN + "successfully" + ChatColor.RESET : ChatColor.RED + "unsuccessfully" + ChatColor.RESET;
                p.sendMessage(ChatColor.GRAY + "Mapdata loaded " + successful + ".");
            } else if ((b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN) && a == Action.RIGHT_CLICK_BLOCK) {
                Sign s = (Sign) b.getState();
                if (s.getLine(0).equalsIgnoreCase(Local.BASE_STRING.getSetting())) {
                    event.setUseInteractedBlock(Result.DENY);
                    runLines(s, p);
                    return;
                }
                return;
            } else if (data.isZAPlayer(p)) {
                if (!BlockPlace.shouldBePlaced(p.getItemInHand().getType())) {
                    event.setUseItemInHand(Result.DENY);
                }
                ZAPlayer zap = data.getZAPlayer(p);
                Game game = zap.getGame();
                if (b.getType() == Material.ENDER_PORTAL_FRAME && a == Action.RIGHT_CLICK_BLOCK) {

                } else if (b.getType() == Material.CHEST && a == Action.RIGHT_CLICK_BLOCK) {
                    Location l = b.getLocation();
                    if (data.isMysteryChest(l)) {
                        MysteryChest mc = data.getMysteryChest(l);
                        if (mc != null && mc.isActive()) {
                            mc.giveRandomItem(zap);
                        } else {
                            p.sendMessage(ChatColor.RED + "That chest is not active!");
                            event.setCancelled(true);
                            return;
                        }
                    }
                } else if (b.getType() == Material.WOOD_DOOR || b.getType() == Material.IRON_DOOR) {
                    event.setCancelled(true);
                } else if (b.getType() == Material.FENCE && a == Action.LEFT_CLICK_BLOCK) {
                    // through-fence damage
                    short damage = p.getItemInHand().getDurability();
                    zap.shoot(3, 1, damage, false, true);
                } else if (a == Action.RIGHT_CLICK_BLOCK) {
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
                    } else if (b.getType() == Material.ENDER_PORTAL_FRAME) {
                        // not mainframe
                        int time = -1;
                        boolean requiresLinkage = true;
                        Location below = loc.clone().subtract(0, 1, 0);
                        if (below.getBlock().getState() instanceof Sign) {
                            Sign sign = (Sign) below.getBlock().getState();
                            try {
                                requiresLinkage = Boolean.parseBoolean(sign.getLine(0));
                                time = Integer.parseInt(sign.getLine(1));
                            } catch (Exception e) {
                                // nothing
                            }
                        }
                        if (game.getMainframe().isLinked(b.getLocation()) || !requiresLinkage) {
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
                            time = time == -1 ? (int) (loc.distanceSquared(game.getMainframe().getLocation()) * 4.5) : time;// 1 second per block difference (4.5 approx sqrt 20)
                            PlayerInteract.mainframeLinkers.put(zap, loc);
                            PlayerInteract.mainframeLinkers_Timers.put(zap, new TeleporterLinkageTimerThread(game.getMainframe(), zap, time)); // difference
                            p.sendMessage(ChatColor.GRAY + "You now have " + time / 20 + " seconds to link the teleporter to the mainframe!");
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
                if (!data.isZAPlayer(player)) {// JOIN
                    if (l2.equalsIgnoreCase(Local.BASE_JOIN_STRING.getSetting())) {
                        joinGame(sign, player, l3);
                    } else if (l2.equalsIgnoreCase(Local.BASE_PERK_STRING.getSetting())) {
                        player.sendMessage(ChatColor.GRAY + "This sign can be used in-game to purchase the " + l3 + " perk.");
                    } else if (l2.equalsIgnoreCase(Local.BASE_ENCHANTMENT_STRING.getSetting())) {
                        player.sendMessage(ChatColor.GRAY + "This sign can be used in-game to purchase the " + l3 + " enchantment for any weapon that behaves like a sword.");
                    } else if (l2.equalsIgnoreCase(Local.BASE_WEAPON_STRING.getSetting())) {
                        player.sendMessage(ChatColor.GRAY + "This sign can be used in-game to purchase the " + l3 + " weapon.");
                    } else if (l2.equalsIgnoreCase(Local.BASE_PASSAGE_STRING.getSetting())) {
                        player.sendMessage(ChatColor.GRAY + "This sign can be used in-game to open the closest passage for " + l3 + " points.");
                    }
                } else if (data.isZAPlayer(player)) {
                    ZAPlayer zap = data.getZAPlayer(player);
                    int points = zap.getPoints();
                    if (l2.equalsIgnoreCase(Local.BASE_PERK_STRING.getSetting())) {
                        givePerk(sign, player, zap, l3, points);
                    } else if (l2.equalsIgnoreCase(Local.BASE_ENCHANTMENT_STRING.getSetting()) && MiscUtil.isEnchantableLikeSwords(player.getItemInHand())) {
                        giveEnchantment(sign, player, zap, l3, points);
                    } else if (l2.equalsIgnoreCase(Local.BASE_WEAPON_STRING.getSetting())) {
                        giveItem(sign, player, zap, l3, points);
                    } else if (l2.equalsIgnoreCase(Local.BASE_PASSAGE_STRING.getSetting())) {
                        buyPassage(sign, player, zap, l3, points);
                    }
                    player.updateInventory();
                }
            }
        }
    }

    private void buyPassage(Sign sign, Player player, ZAPlayer zap, String l3, int points) {
        int cost = 1500;
        try {
            cost = Integer.parseInt(l3);
        } catch (Exception e) {
            Ablockalypse.crash("The sign at " + sign.getLocation().toString() + " does not have a cost value on line 4.", 0);
            player.sendMessage(ChatColor.RED + "That sign is incorrectly formatted.\nThe Ops have already been alerted.");
        }
        sign.setLine(3, " " + cost + " ");
        if (zap.getPoints() >= cost) {
            Passage a = getClosestPassage(sign.getBlock(), zap.getGame());
            if (a != null) {
                if (!a.isOpened()) {
                    a.open();
                    ZAEffect.POTION_BREAK.play(sign.getLocation());
                    zap.subtractPoints(cost);
                    player.sendMessage(ChatColor.BOLD + "You have bought a passage for " + cost + " points.");
                    return;
                } else {
                    player.sendMessage(ChatColor.RED + "This passage has already been purchased!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "There is no passage close to this sign!");
            }
            return;
        } else {
            player.sendMessage(ChatColor.RED + "You have " + zap.getPoints() + " / " + cost + " points to buy this.");
            return;
        }
    }

    /* Gets the closest game area to the given block. */
    private Passage getClosestPassage(Block b, Game zag) {
        double distanceSquared = Double.MAX_VALUE;
        Location loc = b.getLocation();
        Passage lp = null;
        for (Passage a : data.getObjectsOfType(Passage.class)) {
            if (a.getGame() == zag) {
                Location l = a.getPoint(1);
                double current = l.distanceSquared(loc);
                if (current < distanceSquared) {
                    distanceSquared = current;
                    lp = a;
                }
            }
        }
        if (lp != null) {
            return lp;
        }
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
                MiscUtil.dropItemAtPlayer(sign.getLocation(), hand, player, 1, 1);
                return;
            }
        }
    }

    private void giveItem(Sign sign, Player player, ZAPlayer zap, String l3, int points) {
        HashMap<Integer, BuyableItem> maps = Ablockalypse.getExternal().getItemFileManager().getSignItemMaps();
        for (int id : maps.keySet()) {
            BuyableItem map = maps.get(id);
            int cost = sign.getLine(3).isEmpty() ? map.getCost() : Integer.parseInt(sign.getLine(3));
            if (l3.equalsIgnoreCase(map.getName()) && zap.getGame().getLevel() >= map.getRequiredLevel()) {
                if (points < cost) {
                    player.sendMessage(ChatColor.RED + "You have " + points + " / " + cost + " points to buy this.");
                    return;
                }
                MiscUtil.dropItemAtPlayer(sign.getLocation(), map.toItemStack(), player, 1, 1);
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

    private void joinGame(Sign sign, Player player, String l3) {
        if (player.hasPermission("za.create") && !data.isGame(l3)) {
            setupPlayerWithGame(l3, player);
            player.sendMessage(ChatColor.RED + "This game does not have any barriers. Ignoring...");
            return;
        } else if (data.isGame(l3)) {
            setupPlayerWithGame(l3, player);
            ZAEffect.POTION_BREAK.play(sign.getLocation());
            return;
        } else {
            player.sendMessage(ChatColor.RED + "That game does not exist!");
            return;
        }
    }

    /* Checks for the game and player to create a new game instance and player instance. */
    private void setupPlayerWithGame(String name, Player player) {
        boolean exists = data.gameExists(name);
        Game zag = data.getGame(name, true);
        ZAPlayer zap = data.getZAPlayer(player, name, true);
        if (zag.getMainframe() == null) {
            Location pLoc = player.getLocation();
            zag.setMainframe(new Mainframe(zag, pLoc));
        }
        if (!exists) {
            GameCreateEvent gce = new GameCreateEvent(zag, null, player);
            Bukkit.getServer().getPluginManager().callEvent(gce);
            if (!gce.isCancelled()) {
                zap.loadPlayerToGame(name, true);
            } else {
                zag.remove();
            }
        } else {
            zap.loadPlayerToGame(name, true);
        }
    }
}
