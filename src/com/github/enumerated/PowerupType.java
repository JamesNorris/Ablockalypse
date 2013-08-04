package com.github.enumerated;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.Barrier;
import com.github.aspect.Game;
import com.github.aspect.ZAPlayer;
import com.github.behavior.ZAMob;
import com.github.behavior.ZAScheduledTask;
import com.github.event.bukkit.PlayerInteract;
import com.github.utility.MiscUtil;
import com.google.common.collect.Maps;

public enum PowerupType {
    ATOM_BOMB(1) {
        @Override public void play(Game game, Player player, Entity cause, DataContainer data) {
            game.broadcast(ChatColor.GRAY + "ATOM BOMB! - All mobs will now explode.");
            for (ZAMob zam : data.getObjectsOfType(ZAMob.class)) {
                if (zam.getGame() == game) {
                    ZASound.EXPLOSION.play(zam.getEntity().getLocation());
                    ZAEffect.FLAMES.play(zam.getEntity().getLocation());
                    zam.kill();
                }
            }
            for (ZAPlayer zap : game.getPlayers()) {
                zap.addPoints((Integer) Setting.ATOM_BOMB_POINTS.getSetting());
            }
        }

        @Override public void reverse(Game game, Player player, Entity cause, DataContainer data) {
            // nothing
        }
    },
    CARPENTER(2) {
        @Override public void play(Game game, Player player, Entity cause, DataContainer data) {
            game.broadcast(ChatColor.GRAY + "CARPENTER! - All barriers are being fixed.");
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
    DOUBLE_POINTS(5) {
        @Override public void play(Game game, Player player, Entity cause, DataContainer data) {
            game.broadcast(ChatColor.GRAY + "DOUBLE POINTS! - You gain 2x the amount of points.");
            for (ZAPlayer zap : game.getPlayers()) {
                zap.setPointGainMod(2);
                timedReverse(this, game, player, cause, data, 450);
            }
        }

        @Override public void reverse(Game game, Player player, Entity cause, DataContainer data) {
            for (ZAPlayer zap : game.getPlayers()) {
                zap.setPointGainMod(1);
            }
        }
    },
    FIRESALE(6) {
        @Override public void play(Game game, Player player, Entity cause, DataContainer data) {
            game.broadcast(ChatColor.GRAY + "FIRESALE! - Weapons only cost 10 points.");
            PlayerInteract.fireSale.add(game);
            timedReverse(this, game, player, cause, data, 450);
        }

        @Override public void reverse(Game game, Player player, Entity cause, DataContainer data) {
            PlayerInteract.fireSale.remove(game);
        }
    },
    INSTA_KILL(4) {
        @Override public void play(Game game, Player player, Entity cause, DataContainer data) {
            game.broadcast(ChatColor.GRAY + "INSTA KILL! - It only takes one hit to kill.");
            for (ZAPlayer zap : game.getPlayers()) {
                zap.setInstaKill(true);
                timedReverse(this, game, player, cause, data, 450);
            }
        }

        @Override public void reverse(Game game, Player player, Entity cause, DataContainer data) {
            for (ZAPlayer zap : game.getPlayers()) {
                zap.setInstaKill(false);
            }
        }
    },
    MAX_AMMO(3) {
        @Override public void play(Game game, Player player, Entity cause, DataContainer data) {
            game.broadcast(ChatColor.GRAY + "MAX AMMO! - All weapons are being fixed.");
            for (ZAPlayer zap : game.getPlayers()) {
                Player p = zap.getPlayer();
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
    };
    private final static Map<Integer, PowerupType> BY_ID = Maps.newHashMap();
    static {
        for (PowerupType setting : values()) {
            BY_ID.put(setting.id, setting);
        }
    }

    public static PowerupType getById(final int id) {
        return BY_ID.get(id);
    }

    private static void timedReverse(final PowerupType type, final Game game, final Player player, final Entity cause, final DataContainer data, final int delay) {
        Ablockalypse.getMainThread().scheduleDelayedTask(new ZAScheduledTask() {
            @Override public void run() {
                type.reverse(game, player, cause, data);
            }
        }, delay);
    }

    private int id;

    PowerupType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public abstract void play(Game game, Player player, Entity cause, DataContainer data);

    public abstract void reverse(Game game, Player player, Entity cause, DataContainer data);
}
