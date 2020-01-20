package com.dabomstew.pkrandom.constants;

import java.util.Arrays;
import java.util.List;

public class GlobalConstants {

    public static final boolean[] bannedRandomMoves = new boolean[560], bannedForDamagingMove = new boolean[560];
    static {
        bannedRandomMoves[144] = true; // Transform, glitched in RBY
        bannedRandomMoves[165] = true; // Struggle, self explanatory

        bannedForDamagingMove[120] = true; // SelfDestruct
        bannedForDamagingMove[138] = true; // Dream Eater
        bannedForDamagingMove[153] = true; // Explosion
        bannedForDamagingMove[173] = true; // Snore
        bannedForDamagingMove[206] = true; // False Swipe
        bannedForDamagingMove[248] = true; // Future Sight
        bannedForDamagingMove[252] = true; // Fake Out
        bannedForDamagingMove[264] = true; // Focus Punch
        bannedForDamagingMove[353] = true; // Doom Desire
        bannedForDamagingMove[364] = true; // Feint
        bannedForDamagingMove[387] = true; // Last Resort
        bannedForDamagingMove[389] = true; // Sucker Punch

        // new 160
        bannedForDamagingMove[132] = true; // Constrict, overly weak
        bannedForDamagingMove[99] = true; // Rage, lock-in in gen1
        bannedForDamagingMove[205] = true; // Rollout, lock-in
        bannedForDamagingMove[301] = true; // Ice Ball, Rollout clone

        // make sure these cant roll
        bannedForDamagingMove[39] = true; // Sonicboom
        bannedForDamagingMove[82] = true; // Dragon Rage
        bannedForDamagingMove[32] = true; // Horn Drill
        bannedForDamagingMove[12] = true; // Guillotine
        bannedForDamagingMove[90] = true; // Fissure
        bannedForDamagingMove[329] = true; // Sheer Cold

    }

    /* @formatter:off */
    public static final List<Integer> normalMultihitMoves = Arrays.asList(
            292, // Arm Thrust
            140, // Barrage
            198, // Bone Rush
            331, // Bullet Seed
            4, // Comet Punch
            3, // DoubleSlap
            31, // Fury Attack
            154, // Fury Swipes
            333, // Icicle Spear
            42, // Pin Missile
            350, // Rock Blast
            131, // Spike Cannon
            541 // Tail Slap
            );
    
    public static final List<Integer> doubleHitMoves = Arrays.asList(
            155, // Bonemerang
            458, // Double Hit
            24, // Double Kick
            530, // Dual Chop
            544, // Gear Grind
            41 // Twineedle
            );
    
    public static final List<Integer> genderlessPokemon = Arrays.asList(
            81, //Magnemite
            82, //Megneton
            100, //Voltorb
            101, //Electrode
            120, //Staryu
            121, //Starmie
            137,
            233,
            292,
            337,
            338,
            343,
            344,
            374,
            375,
            376,
            436,
            437,
            462,
            474,
            479,
            489,
            490,
            599,
            600,
            601,
            615,
            622,
            623,
            132,
            144,
            145,
            146,
            150,
            151,
            201,
            243,
            244,
            245,
            249,
            250,
            251,
            377,
            378,
            379,
            382,
            383,
            384,
            385,
            386,
            480,
            481,
            482,
            483,
            484,
            486,
            487,
            491,
            492,
            493,
            494,
            638,
            639,
            640,
            643,
            644,
            646,
            647,
            648,
            649
            );
    
    
    /* @formatter:on */

    public static final List<Integer> battleTrappingAbilities = Arrays.asList(23, 42, 71);

    public static final List<Integer> negativeAbilities = Arrays.asList(129, 112, 54, 59, 161);
    // Defeatist, Slow Start, Truant, Forecast, Zen Mode
    // To test: Illusion, Imposter

    public static final int WONDER_GUARD_INDEX = 25;

    public static final int MIN_DAMAGING_MOVE_POWER = 50;

    public static final int METRONOME_MOVE = 118;

    public static final int TRIPLE_KICK_INDEX = 167;

}
