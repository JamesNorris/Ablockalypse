package com.github.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.ContainerBlock;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Jukebox;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.External;
import com.github.aspect.Game;
import com.github.behavior.MapDatable;
import com.github.utility.selection.Rectangle;
import com.github.utility.selection.Region;
import com.github.utility.serial.SavedVersion;
import com.github.utility.serial.SerialLocation;

@SuppressWarnings("deprecation") public class MapDataStorage {
    private static DataContainer data = Ablockalypse.getData();

    public static MapDataStorage getFromGame(Game game) {
        return getFromGame(game.getName());
    }

    /* used for unloaded games */
    public static MapDataStorage getFromGame(String gameName) {
        return new MapDataStorage(gameName);
    }

    private Game game;
    private File mapFile, gameFile;

    public MapDataStorage(Game game) {
        this.game = game;
        mapFile = Ablockalypse.getExternal().getMapDataFile(game.getName(), true);
        gameFile = Ablockalypse.getExternal().getMapGameObjectDataFile(game.getName(), true);
    }

    public MapDataStorage(String gameName) {
        this(data.getGame(gameName, true));
    }

    @SuppressWarnings("unused") public boolean load(Location anchor) {
        try {
            List<Map<String, Object>> serialized = External.load(mapFile);
            String gameName = "UNKNOWN";
            Location originalAnchor = null;
            Location originalOpposite = null;
            for (Map<String, Object> thisSerialBlock : serialized) {
                if (thisSerialBlock.containsKey("gameName")) {
                    gameName = (String) thisSerialBlock.get("gameName");
                    originalAnchor = SerialLocation.returnLocation((SerialLocation) thisSerialBlock.get("anchor"));
                    originalOpposite = SerialLocation.returnLocation((SerialLocation) thisSerialBlock.get("opposite"));
                    continue;
                }
                int id = (Integer) thisSerialBlock.get("id");
                byte data = (Byte) thisSerialBlock.get("data");
                World world = Bukkit.getWorld((String) thisSerialBlock.get("worldName"));
                double x = anchor.getX() + (Double) thisSerialBlock.get("xDif");
                double y = anchor.getY() + (Double) thisSerialBlock.get("yDif");
                double z = anchor.getZ() + (Double) thisSerialBlock.get("zDif");
                float yaw = (Float) thisSerialBlock.get("yaw");
                float pitch = (Float) thisSerialBlock.get("pitch");
                Location loc = new Location(world, x, y, z, yaw, pitch);
                Block block = loc.getBlock();
                block.setTypeId(id);
                block.setData(data);
                BlockState state = block.getState();
                specificData: {
                    if (thisSerialBlock.containsKey("INVENTORY_HOLDER")) {
                        ((InventoryHolder) state).getInventory().setContents(getItemsFromSerialization(thisSerialBlock));
                    } else if (thisSerialBlock.containsKey("CONTAINER_BLOCK")) {
                        ((ContainerBlock) state).getInventory().setContents(getItemsFromSerialization(thisSerialBlock));
                    }
                    if (thisSerialBlock.containsKey("command")) {
                        ((CommandBlock) state).setCommand((String) thisSerialBlock.get("command"));
                    }
                    if (thisSerialBlock.containsKey("spawnType")) {
                        CreatureSpawner spawner = (CreatureSpawner) state;
                        spawner.setSpawnedType(EntityType.valueOf((String) thisSerialBlock.get("spawnType")));
                        spawner.setDelay((Integer) thisSerialBlock.get("spawnDelay"));
                    }
                    if (thisSerialBlock.containsKey("playingRecord")) {
                        ((Jukebox) state).setPlaying(Material.valueOf((String) thisSerialBlock.get("playingRecord")));
                    }
                    if (thisSerialBlock.containsKey("rawNote")) {
                        ((NoteBlock) state).setRawNote((Byte) thisSerialBlock.get("rawNote"));
                    }
                    if (thisSerialBlock.containsKey("signLine0")) {
                        for (int i = 0; i <= 3; i++) {
                            ((Sign) state).setLine(i, (String) thisSerialBlock.get("signLine" + i));
                        }
                    }
                    if (thisSerialBlock.containsKey("skullOwner")) {
                        Skull skull = (Skull) state;
                        skull.setOwner((String) thisSerialBlock.get("skullOwner"));
                        skull.setRotation(BlockFace.valueOf((String) thisSerialBlock.get("skullRotation")));
                        skull.setSkullType(SkullType.valueOf((String) thisSerialBlock.get("skullType")));
                    }
                }
            }
            SavedVersion save = (SavedVersion) External.load(gameFile);
            Region oldRegion = new Region(originalAnchor, originalOpposite);
            for (MapDatable mdble : game.getObjectsOfType(MapDatable.class)) {
                Location original = mdble.getPointClosestToOrigin();
                double xDif = original.getX() - originalAnchor.getX();
                double yDif = original.getY() - originalAnchor.getY();
                double zDif = original.getZ() - originalAnchor.getZ();
                if (!oldRegion.contains(original)) {
                    mdble.remove();
                    continue;
                }
                mdble.paste(anchor.clone().add(xDif, yDif, zDif));
            }
            return true;
        } catch (IOException e) {
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @SuppressWarnings("unused") public boolean save(Rectangle rect) {
        try {
            Location anchor = rect.getCorner(1);
            List<Map<String, Object>> serialized = new ArrayList<Map<String, Object>>();
            gameID: {
                Map<String, Object> serialGameID = new HashMap<String, Object>();
                serialGameID.put("gameName", game.getName());
                serialGameID.put("anchor", new SerialLocation(anchor));
                serialGameID.put("opposite", new SerialLocation(rect.getCorner(2)));
                serialized.add(serialGameID);
            }
            for (Location loc : rect.getLocations()) {
                Map<String, Object> thisSerialBlock = new HashMap<String, Object>();
                Block block = loc.getBlock();
                BlockState state = block.getState();
                thisSerialBlock.put("id", block.getTypeId());
                thisSerialBlock.put("data", block.getData());
                thisSerialBlock.put("worldName", loc.getWorld());
                thisSerialBlock.put("xDif", loc.getX() - anchor.getX());
                thisSerialBlock.put("yDif", loc.getY() - anchor.getY());
                thisSerialBlock.put("zDif", loc.getZ() - anchor.getZ());
                thisSerialBlock.put("yaw", loc.getYaw());
                thisSerialBlock.put("pitch", loc.getPitch());
                thisSerialBlock.put("biome", block.getBiome());// unused
                specificData: {
                    if (state instanceof InventoryHolder) {
                        thisSerialBlock.put("INVENTORY_HOLDER", "NULL");
                        int i = 1;
                        for (ItemStack stack : ((InventoryHolder) state).getInventory().getContents()) {
                            Map<String, Object> serialStack = stack.serialize();
                            for (String name : serialStack.keySet()) {
                                Object obj = serialStack.get(name);
                                serialStack.remove(name);
                                serialStack.put("MAPDATA_ITEM=" + i++ + "=" + name, obj);
                            }
                            thisSerialBlock.putAll(serialStack);
                        }
                    } else if (state instanceof ContainerBlock) {
                        thisSerialBlock.put("CONTAINER_BLOCK", "NULL");
                        int i = 1;
                        for (ItemStack stack : ((ContainerBlock) state).getInventory().getContents()) {
                            Map<String, Object> serialStack = stack.serialize();
                            for (String name : serialStack.keySet()) {
                                Object obj = serialStack.get(name);
                                serialStack.remove(name);
                                serialStack.put("MAPDATA_ITEM=" + i++ + "=" + name, obj);
                            }
                            thisSerialBlock.putAll(serialStack);
                        }
                    }
                    if (state instanceof CommandBlock) {
                        thisSerialBlock.put("command", ((CommandBlock) state).getCommand());
                    }
                    if (state instanceof CreatureSpawner) {
                        CreatureSpawner spawner = (CreatureSpawner) state;
                        thisSerialBlock.put("spawnType", spawner.getSpawnedType().toString());
                        thisSerialBlock.put("spawnDelay", spawner.getDelay());
                    }
                    if (state instanceof Jukebox) {
                        thisSerialBlock.put("playingRecord", ((Jukebox) state).getPlaying().toString());
                    }
                    if (state instanceof NoteBlock) {
                        thisSerialBlock.put("rawNote", ((NoteBlock) state).getRawNote());
                    }
                    if (state instanceof Sign) {
                        for (int i = 0; i <= 3; i++) {
                            String line = ((Sign) state).getLine(i);
                            thisSerialBlock.put("signLine" + i, line);
                        }
                    }
                    if (state instanceof Skull) {
                        Skull skull = (Skull) state;
                        thisSerialBlock.put("skullOwner", skull.getOwner());
                        thisSerialBlock.put("skullRotation", skull.getRotation().toString());
                        thisSerialBlock.put("skullType", skull.getSkullType().toString());
                    }
                }
                serialized.add(thisSerialBlock);
            }
            External.save(serialized, mapFile);
            External.save(game.getSave(), gameFile);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private ItemStack[] getItemsFromSerialization(Map<String, Object> thisSerialBlock) {
        List<ItemStack> itemStacks = new ArrayList<ItemStack>();
        Map<Integer, Map<String, Object>> serialStacks = new HashMap<Integer, Map<String, Object>>();
        for (String name : thisSerialBlock.keySet()) {
            Object obj = thisSerialBlock.get(name);
            if (name.startsWith("MAPDATA_ITEM=")) {
                name.replace("MAPDATA_ITEM=", "");
                int index = name.indexOf("=");
                int itemNum = Integer.parseInt(name.substring(0, index - 1));
                name = name.substring(index);
                Map<String, Object> serialItem = new HashMap<String, Object>();
                serialItem.put(name, obj);
                serialStacks.put(itemNum, serialItem);
            }
        }
        for (Integer stackNum : serialStacks.keySet()) {
            Map<String, Object> serialStack = serialStacks.get(stackNum);
            itemStacks.add(ItemStack.deserialize(serialStack));
        }
        return (ItemStack[]) itemStacks.toArray();
    }
}
