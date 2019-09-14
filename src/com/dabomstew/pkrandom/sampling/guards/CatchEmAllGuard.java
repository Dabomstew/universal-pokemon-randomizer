package com.dabomstew.pkrandom.sampling.guards;

import java.util.HashSet;
import java.util.Set;

import com.dabomstew.pkrandom.pokemon.Pokemon;

public final class CatchEmAllGuard extends SampleHistoryGuard {

    private int pkmnLeft;
    private final boolean gen1;
    private final Set<Pokemon> sampledPokemon = new HashSet<Pokemon>();
    
    public CatchEmAllGuard(int numTotalPokemon, boolean gen1) {
        this.pkmnLeft = numTotalPokemon;
        this.gen1 = gen1;
    }
    
    private void markPokemon(Pokemon pkmn) {
        sampledPokemon.add(pkmn);
        pkmnLeft--;
    }

    @Override
    public void updateLastSample(Pokemon pkmn) {
        if (pkmnLeft == 0) return;
        if (gen1) { // No breeding
            // Mark pokemon as visited
            markPokemon(pkmn);
        } else {
            // Mark whole evolution chain as visited
            pkmn.forEachEvolution(p -> markPokemon(p));
        }
    }

    @Override
    protected double computeWeight(Pokemon obj) {
        // Already sampled all -> no one to prefer
        if (pkmnLeft == 0) return 1;
        // if visited ignore, else consider for sampling
        return sampledPokemon.contains(obj) ? 0 : 1;
    }

}
