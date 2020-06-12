package com.dabomstew.pkrandom.constants;

import com.dabomstew.pkrandom.pokemon.Stat;
import com.dabomstew.pkrandom.pokemon.StatChange;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    public static final Map<Integer,StatChange> gen6StatChanges = setupStatChanges(6);
    public static final Map<Integer,StatChange> gen7StatChanges = setupStatChanges(7);
    public static final Map<Integer,StatChange> gen8StatChanges = setupStatChanges(8);

    private static Map<Integer,StatChange> setupStatChanges(int generation) {
        Map<Integer,StatChange> map = new TreeMap<>();

        switch(generation) {
            case 6:
                map.put(12,new StatChange(Stat.SPATK.val,90));
                map.put(15,new StatChange(Stat.ATK.val,90));
                map.put(18,new StatChange(Stat.SPEED.val,101));
                map.put(25,new StatChange(Stat.DEF.val | Stat.SPDEF.val,40, 50));
                map.put(26,new StatChange(Stat.SPEED.val,110));
                map.put(31,new StatChange(Stat.ATK.val,92));
                map.put(34,new StatChange(Stat.ATK.val,102));
                map.put(36,new StatChange(Stat.SPATK.val,95));
                map.put(40,new StatChange(Stat.SPATK.val,85));
                map.put(45,new StatChange(Stat.SPATK.val,110));
                map.put(62,new StatChange(Stat.ATK.val,95));
                map.put(65,new StatChange(Stat.SPDEF.val,95));
                map.put(71,new StatChange(Stat.SPDEF.val,70));
                map.put(76,new StatChange(Stat.ATK.val,120));
                map.put(181,new StatChange(Stat.DEF.val,85));
                map.put(182,new StatChange(Stat.DEF.val,95));
                map.put(184,new StatChange(Stat.SPATK.val,60));
                map.put(189,new StatChange(Stat.SPDEF.val,95));
                map.put(267,new StatChange(Stat.SPATK.val,100));
                map.put(295,new StatChange(Stat.SPDEF.val,73));
                map.put(398,new StatChange(Stat.SPDEF.val,60));
                map.put(407,new StatChange(Stat.DEF.val,65));
                map.put(508,new StatChange(Stat.ATK.val,110));
                map.put(521,new StatChange(Stat.ATK.val,115));
                map.put(526,new StatChange(Stat.SPDEF.val,80));
                map.put(545,new StatChange(Stat.ATK.val,100));
                map.put(553,new StatChange(Stat.DEF.val,80));
                break;
            case 7:
                map.put(24,new StatChange(Stat.ATK.val,95));
                map.put(51,new StatChange(Stat.ATK.val,100));
//                map.put(65,new StatChange(Stat.SPDEF.val,105)); // Should be Mega Alakazam
                map.put(83,new StatChange(Stat.ATK.val,90));
                map.put(85,new StatChange(Stat.SPEED.val,110));
                map.put(101,new StatChange(Stat.SPEED.val,150));
                map.put(103,new StatChange(Stat.SPDEF.val,75));
                map.put(164,new StatChange(Stat.SPATK.val,86));
                map.put(168,new StatChange(Stat.SPDEF.val,70));
                map.put(211,new StatChange(Stat.DEF.val,85));
                map.put(219,new StatChange(Stat.HP.val | Stat.SPATK.val,60,90));
                map.put(222,new StatChange(Stat.HP.val | Stat.DEF.val | Stat.SPDEF.val,65,95,95));
                map.put(226,new StatChange(Stat.HP.val,85));
                map.put(277,new StatChange(Stat.SPATK.val,75));
                map.put(279,new StatChange(Stat.SPATK.val,95));
                map.put(284,new StatChange(Stat.SPATK.val | Stat.SPEED.val,100,80));
                map.put(301,new StatChange(Stat.SPEED.val,90));
                map.put(313,new StatChange(Stat.DEF.val | Stat.SPDEF.val,75,85));
                map.put(314,new StatChange(Stat.DEF.val | Stat.SPDEF.val,75,85));
                map.put(337,new StatChange(Stat.HP.val,90));
                map.put(338,new StatChange(Stat.HP.val,90));
                map.put(358,new StatChange(Stat.HP.val | Stat.DEF.val | Stat.SPDEF.val,75,80,90));
                map.put(527,new StatChange(Stat.HP.val,65));
                map.put(558,new StatChange(Stat.ATK.val,105));
                map.put(614,new StatChange(Stat.ATK.val,130));
                map.put(615,new StatChange(Stat.HP.val | Stat.DEF.val,80,50));
                break;
            case 8:
                map.put(681,new StatChange(Stat.DEF.val | Stat.SPDEF.val,140,140));
//                map.put(681,new StatChange(Stat.ATK.val | Stat.SPATK.val,140,140)); // Should be Aegislash Blade Forme
                break;
        }
        return map;
    }

    /* @formatter:on */

    public static final int[] ptSpecialIntros = { 377, 378, 379, 479, 480, 482, 483, 484, 485, 486, 487, 491, 492, 493 };

    public static final List<Integer> battleTrappingAbilities = Arrays.asList(23, 42, 71);

    public static final List<Integer> negativeAbilities = Arrays.asList(129, 112, 54, 59, 161, 103, 100, 121);
    // Defeatist, Slow Start, Truant, Forecast, Zen Mode, Klutz, Stall, Multitype
    // To be tested (Gen 6): Stance Change, Parental Bond
    // To be tested (Gen 7): Shields Down, Schooling, Disguise, Battle Bond, Power Construct, RKS System
    // To be tested (Gen 8): Gulp Missile, Ice Face, Hunger Switch

    public static final List<Integer> badAbilities = Arrays.asList(58, 57, 107, 108, 119, 118, 132, 131, 140);
    // Minus, Plus, Anticipation, Forewarn, Frisk, Honey Gather, Friend Guard, Healer, Telepathy
    // TBD: Symbiosis, Aura Break, Battery, Receiver, Power of Alchemy

    public static final int WONDER_GUARD_INDEX = 25;

    public static final int MIN_DAMAGING_MOVE_POWER = 50;

    public static final int METRONOME_MOVE = 118;

    public static final int TRIPLE_KICK_INDEX = 167;

}
