package com.dabomstew.pkrandom.pokemon;

public enum Stat {
    HP(1),
    ATK(1 << 1),
    DEF(1 << 2),
    SPATK(1 << 3),
    SPDEF(1 << 4),
    SPEED(1 << 5),
    SPECIAL(1 << 6),
    POWER(1 << 7),
    ACCURACY(1 << 8),
    PP(1 << 9),
    TYPE(1 << 10),
    CATEGORY(1 << 11);

    public final int val;

    Stat(int val) {
        this.val = val;
    }
}