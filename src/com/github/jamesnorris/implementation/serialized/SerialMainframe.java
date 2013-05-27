package com.github.jamesnorris.implementation.serialized;

import java.util.ArrayList;

import org.bukkit.Location;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.Mainframe;
import com.github.jamesnorris.inter.Permadata;
import com.github.jamesnorris.util.SerializableLocation;

public class SerialMainframe implements Permadata {
    private static final long serialVersionUID = 6078752546397563935L;
    private final SerialGame serialGame;
    private final ArrayList<SerializableLocation> serialLinked = new ArrayList<SerializableLocation>();
    private final SerializableLocation serialLocation;

    public SerialMainframe(Mainframe frame) {
        this.serialGame = (SerialGame) frame.getGame().getSerializedVersion();
        for (Location link : frame.getLinks()) {
            serialLinked.add(new SerializableLocation(link));
        }
        this.serialLocation = new SerializableLocation(frame.getLocation());
    }
    
    public Game getGame() {
        DataContainer data = Ablockalypse.getData();
        return (data.gameExists(serialGame.getName())) ? data.getGame(serialGame.getName(), true) : serialGame.load();
    }
    
    public Location getLocation() {
        return SerializableLocation.returnLocation(serialLocation);
    }

    public Mainframe load() {
        Location frameLoc = getLocation();
        DataContainer data = Ablockalypse.getData();
        Mainframe frame = (data.isMainframe(frameLoc)) ? data.getMainframe(frameLoc) : new Mainframe(getGame(), frameLoc);
        for (SerializableLocation serialLink : serialLinked) {
            frame.link(SerializableLocation.returnLocation(serialLink));
        }
        return frame;
    }
}
