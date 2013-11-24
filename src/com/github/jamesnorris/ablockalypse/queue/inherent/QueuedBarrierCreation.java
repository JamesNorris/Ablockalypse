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
import com.github.jamesnorris.ablockalypse.aspect.block.Barrier;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;
import com.github.jamesnorris.ablockalypse.queue.QueuedPlayerInteractData;

public class QueuedBarrierCreation extends QueuedPlayerInteractData {
    private final String gameName;
    private DataContainer data = Ablockalypse.getData();

    public QueuedBarrierCreation(String playerName, String gameName) {
        super(playerName);
        this.gameName = gameName;
        Bukkit.getPlayer(playerName).sendMessage(ChatColor.GRAY + "Right click the center of a 3x3 wall to make a barrier.");
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
        if (data.isBarrier(block.getLocation())) {
            player.sendMessage(ChatColor.RED + "That is already a barrier!");
            return;
        }
        Game game = data.getGame(gameName, false);
        if (game == null) {
            player.sendMessage(ChatColor.RED + "That game does not exist!");
            return;
        }
        new Barrier(block.getLocation(), game);
        player.sendMessage(ChatColor.GRAY + "Barrier created successfully!");
    }
}
