package com.dabomstew.pkrandom.constants;

import com.dabomstew.pkrandom.pokemon.MoveCategory;
import com.dabomstew.pkrandom.pokemon.Type;

import java.util.*;

public class Gen6Constants {

    public static final int Type_XY = N3DSConstants.Type_XY;
    public static final int Type_ORAS = N3DSConstants.Type_ORAS;

    public static final int pokemonCount = 721;
    private static final int xyFormeCount = 77, orasFormeCount = 104;
    private static final int orasformeMovesetOffset = 35;

    public static final List<Integer> actuallyCosmeticForms = Arrays.asList(
            737, // Cherrim
            743, // Keldeo
            749, 750, 751, 752, 753, 754, 755, 756, 757, // Furfrou
            794, 795, 796, 797, // Floette (non-Eternal)
            815, 816, 817, 818, 819, 820 // Cosplay Pikachu
    );

//    public static final int actuallyCosmeticFormCountXY = actuallyCosmeticForms.size();

    public static final Map<Integer,String> formeSuffixes = setupFormeSuffixes();
    public static final Map<Integer,String> dummyFormeSuffixes = setupDummyFormeSuffixes();

    private static final int moveCountXY = 617, moveCountORAS = 621;
    private static final int highestAbilityIndexXY = 188, highestAbilityIndexORAS = 191;

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

    // Copied from pk3DS. "Dark Grass Held Item" should probably be renamed
    public static final int bsHPOffset = 0, bsAttackOffset = 1, bsDefenseOffset = 2, bsSpeedOffset = 3,
            bsSpAtkOffset = 4, bsSpDefOffset = 5, bsPrimaryTypeOffset = 6, bsSecondaryTypeOffset = 7,
            bsCatchRateOffset = 8, bsCommonHeldItemOffset = 12, bsRareHeldItemOffset = 14,
            bsDarkGrassHeldItemOffset = 16, bsGrowthCurveOffset = 21, bsAbility1Offset = 24, bsAbility2Offset = 25,
            bsAbility3Offset = 26, bsFormeOffset = 28, bsFormeSpriteOffset = 30, bsFormeCountOffset = 32,
            bsTMHMCompatOffset = 40, bsMTCompatOffset = 56; // Need to confirm these

    public static final int evolutionMethodCount = 34;

    public static final int slowpokeIndex = 79, karrablastIndex = 588, shelmetIndex = 616;

    public static final int waterStoneIndex = 84;

    public static final String tmDataPrefix = "D400AE02AF02B002";
    public static final int tmCount = 100, tmBlockOneCount = 92, tmBlockTwoCount = 3, tmBlockThreeCount = 5,
            tmBlockOneOffset = 328, tmBlockTwoOffset = 618, tmBlockThreeOffset = 690, hmBlockOneCount = 5,
            rockSmashOffsetORAS = 10, diveOffsetORAS = 28;
    private static final int tmBlockTwoStartingOffsetXY = 97, tmBlockTwoStartingOffsetORAS = 98,
            hmCountXY = 5, hmCountORAS = 7;
    public static final String itemPalettesPrefix = "6F7461746500FF920A063F";

    public static final Map<Integer,List<Integer>> speciesToMegaStoneXY = setupSpeciesToMegaStone(Type_XY);
    public static final Map<Integer,List<Integer>> speciesToMegaStoneORAS = setupSpeciesToMegaStone(Type_ORAS);

    public static int getFormeCount(int romType) {
        if (romType == Type_XY) {
            return xyFormeCount;
        } else if (romType == Type_ORAS) {
            return orasFormeCount;
        }
        return 0;
    }

    public static int getFormeMovesetOffset(int romType) {
        if (romType == Type_XY) {
            return orasformeMovesetOffset;
        } else if (romType == Type_ORAS) {
            return orasformeMovesetOffset;
        }
        return 0;
    }

    public static int getMoveCount(int romType) {
        if (romType == Type_XY) {
            return moveCountXY;
        } else if (romType == Type_ORAS) {
            return moveCountORAS;
        }
        return moveCountXY;
    }

    public static int getTMBlockTwoStartingOffset(int romType) {
        if (romType == Type_XY) {
            return tmBlockTwoStartingOffsetXY;
        } else if (romType == Type_ORAS) {
            return tmBlockTwoStartingOffsetORAS;
        }
        return tmBlockTwoStartingOffsetXY;
    }

    public static int getHMCount(int romType) {
        if (romType == Type_XY) {
            return hmCountXY;
        } else if (romType == Type_ORAS) {
            return hmCountORAS;
        }
        return hmCountXY;
    }

