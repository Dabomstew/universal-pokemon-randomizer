package com.dabomstew.pkrandom.sampling.guards;

import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Type;
import com.dabomstew.pkrandom.sampling.Guard;

public class TypeThemedGuard extends Guard<Pokemon> implements BaseProbabilityGuard {

    private Type theme = null;

    public void setTheme(Type theme) {
        this.theme = theme;
    }

    @Override
    protected double computeWeight(Pokemon obj) {
        if (theme == null) {
            return 1;
        }
        // Get minimum distance over both types
        double weight = theme.getOccuranceWeight(obj.primaryType, 0);
        if (obj.secondaryType != null) {
            weight = Math.max(weight, theme.getOccuranceWeight(obj.secondaryType, 0));
        }
        return weight;
    }

}
