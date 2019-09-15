package com.dabomstew.pkrandom.sampling;

import java.util.List;
import java.util.Random;

import com.dabomstew.pkrandom.pokemon.Encounter;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.sampling.guards.EncounterGuard;
import com.dabomstew.pkrandom.sampling.guards.TradeGuard;

public class PokemonSampler extends PoolSampler<Pokemon> {

    public PokemonSampler(Random random, List<Pokemon> pokePool) {
        super(random, pokePool);
    }

    public Pokemon samplePokemon() {
        // Update guards
        getGuards(true).stream().filter(g -> g instanceof EncounterGuard).map(g -> (EncounterGuard)g).forEach(g -> g.setEncounter(null));
        getGuards(true).stream().filter(g -> g instanceof TradeGuard).map(g -> (TradeGuard)g).forEach(g -> g.setEncounter(null));

        return super.sampleFor(null);
    }

    @Override
    public Pokemon sampleFor(Pokemon obj) {
        // Update guards
        getGuards(true).stream().filter(g -> g instanceof EncounterGuard).map(g -> (EncounterGuard)g).forEach(g -> g.setEncounter(null));
        getGuards(true).stream().filter(g -> g instanceof TradeGuard).map(g -> (TradeGuard)g).forEach(g -> g.setEncounter(null));

        return super.sampleFor(obj);
    }

    public Pokemon sampleFor(Encounter encounter) {
        // Update guards
        getGuards(true).stream().filter(g -> g instanceof EncounterGuard).map(g -> (EncounterGuard)g).forEach(g -> g.setEncounter(encounter));
        getGuards(true).stream().filter(g -> g instanceof TradeGuard).map(g -> (TradeGuard)g).forEach(g -> g.setEncounter(null));

        return super.sampleFor(encounter.pokemon);
    }
}
