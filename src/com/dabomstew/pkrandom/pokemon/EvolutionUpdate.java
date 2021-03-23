package com.dabomstew.pkrandom.pokemon;

public class EvolutionUpdate implements Comparable<EvolutionUpdate> {

    private Pokemon from, to;
    private String fromName, toName;
    private EvolutionType type;
    private String extraInfo;
    private boolean condensed;
    private boolean additional;


    public EvolutionUpdate(Pokemon from, Pokemon to, EvolutionType type, String extraInfo, boolean condensed, boolean additional) {
        this.from = from;
        this.to = to;
        fromName = from.fullName();
        toName = to.fullName();
        this.type = type;
        this.extraInfo = extraInfo;
        this.condensed = condensed;
        this.additional = additional;
    }

    public boolean isCondensed() {
        return condensed;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EvolutionUpdate other = (EvolutionUpdate) obj;
        return from == other.from && to == other.to && type == other.type;
    }

    @Override
    public int compareTo(EvolutionUpdate o) {
        if (this.from.number < o.from.number) {
            return -1;
        } else if (this.from.number > o.from.number) {
            return 1;
        } else if (this.to.number < o.to.number) {
            return -1;
        } else if (this.to.number > o.to.number) {
            return 1;
        } else return Integer.compare(this.type.ordinal(), o.type.ordinal());
    }

    @Override
    public String toString() {
        switch (type) {
            case LEVEL:
                if (condensed) {
                    return String.format("%-15s now%s evolves into %-15s at minimum level %s",
                            fromName, additional ? " also" : "", toName, extraInfo);
                } else {
                    return String.format("%-15s -> %-15s at level %s", fromName, toName, extraInfo);
                }
            case STONE:
                return String.format("%-15s -> %-15s using a %s", fromName, toName, extraInfo);
            case HAPPINESS:
                return String.format("%-15s -> %-15s by reaching high happiness", fromName, toName);
            case LEVEL_ITEM_DAY:
                return String.format("%-15s -> %-15s by leveling up holding %s", fromName, toName, extraInfo);
            case LEVEL_WITH_OTHER:
                return String.format("%-15s -> %-15s by leveling up with %s in the party", fromName, toName, extraInfo);
             default:
                return "";
        }
    }
}
