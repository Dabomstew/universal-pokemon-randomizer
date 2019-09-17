package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.pokemon.Type;

public final class SimilarTypeTypeSamplerGuard extends ReplacementGuard<Type> implements BaseProbabilityGuard {

    @Override
    protected double computeWeight(Type obj) {
        double distance = oldValue.getOccuranceDistance(obj);

        // now do gaussian with the distance
        double stddev = 0.2;
        double tmp = distance / stddev;
        return Math.exp(-0.5 * tmp * tmp);
    }

}
