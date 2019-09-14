package com.dabomstew.pkrandom.sampling;

public abstract class Guard<T> {
    protected abstract double computeWeight(T obj);
    
    public double getWeight(T obj) {
        return computeWeight(obj);
    }
}
