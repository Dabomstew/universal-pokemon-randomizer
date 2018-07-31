package com.dabomstew.pkrandom.constants;

import java.util.Arrays;
import java.util.List;

import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.MoveCategory;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.Type;

public class Gen4Constants {

    public static final int Type_DP = 0;
    public static final int Type_Plat = 1;
    public static final int Type_HGSS = 2;

    public static final int pokemonCount = 493, moveCount = 467;

    public static final int bsHPOffset = 0, bsAttackOffset = 1, bsDefenseOffset = 2, bsSpeedOffset = 3,
            bsSpAtkOffset = 4, bsSpDefOffset = 5, bsPrimaryTypeOffset = 6, bsSecondaryTypeOffset = 7,
            bsCatchRateOffset = 8, bsCommonHeldItemOffset = 12, bsRareHeldItemOffset = 14, bsGrowthCurveOffset = 19,
            bsAbility1Offset = 22, bsAbility2Offset = 23, bsTMHMCompatOffset = 28;

    public static final byte[] hgssStarterCodeSuffix = { 0x03, 0x03, 0x1A, 0x12, 0x1, 0x23, 0x0, 0x0 };

    public static final int[] hgssFilesWithRivalScript = { 7, 23, 96, 110, 819, 850, 866 };

    public static final byte[] hgssRivalScriptMagic = { (byte) 0xCE, 0x00, 0x0C, (byte) 0x80, 0x11, 0x00, 0x0C,
            (byte) 0x80, (byte) 152, 0, 0x1C, 0x00, 0x05 };

    public static final int[] ptFilesWithRivalScript = { 31, 36, 112, 123, 186, 427, 429, 1096 };

    public static final int[] dpFilesWithRivalScript = { 34, 90, 118, 180, 195, 394 };

    public static final byte[] dpptRivalScriptMagic = { (byte) 0xDE, 0x00, 0x0C, (byte) 0x80, 0x11, 0x00, 0x0C,
            (byte) 0x80, (byte) 0x83, 0x01, 0x1C, 0x00, 0x01 };

    public static final byte[] dpptTagBattleScriptMagic1 = { (byte) 0xDE, 0x00, 0x0C, (byte) 0x80, 0x28, 0x00, 0x04,
            (byte) 0x80 };

    public static final byte[] dpptTagBattleScriptMagic2 = { 0x11, 0x00, 0x0C, (byte) 0x80, (byte) 0x86, 0x01, 0x1C,
            0x00, 0x01 };

    public static final int[] ptFilesWithTagScript = { 2, 136, 201, 236 };

    public static final int[] dpFilesWithTagScript = { 2, 131, 230 };

    public static final int dpStarterStringIndex = 19, ptStarterStringIndex = 36;

    public static final int chikoritaIndex = 152, cyndaquilIndex = 155, totodileIndex = 158, turtwigIndex = 387,
            chimcharIndex = 390, piplupIndex = 393, slowpokeIndex = 79;

    public static final int fossilCount = 7;

    public static final String dpptTMDataPrefix = "D100D200D300D400", hgssTMDataPrefix = "1E003200";

    public static final int tmCount = 92, hmCount = 8;

    public static final int tmItemOffset = 328;

    public static final int textCharsPerLine = 40;

    public static final String dpItemPalettesPrefix = "8D018E01210132018D018F0122013301",
            pthgssItemPalettesPrefix = "8D018E01210133018D018F0122013401";

    public static final int evolutionMethodCount = 26;

    public static final int waterStoneIndex = 84, leafStoneIndex = 85, dawnStoneIndex = 109;

    public static final int highestAbilityIndex = 123;

    public static final int dpptSetVarScript = 0x28, hgssSetVarScript = 0x29;

    public static final int scriptListTerminator = 0xFD13;

    public static final int itemScriptVariable = 0x8008;
    
    public static final int luckyEggIndex = 0xE7;

    // The original slot each of the 20 "alternate" slots is mapped to
    // swarmx2, dayx2, nightx2, pokeradarx4, GBAx10
    // NOTE: in the game data there are 6 fillers between pokeradar and GBA

    public static final int[] dpptAlternateSlots = new int[] { 0, 1, 2, 3, 2, 3, 4, 5, 10, 11, 8, 9, 8, 9, 8, 9, 8, 9,
            8, 9 };

    public static final String[] dpptWaterSlotSetNames = new String[] { "Surfing", "Filler", "Old Rod", "Good Rod",
            "Super Rod" };

    public static final String[] hgssTimeOfDayNames = new String[] { "Morning", "Day", "Night" };

