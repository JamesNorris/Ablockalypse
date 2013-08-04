package com.github.event.bukkit;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.behavior.Buyable;
import com.github.behavior.GameObject;
import com.github.behavior.Powerable;

public class BlockRedstone implements Listener {
    DataContainer data = Ablockalypse.getData();

    @EventHandler(priority = EventPriority.HIGHEST) public void BRE(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        if (data.isGameObject(block.getLocation())) {
            GameObject obj = data.getGameObjectByLocation(block.getLocation());
            for (Powerable powered : obj.getGame().getObjectsOfType(Powerable.class)) {
                if (powered.requiresPower() && powered.getDefiningBlocks().contains(block)) {
                    if (powered instanceof Buyable) {
                        Buyable buyable = (Buyable) obj;
                        if (!buyable.isBought() && powered.requiresPurchaseFirst()) {
                            return;
                        }
                    }
                    powered.power(event.getNewCurrent() > 16);
                }
            }
        }
    }
}
