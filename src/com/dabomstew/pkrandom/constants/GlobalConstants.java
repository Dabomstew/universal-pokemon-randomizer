package com.dabomstew.pkrandom.constants;

/*----------------------------------------------------------------------------*/
/*--  GlobalConstants.java - constants that are relevant for multiple games --*/
/*--                         in the Pokemon series                          --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
/*--  Originally part of "Universal Pokemon Randomizer" by Dabomstew        --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2020.                                 --*/
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

import com.dabomstew.pkrandom.pokemon.Stat;
import com.dabomstew.pkrandom.pokemon.StatChange;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GlobalConstants {

    public static final boolean[] bannedRandomMoves = new boolean[796], bannedForDamagingMove = new boolean[796];
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
            541, // Tail Slap
            594 // Water Shuriken
            );
    
    public static final List<Integer> doubleHitMoves = Arrays.asList(
            155, // Bonemerang
            458, // Double Hit
            742, // Double Iron Bash
            24, // Double Kick
            751, // Dragon Darts
            530, // Dual Chop
            544, // Gear Grind
            41 // Twineedle
            );

    public static final List<Integer> varyingPowerZMoves = Arrays.asList(
            622, // Breakneck Blitz (Physical)
            623, // Breakneck Blitz (Special)
            624, // All-Out Pummeling (Physical)
            625, // All-Out Pummeling (Special)
            626, // Supersonic Skystrike (Physical)
            627, // Supersonic Skystrike (Special)
            628, // Acid Downpour (Physical)
            629, // Acid Downpour (Special)
            630, // Tectonic Rage (Physical)
            631, // Tectonic Rage (Special)
            632, // Continental Crush (Physical)
            633, // Continental Crush (Special)
            634, // Savage Spin-Out (Physical)
            635, // Savage Spin-Out (Special)
            636, // Never-Ending Nightmare (Physical)
            637, // Never-Ending Nightmare (Special)
            638, // Corkscrew Crash (Physical)
            639, // Corkscrew Crash (Special)
            640, // Inferno Overdrive (Physical)
            641, // Inferno Overdrive (Special)
            642, // Hydro Vortex (Physical)
            643, // Hydro Vortex (Special)
            644, // Bloom Doom (Physical)
            645, // Bloom Doom (Special)
            646, // Gigavolt Havoc (Physical)
            647, // Gigavolt Havoc (Special)
            648, // Shattered Psyche (Physical)
            649, // Shattered Psyche (Special)
            650, // Subzero Slammer (Physical)
            651, // Subzero Slammer (Special)
            652, // Devastating Drake (Physical)
            653, // Devastating Drake (Special)
            654, // Black Hole Eclipse (Physical)
            655, // Black Hole Eclipse (Special)
            656, // Twinkle Tackle (Physical)
            657 // Twinkle Tackle (Special)
            );

    public static final List<Integer> fixedPowerZMoves = Arrays.asList(
            658, // Catastropika
            695, // Sinister Arrow Raid
            696, // Malicious Moonsault
            697, // Oceanic Operetta
            698, // Guardian of Alola
            699, // Soul-Stealing 7-Star Strike
            700, // Stoked Sparksurfer
            701, // Pulverizing Pancake
            702, // Extreme Evoboost
            703, // Genesis Supernova
            719, // 10,000,000 Volt Thunderbolt
            723, // Light That Burns the Sky
            724, // Searing Sunraze Smash
            725, // Menacing Moonraze Maelstrom
            726, // Let's Snuggle Forever
            727, // Splintered Stormshards
            728 // Clangorous Soulblaze
            );

    public static final List<Integer> zMoves = Stream.concat(fixedPowerZMoves.stream(),
            varyingPowerZMoves.stream()).collect(Collectors.toList());

    public static Map<Integer,StatChange> getStatChanges(int generation) {
        Map<Integer,StatChange> map = new TreeMap<>();

        switch(generation) {
            case 6:
                map.put(Species.butterfree,new StatChange(Stat.SPATK.val,90));
                map.put(Species.beedrill,new StatChange(Stat.ATK.val,90));
                map.put(Species.pidgeot,new StatChange(Stat.SPEED.val,101));
                map.put(Species.pikachu,new StatChange(Stat.DEF.val | Stat.SPDEF.val,40, 50));
                map.put(Species.raichu,new StatChange(Stat.SPEED.val,110));
                map.put(Species.nidoqueen,new StatChange(Stat.ATK.val,92));
                map.put(Species.nidoking,new StatChange(Stat.ATK.val,102));
                map.put(Species.clefable,new StatChange(Stat.SPATK.val,95));
                map.put(Species.wigglytuff,new StatChange(Stat.SPATK.val,85));
                map.put(Species.vileplume,new StatChange(Stat.SPATK.val,110));
                map.put(Species.poliwrath,new StatChange(Stat.ATK.val,95));
                map.put(Species.alakazam,new StatChange(Stat.SPDEF.val,95));
                map.put(Species.victreebel,new StatChange(Stat.SPDEF.val,70));
                map.put(Species.golem,new StatChange(Stat.ATK.val,120));
                map.put(Species.ampharos,new StatChange(Stat.DEF.val,85));
                map.put(Species.bellossom,new StatChange(Stat.DEF.val,95));
                map.put(Species.azumarill,new StatChange(Stat.SPATK.val,60));
                map.put(Species.jumpluff,new StatChange(Stat.SPDEF.val,95));
                map.put(Species.beautifly,new StatChange(Stat.SPATK.val,100));
                map.put(Species.exploud,new StatChange(Stat.SPDEF.val,73));
                map.put(Species.staraptor,new StatChange(Stat.SPDEF.val,60));
                map.put(Species.roserade,new StatChange(Stat.DEF.val,65));
                map.put(Species.stoutland,new StatChange(Stat.ATK.val,110));
                map.put(Species.unfezant,new StatChange(Stat.ATK.val,115));
                map.put(Species.gigalith,new StatChange(Stat.SPDEF.val,80));
                map.put(Species.seismitoad,new StatChange(Stat.ATK.val,95));
                map.put(Species.leavanny,new StatChange(Stat.SPDEF.val,80));
                map.put(Species.scolipede,new StatChange(Stat.ATK.val,100));
                map.put(Species.krookodile,new StatChange(Stat.DEF.val,80));
                break;
            case 7:
                map.put(Species.arbok,new StatChange(Stat.ATK.val,95));
                map.put(Species.dugtrio,new StatChange(Stat.ATK.val,100));
                map.put(Species.farfetchd,new StatChange(Stat.ATK.val,90));
                map.put(Species.dodrio,new StatChange(Stat.SPEED.val,110));
                map.put(Species.electrode,new StatChange(Stat.SPEED.val,150));
                map.put(Species.exeggutor,new StatChange(Stat.SPDEF.val,75));
                map.put(Species.noctowl,new StatChange(Stat.SPATK.val,86));
                map.put(Species.ariados,new StatChange(Stat.SPDEF.val,70));
                map.put(Species.qwilfish,new StatChange(Stat.DEF.val,85));
                map.put(Species.magcargo,new StatChange(Stat.HP.val | Stat.SPATK.val,60,90));
                map.put(Species.corsola,new StatChange(Stat.HP.val | Stat.DEF.val | Stat.SPDEF.val,65,95,95));
                map.put(Species.mantine,new StatChange(Stat.HP.val,85));
                map.put(Species.swellow,new StatChange(Stat.SPATK.val,75));
                map.put(Species.pelipper,new StatChange(Stat.SPATK.val,95));
                map.put(Species.masquerain,new StatChange(Stat.SPATK.val | Stat.SPEED.val,100,80));
                map.put(Species.delcatty,new StatChange(Stat.SPEED.val,90));
                map.put(Species.volbeat,new StatChange(Stat.DEF.val | Stat.SPDEF.val,75,85));
                map.put(Species.illumise,new StatChange(Stat.DEF.val | Stat.SPDEF.val,75,85));
                map.put(Species.lunatone,new StatChange(Stat.HP.val,90));
                map.put(Species.solrock,new StatChange(Stat.HP.val,90));
                map.put(Species.chimecho,new StatChange(Stat.HP.val | Stat.DEF.val | Stat.SPDEF.val,75,80,90));
                map.put(Species.woobat,new StatChange(Stat.HP.val,65));
                map.put(Species.crustle,new StatChange(Stat.ATK.val,105));
                map.put(Species.beartic,new StatChange(Stat.ATK.val,130));
                map.put(Species.cryogonal,new StatChange(Stat.HP.val | Stat.DEF.val,80,50));
                break;
            case 8:
                map.put(Species.aegislash,new StatChange(Stat.DEF.val | Stat.SPDEF.val,140,140));
                break;
        }
        return map;
    }

    /* @formatter:on */

    public static final int[] ptSpecialIntros = { Species.regirock, Species.regice, Species.registeel, Species.rotom,
            Species.uxie, Species.azelf, Species.dialga, Species.palkia, Species.heatran, Species.regigigas,
            Species.giratina, Species.darkrai, Species.shaymin, Species.arceus };

    public static final List<Integer> xItems = Arrays.asList(55, 56, 57, 58, 59, 60, 61, 62);

    public static final List<Integer> battleTrappingAbilities = Arrays.asList(23, 42, 71);

    public static final List<Integer> negativeAbilities = Arrays.asList(
            129, 112, 54, 59, 161, 103, 100, 121, 176, 197,
            208, 210, 211, 225, 209);
    // Defeatist, Slow Start, Truant, Forecast, Zen Mode, Klutz, Stall, Multitype, Stance Change, Shields Down,
    // Schooling, Battle Bond, Power Construct, RKS System, Disguise
    // To be tested (Gen 8): Gulp Missile, Ice Face, Hunger Switch

    public static final List<Integer> badAbilities = Arrays.asList(
            58, 57, 107, 108, 119, 118, 132, 131, 140, 180, 188, 217,
            222, 223);
    // Minus, Plus, Anticipation, Forewarn, Frisk, Honey Gather, Friend Guard, Healer, Telepathy, Symbiosis, Aura Break,
    // Battery, Receiver, Power of Alchemy

    public static final List<Integer> duplicateAbilities = Arrays.asList(
            72, 73, 74, 75, 76, 116, 160, 163, 164, 194, 219, 221, 223, 230, 231, 232, 236, 242
    );
    // Vital Spirit, White Smoke, Pure Power, Shell Armor, Air Lock, Solid Rock, Iron Barbs, Turboblaze, Teravolt,
    // Emergency Exit, Dazzling, Tangling Hair, Power of Alchemy, Full Metal Body, Shadow Shield, Prism Armor, Libero,
    // Stalwart

    public static final int WONDER_GUARD_INDEX = 25;

    public static final int MIN_DAMAGING_MOVE_POWER = 50;

    public static final int METRONOME_MOVE = 118;

    public static final int TRIPLE_KICK_INDEX = 167;

    public static final int SWIFT_INDEX = 129;

    public static final int HIGHEST_POKEMON_GEN = 8;

}
