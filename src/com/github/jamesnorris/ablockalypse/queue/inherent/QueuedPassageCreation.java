package com.github.jamesnorris.ablockalypse.queue.inherent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.block.Passage;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;
import com.github.jamesnorris.ablockalypse.queue.QueuedPlayerInteractData;

public class QueuedPassageCreation extends QueuedPlayerInteractData {
    private final String gameName;
    private DataContainer data = Ablockalypse.getData();
    private Location loc1 = null;
    private boolean shouldRemove = false;

    public QueuedPassageCreation(String playerName, String gameName) {
        super(playerName);
        this.gameName = gameName;
        Bukkit.getPlayer(playerName).sendMessage(ChatColor.GRAY + "Right click a block to select point " + ChatColor.RED + "1" + ChatColor.GRAY + ".");
    }

    @Override public boolean isCompatible(PlayerInteractEvent event) {
        return event.getClickedBlock() != null && event.getPlayer().getName().equals(key) && !data.isZAPlayer(event.getPlayer()) && event.getAction() == Action.RIGHT_CLICK_BLOCK;
    }

    @Override public boolean removeAfterRun() {
        return shouldRemove;
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
        if (data.isPassage(block.getLocation())) {
            player.sendMessage(ChatColor.RED + "That is already a passage!");
            return;
        }
        Game game = data.getGame(gameName, false);
        if (game == null) {
            player.sendMessage(ChatColor.RED + "That game does not exist!");
            return;
        }
        if (loc1 == null) {
            loc1 = block.getLocation();
            player.sendMessage(ChatColor.GRAY + "Right click a block to select point " + ChatColor.RED + "2" + ChatColor.GRAY + ".");
        } else {
            shouldRemove = true;
            new Passage(game, loc1, block.getLocation());
            player.sendMessage(ChatColor.GRAY + "Passage created!");
        }
    }
}
