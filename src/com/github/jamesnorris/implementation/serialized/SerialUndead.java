package com.github.jamesnorris.implementation.serialized;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.Undead;
import com.github.jamesnorris.inter.Permadata;

public class SerialUndead implements Permadata {
    private static final long serialVersionUID = -1136808264340705930L;
    private final String worldName;
    private final int absorption;
    private final boolean fireproof;
    private final SerialGame serialGame;
    private final UUID zombieUUID;
    
    public SerialUndead(Undead undead) {
        this.worldName = undead.getEntity().getWorld().getName();
        this.absorption = undead.getHitAbsorption();
        this.fireproof = undead.isFireproof();
        this.serialGame = (SerialGame) undead.getGame().getSerializedVersion();
        this.zombieUUID = undead.getEntity().getUniqueId();
    }
    
    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }
    
    public UUID getEntityUniqueId() {
        return zombieUUID;
    }
    
    public Game getGame() {
        DataContainer data = Ablockalypse.getData();
        return (data.gameExists(serialGame.getName())) ? data.getGame(serialGame.getName(), true) : serialGame.load();
    }

    public Undead load() {
        DataContainer data = Ablockalypse.getData();
        Entity undeadEntity = data.getEntityByUUID(getWorld(), getEntityUniqueId());
        Undead undead = (data.isUndead(undeadEntity)) ? data.getUndead(undeadEntity) : new Undead(getWorld(), getEntityUniqueId(), getGame());
        undead.setHitAbsorption(absorption);
        undead.setFireproof(fireproof);
        return undead;
    }
}
