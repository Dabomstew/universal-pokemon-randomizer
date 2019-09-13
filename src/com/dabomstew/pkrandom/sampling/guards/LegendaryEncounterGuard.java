package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.pokemon.Pokemon;

public class LegendaryEncounterGuard extends ReplacementGuard<Pokemon> {

    @Override
    public double computeWeight(Pokemon obj) {
        if (obj.isLegendary() == oldValue.isLegendary()) return 1;
        else return 0;
    }
}
