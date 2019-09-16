package com.dabomstew.pkrandom.sampling.guards;

public interface BaseProbabilityGuard {
    final double defaultBaseProbability = 0.05;

    default double getBaseProbability() {
        return defaultBaseProbability;
    }
}
