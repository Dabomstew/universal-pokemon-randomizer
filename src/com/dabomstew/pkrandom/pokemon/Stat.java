package com.dabomstew.pkrandom.pokemon;

public enum Stat {
    HP(1),
    ATK(1 << 1),
    DEF(1 << 2),
    SPATK(1 << 3),
    SPDEF(1 << 4),
    SPEED(1 << 5),
    POWER(1 << 6),
    ACCURACY(1 << 7),
    PP(1 << 8),
    TYPE(1 << 9),
    CATEGORY(1 << 10);

    public final int val;

    Stat(int val) {
        this.val = val;
    }
}