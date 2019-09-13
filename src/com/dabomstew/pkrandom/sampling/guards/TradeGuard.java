package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.pokemon.IngameTrade;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.sampling.Guard;

public abstract class TradeGuard extends Guard<Pokemon> {
    protected IngameTrade trade;
    
    public void setEncounter(IngameTrade trade) {
        this.trade = trade;
    }
}
