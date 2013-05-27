package com.github.jamesnorris.enumerated;

import java.util.Map;

import com.github.jamesnorris.implementation.ZAPlayer;
import com.google.common.collect.Maps;

public enum PlayerStatus {
    LAST_STAND(1) {
        @Override public void set(ZAPlayer zap) {
            if (!zap.isInLastStand()) {
                zap.toggleLastStand();
            }
        }	    
	},
	LIMBO(2) {
        @Override public void set(ZAPlayer zap) {
            if (!zap.isInLimbo()) {
                zap.setLimbo(true);
            }
        }	    
	}, 
	//@formatter:off
    NORMAL(3) {
        @Override public void set(ZAPlayer zap) {
            if (zap.isInLastStand()) {
                zap.toggleLastStand();
            }
            if (zap.isInLimbo()) {
                zap.setLimbo(false);
            }
            if (zap.isTeleporting()) {
                zap.setTeleporting(false);
            }
        }        
    }, 
	TELEPORTING(4) {
        @Override public void set(ZAPlayer zap) {
            if (!zap.isTeleporting()) {
                zap.setTeleporting(true);
            }
        }    
	};
	//@formatter:on
    //
    public abstract void set(ZAPlayer zap);
    
    private final static Map<Integer, PlayerStatus> BY_ID = Maps.newHashMap();
    
    private int id;
    
    PlayerStatus(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public static PlayerStatus getById(int id) {
        return BY_ID.get(id);
    }
    
    static {
        for (PlayerStatus status : values()) {
            BY_ID.put(status.id, status);
        }
    }
}
