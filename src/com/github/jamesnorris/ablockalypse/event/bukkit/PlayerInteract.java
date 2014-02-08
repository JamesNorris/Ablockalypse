package com.github.jamesnorris.ablockalypse.event.bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Lever;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.Game;
import com.github.jamesnorris.ablockalypse.aspect.MapData;
import com.github.jamesnorris.ablockalypse.aspect.MysteryBox;
import com.github.jamesnorris.ablockalypse.aspect.Passage;
import com.github.jamesnorris.ablockalypse.aspect.PowerSwitch;
import com.github.jamesnorris.ablockalypse.aspect.Teleporter;
import com.github.jamesnorris.ablockalypse.aspect.ZAMob;
import com.github.jamesnorris.ablockalypse.aspect.ZAPlayer;
import com.github.jamesnorris.ablockalypse.enumerated.Local;
import com.github.jamesnorris.ablockalypse.enumerated.Setting;
import com.github.jamesnorris.ablockalypse.enumerated.ZAEffect;
import com.github.jamesnorris.ablockalypse.enumerated.ZAEnchantment;
import com.github.jamesnorris.ablockalypse.enumerated.ZAPerk;
import com.github.jamesnorris.ablockalypse.event.GameCreateEvent;
import com.github.jamesnorris.ablockalypse.event.GameSignClickEvent;
import com.github.jamesnorris.ablockalypse.queue.QueuedPlayerInteractData;
import com.github.jamesnorris.ablockalypse.threading.inherent.TeleportTask;
import com.github.jamesnorris.ablockalypse.threading.inherent.TeleporterLinkageTimerTask;
import com.github.jamesnorris.ablockalypse.utility.AblockalypseUtility;
import com.github.jamesnorris.ablockalypse.utility.BukkitUtility;
import com.github.jamesnorris.ablockalypse.utility.BuyableItemData;
import com.github.jamesnorris.ablockalypse.utility.Cube;
import com.github.jamesnorris.ablockalypse.utility.Cuboid;
import com.github.jamesnorris.ablockalypse.utility.HitThroughWallShot;
import com.github.jamesnorris.mcshot.Hit;
import com.github.jamesnorris.mcshot.HitBox;
import com.github.jamesnorris.mcshot.Shot;
import com.github.jamesnorris.mcshot.type.EntityHitBox;

public class PlayerInteract implements Listener {
    public static ArrayList<Game> fireSale = new ArrayList<Game>();// TODO queue all of these remaining lists/maps
    public static HashMap<ZAPlayer, Teleporter> mainframeLinkers = new HashMap<ZAPlayer, Teleporter>();
    public static HashMap<ZAPlayer, TeleporterLinkageTimerTask> mainframeLinkers_Timers = new HashMap<ZAPlayer, TeleporterLinkageTimerTask>();
    public static HashMap<String, Game> passagePlayers = new HashMap<String, Game>();
    public static HashMap<String, String> mapDataSavePlayers = new HashMap<String, String>();
    private static HashMap<String, Location> mapDataPoint1SaveClickers = new HashMap<String, Location>();
    public static HashMap<String, String> mapDataLoadPlayers = new HashMap<String, String>();
    private DataContainer data = Ablockalypse.getData();
    public static BlockingQueue<QueuedPlayerInteractData> queue = new LinkedBlockingQueue<QueuedPlayerInteractData>();

