package com.github.jamesnorris.ablockalypse.aspect.entity;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Wolf;

import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;
import com.github.jamesnorris.ablockalypse.utility.AblockalypseUtility;

public class Hellhound extends ZAMob {
    /**
     * Creates a new instance of the GameWolf for ZA.
     * 
     * @param wolf The wolf to be made into this instance
     * @param game The game to involve this wolf in
     */
    public Hellhound(World world, UUID entityUUID, Game game) {
        super((Wolf) data.getEntityByUUID(world, entityUUID), game);
        setHitAbsorption(game.getLevel() / Math.sqrt(3 * game.getLevel()));// more than an undead
        entity.getWorld().strikeLightning(entity.getLocation());
        setHitBox(AblockalypseUtility.createHellhoundHitBox(this));
        getTargetter().setNodesPerTick(.1D);
        setAggressive(true);
    }

    /**
     * Gets the Wolf instance associated with this instance.
     * 
     * @return The Wolf instance associated with this instance
     */
    public Wolf getWolf() {
        return (Wolf) entity;
    }

    /**
     * Changes the GameWolfs' state to angry.
     * 
     * @param tf Whether or not to make the wolf aggressive
     */
    public void setAggressive(boolean tf) {
        ((Wolf) entity).setAngry(tf);
    }
}
