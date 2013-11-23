package com.github.event.bukkit;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.NonspecificGameAspect;
import com.github.behavior.GameAspect;
import com.github.utility.selection.Cube;

public class BlockRedstone implements Listener {
    private DataContainer data = Ablockalypse.getData();

    @EventHandler(priority = EventPriority.HIGHEST) public void BRE(BlockRedstoneEvent event) {
        Block redstoneBlock = event.getBlock();
        Cube cube = new Cube(redstoneBlock.getLocation(), 1);
        for (Location loc : cube.getLocations()) {
            Block block = loc.getBlock();
            if (data.isGameObject(block.getLocation())) {
                GameAspect obj = data.getGameObjectByLocation(block.getLocation());
                if (obj instanceof NonspecificGameAspect) {
                    ((NonspecificGameAspect) obj).checkForPower();
                }
            }
        }
    }
}
