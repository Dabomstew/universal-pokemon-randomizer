package com.dabomstew.pkrandom.constants;

import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.MoveCategory;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.Type;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            110,221,226,227,233,235,252,321,322,323,324,325,326,327,646,647,849);

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
        allowedItemsSM.banSingles(574, 578, 579, 616, 617);
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
        allowedItemsSM.banRange(718,33);
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

        regularShopItemsSM = new ArrayList<>();

        regularShopItemsSM.addAll(IntStream.rangeClosed(2,4).boxed().collect(Collectors.toList()));
        regularShopItemsSM.addAll(IntStream.rangeClosed(0x11,0x1D).boxed().collect(Collectors.toList()));
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
            0x21E,
            0x221,
            0x222,
            0x27F,
            0x288,
            0x289,
            0x2AF,
            0x2B0
    );

    private static final List<Integer> requiredFieldTMsSM = Arrays.asList(
            5, 64, 62, 100, 31, 46, 88, 57, 41, 59, 73, 53, 61, 39, 86, 93, 84, 74, 72, 3, 13, 36, 91, 79, 24, 97, 50,
            99, 35, 2, 26, 6
    );

    private static final List<Integer> requiredFieldTMsUSUM = Arrays.asList(
            5, 64, 23, 100, 79, 24, 31, 46, 88, 41, 59, 32, 53, 61, 39, 86, 62, 80, 74, 73, 72, 3, 84, 13, 36, 91, 55,
            97, 50, 93, 99, 35, 2, 26, 6
    );

    public static List<Integer> getRequiredFieldTMs(int romType) {
        if (romType == Type_SM) {
            return requiredFieldTMsSM;
        } else {
            return requiredFieldTMsUSUM;
        }
    }

    private static final List<Integer> duplicateFieldTMsSM = Arrays.asList(
            19, 32
    );

    private static final List<Integer> duplicateFieldTMsUSUM = Arrays.asList(
            21, 30, 36
    );

    public static List<Integer> getDuplicateFieldTMs(int romType) {
        if (romType == Type_SM) {
            return duplicateFieldTMsSM;
        } else {
            return duplicateFieldTMsUSUM;
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
}
