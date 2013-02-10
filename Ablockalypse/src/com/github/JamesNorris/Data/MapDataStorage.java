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
import com.github.JamesNorris.Util.SerializableLocation;

public class MapDataStorage implements Serializable {
    private static final long serialVersionUID = -1279160560017448013L;
    private final SerializableLocation keyLoc;
    private final String gameName;
    /**
     * [i][0] = SerializableLocation of the orientator
     * [i][1] = X int difference from key
     * [i][2] = Y int difference from key
     * [i][3] = Z int difference from key
     * [i][4] = The string that matches the class name of object of the orientator
     * [i][5] = Extra attachment that may return null
     */
    public final Object[/*rows*/][/*columns*/] locDifs;

    public MapDataStorage(Location signLoc, String[] lines) {
        this.gameName = lines[2];
        GlobalData data = DataManipulator.data;
        // get game orientators
        int j = 0;
        ZAGame game = data.findGame(gameName);
        Object[][] orientators = new Object[game.getAllPhysicalObjects().size() + 1][6];
        for (int i = j; i < game.getAllPhysicalObjects().size(); i++) {
            GameObject object = game.getAllPhysicalObjects().get(i);
            Location key = object.getDefiningBlocks().get(0).getLocation();
            orientators[i][0] = new SerializableLocation(key);
            orientators[i][1] = signLoc.getBlockX() - key.getBlockX();
            orientators[i][2] = signLoc.getBlockY() - key.getBlockY();
            orientators[i][3] = signLoc.getBlockZ() - key.getBlockZ();
            orientators[i][4] = object.getType();
            if (object.getType().equalsIgnoreCase("GameArea"))
                orientators[i][5] = ((GameArea) object).getPoint(2);
        }
        Location mainframe = game.getMainframe();// mainframe
        orientators[j][0] = new SerializableLocation(mainframe);
        orientators[j][1] = signLoc.getBlockX() - mainframe.getBlockX();
        orientators[j][2] = signLoc.getBlockY() - mainframe.getBlockY();
        orientators[j][3] = signLoc.getBlockZ() - mainframe.getBlockZ();
        orientators[j][4] = "Mainframe";
        // done
        keyLoc = new SerializableLocation(signLoc);
        locDifs = orientators;
    }

    public void loadToGame(ZAGame game) {
        Location key = SerializableLocation.returnLocation(keyLoc);
        for (int i = 0; i < locDifs.length; i++) {
            Location loc = SerializableLocation.returnLocation((SerializableLocation) locDifs[i][0]);
            int Xdif = (Integer) locDifs[i][1];
            int Ydif = (Integer) locDifs[i][2];
            int Zdif = (Integer) locDifs[i][3];
            Location calculated = key.add(Xdif, Ydif, Zdif);
            String type = (String) locDifs[i][4];
            if (type.equalsIgnoreCase("GameBarrier"))
                game.addBarrier(new GameBarrier(calculated.getBlock(), (ZAGameBase) game));
            else if (type.equalsIgnoreCase("GameMobSpawner"))
                game.addMobSpawner(new GameMobSpawner(calculated, game));
            else if (type.equalsIgnoreCase("GameArea"))
                game.addArea(new GameArea((ZAGameBase) game, loc, SerializableLocation.returnLocation((SerializableLocation) locDifs[i][5])));
            else if (type.equalsIgnoreCase("GameMysteryChest"))
                game.addMysteryChest(new GameMysteryChest(calculated.getBlock(), game, calculated, game.getActiveMysteryChest() == null));
            else if (type.equalsIgnoreCase("Mainframe"))
                game.setMainframe(calculated);
        }
    }

    public Location getKeyLocation() {
        return SerializableLocation.returnLocation(keyLoc);
    }

    public Object[][] getData() {
        return locDifs;
    }

    public String getGameName() {
        return gameName;
    }
}
