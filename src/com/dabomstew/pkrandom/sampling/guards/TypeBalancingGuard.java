package com.dabomstew.pkrandom.sampling.guards;

import java.util.HashMap;
import java.util.Map;

import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Type;

public final class TypeBalancingGuard extends SampleHistoryGuard<Pokemon> {
    private final Map<Type, Integer> samples = new HashMap<Type, Integer>();
    private int sum = 0;
    
    private void incrementType(Type t) {
        Integer cnt = samples.get(t);
        int oldVal = cnt == null ? 0 : cnt.intValue();
        cnt = Integer.valueOf(oldVal + 1);
        samples.put(t, cnt);    
    }
    
    @Override
    public void updateLastSample(Pokemon pkmn) {
        incrementType(pkmn.primaryType);
        if (pkmn.secondaryType != null)
            incrementType(pkmn.secondaryType);
        sum++;
    }

    @Override
    public double computeWeight(Pokemon obj) {
        int c1 = samples.getOrDefault(obj.primaryType, Integer.valueOf(0)).intValue();
        int c2 = obj.secondaryType == null ? c1 : 
                  samples.getOrDefault(obj.secondaryType, Integer.valueOf(0)).intValue();
        // average out
        double c = (c1 + c2) / 2.;
        // evaluate share of all sampled
        double w = sum == 0 ? 0 : c / sum;
        // Prefer least sampled objects
        // (1-x^2)^4 boosts if below average (^2)
        // but greatly punishes if above average (^4)
        return Math.pow(1 - w * w, 4);
    }

}
