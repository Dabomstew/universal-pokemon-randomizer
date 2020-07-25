package com.dabomstew.pkrandom.pokemon;

public class StaticEncounter {
    public Pokemon pkmn;
    public int forme = 0;
    public String formeSuffix = "";
    public int level;
    public int heldItem;
    public boolean resetMoves = false;

    public StaticEncounter() {

    }

    public StaticEncounter(Pokemon pkmn) {
        this.pkmn = pkmn;
    }

    @Override
    public String toString() {
        return pkmn.fullName() + formeSuffix;
    }

    public boolean canMegaEvolve() {
        if (heldItem != 0) {
            for (MegaEvolution mega: pkmn.megaEvolutionsFrom) {
                if (mega.argument == heldItem) {
                    return true;
                }
            }
        }
        return false;
    }
}
