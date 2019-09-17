package com.dabomstew.pkrandom.sampling;

import java.util.List;
import java.util.Random;

import com.dabomstew.pkrandom.pokemon.Type;

public class TypeSampler extends PoolSampler<Type> {

    public TypeSampler(Random random, List<Type> pool) {
        super(random, pool);
    }

}
