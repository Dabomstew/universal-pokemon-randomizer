package com.dabomstew.pkrandom.pokemon;

import com.dabomstew.pkrandom.RomFunctions;

import java.util.Random;

public class Aura {

    private enum AuraStat {
        NONE, ATTACK, DEFENSE, SPECIAL_ATTACK, SPECIAL_DEFENSE, SPEED, ALL
    }

    public AuraStat stat;

    public int stages;

    public Aura(byte b) {
        if (b == 0) {
            stat = AuraStat.NONE;
            stages = 0;
        } else {
            stat = AuraStat.values()[((b - 1) / 3) + 1];
            stages = ((b - 1) % 3) + 1;
        }
    }

    private Aura(AuraStat stat, int stages) {
        this.stat = stat;
        this.stages = stages;
    }

    public byte toByte() {
        if (stat == AuraStat.NONE) {
            return 0;
        } else {
            return (byte)(((stat.ordinal() - 1) * 3) + (stages));
        }
    }

    public static Aura randomAura(Random random) {
        return new Aura((byte)(random.nextInt(18) + 1));
    }

    public static Aura randomAuraSimilarStrength(Random random, Aura old) {
        if (old.stat == AuraStat.NONE || old.stat == AuraStat.ALL) {
            return old;
        } else {
            return new Aura(AuraStat.values()[random.nextInt(5) + 1], old.stages);
        }
    }

    @Override
    public String toString() {
        String ret = RomFunctions.camelCase(stat.toString()).replace("_"," ");
        return stat == AuraStat.NONE ? ret : ret + " +" + stages;
    }
}
