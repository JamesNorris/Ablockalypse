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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.Ablockalypse;
import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Enumerated.Local;
import com.github.JamesNorris.Enumerated.Setting;
import com.github.JamesNorris.Enumerated.ZAEffect;
import com.github.JamesNorris.Enumerated.ZAEnchantment;
import com.github.JamesNorris.Enumerated.ZAPerk;
import com.github.JamesNorris.Enumerated.ZAWeapon;
import com.github.JamesNorris.Event.GameCreateEvent;
import com.github.JamesNorris.Event.GameSignClickEvent;
import com.github.JamesNorris.Implementation.GameArea;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Implementation.GameMobSpawner;
import com.github.JamesNorris.Implementation.GameMysteryChest;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Interface.GameObject;
import com.github.JamesNorris.Interface.MysteryChest;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Manager.ItemManager;
import com.github.JamesNorris.Threading.TeleportThread;
import com.github.JamesNorris.Util.EffectUtil;
import com.github.JamesNorris.Util.MathAssist;
import com.github.JamesNorris.Util.MiscUtil;

public class PlayerInteract extends DataManipulator implements Listener {
    public static HashMap<String, ZAGameBase> areaPlayers = new HashMap<String, ZAGameBase>();
    public static HashMap<String, ZAGameBase> barrierPlayers = new HashMap<String, ZAGameBase>();
    public static HashMap<String, ZAGameBase> chestPlayers = new HashMap<String, ZAGameBase>();
    public static HashMap<String, Location> locClickers = new HashMap<String, Location>();
    public static ArrayList<String> removers = new ArrayList<String>();
    public static HashMap<String, ZAGameBase> spawnerPlayers = new HashMap<String, ZAGameBase>();
    private ItemManager im;

    public PlayerInteract() {
        im = new ItemManager();
    }

    private void sendPlayerMessage(Player player, String message) {
        MiscUtil.sendPlayerMessage(player, message);
    }

    private void buyArea(Sign sign, Player player, ZAPlayerBase zap, String l3, int points) {
        int cost = 1500;
        try {
            cost = Integer.parseInt(l3);
        } catch (Exception e) {
            Location l = sign.getLocation();
            Ablockalypse.crash("The sign at " + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ() + " is incorrectly formatted!", false);
        }
        sign.setLine(3, " " + cost + " ");
        if (zap.getPoints() >= cost) {
            GameArea a = getClosestArea(sign.getBlock(), (ZAGameBase) zap.getGame());
            if (a != null) {
                if (!a.isOpened()) {
                    a.open();
                    EffectUtil.generateEffect(player, sign.getLocation(), ZAEffect.POTION_BREAK);
                    zap.subtractPoints(cost);
                    sendPlayerMessage(player, ChatColor.BOLD + "You have bought an area for " + cost + " points.");
                    return;
                } else
                    sendPlayerMessage(player, ChatColor.RED + "This area has already been purchased!");
            } else
                sendPlayerMessage(player, ChatColor.RED + "There is no area close to this sign!");
            return;
        } else {
            sendPlayerMessage(player, ChatColor.RED + "You have " + zap.getPoints() + " / " + cost + " points to buy this.");
            return;
        }
    }

