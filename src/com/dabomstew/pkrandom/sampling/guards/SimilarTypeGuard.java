package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.pokemon.Pokemon;

public class SimilarTypeGuard extends ReplacementGuard<Pokemon> implements BaseProbabilityGuard {

    @Override
    protected double computeWeight(Pokemon obj) {
        // Get the minimum distance of all types
        double weight = oldValue.primaryType.getOccuranceWeight(obj.primaryType, 0.05);
        if (oldValue.secondaryType != null) {
            weight = Math.max(weight, oldValue.secondaryType.getOccuranceWeight(obj.primaryType, 0.05));
        }
        if (obj.secondaryType != null) {
            weight = Math.max(weight, oldValue.primaryType.getOccuranceWeight(obj.secondaryType, 0.05));
            if (oldValue.secondaryType != null) {
                weight = Math.max(weight, oldValue.secondaryType.getOccuranceWeight(obj.secondaryType, 0.05));
            }
        }
        return weight;
    }

}
