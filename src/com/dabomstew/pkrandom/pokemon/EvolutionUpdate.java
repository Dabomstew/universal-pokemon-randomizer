package com.dabomstew.pkrandom.pokemon;

public class EvolutionUpdate {

    private String pkFrom, pkTo;
    private EvolutionType type;
    private String extraInfo;
    private boolean condensed;
    private boolean additional;


    public EvolutionUpdate(Pokemon from, Pokemon to, EvolutionType type, String extraInfo, boolean condensed, boolean additional) {
        pkFrom = from.fullName();
        pkTo = to.fullName();
        this.type = type;
        this.extraInfo = extraInfo;
        this.condensed = condensed;
        this.additional = additional;
    }

    public boolean isCondensed() {
        return condensed;
    }

    @Override
    public String toString() {
        switch (type) {
            case LEVEL:
                if (condensed) {
                    return String.format("%-15s now%s evolves into %-15s at minimum level %s",
                            pkFrom, additional ? " also" : "", pkTo, extraInfo);
                } else {
                    return String.format("%-15s -> %-15s at level %s", pkFrom, pkTo, extraInfo);
                }
            case STONE:
                return String.format("%-15s -> %-15s using a %s", pkFrom, pkTo, extraInfo);
            case HAPPINESS:
                return String.format("%-15s -> %-15s by reaching high happiness", pkFrom, pkTo);
            case LEVEL_ITEM_DAY:
                return String.format("%-15s -> %-15s by leveling up holding %s", pkFrom, pkTo, extraInfo);
            case LEVEL_WITH_OTHER:
                return String.format("%-15s -> %-15s by leveling up with %s in the party", pkFrom, pkTo, extraInfo);
             default:
                return "";
        }
    }
}
