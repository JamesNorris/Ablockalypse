package com.github.jamesnorris.ablockalypse.queue.inherent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.behavior.GameAspect;
import com.github.jamesnorris.ablockalypse.queue.QueuedPlayerInteractData;

public class QueuedGameObjectRemoval extends QueuedPlayerInteractData {
    private DataContainer data = Ablockalypse.getData();

    public QueuedGameObjectRemoval(String playerName) {
        super(playerName);
        Bukkit.getPlayer(playerName).sendMessage(ChatColor.GRAY + "Right click any ZA object to remove it.");
    }

    @Override public boolean isCompatible(PlayerInteractEvent event) {
        return event.getClickedBlock() != null && !data.isZAPlayer(event.getPlayer()) && event.getAction() == Action.RIGHT_CLICK_BLOCK;
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
        GameAspect removal = data.getGameObjectByLocation(block.getLocation());
        if (removal == null) {
            player.sendMessage(ChatColor.GRAY + "Removal: " + ChatColor.RED + "UNSUCCESSFUL");
            return;
        }
        removal.remove();
        player.sendMessage(ChatColor.GRAY + "Removal " + ChatColor.GREEN + "SUCCESSFUL");
    }
}
