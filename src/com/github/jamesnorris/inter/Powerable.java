package com.github.jamesnorris.inter;

public interface Powerable {
    public void powerOn();
    public void powerOff();
    public void power(boolean power);
    public boolean requiresPower();
    public void setRequiresPower(boolean required);
    public boolean isPowered();
}
