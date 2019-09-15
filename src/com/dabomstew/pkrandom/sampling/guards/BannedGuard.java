package com.dabomstew.pkrandom.sampling.guards;

import java.util.HashSet;
import java.util.Set;

import com.dabomstew.pkrandom.sampling.Guard;

public class BannedGuard<T extends Comparable<T>> extends Guard<T> {
    private final Set<T> banned = new HashSet<T>();

    public BannedGuard(Set<T> banned) {
        this.banned.addAll(banned);
    }

    public void updateBannset(Set<T> newSet) {
        banned.clear();
        banned.addAll(newSet);
    }

    @Override
    protected double computeWeight(T obj) {
        return banned.contains(obj) ? 0 : 1;
    }

}
