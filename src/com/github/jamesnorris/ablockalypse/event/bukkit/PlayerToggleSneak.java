package com.github.jamesnorris.ablockalypse.event.bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.Barrier;
import com.github.jamesnorris.ablockalypse.aspect.Claymore;
import com.github.jamesnorris.ablockalypse.aspect.Grenade;
import com.github.jamesnorris.ablockalypse.aspect.MysteryBox;
import com.github.jamesnorris.ablockalypse.aspect.Teleporter;
import com.github.jamesnorris.ablockalypse.aspect.ZAPlayer;
import com.github.jamesnorris.ablockalypse.threading.inherent.LastStandPickupTask;
import com.github.jamesnorris.ablockalypse.threading.inherent.TeleportTask;

public class PlayerToggleSneak implements Listener {
    private DataContainer data = Ablockalypse.getData();

    /* Called when a player changes from walking to sneaking.
     * Used mostly for triggering shift interact objects. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PTSE(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        if (data.isZAPlayer(player)) {
            ZAPlayer zap = data.getZAPlayer(player);
            {
                Grenade closestGrenade = data.getClosest(Grenade.class, loc, 2, 3.5, 2);
                if (closestGrenade != null && closestGrenade.getGrenadeEntity() != null) {
                    zap.giveItem(new ItemStack(Material.ENDER_PEARL, 1));
                    closestGrenade.setLive(false);
                    closestGrenade.remove();
                    return;
                }
                ZAPlayer closestPlayer = data.getClosest(ZAPlayer.class, loc, 2, 3.5, 2);
                if (closestPlayer != null && closestPlayer.isInLastStand()) {
                    new LastStandPickupTask(zap, closestPlayer, 20, 5, true);// can only be done once for each downed player
                    return;
                }
                Teleporter closestTeleporter = data.getClosest(Teleporter.class, loc, 1, 2.5, 1);
                if (closestTeleporter != null && closestTeleporter.isPowered() && closestTeleporter.isLinked() && !zap.isTeleporting()) {
                    closestTeleporter.playEffects(Teleporter.TELEPORT_EFFECTS);
                    new TeleportTask(zap, 5, true);// only alllows one teleport thread per player
                    return;
                }
                Claymore closestClaymore = data.getClosest(Claymore.class, loc, 2, 2, 2);
                if (closestClaymore != null) {
                    Ablockalypse.getExternal().getItemFileManager().giveItem(player, new ItemStack(Material.FLOWER_POT_ITEM, 1));
                    closestClaymore.remove();
                    return;
                }
                Barrier closestBarrier = data.getClosest(Barrier.class, loc, 2, 3, 2);
                if (closestBarrier != null && closestBarrier.getHP() < 5) {
                    closestBarrier.fixBarrier(zap);
                    return;
                }
                MysteryBox closestBox = data.getClosest(MysteryBox.class, loc, 2, 3.5, 2);
                if (closestBox != null && closestBox.isShowingItem()) {
                    closestBox.getLastShownZAPlayer().giveItem(closestBox.getLastShownItem());
                    closestBox.stopShowing();
                    return;
                }
            }
        }
    }
}
