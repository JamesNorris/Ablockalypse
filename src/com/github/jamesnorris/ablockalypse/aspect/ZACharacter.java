package com.github.jamesnorris.ablockalypse.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.behavior.GameAspect;
import com.github.jamesnorris.ablockalypse.enumerated.PlayerStatus;
import com.github.jamesnorris.ablockalypse.enumerated.Setting;
import com.github.jamesnorris.ablockalypse.enumerated.ZASound;
import com.github.jamesnorris.ablockalypse.utility.BukkitUtility;

public class ZACharacter extends ZALiving implements GameAspect {
    protected static DataContainer data = Ablockalypse.getData();
    private PlayerStatus status = PlayerStatus.NORMAL;
    private Player player;
    private Seat seat;
    private Game game;
    private double absorption;

    public ZACharacter(Map<String, Object> savings) {
        super(savings);
        player = BukkitUtility.forceObtainPlayer((String) savings.get("name")).getPlayer();
        status = PlayerStatus.getById((Integer) savings.get("status_id"));
        absorption = (Double) savings.get("hit_absorption");
        game = Ablockalypse.getData().getGame((String) savings.get("game_name"), true);
    }

    public ZACharacter(Player player, Game game) {
        super(player);
        this.player = player;
        this.game = game;
        data.objects.add(this);
        game.addObject(this);
    }

    @Override public Block getDefiningBlock() {
        if (player == null) {
            return null;
        }
        return player.getLocation().clone().subtract(0, 1, 0).getBlock();
    }

    /**
     * Gets the blocks that defines this object as an object.
     * 
     * @return The blocks assigned to this object
     */
    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(getDefiningBlock());
        return blocks;
    }

    @Override public Game getGame() {
        return game;
    }

    public double getHitAbsorption() {
        return absorption;
    }

    @Override public int getLoadPriority() {
        return 1;
    }

    public Player getPlayer() {
        return player;
    }

    @Override public Map<String, Object> getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("name", player.getName());
        savings.put("status_id", status.getId());
        savings.putAll(super.getSave());
        return savings;
    }

    public Seat getSeat() {
        if (seat == null) {
            seat = new Seat(player.getLocation());
        }
        return seat;
    }

    public PlayerState getState() {
        return new PlayerState(player);
    }

    public PlayerStatus getStatus() {
        return status;
    }

    @Override public UUID getUUID() {
        return player.getUniqueId();
    }

    @Override public void onGameEnd() {}

    @Override public void onGameStart() {}

    @Override public void onLevelEnd() {}

    @Override public void onNextLevel() {}

    @Override public void remove() {
        if (game != null) {
            game.removeObject(this);
        }
        data.objects.remove(this);
    }

    public void rename(String prefix, String name, String suffix) {
        String mod = name;
        int cutoff = 16 - (suffix.length() + 1);
        if (name.length() > cutoff) {
            mod = name.substring(0, cutoff);
        }
        player.setDisplayName(prefix + " " + mod + " " + suffix);
    }

    public void sendToMainframe(String message, String reason) {
        if (message != null) {
            player.sendMessage(message);
        }
        Location loc = game.getMainframe().getLocation().clone().add(0, 1, 0);
        Chunk c = loc.getChunk();
        if (!c.isLoaded()) {
            c.load();
        }
        ZASound.TELEPORT.play(loc);
        if ((Boolean) Setting.DEBUG.getSetting()) {
            System.out.println("[Ablockalypse] [DEBUG] Mainframe TP reason: (" + game.getName() + ") " + reason);
        }
    }

    public void setHitAbsorption(double absorption) {
        this.absorption = absorption;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public void setStatus(PlayerStatus status) {
        if (this.status == status) {
            return;// prevents recursion
        }
        this.status = status;
        status.set(this);
    }

    public void teleport(Location location, String reason) {
        player.teleport(location);
        if ((Boolean) Setting.DEBUG.getSetting()) {
            System.out.println("[Ablockalypse] [DEBUG] TP reason: (" + game.getName() + ") " + reason);
        }
    }

    public void teleport(World world, int x, int y, int z, String reason) {
        teleport(world.getBlockAt(x, y, z).getLocation(), reason);
    }
}
