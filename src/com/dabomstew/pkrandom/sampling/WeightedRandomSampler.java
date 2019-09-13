package com.dabomstew.pkrandom.sampling;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class WeightedRandomSampler<T> {
    
    private class LookupEntry {
        public double start;
        public double end;
        public T value;
        public LookupEntry(double start, double end, T value) {
            this.start = start;
            this.end = end;
            this.value = value;
        }
    }
    
    private final Random random;
    protected final List<Guard<T>> guards = new ArrayList<Guard<T>>();
    private final List<LookupEntry> lookup = new ArrayList<LookupEntry>();
    
    private T performLookup(double d) {
        int min = 0;
        int max = lookup.size()-1;
        while (min<=max) {
            int idx = (max - min) / 2 + min;
            LookupEntry lo = lookup.get(idx);
            if (d >= lo.start && d < lo.end) return lo.value;
            else if (d < lo.start) max = idx - 1;
            else if (d >= lo.end) min = idx + 1;
        }
        return null;
    }
      
    public WeightedRandomSampler(Random rnd) {
        random = rnd;
    }
    
    public void clear() {
        lookup.clear();
    }
    
    public void clearGuards() {
        guards.clear();
    }
    
    public void addGuard(Guard<T> guard) {
        guards.add(guard);
    }
    
    public void addObject(T object) {
        double weight = 1;
        for (Guard<T> g: guards) {
            weight *= g.computeWeight(object);
            if (weight == 0) return;
        }
        LookupEntry last = lookup.get(lookup.size() - 1);
        LookupEntry next = new LookupEntry(last.end, last.end + weight, object);
        lookup.add(next);
    }
    
    public T sampleObject() {
        if (lookup.isEmpty()) return null;
        double maxVal = lookup.get(lookup.size() - 1).end;
        double rnd = maxVal;
        // random.nextDouble in [0..1] but we have a range of [0..maxVal)
        // so when sampling maxValue we simply resample, until we don't hit maxVal
        while (rnd == maxVal) {
            rnd = random.nextDouble() * maxVal;
        }
        return performLookup(rnd);
    }

}
