package com.dabomstew.pkrandom.pokemon;

public class StatChange {

    public int stat;
    public int[] values;

    public StatChange(int stat, int... values) {
        this.stat = stat;
        this.values = values;
    }
}
