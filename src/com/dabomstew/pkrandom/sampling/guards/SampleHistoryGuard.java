package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.sampling.Guard;

public abstract class SampleHistoryGuard<T> extends Guard<T> {
    public abstract void updateLastSample(T obj);
}
