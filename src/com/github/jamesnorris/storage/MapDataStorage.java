package com.github.jamesnorris.storage;

import java.io.Serializable;

import org.bukkit.Location;


import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.enumerated.GameObjectType;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.util.GameObjectDifference;
import com.github.jamesnorris.util.SerializableLocation;

public class MapDataStorage extends DataManipulator implements Serializable {
    private static final long serialVersionUID = -1279160560017448013L;
    private final SerializableLocation keyLoc;
    private final String gameName;
    /**
     * [i][0] = SerializableLocation of the orientator
     * [i][1] = The string that matches the class name of object of the orientator
     * [i][2] = Extra attachment that may return null
     */
    public final GameObjectDifference[] locDifs;

    public MapDataStorage(Location baseKey, String gameName) {
        this.gameName = gameName;
        // get game orientators
        int j = 0;
        Game game = data.getGame(gameName, true);
        GameObjectDifference[] orientators = new GameObjectDifference[game.getAllPermanentPhysicalObjects().size() + 2];
        for (int i = j; i < game.getAllPermanentPhysicalObjects().size(); i++) {
            GameObject object = game.getAllPermanentPhysicalObjects().get(i);
            Location key = object.getDefiningBlock().getLocation();
            GameObjectDifference dif = new GameObjectDifference(key, baseKey, object.getObjectType().getSerialization());
            orientators[i] = dif;
            if (object.getObjectType().requiresSecondLocation())
                dif.addSecondLocation(object.getObjectType().getSecondLocationIfApplicable(object), baseKey);
        }
        keyLoc = new SerializableLocation(baseKey);
        locDifs = orientators;
    }
    
    public boolean possibleKey(Location test) {
        for (GameObjectDifference dif : locDifs) {
            if (dif.typeid != test.clone().add(-dif.Xdif, -dif.Ydif, -dif.Zdif).getBlock().getTypeId()) {
                return false;
            }
        }
        return true;
    }

    public void loadToGame(Location baseKey) {
        Game game = data.getGame(gameName, true);
        for (int i = 0; i < locDifs.length; i++) {
            if (locDifs[i] != null) {
                GameObjectDifference dif = locDifs[i];
                Location calculated = baseKey.clone().add(-dif.Xdif, -dif.Ydif, -dif.Zdif);
                Location calculated2 = baseKey.clone().add(-dif.Xdif2, -dif.Ydif2, -dif.Zdif2);
                GameObjectType type = GameObjectType.bySerialization(dif.type);
                type.loadToGame(game, calculated, calculated2);
            }
        }
    }

    public Location getKeyLocation() {
        return SerializableLocation.returnLocation(keyLoc);
    }

    public GameObjectDifference[] getData() {
        return locDifs;
    }

    public String getGameName() {
        return gameName;
    }
}
