package com.github.jamesnorris.implementation.serialized;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.Hellhound;
import com.github.jamesnorris.inter.Permadata;

public class SerialHellhound implements Permadata {
    private static final long serialVersionUID = -3513612207660809658L;
    private final int absorption;
    private final String worldName;
    private final SerialGame serialGame;
    private final UUID wolfUUID;
    
    public SerialHellhound(Hellhound hound) {
        this.absorption = hound.getHitAbsorption();
        this.worldName = hound.getEntity().getWorld().getName();
        this.serialGame = (SerialGame) hound.getGame().getSerializedVersion();
        this.wolfUUID = hound.getEntity().getUniqueId();
    }
    
    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }
    
    public UUID getEntityUniqueId() {
        return wolfUUID;
    }
    
    public Game getGame() {
        DataContainer data = Ablockalypse.getData();
        return (data.gameExists(serialGame.getName())) ? data.getGame(serialGame.getName(), true) : serialGame.load();
    }
    
    public Hellhound load() {
        DataContainer data = Ablockalypse.getData();
        Entity houndEntity = data.getEntityByUUID(getWorld(), getEntityUniqueId());
        Hellhound hound = (data.isHellhound(houndEntity)) ? data.getHellhound(houndEntity) : new Hellhound(getWorld(), getEntityUniqueId(), getGame());
        hound.setHitAbsorption(absorption);
        return hound;
    }
}
