package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.Pokemon;

public final class EvolutionSanityGuard extends EncounterGuard implements BaseProbabilityGuard {
    
    private int getLowerBound(Pokemon pkmn) {
        // Basis pokemon -> Lvl 0 base line
        if (pkmn.evolutionsTo.isEmpty()) return 0;
        // We want to find out the lowest level at which we can get this pokemon by evolution
        // if the pokemon doesn't evolve by level, we try to do a "smart" estimate
        int result = 200;
        for (Evolution priorEvo: pkmn.evolutionsTo) {
            int evoLevel = 200;
            if (priorEvo.type.usesLevel()) {
                evoLevel = priorEvo.extraInfo;
                if (evoLevel < result) result = evoLevel;
            } else { // e.g. Stone evolutions
                evoLevel = getUpperBound(pkmn);
                // Check if there is another evolution
                // e.g. Slowpoke can also become Slowbro
                // so we assume the same level for both evos
                if (evoLevel == 100) {
                    // There is no levelup evo
                    // Check the evo level of prior step
                    int priorEvoLevel = getLowerBound(priorEvo.from);
                    // now we just say the evolution level is the prior +10
                    // but for Base 2 evos we don't want them that early
                    // So at least lvl 25 is required
                    evoLevel = Math.max(priorEvoLevel + 10, 25);
                }
            }
            if (evoLevel < result) {
                result = evoLevel;
            }
        }
        return result;
    }
    
    private int getUpperBound(Pokemon pkmn) {
        int result = 0;
        for (Evolution nextEvo: pkmn.evolutionsFrom) {
            if (nextEvo.type.usesLevel()) {
                int evoLevel = nextEvo.extraInfo;
                if (evoLevel > result) result = evoLevel;
            }
            // As most stone evolutions don't learn any new moves after
            // evolving,
            // We don't wan't a max level for encountering it's prior form
            // Also this behavior is required by getLowerBound()
        }

        // minus 1 as we want the max level before evolving
        return result == 0 ? 100 : result - 1;
    }

    @Override
    protected double computeWeight(Pokemon obj) {
        int lvl = encounter.level;
        int lb = getLowerBound(obj);
        int ub = getUpperBound(obj);
        if (lvl >= lb && lvl <= ub) {
            return 1;
        }
        double mean = lvl < lb ? lb : ub;
        double stddev = mean * 0.05;
        // Now gaussian, but drop normalization so we get up to 1 for lvl = mean;
        double tmp = (lvl - mean) / stddev;
        return Math.exp(-0.5 * tmp*tmp);
    }

}
