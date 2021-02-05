package com.dabomstew.pkrandom.constants;

/*----------------------------------------------------------------------------*/
/*--  Gen5Constants.java - Constants for Black/White/Black 2/White 2        --*/
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.MoveCategory;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.Type;

public class Gen5Constants {

    public static final int Type_BW = 0;
    public static final int Type_BW2 = 1;

    public static final int pokemonCount = 649, moveCount = 559, nonUnovaPokemonCount = 493;
    private static final int bw1FormeCount = 18, bw2FormeCount = 24;
    private static final int bw1formeOffset = 0, bw2formeOffset = 35;

    private static final int bw1NonPokemonBattleSpriteCount = 3;
    private static final int bw2NonPokemonBattleSpriteCount = 36;

    public static final int bsHPOffset = 0, bsAttackOffset = 1, bsDefenseOffset = 2, bsSpeedOffset = 3,
            bsSpAtkOffset = 4, bsSpDefOffset = 5, bsPrimaryTypeOffset = 6, bsSecondaryTypeOffset = 7,
            bsCatchRateOffset = 8, bsCommonHeldItemOffset = 12, bsRareHeldItemOffset = 14,
            bsDarkGrassHeldItemOffset = 16, bsGrowthCurveOffset = 21, bsAbility1Offset = 24, bsAbility2Offset = 25,
            bsAbility3Offset = 26, bsFormeOffset = 28, bsFormeSpriteOffset = 30, bsFormeCountOffset = 32,
            bsTMHMCompatOffset = 40, bsMTCompatOffset = 60;

    public static final byte[] bw1NewStarterScript = { 0x24, 0x00, (byte) 0xA7, 0x02, (byte) 0xE7, 0x00, 0x00, 0x00,
            (byte) 0xDE, 0x00, 0x00, 0x00, (byte) 0xF8, 0x01, 0x05, 0x00 };

    public static final String bw1StarterScriptMagic = "2400A702";

    public static final int bw1StarterTextOffset = 18, bw1CherenText1Offset = 26, bw1CherenText2Offset = 53;

    public static final byte[] bw2NewStarterScript = { 0x28, 0x00, (byte) 0xA1, 0x40, 0x04, 0x00, (byte) 0xDE, 0x00,
            0x00, 0x00, (byte) 0xFD, 0x01, 0x05, 0x00 };

    public static final String bw2StarterScriptMagic = "2800A1400400";

    public static final int bw2StarterTextOffset = 37, bw2RivalTextOffset = 60;

    public static final int perSeasonEncounterDataLength = 232;
    private static final int bw1AreaDataEntryLength = 249, bw2AreaDataEntryLength = 345, bw1EncounterAreaCount = 61, bw2EncounterAreaCount = 85;

    public static final int[] encountersOfEachType = { 12, 12, 12, 5, 5, 5, 5 };

    public static final String[] encounterTypeNames = { "Grass/Cave", "Doubles Grass", "Shaking Spots", "Surfing",
            "Surfing Spots", "Fishing", "Fishing Spots" };

    public static final int[] habitatClassificationOfEachType = { 0, 0, 0, 1, 1, 2, 2 };

    public static final int bw2Route4AreaIndex = 40, bw2VictoryRoadAreaIndex = 76, bw2ReversalMountainAreaIndex = 73;

    public static final int b2Route4EncounterFile = 104, b2VRExclusiveRoom1 = 71, b2VRExclusiveRoom2 = 73,
            b2ReversalMountainStart = 49, b2ReversalMountainEnd = 54;

    public static final int w2Route4EncounterFile = 105, w2VRExclusiveRoom1 = 78, w2VRExclusiveRoom2 = 79,
            w2ReversalMountainStart = 55, w2ReversalMountainEnd = 60;

    public static final int[] bw2HiddenHollowUnovaPokemon = { 505, 507, 510, 511, 513, 515, 519, 523, 525, 527, 529,
            531, 533, 535, 538, 539, 542, 545, 546, 548, 550, 553, 556, 558, 559, 561, 564, 569, 572, 575, 578, 580,
            583, 587, 588, 594, 596, 601, 605, 607, 610, 613, 616, 618, 619, 621, 622, 624, 626, 628, 630, 631, 632, };

    public static final String tmDataPrefix = "87038803";

    public static final int tmCount = 95, hmCount = 6, tmBlockOneCount = 92, tmBlockOneOffset = 328,
            tmBlockTwoOffset = 618;

    public static final String bw1ItemPalettesPrefix = "E903EA03020003000400050006000700",
            bw2ItemPalettesPrefix = "FD03FE03020003000400050006000700";

    public static final int bw2MoveTutorCount = 60, bw2MoveTutorBytesPerEntry = 12;

    public static final int evolutionMethodCount = 27;

    public static final int slowpokeIndex = 79, eeveeIndex = 133, karrablastIndex = 588, shelmetIndex = 616;

    public static final int sunStoneIndex = 80, moonStoneIndex = 81, waterStoneIndex = 84;

    public static final int highestAbilityIndex = 164;

    public static final int fossilPokemonFile = 877;
    public static final int fossilPokemonLevelOffset = 0x3F7;

    public static final Map<Integer,List<Integer>> abilityVariations = setupAbilityVariations();

    private static Map<Integer,List<Integer>> setupAbilityVariations() {
        Map<Integer,List<Integer>> map = new HashMap<>();
        map.put(15,Arrays.asList(15,72)); // Insomnia/Vital Spirit
        map.put(29,Arrays.asList(29,73)); // Clear Body/White Smoke
        map.put(37,Arrays.asList(37,74)); // Huge Power/Pure Power
        map.put(4,Arrays.asList(4,75)); // Battle Armor/Shell Armor
        map.put(13,Arrays.asList(13,76)); // Cloud Nine/Air Lock
        map.put(111,Arrays.asList(111,116)); // Filter/Solid Rock
        map.put(24,Arrays.asList(24,160)); // Rough Skin/Iron Barbs
        map.put(104,Arrays.asList(104,163,164)); // Mold Breaker/Turboblaze/Teravolt

        return map;
    }

    public static final int normalItemSetVarCommand = 0x28, hiddenItemSetVarCommand = 0x2A, normalItemVarSet = 0x800C,
            hiddenItemVarSet = 0x8000;

    public static final int scriptListTerminator = 0xFD13;
    
    public static final int luckyEggIndex = 0xE7;

    public static final int[] mulchIndices = {0x5F, 0x60, 0x61, 0x62};

    public static final MoveCategory[] moveCategoryIndices = { MoveCategory.STATUS, MoveCategory.PHYSICAL,
            MoveCategory.SPECIAL };

    public static byte moveCategoryToByte(MoveCategory cat) {
        switch (cat) {
        case PHYSICAL:
            return 1;
        case SPECIAL:
            return 2;
        case STATUS:
        default:
            return 0;
        }
    }

    public static final Type[] typeTable = constructTypeTable();

    private static final Map<Integer,String> bw1FormeSuffixes = setupFormeSuffixes(Gen5Constants.Type_BW);

    private static final Map<Integer,String> bw2FormeSuffixes = setupFormeSuffixes(Gen5Constants.Type_BW2);

    private static final Map<Integer,Map<Integer,String>> formeSuffixesByBaseForme = setupFormeSuffixesByBaseForme();
    private static final Map<Integer,String> dummyFormeSuffixes = setupDummyFormeSuffixes();

    private static final Map<Integer,Map<Integer,Integer>> absolutePokeNumsByBaseForme = setupAbsolutePokeNumsByBaseForme();
    private static final Map<Integer,Integer> dummyAbsolutePokeNums = setupDummyAbsolutePokeNums();

    public static String getFormeSuffixByBaseForme(int baseForme, int formNum) {
        return formeSuffixesByBaseForme.getOrDefault(baseForme,dummyFormeSuffixes).getOrDefault(formNum,"");
    }

    public static Integer getAbsolutePokeNumByBaseForme(int baseForme, int formNum) {
        return absolutePokeNumsByBaseForme.getOrDefault(baseForme,dummyAbsolutePokeNums).getOrDefault(formNum,baseForme);
    }

    public static final List<Integer> emptyPlaythroughTrainers = Arrays.asList(new Integer[] { });
    
    public static final List<Integer> bw1MainPlaythroughTrainers = Arrays.asList(
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
            20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
            40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52,
            62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 79,
            80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 93, 94, 95, 96, 97, 98,
            99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118,
            119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 137, 138,
            139, 140, 141, 142, 143, 144, 145, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162,
            163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181,
            182, 183, 184, 186, 187, 188, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203,
            204, 212, 213, 214, 215, 216, 217, 218, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230,
            231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250,
            251, 252, 253, 254, 255, 256, 257, 258, 263, 264, 265, 266, 267, 268, 269, 270, 271, 272, 273,
            274, 275, 276, 277, 278, 279, 280, 281, 282, 283, 284, 285, 286, 290, 291, 292, 293,
            294, 295, 296, 297, 298, 299, 300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312,
            313, 315, 316, 401, 402, 408, 409, 412, 413, 438, 439, 441, 442, 443, 445, 447, 450,
            460, 461, 462, 465, 466, 468, 469, 470, 471, 472, 473, 474, 475, 476, 477, 478, 479, 480, 481,
            484, 485, 488, 489, 490, 501, 502, 503, 504, 505, 506,
            513, 514, 515, 516, 517, 518, 519, 520, 526, 531, 532, 533, 534, 535, 536, 537,
            538, 544, 545, 546, 549, 550, 552, 553, 554, 555, 556, 557, 582, 583, 584, 585, 586,
            587, 600, 601, 602, 603, 604, 605, 606, 607, 610, 611, 612, 613);