    public static int getHighestAbilityIndex(int romType) {
        if (romType == Type_XY) {
            return highestAbilityIndexXY;
        } else if (romType == Type_ORAS) {
            return highestAbilityIndexORAS;
        }
        return highestAbilityIndexXY;
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

    private static Map<Integer,String> setupFormeSuffixes() {
        Map<Integer,String> formeSuffixes = new HashMap<>();
        formeSuffixes.put(722,"-A"); // Deoxys-A
        formeSuffixes.put(723,"-D"); // Deoxys-D
        formeSuffixes.put(724,"-S"); // Deoxys-S
        formeSuffixes.put(725,"-S"); // Wormadam-S
        formeSuffixes.put(726,"-T"); // Wormadam-T
        formeSuffixes.put(727,"-S"); // Shaymin-S
        formeSuffixes.put(728,"-O"); // Giratina-O
        formeSuffixes.put(729,"-H"); // Rotom-H
        formeSuffixes.put(730,"-W"); // Rotom-W
        formeSuffixes.put(731,"-Fr"); // Rotom-Fr
        formeSuffixes.put(732,"-Fa"); // Rotom-Fa
        formeSuffixes.put(733,"-M"); // Rotom-M
        formeSuffixes.put(734,"-F"); // Castform-F
        formeSuffixes.put(735,"-W"); // Castform-W
        formeSuffixes.put(736,"-I"); // Castform-I
        formeSuffixes.put(737,"-S"); // Cherrim-S (cosmetic)
        formeSuffixes.put(738,"-B"); // Basculin-B
        formeSuffixes.put(739,"-Z"); // Darmanitan-Z
        formeSuffixes.put(740,"-P"); // Meloetta-P
        formeSuffixes.put(741,"-W"); // Kyurem-W
        formeSuffixes.put(742,"-B"); // Kyurem-B
        formeSuffixes.put(743,"-R"); // Keldeo-R (cosmetic)
        formeSuffixes.put(744,"-T"); // Tornadus-T
        formeSuffixes.put(745,"-T"); // Thundurus-T
        formeSuffixes.put(746,"-T"); // Landorus-T
        formeSuffixes.put(747,"-Mega"); // Mega Gengar
        formeSuffixes.put(748,"-F"); // Meowstic
        // 749 - 757 Furfrou
        formeSuffixes.put(758,"-Mega"); // Mega Gardevoir
        formeSuffixes.put(759,"-Mega"); // Mega Ampharos
        formeSuffixes.put(760,"-Mega"); // Mega Venusaur
        formeSuffixes.put(761,"-Mega-X"); // Mega Charizard X
        formeSuffixes.put(762,"-Mega-Y"); // Mega Charizard Y
        formeSuffixes.put(763,"-Mega-X"); // Mega Mewtwo X
        formeSuffixes.put(764,"-Mega-Y"); // Mega Mewtwo Y
        formeSuffixes.put(765,"-Mega"); // Mega Blaziken
        formeSuffixes.put(766,"-Mega"); // Mega Medicham
        formeSuffixes.put(767,"-Mega"); // Mega Houndoom
        formeSuffixes.put(768,"-Mega"); // Mega Aggron
        formeSuffixes.put(769,"-Mega"); // Mega Banette
        formeSuffixes.put(770,"-Mega"); // Mega Tyranitar
        formeSuffixes.put(771,"-Mega"); // Mega Scizor
        formeSuffixes.put(772,"-Mega"); // Mega Pinsir
        formeSuffixes.put(773,"-Mega"); // Mega Aerodactyl
        formeSuffixes.put(774,"-Mega"); // Mega Lucario
        formeSuffixes.put(775,"-Mega"); // Mega Abomasnow
        formeSuffixes.put(776,"-B");    // Aegislash-B
        formeSuffixes.put(777,"-Mega"); // Mega Blastoise
        formeSuffixes.put(778,"-Mega"); // Mega Kangaskhan
        formeSuffixes.put(779,"-Mega"); // Mega Gyarados
        formeSuffixes.put(780,"-Mega"); // Mega Absol
        formeSuffixes.put(781,"-Mega"); // Mega Alakazam
        formeSuffixes.put(782,"-Mega"); // Mega Heracross
        formeSuffixes.put(783,"-Mega"); // Mega Mawile
        formeSuffixes.put(784,"-Mega"); // Mega Manectric
        formeSuffixes.put(785,"-Mega"); // Mega Garchomp
        formeSuffixes.put(786,"-Mega"); // Mega Latios
        formeSuffixes.put(787,"-Mega"); // Mega Latias
        formeSuffixes.put(788,"-M"); // Pumpkaboo-M
        formeSuffixes.put(789,"-L"); // Pumpkaboo-L
        formeSuffixes.put(790,"-XL"); // Pumpkaboo-XL
        formeSuffixes.put(791,"-M"); // Gourgeist-M
        formeSuffixes.put(792,"-L"); // Gourgeist-L
        formeSuffixes.put(793,"-XL"); // Gourgeist-XL
        // 794 - 797 Floette
        formeSuffixes.put(798,"-E"); // Floette-E
        formeSuffixes.put(799,"-Mega"); // Mega Swampert
        formeSuffixes.put(800,"-Mega"); // Mega Sceptile
        formeSuffixes.put(801,"-Mega"); // Mega Sableye
        formeSuffixes.put(802,"-Mega"); // Mega Altaria
        formeSuffixes.put(803,"-Mega"); // Mega Gallade
        formeSuffixes.put(804,"-Mega"); // Mega Audino
        formeSuffixes.put(805,"-Mega"); // Mega Sharpedo
        formeSuffixes.put(806,"-Mega"); // Mega Slowbro
        formeSuffixes.put(807,"-Mega"); // Mega Steelix
        formeSuffixes.put(808,"-Mega"); // Mega Pidgeot
        formeSuffixes.put(809,"-Mega"); // Mega Glalie
        formeSuffixes.put(810,"-Mega"); // Mega Diancie
        formeSuffixes.put(811,"-Mega"); // Mega Metagross
        formeSuffixes.put(812,"-P"); // Kyogre-P
        formeSuffixes.put(813,"-P"); // Groudon-P
        formeSuffixes.put(814,"-Mega"); // Mega Rayquaza
        // 815 - 820 contest Pikachu
        formeSuffixes.put(821,"-U"); // Hoopa-U
        formeSuffixes.put(822,"-Mega"); // Mega Camerupt
        formeSuffixes.put(823,"-Mega"); // Mega Lopunny
        formeSuffixes.put(824,"-Mega"); // Mega Salamence
        formeSuffixes.put(825,"-Mega"); // Mega Beedrill

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

        for (Integer species: speciesToMegaStoneORAS.keySet()) {
            Map<Integer,String> megaMap = new HashMap<>();
            if (species == 6 || species == 150) {
                megaMap.put(1,"-Mega-X");
                megaMap.put(2,"-Mega-Y");
            } else {
                megaMap.put(1,"-Mega");
            }
            map.put(species,megaMap);
        }

        return map;
    }

    private static Map<Integer,String> setupDummyFormeSuffixes() {
        Map<Integer,String> m = new HashMap<>();
        m.put(0,"");
        return m;
    }

    private static Map<Integer,List<Integer>> setupSpeciesToMegaStone(int romType) {
        Map<Integer,List<Integer>> map = new TreeMap<>();

        map.put(3, Collections.singletonList(659));     // Venusaur
        map.put(6, Arrays.asList(660, 678));            // Charizard
        map.put(9, Collections.singletonList(661));     // Blastoise
        map.put(65, Collections.singletonList(679));    // Alakazam
        map.put(94, Collections.singletonList(656));    // Gengar
        map.put(115, Collections.singletonList(675));   // Kangaskhan
        map.put(127, Collections.singletonList(671));   // Pinsir
        map.put(130, Collections.singletonList(676));   // Gyarados
        map.put(142, Collections.singletonList(672));   // Aerodactyl
        map.put(150, Arrays.asList(662,663));           // Mewtwo
        map.put(181, Collections.singletonList(658));   // Ampharos
        map.put(212, Collections.singletonList(670));   // Scizor
        map.put(214, Collections.singletonList(680));   // Heracross
        map.put(229, Collections.singletonList(666));   // Houndoom
        map.put(248, Collections.singletonList(669));   // Tyranitar
        map.put(257, Collections.singletonList(664));   // Blaziken
        map.put(282, Collections.singletonList(657));   // Gardevoir
        map.put(303, Collections.singletonList(681));   // Mawile
        map.put(306, Collections.singletonList(667));   // Aggron
        map.put(308, Collections.singletonList(665));   // Medicham
        map.put(310, Collections.singletonList(682));   // Manectric
        map.put(354, Collections.singletonList(668));   // Banette
        map.put(359, Collections.singletonList(677));   // Absol
        map.put(380, Collections.singletonList(684));   // Latias
        map.put(381, Collections.singletonList(685));   // Latios
        map.put(445, Collections.singletonList(683));   // Garchomp
        map.put(448, Collections.singletonList(673));   // Lucario
        map.put(460, Collections.singletonList(674));   // Abomasnow

        if (romType == Type_ORAS) {
            map.put(15, Collections.singletonList(770));    // Beedrill
            map.put(18, Collections.singletonList(762));    // Pidgeot
            map.put(80, Collections.singletonList(760));    // Slowbro
            map.put(208, Collections.singletonList(761));   // Steelix
            map.put(254, Collections.singletonList(753));   // Sceptile
            map.put(260, Collections.singletonList(752));   // Swampert
            map.put(302, Collections.singletonList(754));   // Sableye
            map.put(319, Collections.singletonList(759));   // Sharpedo
            map.put(323, Collections.singletonList(767));   // Camerupt
            map.put(334, Collections.singletonList(755));   // Altaria
            map.put(362, Collections.singletonList(763));   // Glalie
            map.put(373, Collections.singletonList(769));   // Salamence
            map.put(376, Collections.singletonList(758));   // Metagross
            map.put(428, Collections.singletonList(768));   // Lopunny
            map.put(475, Collections.singletonList(756));   // Gallade
            map.put(531, Collections.singletonList(757));   // Audino
            map.put(719, Collections.singletonList(764));   // Diancie
        }

        return map;
    }
}
