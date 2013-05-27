package com.github.jamesnorris.implementation.serialized;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.Ablockalypse;
import com.github.jamesnorris.DataContainer;
import com.github.jamesnorris.enumerated.PlayerStatus;
import com.github.jamesnorris.enumerated.ZAPerk;
import com.github.jamesnorris.implementation.Game;
import com.github.jamesnorris.implementation.ZAPlayer;
import com.github.jamesnorris.inter.Permadata;

public class SerialZAPlayer implements Permadata {
    private static final long serialVersionUID = 1168096152093024715L;
    private final SerialGame serialGame;
    private final int points, kills, pointGainMod, absorption, status;
    private final String name;
    private final ArrayList<Integer> serialPerks;
    private final boolean sentIntoGame, instakill;

    public SerialZAPlayer(ZAPlayer zap) {
        this.serialGame = (SerialGame) zap.getGame().getSerializedVersion();
        this.points = zap.getPoints();
        this.kills = zap.getKills();
        this.pointGainMod = zap.getPointGainMod();
        this.absorption = zap.getHitAbsorption();
        this.status = zap.getStatus().getId();
        this.name = zap.getName();
        this.serialPerks = new ArrayList<Integer>();
        for (ZAPerk perk : zap.getPerks()) {
            serialPerks.add(perk.getId());
        }
        this.sentIntoGame = zap.hasBeenSentIntoGame();
        this.instakill = zap.hasInstaKill();
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(name);
    }
    
    public Game getGame() {
        DataContainer data = Ablockalypse.getData();
        return (data.gameExists(serialGame.getName())) ? data.getGame(serialGame.getName(), true) : serialGame.load();
    }

    public ZAPlayer load() {
        DataContainer data = Ablockalypse.getData();
        Player player = getPlayer();
        ZAPlayer zap = (data.isZAPlayer(player)) ? data.getZAPlayer(player) : new ZAPlayer(player, getGame());
        zap.setPoints(points);
        zap.setKills(kills);
        zap.setPointGainMod(pointGainMod);
        zap.setHitAbsorption(absorption);
        zap.setStatus(PlayerStatus.getById(status));
        zap.setSentIntoGame(sentIntoGame);
        zap.setInstaKill(instakill);
        return null;
    }
}