    public static final List<Integer> bw2MainPlaythroughTrainers = Arrays.asList(
            4, 5, 6, 133, 134, 135, 136, 137, 138, 139, 147, 148, 149,
            150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160,
            164, 165, 169, 170, 171, 172, 173, 174, 175, 176, 177,
            178, 179, 180, 181, 182, 203, 204, 205, 206, 207, 208, 209, 210, 211,
            212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225,
            226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 237, 238, 239, 240,
            242, 243, 244, 245, 247, 248, 249, 250, 252, 253, 254, 255, 256, 257,
            258, 259, 260, 261, 262, 263, 264, 265, 266, 267, 268, 269, 270, 271,
            272, 273, 274, 275, 276, 277, 278, 279, 280, 281, 282, 283, 284, 285,
            286, 287, 288, 289, 290, 291, 292, 293, 294, 295, 296, 297, 298, 299,
            300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313,
            314, 315, 316, 317, 318, 319, 320, 321, 322, 323, 324, 325, 326, 327,
            328, 329, 330, 331, 332, 333, 334, 335, 336, 337, 338, 339, 340, 341,
            342, 343, 344, 345, 346, 347, 348, 349, 350, 351, 352, 353, 354, 355,
            356, 357, 358, 359, 360, 361, 362, 363, 364, 365, 366, 367,
            372, 373, 374, 375, 376, 377, 381, 382, 383,
            384, 385, 386, 387, 388, 389, 390, 391, 392, 426, 427, 428, 429, 430,
            431, 432, 433, 434, 435, 436, 437, 438, 439, 440, 441, 442, 443, 444,
            445, 446, 447, 448, 449, 450, 451, 452, 453, 454, 455, 461, 462, 463,
            464, 465, 466, 467, 468, 469, 470, 471, 472, 473, 474, 475, 476, 477,
            478, 479, 480, 481, 482, 483, 484, 485, 486, 497, 498, 499, 500, 501,
            502, 503, 510, 511, 512, 513, 514, 515, 516, 517, 518, 519, 520, 521,
            522, 523, 524, 537, 538, 539, 540, 541, 542, 543, 544, 545, 546, 547,
            548, 549, 550, 551, 552, 553, 554, 555, 556, 557, 558, 559, 560, 561,
            562, 563, 564, 565, 566, 567, 568, 569, 570, 580, 581, 583, 584, 585,
            586, 587, 592, 593, 594, 595, 596, 597, 598, 599, 600,
            601, 602, 603, 604, 605, 606, 607, 608, 609, 610, 611, 612, 613, 614,
            615, 621, 622, 623, 624, 625, 626, 627, 628, 629, 630, 631, 657, 658,
            659, 660, 661, 662, 663, 664, 665, 666, 667, 668, 669, 670, 671, 672,
            673, 679, 680, 681, 682, 683, 690, 691, 692, 703, 704,
            705, 712, 713, 714, 715, 716, 717, 718, 719, 720, 721, 722, 723, 724,
            725, 726, 727, 728, 729, 730, 731, 732, 733, 734, 735, 736, 737, 738,
            745, 746, 747, 748, 749, 750, 751, 752, 754, 755, 756, 763, 764, 765,
            766, 767, 768, 769, 770, 771, 772, 773, 774, 775, 776, 786, 787, 788,
            789, 797, 798, 799, 800, 801, 802, 803, 804, 805, 806,
            807, 808, 809, 810, 811, 812);
    
//    public static final Map<Integer, String> bw1ShopIndex = new HashMap<Integer, String>() {1:"Check"};

    public static final List<Integer> bw1MainGameShops = Arrays.asList(
            3, 5, 6, 8, 9, 12, 14, 17, 18, 19, 21, 22
    );

    public static final List<String> bw1ShopNames = Arrays.asList(
            "Primary 0 Badges",
            "Shopping Mall 9 TMs",
            "Icirrus Secondary (TMs)",
            "Driftveil Herb Salesman",
            "Mistralton Secondary (TMs)",
            "Shopping Mall 9 F3 Left",
            "Accumula Secondary",
            "Nimbasa Secondary (TMs)",
            "Striaton Secondary",
            "League Secondary",
            "Lacunosa Secondary",
            "Black City/White Forest Secondary",
            "Nacrene/Shopping Mall 9 X Items",
            "Driftveil Incense Salesman",
            "Nacrene Secondary",
            "Undella Secondary",
            "Primary 2 Badges",
            "Castelia Secondary",
            "Driftveil Secondary",
            "Opelucid Secondary",
            "Primary 3 Badges",
            "Shopping Mall 9 F1",
            "Shopping Mall 9 F2",
            "Primary 5 Badges",
            "Primary 7 Badges",
            "Primary 8 Badges");

    public static final List<Integer> bw2MainGameShops = Arrays.asList(
            9, 11, 14, 15, 16, 18, 20, 21, 22, 23, 25, 26, 27, 28, 29, 30, 31
    );
    
    public static final List<String> bw2ShopNames = Arrays.asList(
            "Primary 0 Badges",
            "Primary 1 Badges",
            "Primary 3 Badges",
            "Primary 5 Badges",
            "Primary 7 Badges",
            "Primary 8 Badges",
            "Accumula Secondary",
            "Striaton Secondary (TMs)",
            "Nacrene Secondary",
            "Castelia Secondary",
            "Nimbasa Secondary (TMs)",
            "Driftveil Secondary",
            "Mistralton Secondary (TMs)",
            "Icirrus Secondary",
            "Opelucid Secondary",
            "Victory Road Secondary",
            "Pokemon League Secondary",
            "Lacunosa Secondary (TMs)",
            "Undella Secondary",
            "Black City/White Forest Secondary",
            "Nacrene/Shopping Mall 9 X Items",
            "Driftveil Herb Salesman",
            "Driftveil Incense Salesman",
            "Shopping Mall 9 F1",
            "Shopping Mall 9 TMs",
            "Shopping Mall 9 F2",
            "Shopping Mall 9 F3 Left",
            "Aspertia Secondary",
            "Virbank Secondary",
            "Humilau Secondary",
            "Floccesy Secondary",
            "Lentimas Secondary");


    public static final List<Integer> evolutionItems = Arrays.asList(80,81,82,83,84,85,107,108,109,
            110,221,226,227,233,235,252,321,322,323,324,325,326,327,537);

    
    public static final List<Integer> bw1RequiredFieldTMs = Arrays.asList(2, 3, 5, 6, 9, 12, 13, 19,
            22, 24, 26, 29, 30, 35, 36, 39, 41, 46, 47, 50, 52, 53, 55, 58, 61, 63, 65, 66, 71, 80, 81, 84, 85, 86, 90,
            91, 92, 93);

    public static final List<Integer> bw2RequiredFieldTMs = Arrays.asList(1, 2, 3, 5, 6, 12, 13, 19,
            22, 26, 28, 29, 30, 36, 39, 41, 46, 47, 50, 52, 53, 56, 58, 61, 63, 65, 66, 67, 69, 71, 80, 81, 84, 85, 86,
            90, 91, 92, 93);

    public static final List<Integer> bw1EarlyRequiredHMMoves = Collections.singletonList(15);

    @SuppressWarnings("unchecked")
    public static final List<Integer> bw2EarlyRequiredHMMoves = Collections.EMPTY_LIST;

    public static final List<Integer> fieldMoves = Arrays.asList(15, 19, 57, 70, 148, 91, 100, 127, 230, 291);

    public static final String shedinjaFunctionLocator = "F8B582B0061C30680F1C";

    private static Type[] constructTypeTable() {
        Type[] table = new Type[256];
        table[0x00] = Type.NORMAL;
        table[0x01] = Type.FIGHTING;
        table[0x02] = Type.FLYING;
        table[0x03] = Type.POISON;
        table[0x04] = Type.GROUND;
        table[0x05] = Type.ROCK;
        table[0x06] = Type.BUG;
        table[0x07] = Type.GHOST;
        table[0x08] = Type.STEEL;
        table[0x09] = Type.FIRE;
        table[0x0A] = Type.WATER;
        table[0x0B] = Type.GRASS;
        table[0x0C] = Type.ELECTRIC;
        table[0x0D] = Type.PSYCHIC;
        table[0x0E] = Type.ICE;
        table[0x0F] = Type.DRAGON;
        table[0x10] = Type.DARK;
        return table;
    }

    public static byte typeToByte(Type type) {
        if (type == null) {
            return 0x00; // normal?
        }
        switch (type) {
        case NORMAL:
            return 0x00;
        case FIGHTING:
            return 0x01;
        case FLYING:
            return 0x02;
        case POISON:
            return 0x03;
        case GROUND:
            return 0x04;
        case ROCK:
            return 0x05;
        case BUG:
            return 0x06;
        case GHOST:
            return 0x07;
        case FIRE:
            return 0x09;
        case WATER:
            return 0x0A;
        case GRASS:
            return 0x0B;
        case ELECTRIC:
            return 0x0C;
        case PSYCHIC:
            return 0x0D;
        case ICE:
            return 0x0E;
        case DRAGON:
            return 0x0F;
        case STEEL:
            return 0x08;
        case DARK:
            return 0x10;
        default:
            return 0; // normal by default
        }
    }

    public static int getAreaDataEntryLength(int romType) {
        if (romType == Type_BW) {
            return bw1AreaDataEntryLength;
        } else if (romType == Type_BW2) {
            return bw2AreaDataEntryLength;
        }
        return 0;
    }

