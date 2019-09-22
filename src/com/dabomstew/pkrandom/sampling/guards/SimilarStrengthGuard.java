package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.pokemon.Pokemon;

public final class SimilarStrengthGuard extends ReplacementGuard<Pokemon> implements BaseProbabilityGuard {

    private double decay = 0.05;

    public double getDecay() {
        return decay;
    }

    public void setDecay(double decay) {
        this.decay = decay;
    }

    @Override
    public double computeWeight(Pokemon obj) {
        // FIXME: Maybe we wanna include the stats of the last evo stage rather than the actual pokemon?
        // Gaussian distribution
        double mean = oldValue.bst();
        double stddev = mean * decay;
        double x = obj.bst();
        double tmp = (x-mean)/stddev;
        // Drop normalisation to get peak at 1
        return Math.exp(-0.5* tmp * tmp);
    }
    
}
