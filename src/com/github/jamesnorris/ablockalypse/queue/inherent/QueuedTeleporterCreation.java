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
import com.github.jamesnorris.ablockalypse.aspect.block.Teleporter;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;
import com.github.jamesnorris.ablockalypse.queue.QueuedPlayerInteractData;

public class QueuedTeleporterCreation extends QueuedPlayerInteractData {
    private final String gameName;
    private Location mLoc;
    private boolean mainframe = false, complete = false;
    private DataContainer data = Ablockalypse.getData();

    public QueuedTeleporterCreation(String playerName, String gameName, boolean mainframe) {
        super(playerName);
        this.gameName = gameName;
        this.mainframe = mainframe;
        Bukkit.getPlayer(playerName).sendMessage(ChatColor.GRAY + "Right click a block to turn it into a " + (mainframe ? "mainframe" : "teleporter") + ".");
    }

    @Override public boolean isCompatible(PlayerInteractEvent event) {
        if (mLoc != null) {
            return event.getPlayer().getName().equals(key) && !data.isZAPlayer(event.getPlayer())
                    && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK);
        }
        return event.getClickedBlock() != null && event.getPlayer().getName().equals(key) && !data.isZAPlayer(event.getPlayer()) && event.getAction() == Action.RIGHT_CLICK_BLOCK;
    }

    @Override public boolean removeAfterRun() {
        return complete;
    }

    @Override public void run() {
        if (!hasImportedPIE()) {
            complete = true;
            return;
        }
        PlayerInteractEvent event = getPIE();
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        event.setUseInteractedBlock(Result.DENY);
        event.setUseItemInHand(Result.DENY);
        Game game = data.getGame(gameName, false);
        if (game == null) {
            complete = true;
            player.sendMessage(ChatColor.RED + "That game does not exist!");
            return;
        }
        if (mainframe && mLoc != null) {
            complete = true;
            mLoc.setPitch(player.getLocation().getPitch());
            mLoc.setYaw(player.getLocation().getYaw());
            Teleporter tele = new Teleporter(game, mLoc);
            game.setMainframe(tele);
            tele.checkForPower();
            player.sendMessage(ChatColor.GRAY + "You have set the mainframe for " + gameName);
            return;
        }
        if (data.isTeleporter(block.getLocation())) {
            complete = true;
            player.sendMessage(ChatColor.RED + "That is already a teleporter!");
            return;
        }
        if (mainframe && mLoc == null) {
            mLoc = block.getLocation();
            player.sendMessage(ChatColor.GRAY + "Now left click the air to set pitch and yaw.");
            return;
        }
        complete = true;
        new Teleporter(game, block.getLocation());
        player.sendMessage(ChatColor.GRAY + "Teleporter created successfully!");
    }
}