    public static int getEncounterAreaCount(int romType) {
        if (romType == Type_BW) {
            return bw1EncounterAreaCount;
        } else if (romType == Type_BW2) {
            return bw2EncounterAreaCount;
        }
        return 0;
    }

    public static int[] getWildFileToAreaMap(int romType) {
        if (romType == Type_BW) {
            return bw1WildFileToAreaMap;
        } else if (romType == Type_BW2) {
            return bw2WildFileToAreaMap;
        }
        return new int[0];
    }

    public static List<Integer> getMainGameShops(int romType) {
        if (romType == Type_BW) {
            return bw1MainGameShops;
        } else if (romType == Type_BW2) {
            return bw2MainGameShops;
        }
        return new ArrayList<>();
    }

    public static int getFormeCount(int romType) {
        if (romType == Type_BW) {
            return bw1FormeCount;
        } else if (romType == Type_BW2) {
            return bw2FormeCount;
        }
        return 0;
    }

    public static int getFormeOffset(int romType) {
        if (romType == Type_BW) {
            return bw1formeOffset;
        } else if (romType == Type_BW2) {
            return bw2formeOffset;
        }
        return 0;
    }

    public static int getNonPokemonBattleSpriteCount(int romType) {
        if (romType == Type_BW) {
            return bw1NonPokemonBattleSpriteCount;
        } else if (romType == Type_BW2) {
            return bw2NonPokemonBattleSpriteCount;
        }
        return 0;
    }

    public static String getFormeSuffix(int internalIndex, int romType) {
        if (romType == Type_BW) {
            return bw1FormeSuffixes.getOrDefault(internalIndex,"");
        } else if (romType == Type_BW2) {
            return bw2FormeSuffixes.getOrDefault(internalIndex,"");
        } else {
            return "";
        }
    }

    private static Map<Integer,String> setupFormeSuffixes(int gameVersion) {
        Map<Integer,String> formeSuffixes = new HashMap<>();
        if (gameVersion == Gen5Constants.Type_BW) {
            formeSuffixes.put(650,"-A"); // Deoxys-A
            formeSuffixes.put(651,"-D"); // Deoxys-D
            formeSuffixes.put(652,"-S"); // Deoxys-S
            formeSuffixes.put(653,"-S"); // Wormadam-S
            formeSuffixes.put(654,"-T"); // Wormadam-T
            formeSuffixes.put(655,"-S"); // Shaymin-S
            formeSuffixes.put(656,"-O"); // Giratina-O
            formeSuffixes.put(657,"-H"); // Rotom-H
            formeSuffixes.put(658,"-W"); // Rotom-W
            formeSuffixes.put(659,"-Fr"); // Rotom-Fr
            formeSuffixes.put(660,"-Fa"); // Rotom-Fa
            formeSuffixes.put(661,"-M"); // Rotom-M
            formeSuffixes.put(662,"-F"); // Castform-F
            formeSuffixes.put(663,"-W"); // Castform-W
            formeSuffixes.put(664,"-I"); // Castform-I
            formeSuffixes.put(665,"-B"); // Basculin-B
            formeSuffixes.put(666,"-Z"); // Darmanitan-Z
            formeSuffixes.put(667,"-P"); // Meloetta-P
        } else if (gameVersion == Gen5Constants.Type_BW2) {
            formeSuffixes.put(685,"-A"); // Deoxys-A
            formeSuffixes.put(686,"-D"); // Deoxys-D
            formeSuffixes.put(687,"-S"); // Deoxys-S
            formeSuffixes.put(688,"-S"); // Wormadam-S
            formeSuffixes.put(689,"-T"); // Wormadam-T
            formeSuffixes.put(690,"-S"); // Shaymin-S
            formeSuffixes.put(691,"-O"); // Giratina-O
            formeSuffixes.put(692,"-H"); // Rotom-H
            formeSuffixes.put(693,"-W"); // Rotom-W
            formeSuffixes.put(694,"-Fr"); // Rotom-Fr
            formeSuffixes.put(695,"-Fa"); // Rotom-Fa
            formeSuffixes.put(696,"-M"); // Rotom-M
            formeSuffixes.put(697,"-F"); // Castform-F
            formeSuffixes.put(698,"-W"); // Castform-W
            formeSuffixes.put(699,"-I"); // Castform-I
            formeSuffixes.put(700,"-B"); // Basculin-B
            formeSuffixes.put(701,"-Z"); // Darmanitan-Z
            formeSuffixes.put(702,"-P"); // Meloetta-P
            formeSuffixes.put(703,"-W"); // Kyurem-W
            formeSuffixes.put(704,"-B"); // Kyurem-B
            formeSuffixes.put(705,"-R"); // Keldeo-R
            formeSuffixes.put(706,"-T"); // Tornadus-T
            formeSuffixes.put(707,"-T"); // Thundurus-T
            formeSuffixes.put(708,"-T"); // Landorus-T
        }

        return formeSuffixes;
    }

    private static Map<Integer,Map<Integer,String>> setupFormeSuffixesByBaseForme() {
        Map<Integer,Map<Integer,String>> map = new HashMap<>();

        Map<Integer,String> deoxysMap = new HashMap<>();
        deoxysMap.put(1,"-A");
        deoxysMap.put(2,"-D");
        deoxysMap.put(3,"-S");
        map.put(386,deoxysMap);

        Map<Integer,String> wormadamMap = new HashMap<>();
        wormadamMap.put(1,"-S");
        wormadamMap.put(2,"-T");
        map.put(413,wormadamMap);

        Map<Integer,String> shayminMap = new HashMap<>();
        shayminMap.put(1,"-S");
        map.put(492,shayminMap);

        Map<Integer,String> giratinaMap = new HashMap<>();
        giratinaMap.put(1,"-O");
        map.put(487,giratinaMap);

        Map<Integer,String> rotomMap = new HashMap<>();
        rotomMap.put(1,"-H");
        rotomMap.put(2,"-W");
        rotomMap.put(3,"-Fr");
        rotomMap.put(4,"-Fa");
        rotomMap.put(5,"-M");
        map.put(479,rotomMap);

        Map<Integer,String> castformMap = new HashMap<>();
        castformMap.put(1,"-F");
        castformMap.put(2,"-W");
        castformMap.put(3,"-I");
        map.put(351,castformMap);

        Map<Integer,String> basculinMap = new HashMap<>();
        basculinMap.put(1,"-B");
        map.put(550,basculinMap);

        Map<Integer,String> darmanitanMap = new HashMap<>();
        darmanitanMap.put(1,"-Z");
        map.put(555,darmanitanMap);

        Map<Integer,String> meloettaMap = new HashMap<>();
        meloettaMap.put(1,"-P");
        map.put(648,meloettaMap);

        Map<Integer,String> kyuremMap = new HashMap<>();
        kyuremMap.put(1,"-W");
        kyuremMap.put(2,"-B");
        map.put(646,kyuremMap);

        Map<Integer,String> keldeoMap = new HashMap<>();
        keldeoMap.put(1,"-R");
        map.put(647,keldeoMap);

        Map<Integer,String> tornadusMap = new HashMap<>();
        tornadusMap.put(1,"-T");
        map.put(641,tornadusMap);

        Map<Integer,String> thundurusMap = new HashMap<>();
        thundurusMap.put(1,"-T");
        map.put(642,thundurusMap);

        Map<Integer,String> landorusMap = new HashMap<>();
        landorusMap.put(1,"-T");
        map.put(645,landorusMap);

        return map;
    }

    private static Map<Integer,String> setupDummyFormeSuffixes() {
        Map<Integer,String> m = new HashMap<>();
        m.put(0,"");
        return m;
    }

    private static Map<Integer,Map<Integer,Integer>> setupAbsolutePokeNumsByBaseForme() {

        Map<Integer,Map<Integer,Integer>> map = new HashMap<>();

        Map<Integer,Integer> deoxysMap = new HashMap<>();
        deoxysMap.put(1,650);
        deoxysMap.put(2,651);
        deoxysMap.put(3,652);
        map.put(386,deoxysMap);

        Map<Integer,Integer> wormadamMap = new HashMap<>();
        wormadamMap.put(1,653);
        wormadamMap.put(2,654);
        map.put(413,wormadamMap);

        Map<Integer,Integer> shayminMap = new HashMap<>();
        shayminMap.put(1,655);
        map.put(492,shayminMap);

        Map<Integer,Integer> giratinaMap = new HashMap<>();
        giratinaMap.put(1,656);
        map.put(487,giratinaMap);

        Map<Integer,Integer> rotomMap = new HashMap<>();
        rotomMap.put(1,657);
        rotomMap.put(2,658);
        rotomMap.put(3,659);
        rotomMap.put(4,660);
        rotomMap.put(5,661);
        map.put(479,rotomMap);

        Map<Integer,Integer> castformMap = new HashMap<>();
        castformMap.put(1,662);
        castformMap.put(2,663);
        castformMap.put(3,664);
        map.put(351,castformMap);

        Map<Integer,Integer> basculinMap = new HashMap<>();
        basculinMap.put(1,665);
        map.put(550,basculinMap);

        Map<Integer,Integer> darmanitanMap = new HashMap<>();
        darmanitanMap.put(1,666);
        map.put(555,darmanitanMap);

        Map<Integer,Integer> meloettaMap = new HashMap<>();
        meloettaMap.put(1,667);
        map.put(648,meloettaMap);

        Map<Integer,Integer> kyuremMap = new HashMap<>();
        kyuremMap.put(1,668);
        kyuremMap.put(2,669);
        map.put(646,kyuremMap);

        Map<Integer,Integer> keldeoMap = new HashMap<>();
        keldeoMap.put(1,670);
        map.put(647,keldeoMap);

        Map<Integer,Integer> tornadusMap = new HashMap<>();
        tornadusMap.put(1,671);
        map.put(641,tornadusMap);

        Map<Integer,Integer> thundurusMap = new HashMap<>();
        thundurusMap.put(1,672);
        map.put(642,thundurusMap);

        Map<Integer,Integer> landorusMap = new HashMap<>();
        landorusMap.put(1,673);
        map.put(645,landorusMap);

        return map;
    }

