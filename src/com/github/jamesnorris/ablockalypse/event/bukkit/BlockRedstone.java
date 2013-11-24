package com.github.jamesnorris.ablockalypse.event.bukkit;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.NonspecificGameAspect;
import com.github.jamesnorris.ablockalypse.behavior.GameAspect;
import com.github.jamesnorris.ablockalypse.utility.selection.Cube;

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
