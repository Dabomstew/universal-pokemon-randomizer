package com.dabomstew.pkrandom.sampling.guards;

import java.util.HashSet;
import java.util.Set;

public class UniqueGuard<T> extends SampleHistoryGuard<T> {
    Set<T> sampled = new HashSet<T>();

    public void reset() {
        sampled.clear();
    }

    @Override
    public void updateLastSample(T obj) {
        sampled.add(obj);
    }

    @Override
    protected double computeWeight(T obj) {
        if (sampled.contains(obj)) {
            return 0;
        }
        return 1;
    }

}
