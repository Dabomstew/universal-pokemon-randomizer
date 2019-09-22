package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.pokemon.Pokemon;

public final class SameEvolutionaryStepGuard extends ReplacementGuard<Pokemon> implements BaseProbabilityGuard {
    private double stddev = 0.2;
    
    public double getStddev() {
        return stddev;
    }

    public void setStddev(double stddev) {
        this.stddev = stddev;
    }

    private int getEvoPos(Pokemon pkmn) {
        if (pkmn.evolutionsTo.isEmpty()) return 0;
        return 1 + getEvoPos(pkmn.evolutionsTo.get(0).from);
    }

    @Override
    protected double computeWeight(Pokemon obj) {
        // Gaussian
        double mean = getEvoPos(oldValue);
        double x = getEvoPos(obj);
        double tmp = (x - mean) / stddev;
        return Math.exp(-0.5 * tmp * tmp);
    }

}