    public boolean isPassage(Block block) {
        for (Passage passage : data.getObjectsOfType(Passage.class)) {
            if (passage.getDefiningBlocks().contains(block)) {
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
        for (QueuedPlayerInteractData data : queue) {
            if ((data.getKey().equals(event.getPlayer().getName()) || data.getKey().equals(QueuedPlayerInteractData.ANY_PLAYER)) && data.isCompatible(event)) {
                data.importPIE(event);
                data.run();
                if (data.removeAfterRun()) {
                    queue.remove(data);
                }
                return;
            }
        }
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (block != null) {
            Location loc = block.getLocation();
            if (!data.isZAPlayer(player) && mapDataSavePlayers.containsKey(player.getName()) && action == Action.RIGHT_CLICK_BLOCK) {
                if (!mapDataPoint1SaveClickers.containsKey(player.getName())) {
                    mapDataPoint1SaveClickers.put(player.getName(), block.getLocation());
                    player.sendMessage(ChatColor.GRAY + "Please click the other corner of the map.");
                    return;
                } else {
                    boolean saved = new MapData(mapDataSavePlayers.get(player.getName())).save(new Cuboid(mapDataPoint1SaveClickers.get(player.getName()), block.getLocation()));
                    mapDataPoint1SaveClickers.remove(player.getName());
                    mapDataSavePlayers.remove(player.getName());
                    String successful = saved ? ChatColor.GREEN + "successfully" + ChatColor.RESET : ChatColor.RED + "unsuccessfully" + ChatColor.RESET;
                    player.sendMessage(ChatColor.GRAY + "Mapdata saved " + successful + ChatColor.GRAY + ".");
                }
            } else if (!data.isZAPlayer(player) && mapDataLoadPlayers.containsKey(player.getName()) && action == Action.RIGHT_CLICK_BLOCK) {
                boolean loaded = MapData.getFromGame(mapDataLoadPlayers.get(player.getName())).load(block.getLocation());
                mapDataLoadPlayers.remove(player.getName());
                String successful = loaded ? ChatColor.GREEN + "successfully" + ChatColor.RESET : ChatColor.RED + "unsuccessfully" + ChatColor.RESET;
                player.sendMessage(ChatColor.GRAY + "Mapdata loaded " + successful + ".");
            } else if (!data.isZAPlayer(player) && data.isGameObject(loc)) {
                event.setCancelled(true);
            } else if ((block.getType() == Material.SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) && action == Action.RIGHT_CLICK_BLOCK) {
                Sign s = (Sign) block.getState();
                if (s.getLine(0).equalsIgnoreCase(Local.BASE_STRING.getSetting())) {
                    event.setUseInteractedBlock(Result.DENY);
                    runLines(s, player);
                    return;
                }
                return;
            } else if (data.isZAPlayer(player)) {
                if (!BlockPlace.shouldBePlaced(player.getItemInHand().getType())) {
                    event.setUseItemInHand(Result.DENY);
                }
                ZAPlayer zap = data.getZAPlayer(player);
                Game game = zap.getGame();
                if (block.getType() == Material.CHEST && action == Action.RIGHT_CLICK_BLOCK) {
                    event.setUseInteractedBlock(Result.DENY);
                    Location l = block.getLocation();
                    if (data.isMysteryChest(l)) {
                        MysteryBox mc = data.getMysteryChest(l);
                        if (mc != null && mc.isActive()) {
                            mc.openToRandomItem(zap);
                        } else {
                            player.sendMessage(ChatColor.RED + "That chest is not active!");
                            event.setCancelled(true);
                            return;
                        }
                    }
                } else if (block.getType() == Material.WOOD_DOOR || block.getType() == Material.IRON_DOOR) {
                    event.setCancelled(true);
                } else if (block.getType() == Material.LEVER && action == Action.RIGHT_CLICK_BLOCK) {
                    Lever lever = (Lever) block.getState().getData();
                    if (lever.isPowered()) {
                        player.sendMessage(lever.isPowered() ? ChatColor.GRAY + "The switch is on." : ChatColor.RED + "That switch is in use by another game!");
                        event.setUseInteractedBlock(Result.DENY);
                        event.setUseItemInHand(Result.DENY);
                        event.setCancelled(true);
                        return;
                    }
                    new PowerSwitch(game, loc, lever);
                } else if (!block.getType().isOccluding() && (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)) {
                    // through-fence damage
                    ItemStack handItem = player.getItemInHand();
                    short damage = handItem == null ? 1/* hand damage */
                    : handItem.getDurability();
                    HitThroughWallShot shotData = new HitThroughWallShot(damage);
                    Shot shot = new Shot(player.getEyeLocation(), shotData);
                    List<Hit> results = shot.shoot(shot.arrangeClosest(data.getObjectsOfType(HitBox.class)));
                    if (results.isEmpty()) {
                        return;
                    }
                    Hit closestHit = results.get(0);
                    if (closestHit == null || !(closestHit.getBoxHit() instanceof EntityHitBox)) {
                        return;
                    }
                    Entity hitBoxEntity = ((EntityHitBox) closestHit.getBoxHit()).getEntity();
                    if (!(hitBoxEntity instanceof LivingEntity)) {
                        return;
                    }
                    ZAMob zam = data.getZAMob((LivingEntity) hitBoxEntity);
                    if (!zam.getGame().getUUID().equals(zap.getGame().getUUID())) {
                        return;
                    }
                    zam.getEntity().damage(shotData.getDamage(0), player);
                    return;
                } else if (action == Action.RIGHT_CLICK_BLOCK) {
                    if (block.getType() == Material.CHEST) {
                        event.setUseInteractedBlock(Result.DENY);
                    }
                    if (BukkitUtility.locationMatch(block.getLocation(), game.getMainframe().getLocation(), 2)) {
                        // mainframe
                        if (PlayerInteract.mainframeLinkers.containsKey(zap)) {
                            TeleporterLinkageTimerTask tltt = PlayerInteract.mainframeLinkers_Timers.get(zap);
                            if (tltt.canBeLinked()) {
                                player.sendMessage(ChatColor.GREEN + "Teleporter linked!");
                                tltt.setLinked(true);
                                PlayerInteract.mainframeLinkers.get(zap).setLinked(true);
                            }
                            PlayerInteract.mainframeLinkers.remove(zap);
                            PlayerInteract.mainframeLinkers_Timers.remove(zap);
                        }
                    } else if (data.isTeleporter(block.getLocation())) {
                        Teleporter tele = data.getTeleporter(block.getLocation());
                        if (!tele.isPowered() && (Boolean) Setting.TELEPORTERS_REQUIRE_POWER.getSetting()) {
                            return;
                        }
                        // not mainframe
                        double distanceSquared = Double.MAX_VALUE;
                        double time = -1;
                        boolean requiresLinkage = true;
                        Cube cube = new Cube(block.getLocation(), 2);
                        for (Location sqLoc : cube.getLocations()) {
                            if (sqLoc.getBlock().getState() instanceof Sign) {
                                Sign sign = (Sign) sqLoc.getBlock().getState();
                                String[] lines = sign.getLines();
                                if (lines[0].equalsIgnoreCase(Local.BASE_STRING.getSetting()) && lines[1].equalsIgnoreCase(Local.BASE_TELEPORTER_SETTINGS_STRING.getSetting())) {
                                    try {
                                        if (sqLoc.distanceSquared(block.getLocation()) < distanceSquared) {
                                            requiresLinkage = Boolean.parseBoolean(lines[2]);
                                            time = Double.parseDouble(lines[3]);
                                            distanceSquared = sqLoc.distanceSquared(block.getLocation());
                                        }
                                    } catch (Exception e) {
                                        // nothing
                                    }
                                }
                            }
                        }
                        tele.refresh();
                        if (data.getTeleporter(block.getLocation()).isLinked() || !requiresLinkage) {
                            // this teleporter is linked to the mainframe...
                            if (!zap.isTeleporting()) {
                                player.sendMessage(ChatColor.GRAY + "Teleportation sequence started...");
                                new TeleportTask(zap, (Integer) Setting.TELEPORT_TIME.getSetting(), true);
                                return;
                            } else {
                                player.sendMessage(ChatColor.GRAY + "You are already teleporting!");
                                return;
                            }
                        } else {
                            // this teleporter is not linked to the mainframe...
                            time = time == -1 ? (int) (loc.distanceSquared(game.getMainframe().getLocation()) * .1) : time;// 1 second per block difference (4.5 approx sqrt 20)
                            tele.setLinkTime(time);
                            PlayerInteract.mainframeLinkers.put(zap, tele);
                            PlayerInteract.mainframeLinkers_Timers.put(zap, new TeleporterLinkageTimerTask(game.getMainframe(), zap, (int) time * 20, true)); // difference
                            player.sendMessage(ChatColor.GRAY + "You now have " + time + " seconds to link the teleporter to the mainframe!");
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
                    } else if (l2.equalsIgnoreCase(Local.BASE_ENCHANTMENT_STRING.getSetting()) && BukkitUtility.isEnchantableLikeSwords(player.getItemInHand())) {
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
            Ablockalypse.getTracker().error("The sign at " + sign.getLocation().toString() + " does not have a cost value on line 4.", 0);
            player.sendMessage(ChatColor.RED + "That sign is incorrectly formatted.\nThe server has already been alerted.");
            return;
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
                AblockalypseUtility.dropItemAtPlayer(sign.getLocation(), hand, player, 1, 1);
                return;
            }
        }
    }

    private void giveItem(Sign sign, Player player, ZAPlayer zap, String l3, int points) {
        Map<Integer, BuyableItemData> items = Ablockalypse.getExternal().getItemFileManager().getSignItemMap();
        for (int id : items.keySet()) {
            BuyableItemData map = items.get(id);
            int cost = sign.getLine(3).isEmpty() ? map.getCost() : Integer.parseInt(sign.getLine(3));
            if (l3.equalsIgnoreCase(map.getName()) && zap.getGame().getLevel() >= map.getRequiredLevel()) {
                if (points < cost) {
                    player.sendMessage(ChatColor.RED + "You have " + points + " / " + cost + " points to buy this.");
                    return;
                }
                AblockalypseUtility.dropItemAtPlayer(sign.getLocation(), map.toItemStack(), player, 1, 1);
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
            zag.setMainframe(new Teleporter(zag, pLoc));
        }
        if (!exists) {
            GameCreateEvent gce = new GameCreateEvent(zag, null, player);
            Bukkit.getServer().getPluginManager().callEvent(gce);
            if (!gce.isCancelled()) {
                zap.loadPlayerToGame(name, true);
            } else {
                zag.remove(true);
            }
        } else {
            zap.loadPlayerToGame(name, true);
        }
    }
}
