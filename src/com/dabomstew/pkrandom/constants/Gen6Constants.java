package com.dabomstew.pkrandom.constants;

import com.dabomstew.pkrandom.pokemon.MoveCategory;
import com.dabomstew.pkrandom.pokemon.Type;

import java.util.*;

public class Gen6Constants {

    public static final int Type_XY = N3DSConstants.Type_XY;
    public static final int Type_ORAS = N3DSConstants.Type_ORAS;

    public static final int pokemonCount = 721;
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
            bsAbility3Offset = 26, bsFormeOffset = 28, bsFormeSpriteOffset = 30, bsFormeCountOffset = 32;
//            bsTMHMCompatOffset = 40, bsMTCompatOffset = 60; // Need to confirm these

    public static final int evolutionMethodCount = 31;

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
