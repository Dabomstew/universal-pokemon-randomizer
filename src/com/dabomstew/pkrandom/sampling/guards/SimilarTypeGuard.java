package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.pokemon.Pokemon;

public class SimilarTypeGuard extends ReplacementGuard<Pokemon> implements BaseProbabilityGuard {

    @Override
    protected double computeWeight(Pokemon obj) {
        // Get the minimum distance of all types
        double d1 = oldValue.primaryType.getOccuranceDistance(obj.primaryType);
        if (oldValue.secondaryType != null) {
            d1 = Math.min(d1, oldValue.secondaryType.getOccuranceDistance(obj.primaryType));
        }
        double d2 = d1;
        if (obj.secondaryType != null) {
            d2 =oldValue.primaryType.getOccuranceDistance(obj.secondaryType);
            if (oldValue.secondaryType != null) {
                d2 = Math.min(d2, oldValue.secondaryType.getOccuranceDistance(obj.secondaryType));
            }
        }

        double distance = (d1 + d2) / 2;
        // now do gaussian with the distance
        double stddev = 0.12;
        double tmp = distance / stddev;
        return Math.exp(-0.5 * tmp * tmp);
    }

}
