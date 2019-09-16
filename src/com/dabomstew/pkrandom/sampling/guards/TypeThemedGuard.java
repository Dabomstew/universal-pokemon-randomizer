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
        double distance = theme.getOccuranceDistance(obj.primaryType);
        if (obj.secondaryType != null) {
            distance = Math.min(distance, theme.getOccuranceDistance(obj.secondaryType));
        }
        // now do gaussian with the distance
        double stddev = 0.2;
        double tmp = distance / stddev;
        return Math.exp(-0.5 * tmp * tmp);
    }

}
