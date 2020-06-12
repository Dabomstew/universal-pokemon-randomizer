package com.dabomstew.pkrandom.constants;

public class Gen6Constants {

    public static final int Type_XY = 0;
    public static final int Type_ORAS = 1;

    private static final int highestAbilityIndexXY = 188;
    private static final int highestAbilityIndexORAS = 191;

    public static int getHighestAbilityIndex(int romType) {
        if (romType == Type_XY) {
            return highestAbilityIndexXY;
        } else if (romType == Type_ORAS) {
            return highestAbilityIndexORAS;
        }
        return highestAbilityIndexXY;
    }
}
