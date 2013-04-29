package com.github.jamesnorris.enumerated;

import com.github.jamesnorris.implementation.ZAPlayer;

public enum PlayerStatus {
    LAST_STAND {
        @Override public void set(ZAPlayer zap) {
            if (!zap.isInLastStand()) {
                zap.toggleLastStand();
            }
        }	    
	},
	LIMBO {
        @Override public void set(ZAPlayer zap) {
            if (!zap.isInLimbo()) {
                zap.setLimbo(true);
            }
        }	    
	}, 
	//@formatter:off
    NORMAL {
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
	TELEPORTING {
        @Override public void set(ZAPlayer zap) {
            if (!zap.isTeleporting()) {
                zap.setTeleporting(true);
            }
        }    
	};
	//@formatter:on
    //
    public abstract void set(ZAPlayer zap);
}