    private static Map<Integer,Integer> setupDummyAbsolutePokeNums() {
        Map<Integer,Integer> m = new HashMap<>();
        m.put(255,0);
        return m;
    }

    public static ItemList allowedItems, nonBadItems;
    public static List<Integer> regularShopItems, opShopItems;

    public static String blackBoxLegendaryCheckPrefix1 = "79F6BAEF07B0F0BDC046", blackBoxLegendaryCheckPrefix2 = "DEDB0020C04302B0F8BDC046",
        whiteBoxLegendaryCheckPrefix1 = "00F0FEF8002070BD", whiteBoxLegendaryCheckPrefix2 = "64F62EF970BD0000";

    static {
        setupAllowedItems();
    }

    private static void setupAllowedItems() {
        allowedItems = new ItemList(638);
        // Key items + version exclusives
        allowedItems.banRange(428, 76);
        allowedItems.banRange(505,32);
        allowedItems.banRange(621, 18);
        allowedItems.banSingles(574, 578, 579, 616, 617);
        // Unknown blank items or version exclusives
        allowedItems.banRange(113, 3);
        allowedItems.banRange(120, 14);
        // TMs & HMs - tms cant be held in gen5
        allowedItems.tmRange(328, 92);
        allowedItems.tmRange(618, 3);
        allowedItems.banRange(328, 100);
        allowedItems.banRange(618, 3);
        // Battle Launcher exclusives
        allowedItems.banRange(592, 24);

        // non-bad items
        // ban specific pokemon hold items, berries, apricorns, mail
        nonBadItems = allowedItems.copy();

        nonBadItems.banSingles(0x6F, 0x70, 0xE1, 0xEC, 0x9B, 0x112, 0x23F);
        nonBadItems.banRange(0x5F, 4); // mulch
        nonBadItems.banRange(0x87, 2); // orbs
        nonBadItems.banRange(0x89, 12); // mails
        nonBadItems.banRange(0x9F, 25); // berries without useful battle effects
        nonBadItems.banRange(0x100, 4); // pokemon specific
        nonBadItems.banRange(0x104, 5); // contest scarves

        regularShopItems = new ArrayList<>();

        regularShopItems.addAll(IntStream.rangeClosed(2,4).boxed().collect(Collectors.toList()));
        regularShopItems.addAll(IntStream.rangeClosed(0x11,0x1C).boxed().collect(Collectors.toList()));
        regularShopItems.addAll(IntStream.rangeClosed(0x4C,0x4F).boxed().collect(Collectors.toList()));

        opShopItems = new ArrayList<>();

        // "Money items" etc
        opShopItems.add(0x2A);
        opShopItems.add(0x2B);
        opShopItems.add(0x32);
        opShopItems.add(0x36);
        opShopItems.addAll(IntStream.rangeClosed(0x41,0x47).boxed().collect(Collectors.toList()));
        opShopItems.addAll(IntStream.rangeClosed(0x56,0x5C).boxed().collect(Collectors.toList()));
        opShopItems.add(0x6A);
        opShopItems.addAll(IntStream.rangeClosed(0xCE,0xD4).boxed().collect(Collectors.toList()));
        opShopItems.add(0xE7);
        opShopItems.add(0x23B);
        opShopItems.addAll(IntStream.rangeClosed(0x244,0x24F).boxed().collect(Collectors.toList()));
    }

