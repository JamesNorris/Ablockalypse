package com.github.jamesnorris.enumerated;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.event.bukkit.PlayerInteract;
import com.github.jamesnorris.implementation.Barrier;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.ZAMob;
import com.github.jamesnorris.inter.ZAScheduledTask;
import com.github.jamesnorris.util.MiscUtil;
import com.google.common.collect.Maps;

public enum PowerupType {
    ATOM_BOMB(1) {
        public void play(Game game, Player player, Entity cause, DataContainer data) {
            game.broadcast(ChatColor.GRAY + "ATOM BOMB! - All mobs will now explode.", null);
            for (ZAMob zam : data.mobs)
                if (zam.getGame() == game) {
                    ZASound.EXPLOSION.play(zam.getEntity().getLocation());
                    ZAEffect.FLAMES.play(zam.getEntity().getLocation());
                    zam.kill();
                }
            for (String s2 : game.getPlayers()) {
                Player p = Bukkit.getPlayer(s2);
                ZAPlayer zap = data.getZAPlayer(p, game.getName(), false);
                if (zap != null) {
                    zap.addPoints((Integer) Setting.ATOM_BOMB_POINTS.getSetting());
                }
            }
        }

        @Override public void reverse(Game game, Player player, Entity cause, DataContainer data) {
            // nothing
        }
    },
    CARPENTER(2) {
        public void play(Game game, Player player, Entity cause, DataContainer data) {
            game.broadcast(ChatColor.GRAY + "CARPENTER! - All barriers are being fixed.", null);
            List<Barrier> barriers = game.getObjectsOfType(Barrier.class);
            if (barriers.size() >= 1) {
                for (Barrier b : barriers) {
                    b.replacePanels();
                }
            }
        }

        @Override public void reverse(Game game, Player player, Entity cause, DataContainer data) {
            // nothing
        }
    },
    MAX_AMMO(3) {
        public void play(Game game, Player player, Entity cause, DataContainer data) {
            game.broadcast(ChatColor.GRAY + "MAX AMMO! - All weapons are being fixed.", null);
            for (String s3 : game.getPlayers()) {
                Player p = Bukkit.getPlayer(s3);
                Inventory i = p.getInventory();
                for (ItemStack it : i.getContents()) {
                    if (it != null) {
                        if (MiscUtil.isEnchantableLikeSwords(it)) {
                            it.setDurability((short) 0);
                            ZAEffect.EXTINGUISH.play(p.getLocation());
                        }
                    }
                }
            }
        }

        @Override public void reverse(Game game, Player player, Entity cause, DataContainer data) {
            // nothing
        }
    },
    INSTA_KILL(4) {
        public void play(Game game, Player player, Entity cause, DataContainer data) {
            game.broadcast(ChatColor.GRAY + "INSTA KILL! - It only takes one hit to kill.", null);
            for (String s3 : game.getPlayers()) {
                Player p = Bukkit.getPlayer(s3);
                if (data.playerExists(p)) {
                    final ZAPlayer zap = data.getZAPlayer(p);
                    zap.setInstaKill(true);
                    timedReverse(this, game, player, cause, data, 450);
                }
            }
        }

        @Override public void reverse(Game game, Player player, Entity cause, DataContainer data) {
            for (String s3 : game.getPlayers()) {
                Player p = Bukkit.getPlayer(s3);
                if (data.playerExists(p)) {
                    ZAPlayer zap = data.getZAPlayer(p);
                    zap.setInstaKill(false);
                }
            }
        }
    },
    DOUBLE_POINTS(5) {
        public void play(Game game, Player player, Entity cause, DataContainer data) {
            game.broadcast(ChatColor.GRAY + "DOUBLE POINTS! - You gain 2x the amount of points.", null);
            for (String s3 : game.getPlayers()) {
                Player p = Bukkit.getPlayer(s3);
                if (data.playerExists(p)) {
                    ZAPlayer zap = data.getZAPlayer(p);
                    zap.setPointGainMod(2);
                    timedReverse(this, game, player, cause, data, 450);
                }
            }
        }

        @Override public void reverse(Game game, Player player, Entity cause, DataContainer data) {
            for (String s3 : game.getPlayers()) {
                Player p = Bukkit.getPlayer(s3);
                if (data.playerExists(p)) {
                    ZAPlayer zap = data.getZAPlayer(p);
                    zap.setPointGainMod(1);
                }
            }
        }
    },
    FIRESALE(6) {
        public void play(Game game, Player player, Entity cause, DataContainer data) {
            game.broadcast(ChatColor.GRAY + "FIRESALE! - Weapons only cost 10 points.", null);
            PlayerInteract.fireSale.add(game);
            timedReverse(this, game, player, cause, data, 450);
        }

        @Override public void reverse(Game game, Player player, Entity cause, DataContainer data) {
            PlayerInteract.fireSale.remove(game);
        }
    };
    private final static Map<Integer, PowerupType> BY_ID = Maps.newHashMap();

    public static PowerupType getById(final int id) {
        return BY_ID.get(id);
    }

    private int id;

    PowerupType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    private static void timedReverse(final PowerupType type, final Game game, final Player player, final Entity cause, final DataContainer data, final int delay) {
        Ablockalypse.getMainThread().scheduleDelayedTask(new ZAScheduledTask() {
            @Override public void run() {
                type.reverse(game, player, cause, data);
            }
        }, delay);
    }

    public abstract void play(Game game, Player player, Entity cause, DataContainer data);

    public abstract void reverse(Game game, Player player, Entity cause, DataContainer data);

    static {
        for (PowerupType setting : values()) {
            BY_ID.put(setting.id, setting);
        }
    }
}
