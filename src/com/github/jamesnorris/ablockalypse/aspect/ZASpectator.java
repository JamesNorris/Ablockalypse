package com.github.jamesnorris.ablockalypse.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.jamesnorris.ablockalypse.utility.SerialLocation;

public class ZASpectator extends ZACharacter {// TODO perhaps this will work in the future
    private Player player;
    private Game game;
    private boolean spectating;
    private Location locBeforeSpectating;
    private List<String> hiddenFrom = new ArrayList<String>();

    public ZASpectator(Map<String, Object> savings) {
        super(savings);
        player = super.getPlayer();
        game = super.getGame();
        spectating = (Boolean) savings.get("is_spectating");
        SerialLocation serialLocBeforeSpectating = (SerialLocation) savings.get("location_before_spectating");
        if (serialLocBeforeSpectating != null) {
            locBeforeSpectating = SerialLocation.returnLocation(serialLocBeforeSpectating);
        }
    }

    public ZASpectator(Player player, Game game) {// TODO cancel all events except move, allow noclip and prevent projectile interference
        super(player, game);
    }

    public void beginSpectatorSession() {
        if (spectating) {
            return;
        }
        spectating = true;
        locBeforeSpectating = player.getLocation();
        super.teleport(game.getMainframe().getLocation().clone().add(0, 1, 0), "Spectator mainframe teleport");
        super.rename("", player.getName(), "[S]");
        for (Player other : Bukkit.getServer().getOnlinePlayers()) {
            if (other.canSee(player)) {
                other.hidePlayer(player);
                hiddenFrom.add(other.getName());
            }
        }
    }

    public void endSpectatorSession() {
        if (!spectating) {
            return;
        }
        spectating = false;
        super.teleport(locBeforeSpectating, "Spectator previous teleport");
        super.rename("", player.getName(), "");
        for (String name : hiddenFrom) {
            Player other = Bukkit.getPlayer(name);
            if (other == null || !other.isOnline()) {
                continue;
            }
            other.showPlayer(player);
        }
    }

    @Override public Map<String, Object> getSave() {
        Map<String, Object> savings = new HashMap<String, Object>();
        savings.put("is_spectating", spectating);
        savings.put("location_before_spectating", locBeforeSpectating != null ? new SerialLocation(locBeforeSpectating) : null);
        // hiddenFrom?
        savings.putAll(super.getSave());
        return savings;
    }

    @Override public UUID getUUID() {
        return player.getUniqueId();
    }

    public boolean isInSpectatorSession() {
        return spectating;
    }

    @Override public void remove() {
        if (isInSpectatorSession()) {
            endSpectatorSession();
        }
        super.remove();
    }
}
