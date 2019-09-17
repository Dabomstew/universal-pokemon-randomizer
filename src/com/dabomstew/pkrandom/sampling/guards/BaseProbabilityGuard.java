package com.dabomstew.pkrandom.sampling.guards;

public interface BaseProbabilityGuard {
    final double defaultBaseProbability = 0.005;

    default double getBaseProbability() {
        return defaultBaseProbability;
    }
}
