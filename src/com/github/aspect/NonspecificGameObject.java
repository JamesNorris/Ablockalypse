package com.github.aspect;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.intelligent.Game;
import com.github.behavior.GameObject;

public class NonspecificGameObject extends PermanentAspect implements GameObject {
    protected DataContainer data = Ablockalypse.getData();
    protected Location location;
    
    public NonspecificGameObject(Location location) {
        this.location = location;
        data.objects.add(this);
    }
    
    public Location getLocation() {
        return location;
    }

    @Override public Block getDefiningBlock() {
        return location.getBlock();
    }

    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(location.getBlock());
        return blocks;
    }

    @Override public Game getGame() {
        return null;
    }

    @Override public void remove() {
        data.objects.remove(this);
    }

    @Override public void onGameEnd() {}

    @Override public void onGameStart() {}

    @Override public void onNextLevel() {}

    @Override public void onLevelEnd() {}

    @Override public int getLoadPriority() {
        return Integer.MAX_VALUE;
    }
    
}
