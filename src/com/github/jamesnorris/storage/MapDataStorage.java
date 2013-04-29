package com.github.jamesnorris.storage;

import java.io.Serializable;

import org.bukkit.Location;

import com.github.Ablockalypse;
import com.github.jamesnorris.enumerated.GameObjectType;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.util.GameObjectOrientation;
import com.github.jamesnorris.util.SerializableLocation;

public class MapDataStorage implements Serializable {
    private static final long serialVersionUID = -1279160560017448013L;
    /**
     * [i][0] = SerializableLocation of the orientator
     * [i][1] = The string that matches the class name of object of the orientator
     * [i][2] = Extra attachment that may return null
     */
    public final GameObjectOrientation[] locDifs;
    private final String gameName;
    private final SerializableLocation keyLoc;

    public MapDataStorage(Location baseKey, String gameName) {
        this.gameName = gameName;
        // get game orientators
        int j = 0;
        Game game = Ablockalypse.getData().getGame(gameName, true);
        GameObjectOrientation[] orientators = new GameObjectOrientation[game.getAllPhysicalObjects().size() + 2];
        for (int i = j; i < game.getAllPhysicalObjects().size(); i++) {
            GameObject object = game.getAllPhysicalObjects().get(i);
            Location key = object.getDefiningBlock().getLocation();
            GameObjectOrientation dif = new GameObjectOrientation(key, baseKey, object.getObjectType().getSerialization());
            orientators[i] = dif;
            if (object.getObjectType().requiresSecondLocation()) {
                dif.addSecondLocation(object.getObjectType().getSecondLocationIfApplicable(object), baseKey);
            }
        }
        keyLoc = new SerializableLocation(baseKey);
        locDifs = orientators;
    }

    public GameObjectOrientation[] getData() {
        return locDifs;
    }

    public String getGameName() {
        return gameName;
    }

    public Location getKeyLocation() {
        return SerializableLocation.returnLocation(keyLoc);
    }

    public void loadToGame(Location baseKey) {
        Game game = Ablockalypse.getData().getGame(gameName, true);
        for (int i = 0; i < locDifs.length; i++) {
            if (locDifs[i] != null) {
                GameObjectOrientation dif = locDifs[i];
                Location calculated = baseKey.clone().add(-dif.Xdif, -dif.Ydif, -dif.Zdif);
                Location calculated2 = baseKey.clone().add(-dif.Xdif2, -dif.Ydif2, -dif.Zdif2);
                GameObjectType type = GameObjectType.bySerialization(dif.type);
                type.loadToGame(game, calculated, calculated2);
            }
        }
    }

    public boolean possibleKey(Location test) {
        for (GameObjectOrientation dif : locDifs) {
            if (dif.typeid != test.clone().add(-dif.Xdif, -dif.Ydif, -dif.Zdif).getBlock().getTypeId()) {
                return false;
            }
        }
        return true;
    }
}
