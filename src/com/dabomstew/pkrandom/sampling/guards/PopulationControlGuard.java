package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.pokemon.Pokemon;

public final class PopulationControlGuard extends DiversityGuard<Pokemon> {

    @Override
    public void updateLastSample(Pokemon pkmn) {
        // We want to consider whole evolution chains, otherwise 3 step evos
        // would be preferred
        super.updateLastSample(pkmn.getEvolutionRepresentant());
    }

    @Override
    protected double computeWeight(Pokemon obj) {
        // So of course we need to compute the weight for the evo chain
        return super.computeWeight(obj.getEvolutionRepresentant());
    }

}
