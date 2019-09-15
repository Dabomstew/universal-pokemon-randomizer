package com.dabomstew.pkrandom.sampling.guards;

import java.util.ArrayList;
import java.util.List;

import com.dabomstew.pkrandom.sampling.Guard;

public abstract class CompositeGuard<T> extends Guard<T> {
    protected final List<Guard<T>> guards = new ArrayList<Guard<T>>();
    
    public CompositeGuard<T> addGuard(Guard<T> guard) {
        guards.add(guard);
        return this;
    }
    
    public List<Guard<T>> getLeafs() {
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
}
