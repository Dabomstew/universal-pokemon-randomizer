package com.dabomstew.pkrandom.sampling;

public abstract class Guard<T> {
    public abstract double computeWeight(T obj);
}
