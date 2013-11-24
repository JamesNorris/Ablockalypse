package com.github.jamesnorris.ablockalypse.aspect.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.PlayerState;
import com.github.jamesnorris.ablockalypse.enumerated.PlayerStatus;
import com.github.jamesnorris.ablockalypse.enumerated.Setting;
import com.github.jamesnorris.ablockalypse.enumerated.ZASound;
import com.github.jamesnorris.ablockalypse.utility.AblockalypseUtility;
import com.github.jamesnorris.ablockalypse.utility.serial.SavedVersion;

public class ZACharacter extends ZAMob {
    private PlayerStatus status;
    private Player player;
    private Seat seat;
    private Game game;
    private double absorption;

    public ZACharacter(Player player, Game game) {
        super(player, game);
        this.player = player;
        this.game = game;
    }

    public ZACharacter(SavedVersion savings) {
        super(savings);
        player = AblockalypseUtility.forceObtainPlayer((String) savings.get("name")).getPlayer();
        status = PlayerStatus.getById((Integer) savings.get("status_id"));
        absorption = super.getHitAbsorption();
        game = super.getGame();
    }

    @Override public double getHitAbsorption() {
        return absorption;
    }

    @Override public int getLoadPriority() {
        return 1;
    }

    public Player getPlayer() {
        return player;
    }

    @Override public SavedVersion getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("name", player.getName());
        savings.put("status_id", status.getId());
        return new SavedVersion(getHeader(), savings, getClass()).combine(super.getSave());
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

    @Override public void setHitAbsorption(double absorption) {
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
