package com.dabomstew.pkrandom.sampling;

import java.util.List;
import java.util.Random;

import com.dabomstew.pkrandom.sampling.guards.ReplacementGuard;
import com.dabomstew.pkrandom.sampling.guards.SampleHistoryGuard;

public class PoolSampler<T> extends WeightedRandomSampler<T> {
    private final List<T> pool;

    public PoolSampler(Random random, List<T> pool) {
        super(random);
        this.pool = List.copyOf(pool);
    }

    @Override
    public T sampleObject() {
        // FIXME: Maybe we want to recompute the weights in batches, for performance, as well as stability (especially early when simple changes can lead to high weight fluctuations)
        // compute weights
        super.clear();
        pool.forEach(p -> super.addObject(p));
        // sample objects
        T result = super.sampleObject();
        if (result == null) return result;
        // update sample history based guards
        getGuards(true).stream().filter(g -> g instanceof SampleHistoryGuard).map(g -> (SampleHistoryGuard<T>)g).forEach(g -> g.updateLastSample(result));
        return result;
    }

    public T sampleFor(T obj) {
        // Update guards
        getGuards(true).stream().filter(g -> g instanceof ReplacementGuard).map(g -> (ReplacementGuard<T>)g).forEach(g -> g.setOldValue(obj));

        return sampleObject();
    }

}
