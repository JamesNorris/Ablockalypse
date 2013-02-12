package com.github.JamesNorris.Data;

import java.io.Serializable;

import org.bukkit.Location;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Implementation.GameArea;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Implementation.GameMobSpawner;
import com.github.JamesNorris.Implementation.GameMysteryChest;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Interface.GameObject;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Util.GameObjectDifference;
import com.github.JamesNorris.Util.SerializableLocation;

public class MapDataStorage implements Serializable {
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
        GlobalData data = DataManipulator.data;
        // get game orientators
        int j = 0;
        ZAGame game = data.findGame(gameName);
        GameObjectDifference[] orientators = new GameObjectDifference[game.getAllPhysicalObjects().size() + 2];
        for (int i = j; i < game.getAllPhysicalObjects().size(); i++) {
            GameObject object = game.getAllPhysicalObjects().get(i);
            Location key = object.getDefiningBlock().getLocation();
            GameObjectDifference dif = new GameObjectDifference(key, baseKey, object.getType());
            orientators[i] = dif;
            if (object.getType() .equalsIgnoreCase("GameArea"))
                dif.addSecondLocation(((GameArea) object).getPoint(2), baseKey);
        }
        Location mainframe = game.getMainframe();// mainframe
        orientators[orientators.length - 1] = new GameObjectDifference(mainframe, baseKey, "Mainframe");
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

    public void loadToGame(Location baseKey, ZAGame game) {
        for (int i = 0; i < locDifs.length; i++) {
            if (locDifs[i] != null) {
                GameObjectDifference dif = locDifs[i];
                Location calculated = baseKey.clone().add(-dif.Xdif, -dif.Ydif, -dif.Zdif);
                Location calculated2 = baseKey.clone().add(-dif.Xdif2, -dif.Ydif2, -dif.Zdif2);
                String type = dif.type;
                if (type.equalsIgnoreCase("GameBarrier"))
                    game.addBarrier(new GameBarrier(calculated.getBlock(), (ZAGameBase) game));
                else if (type.equalsIgnoreCase("GameMobSpawner"))
                    game.addMobSpawner(new GameMobSpawner(calculated, game));
                else if (type.equalsIgnoreCase("GameArea"))
                    game.addArea(new GameArea((ZAGameBase) game, calculated, calculated2));
                else if (type.equalsIgnoreCase("GameMysteryChest"))
                    game.addMysteryChest(new GameMysteryChest(calculated.getBlock(), game, calculated, game.getActiveMysteryChest() == null));
                else if (type.equalsIgnoreCase("Mainframe"))
                    game.setMainframe(calculated);
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
