package com.dabomstew.pkrandom.constants;

public class Gen7Constants {

    public static final int Type_SM = N3DSConstants.Type_SM;
    public static final int Type_USUM = N3DSConstants.Type_USUM;

    private static final int pokemonCountSM = 802, pokemonCountUSUM = 807;
    private static final int moveCountSM = 719, moveCountUSUM = 728;
    private static final int highestAbilityIndexSM = 232, highestAbilityIndexUSUM = 233;

    public static int getPokemonCount(int romType) {
        if (romType == Type_SM) {
            return pokemonCountSM;
        } else if (romType == Type_USUM) {
            return pokemonCountUSUM;
        }
        return pokemonCountSM;
    }

    public static int getMoveCount(int romType) {
        if (romType == Type_SM) {
            return moveCountSM;
        } else if (romType == Type_USUM) {
            return moveCountUSUM;
        }
        return moveCountSM;
    }

    public static int getHighestAbilityIndex(int romType) {
        if (romType == Type_SM) {
            return highestAbilityIndexSM;
        } else if (romType == Type_USUM) {
            return highestAbilityIndexUSUM;
        }
        return highestAbilityIndexSM;
    }
}
