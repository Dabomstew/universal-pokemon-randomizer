package com.dabomstew.pkrandom.constants;

/*----------------------------------------------------------------------------*/
/*--  Gen7Constants.java - Constants for Sun/Moon/Ultra Sun/Ultra Moon      --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
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

import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.MoveCategory;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.Type;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Gen7Constants {

    public static final int Type_SM = N3DSConstants.Type_SM;
    public static final int Type_USUM = N3DSConstants.Type_USUM;

    private static final int pokemonCountSM = 802, pokemonCountUSUM = 807;
    private static final int formeCountSM = 158, formeCountUSUM = 168;
    private static final int moveCountSM = 719, moveCountUSUM = 728;
    private static final int highestAbilityIndexSM = 232, highestAbilityIndexUSUM = 233;

    public static final int bsHPOffset = 0, bsAttackOffset = 1, bsDefenseOffset = 2, bsSpeedOffset = 3,
            bsSpAtkOffset = 4, bsSpDefOffset = 5, bsPrimaryTypeOffset = 6, bsSecondaryTypeOffset = 7,
            bsCatchRateOffset = 8, bsCommonHeldItemOffset = 12, bsRareHeldItemOffset = 14, bsDarkGrassHeldItemOffset = 16,
            bsGenderOffset = 18, bsGrowthCurveOffset = 21, bsAbility1Offset = 24, bsAbility2Offset = 25,
            bsAbility3Offset = 26, bsCallRateOffset = 27, bsFormeOffset = 28, bsFormeSpriteOffset = 30,
            bsFormeCountOffset = 32, bsTMHMCompatOffset = 40, bsSpecialMTCompatOffset = 56, bsMTCompatOffset = 60;

    public static final int bsSize = 0x54;

    public static final int evolutionMethodCount = 42;
    public static final int rockruffIndex = 744;

    public static final int slowpokeIndex = 79, eeveeIndex = 133, karrablastIndex = 588, shelmetIndex = 616;

    public static final int sunStoneIndex = 80, moonStoneIndex = 81, waterStoneIndex = 84, duskStoneIndex = 108, luckyEggIndex = 0xE7;


    private static List<Integer> speciesWithAlolanForms = Arrays.asList(
            19, 20, 26, 27, 28, 37, 38, 50, 51, 52, 53, 74, 75, 76, 88, 89, 103, 105
    );

    private static final Map<Integer,String> dummyFormeSuffixes = setupDummyFormeSuffixes();
    private static final Map<Integer,Map<Integer,String>> formeSuffixesByBaseForme = setupFormeSuffixesByBaseForme();

    public static String getFormeSuffixByBaseForme(int baseForme, int formNum) {
        return formeSuffixesByBaseForme.getOrDefault(baseForme,dummyFormeSuffixes).getOrDefault(formNum,"");
    }

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

    public static List<Integer> bannedMoves = Arrays.asList(
            464, 621 // Ban Dark Void, Hyperspace Fury
    );

    public static final Type[] typeTable = constructTypeTable();

    private static final String tmDataPrefixSM = "034003410342034303",  tmDataPrefixUSUM = "03BC03BD03BE03BF03";
    public static final int tmCount = 100, tmBlockOneCount = 92, tmBlockTwoCount = 3, tmBlockThreeCount = 5,
            tmBlockOneOffset = 328, tmBlockTwoOffset = 618, tmBlockThreeOffset = 690;
    public static final String itemPalettesPrefix = "070000000000000000010100";

    public static final int shopItemsOffsetSM = 0x50A8;
    public static final int shopItemsOffsetUSUM = 0x50BC;

    public static final int tutorsOffset = 0x54DE;
    public static final String tutorsPrefix = "5F6F6E5F6F6666FF";
    public static final int tutorMoveCount = 67;

    public static final String[] fastestTextPrefixes = new String[]{"1080BDE80E000500F0412DE9", "34019FE50060A0E3"};

    private static final List<Integer> mainGameShopsSM = Arrays.asList(
            8, 9, 10, 11, 14, 15, 17, 20, 21, 22, 23
    );

    private static final List<Integer> mainGameShopsUSUM = Arrays.asList(
            8, 9, 10, 11, 14, 15, 17, 20, 21, 22, 23, 24, 25, 26, 27
    );

    public static final List<Integer> evolutionItems = Arrays.asList(80,81,82,83,84,85,107,108,109,
            110,221,226,227,233,235,252,321,322,323,324,325,326,327,537,646,647,849);

    private static final List<Boolean> relevantEncounterFilesSM = setupRelevantEncounterFiles(Type_SM);
    private static final List<Boolean> relevantEncounterFilesUSUM = setupRelevantEncounterFiles(Type_USUM);

    public static final List<Integer> heldZCrystals = Arrays.asList(
            0x308, // Normal
            0x30E, // Fighting
            0x311, // Flying
            0x30F, // Poison
            0x310, // Ground
            0x314, // Rock
            0x313, // Bug
            0x315, // Ghost
            0x318, // Steel
            0x309, // Fire
            0x30A, // Water
            0x30C, // Grass
            0x30B, // Electric
            0x312, // Psychic
            0x30D, // Ice
            0x316, // Dragon
            0x317, // Dark
            0x319  // Fairy
    );

    public static final Map<Integer,List<Integer>> abilityVariations = setupAbilityVariations();

    private static Map<Integer,List<Integer>> setupAbilityVariations() {
        Map<Integer,List<Integer>> map = new HashMap<>();
        map.put(15,Arrays.asList(15,72)); // Insomnia/Vital Spirit
        map.put(29,Arrays.asList(29,73,230)); // Clear Body/White Smoke/Full Metal Body
        map.put(37,Arrays.asList(37,74)); // Huge Power/Pure Power
        map.put(4,Arrays.asList(4,75)); // Battle Armor/Shell Armor
        map.put(13,Arrays.asList(13,76)); // Cloud Nine/Air Lock
        map.put(111,Arrays.asList(111,116,232)); // Filter/Solid Rock/Prism Armor
        map.put(24,Arrays.asList(24,160)); // Rough Skin/Iron Barbs
        map.put(104,Arrays.asList(104,163,164)); // Mold Breaker/Turboblaze/Teravolt
        map.put(193,Arrays.asList(193,194)); // Wimp Out/Emergency Exit
        map.put(214,Arrays.asList(214,219)); // Queenly Majesty/Dazzling
        map.put(183,Arrays.asList(183,221)); // Gooey/Tangling Hair
        map.put(222,Arrays.asList(222,223)); // Receiver/Power of Alchemy
        map.put(136,Arrays.asList(136,231)); // Multiscale/Shadow Shield

        return map;
    }

    private static final String saveLoadFormeReversionPrefixSM = "00EB040094E50C1094E5F70E80E2", saveLoadFormeReversionPrefixUSUM = "00EB040094E50C1094E5030B80E2EE0F80E2";
    public static final String afterBattleFormeReversionPrefix = "0055E10B00001A0010A0E30700A0E1";

    public static final String ninjaskSpeciesPrefix = "11FF2FE11CD08DE2F080BDE8", shedinjaPrefix = "A0E194FDFFEB0040A0E1";

    public static final String beastLusaminePokemonBoostsPrefix = "1D14FFFF";
    public static final int beastLusamineTrainerIndex = 157;

    public static final String miniorWildEncounterPatchPrefix = "032C42E2062052E2";

    public static int getPokemonCount(int romType) {
        if (romType == Type_SM) {
            return pokemonCountSM;
        } else if (romType == Type_USUM) {
            return pokemonCountUSUM;
        }
        return pokemonCountSM;
    }

    public static List<Integer> getRegularShopItems(int romType) {
        if (romType == Type_SM) {
            return regularShopItemsSM;
        } else {
            return regularShopItemsUSUM;
        }
    }

    public static List<String> getShopNames(int romType) {
        List<String> shopNames = new ArrayList<>();
        shopNames.add("Primary 0 Trials");
        shopNames.add("Primary 1 Trials");
        shopNames.add("Primary 2 Trials");
        shopNames.add("Primary 3 Trials");
        shopNames.add("Primary 4 Trials");
        shopNames.add("Primary 5 Trials");
        shopNames.add("Primary 6 Trials");
        shopNames.add("Primary 7 Trials");
        shopNames.add("Konikoni City Incenses");
        shopNames.add("Konikoni City Herbs");
        shopNames.add("Hau'oli City Secondary");
        shopNames.add("Route 2 Secondary");
        shopNames.add("Heahea City Secondary (TMs)");
        shopNames.add("Royal Avenue Secondary (TMs)");
        shopNames.add("Route 8 Secondary");
        shopNames.add("Paniola Town Secondary");
        shopNames.add("Malie City Secondary (TMs)");
        shopNames.add("Mount Hokulani Secondary");
        shopNames.add("Seafolk Village Secondary (TMs)");
        shopNames.add("Konikoni City TMs");
        shopNames.add("Konikoni City Stones");
        shopNames.add("Thrifty Megamart, Center-Left");
        shopNames.add("Thrifty Megamart, Center-Right");
        shopNames.add("Thrifty Megamart, Right");
        if (romType == Type_USUM) {
            shopNames.add("Route 5 Secondary");
            shopNames.add("Konikoni City Secondary");
            shopNames.add("Tapu Village Secondary");
            shopNames.add("Mount Lanakila Secondary");
        }
        return shopNames;
    }

    public static List<Integer> getMainGameShops(int romType) {
        if (romType == Type_SM) {
            return mainGameShopsSM;
        } else {
            return mainGameShopsUSUM;
        }
    }

    public static int getShopItemsOffset(int romType) {
        if (romType == Type_SM) {
            return shopItemsOffsetSM;
        } else {
            return shopItemsOffsetUSUM;
        }
    }

    public static int getFormeCount(int romType) {
        if (romType == Type_SM) {
            return formeCountSM;
        } else {
            return formeCountUSUM;
        }
    }

    public static int getMoveCount(int romType) {
        if (romType == Type_SM) {
            return moveCountSM;
        } else if (romType == Type_USUM) {
            return moveCountUSUM;
        }
        return moveCountSM;
    }

    public static String getTmDataPrefix(int romType) {
        if (romType == Type_SM) {
            return tmDataPrefixSM;
        } else if (romType == Type_USUM) {
            return tmDataPrefixUSUM;
        }
        return tmDataPrefixSM;
    }

    public static int getHighestAbilityIndex(int romType) {
        if (romType == Type_SM) {
            return highestAbilityIndexSM;
        } else if (romType == Type_USUM) {
            return highestAbilityIndexUSUM;
        }
        return highestAbilityIndexSM;
    }

    public static List<Boolean> getRelevantEncounterFiles(int romType) {
        if (romType == Type_SM) {
            return relevantEncounterFilesSM;
        } else {
            return relevantEncounterFilesUSUM;
        }
    }

    public static String getSaveLoadFormeReversionPrefix(int romType) {
        if (romType == Type_SM) {
            return saveLoadFormeReversionPrefixSM;
        } else {
            return saveLoadFormeReversionPrefixUSUM;
        }
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

        Map<Integer,String> tornadusMap = new HashMap<>();
        tornadusMap.put(1,"-T");
        map.put(641,tornadusMap);

        Map<Integer,String> thundurusMap = new HashMap<>();
        thundurusMap.put(1,"-T");
        map.put(642,thundurusMap);

        Map<Integer,String> landorusMap = new HashMap<>();
        landorusMap.put(1,"-T");
        map.put(645,landorusMap);

        Map<Integer,String> meowsticMap = new HashMap<>();
        meowsticMap.put(1,"-F");
        map.put(678,meowsticMap);

        Map<Integer,String> aegislashMap = new HashMap<>();
        aegislashMap.put(1,"-B");
        map.put(681,aegislashMap);

        Map<Integer,String> pumpkabooMap = new HashMap<>();
        pumpkabooMap.put(1,"-M");
        pumpkabooMap.put(2,"-L");
        pumpkabooMap.put(3,"-XL");
        map.put(710,pumpkabooMap);

        Map<Integer,String> gourgeistMap = new HashMap<>();
        gourgeistMap.put(1,"-M");
        gourgeistMap.put(2,"-L");
        gourgeistMap.put(3,"-XL");
        map.put(711,gourgeistMap);

        Map<Integer,String> floetteMap = new HashMap<>();
        floetteMap.put(5,"-E");
        map.put(670,floetteMap);

        Map<Integer,String> kyogreMap = new HashMap<>();
        kyogreMap.put(1,"-P");
        map.put(382,kyogreMap);

        Map<Integer,String> groudonMap = new HashMap<>();
        groudonMap.put(1,"-P");
        map.put(383,groudonMap);

        Map<Integer,String> rayquazaMap = new HashMap<>();
        rayquazaMap.put(1,"-Mega");
        map.put(384,rayquazaMap);

        Map<Integer,String> hoopaMap = new HashMap<>();
        hoopaMap.put(1,"-U");
        map.put(720,hoopaMap);

        for (Integer species: Gen6Constants.speciesToMegaStoneORAS.keySet()) {
            Map<Integer,String> megaMap = new HashMap<>();
            if (species == 6 || species == 150) {
                megaMap.put(1,"-Mega-X");
                megaMap.put(2,"-Mega-Y");
            } else {
                megaMap.put(1,"-Mega");
            }
            map.put(species,megaMap);
        }

        Map<Integer,String> wishiwashiMap = new HashMap<>();
        wishiwashiMap.put(1,"-S");
        map.put(746,wishiwashiMap);

        Map<Integer,String> oricorioMap = new HashMap<>();
        oricorioMap.put(1,"-E");
        oricorioMap.put(2,"-P");
        oricorioMap.put(3,"-G");
        map.put(741,oricorioMap);

        Map<Integer,String> lycanrocMap = new HashMap<>();
        lycanrocMap.put(1,"-M");
        lycanrocMap.put(2,"-D");
        map.put(745,lycanrocMap);

        for (int species: speciesWithAlolanForms) {
            Map<Integer,String> alolanMap = new HashMap<>();
            alolanMap.put(1,"-A");
            map.put(species,alolanMap);
        }

        Map<Integer,String> greninjaMap = new HashMap<>();
        greninjaMap.put(2,"-A");
        map.put(658,greninjaMap);

        Map<Integer,String> zygardeMap = new HashMap<>();
        zygardeMap.put(1,"-10");
        zygardeMap.put(4,"-C");
        map.put(718,zygardeMap);

        Map<Integer,String> miniorMap = new HashMap<>();
        miniorMap.put(7,"-C");
        map.put(774,miniorMap);

        Map<Integer,String> necrozmaMap = new HashMap<>();
        necrozmaMap.put(1,"-DM");
        necrozmaMap.put(2,"-DW");
        necrozmaMap.put(3,"-U");
        map.put(800,necrozmaMap);

        return map;
    }

    private static Map<Integer,String> setupDummyFormeSuffixes() {
        Map<Integer,String> m = new HashMap<>();
        m.put(0,"");
        return m;
    }

    private static List<Integer> actuallyCosmeticFormsSM = Arrays.asList(
            818, // Cherrim
            819, // Shellos
            820, // Gastrodon
            826, // Keldeo
            832, 833, 834, 835, 836, 837, 838, 839, 840, // Furfrou
            871, 872, 873, // Pumpkaboo
            874, 875, 876, // Gourgeist
            877, 878, 879, 880, // Floette (Non-Eternal)
            910, 946, 947, 948, 956, 957, 958, 959, 960, // Totems
            925, // Battle Bond Greninja
            928, 929, // Power Construct Zygardes
            931, 932, 933, 934, 935, 936, 938, 939, 940, 941, 942, 943, // Minior
            949, // Magearna
            950, 951, 952, 953, 954, 955 // Pikachu With Funny Hats
    );

    private static List<Integer> actuallyCosmeticFormsUSUM = Arrays.asList(
            823, // Cherrim
            824, // Shellos
            825, // Gastrodon
            831, // Keldeo
            837, 838, 839, 840, 841, 842, 843, 844, 845, // Furfrou
            876, 877, 878, // Pumpkaboo
            879, 880, 881, // Gourgeist
            882, 883, 884, 885, // Floette (Non-Eternal)
            916, 931, 953, 954, 955, 964, 965, 966, 967, 968, 972, 973, 974, // Totems
            932, // Battle Bond Greninja
            935, 936, // Power Construct Zygardes
            938, 939, 940, 941, 942, 943, 945, 946, 947, 948, 949, 950, // Minior
            956, // Magearna
            957, 958, 959, 960, 961, 962, 963, // Pikachu With Funny Hats
            975 // Own Tempo Rockruff
    );

    public static List<Integer> getActuallyCosmeticForms(int romType) {
        if (romType == Type_SM) {
            return actuallyCosmeticFormsSM;
        } else {
            return actuallyCosmeticFormsUSUM;
        }
    }

    private static List<Integer> ignoreFormsSM = Arrays.asList(
            818, 925, 928, 929, 931, 932, 933, 934, 935, 936, 946, 948
    );

    private static List<Integer> ignoreFormsUSUM = Arrays.asList(
            823, 932, 935, 936, 938, 939, 940, 941, 942, 943, 953, 955, 975
    );

    public static List<Integer> getIgnoreForms(int romType) {
        if (romType == Type_SM) {
            return ignoreFormsSM;
        } else {
            return ignoreFormsUSUM;
        }
    }

    private static Map<Integer,Integer> altFormesWithCosmeticFormsSM = setupAltFormesWithCosmeticForms(Type_SM);
    private static Map<Integer,Integer> altFormesWithCosmeticFormsUSUM = setupAltFormesWithCosmeticForms(Type_USUM);

    public static Map<Integer,Integer> getAltFormesWithCosmeticForms(int romType) {
        if (romType == Type_SM) {
            return altFormesWithCosmeticFormsSM;
        } else {
            return altFormesWithCosmeticFormsUSUM;
        }
    }

    private static Map<Integer,Integer> setupAltFormesWithCosmeticForms(int romType) {
        Map<Integer,Integer> map = new HashMap<>();
        if (romType == Type_SM) {
            map.put(909,1); // Alolan Raticate: 1 form
            map.put(927,1);
            map.put(937,6); // Core Minior: 6 forms
        } else {
            map.put(915,1); // Alolan Raticate: 1 form
            map.put(930,1); // Alolan Marowak: 1 form
            map.put(934,1);
            map.put(944,6); // Core Minior: 6 forms
        }

        return map;
    }

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
        table[0x11] = Type.FAIRY;
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
            case FAIRY:
                return 0x11;
            default:
                return 0; // normal by default
        }
    }

    private static List<Boolean> setupRelevantEncounterFiles(int romType) {
        int fileCount = romType == Type_SM ? 2761 : 3696;
        List<Boolean> list = new ArrayList<>();

        for (int i = 0; i < fileCount; i++) {
            if (((i - 9) % 11 == 0) || (i % 11 == 0)) {
                list.add(true);
            } else {
                list.add(false);
            }
        }

        return list;
    }

    public static ItemList allowedItemsSM, allowedItemsUSUM, nonBadItems;
    public static List<Integer> regularShopItemsSM, regularShopItemsUSUM, opShopItems;

    static {
        setupAllowedItems();
    }

    private static void setupAllowedItems() {
        allowedItemsSM = new ItemList(920);
        // Key items + version exclusives
        allowedItemsSM.banRange(428, 76);
        allowedItemsSM.banRange(505,32);
        allowedItemsSM.banRange(621, 18);
        allowedItemsSM.banSingles(216, 574, 578, 579, 616, 617);
        // Unknown blank items or version exclusives
        allowedItemsSM.banRange(113, 3);
        allowedItemsSM.banRange(120, 14);
        // TMs & HMs - tms cant be held in gen5
        allowedItemsSM.tmRange(328, 92);
        allowedItemsSM.tmRange(618, 3);
        allowedItemsSM.banRange(328, 100);
        allowedItemsSM.banRange(618, 3);
        // Battle Launcher exclusives
        allowedItemsSM.banRange(592, 24);

        // Key items (Gen 6)
        allowedItemsSM.banRange(641,3);
        allowedItemsSM.banSingles(651, 689);
        allowedItemsSM.banRange(695,4);
        allowedItemsSM.banRange(700,4);
        allowedItemsSM.banRange(705,3);
        allowedItemsSM.banRange(712,3);
        allowedItemsSM.banRange(716,2);

        // TMs (Gen 6)
        allowedItemsSM.tmRange(690,5);
        allowedItemsSM.banRange(690,5);

        // Key items and an HM
        allowedItemsSM.banRange(718,34);
        allowedItemsSM.banRange(765,2);
        allowedItemsSM.banRange(771,5);

        // Z-Crystals
        allowedItemsSM.banRange(776,19);
        allowedItemsSM.banRange(798,39);

        // Key Items (Gen 7)
        allowedItemsSM.banSingles(797, 845, 847, 850, 857, 858, 860);
        allowedItemsSM.banRange(841,3);

        // Unused
        allowedItemsSM.banSingles(848, 859);
        allowedItemsSM.banRange(837,4);
        allowedItemsSM.banRange(861,18);
        allowedItemsSM.banRange(885,19);

        allowedItemsUSUM = allowedItemsSM.copy(959);

        // Z-Crystals
        allowedItemsUSUM.banRange(921,12);

        // Key Items
        allowedItemsUSUM.banRange(933,16);

        // ROTO LOTO
        allowedItemsUSUM.banRange(949,11);

        // non-bad items
        // ban specific pokemon hold items, berries, apricorns, mail
        nonBadItems = allowedItemsSM.copy();

        nonBadItems.banSingles(0x6F, 0x70, 0xE1, 0xEC, 0x9B, 0x112, 0x23F, 0x2BB, 0x2C0, 0x34C);
        nonBadItems.banRange(0x5F, 4); // mulch
        nonBadItems.banRange(0x87, 2); // orbs
        nonBadItems.banRange(0x89, 12); // mails
        nonBadItems.banRange(0x9F, 25); // berries without useful battle effects
        nonBadItems.banRange(0x100, 4); // pokemon specific
        nonBadItems.banRange(0x104, 5); // contest scarves
        nonBadItems.banRange(0x28C,4); // more mulch
        nonBadItems.banRange(0x290, 30); // Mega Stones, part 1
        nonBadItems.banRange(0x2F0, 13); // Mega Stones, part 2
        nonBadItems.banRange(0x2FF, 4); // Mega Stones, part 3
        nonBadItems.banRange(0x388,17); // Memories
        nonBadItems.banRange(0x248,7); // relic items

        regularShopItemsSM = new ArrayList<>();

        regularShopItemsSM.addAll(IntStream.rangeClosed(2,4).boxed().collect(Collectors.toList()));
        regularShopItemsSM.addAll(IntStream.rangeClosed(0x11,0x1C).boxed().collect(Collectors.toList()));
        regularShopItemsSM.addAll(IntStream.rangeClosed(0x4C,0x4F).boxed().collect(Collectors.toList()));
        regularShopItemsSM.add(0x5E); // Honey
        regularShopItemsSM.add(0x34E); // Adrenaline Orb

        regularShopItemsUSUM = new ArrayList<>(regularShopItemsSM);
        regularShopItemsUSUM.add(0x241); // Poke Toy

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

    public static ItemList getAllowedItems(int romType) {
        if (romType == Type_SM) {
            return allowedItemsSM;
        } else {
            return allowedItemsUSUM;
        }
    }

    public static List<Integer> validConsumableHeldItems = Arrays.asList(
            0x95,
            0x96,
            0x97,
            0x98,
            0x99,
            0x9A,
            0x9B,
            0x9C,
            0x9D,
            0x9E,
            0x9F,
            0xA0,
            0xA1,
            0xA2,
            0xA3,
            0xC8,
            0xC9,
            0xCA,
            0xCB,
            0xCC,
            0xCD,
            0xCE,
            0xCF,
            0xD0,
            0xD1,
            0xD2,
            0xD3,
            0xD4,
            0xD6,
            0x113,
            0x21D,
            0x221,
            0x222,
            0x27F,
            0x288,
            0x289,
            0x2AF,
            0x2B0
    );

    private static final List<Integer> requiredFieldTMsSM = Arrays.asList(
            80, 49, 5, 83, 64, 62, 100, 31, 46, 88, 57, 41, 59, 73, 53, 61, 28, 39, 55, 86, 30, 93, 81, 84, 74, 85, 72,
            3, 3, 13, 36, 91, 79, 24, 97, 50, 99, 35, 2, 26, 6, 6
    );

    private static final List<Integer> requiredFieldTMsUSUM = Arrays.asList(
            49, 5, 83, 64, 23, 100, 79, 24, 31, 46, 88, 41, 59, 32, 53, 61, 28, 39, 57, 86, 30, 62, 81, 80, 74, 85, 73,
            72, 3, 3, 84, 13, 36, 91, 55, 97, 50, 93, 93, 99, 35, 2, 26, 6, 6
    );

    public static List<Integer> getRequiredFieldTMs(int romType) {
        if (romType == Type_SM) {
            return requiredFieldTMsSM.stream().distinct().collect(Collectors.toList());
        } else {
            return requiredFieldTMsUSUM.stream().distinct().collect(Collectors.toList());
        }
    }

    public static void tagTrainersSM(List<Trainer> trs) {

        tag(trs,"ELITE1", 23, 152, 349); // Hala
        tag(trs,"ELITE2",90, 153, 351); // Olivia
        tag(trs,"ELITE3", 154, 403); // Nanu
        tag(trs,"ELITE4", 155, 359); // Hapu
        tag(trs,"ELITE5", 149, 350); // Acerola
        tag(trs,"ELITE6", 156, 352); // Kahili

        tag(trs,"RIVAL2-0", 129);
        tag(trs,"RIVAL2-1", 413);
        tag(trs,"RIVAL2-2", 414);
        tagRival(trs,"RIVAL3",477);

        tagRival(trs,"FRIEND1", 6);
        tagRival(trs,"FRIEND2", 9);
        tagRival(trs,"FRIEND3", 12);
        tagRival(trs,"FRIEND4", 76);
        tagRival(trs,"FRIEND5", 82);
        tagRival(trs,"FRIEND6", 438);
        tagRival(trs,"FRIEND7", 217);
        tagRival(trs,"FRIEND8", 220);
        tagRival(trs,"FRIEND9", 447);
        tagRival(trs,"FRIEND10", 450);
        tagRival(trs,"FRIEND11", 482);
        tagRival(trs,"FRIEND12", 356);

        tag(trs,"THEMED:GLADION-STRONG", 79, 185, 239, 240, 415, 416, 417, 418, 419, 441);
        tag(trs,"THEMED:ILIMA-STRONG", 52, 215, 216, 396);
        tag(trs,"THEMED:LANA-STRONG", 144);
        tag(trs,"THEMED:KIAWE-STRONG", 398);
        tag(trs,"THEMED:MALLOW-STRONG", 146);
        tag(trs,"THEMED:SOPHOCLES-STRONG", 405);
        tag(trs,"THEMED:MOLAYNE-STRONG", 167, 481);
        tag(trs,"THEMED:MINA-STRONG", 435, 467);
        tag(trs,"THEMED:PLUMERIA-STRONG", 89, 238, 401);
        tag(trs,"THEMED:SINA-STRONG", 75);
        tag(trs,"THEMED:DEXIO-STRONG", 74, 412);
        tag(trs,"THEMED:FABA-STRONG",132, 241, 360, 410);
        tag(trs,"THEMED:GUZMA-LEADER", 138, 235, 236, 400);
        tag(trs,"THEMED:LUSAMINE-LEADER", 131, 158);
    }

    public static void tagTrainersUSUM(List<Trainer> trs) {

        tag(trs,"ELITE1", 23, 650); // Hala
        tag(trs,"ELITE2", 90, 153, 351); // Olivia
        tag(trs,"ELITE3", 154, 508); // Nanu
        tag(trs,"ELITE4", 359, 497); // Hapu
        tag(trs,"ELITE5", 489, 490); // Big Mo
        tag(trs,"ELITE6", 149, 350); // Acerola
        tag(trs,"ELITE7", 156, 352); // Kahili

        tagRival(trs,"RIVAL2", 477); // Kukui

        // Hau
        tagRival(trs,"FRIEND1", 491);
        tagRival(trs,"FRIEND2", 9);
        tagRival(trs,"FRIEND3", 12);
        tagRival(trs,"FRIEND4", 76);
        tagRival(trs,"FRIEND5", 82);
        tagRival(trs,"FRIEND6", 438);
        tagRival(trs,"FRIEND7", 217);
        tagRival(trs,"FRIEND8", 220);
        tagRival(trs,"FRIEND9", 447);
        tagRival(trs,"FRIEND10", 450);
        tagRival(trs,"FRIEND11", 494);
        tagRival(trs,"FRIEND12", 356);

        tag(trs,"THEMED:GLADION-STRONG", 79, 185, 239, 240, 415, 416, 417, 418, 419, 441);
        tag(trs,"THEMED:ILIMA-STRONG", 52, 215, 216, 396, 502);
        tag(trs,"THEMED:LANA-STRONG", 144, 503);
        tag(trs,"THEMED:KIAWE-STRONG", 398, 504);
        tag(trs,"THEMED:MALLOW-STRONG", 146, 505);
        tag(trs,"THEMED:SOPHOCLES-STRONG", 405, 506);
        tag(trs,"THEMED:MINA-STRONG", 507);
        tag(trs,"THEMED:PLUMERIA-STRONG", 89, 238, 401);
        tag(trs,"THEMED:SINA-STRONG", 75);
        tag(trs,"THEMED:DEXIO-STRONG", 74, 412, 623);
        tag(trs,"THEMED:FABA-STRONG", 132, 241, 410, 561);
        tag(trs,"THEMED:SOLIERA-STRONG", 498, 499, 648, 651);
        tag(trs,"THEMED:DULSE-STRONG", 500, 501, 649, 652);
        tag(trs,"THEMED:GUZMA-LEADER", 138, 235, 236, 558, 647);
        tag(trs,"THEMED:LUSAMINE-LEADER", 131, 644);

        tag(trs,"UBER", 541, 542, 543, 580, 572, 573, 559, 560, 562, 645); // RR Episode
    }

    private static void tagRival(List<Trainer> allTrainers, String tag, int offset) {
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

    public static void setCouldBeMultiBattleSM(List<Trainer> trs) {
        // All Double Battles in Gen 7 are internally treated as a Multi Battle
        // 92 + 93: Rising Star Duo Justin and Lauren
        // 97 + 98: Twins Isa and Nico
        // 134 + 136: Aether Foundation Employees in Secret Lab B w/ Hau
        // 141 + 227: Team Skull Grunts on Route 17
        // 241 + 442: Faba and Aether Foundation Employee w/ Hau
        // 262 + 265: Ace Duo Aimee and Kent
        // 270 + 299: Swimmers Jake and Yumi
        // 278 + 280: Honeymooners Noriko and Devin
        // 303 + 307: Veteran Duo Tsunekazu and Nobuko
        // 315 + 316: Team Skull Grunts in Po Town
        // 331 + 332: Karate Family Guy and Samuel
        // 371 + 372: Twins Harper and Sarah
        // 373 + 374: Swimmer Girls Ashlyn and Kylie
        // 375 + 376: Golf Buddies Tara and Tina
        // 421 + 422: Athletic Siblings Alyssa and Sho
        // 425 + 426: Punk Pair Lane and Yoko
        // 429 + 430: Punk Pair Troy and Marie
        // 443 + 444: Team Skull Grunts in Diglett's Tunnel w/ Hau
        // 453 + 454: Aether Foundation Employees w/ Hau
        // 455 + 456: Aether Foundation Employees w/ Gladion
        setCouldBeMultiBattle(trs, 92, 93, 97, 98, 134, 136, 141, 227, 241, 262, 265, 270, 278, 280, 299,
                303, 307, 315, 316, 331, 332, 371, 372, 373, 374, 375, 376, 421, 422, 425, 426, 429, 430, 442, 443,
                444, 453, 454, 455, 456);
    }

    public static void setCouldBeMultiBattleUSUM(List<Trainer> trs) {
        // All Double Battles in Gen 7 are internally treated as a Multi Battle
        // 92 + 93: Rising Star Duo Justin and Lauren
        // 97 + 98: Twins Isa and Nico
        // 134 + 136: Aether Foundation Employees in Secret Lab B w/ Hau
        // 141 + 227: Team Skull Grunts on Route 17
        // 178 + 511: Capoeira Couple Cara and Douglas
        // 241 + 442: Faba and Aether Foundation Employee w/ Hau
        // 262 + 265: Ace Duo Aimee and Kent
        // 270 + 299: Swimmers Jake and Yumi
        // 278 + 280: Honeymooners Noriko and Devin
        // 303 + 307: Veteran Duo Tsunekazu and Nobuko
        // 315 + 316: Team Skull Grunts in Po Town
        // 331 + 332: Karate Family Guy and Samuel
        // 371 + 372: Twins Harper and Sarah
        // 373 + 374: Swimmer Girls Ashlyn and Kylie
        // 375 + 376: Golf Buddies Tara and Tina
        // 421 + 422: Athletic Siblings Alyssa and Sho
        // 425 + 426: Punk Pair Lane and Yoko
        // 429 + 430: Punk Pair Troy and Marie
        // 443 + 444: Team Skull Grunts in Diglett's Tunnel w/ Hau
        // 453 + 454: Aether Foundation Employees w/ Hau
        // 455 + 456: Aether Foundation Employees w/ Gladion
        // 514 + 521: Tourist Couple Yuriko and Landon
        // 515 + 534: Tourist Couple Steve and Reika
        // 529 + 530: Dancing Family Jen and Fumiko
        // 554 + 561: Aether Foundation Employee and Faba w/ Lillie
        // 557 + 578: GAME FREAK Iwao and Morimoto
        // 586 + 595: Team Rainbow Rocket Grunts w/ Guzma
        // 613 + 626: Master & Apprentice Kaimana and Breon
        // 617 + 618: Sparring Partners Allon and Eimar
        // 619 + 620: Sparring Partners Craig and Jason
        setCouldBeMultiBattle(trs, 92, 93, 97, 98, 134, 136, 141, 178, 227, 241, 262, 265, 270, 278, 280,
                299, 303, 307, 315, 316, 331, 332, 371, 372, 373, 374, 375, 376, 421, 422, 425, 426, 429, 430, 442,
                443, 444, 453, 454, 455, 456, 511, 514, 515, 521, 529, 530, 534, 544, 557, 561, 578, 586, 595, 613,
                617, 618, 619, 620, 626);
    }

    private static void setCouldBeMultiBattle(List<Trainer> allTrainers, int... numbers) {
        for (int num : numbers) {
            if (allTrainers.size() > (num - 1)) {
                allTrainers.get(num - 1).couldBeMultiBattle = true;
            }
        }
    }

    public static void setForcedRivalStarterPositionsUSUM(List<Trainer> allTrainers) {

        // Hau 3
        allTrainers.get(12 - 1).forceStarterPosition = 0;
        allTrainers.get(13 - 1).forceStarterPosition = 0;
        allTrainers.get(14 - 1).forceStarterPosition = 0;

        // Hau 6
        allTrainers.get(217 - 1).forceStarterPosition = 0;
        allTrainers.get(218 - 1).forceStarterPosition = 0;
        allTrainers.get(219 - 1).forceStarterPosition = 0;
    }

    public static final Map<Integer,Integer> balancedItemPrices = Stream.of(new Integer[][] {
            // Skip item index 0. All prices divided by 10
            {1, 300}, // Master Ball
            {2, 80}, // Ultra Ball
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
            {17, 20}, // Potion
            {18, 20}, // Antidote
            {19, 30}, // Burn Heal
            {20, 10}, // Ice Heal
            {21, 10}, // Awakening
            {22, 30}, // Parlyz Heal
            {23, 300}, // Full Restore
            {24, 250}, // Max Potion
            {25, 150}, // Hyper Potion
            {26, 70}, // Super Potion
            {27, 40}, // Full Heal
            {28, 200}, // Revive
            {29, 400}, // Max Revive
            {30, 20}, // Fresh Water
            {31, 30}, // Soda Pop
            {32, 40}, // Lemonade
            {33, 60}, // Moomoo Milk
            {34, 50}, // EnergyPowder
            {35, 120}, // Energy Root
            {36, 30}, // Heal Powder
            {37, 280}, // Revival Herb
            {38, 300}, // Ether
            {39, 450}, // Max Ether
            {40, 1500}, // Elixir
            {41, 1800}, // Max Elixir
            {42, 35}, // Lava Cookie
            {43, 20}, // Berry Juice
            {44, 500}, // Sacred Ash
            {45, 1000}, // HP Up
            {46, 1000}, // Protein
            {47, 1000}, // Iron
            {48, 1000}, // Carbos
            {49, 1000}, // Calcium
            {50, 1000}, // Rare Candy
            {51, 1000}, // PP Up
            {52, 1000}, // Zinc
            {53, 2500}, // PP Max
            {54, 35}, // Old Gateau
            {55, 150}, // Guard Spec.
            {56, 100}, // Dire Hit
            {57, 100}, // X Attack
            {58, 200}, // X Defend
            {59, 100}, // X Speed
            {60, 100}, // X Accuracy
            {61, 100}, // X Special
            {62, 200}, // X Sp. Def
            {63, 10}, // Poké Doll
            {64, 10}, // Fluffy Tail
            {65, 2}, // Blue Flute
            {66, 2}, // Yellow Flute
            {67, 2}, // Red Flute
            {68, 2}, // Black Flute
            {69, 2}, // White Flute
            {70, 2}, // Shoal Salt
            {71, 2}, // Shoal Shell
            {72, 100}, // Red Shard
            {73, 100}, // Blue Shard
            {74, 100}, // Yellow Shard
            {75, 100}, // Green Shard
            {76, 70}, // Super Repel
            {77, 90}, // Max Repel
            {78, 100}, // Escape Rope
            {79, 40}, // Repel
            {80, 300}, // Sun Stone
            {81, 300}, // Moon Stone
            {82, 300}, // Fire Stone
            {83, 300}, // Thunderstone
            {84, 300}, // Water Stone
            {85, 300}, // Leaf Stone
            {86, 50}, // TinyMushroom
            {87, 500}, // Big Mushroom
            {88, 200}, // Pearl
            {89, 800}, // Big Pearl
            {90, 300}, // Stardust
            {91, 1200}, // Star Piece
            {92, 1000}, // Nugget
            {93, 500}, // Heart Scale
            {94, 30}, // Honey
            {95, 20}, // Growth Mulch
            {96, 20}, // Damp Mulch
            {97, 20}, // Stable Mulch
            {98, 20}, // Gooey Mulch
            {99, 700}, // Root Fossil
            {100, 700}, // Claw Fossil
            {101, 700}, // Helix Fossil
            {102, 700}, // Dome Fossil
            {103, 1000}, // Old Amber
            {104, 700}, // Armor Fossil
            {105, 700}, // Skull Fossil
            {106, 500}, // Rare Bone
            {107, 300}, // Shiny Stone
            {108, 300}, // Dusk Stone
            {109, 300}, // Dawn Stone
            {110, 200}, // Oval Stone
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
            {213, 400}, // BrightPowder
            {214, 400}, // White Herb
            {215, 300}, // Macho Brace
            {216, 0}, // Exp. Share
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
            {228, 400}, // Smoke Ball
            {229, 300}, // Everstone
            {230, 300}, // Focus Band
            {231, 1000}, // Lucky Egg
            {232, 500}, // Scope Lens
            {233, 300}, // Metal Coat
            {234, 1000}, // Leftovers
            {235, 300}, // Dragon Scale
            {236, 100}, // Light Ball
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
            {256, 100}, // Lucky Punch
            {257, 100}, // Metal Powder
            {258, 100}, // Thick Club
            {259, 100}, // Stick
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
            {274, 100}, // Quick Powder
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
            {328, 1000}, // TM01 Work Up
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
            {343, 1000}, // TM16 Light Screen
            {344, 1000}, // TM17 Protect
            {345, 2000}, // TM18 Rain Dance
            {346, 1000}, // TM19 Roost
            {347, 1000}, // TM20 Safeguard
            {348, 1000}, // TM21 Frustration
            {349, 1000}, // TM22 Solar Beam
            {350, 1000}, // TM23 Smack Down
            {351, 1000}, // TM24 Thunderbolt
            {352, 2000}, // TM25 Thunder
            {353, 1000}, // TM26 Earthquake
            {354, 1000}, // TM27 Return
            {355, 2000}, // TM28 Leech Life
            {356, 1000}, // TM29 Psychic
            {357, 1000}, // TM30 Shadow Ball
            {358, 1000}, // TM31 Brick Break
            {359, 1000}, // TM32 Double Team
            {360, 1000}, // TM33 Reflect
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
            {377, 2000}, // TM50 Overheat
            {378, 1000}, // TM51 Steel Wing
            {379, 2000}, // TM52 Focus Blast
            {380, 1000}, // TM53 Energy Ball
            {381, 1000}, // TM54 False Swipe
            {382, 1000}, // TM55 Scald
            {383, 1000}, // TM56 Fling
            {384, 1000}, // TM57 Charge Beam
            {385, 1000}, // TM58 Sky Drop
            {386, 2000}, // TM59 Brutal Swing
            {387, 1000}, // TM60 Quash
            {388, 1000}, // TM61 Will-O-Wisp
            {389, 1000}, // TM62 Acrobatics
            {390, 1000}, // TM63 Embargo
            {391, 1000}, // TM64 Explosion
            {392, 1000}, // TM65 Shadow Claw
            {393, 1000}, // TM66 Payback
            {394, 1000}, // TM67 Smart Strike
            {395, 2000}, // TM68 Giga Impact
            {396, 1000}, // TM69 Rock Polish
            {397, 2000}, // TM70 Aurora Veil
            {398, 2000}, // TM71 Stone Edge
            {399, 1000}, // TM72 Volt Switch
            {400, 500}, // TM73 Thunder Wave
            {401, 1000}, // TM74 Gyro Ball
            {402, 1000}, // TM75 Swords Dance
            {403, 1000}, // TM76 Fly
            {404, 1000}, // TM77 Psych Up
            {405, 1000}, // TM78 Bulldoze
            {406, 1000}, // TM79 Frost Breath
            {407, 1000}, // TM80 Rock Slide
            {408, 1000}, // TM81 X-Scissor
            {409, 1000}, // TM82 Dragon Tail
            {410, 1000}, // TM83 Infestation
            {411, 1000}, // TM84 Poison Jab
            {412, 1000}, // TM85 Dream Eater
            {413, 1000}, // TM86 Grass Knot
            {414, 1000}, // TM87 Swagger
            {415, 1000}, // TM88 Sleep Talk
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
            {504, 35}, // RageCandyBar
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
            {565, 30}, // Health Wing
            {566, 30}, // Muscle Wing
            {567, 30}, // Resist Wing
            {568, 30}, // Genius Wing
            {569, 30}, // Clever Wing
            {570, 30}, // Swift Wing
            {571, 100}, // Pretty Wing
            {572, 700}, // Cover Fossil
            {573, 700}, // Plume Fossil
            {574, 0}, // Liberty Pass
            {575, 20}, // Pass Orb
            {576, 100}, // Dream Ball
            {577, 10}, // Poké Toy
            {578, 0}, // Prop Case
            {579, 0}, // Dragon Skull
            {580, 1500}, // BalmMushroom
            {581, 4000}, // Big Nugget
            {582, 3000}, // Pearl String
            {583, 6000}, // Comet Shard
            {584, 0}, // Relic Copper
            {585, 0}, // Relic Silver
            {586, 0}, // Relic Gold
            {587, 0}, // Relic Vase
            {588, 0}, // Relic Band
            {589, 0}, // Relic Statue
            {590, 0}, // Relic Crown
            {591, 35}, // Casteliacone
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
            {618, 2000}, // TM93 Wild Charge
            {619, 2000}, // TM94 Surf
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
            {638, 0}, // Reveal Glass
            {639, 200}, // Weakness Policy
            {640, 600}, // Assault Vest
            {641, 0}, // Holo Caster
            {642, 0}, // Prof’s Letter
            {643, 0}, // Roller Skates
            {644,200}, // Pixie Plate
            {645,500}, // Ability Capsule
            {646,300}, // Whipped Dream
            {647,300}, // Sachet
            {648,20}, // Luminous Moss
            {649,20}, // Snowball
            {650,300}, // Safety Goggles
            {651, 0}, // Poké Flute
            {652, 20}, // Rich Mulch
            {653, 20}, // Surprise Mulch
            {654, 20}, // Boost Mulch
            {655, 20}, // Amaze Mulch
            {656, 1000}, // Gengarite
            {657, 1000}, // Gardevoirite
            {658, 1000}, // Ampharosite
            {659, 1000}, // Venusaurite
            {660, 1000}, // Charizardite X
            {661, 1000}, // Blastoisinite
            {662, 2000}, // Mewtwonite X
            {663, 2000}, // Mewtwonite Y
            {664, 1000}, // Blazikenite
            {665, 500}, // Medichamite
            {666, 1000}, // Houndoominite
            {667, 1000}, // Aggronite
            {668, 500}, // Banettite
            {669, 2000}, // Tyranitarite
            {670, 1000}, // Scizorite
            {671, 1000}, // Pinsirite
            {672, 1000}, // Aerodactylite
            {673, 1000}, // Lucarionite
            {674, 500}, // Abomasite
            {675, 500}, // Kangaskhanite
            {676, 1000}, // Gyaradosite
            {677, 500}, // Absolite
            {678, 1000}, // Charizardite Y
            {679, 1000}, // Alakazite
            {680, 1000}, // Heracronite
            {681, 300}, // Mawilite
            {682, 500}, // Manectite
            {683, 2000}, // Garchompite
            {684, 2000}, // Latiasite
            {685, 2000}, // Latiosite
            {686, 100}, // Roseli Berry
            {687, 100}, // Kee Berry
            {688, 100}, // Maranga Berry
            {689, 0}, // Sprinklotad
            {690, 1000}, // TM96 Nature Power
            {691, 1000}, // TM97 Dark Pulse
            {692, 2000}, // TM98 Waterfall
            {693, 1000}, // TM99 Dazzling Gleam
            {694, 500}, // TM100 Confide
            {695, 0}, // Power Plant Pass
            {696, 0}, // Mega Ring
            {697, 0}, // Intriguing Stone
            {698, 0}, // Common Stone
            {699, 2}, // Discount Coupon
            {700, 0}, // Elevator Key
            {701, 0}, // TMV Pass
            {702, 0}, // Honor of Kalos
            {703, 0}, // Adventure Rules
            {704, 300}, // Strange Souvenir
            {705, 0}, // Lens Case
            {706, 0}, // Travel Trunk
            {707, 0}, // Travel Trunk
            {708, 35}, // Lumiose Galette
            {709, 35}, // Shalour Sable
            {710, 700}, // Jaw Fossil
            {711, 700}, // Sail Fossil
            {712, 0}, // Looker Ticket
            {713, 0}, // Bike
            {714, 0}, // Holo Caster
            {715, 100}, // Fairy Gem
            {716, 0}, // Mega Charm
            {717, 0}, // Mega Glove
            {718, 0}, // Mach Bike
            {719, 0}, // Acro Bike
            {720, 0}, // Wailmer Pail
            {721, 0}, // Devon Parts
            {722, 0}, // Soot Sack
            {723, 0}, // Basement Key
            {724, 0}, // Pokéblock Kit
            {725, 0}, // Letter
            {726, 0}, // Eon Ticket
            {727, 0}, // Scanner
            {728, 0}, // Go-Goggles
            {729, 0}, // Meteorite
            {730, 0}, // Key to Room 1
            {731, 0}, // Key to Room 2
            {732, 0}, // Key to Room 4
            {733, 0}, // Key to Room 6
            {734, 0}, // Storage Key
            {735, 0}, // Devon Scope
            {736, 0}, // S.S. Ticket
            {737, 0}, // HM07
            {738, 0}, // Devon Scuba Gear
            {739, 0}, // Contest Costume
            {740, 0}, // Contest Costume
            {741, 0}, // Magma Suit
            {742, 0}, // Aqua Suit
            {743, 0}, // Pair of Tickets
            {744, 0}, // Mega Bracelet
            {745, 0}, // Mega Pendant
            {746, 0}, // Mega Glasses
            {747, 0}, // Mega Anchor
            {748, 0}, // Mega Stickpin
            {749, 0}, // Mega Tiara
            {750, 0}, // Mega Anklet
            {751, 0}, // Meteorite
            {752, 1000}, // Swampertite
            {753, 1000}, // Sceptilite
            {754, 300}, // Sablenite
            {755, 500}, // Altarianite
            {756, 1000}, // Galladite
            {757, 500}, // Audinite
            {758, 2000}, // Metagrossite
            {759, 500}, // Sharpedonite
            {760, 500}, // Slowbronite
            {761, 1000}, // Steelixite
            {762, 500}, // Pidgeotite
            {763, 500}, // Glalitite
            {764, 2000}, // Diancite
            {765, 0}, // Prison Bottle
            {766, 0}, // Mega Cuff
            {767, 500}, // Cameruptite
            {768, 500}, // Lopunnite
            {769, 2000}, // Salamencite
            {770, 300}, // Beedrillite
            {771, 0}, // Meteorite
            {772, 0}, // Meteorite
            {773, 0}, // Key Stone
            {774, 0}, // Meteorite Shard
            {775, 0}, // Eon Flute
            {776, 0}, // Normalium Z
            {777, 0}, // Firium Z
            {778, 0}, // Waterium Z
            {779, 0}, // Electrium Z
            {780, 0}, // Grassium Z
            {781, 0}, // Icium Z
            {782, 0}, // Fightnium Z
            {783, 0}, // Poisonium Z
            {784, 0}, // Groundium Z
            {785, 0}, // Flynium Z
            {786, 0}, // Psychium Z
            {787, 0}, // Bugnium Z
            {788, 0}, // Rockium Z
            {789, 0}, // Ghostium Z
            {790, 0}, // Dragonium Z
            {791, 0}, // Darknium Z
            {792, 0}, // Steelium Z
            {793, 0}, // Fairium Z
            {794, 0}, // Pikanium Z
            {795, 500}, // Bottle Cap
            {796, 1000}, // Gold Bottle Cap
            {797, 0}, // Z-Ring
            {798, 0}, // Decidium Z
            {799, 0}, // Incinium Z
            {800, 0}, // Primarium Z
            {801, 0}, // Tapunium Z
            {802, 0}, // Marshadium Z
            {803, 0}, // Aloraichium Z
            {804, 0}, // Snorlium Z
            {805, 0}, // Eevium Z
            {806, 0}, // Mewnium Z
            {807, 0}, // Normalium Z
            {808, 0}, // Firium Z
            {809, 0}, // Waterium Z
            {810, 0}, // Electrium Z
            {811, 0}, // Grassium Z
            {812, 0}, // Icium Z
            {813, 0}, // Fightnium Z
            {814, 0}, // Poisonium Z
            {815, 0}, // Groundium Z
            {816, 0}, // Flynium Z
            {817, 0}, // Psychium Z
            {818, 0}, // Bugnium Z
            {819, 0}, // Rockium Z
            {820, 0}, // Ghostium Z
            {821, 0}, // Dragonium Z
            {822, 0}, // Darknium Z
            {823, 0}, // Steelium Z
            {824, 0}, // Fairium Z
            {825, 0}, // Pikanium Z
            {826, 0}, // Decidium Z
            {827, 0}, // Incinium Z
            {828, 0}, // Primarium Z
            {829, 0}, // Tapunium Z
            {830, 0}, // Marshadium Z
            {831, 0}, // Aloraichium Z
            {832, 0}, // Snorlium Z
            {833, 0}, // Eevium Z
            {834, 0}, // Mewnium Z
            {835, 0}, // Pikashunium Z
            {836, 0}, // Pikashunium Z
            {837, 0}, // ???
            {838, 0}, // ???
            {839, 0}, // ???
            {840, 0}, // ???
            {841, 0}, // Forage Bag
            {842, 0}, // Fishing Rod
            {843, 0}, // Professor's Mask
            {844, 1}, // Festival Ticket
            {845, 0}, // Sparkling Stone
            {846, 30}, // Adrenaline Orb
            {847, 0}, // Zygarde Cube
            {848, 0}, // ???
            {849, 300}, // Ice Stone
            {850, 0}, // Ride Pager
            {851, 30}, // Beast Ball
            {852, 35}, // Big Malasada
            {853, 30}, // Red Nectar
            {854, 30}, // Yellow Nectar
            {855, 30}, // Pink Nectar
            {856, 30}, // Purple Nectar
            {857, 0}, // Sun Flute
            {858, 0}, // Moon Flute
            {859, 0}, // ???
            {860, 0}, // Enigmatic Card
            {861, 0}, // ???
            {862, 0}, // ???
            {863, 0}, // ???
            {864, 0}, // ???
            {865, 0}, // ???
            {866, 0}, // ???
            {867, 0}, // ???
            {868, 0}, // ???
            {869, 0}, // ???
            {870, 0}, // ???
            {871, 0}, // ???
            {872, 0}, // ???
            {873, 0}, // ???
            {874, 0}, // ???
            {875, 0}, // ???
            {876, 0}, // ???
            {877, 0}, // ???
            {878, 0}, // ???
            {879, 400}, // Terrain Extender
            {880, 300}, // Protective Pads
            {881, 100}, // Electric Seed
            {882, 100}, // Psychic Seed
            {883, 100}, // Misty Seed
            {884, 100}, // Grassy Seed
            {885, 0}, // ???
            {886, 0}, // ???
            {887, 0}, // ???
            {888, 0}, // ???
            {889, 0}, // ???
            {890, 0}, // ???
            {891, 0}, // ???
            {892, 0}, // ???
            {893, 0}, // ???
            {894, 0}, // ???
            {895, 0}, // ???
            {896, 0}, // ???
            {897, 0}, // ???
            {898, 0}, // ???
            {899, 0}, // ???
            {900, 0}, // ???
            {901, 0}, // ???
            {902, 0}, // ???
            {903, 0}, // ???
            {904, 100}, // Fighting Memory
            {905, 100}, // Flying Memory
            {906, 100}, // Poison Memory
            {907, 100}, // Ground Memory
            {908, 100}, // Rock Memory
            {909, 100}, // Bug Memory
            {910, 100}, // Ghost Memory
            {911, 100}, // Steel Memory
            {912, 100}, // Fire Memory
            {913, 100}, // Water Memory
            {914, 100}, // Grass Memory
            {915, 100}, // Electric Memory
            {916, 100}, // Psychic Memory
            {917, 100}, // Ice Memory
            {918, 100}, // Dragon Memory
            {919, 100}, // Dark Memory
            {920, 100}, // Fairy Memory
            {921, 0}, // Solganium Z
            {922, 0}, // Lunalium Z
            {923, 0}, // Ultranecrozium Z
            {924, 0}, // Mimikium Z
            {925, 0}, // Lycanium Z
            {926, 0}, // Kommonium Z
            {927, 0}, // Solganium Z
            {928, 0}, // Lunalium Z
            {929, 0}, // Ultranecrozium Z
            {930, 0}, // Mimikium Z
            {931, 0}, // Lycanium Z
            {932, 0}, // Kommonium Z
            {933, 0}, // Z-Power Ring
            {934, 0}, // Pink Petal
            {935, 0}, // Orange Petal
            {936, 0}, // Blue Petal
            {937, 0}, // Red Petal
            {938, 0}, // Green Petal
            {939, 0}, // Yellow Petal
            {940, 0}, // Purple Petal
            {941, 0}, // Rainbow Flower
            {942, 0}, // Surge Badge
            {943, 0}, // N-Solarizer
            {944, 0}, // N-Lunarizer
            {945, 0}, // N-Solarizer
            {946, 0}, // N-Lunarizer
            {947, 0}, // Ilima's Normalium Z
            {948, 0}, // Left Poké Ball
            {949, 0}, // Roto Hatch
            {950, 0}, // Roto Bargain
            {951, 0}, // Roto Prize Money
            {952, 0}, // Roto Exp. Points
            {953, 0}, // Roto Friendship
            {954, 0}, // Roto Encounter
            {955, 0}, // Roto Stealth
            {956, 0}, // Roto HP Restore
            {957, 0}, // Roto PP Restore
            {958, 0}, // Roto Boost
            {959, 0} // Roto Catch
    }).collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
}
