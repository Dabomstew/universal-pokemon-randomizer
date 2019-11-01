package com.dabomstew.pkrandom.constants;

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

    public static final int bsHPOffset = 0, bsAttackOffset = 1, bsDefenseOffset = 2, bsSpeedOffset = 3,
            bsSpAtkOffset = 4, bsSpDefOffset = 5, bsPrimaryTypeOffset = 6, bsSecondaryTypeOffset = 7,
            bsCatchRateOffset = 8, bsCommonHeldItemOffset = 12, bsRareHeldItemOffset = 14,
            bsDarkGrassHeldItemOffset = 16, bsGrowthCurveOffset = 21, bsAbility1Offset = 24, bsAbility2Offset = 25,
            bsAbility3Offset = 26, bsTMHMCompatOffset = 40, bsMTCompatOffset = 60;

    public static final byte[] bw1NewStarterScript = { 0x24, 0x00, (byte) 0xA7, 0x02, (byte) 0xE7, 0x00, 0x00, 0x00,
            (byte) 0xDE, 0x00, 0x00, 0x00, (byte) 0xF8, 0x01, 0x05, 0x00 };

    public static final String bw1StarterScriptMagic = "2400A702";

    public static final int bw1StarterTextOffset = 18, bw1CherenText1Offset = 26, bw1CherenText2Offset = 53;

    public static final byte[] bw2NewStarterScript = { 0x28, 0x00, (byte) 0xA1, 0x40, 0x04, 0x00, (byte) 0xDE, 0x00,
            0x00, 0x00, (byte) 0xFD, 0x01, 0x05, 0x00 };

    public static final String bw2StarterScriptMagic = "2800A1400400";

    public static final int bw2StarterTextOffset = 37, bw2RivalTextOffset = 60;

    public static final int perSeasonEncounterDataLength = 232, bw2AreaDataEntryLength = 345,
            bw2EncounterAreaCount = 85;

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

    public static final int slowpokeIndex = 79, karrablastIndex = 588, shelmetIndex = 616;

    public static final int waterStoneIndex = 84;

    public static final int highestAbilityIndex = 164;

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

    public static final List<Integer> emptyPlaythroughTrainers = Arrays.asList(new Integer[] { });
    
    public static final List<Integer> bw1MainPlaythroughTrainers = Arrays.asList(new Integer[] {
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
        587, 600, 601, 602, 603, 604, 605, 606, 607, 610, 611, 612, 613});

    public static final List<Integer> bw2MainPlaythroughTrainers = Arrays.asList(new Integer[] {
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
        807, 808, 809, 810, 811, 812});
    
    public static final List<Integer> bw1RequiredFieldTMs = Arrays.asList(new Integer[] { 2, 3, 5, 6, 9, 12, 13, 19,
            22, 24, 26, 29, 30, 35, 36, 39, 41, 46, 47, 50, 52, 53, 55, 58, 61, 63, 65, 66, 71, 80, 81, 84, 85, 86, 90,
            91, 92, 93 });

    public static final List<Integer> bw2RequiredFieldTMs = Arrays.asList(new Integer[] { 1, 2, 3, 5, 6, 12, 13, 19,
            22, 26, 28, 29, 30, 36, 39, 41, 46, 47, 50, 52, 53, 56, 58, 61, 63, 65, 66, 67, 69, 71, 80, 81, 84, 85, 86,
            90, 91, 92, 93 });

    public static final List<Integer> bw1EarlyRequiredHMMoves = Arrays.asList(15);

    @SuppressWarnings("unchecked")
    public static final List<Integer> bw2EarlyRequiredHMMoves = Collections.EMPTY_LIST;

    public static final List<Integer> fieldMoves = Arrays.asList(15, 19, 57, 70, 148, 91, 100, 127, 230, 291);

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

    public static ItemList allowedItems, nonBadItems;
    public static List<Integer> regularShopItems, opShopItems;

    static {
        setupAllowedItems();
    }

    private static void setupAllowedItems() {
        allowedItems = new ItemList(638);
        // Key items + version exclusives
        allowedItems.banRange(428, 109);
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

        nonBadItems.banSingles(0x6F, 0x70, 0xE1, 0xEC, 0x9B, 0x112);
        nonBadItems.banRange(0x5F, 4); // mulch
        nonBadItems.banRange(0x87, 2); // orbs
        nonBadItems.banRange(0x89, 12); // mails
        nonBadItems.banRange(0x9F, 25); // berries without useful battle effects
        nonBadItems.banRange(0x100, 4); // pokemon specific
        nonBadItems.banRange(0x104, 5); // contest scarves

        regularShopItems = new ArrayList<>();

        regularShopItems.addAll(IntStream.rangeClosed(2,4).boxed().collect(Collectors.toList()));
        regularShopItems.addAll(IntStream.rangeClosed(0x11,0x1D).boxed().collect(Collectors.toList()));
        regularShopItems.addAll(IntStream.rangeClosed(0x4C,0x4F).boxed().collect(Collectors.toList()));

        opShopItems = new ArrayList<>();

        // "Money items" etc
        opShopItems.add(0x2A);
        opShopItems.add(0x2B);
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
            {1, 300},
            {2, 120},
            {3, 60},
            {4, 20},
            {5, 0},
            {6, 100},
            {7, 100},
            {8, 100},
            {9, 100},
            {10, 100},
            {11, 100},
            {12, 20},
            {13, 100},
            {14, 30},
            {15, 100},
            {16, 20},
            {17, 30},
            {18, 10},
            {19, 25},
            {20, 25},
            {21, 25},
            {22, 20},
            {23, 300},
            {24, 250},
            {25, 120},
            {26, 70},
            {27, 60},
            {28, 150},
            {29, 400}, // Max Revive
            {30, 40}, // Fresh Water
            {31, 60}, // Soda Pop
            {32, 70}, // Lemonade
            {33, 80}, // Milk
            {34, 50},
            {35, 80},
            {36, 45},
            {37, 280},
            {38, 120}, // Ether
            {39, 200}, // Max Ether
            {40, 300}, // Elixir
            {41, 450}, // Max Elixir
            {42, 45},
            {43, 10},
            {44, 1000},
            {45, 980},
            {46, 980},
            {47, 980},
            {48, 980},
            {49, 980},
            {50, 1000},
            {51, 980},
            {52, 980},
            {53, 2490},
            {54, 45},
            {55, 70},
            {56, 65},
            {57, 50},
            {58, 55},
            {59, 35},
            {60, 95},
            {61, 35},
            {62, 35},
            {63, 100},
            {64, 100},
            {65, 2},
            {66, 2},
            {67, 2},
            {68, 2},
            {69, 2},
            {70, 2},
            {71, 2},
            {72, 40},
            {73, 40},
            {74, 40},
            {75, 40},
            {76, 50},
            {77, 70},
            {78, 55},
            {79, 35},
            {80, 300},
            {81, 300},
            {82, 300},
            {83, 300},
            {84, 300},
            {85, 300},
            {86, 50},
            {87, 500},
            {88, 140},
            {89, 750},
            {90, 200},
            {91, 980},
            {92, 1000},
            {93, 200}, // Heart Scale
            {94, 50},
            {95, 20},
            {96, 20},
            {97, 20},
            {98, 20},
            {99, 500},
            {100, 500},
            {101, 500},
            {102, 500},
            {103, 500},
            {104, 500},
            {105, 500},
            {106, 1000},
            {107, 300},
            {108, 300},
            {109, 300},
            {110, 300},
            {111, 210},
            {112, 1000},
            {113, 0},
            {114, 0},
            {115, 0},
            {116, 100},
            {117, 100},
            {118, 100},
            {119, 100},
            {120, 0},
            {121, 0},
            {122, 0},
            {123, 0},
            {124, 0},
            {125, 0},
            {126, 0},
            {127, 0},
            {128, 0},
            {129, 0},
            {130, 0},
            {131, 0},
            {132, 0},
            {133, 0},
            {134, 15},
            {135, 1000},
            {136, 1000},
            {137, 5},
            {138, 5},
            {139, 5},
            {140, 5},
            {141, 5},
            {142, 5},
            {143, 5},
            {144, 5},
            {145, 5},
            {146, 5},
            {147, 5},
            {148, 5},
            {149, 20},
            {150, 25},
            {151, 10},
            {152, 25},
            {153, 25},
            {154, 120},
            {155, 2},
            {156, 20},
            {157, 50},
            {158, 50},
            {159, 2},
            {160, 2},
            {161, 2},
            {162, 2},
            {163, 2},
            {164, 2},
            {165, 2},
            {166, 2},
            {167, 2},
            {168, 2},
            {169, 2},
            {170, 2},
            {171, 2},
            {172, 2},
            {173, 2},
            {174, 2},
            {175, 2},
            {176, 2},
            {177, 2},
            {178, 2},
            {179, 2},
            {180, 2},
            {181, 2},
            {182, 2},
            {183, 2},
            {184, 100},
            {185, 100},
            {186, 100},
            {187, 100},
            {188, 100},
            {189, 100},
            {190, 100},
            {191, 100},
            {192, 100},
            {193, 100},
            {194, 100},
            {195, 100},
            {196, 100},
            {197, 100},
            {198, 100},
            {199, 100},
            {200, 100},
            {201, 100},
            {202, 100},
            {203, 100},
            {204, 100},
            {205, 100},
            {206, 2},
            {207, 2},
            {208, 2},
            {209, 2},
            {210, 2},
            {211, 2},
            {212, 2},
            {213, 300},
            {214, 100},
            {215, 300},
            {216, 300},
            {217, 300},
            {218, 100},
            {219, 100},
            {220, 300},
            {221, 300},
            {222, 100},
            {223, 500},
            {224, 100},
            {225, 20},
            {226, 300},
            {227, 300},
            {228, 20},
            {229, 20},
            {230, 300},
            {231, 20},
            {232, 150},
            {233, 300},
            {234, 400},
            {235, 300},
            {236, 10},
            {237, 100},
            {238, 100},
            {239, 100},
            {240, 100},
            {241, 100},
            {242, 100},
            {243, 100},
            {244, 100},
            {245, 100},
            {246, 100},
            {247, 100},
            {248, 100},
            {249, 100},
            {250, 100},
            {251, 100},
            {252, 300},
            {253, 300},
            {254, 100},
            {255, 300},
            {256, 1},
            {257, 1},
            {258, 50},
            {259, 20},
            {260, 10},
            {261, 10},
            {262, 10},
            {263, 10},
            {264, 10},
            {265, 150},
            {266, 200},
            {267, 200},
            {268, 200},
            {269, 150},
            {270, 400},
            {271, 100},
            {272, 150},
            {273, 150},
            {274, 1},
            {275, 200},
            {276, 150},
            {277, 300},
            {278, 100},
            {279, 100},
            {280, 150},
            {281, 200},
            {282, 20},
            {283, 20},
            {284, 20},
            {285, 20},
            {286, 150},
            {287, 300},
            {288, 150},
            {289, 300},
            {290, 300},
            {291, 300},
            {292, 300},
            {293, 300},
            {294, 300},
            {295, 50},
            {296, 150},
            {297, 300},
            {298, 100},
            {299, 100},
            {300, 100},
            {301, 100},
            {302, 100},
            {303, 100},
            {304, 100},
            {305, 100},
            {306, 100},
            {307, 100},
            {308, 100},
            {309, 100},
            {310, 100},
            {311, 100},
            {312, 100},
            {313, 100},
            {314, 100},
            {315, 100},
            {316, 100},
            {317, 100},
            {318, 100},
            {319, 300},
            {320, 100},
            {321, 300},
            {322, 300},
            {323, 300},
            {324, 300},
            {325, 300},
            {326, 300},
            {327, 300},
            {328, 1000},
            {329, 1000},
            {330, 1000},
            {331, 1000},
            {332, 1000},
            {333, 1000},
            {334, 5000},
            {335, 1000},
            {336, 1000},
            {337, 1000},
            {338, 5000},
            {339, 1000},
            {340, 1000},
            {341, 7000},
            {342, 9000},
            {343, 3000},
            {344, 1000},
            {345, 5000},
            {346, 1000},
            {347, 3000},
            {348, 1000},
            {349, 1000},
            {350, 1000},
            {351, 1000},
            {352, 7000},
            {353, 1000},
            {354, 1000},
            {355, 1000},
            {356, 1000},
            {357, 1000},
            {358, 1000},
            {359, 1000},
            {360, 3000},
            {361, 1000},
            {362, 1000},
            {363, 1000},
            {364, 5000},
            {365, 7000},
            {366, 1000},
            {367, 1000},
            {368, 1000},
            {369, 1000},
            {370, 1000},
            {371, 1000},
            {372, 1000},
            {373, 1000},
            {374, 1000},
            {375, 1000},
            {376, 1000},
            {377, 1000},
            {378, 1000},
            {379, 1000},
            {380, 1000},
            {381, 1000},
            {382, 1000},
            {383, 1000},
            {384, 1000},
            {385, 1000},
            {386, 1000},
            {387, 1000},
            {388, 1000},
            {389, 1000},
            {390, 1000},
            {391, 1000},
            {392, 1000},
            {393, 1000},
            {394, 1000},
            {395, 9000},
            {396, 1000},
            {397, 1000},
            {398, 1000},
            {399, 1000},
            {400, 1000},
            {401, 1000},
            {402, 1000},
            {403, 1000},
            {404, 1000},
            {405, 1000},
            {406, 1000},
            {407, 1000},
            {408, 1000},
            {409, 1000},
            {410, 1000},
            {411, 1000},
            {412, 1000},
            {413, 1000},
            {414, 1000},
            {415, 1000},
            {416, 1000},
            {417, 1000},
            {418, 1000},
            {419, 1000},
            {420, 0},
            {421, 0},
            {422, 0},
            {423, 0},
            {424, 0},
            {425, 0},
            {426, 0},
            {427, 0},
            {428, 0},
            {429, 0},
            {430, 0},
            {431, 0},
            {432, 0},
            {433, 0},
            {434, 0},
            {435, 0},
            {436, 0},
            {437, 0},
            {438, 0},
            {439, 0},
            {440, 0},
            {441, 0},
            {442, 0},
            {443, 0},
            {444, 0},
            {445, 0},
            {446, 0},
            {447, 0},
            {448, 0},
            {449, 0},
            {450, 0},
            {451, 0},
            {452, 0},
            {453, 0},
            {454, 0},
            {455, 0},
            {456, 0},
            {457, 0},
            {458, 0},
            {459, 0},
            {460, 0},
            {461, 0},
            {462, 0},
            {463, 0},
            {464, 0},
            {465, 0},
            {466, 0},
            {467, 0},
            {468, 0},
            {469, 0},
            {470, 0},
            {471, 0},
            {472, 0},
            {473, 0},
            {474, 0},
            {475, 0},
            {476, 0},
            {477, 0},
            {478, 0},
            {479, 0},
            {480, 0},
            {481, 0},
            {482, 0},
            {483, 0},
            {484, 0},
            {485, 2},
            {486, 2},
            {487, 2},
            {488, 2},
            {489, 2},
            {490, 2},
            {491, 2},
            {492, 30},
            {493, 30},
            {494, 30},
            {495, 30},
            {496, 30},
            {497, 30},
            {498, 30},
            {499, 30},
            {500, 0},
            {501, 0},
            {502, 0},
            {503, 0},
            {504, 30},
            {505, 0},
            {506, 0},
            {507, 0},
            {508, 0},
            {509, 0},
            {510, 0},
            {511, 0},
            {512, 0},
            {513, 0},
            {514, 0},
            {515, 0},
            {516, 0},
            {517, 0},
            {518, 0},
            {519, 0},
            {520, 0},
            {521, 0},
            {522, 0},
            {523, 0},
            {524, 0},
            {525, 0},
            {526, 0},
            {527, 0},
            {528, 0},
            {529, 0},
            {530, 0},
            {531, 0},
            {532, 0},
            {533, 0},
            {534, 0},
            {535, 0},
            {536, 0},
            {537, 300},
            {538, 500},
            {539, 100},
            {540, 300},
            {541, 100},
            {542, 100},
            {543, 100},
            {544, 200},
            {545, 100},
            {546, 100},
            {547, 100},
            {548, 100},
            {549, 100},
            {550, 100},
            {551, 100},
            {552, 100},
            {553, 100},
            {554, 100},
            {555, 100},
            {556, 100},
            {557, 100},
            {558, 100},
            {559, 100},
            {560, 100},
            {561, 100},
            {562, 100},
            {563, 100},
            {564, 100},
            {565, 300},
            {566, 300},
            {567, 300},
            {568, 300},
            {569, 300},
            {570, 300},
            {571, 20},
            {572, 500},
            {573, 500},
            {574, 0},
            {575, 20},
            {576, 100},
            {577, 100},
            {578, 0},
            {579, 0},
            {580, 0},
            {581, 0},
            {582, 0},
            {583, 0},
            {584, 0},
            {585, 0},
            {586, 0},
            {587, 0},
            {588, 0},
            {589, 0},
            {590, 0},
            {591, 45},
            {592, 0},
            {593, 0},
            {594, 0},
            {595, 0},
            {596, 0},
            {597, 0},
            {598, 0},
            {599, 0},
            {600, 0},
            {601, 0},
            {602, 0},
            {603, 0},
            {604, 0},
            {605, 0},
            {606, 0},
            {607, 0},
            {608, 0},
            {609, 0},
            {610, 0},
            {611, 0},
            {612, 0},
            {613, 0},
            {614, 0},
            {615, 0},
            {616, 0},
            {617, 0},
            {618, 1000},
            {619, 1000},
            {620, 1000},
            {621, 0},
            {622, 0},
            {623, 0},
            {624, 0},
            {625, 0},
            {626, 0},
            {627, 0},
            {628, 0},
            {629, 0},
            {630, 0},
            {631, 0},
            {632, 0},
            {633, 0},
            {634, 0},
            {635, 0},
            {636, 0},
            {637, 0},
            {638, 0}
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
    
    public static final int[] wildFileToAreaMap = {
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
    /* @formatter:on */

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
        tag(trs, 0x0C, "GYM1"); // Cilan
        tag(trs, 0x0B, "GYM9"); // Chili
        tag(trs, 0x0D, "GYM10"); // Cress
        tag(trs, 0x15, "GYM2"); // Lenora
        tag(trs, 0x16, "GYM3"); // Burgh
        tag(trs, 0x17, "GYM4"); // Elesa
        tag(trs, 0x18, "GYM5"); // Clay
        tag(trs, 0x19, "GYM6"); // Skyla
        tag(trs, 0x83, "GYM7"); // Brycen
        tag(trs, 0x84, "GYM8"); // Iris or Drayden
        tag(trs, 0x85, "GYM8"); // Iris or Drayden

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
        tag(trs, "GYM1", 0x9c, 0x2fc); // Cheren
        tag(trs, "GYM2", 0x9d, 0x2fd); // Roxie
        tag(trs, "GYM3", 0x9a, 0x2fe); // Burgh
        tag(trs, "GYM4", 0x99, 0x2ff); // Elesa
        tag(trs, "GYM5", 0x9e, 0x300); // Clay
        tag(trs, "GYM6", 0x9b, 0x301); // Skyla
        tag(trs, "GYM7", 0x9f, 0x302); // Drayden
        tag(trs, "GYM8", 0xa0, 0x303); // Marlon

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
        tag(trs, "GYM9", 0x1f0); // Cilan
        tag(trs, "GYM10", 0x1ee); // Chili
        tag(trs, "GYM11", 0x1ef); // Cress

        // Themed Trainers
        tag(trs, "THEMED:ZINZOLIN", 0x2c0, 0x248, 0x15b);
        tag(trs, "THEMED:COLRESS", 0x166, 0x158, 0x32d, 0x32f);
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

}
