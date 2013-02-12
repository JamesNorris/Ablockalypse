package com.github.JamesNorris.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.Plugin;

import com.github.JamesNorris.Implementation.GameArea;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Implementation.GameHellHound;
import com.github.JamesNorris.Implementation.GameUndead;
import com.github.JamesNorris.Implementation.ZAGameBase;
import com.github.JamesNorris.Implementation.ZAPlayerBase;
import com.github.JamesNorris.Interface.GameObject;
import com.github.JamesNorris.Interface.HellHound;
import com.github.JamesNorris.Interface.MysteryChest;
import com.github.JamesNorris.Interface.Undead;
import com.github.JamesNorris.Interface.ZAGame;
import com.github.JamesNorris.Interface.ZALocation;
import com.github.JamesNorris.Interface.ZAMob;
import com.github.JamesNorris.Interface.ZAPlayer;
import com.github.JamesNorris.Interface.ZAThread;
import com.github.JamesNorris.Threading.BlinkerThread;

public class GlobalData {
    public ArrayList<GameArea> areas = new ArrayList<GameArea>();
    public List<String> authors;
    public HashMap<GameBarrier, Location> barrierpanels = new HashMap<GameBarrier, Location>();
    public HashMap<Location, String> barriers = new HashMap<Location, String>();
    public ArrayList<BlinkerThread> blinkers = new ArrayList<BlinkerThread>();
    public HashMap<Location, MysteryChest> chests = new HashMap<Location, MysteryChest>();
    public ArrayList<GameBarrier> gamebarriers = new ArrayList<GameBarrier>();
    public HashMap<String, Integer> gameLevels = new HashMap<String, Integer>();
    public HashMap<String, ZAGameBase> games = new HashMap<String, ZAGameBase>();
    public ArrayList<GameHellHound> hellhounds = new ArrayList<GameHellHound>();
    public HashMap<String, Location> mainframes = new HashMap<String, Location>();
    public ArrayList<ZAMob> mobs = new ArrayList<ZAMob>();
    public ArrayList<GameObject> objects = new ArrayList<GameObject>();
    public HashMap<String, HashMap<String, Integer>> playerPoints = new HashMap<String, HashMap<String, Integer>>();
    public HashMap<Player, ZAPlayerBase> players = new HashMap<Player, ZAPlayerBase>();
    public HashMap<ZAGameBase, ZALocation> spawns = new HashMap<ZAGameBase, ZALocation>();
    public ArrayList<GameUndead> undead = new ArrayList<GameUndead>();
    public ArrayList<ZAThread> threads = new ArrayList<ZAThread>();
    public HashMap<Location, String> mapDataSigns = new HashMap<Location, String>();
    public String version;

    /**
     * Creates new data storage for Ablockalypse.
     * 
     * @param plugin The instance of the Ablockalypse plugin
     */
    public GlobalData(Plugin plugin) {
        this.authors = plugin.getDescription().getAuthors();
        this.version = plugin.getDescription().getVersion();
    }

    /**
     * Checks if the game exists, if not, creates a new game.
     * 
     * @param name The name of the ZAGame
     * @param spawners Whether or not spawners should be immediately loaded
     * @return The ZAGame found from the name given
     */
    public ZAGame findGame(String name) {
        return (games.containsKey(name)) ? games.get(name) : new ZAGameBase(name);
    }

    /**
     * Finds a ZAPlayer, with the specified Player instance.
     */
    public ZAPlayer findZAPlayer(Player player, String gamename) {
        ZAPlayerBase zap;
        if (players.containsKey(player))
            zap = players.get(player);
        else if (games.containsKey(gamename))
            zap = new ZAPlayerBase(player, games.get(gamename));
        else
            zap = new ZAPlayerBase(player, new ZAGameBase(gamename));
        return zap;
    }

    /**
     * Checks if the specified game exists.
     * 
     * @param gamename The game name to check for
     * @return Whether or not the game exists
     */
    public boolean gameExists(String gamename) {
        if (games.containsKey(gamename))
            return true;
        return false;
    }

    /**
     * Gets a HellHound instance associated with the provided entity.
     * 
     * @param e The entity to check for
     * @return The HellHound instance of the entity
     */
    public HellHound getHellHound(Entity e) {
        for (HellHound hh : hellhounds)
            if (hh.getWolf().getEntityId() == e.getEntityId())
                return hh;
        return null;
    }

    /**
     * Gets the chest attached to this block.
     * 
     * @param b The block to check for
     * @return The MysteryChest that is at the same location as this block
     */
    public MysteryChest getMysteryChest(Location loc) {
        MysteryChest mc = null;
        if (chests.containsKey(loc))
            mc = chests.get(loc);
        return mc;
    }

    /**
     * Gets an arraylist of spawning locations for the game provided.
     * 
     * @param gamename The game to look for
     * @return The arraylist of spawners for the provided game
     */
    public ArrayList<ZALocation> getSpawns(String gamename) {
        ArrayList<ZALocation> locs = new ArrayList<ZALocation>();
        for (ZAGameBase zag : spawns.keySet())
            if (zag.getName() == gamename)
                locs.add(spawns.get(zag));
        return locs;
    }

    /**
     * Gets a GameUndead instance associated with the provided entity.
     * 
     * @param e The entity to check for
     * @return The GameUndead instance of the entity
     */
    public Undead getUndead(Entity e) {
        for (GameUndead gu : undead)
            if (gu.getZombie().getEntityId() == e.getEntityId())
                return gu;
        return null;
    }

    /**
     * Gets the ZAMob linked to the provided entity if one is present.
     * 
     * @param e The entity to check for
     * @return The ZAMob linked to the entity
     */
    public ZAMob getZAMob(Entity e) {
        if (e instanceof Zombie) {
            for (GameUndead gu : undead)
                if (gu.getZombie().getEntityId() == e.getEntityId())
                    return gu;
        } else if (e instanceof Wolf)
            for (GameHellHound ghh : hellhounds)
                if (ghh.getWolf().getEntityId() == e.getEntityId())
                    return ghh;
        return null;
    }

    /**
     * Gets a ZAPlayer from a player without using a string, if the player exists.
     * 
     * @param p The player to check for
     * @return The ZAPlayer instance connected to that player
     */
    public ZAPlayer getZAPlayer(Player p) {
        for (ZAPlayerBase zap : players.values())
            if (zap.getName() == p.getName())
                return zap;
        return null;
    }

    /**
     * Checks if the block given is a MysteryChest instance.
     * 
     * @param b The block to check for
     * @return Whether or not this block is a mystery chest
     */
    public boolean isMysteryChest(Location loc) {
        return chests.keySet().contains(loc);
    }

    /**
     * Checks if the specified entity is a ZA entity
     * 
     * @param e The entity to check for
     * @return Whether or not the entity is a ZA entity
     */
    public boolean isZAMob(Entity e) {
        if (e != null)
            if (e instanceof Wolf && hellhounds != null) {
                for (GameHellHound gh : hellhounds)
                    if (gh.getWolf().getEntityId() == e.getEntityId())
                        return true;
            } else if (e instanceof Zombie && undead != null)
                for (GameUndead gu : undead)
                    if (gu.getZombie().getEntityId() == e.getEntityId())
                        return true;
        return false;
    }

    /**
     * Checks if the specified player exists.
     * 
     * @param player The player to check for
     * @return Whether or not the player exists
     */
    public boolean playerExists(Player player) {
        if (players.containsKey(player))
            return true;
        return false;
    }
}
