package com.dabomstew.pkrandom.constants;

import java.util.Arrays;
import java.util.List;

/*----------------------------------------------------------------------------*/
/*--  GlobalConstants.java - hold values shared between all games.          --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer" by Dabomstew                   --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2012.                                 --*/
/*--                                                                        --*/
/*--  The custom code written here is licensed under the terms of the GPL:  --*/
/*--                                                                        --*/
/*--  This program is free software: you can redistribute it and/or modify  --*/
/*--  it under the terms of the GNU General Public License as published by  --*/
/*--  the Free Software Foundation, either version 3 of the License, or     --*/
/*--  (at your option) any later version.                                   --*/
/*--                                                                        --*/
/*--  This program is distributed in the hope that it will be useful,       --*/
/*--  but WITHOUT ANY WARRANTY; without even the implied warranty of        --*/
/*--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          --*/
/*--  GNU General Public License for more details.                          --*/
/*--                                                                        --*/
/*--  You should have received a copy of the GNU General Public License     --*/
/*--  along with this program. If not, see <http://www.gnu.org/licenses/>.  --*/
/*----------------------------------------------------------------------------*/

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
    
    
    /* @formatter:on */

    public static final int[] ptSpecialIntros = { 377, 378, 379, 479, 480, 482, 483, 484, 485, 486, 487, 491, 492, 493 };

    public static final List<Integer> battleTrappingAbilities = Arrays.asList(23, 42, 71);

    public static final List<Integer> negativeAbilities = Arrays.asList(129, 112, 54, 59, 161, 150);
    // Defeatist, Slow Start, Truant, Forecast, Zen Mode, Imposter
    // To test: Illusion

    public static final int WONDER_GUARD_INDEX = 25;

    public static final int MIN_DAMAGING_MOVE_POWER = 50;

    public static final int METRONOME_MOVE = 118;

    public static final int TRIPLE_KICK_INDEX = 167;

}
