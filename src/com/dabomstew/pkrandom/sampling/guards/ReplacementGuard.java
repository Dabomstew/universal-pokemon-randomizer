package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.sampling.Guard;

public abstract class ReplacementGuard<T> extends Guard<T> {
    protected T oldValue;
    
    public void setOldValue(T oldValue) {
        this.oldValue = oldValue;
    }
}
