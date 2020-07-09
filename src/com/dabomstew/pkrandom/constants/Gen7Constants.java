package com.dabomstew.pkrandom.constants;

import com.dabomstew.pkrandom.pokemon.MoveCategory;
import com.dabomstew.pkrandom.pokemon.Type;

import java.util.*;

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
            bsFormeCountOffset = 32, bsTMHMCompatOffset = 40, bsSpecialMTCompatOffset = 56, bsMTCompatOffset = 64;

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

    public static final String[] fastestTextPrefixes = new String[]{"1080BDE80E000500F0412DE9", "34019FE50060A0E3"};

    private static final List<Boolean> relevantEncounterFilesSM = setupRelevantEncounterFiles(Type_SM);
    private static final List<Boolean> relevantEncounterFilesUSUM = setupRelevantEncounterFiles(Type_USUM);

    public static int getPokemonCount(int romType) {
        if (romType == Type_SM) {
            return pokemonCountSM;
        } else if (romType == Type_USUM) {
            return pokemonCountUSUM;
        }
        return pokemonCountSM;
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
            if ((i - 9) % 11 == 0) {
                list.add(true);
            } else {
                list.add(false);
            }
        }

        return list;
    }
}
