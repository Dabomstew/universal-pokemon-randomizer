package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.sampling.Guard;

public abstract class ReplacementGuard<T> extends Guard<T> {
    protected T oldValue;
    
    public void setOldValue(T oldValue) {
        this.oldValue = oldValue;
    }

    
    @Override
    public double getWeight(T obj) {
        if (oldValue == null) return 1;
        return super.getWeight(obj);
    }
}
