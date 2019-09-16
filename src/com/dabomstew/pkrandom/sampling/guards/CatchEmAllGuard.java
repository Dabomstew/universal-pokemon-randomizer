package com.dabomstew.pkrandom.sampling.guards;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dabomstew.pkrandom.pokemon.Pokemon;

public final class CatchEmAllGuard extends SampleHistoryGuard<Pokemon> {

    private final boolean gen1;
    private final Set<Pokemon> leftToSample = new HashSet<Pokemon>();
    
    public CatchEmAllGuard(List<Pokemon> listOfAllPokemon, boolean gen1) {
        listOfAllPokemon.forEach(p -> leftToSample.add(gen1 ? p : p.getEvolutionRepresentant()));
        this.gen1 = gen1;
    }

    @Override
    public void updateLastSample(Pokemon pkmn) {
        leftToSample.remove(gen1 ? pkmn : pkmn.getEvolutionRepresentant());
    }

    @Override
    protected double computeWeight(Pokemon obj) {
        // Already sampled all -> no one to prefer
        if (leftToSample.isEmpty()) {
            return 1;
        }
        // if visited ignore, else consider for sampling
        return leftToSample.contains(obj) ? 1 : 0;
    }

}
