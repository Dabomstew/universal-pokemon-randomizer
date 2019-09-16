package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.pokemon.Pokemon;

public class SimilarTypeGuard extends ReplacementGuard<Pokemon> implements BaseProbabilityGuard {

    @Override
    protected double computeWeight(Pokemon obj) {
        // Get the minimum distance of all types
        double distance = oldValue.primaryType.getOccuranceDistance(obj.primaryType);
        if (oldValue.secondaryType != null) {
            distance = Math.min(distance, oldValue.secondaryType.getOccuranceDistance(obj.primaryType));
            if (obj.secondaryType != null) {
                distance = Math.min(distance, oldValue.secondaryType.getOccuranceDistance(obj.secondaryType));
            }
        }
        if (obj.secondaryType != null) {
            distance = Math.min(distance, oldValue.primaryType.getOccuranceDistance(obj.secondaryType));
        }

        // now do gaussian with the distance
        double stddev = 0.2;
        double tmp = distance / stddev;
        return Math.exp(-0.5 * tmp * tmp);
    }

}
