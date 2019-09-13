package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.sampling.Guard;

public abstract class SampleHistoryGuard extends Guard<Pokemon> {
    public abstract void updateLastSample(Pokemon pkmn);
}
