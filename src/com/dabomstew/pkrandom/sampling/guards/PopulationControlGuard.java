package com.dabomstew.pkrandom.sampling.guards;

import java.util.HashMap;
import java.util.Map;

import com.dabomstew.pkrandom.pokemon.Pokemon;

public final class PopulationControlGuard extends SampleHistoryGuard {
    private final Map<Pokemon, Integer> samples = new HashMap<Pokemon, Integer>();
    private int sum = 0;

    @Override
    public void updateLastSample(Pokemon pkmn) {
        Pokemon p = pkmn.getEvolutionRepresentant();
        Integer cnt = samples.get(p);
        int oldVal = cnt == null ? 0 : cnt.intValue();
        cnt = Integer.valueOf(oldVal + 1);
        samples.put(p, cnt);
        sum++;
    }

    @Override
    public double computeWeight(Pokemon obj) {
        Integer cnt = samples.get(obj);
        // Double so we do float divide and not int div
        double c = cnt == null ? 0 : cnt.intValue();
        double w = c / sum;
        // Preferr least sampled pokemon
        return 1.-w;
    }

}
