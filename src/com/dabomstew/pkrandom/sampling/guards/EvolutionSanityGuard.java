package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.Pokemon;

public final class EvolutionSanityGuard extends EncounterGuard implements BaseProbabilityGuard {
    
    private int getLowerBound(Pokemon pkmn) {
        // Basis pokemon -> Lvl 0 base line
        if (pkmn.evolutionsTo.isEmpty()) return 0;
        // We want to find out the lowest level at which we can get this pokemon by evolution
        // if the pokemon doesn't evolve by level, we are simply going going to return infinity (200, should be high enough)
        int result = 200;
        for (Evolution priorEvo: pkmn.evolutionsTo) {
            if (priorEvo.type.usesLevel()) {
                int evoLevel = priorEvo.extraInfo;
                if (evoLevel < result) result = evoLevel;
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