    public static final String[] hgssNonGrassSetNames = new String[] { "", "Surfing", "Rock Smash", "Old Rod",
            "Good Rod", "Super Rod" };

    public static final MoveCategory[] moveCategoryIndices = { MoveCategory.PHYSICAL, MoveCategory.SPECIAL,
            MoveCategory.STATUS };

    public static byte moveCategoryToByte(MoveCategory cat) {
        switch (cat) {
        case PHYSICAL:
            return 0;
        case SPECIAL:
            return 1;
        case STATUS:
        default:
            return 2;
        }
    }

    public static final List<Integer> dpRequiredFieldTMs = Arrays.asList(new Integer[] { 2, 3, 5, 9, 12, 19, 23, 28,
            34, 39, 41, 43, 46, 47, 49, 50, 62, 69, 79, 80, 82, 84, 85, 87 });

    public static final List<Integer> ptRequiredFieldTMs = Arrays.asList(new Integer[] { 2, 3, 5, 7, 9, 11, 12, 18, 19,
            23, 28, 34, 37, 39, 41, 43, 46, 47, 49, 50, 62, 69, 79, 80, 82, 84, 85, 87 });

    // DPPt:
    // cut, fly, surf, strength, flash, dig, teleport, waterfall,
    // rock smash, sweet scent, defog, rock climb
    public static final List<Integer> dpptFieldMoves = Arrays.asList(15, 19, 57, 70, 148, 91, 100, 127, 249, 230, 432,
            431);

    public static final List<Integer> hgssFieldMoves = Arrays.asList(15, 19, 57, 70, 148, 91, 100, 250, 127, 249, 29,
            230, 431);
    // DPPt: rock smash, cut
    public static final List<Integer> dpptEarlyRequiredHMMoves = Arrays.asList(249, 15);
    // HGSS: just cut
    public static final List<Integer> hgssEarlyRequiredHMMoves = Arrays.asList(15);

    public static ItemList allowedItems, nonBadItems;

    static {
        setupAllowedItems();
    }

    private static void setupAllowedItems() {
        allowedItems = new ItemList(536);
        // Key items + version exclusives
        allowedItems.banRange(428, 109);
        // Unknown blank items or version exclusives
        allowedItems.banRange(112, 23);
        // HMs
        allowedItems.banRange(420, 8);
        // TMs
        allowedItems.tmRange(328, 92);

        // non-bad items
        // ban specific pokemon hold items, berries, apricorns, mail
        nonBadItems = allowedItems.copy();

        nonBadItems.banSingles(0x6F, 0x70, 0xEC, 0x9B);
        nonBadItems.banRange(0x5F, 4); // mulch
        nonBadItems.banRange(0x87, 2); // orbs
        nonBadItems.banRange(0x89, 12); // mails
        nonBadItems.banRange(0x9F, 54); // berries DansGame
        nonBadItems.banRange(0x100, 4); // pokemon specific
        nonBadItems.banRange(0x104, 5); // contest scarves
    }

