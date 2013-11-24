package com.github.jamesnorris.ablockalypse.queue.inherent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.block.MysteryBox;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;
import com.github.jamesnorris.ablockalypse.queue.QueuedPlayerInteractData;

public class QueuedMysteryBoxCreation extends QueuedPlayerInteractData {
    private final String gameName;
    private DataContainer data = Ablockalypse.getData();

    public QueuedMysteryBoxCreation(String playerName, String gameName) {
        super(playerName);
        this.gameName = gameName;
        Bukkit.getPlayer(playerName).sendMessage(ChatColor.GRAY + "Right click a chest to turn it into a mystery box.");
    }

    @Override public boolean isCompatible(PlayerInteractEvent event) {
        return event.getClickedBlock() != null && !data.isZAPlayer(event.getPlayer()) && event.getAction() == Action.RIGHT_CLICK_BLOCK
                && event.getClickedBlock().getType() == Material.CHEST;
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
        Game game = data.getGame(gameName, false);
        if (game == null) {
            player.sendMessage(ChatColor.RED + "That game does not exist!");
            return;
        }
        if (data.isMysteryChest(block.getLocation())) {
            player.sendMessage(ChatColor.RED + "That is already a mystery box!");
            return;
        }
        game.addObject(new MysteryBox(game, block.getLocation(), game.getActiveMysteryChest() == null));
        player.sendMessage(ChatColor.GRAY + "Mystery box created successfully!");
    }
}
