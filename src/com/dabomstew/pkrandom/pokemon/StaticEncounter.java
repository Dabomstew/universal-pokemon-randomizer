package com.dabomstew.pkrandom.pokemon;

public class StaticEncounter {
    public Pokemon pkmn;
    public int forme = 0;
    public String formeSuffix = "";
    public int level;
    public int heldItem;

    public StaticEncounter() {

    }

    public StaticEncounter(Pokemon pkmn) {
        this.pkmn = pkmn;
    }
}
