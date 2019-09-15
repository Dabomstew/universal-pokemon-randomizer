package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.sampling.Guard;

public final class MaxGuard<T> extends CompositeGuard<T> {

    @Override
    protected double computeWeight(T obj) {
        double result = 0;        
        for (Guard<T> g: guards) {
            result = Math.max(result, g.getWeight(obj));            
        }        
        return result;
    }

}
