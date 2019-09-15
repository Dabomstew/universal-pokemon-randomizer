package com.dabomstew.pkrandom.sampling;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.dabomstew.pkrandom.sampling.guards.CompositeGuard;

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
    private final List<Guard<T>> guards = new ArrayList<Guard<T>>();
    private final List<LookupEntry> lookup = new ArrayList<LookupEntry>();
    
    public List<Guard<T>> getGuards(boolean resolveComposites) {
        if (!resolveComposites) return Collections.unmodifiableList(guards);
        List<Guard<T>> result = new ArrayList<Guard<T>>();
        for (Guard<T> g: guards) {
            if (g instanceof CompositeGuard) {
                result.addAll(((CompositeGuard<T>) g).getLeafs());
            } else {
                result.add(g);
            }
        }
        return result;
    }
    
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
    
    public WeightedRandomSampler<T> addGuard(Guard<T> guard) {
        guards.add(guard);
        return this;
    }
    
    public void addObject(T object) {
        double weight = 1;
        for (Guard<T> g: guards) {
            weight *= g.computeWeight(object);
            if (weight == 0) return;
        }
        LookupEntry last;
        // a bit hacky but who cares
        if (lookup.isEmpty()) {
            last = new LookupEntry(0, 0, null);
        } else {
            last = lookup.get(lookup.size() - 1);
        }
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
