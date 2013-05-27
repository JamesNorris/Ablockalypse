package com.github.jamesnorris.implementation.serialized;

import org.bukkit.Location;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.MobSpawner;
import com.github.jamesnorris.inter.Permadata;
import com.github.jamesnorris.util.SerializableLocation;

public class SerialMobSpawner implements Permadata {
    private static final long serialVersionUID = -4870718907957308120L;
    private final boolean requiresPower, active, powered;
    private final SerialGame serialGame;
    private final SerializableLocation serialLocation;
    
    public SerialMobSpawner(MobSpawner spawner) {
        this.requiresPower = spawner.requiresPower();
        this.active = spawner.isActive();
        this.powered = spawner.isPowered();
        this.serialGame = (SerialGame) spawner.getGame().getSerializedVersion();
        this.serialLocation = new SerializableLocation(spawner.getBukkitLocation());
    }
    
    public Location getLocation() {
        return SerializableLocation.returnLocation(serialLocation);
    }
    
    public Game getGame() {
        DataContainer data = Ablockalypse.getData();
        return (data.gameExists(serialGame.getName())) ? data.getGame(serialGame.getName(), true) : serialGame.load();
    }
    
    public MobSpawner load() {
        Location spawnerLoc = getLocation();
        DataContainer data = Ablockalypse.getData();
        MobSpawner spawner = (data.isMobSpawner(spawnerLoc)) ? data.getMobSpawner(spawnerLoc) : new MobSpawner(spawnerLoc, getGame());
        spawner.setRequiresPower(requiresPower);
        spawner.setActive(active);
        spawner.power(powered);
        return spawner;
    }
}