    /*
     * Gets the closest game area to the given block.
     */
    private GameArea getClosestArea(Block b, ZAGameBase zag) {
        int distance = Integer.MAX_VALUE;
        Location loc = b.getLocation();
        GameArea lp = null;
        for (GameArea a : data.areas)
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

    private void giveEnchantment(Sign sign, Player player, ZAPlayerBase zap, String l3, int points) {
        for (ZAEnchantment ench : ZAEnchantment.values()) {
            if (l3.equalsIgnoreCase(ench.getLabel())) {
                if (zap.getPoints() < ench.getCost()) {
                    sendPlayerMessage(player, ChatColor.RED + "You have " + points + " / " + ench.getCost() + " points to buy this.");
                    return;
                }
                ItemStack hand = player.getItemInHand();
                player.getInventory().remove(hand);
                im.addEnchantment(hand, ench.getEnchantment(), 3);
                zap.subtractPoints(ench.getCost());
                sendPlayerMessage(player, ChatColor.BOLD + "You have bought " + l3 + " for " + ench.getCost() + " points!");
                EffectUtil.generateEffect(player, sign.getLocation(), ZAEffect.POTION_BREAK);
                MiscUtil.dropItemAtPlayer(sign.getLocation(), hand, player);
                return;
            }
        }
    }

    private void givePerk(Sign sign, Player player, ZAPlayerBase zap, String l3, int points) {
        for (ZAPerk perk : ZAPerk.values()) {
            if (l3.equalsIgnoreCase(perk.getLabel()) && zap.getGame().getLevel() >= perk.getLevel()) {
                if (zap.getPoints() < perk.getCost()) {
                    sendPlayerMessage(player, ChatColor.RED + "You have " + points + " / " + perk.getCost() + " points to buy this.");
                    return;
                }
                zap.addPerk(perk, perk.getDuration(), 1);
                zap.subtractPoints(perk.getCost());
                sendPlayerMessage(player, ChatColor.BOLD + "You have bought " + l3 + " for " + perk.getCost() + " points!");
                EffectUtil.generateEffect(player, sign.getLocation(), ZAEffect.POTION_BREAK);
                return;
            }
        }
    }

    private void giveWeapon(Sign sign, Player player, ZAPlayerBase zap, String l3, int points) {
        for (ZAWeapon wep : ZAWeapon.values()) {
            if (l3.equalsIgnoreCase(wep.getLabel()) && zap.getPoints() >= wep.getCost() && zap.getGame().getLevel() >= wep.getLevel()) {
                if (zap.getPoints() < wep.getCost()) {
                    sendPlayerMessage(player, ChatColor.RED + "You have " + points + " / " + wep.getCost() + " points to buy this.");
                    return;
                }
                Material type = wep.getMaterial();
                if (type != Material.ENDER_PEARL)
                    MiscUtil.dropItemAtPlayer(sign.getLocation(), new ItemStack(type, 1), player);
                else
                    MiscUtil.dropItemAtPlayer(sign.getLocation(), new ItemStack(type, 5), player);
                zap.subtractPoints(wep.getCost());
                sendPlayerMessage(player, ChatColor.BOLD + "You have bought " + l3 + " for " + wep.getCost() + " points!");
                EffectUtil.generateEffect(player, sign.getLocation(), ZAEffect.POTION_BREAK);
                return;
            }
        }
    }

    private void joinGame(Sign sign, Player player, String l3) {
        if (player.hasPermission("za.create") && !data.games.containsKey(l3)) {
            setupPlayerWithGame(l3, player);
            sendPlayerMessage(player, ChatColor.RED + "This game does not have any barriers. Ignoring...");
            return;
        } else if (data.games.containsKey(l3)) {
            setupPlayerWithGame(l3, player);
            EffectUtil.generateEffect(player, sign.getLocation(), ZAEffect.POTION_BREAK);
            return;
        } else {
            sendPlayerMessage(player, ChatColor.RED + "That game does not exist!");
            return;
        }
    }

    public boolean isArea(Block block) {
        for (GameArea area : data.areas) {
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
        if (b != null)
            if ((!data.playerExists(p) && barrierPlayers.containsKey(p.getName()) && b.getType() == Material.FENCE) && a == Action.RIGHT_CLICK_BLOCK) {
                if (data.barriers.containsKey(b.getLocation())) {
                    p.sendMessage(ChatColor.RED + "That is already a barrier!");
                    return;
                }
                new GameBarrier(b, barrierPlayers.get(p.getName()));
                p.sendMessage(ChatColor.GRAY + "Barrier created successfully!");
                barrierPlayers.remove(p.getName());
            } else if ((!data.playerExists(p) && spawnerPlayers.containsKey(p.getName())) && a == Action.RIGHT_CLICK_BLOCK) {
                if (data.spawns.containsValue(b.getLocation()) && data.spawns.containsKey(spawnerPlayers.get(p.getName()))) {
                    p.sendMessage(ChatColor.RED + "That is already a mob spawner!");
                    return;
                }
                spawnerPlayers.get(p.getName()).addMobSpawner(new GameMobSpawner(b.getLocation(), spawnerPlayers.get(p.getName())));
                p.sendMessage(ChatColor.GRAY + "Spawner created successfully!");
                spawnerPlayers.remove(p.getName());
            } else if ((!data.playerExists(p) && chestPlayers.containsKey(p.getName())) && a == Action.RIGHT_CLICK_BLOCK && b.getType() == Material.CHEST) {
                event.setUseInteractedBlock(Result.DENY);
                ZAGameBase zag = chestPlayers.get(p.getName());
                if (!data.isMysteryChest(b.getLocation())) {
                    chestPlayers.get(p.getName()).addMysteryChest(new GameMysteryChest(b.getState(), zag, b.getLocation(), zag.getActiveMysteryChest() == null));
                    p.sendMessage(ChatColor.GRAY + "Mystery chest created successfully!");
                } else
                    p.sendMessage(ChatColor.RED + "That is already a mystery chest!");
                chestPlayers.remove(p.getName());
            } else if (!data.playerExists(p) && removers.contains(p.getName()) && a == Action.RIGHT_CLICK_BLOCK) {
                event.setUseInteractedBlock(Result.DENY);
                GameObject removal = null;
                for (GameObject o : data.objects) {
                    for (Block block : o.getDefiningBlocks()) {
                        if (block != null && block.getLocation().distance(b.getLocation()) <= 1) {
                            removal = o;
                            break;
                        }
                    }
                }
                if (removal != null) {
                    removal.remove();
                    p.sendMessage(ChatColor.GRAY + "Removal " + ChatColor.GREEN + "sucessful");
                } else
                    p.sendMessage(ChatColor.GRAY + "Removal " + ChatColor.RED + "unsuccessful");
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
                    new GameArea(areaPlayers.get(p.getName()), b.getLocation(), locClickers.get(p.getName()));
                    locClickers.remove(p.getName());
                    areaPlayers.remove(p.getName());
                    p.sendMessage(ChatColor.GRAY + "Area created!");
                }
            } else if ((b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN) && a == Action.RIGHT_CLICK_BLOCK) {
                Sign s = (Sign) b.getState();
                if (s.getLine(0).equalsIgnoreCase(Local.BASESTRING.getSetting())) {
                    event.setUseInteractedBlock(Result.DENY);
                    runLines(s, p);
                    return;
                }
                return;
            } else if (data.players.containsKey(p)) {
                event.setUseInteractedBlock(Result.DENY);
                ZAPlayerBase zap = data.players.get(p);
                if (b.getType() == Material.ENDER_PORTAL_FRAME && a == Action.RIGHT_CLICK_BLOCK) {
                    if (!zap.isTeleporting()) {
                        p.sendMessage(ChatColor.GRAY + "Teleportation sequence started...");
                        new TeleportThread(zap, (Integer) Setting.TELEPORTTIME.getSetting(), true, 20);
                        return;
                    } else {
                        p.sendMessage(ChatColor.GRAY + "You are already teleporting!");
                        return;
                    }
                } else if (b.getType() == Material.CHEST && a == Action.RIGHT_CLICK_BLOCK) {
                    Location l = b.getLocation();
                    if (data.isMysteryChest(l)) {
                        MysteryChest mc = data.getMysteryChest(l);
                        if (mc != null && mc.isActive()) {
                            if (zap.getPoints() >= (Integer) Setting.CHESTCOST.getSetting()) {
                                mc.giveRandomItem(p);
                                zap.subtractPoints((Integer) Setting.CHESTCOST.getSetting());
                                return;
                            } else {
                                p.sendMessage(ChatColor.RED + "You have " + zap.getPoints() + " / " + (Integer) Setting.CHESTCOST.getSetting() + " points to buy this.");
                                event.setCancelled(true);
                                return;
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "That chest is not active!");
                            event.setCancelled(true);
                            return;
                        }
                    }
                } else if (b.getType() == Material.WOOD_DOOR || b.getType() == Material.IRON_DOOR)
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
                                zap.addPoints((Integer) Setting.KILLPOINTINCREASE.getSetting());
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

    /**
     * Checks the lines of the sign for strings in the config, that enable changes to be made to the player.
     * 
     * @param player The player to affect if the lines are run through
     */
    @SuppressWarnings("deprecation") public void runLines(Sign sign, Player player) {
        String l1 = sign.getLine(0);
        String l2 = sign.getLine(1);
        String l3 = sign.getLine(2);
        // String l4 = sign.getLine(3);//UNUSED
        if (l1.equalsIgnoreCase(Local.BASESTRING.getSetting())) {
            GameSignClickEvent gsce = new GameSignClickEvent(sign, player);
            Bukkit.getPluginManager().callEvent(gsce);
            if (!gsce.isCancelled()) {
                if (l2.equalsIgnoreCase(Local.BASEJOINSTRING.getSetting()) && !data.players.containsKey(player))// JOIN
                    joinGame(sign, player, l3);
                else if (data.players.containsKey(player)) {
                    ZAPlayerBase zap = data.players.get(player);
                    int points = zap.getPoints();
                    if (l2.equalsIgnoreCase(Local.BASEPERKSTRING.getSetting()))// PERK
                        givePerk(sign, player, zap, l3, points);
                    else if (l2.equalsIgnoreCase(Local.BASEENCHANTMENTSTRING.getSetting()) && MiscUtil.isSword(player.getItemInHand()))// ENCHANTMENT
                        giveEnchantment(sign, player, zap, l3, points);
                    else if (l2.equalsIgnoreCase(Local.BASEWEAPONSTRING.getSetting()))// WEAPON
                        giveWeapon(sign, player, zap, l3, points);
                    else if (l2.equalsIgnoreCase(Local.BASEAREASTRING.getSetting()))// AREA
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
        ZAGame zag = data.findGame(name);
        if (zag.getMainframe() == null)
            zag.setMainframe(player.getLocation());
        ZAPlayer zap = data.findZAPlayer(player, name);
        GameCreateEvent gce = new GameCreateEvent(zag, null, player);
        Bukkit.getServer().getPluginManager().callEvent(gce);
        if (!gce.isCancelled())
            zap.loadPlayerToGame(name);
        else
            zag.remove();
    }
}