    public static final Map<Integer,Integer> balancedItemPrices = Stream.of(new Integer[][] {
            // Skip item index 0. All prices divided by 10
            {1, 300}, // Master Ball
            {2, 120}, // Ultra Ball
            {3, 60}, // Great Ball
            {4, 20}, // Poké Ball
            {5, 50}, // Safari Ball
            {6, 100}, // Net Ball
            {7, 100}, // Dive Ball
            {8, 100}, // Nest Ball
            {9, 100}, // Repeat Ball
            {10, 100}, // Timer Ball
            {11, 100}, // Luxury Ball
            {12, 20}, // Premier Ball
            {13, 100}, // Dusk Ball
            {14, 30}, // Heal Ball
            {15, 100}, // Quick Ball
            {16, 20}, // Cherish Ball
            {17, 30}, // Potion
            {18, 10}, // Antidote
            {19, 25}, // Burn Heal
            {20, 25}, // Ice Heal
            {21, 25}, // Awakening
            {22, 20}, // Parlyz Heal
            {23, 300}, // Full Restore
            {24, 250}, // Max Potion
            {25, 120}, // Hyper Potion
            {26, 70}, // Super Potion
            {27, 60}, // Full Heal
            {28, 150}, // Revive
            {29, 400}, // Max Revive
            {30, 40}, // Fresh Water
            {31, 60}, // Soda Pop
            {32, 70}, // Lemonade
            {33, 80}, // Moomoo Milk
            {34, 40}, // EnergyPowder
            {35, 110}, // Energy Root
            {36, 45}, // Heal Powder
            {37, 280}, // Revival Herb
            {38, 300}, // Ether
            {39, 450}, // Max Ether
            {40, 1500}, // Elixir
            {41, 1800}, // Max Elixir
            {42, 45}, // Lava Cookie
            {43, 10}, // Berry Juice
            {44, 1000}, // Sacred Ash
            {45, 980}, // HP Up
            {46, 980}, // Protein
            {47, 980}, // Iron
            {48, 980}, // Carbos
            {49, 980}, // Calcium
            {50, 1000}, // Rare Candy
            {51, 980}, // PP Up
            {52, 980}, // Zinc
            {53, 2490}, // PP Max
            {54, 45}, // Old Gateau
            {55, 70}, // Guard Spec.
            {56, 65}, // Dire Hit
            {57, 50}, // X Attack
            {58, 55}, // X Defend
            {59, 35}, // X Speed
            {60, 95}, // X Accuracy
            {61, 35}, // X Special
            {62, 35}, // X Sp. Def
            {63, 100}, // Poké Doll
            {64, 100}, // Fluffy Tail
            {65, 2}, // Blue Flute
            {66, 2}, // Yellow Flute
            {67, 2}, // Red Flute
            {68, 2}, // Black Flute
            {69, 2}, // White Flute
            {70, 2}, // Shoal Salt
            {71, 2}, // Shoal Shell
            {72, 40}, // Red Shard
            {73, 40}, // Blue Shard
            {74, 40}, // Yellow Shard
            {75, 40}, // Green Shard
            {76, 50}, // Super Repel
            {77, 70}, // Max Repel
            {78, 55}, // Escape Rope
            {79, 35}, // Repel
            {80, 300}, // Sun Stone
            {81, 300}, // Moon Stone
            {82, 300}, // Fire Stone
            {83, 300}, // Thunderstone
            {84, 300}, // Water Stone
            {85, 300}, // Leaf Stone
            {86, 50}, // TinyMushroom
            {87, 500}, // Big Mushroom
            {88, 140}, // Pearl
            {89, 750}, // Big Pearl
            {90, 200}, // Stardust
            {91, 980}, // Star Piece
            {92, 1000}, // Nugget
            {93, 500}, // Heart Scale
            {94, 50}, // Honey
            {95, 20}, // Growth Mulch
            {96, 20}, // Damp Mulch
            {97, 20}, // Stable Mulch
            {98, 20}, // Gooey Mulch
            {99, 500}, // Root Fossil
            {100, 500}, // Claw Fossil
            {101, 500}, // Helix Fossil
            {102, 500}, // Dome Fossil
            {103, 800}, // Old Amber
            {104, 500}, // Armor Fossil
            {105, 500}, // Skull Fossil
            {106, 1000}, // Rare Bone
            {107, 300}, // Shiny Stone
            {108, 300}, // Dusk Stone
            {109, 300}, // Dawn Stone
            {110, 300}, // Oval Stone
            {111, 210}, // Odd Keystone
            {112, 1000}, // Griseous Orb
            {113, 0}, // unknown
            {114, 0}, // unknown
            {115, 0}, // unknown
            {116, 100}, // Douse Drive
            {117, 100}, // Shock Drive
            {118, 100}, // Burn Drive
            {119, 100}, // Chill Drive
            {120, 0}, // unknown
            {121, 0}, // unknown
            {122, 0}, // unknown
            {123, 0}, // unknown
            {124, 0}, // unknown
            {125, 0}, // unknown
            {126, 0}, // unknown
            {127, 0}, // unknown
            {128, 0}, // unknown
            {129, 0}, // unknown
            {130, 0}, // unknown
            {131, 0}, // unknown
            {132, 0}, // unknown
            {133, 0}, // unknown
            {134, 15}, // Sweet Heart
            {135, 1000}, // Adamant Orb
            {136, 1000}, // Lustrous Orb
            {137, 5}, // Greet Mail
            {138, 5}, // Favored Mail
            {139, 5}, // RSVP Mail
            {140, 5}, // Thanks Mail
            {141, 5}, // Inquiry Mail
            {142, 5}, // Like Mail
            {143, 5}, // Reply Mail
            {144, 5}, // BridgeMail S
            {145, 5}, // BridgeMail D
            {146, 5}, // BridgeMail T
            {147, 5}, // BridgeMail V
            {148, 5}, // BridgeMail M
            {149, 20}, // Cheri Berry
            {150, 25}, // Chesto Berry
            {151, 10}, // Pecha Berry
            {152, 25}, // Rawst Berry
            {153, 25}, // Aspear Berry
            {154, 300}, // Leppa Berry
            {155, 5}, // Oran Berry
            {156, 20}, // Persim Berry
            {157, 50}, // Lum Berry
            {158, 50}, // Sitrus Berry
            {159, 10}, // Figy Berry
            {160, 10}, // Wiki Berry
            {161, 10}, // Mago Berry
            {162, 10}, // Aguav Berry
            {163, 10}, // Iapapa Berry
            {164, 50}, // Razz Berry
            {165, 50}, // Bluk Berry
            {166, 50}, // Nanab Berry
            {167, 50}, // Wepear Berry
            {168, 50}, // Pinap Berry
            {169, 50}, // Pomeg Berry
            {170, 50}, // Kelpsy Berry
            {171, 50}, // Qualot Berry
            {172, 50}, // Hondew Berry
            {173, 50}, // Grepa Berry
            {174, 50}, // Tamato Berry
            {175, 50}, // Cornn Berry
            {176, 50}, // Magost Berry
            {177, 50}, // Rabuta Berry
            {178, 50}, // Nomel Berry
            {179, 50}, // Spelon Berry
            {180, 50}, // Pamtre Berry
            {181, 50}, // Watmel Berry
            {182, 50}, // Durin Berry
            {183, 50}, // Belue Berry
            {184, 100}, // Occa Berry
            {185, 100}, // Passho Berry
            {186, 100}, // Wacan Berry
            {187, 100}, // Rindo Berry
            {188, 100}, // Yache Berry
            {189, 100}, // Chople Berry
            {190, 100}, // Kebia Berry
            {191, 100}, // Shuca Berry
            {192, 100}, // Coba Berry
            {193, 100}, // Payapa Berry
            {194, 100}, // Tanga Berry
            {195, 100}, // Charti Berry
            {196, 100}, // Kasib Berry
            {197, 100}, // Haban Berry
            {198, 100}, // Colbur Berry
            {199, 100}, // Babiri Berry
            {200, 100}, // Chilan Berry
            {201, 100}, // Liechi Berry
            {202, 100}, // Ganlon Berry
            {203, 100}, // Salac Berry
            {204, 100}, // Petaya Berry
            {205, 100}, // Apicot Berry
            {206, 100}, // Lansat Berry
            {207, 100}, // Starf Berry
            {208, 100}, // Enigma Berry
            {209, 100}, // Micle Berry
            {210, 100}, // Custap Berry
            {211, 100}, // Jaboca Berry
            {212, 100}, // Rowap Berry
            {213, 300}, // BrightPowder
            {214, 100}, // White Herb
            {215, 300}, // Macho Brace
            {216, 600}, // Exp. Share
            {217, 450}, // Quick Claw
            {218, 100}, // Soothe Bell
            {219, 100}, // Mental Herb
            {220, 1000}, // Choice Band
            {221, 500}, // King's Rock
            {222, 200}, // SilverPowder
            {223, 1500}, // Amulet Coin
            {224, 100}, // Cleanse Tag
            {225, 20}, // Soul Dew
            {226, 300}, // DeepSeaTooth
            {227, 300}, // DeepSeaScale
            {228, 20}, // Smoke Ball
            {229, 20}, // Everstone
            {230, 300}, // Focus Band
            {231, 1000}, // Lucky Egg
            {232, 500}, // Scope Lens
            {233, 300}, // Metal Coat
            {234, 1000}, // Leftovers
            {235, 300}, // Dragon Scale
            {236, 10}, // Light Ball
            {237, 200}, // Soft Sand
            {238, 200}, // Hard Stone
            {239, 200}, // Miracle Seed
            {240, 200}, // BlackGlasses
            {241, 200}, // Black Belt
            {242, 200}, // Magnet
            {243, 200}, // Mystic Water
            {244, 200}, // Sharp Beak
            {245, 200}, // Poison Barb
            {246, 200}, // NeverMeltIce
            {247, 200}, // Spell Tag
            {248, 200}, // TwistedSpoon
            {249, 200}, // Charcoal
            {250, 200}, // Dragon Fang
            {251, 200}, // Silk Scarf
            {252, 300}, // Up-Grade
            {253, 600}, // Shell Bell
            {254, 200}, // Sea Incense
            {255, 300}, // Lax Incense
            {256, 1}, // Lucky Punch
            {257, 1}, // Metal Powder
            {258, 50}, // Thick Club
            {259, 20}, // Stick
            {260, 10}, // Red Scarf
            {261, 10}, // Blue Scarf
            {262, 10}, // Pink Scarf
            {263, 10}, // Green Scarf
            {264, 10}, // Yellow Scarf
            {265, 150}, // Wide Lens
            {266, 200}, // Muscle Band
            {267, 200}, // Wise Glasses
            {268, 600}, // Expert Belt
            {269, 150}, // Light Clay
            {270, 1000}, // Life Orb
            {271, 100}, // Power Herb
            {272, 150}, // Toxic Orb
            {273, 150}, // Flame Orb
            {274, 1}, // Quick Powder
            {275, 200}, // Focus Sash
            {276, 150}, // Zoom Lens
            {277, 300}, // Metronome
            {278, 100}, // Iron Ball
            {279, 100}, // Lagging Tail
            {280, 150}, // Destiny Knot
            {281, 500}, // Black Sludge
            {282, 20}, // Icy Rock
            {283, 20}, // Smooth Rock
            {284, 20}, // Heat Rock
            {285, 20}, // Damp Rock
            {286, 150}, // Grip Claw
            {287, 1000}, // Choice Scarf
            {288, 150}, // Sticky Barb
            {289, 300}, // Power Bracer
            {290, 300}, // Power Belt
            {291, 300}, // Power Lens
            {292, 300}, // Power Band
            {293, 300}, // Power Anklet
            {294, 300}, // Power Weight
            {295, 50}, // Shed Shell
            {296, 150}, // Big Root
            {297, 1000}, // Choice Specs
            {298, 200}, // Flame Plate
            {299, 200}, // Splash Plate
            {300, 200}, // Zap Plate
            {301, 200}, // Meadow Plate
            {302, 200}, // Icicle Plate
            {303, 200}, // Fist Plate
            {304, 200}, // Toxic Plate
            {305, 200}, // Earth Plate
            {306, 200}, // Sky Plate
            {307, 200}, // Mind Plate
            {308, 200}, // Insect Plate
            {309, 200}, // Stone Plate
            {310, 200}, // Spooky Plate
            {311, 200}, // Draco Plate
            {312, 200}, // Dread Plate
            {313, 200}, // Iron Plate
            {314, 200}, // Odd Incense
            {315, 200}, // Rock Incense
            {316, 100}, // Full Incense
            {317, 200}, // Wave Incense
            {318, 200}, // Rose Incense
            {319, 1500}, // Luck Incense
            {320, 100}, // Pure Incense
            {321, 300}, // Protector
            {322, 300}, // Electirizer
            {323, 300}, // Magmarizer
            {324, 300}, // Dubious Disc
            {325, 300}, // Reaper Cloth
            {326, 500}, // Razor Claw
            {327, 500}, // Razor Fang
            {328, 1000}, // TM01 Hone Claws
            {329, 1000}, // TM02 Dragon Claw
            {330, 1000}, // TM03 Psyshock
            {331, 1000}, // TM04 Calm Mind
            {332, 1000}, // TM05 Roar
            {333, 1000}, // TM06 Toxic
            {334, 2000}, // TM07 Hail
            {335, 1000}, // TM08 Bulk Up
            {336, 1000}, // TM09 Venoshock
            {337, 1000}, // TM10 Hidden Power
            {338, 2000}, // TM11 Sunny Day
            {339, 1000}, // TM12 Taunt
            {340, 1000}, // TM13 Ice Beam
            {341, 2000}, // TM14 Blizzard
            {342, 2000}, // TM15 Hyper Beam
            {343, 2000}, // TM16 Light Screen
            {344, 1000}, // TM17 Protect
            {345, 2000}, // TM18 Rain Dance
            {346, 1000}, // TM19 Telekinesis
            {347, 2000}, // TM20 Safeguard
            {348, 1000}, // TM21 Frustration
            {349, 1000}, // TM22 SolarBeam
            {350, 1000}, // TM23 Smack Down
            {351, 1000}, // TM24 Thunderbolt
            {352, 2000}, // TM25 Thunder
            {353, 1000}, // TM26 Earthquake
            {354, 1000}, // TM27 Return
            {355, 1000}, // TM28 Dig
            {356, 1000}, // TM29 Psychic
            {357, 1000}, // TM30 Shadow Ball
            {358, 1000}, // TM31 Brick Break
            {359, 1000}, // TM32 Double Team
            {360, 2000}, // TM33 Reflect
            {361, 1000}, // TM34 Sludge Wave
            {362, 1000}, // TM35 Flamethrower
            {363, 1000}, // TM36 Sludge Bomb
            {364, 2000}, // TM37 Sandstorm
            {365, 2000}, // TM38 Fire Blast
            {366, 1000}, // TM39 Rock Tomb
            {367, 1000}, // TM40 Aerial Ace
            {368, 1000}, // TM41 Torment
            {369, 1000}, // TM42 Facade
            {370, 1000}, // TM43 Flame Charge
            {371, 1000}, // TM44 Rest
            {372, 1000}, // TM45 Attract
            {373, 1000}, // TM46 Thief
            {374, 1000}, // TM47 Low Sweep
            {375, 1000}, // TM48 Round
            {376, 1000}, // TM49 Echoed Voice
            {377, 1000}, // TM50 Overheat
            {378, 1000}, // TM51 Ally Switch
            {379, 1000}, // TM52 Focus Blast
            {380, 1000}, // TM53 Energy Ball
            {381, 1000}, // TM54 False Swipe
            {382, 1000}, // TM55 Scald
            {383, 1000}, // TM56 Fling
            {384, 1000}, // TM57 Charge Beam
            {385, 1000}, // TM58 Sky Drop
            {386, 1000}, // TM59 Incinerate
            {387, 1000}, // TM60 Quash
            {388, 1000}, // TM61 Will-O-Wisp
            {389, 1000}, // TM62 Acrobatics
            {390, 1000}, // TM63 Embargo
            {391, 1000}, // TM64 Explosion
            {392, 1000}, // TM65 Shadow Claw
            {393, 1000}, // TM66 Payback
            {394, 1000}, // TM67 Retaliate
            {395, 2000}, // TM68 Giga Impact
            {396, 1000}, // TM69 Rock Polish
            {397, 1000}, // TM70 Flash
            {398, 1000}, // TM71 Stone Edge
            {399, 1000}, // TM72 Volt Switch
            {400, 1000}, // TM73 Thunder Wave
            {401, 1000}, // TM74 Gyro Ball
            {402, 1000}, // TM75 Swords Dance
            {403, 1000}, // TM76 Struggle Bug
            {404, 1000}, // TM77 Psych Up
            {405, 1000}, // TM78 Bulldoze
            {406, 1000}, // TM79 Frost Breath
            {407, 1000}, // TM80 Rock Slide
            {408, 1000}, // TM81 X-Scissor
            {409, 1000}, // TM82 Dragon Tail
            {410, 1000}, // TM83 Work Up
            {411, 1000}, // TM84 Poison Jab
            {412, 1000}, // TM85 Dream Eater
            {413, 1000}, // TM86 Grass Knot
            {414, 1000}, // TM87 Swagger
            {415, 1000}, // TM88 Pluck
            {416, 1000}, // TM89 U-turn
            {417, 1000}, // TM90 Substitute
            {418, 1000}, // TM91 Flash Cannon
            {419, 1000}, // TM92 Trick Room
            {420, 0}, // HM01
            {421, 0}, // HM02
            {422, 0}, // HM03
            {423, 0}, // HM04
            {424, 0}, // HM05
            {425, 0}, // HM06
            {426, 0}, // unknown
            {427, 0}, // unknown
            {428, 0}, // Explorer Kit
            {429, 0}, // Loot Sack
            {430, 0}, // Rule Book
            {431, 0}, // Poké Radar
            {432, 0}, // Point Card
            {433, 0}, // Journal
            {434, 0}, // Seal Case
            {435, 0}, // Fashion Case
            {436, 0}, // Seal Bag
            {437, 0}, // Pal Pad
            {438, 0}, // Works Key
            {439, 0}, // Old Charm
            {440, 0}, // Galactic Key
            {441, 0}, // Red Chain
            {442, 0}, // Town Map
            {443, 0}, // Vs. Seeker
            {444, 0}, // Coin Case
            {445, 0}, // Old Rod
            {446, 0}, // Good Rod
            {447, 0}, // Super Rod
            {448, 0}, // Sprayduck
            {449, 0}, // Poffin Case
            {450, 0}, // Bicycle
            {451, 0}, // Suite Key
            {452, 0}, // Oak's Letter
            {453, 0}, // Lunar Wing
            {454, 0}, // Member Card
            {455, 0}, // Azure Flute
            {456, 0}, // S.S. Ticket
            {457, 0}, // Contest Pass
            {458, 0}, // Magma Stone
            {459, 0}, // Parcel
            {460, 0}, // Coupon 1
            {461, 0}, // Coupon 2
            {462, 0}, // Coupon 3
            {463, 0}, // Storage Key
            {464, 0}, // SecretPotion
            {465, 0}, // Vs. Recorder
            {466, 0}, // Gracidea
            {467, 0}, // Secret Key
            {468, 0}, // Apricorn Box
            {469, 0}, // Unown Report
            {470, 0}, // Berry Pots
            {471, 0}, // Dowsing MCHN
            {472, 0}, // Blue Card
            {473, 0}, // SlowpokeTail
            {474, 0}, // Clear Bell
            {475, 0}, // Card Key
            {476, 0}, // Basement Key
            {477, 0}, // SquirtBottle
            {478, 0}, // Red Scale
            {479, 0}, // Lost Item
            {480, 0}, // Pass
            {481, 0}, // Machine Part
            {482, 0}, // Silver Wing
            {483, 0}, // Rainbow Wing
            {484, 0}, // Mystery Egg
            {485, 2}, // Red Apricorn
            {486, 2}, // Blu Apricorn
            {487, 2}, // Ylw Apricorn
            {488, 2}, // Grn Apricorn
            {489, 2}, // Pnk Apricorn
            {490, 2}, // Wht Apricorn
            {491, 2}, // Blk Apricorn
            {492, 30}, // Fast Ball
            {493, 30}, // Level Ball
            {494, 30}, // Lure Ball
            {495, 30}, // Heavy Ball
            {496, 30}, // Love Ball
            {497, 30}, // Friend Ball
            {498, 30}, // Moon Ball
            {499, 30}, // Sport Ball
            {500, 0}, // Park Ball
            {501, 0}, // Photo Album
            {502, 0}, // GB Sounds
            {503, 0}, // Tidal Bell
            {504, 1500}, // RageCandyBar
            {505, 0}, // Data Card 01
            {506, 0}, // Data Card 02
            {507, 0}, // Data Card 03
            {508, 0}, // Data Card 04
            {509, 0}, // Data Card 05
            {510, 0}, // Data Card 06
            {511, 0}, // Data Card 07
            {512, 0}, // Data Card 08
            {513, 0}, // Data Card 09
            {514, 0}, // Data Card 10
            {515, 0}, // Data Card 11
            {516, 0}, // Data Card 12
            {517, 0}, // Data Card 13
            {518, 0}, // Data Card 14
            {519, 0}, // Data Card 15
            {520, 0}, // Data Card 16
            {521, 0}, // Data Card 17
            {522, 0}, // Data Card 18
            {523, 0}, // Data Card 19
            {524, 0}, // Data Card 20
            {525, 0}, // Data Card 21
            {526, 0}, // Data Card 22
            {527, 0}, // Data Card 23
            {528, 0}, // Data Card 24
            {529, 0}, // Data Card 25
            {530, 0}, // Data Card 26
            {531, 0}, // Data Card 27
            {532, 0}, // Jade Orb
            {533, 0}, // Lock Capsule
            {534, 0}, // Red Orb
            {535, 0}, // Blue Orb
            {536, 0}, // Enigma Stone
            {537, 300}, // Prism Scale
            {538, 1000}, // Eviolite
            {539, 100}, // Float Stone
            {540, 600}, // Rocky Helmet
            {541, 100}, // Air Balloon
            {542, 100}, // Red Card
            {543, 100}, // Ring Target
            {544, 200}, // Binding Band
            {545, 100}, // Absorb Bulb
            {546, 100}, // Cell Battery
            {547, 100}, // Eject Button
            {548, 100}, // Fire Gem
            {549, 100}, // Water Gem
            {550, 100}, // Electric Gem
            {551, 100}, // Grass Gem
            {552, 100}, // Ice Gem
            {553, 100}, // Fighting Gem
            {554, 100}, // Poison Gem
            {555, 100}, // Ground Gem
            {556, 100}, // Flying Gem
            {557, 100}, // Psychic Gem
            {558, 100}, // Bug Gem
            {559, 100}, // Rock Gem
            {560, 100}, // Ghost Gem
            {561, 100}, // Dragon Gem
            {562, 100}, // Dark Gem
            {563, 100}, // Steel Gem
            {564, 100}, // Normal Gem
            {565, 300}, // Health Wing
            {566, 300}, // Muscle Wing
            {567, 300}, // Resist Wing
            {568, 300}, // Genius Wing
            {569, 300}, // Clever Wing
            {570, 300}, // Swift Wing
            {571, 20}, // Pretty Wing
            {572, 500}, // Cover Fossil
            {573, 500}, // Plume Fossil
            {574, 0}, // Liberty Pass
            {575, 20}, // Pass Orb
            {576, 100}, // Dream Ball
            {577, 100}, // Poké Toy
            {578, 0}, // Prop Case
            {579, 0}, // Dragon Skull
            {580, 0}, // BalmMushroom
            {581, 0}, // Big Nugget
            {582, 0}, // Pearl String
            {583, 0}, // Comet Shard
            {584, 0}, // Relic Copper
            {585, 0}, // Relic Silver
            {586, 0}, // Relic Gold
            {587, 0}, // Relic Vase
            {588, 0}, // Relic Band
            {589, 0}, // Relic Statue
            {590, 0}, // Relic Crown
            {591, 45}, // Casteliacone
            {592, 0}, // Dire Hit 2
            {593, 0}, // X Speed 2
            {594, 0}, // X Special 2
            {595, 0}, // X Sp. Def 2
            {596, 0}, // X Defend 2
            {597, 0}, // X Attack 2
            {598, 0}, // X Accuracy 2
            {599, 0}, // X Speed 3
            {600, 0}, // X Special 3
            {601, 0}, // X Sp. Def 3
            {602, 0}, // X Defend 3
            {603, 0}, // X Attack 3
            {604, 0}, // X Accuracy 3
            {605, 0}, // X Speed 6
            {606, 0}, // X Special 6
            {607, 0}, // X Sp. Def 6
            {608, 0}, // X Defend 6
            {609, 0}, // X Attack 6
            {610, 0}, // X Accuracy 6
            {611, 0}, // Ability Urge
            {612, 0}, // Item Drop
            {613, 0}, // Item Urge
            {614, 0}, // Reset Urge
            {615, 0}, // Dire Hit 3
            {616, 0}, // Light Stone
            {617, 0}, // Dark Stone
            {618, 1000}, // TM93 Wild Charge
            {619, 1000}, // TM94 Rock Smash
            {620, 1000}, // TM95 Snarl
            {621, 0}, // Xtransceiver
            {622, 0}, // God Stone
            {623, 0}, // Gram 1
            {624, 0}, // Gram 2
            {625, 0}, // Gram 3
            {626, 0}, // Xtransceiver
            {627, 0}, // Medal Box
            {628, 0}, // DNA Splicers
            {629, 0}, // DNA Splicers
            {630, 0}, // Permit
            {631, 0}, // Oval Charm
            {632, 0}, // Shiny Charm
            {633, 0}, // Plasma Card
            {634, 0}, // Grubby Hanky
            {635, 0}, // Colress MCHN
            {636, 0}, // Dropped Item
            {637, 0}, // Dropped Item
            {638, 0} // Reveal Glass
    }).collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));

    /* @formatter:off */
    @SuppressWarnings("unused")
    private static final int[][] habitatListEntries = {
        { 104, 105 }, // Route 4
        { 124 }, // Route 15
        { 134 }, // Route 21
        { 84, 85, 86 }, // Clay Tunnel
        { 23, 24, 25, 26 }, // Twist Mountain
        { 97 }, // Village Bridge
        { 27, 28, 29, 30 }, // Dragonspiral Tower
        { 81, 82, 83 }, // Relic Passage
        { 106 }, // Route 5*
        { 125 }, // Route 16*
        { 98 }, // Marvelous Bridge
        { 123 }, // Abundant Shrine
        { 132 }, // Undella Town
        { 107 }, // Route 6
        { 43 }, // Undella Bay
        { 102, 103 }, // Wellspring Cave
        { 95 }, // Nature Preserve
        { 127 }, // Route 18
        { 32, 33, 34, 35, 36 }, // Giant Chasm
        { 111 }, // Route 7
        { 31, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80 }, // Victory Road
        { 12, 13, 14, 15, 16, 17, 18, 19 }, // Relic Castle
        { 0 }, // Striation City
        { 128 }, // Route 19
        { 3 }, // Aspertia City
        { 116 }, // Route 8*
        { 44, 45 }, // Floccesy Ranch
        { 61, 62, 63, 64, 65, 66, 67, 68, 69, 70 }, // Strange House
        { 129 }, // Route 20
        { 4 }, // Virbank City
        { 37, 38, 39, 40, 41 }, // Castelia Sewers
        { 118 }, // Route 9
        { 46, 47 }, // Virbank Complex
        { 42 }, // P2 Laboratory
        { 1 }, // Castelia City
        { 8, 9 }, // Pinwheel Forest
        { 5 }, // Humilau City
        { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60 }, // Reversal Mountain
        { 6, 7 }, // Dreamyard
        { 112, 113, 114, 115 }, // Celestial Tower
        { 130 }, // Route 22
        { 10, 11 }, // Desert Resort
        { 119 }, // Route 11
        { 133 }, // Route 17
        { 99 }, // Route 1
        { 131 }, // Route 23
        { 2 }, // Icirrus City*
        { 120 }, // Route 12
        { 100 }, // Route 2
        { 108, 109 }, // Mistralton Cave
        { 121 }, // Route 13
        { 101 }, // Route 3
        { 117 }, // Moor of Icirrus*
        { 96 }, // Driftveil Drawbridge
        { 93, 94 }, // Seaside Cave
        { 126 }, // Lostlorn Forest
        { 122 }, // Route 14
        { 20, 21, 22 }, // Chargestone Cave
    };

    private static final int[] bw1WildFileToAreaMap = {
        2,
        6,
        8,
        18, 18,
        19, 19,
        20, 20,
        21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, // lol
        22,
        23, 23, 23,
        24, 24, 24, 24,
        25, 25, 25, 25,
        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
        27, 27, 27, 27,
        29,
        36,
        57,
        59,
        60,
        38,
        39,
        40,
        30, 30,
        41,
        42,
        43,
        31, 31, 31,
        44,
        33, 33, 33, 33,
        45,
        34,
        46,
        32, 32, 32,
        47, 47,
        48,
        49,
        50,
        51,
        35,
        52,
        53,
        37,
        55,
        12,
        54,
    };
    
    private static final int[] bw2WildFileToAreaMap = {
        2,
        4,
        8,
        59,
        61,
        63,
        19, 19,
        20, 20,
        21, 21,
        22, 22, 22, 22, 22, 22, 22, 22,
        24, 24, 24,
        25, 25, 25, 25,
        26, 26, 26, 26,
        76,
        27, 27, 27, 27, 27,
        70, 70, 70, 70, 70,
        29,
        35,
        71, 71,
        72, 72,
        73, 73, 73, 73, 73, 73, 73, 73, 73, 73, 73, 73, 73,
        74, 74, 74, 74, 74, 74, 74, 74, 74, 74,
        76, 76, 76, 76, 76, 76, 76, 76, 76, 76,
        77, 77, 77,
        79, 79, 79, 79, 79, 79, 79, 79, 79,
        78, 78,
        -1, // Nature Preserve (not on map)
        55,
        57,
        58,
        37,
        38,
        39,
        30, 30,
        40, 40,
        41,
        42,
        31, 31, 31,
        43,
        32, 32, 32, 32,
        44,
        33,
        45,
        46,
        47,
        48,
        49,
        34,
        50,
        51,
        36,
        53,
        66,
        67,
        69,
        75,
        12,
        52,
        68,
    };

    public static void tagTrainersBW(List<Trainer> trs) {
        // We use different Gym IDs to cheat the system for the 3 n00bs
        // Chili, Cress, and Cilan
        // Cilan can be GYM1, then Chili is GYM9 and Cress GYM10
        // Also their *trainers* are GYM11 lol

        // Gym Trainers
        tag(trs, "GYM11", 0x09, 0x0A);
        tag(trs, "GYM2", 0x56, 0x57, 0x58);
        tag(trs, "GYM3", 0xC4, 0xC6, 0xC7, 0xC8);
        tag(trs, "GYM4", 0x42, 0x43, 0x44, 0x45);
        tag(trs, "GYM5", 0xC9, 0xCA, 0xCB, 0x5F, 0xA8);
        tag(trs, "GYM6", 0x7D, 0x7F, 0x80, 0x46, 0x47);
        tag(trs, "GYM7", 0xD7, 0xD8, 0xD9, 0xD4, 0xD5, 0xD6);
        tag(trs, "GYM8", 0x109, 0x10A, 0x10F, 0x10E, 0x110, 0x10B, 0x113, 0x112);

        // Gym Leaders
        tag(trs, 0x0C, "GYM1-LEADER"); // Cilan
        tag(trs, 0x0B, "GYM9-LEADER"); // Chili
        tag(trs, 0x0D, "GYM10-LEADER"); // Cress
        tag(trs, 0x15, "GYM2-LEADER"); // Lenora
        tag(trs, 0x16, "GYM3-LEADER"); // Burgh
        tag(trs, 0x17, "GYM4-LEADER"); // Elesa
        tag(trs, 0x18, "GYM5-LEADER"); // Clay
        tag(trs, 0x19, "GYM6-LEADER"); // Skyla
        tag(trs, 0x83, "GYM7-LEADER"); // Brycen
        tag(trs, 0x84, "GYM8-LEADER"); // Iris or Drayden
        tag(trs, 0x85, "GYM8-LEADER"); // Iris or Drayden

        // Elite 4
        tag(trs, 0xE4, "ELITE1"); // Shauntal
        tag(trs, 0xE6, "ELITE2"); // Grimsley
        tag(trs, 0xE7, "ELITE3"); // Caitlin
        tag(trs, 0xE5, "ELITE4"); // Marshal

        // Elite 4 R2
        tag(trs, 0x233, "ELITE1"); // Shauntal
        tag(trs, 0x235, "ELITE2"); // Grimsley
        tag(trs, 0x236, "ELITE3"); // Caitlin
        tag(trs, 0x234, "ELITE4"); // Marshal
        tag(trs, 0x197, "CHAMPION"); // Alder

        // Ubers?
        tag(trs, 0x21E, "UBER"); // Game Freak Guy
        tag(trs, 0x237, "UBER"); // Cynthia
        tag(trs, 0xE8, "UBER"); // Ghetsis
        tag(trs, 0x24A, "UBER"); // N-White
        tag(trs, 0x24B, "UBER"); // N-Black

        // Rival - Cheren
        tagRivalBW(trs, "RIVAL1", 0x35);
        tagRivalBW(trs, "RIVAL2", 0x11F);
        tagRivalBW(trs, "RIVAL3", 0x38); // used for 3rd battle AND tag battle
        tagRivalBW(trs, "RIVAL4", 0x193);
        tagRivalBW(trs, "RIVAL5", 0x5A); // 5th battle & 2nd tag battle
        tagRivalBW(trs, "RIVAL6", 0x21B);
        tagRivalBW(trs, "RIVAL7", 0x24C);
        tagRivalBW(trs, "RIVAL8", 0x24F);

        // Rival - Bianca
        tagRivalBW(trs, "FRIEND1", 0x3B);
        tagRivalBW(trs, "FRIEND2", 0x1F2);
        tagRivalBW(trs, "FRIEND3", 0x1FB);
        tagRivalBW(trs, "FRIEND4", 0x1EB);
        tagRivalBW(trs, "FRIEND5", 0x1EE);
        tagRivalBW(trs, "FRIEND6", 0x252);

        // N
        tag(trs, "NOTSTRONG", 64);
        tag(trs, "STRONG", 65, 89, 218);
    }

    public static void tagTrainersBW2(List<Trainer> trs) {
        // Use GYM9/10/11 for the retired Chili/Cress/Cilan.
        // Lenora doesn't have a team, or she'd be 12.
        // Likewise for Brycen

        // Some trainers have TWO teams because of Challenge Mode
        // I believe this is limited to Gym Leaders, E4, Champ...
        // The "Challenge Mode" teams have levels at similar to regular,
        // but have the normal boost applied too.

        // Gym Trainers
        tag(trs, "GYM1", 0xab, 0xac);
        tag(trs, "GYM2", 0xb2, 0xb3);
        tag(trs, "GYM3", 0x2de, 0x2df, 0x2e0, 0x2e1);
        // GYM4: old gym site included to give the city a theme
        tag(trs, "GYM4", 0x26d, 0x94, 0xcf, 0xd0, 0xd1); // 0x94 might be 0x324
        tag(trs, "GYM5", 0x13f, 0x140, 0x141, 0x142, 0x143, 0x144, 0x145);
        tag(trs, "GYM6", 0x95, 0x96, 0x97, 0x98, 0x14c);
        tag(trs, "GYM7", 0x17d, 0x17e, 0x17f, 0x180, 0x181);
        tag(trs, "GYM8", 0x15e, 0x15f, 0x160, 0x161, 0x162, 0x163);

        // Gym Leaders
        // Order: Normal, Challenge Mode
        // All the challenge mode teams are near the end of the ROM
        // which makes things a bit easier.
        tag(trs, "GYM1-LEADER", 0x9c, 0x2fc); // Cheren
        tag(trs, "GYM2-LEADER", 0x9d, 0x2fd); // Roxie
        tag(trs, "GYM3-LEADER", 0x9a, 0x2fe); // Burgh
        tag(trs, "GYM4-LEADER", 0x99, 0x2ff); // Elesa
        tag(trs, "GYM5-LEADER", 0x9e, 0x300); // Clay
        tag(trs, "GYM6-LEADER", 0x9b, 0x301); // Skyla
        tag(trs, "GYM7-LEADER", 0x9f, 0x302); // Drayden
        tag(trs, "GYM8-LEADER", 0xa0, 0x303); // Marlon

        // Elite 4 / Champion
        // Order: Normal, Challenge Mode, Rematch, Rematch Challenge Mode
        tag(trs, "ELITE1", 0x26, 0x304, 0x8f, 0x309);
        tag(trs, "ELITE2", 0x28, 0x305, 0x91, 0x30a);
        tag(trs, "ELITE3", 0x29, 0x307, 0x92, 0x30c);
        tag(trs, "ELITE4", 0x27, 0x306, 0x90, 0x30b);
        tag(trs, "CHAMPION", 0x155, 0x308, 0x218, 0x30d);

        // Rival - Hugh
        tagRivalBW(trs, "RIVAL1", 0xa1); // Start
        tagRivalBW(trs, "RIVAL2", 0xa6); // Floccessy Ranch
        tagRivalBW(trs, "RIVAL3", 0x24c); // Tag Battles in the sewers
        tagRivalBW(trs, "RIVAL4", 0x170); // Tag Battle on the Plasma Frigate
        tagRivalBW(trs, "RIVAL5", 0x17a); // Undella Town 1st visit
        tagRivalBW(trs, "RIVAL6", 0x2bd); // Lacunosa Town Tag Battle
        tagRivalBW(trs, "RIVAL7", 0x31a); // 2nd Plasma Frigate Tag Battle
        tagRivalBW(trs, "RIVAL8", 0x2ac); // Victory Road
        tagRivalBW(trs, "RIVAL9", 0x2b5); // Undella Town Post-E4
        tagRivalBW(trs, "RIVAL10", 0x2b8); // Driftveil Post-Undella-Battle

        // Tag Battle with Opposite Gender Hero
        tagRivalBW(trs, "FRIEND1", 0x168);
        tagRivalBW(trs, "FRIEND1", 0x16b);

        // Tag/PWT Battles with Cheren
        tag(trs, "GYM1", 0x173, 0x278, 0x32E);

        // The Restaurant Brothers
        tag(trs, "GYM9-LEADER", 0x1f0); // Cilan
        tag(trs, "GYM10-LEADER", 0x1ee); // Chili
        tag(trs, "GYM11-LEADER", 0x1ef); // Cress

        // Themed Trainers
        tag(trs, "THEMED:ZINZOLIN-STRONG", 0x2c0, 0x248, 0x15b, 0x1f1);
        tag(trs, "THEMED:COLRESS-STRONG", 0x166, 0x158, 0x32d, 0x32f);
        tag(trs, "THEMED:SHADOW1", 0x247, 0x15c, 0x2af);
        tag(trs, "THEMED:SHADOW2", 0x1f2, 0x2b0);
        tag(trs, "THEMED:SHADOW3", 0x1f3, 0x2b1);

        // Uber-Trainers
        // There are *fourteen* ubers of 17 allowed (incl. the champion)
        // It's a rather stacked game...
        tag(trs, 0x246, "UBER"); // Alder
        tag(trs, 0x1c8, "UBER"); // Cynthia
        tag(trs, 0xca, "UBER"); // Benga/BlackTower
        tag(trs, 0xc9, "UBER"); // Benga/WhiteTreehollow
        tag(trs, 0x5, "UBER"); // N/Zekrom
        tag(trs, 0x6, "UBER"); // N/Reshiram
        tag(trs, 0x30e, "UBER"); // N/Spring
        tag(trs, 0x30f, "UBER"); // N/Summer
        tag(trs, 0x310, "UBER"); // N/Autumn
        tag(trs, 0x311, "UBER"); // N/Winter
        tag(trs, 0x159, "UBER"); // Ghetsis
        tag(trs, 0x8c, "UBER"); // Game Freak Guy
        tag(trs, 0x24f, "UBER"); // Game Freak Leftovers Guy

    }

    private static void tagRivalBW(List<Trainer> allTrainers, String tag, int offset) {
        allTrainers.get(offset - 1).tag = tag + "-0";
        allTrainers.get(offset).tag = tag + "-1";
        allTrainers.get(offset + 1).tag = tag + "-2";

    }

    private static void tag(List<Trainer> allTrainers, int number, String tag) {
        if (allTrainers.size() > (number - 1)) {
            allTrainers.get(number - 1).tag = tag;
        }
    }

    private static void tag(List<Trainer> allTrainers, String tag, int... numbers) {
        for (int num : numbers) {
            if (allTrainers.size() > (num - 1)) {
                allTrainers.get(num - 1).tag = tag;
            }
        }
    }

    public static void setCouldBeMultiBattleBW(List<Trainer> trs) {
        // 62 + 63: Multi Battle with Team Plasma Grunts in Wellspring Cave w/ Cheren
        // 401 + 402: Double Battle with Preschooler Sarah and Preschooler Billy
        setCouldBeMultiBattle(trs, 62, 63, 401, 402);
    }

    public static void setCouldBeMultiBattleBW2(List<Trainer> trs) {
        // 342 + 356: Multi Battle with Team Plasma Grunts in Castelia Sewers w/ Hugh
        // 347 + 797: Multi Battle with Team Plasma Zinzolin and Team Plasma Grunt w/ Hugh
        // 374 + 375: Multi Battle with Team Plasma Grunts on Plasma Frigate w/ Cheren
        // 376 + 377: Multi Battle with Team Plasma Grunts on Plasma Frigate w/ Hugh
        // 494 + 495 + 496: Cilan, Chili, and Cress all participate in a Multi Battle
        // 513/788 + 522: Potential Double Battle with Backpacker Kiyo (513 in B2, 788 in W2) and Hiker Markus
        // 514/789 + 521: Potential Double Battle with Backpacker Kumiko (514 in W2, 789 in B2) and Hiker Jared
        // 519/786 + 520/787: Potential Double Battle with Ace Trainer Ray (519 in W2, 786 in B2) and Ace Trainer Cora (520 in B2, 787 in W2)
        // 602 + 603: Potential Double Battle with Ace Trainer Webster and Ace Trainer Shanta
        // 614 + 615: Double Battle with Veteran Claude and Veteran Cecile
        // 643 + 644: Double Battle with Veteran Sinan and Veteran Rosaline
        // 790 + 791: Potential Double Battle with Nursery Aide Rosalyn and Preschooler Ike
        // 792 + 793: Potential Double Battle with Youngster Henley and Lass Helia
        // 798 + 799: Multi Battle with Team Plasma Grunts on Plasma Frigate w/ Hugh
        // 807 + 809: Double Battle with Team Plasma Grunts on Plasma Frigate
        setCouldBeMultiBattle(trs, 342, 347, 356, 374, 375, 376, 377, 494, 495, 496, 513, 514, 519, 520, 521,
                522, 602, 603, 614, 615, 643, 644, 786, 787, 788, 789, 790, 791, 792, 793, 797, 798, 799, 807, 809);
    }

    private static void setCouldBeMultiBattle(List<Trainer> allTrainers, int... numbers) {
        for (int num : numbers) {
            if (allTrainers.size() > (num - 1)) {
                allTrainers.get(num - 1).couldBeMultiBattle = true;
            }
        }
    }

}
