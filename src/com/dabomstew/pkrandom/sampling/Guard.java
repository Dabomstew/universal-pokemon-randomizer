package com.dabomstew.pkrandom.sampling;

import com.dabomstew.pkrandom.sampling.guards.BaseProbabilityGuard;

public abstract class Guard<T> {
    protected abstract double computeWeight(T obj);
    
    public double getWeight(T obj) {
        double w = computeWeight(obj);
        // Fuck me thats ugly
        // but Java doesn't have multi inheritance so skrew it
        if (this instanceof BaseProbabilityGuard) {
            double bp = ((BaseProbabilityGuard) this).getBaseProbability();
            return w < bp ? bp : w;
        }
        return w;
    }
}
