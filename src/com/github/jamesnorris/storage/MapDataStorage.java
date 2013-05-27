package com.github.jamesnorris.storage;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import com.github.Ablockalypse;
import com.github.jamesnorris.External;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.inter.Blinkable;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.util.BlockFormation;
import com.github.jamesnorris.util.GameObjectOrientation;
import com.github.jamesnorris.util.Rectangle;

public class MapDataStorage implements Serializable {//TODO new version of this
    private static final long serialVersionUID = -1279160560017448013L;
    public final GameObjectOrientation[] objectOrientations;
    private final String gameName;
    private final BlockFormation physicalFormation;

    public static boolean load(String gameName, Location key) {
        try {
            String path = "mapdata" + File.separatorChar + gameName + ".map";
            File saveFile = new File(Ablockalypse.instance.getDataFolder(), path);
            MapDataStorage mds = (MapDataStorage) External.load(saveFile);
            mds.load(key);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean save(Location start, Location end, String gameName) {
        World world = start.getWorld();
        int startX = start.getBlockX();
        int startY = start.getBlockY();
        int startZ = start.getBlockZ();
        int endX = end.getBlockX();
        int endY = end.getBlockY();
        int endZ = end.getBlockZ();
        int highX = startX > endX ? startX : endX;
        int lowX = startX <= endX ? startX : endX;
        int highY = startY > endY ? startY : endY;
        int lowY = startY <= endY ? startY : endY;
        int highZ = startZ > endZ ? startZ : endZ;
        int lowZ = startZ <= endZ ? startZ : endZ;
        Location topRight = world.getBlockAt(highX, highY, highZ).getLocation();
        Location lowLeft = world.getBlockAt(lowX, lowY, lowZ).getLocation();
        try {
            String path = "mapdata" + File.separatorChar + gameName + ".map";
            File saveFile = new File(Ablockalypse.instance.getDataFolder(), path);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            if (!saveFile.exists()) {// new file given the game name with the suffix ".dat"
                saveFile.createNewFile();
            }
            External.save(new MapDataStorage(lowLeft, topRight, gameName), saveFile);// save a new MapDataStorage to this file
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public MapDataStorage(Location lowLeft, Location topRight, String gameName) {
        this.gameName = gameName;
        // get game orientators
        Game game = Ablockalypse.getData().getGame(gameName, true);
        for (Blinkable blinkable : game.getObjectsOfType(Blinkable.class)) {
            blinkable.getBlinkerThread().setRunThrough(false);
            blinkable.getBlinkerThread().revertBlocks();
        }
        Rectangle rectangle = new Rectangle(lowLeft, topRight);
        List<Location> locations = rectangle.getLocations();
        Location[] locationsArray = new Location[locations.size()];
        locationsArray = rectangle.getLocations().toArray(locationsArray);
        physicalFormation = BlockFormation.toBlockFormation(locationsArray);
        GameObjectOrientation[] orientators = new GameObjectOrientation[game.getAllPhysicalObjects().size() + 2];
        for (int i = 0; i < game.getAllPhysicalObjects().size(); i++) {
            GameObject object = game.getAllPhysicalObjects().get(i);
            Location key = object.getDefiningBlock().getLocation();
            GameObjectOrientation dif = new GameObjectOrientation(key, lowLeft, object.getObjectType().getSerialization());
            orientators[i] = dif;
            if (object.getObjectType().requiresSecondLocation()) {
                dif.addSecondLocation(object.getObjectType().getSecondLocationIfApplicable(object), lowLeft);
            }
        }
        objectOrientations = orientators;
        for (Blinkable blinkable : game.getObjectsOfType(Blinkable.class)) {
            blinkable.getBlinkerThread().setRunThrough(true);
        }
    }

    public GameObjectOrientation[] getOrientations() {
        return objectOrientations;
    }

    public String getGameName() {
        return gameName;
    }

    public void load(final Location lowLeft) {
        final Game game = Ablockalypse.getData().getGame(gameName, true);
        physicalFormation.build(lowLeft);
        for (int i = 0; i < objectOrientations.length; i++) {
            if (objectOrientations[i] != null) {
                GameObjectOrientation dif = objectOrientations[i];
                Location calculated = lowLeft.clone().add(-dif.Xdif, -dif.Ydif, -dif.Zdif);
                Location calculated2 = lowLeft.clone().add(-dif.Xdif2, -dif.Ydif2, -dif.Zdif2);
                GameObjectType type = GameObjectType.bySerialization(dif.type);
                type.loadToGame(game, calculated, calculated2);
            }
        }
    }
}
