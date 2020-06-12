package com.dabomstew.pkrandom.constants;

public class Gen7Constants {

    public static final int Type_SM = 0;
    public static final int Type_USUM = 1;

    private static final int highestAbilityIndexSM = 232;
    private static final int highestAbilityIndexUSUM = 233;

    public static int getHighestAbilityIndex(int romType) {
        if (romType == Type_SM) {
            return highestAbilityIndexSM;
        } else if (romType == Type_USUM) {
            return highestAbilityIndexUSUM;
        }
        return highestAbilityIndexSM;
    }
}
