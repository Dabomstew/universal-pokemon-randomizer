package com.dabomstew.pkrandom.sampling.guards;

import java.util.HashMap;
import java.util.Map;

public class DiversityGuard<T> extends SampleHistoryGuard<T> {
    private final Map<T, Integer> samples = new HashMap<T, Integer>();
    private int sum = 0;

    @Override
    public void updateLastSample(T obj) {
        Integer cnt = samples.get(obj);
        int oldVal = cnt == null ? 0 : cnt.intValue();
        cnt = Integer.valueOf(oldVal + 1);
        samples.put(obj, cnt);
        sum++;
    }

    @Override
    protected double computeWeight(T obj) {
        // Double so we do float divide and not int div
        double c = samples.getOrDefault(obj, Integer.valueOf(0)).intValue();
        double w = sum == 0 ? 0 : c / sum;
        // Prefer least sampled objects
        return 1. - w;
    }

}
