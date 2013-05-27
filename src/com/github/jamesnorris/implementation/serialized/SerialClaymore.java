package com.github.jamesnorris.implementation.serialized;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Claymore;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.Permadata;
import com.github.jamesnorris.util.SerializableLocation;

public class SerialClaymore implements Permadata {
    private static final long serialVersionUID = -553357735950817863L;
    private final SerializableLocation serialLocation, serialBeamLocation;
    private final SerialGame serialGame;
    private final SerialZAPlayer serialPlacer;
    
    public SerialClaymore(Claymore claymore) {
        this.serialLocation = new SerializableLocation(claymore.getLocation());
        this.serialBeamLocation = new SerializableLocation(claymore.getBeamLocation());
        this.serialGame = (SerialGame) claymore.getGame().getSerializedVersion();
        this.serialPlacer = (SerialZAPlayer) claymore.getPlacer().getSerializedVersion();
    }
    
    public Location getLocation() {
        return SerializableLocation.returnLocation(serialLocation);
    }
    
    public Game getGame() {
        DataContainer data = Ablockalypse.getData();
        return (data.gameExists(serialGame.getName())) ? data.getGame(serialGame.getName(), true) : serialGame.load();
    }
    
    public ZAPlayer getPlacer() {
        DataContainer data = Ablockalypse.getData();
        Player player = Bukkit.getPlayer(serialPlacer.getPlayer().getName());
        return (data.isZAPlayer(player)) ? data.getZAPlayer(player) : serialPlacer.load();
    }
    
    public Claymore load() { 
        Location claymoreLoc = SerializableLocation.returnLocation(serialLocation);
        DataContainer data = Ablockalypse.getData();
        Claymore claymore = (data.isClaymore(claymoreLoc)) ? data.getClaymore(claymoreLoc) :  new Claymore(claymoreLoc, getGame(), getPlacer());
        claymore.attemptBeamPlacement(SerializableLocation.returnLocation(serialBeamLocation));
        return claymore;
    }
}