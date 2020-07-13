package com.dabomstew.pkrandom.pokemon;

import java.util.Map;
import java.util.TreeMap;

public class TotemPokemon extends StaticEncounter {
    public Aura aura;
    public int ally1Offset;
    public int ally2Offset;
    public Map<Integer,StaticEncounter> allies = new TreeMap<>();

    public boolean unused = false;

    public TotemPokemon() {

    }

    public TotemPokemon(Pokemon pkmn) {
        this.pkmn = pkmn;
    }

    @Override
    public String toString() {
        String ret = pkmn.fullName() + "@%s Lv" + level + "\n    Aura: " + aura.toString() + "\n";
        int i = 1;
        for (StaticEncounter ally: allies.values()) {
            ret = ret.concat("    Ally " + i + ": " + ally.toString() + "\n");
            i++;
        }
        return ret.concat("\n");
    }
}
