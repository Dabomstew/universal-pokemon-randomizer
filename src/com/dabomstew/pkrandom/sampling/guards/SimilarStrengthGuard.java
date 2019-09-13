package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.pokemon.Pokemon;

public final class SimilarStrengthGuard extends ReplacementGuard<Pokemon> {

    @Override
    public double computeWeight(Pokemon obj) {
        // FIXME: Maybe we wanna include the stats of the last evo stage rather than the actual pokemon?
        // Gaussian distribution
        double mean = oldValue.bst();
        double stddev = mean * 0.05;
        double x = obj.bst();
        // For simplicty if |x-mean| < 3*stddev N(x, mean, stddev) is effectively 0
        if (Math.abs(x-mean) < 3 * stddev) return 0;
        double tmp = (x-mean)/stddev;
        // Drop normalisation to get peak at 1
        return Math.exp(-0.5* tmp * tmp);
    }
    
}
