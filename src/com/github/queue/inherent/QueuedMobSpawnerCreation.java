package com.github.queue.inherent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.block.MobSpawner;
import com.github.aspect.intelligent.Game;
import com.github.queue.QueuedPlayerInteractData;

public class QueuedMobSpawnerCreation extends QueuedPlayerInteractData {
    private final String gameName;
    private DataContainer data = Ablockalypse.getData();

    public QueuedMobSpawnerCreation(String playerName, String gameName) {
        super(playerName);
        this.gameName = gameName;
        Bukkit.getPlayer(playerName).sendMessage(ChatColor.GRAY + "Right click a block to create a spawner.");
    }

    @Override public boolean isCompatible(PlayerInteractEvent event) {
        return event.getClickedBlock() != null && event.getPlayer().getName().equals(key) && !data.isZAPlayer(event.getPlayer()) && event.getAction() == Action.RIGHT_CLICK_BLOCK;
    }

    @Override public void run() {
        if (!hasImportedPIE()) {
            return;
        }
        PlayerInteractEvent event = getPIE();
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        event.setUseInteractedBlock(Result.DENY);
        event.setUseItemInHand(Result.DENY);
        if (data.isMobSpawner(block.getLocation()) && data.getMobSpawner(block.getLocation()).getGame().getName().equals(gameName)) {
            player.sendMessage(ChatColor.RED + "That is already a mob spawner for this game!");
            return;
        }
        Game game = data.getGame(gameName, false);
        if (game == null) {
            player.sendMessage(ChatColor.RED + "That game does not exist!");
            return;
        }
        game.addObject(new MobSpawner(block.getLocation(), game));
        player.sendMessage(ChatColor.GRAY + "Spawner created successfully!");
    }
}
