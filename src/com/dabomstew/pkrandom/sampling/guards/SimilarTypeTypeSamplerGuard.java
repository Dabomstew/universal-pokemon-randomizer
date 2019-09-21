package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.pokemon.Type;

public final class SimilarTypeTypeSamplerGuard extends ReplacementGuard<Type> implements BaseProbabilityGuard {

    @Override
    protected double computeWeight(Type obj) {
        return oldValue.getOccuranceWeight(obj, 0.05);
    }

}
