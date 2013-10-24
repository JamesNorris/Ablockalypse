package com.github.aspect.block;

import java.util.List;

import org.bukkit.block.Block;

import com.github.aspect.PermanentAspect;

public class Powerable extends PermanentAspect {
    private List<Block> blocks;
    private boolean powered = false;
    
    public Powerable(List<Block> blocks) {
        this.blocks = blocks;
        update();
    }
    
    public List<Block> getBlocks() {
        return blocks;
    }
    
    public boolean isPowered() {
        return powered;
    }
    
    public void setPowered(boolean powered) {
        this.powered = powered;
    }
    
    public void update() {
        for (Block block : blocks) {
            boolean powered = block.getBlockPower() > 0;
            setPowered(powered);
            if (powered) {
                return;
            }
        }
    }
}