    public static final Type[] typeTable = constructTypeTable();

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
        table[0x0A] = Type.FIRE;
        table[0x0B] = Type.WATER;
        table[0x0C] = Type.GRASS;
        table[0x0D] = Type.ELECTRIC;
        table[0x0E] = Type.PSYCHIC;
        table[0x0F] = Type.ICE;
        table[0x10] = Type.DRAGON;
        table[0x11] = Type.DARK;
        return table;
    }

    public static byte typeToByte(Type type) {
        if (type == null) {
            return 0x09; // ???-type
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
            return 0x0A;
        case WATER:
            return 0x0B;
        case GRASS:
            return 0x0C;
        case ELECTRIC:
            return 0x0D;
        case PSYCHIC:
            return 0x0E;
        case ICE:
            return 0x0F;
        case DRAGON:
            return 0x10;
        case STEEL:
            return 0x08;
        case DARK:
            return 0x11;
        default:
            return 0; // normal by default
        }
    }

    public static void tagTrainersDP(List<Trainer> trs) {
        // Gym Trainers
        tag(trs, "GYM1", 0xf4, 0xf5);
        tag(trs, "GYM2", 0x144, 0x103, 0x104, 0x15C);
        tag(trs, "GYM3", 0x135, 0x136, 0x137, 0x138);
        tag(trs, "GYM4", 0x1f1, 0x1f2, 0x191, 0x153, 0x125, 0x1E3);
        tag(trs, "GYM5", 0x165, 0x145, 0x10a, 0x14a, 0x154, 0x157, 0x118, 0x11c);
        tag(trs, "GYM6", 0x13a, 0x100, 0x101, 0x117, 0x16f, 0xe8, 0x11b);
        tag(trs, "GYM7", 0x10c, 0x10d, 0x10e, 0x10f, 0x33b, 0x33c);
        tag(trs, "GYM8", 0x158, 0x155, 0x12d, 0x12e, 0x12f, 0x11d, 0x119);

        // Gym Leaders
        tag(trs, 0xf6, "GYM1");
        tag(trs, 0x13b, "GYM2");
        tag(trs, 0x13d, "GYM3"); // Maylene
        tag(trs, 0x13c, "GYM4"); // Wake
        tag(trs, 0x13e, "GYM5"); // Fantina
        tag(trs, 0xfa, "GYM6"); // Byron
        tag(trs, 0x13f, "GYM7"); // Candice
        tag(trs, 0x140, "GYM8"); // Volkner

        // Elite 4
        tag(trs, 0x105, "ELITE1");
        tag(trs, 0x106, "ELITE2");
        tag(trs, 0x107, "ELITE3");
        tag(trs, 0x108, "ELITE4");
        tag(trs, 0x10b, "CHAMPION");

        // Rival battles (8)
        tagRivalConsecutive(trs, "RIVAL1", 0xf8);
        tagRivalConsecutive(trs, "RIVAL2", 0x1d7);
        tagRivalConsecutive(trs, "RIVAL3", 0x1da);
        tagRivalConsecutive(trs, "RIVAL4", 0x1dd);
        // Tag battle is not following ze usual format
        tag(trs, 0x26b, "RIVAL5-0");
        tag(trs, 0x26c, "RIVAL5-1");
        tag(trs, 0x25f, "RIVAL5-2");
        // Back to normal
        tagRivalConsecutive(trs, "RIVAL6", 0x1e0);
        tagRivalConsecutive(trs, "RIVAL7", 0x346);
        tagRivalConsecutive(trs, "RIVAL8", 0x349);

        // Themed
        tag(trs, "THEMED:CYRUS", 0x193, 0x194);
        tag(trs, "THEMED:MARS", 0x127, 0x195, 0x210);
        tag(trs, "THEMED:JUPITER", 0x196, 0x197);
        tag(trs, "THEMED:SATURN", 0x198, 0x199);

        // Lucas & Dawn tag battles
        tagFriendConsecutive(trs, "FRIEND1", 0x265);
        tagFriendConsecutive(trs, "FRIEND1", 0x268);
        tagFriendConsecutive2(trs, "FRIEND2", 0x26D);
        tagFriendConsecutive2(trs, "FRIEND2", 0x270);

    }

    public static void tagTrainersPt(List<Trainer> trs) {
        // Gym Trainers
        tag(trs, "GYM1", 0xf4, 0xf5);
        tag(trs, "GYM2", 0x144, 0x103, 0x104, 0x15C);
        tag(trs, "GYM3", 0x165, 0x145, 0x154, 0x157, 0x118, 0x11c);
        tag(trs, "GYM4", 0x135, 0x136, 0x137, 0x138);
        tag(trs, "GYM5", 0x1f1, 0x1f2, 0x191, 0x153, 0x125, 0x1E3);
        tag(trs, "GYM6", 0x13a, 0x100, 0x101, 0x117, 0x16f, 0xe8, 0x11b);
        tag(trs, "GYM7", 0x10c, 0x10d, 0x10e, 0x10f, 0x33b, 0x33c);
        tag(trs, "GYM8", 0x158, 0x155, 0x12d, 0x12e, 0x12f, 0x11d, 0x119, 0x14b);

        // Gym Leaders
        tag(trs, 0xf6, "GYM1");
        tag(trs, 0x13b, "GYM2");
        tag(trs, 0x13e, "GYM3"); // Fantina
        tag(trs, 0x13d, "GYM4"); // Maylene
        tag(trs, 0x13c, "GYM5"); // Wake
        tag(trs, 0xfa, "GYM6"); // Byron
        tag(trs, 0x13f, "GYM7"); // Candice
        tag(trs, 0x140, "GYM8"); // Volkner

        // Elite 4
        tag(trs, 0x105, "ELITE1");
        tag(trs, 0x106, "ELITE2");
        tag(trs, 0x107, "ELITE3");
        tag(trs, 0x108, "ELITE4");
        tag(trs, 0x10b, "CHAMPION");

        // Rival battles (10)
        tagRivalConsecutive(trs, "RIVAL1", 0x353);
        tagRivalConsecutive(trs, "RIVAL2", 0xf8);
        tagRivalConsecutive(trs, "RIVAL3", 0x1d7);
        tagRivalConsecutive(trs, "RIVAL4", 0x1da);
        tagRivalConsecutive(trs, "RIVAL5", 0x1dd);
        // Tag battle is not following ze usual format
        tag(trs, 0x26b, "RIVAL6-0");
        tag(trs, 0x26c, "RIVAL6-1");
        tag(trs, 0x25f, "RIVAL6-2");
        // Back to normal
        tagRivalConsecutive(trs, "RIVAL7", 0x1e0);
        tagRivalConsecutive(trs, "RIVAL8", 0x346);
        tagRivalConsecutive(trs, "RIVAL9", 0x349);
        tagRivalConsecutive(trs, "RIVAL10", 0x368);

        // Battleground Gym Leaders
        tag(trs, 0x35A, "GYM1");
        tag(trs, 0x359, "GYM2");
        tag(trs, 0x35C, "GYM3");
        tag(trs, 0x356, "GYM4");
        tag(trs, 0x35B, "GYM5");
        tag(trs, 0x358, "GYM6");
        tag(trs, 0x355, "GYM7");
        tag(trs, 0x357, "GYM8");

        // Match vs Volkner and Flint in Battle Frontier
        tag(trs, 0x399, "GYM8");
        tag(trs, 0x39A, "ELITE3");

        // E4 rematch
        tag(trs, 0x362, "ELITE1");
        tag(trs, 0x363, "ELITE2");
        tag(trs, 0x364, "ELITE3");
        tag(trs, 0x365, "ELITE4");
        tag(trs, 0x366, "CHAMPION");

        // Themed
        tag(trs, "THEMED:CYRUS", 0x391, 0x193, 0x194);
        tag(trs, "THEMED:MARS", 0x127, 0x195, 0x210, 0x39e);
        tag(trs, "THEMED:JUPITER", 0x196, 0x197, 0x39f);
        tag(trs, "THEMED:SATURN", 0x198, 0x199);

        // Lucas & Dawn tag battles
        tagFriendConsecutive(trs, "FRIEND1", 0x265);
        tagFriendConsecutive(trs, "FRIEND1", 0x268);
        tagFriendConsecutive2(trs, "FRIEND2", 0x26D);
        tagFriendConsecutive2(trs, "FRIEND2", 0x270);

    }

    public static void tagTrainersHGSS(List<Trainer> trs) {
        // Gym Trainers
        tag(trs, "GYM1", 0x32, 0x1D);
        tag(trs, "GYM2", 0x43, 0x44, 0x45, 0x0a);
        tag(trs, "GYM3", 0x05, 0x46, 0x47, 0x16);
        tag(trs, "GYM4", 0x57, 0x58, 0x59, 0x2e);
        tag(trs, "GYM5", 0x9c, 0x9d, 0x9f, 0xfb);
        tag(trs, "GYM7", 0x1e0, 0x1e1, 0x1e2, 0x1e3, 0x1e4);
        tag(trs, "GYM8", 0x6e, 0x6f, 0x70, 0x75, 0x77);

        tag(trs, "GYM9", 0x134, 0x2ad);
        tag(trs, "GYM10", 0x2a4, 0x2a5, 0x2a6, 0x129, 0x12a);
        tag(trs, "GYM11", 0x18c, 0xe8, 0x151);
        tag(trs, "GYM12", 0x150, 0x146, 0x164, 0x15a);
        tag(trs, "GYM13", 0x53, 0x54, 0xb7, 0x88);
        tag(trs, "GYM14", 0x170, 0x171, 0xe6, 0x19f);
        tag(trs, "GYM15", 0x2b1, 0x2b2, 0x2b3, 0x2b4, 0x2b5, 0x2b6);
        tag(trs, "GYM16", 0x2a9, 0x2aa, 0x2ab, 0x2ac);

        // Gym Leaders
        tag(trs, 0x14, "GYM1");
        tag(trs, 0x15, "GYM2");
        tag(trs, 0x1e, "GYM3");
        tag(trs, 0x1f, "GYM4");
        tag(trs, 0x22, "GYM5");
        tag(trs, 0x21, "GYM6");
        tag(trs, 0x20, "GYM7");
        tag(trs, 0x23, "GYM8");

        tag(trs, 0xFD, "GYM9");
        tag(trs, 0xFE, "GYM10");
        tag(trs, 0xFF, "GYM11");
        tag(trs, 0x100, "GYM12");
        tag(trs, 0x101, "GYM13");
        tag(trs, 0x102, "GYM14");
        tag(trs, 0x103, "GYM15");
        tag(trs, 0x105, "GYM16");

        // Elite 4
        tag(trs, 0xf5, "ELITE1");
        tag(trs, 0xf7, "ELITE2");
        tag(trs, 0x1a2, "ELITE3");
        tag(trs, 0xf6, "ELITE4");
        tag(trs, 0xf4, "CHAMPION");

        // Red
        tag(trs, 0x104, "UBER");

        // Gym Rematches
        tag(trs, 0x2c8, "GYM1");
        tag(trs, 0x2c9, "GYM2");
        tag(trs, 0x2ca, "GYM3");
        tag(trs, 0x2cb, "GYM4");
        tag(trs, 0x2ce, "GYM5");
        tag(trs, 0x2cd, "GYM6");
        tag(trs, 0x2cc, "GYM7");
        tag(trs, 0x2cf, "GYM8");

        tag(trs, 0x2d0, "GYM9");
        tag(trs, 0x2d1, "GYM10");
        tag(trs, 0x2d2, "GYM11");
        tag(trs, 0x2d3, "GYM12");
        tag(trs, 0x2d4, "GYM13");
        tag(trs, 0x2d5, "GYM14");
        tag(trs, 0x2d6, "GYM15");
        tag(trs, 0x2d7, "GYM16");

        // Elite 4 Rematch
        tag(trs, 0x2be, "ELITE1");
        tag(trs, 0x2bf, "ELITE2");
        tag(trs, 0x2c0, "ELITE3");
        tag(trs, 0x2c1, "ELITE4");
        tag(trs, 0x2bd, "CHAMPION");

        // Rival Battles
        tagRivalConsecutive(trs, "RIVAL1", 0x1F0);

        tag(trs, 0x10a, "RIVAL2-0");
        tag(trs, 0x10d, "RIVAL2-1");
        tag(trs, 0x1, "RIVAL2-2");

        tag(trs, 0x10B, "RIVAL3-0");
        tag(trs, 0x10E, "RIVAL3-1");
        tag(trs, 0x107, "RIVAL3-2");

        tag(trs, 0x121, "RIVAL4-0");
        tag(trs, 0x10f, "RIVAL4-1");
        tag(trs, 0x120, "RIVAL4-2");

        tag(trs, 0x10C, "RIVAL5-0");
        tag(trs, 0x110, "RIVAL5-1");
        tag(trs, 0x108, "RIVAL5-2");

        tagRivalConsecutive(trs, "RIVAL6", 0x11e);
        tagRivalConsecutive(trs, "RIVAL7", 0x2e0); // dragons den tag battle
        tagRivalConsecutive(trs, "RIVAL8", 0x1EA);

        // Clair & Lance match in Dragons Den
        tag(trs, 0x2DE, "GYM8");
        tag(trs, 0x2DD, "CHAMPION");

        // Themed
        tag(trs, "THEMED:ARIANA", 0x1df, 0x1de);
        tag(trs, "THEMED:PETREL", 0x1e8, 0x1e7);
        tag(trs, "THEMED:PROTON", 0x1e6, 0x2c2);
        tag(trs, "THEMED:SPROUTTOWER", 0x2b, 0x33, 0x34, 0x35, 0x36, 0x37, 0x122);

    }

    private static void tag(List<Trainer> allTrainers, int number, String tag) {
        allTrainers.get(number - 1).tag = tag;
    }

    private static void tag(List<Trainer> allTrainers, String tag, int... numbers) {
        for (int num : numbers) {
            allTrainers.get(num - 1).tag = tag;
        }
    }

    private static void tagRivalConsecutive(List<Trainer> allTrainers, String tag, int offsetFire) {
        allTrainers.get(offsetFire - 1).tag = tag + "-0";
        allTrainers.get(offsetFire).tag = tag + "-1";
        allTrainers.get(offsetFire - 2).tag = tag + "-2";

    }

    private static void tagFriendConsecutive(List<Trainer> allTrainers, String tag, int offsetGrass) {
        allTrainers.get(offsetGrass - 1).tag = tag + "-1";
        allTrainers.get(offsetGrass).tag = tag + "-2";
        allTrainers.get(offsetGrass + 1).tag = tag + "-0";

    }

    private static void tagFriendConsecutive2(List<Trainer> allTrainers, String tag, int offsetWater) {
        allTrainers.get(offsetWater - 1).tag = tag + "-0";
        allTrainers.get(offsetWater).tag = tag + "-1";
        allTrainers.get(offsetWater + 1).tag = tag + "-2";

    }

}
