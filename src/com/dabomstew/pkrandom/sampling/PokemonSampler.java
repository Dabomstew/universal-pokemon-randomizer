package com.dabomstew.pkrandom.sampling;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.sampling.guards.EncounterGuard;
import com.dabomstew.pkrandom.sampling.guards.ReplacementGuard;
import com.dabomstew.pkrandom.sampling.guards.SampleHistoryGuard;
import com.dabomstew.pkrandom.sampling.guards.TradeGuard;
import com.dabomstew.pkrandom.pokemon.Encounter;
import com.dabomstew.pkrandom.pokemon.IngameTrade;

public class PokemonSampler extends WeightedRandomSampler<Pokemon> {
    private final List<Pokemon> pokePool = new ArrayList<Pokemon>();
    
    public PokemonSampler(Random random) {
        super(random);
    }
    
    public void updatePool(List<Pokemon> newPool) {
        pokePool.clear();
        pokePool.addAll(newPool);
    }
    
    @Override
    public Pokemon sampleObject() {
        // sample pokemon
        Pokemon result = super.sampleObject();
        // update sample history based guards
        guards.stream().filter(g -> g instanceof SampleHistoryGuard).map(g -> (SampleHistoryGuard)g).forEach(g -> g.updateLastSample(result));
        return result;
    }
    
    public Pokemon samplePokemon() {
        // compute weights
        super.clear();
        pokePool.forEach(p -> super.addObject(p));
        // sample with these weights
        return sampleObject();
    }
    
    public Pokemon sampleFor(Encounter encounter) {
        // Update guards
        guards.stream().filter(g -> g instanceof EncounterGuard).map(g -> (EncounterGuard)g).forEach(g -> g.setEncounter(encounter));
        guards.stream().filter(g -> g instanceof ReplacementGuard).map(g -> (ReplacementGuard<Pokemon>)g).forEach(g -> g.setOldValue(encounter.pokemon));
        
        return samplePokemon();
    }

    public Pokemon sampleForTrade(IngameTrade trade) {
        // Update guards
        guards.stream().filter(g -> g instanceof TradeGuard).map(g -> (TradeGuard)g).forEach(g -> g.setEncounter(trade));
        guards.stream().filter(g -> g instanceof ReplacementGuard).map(g -> (ReplacementGuard<Pokemon>)g).forEach(g -> g.setOldValue(trade.givenPokemon));
        
        return samplePokemon();
    }
}
