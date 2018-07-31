package com.dabomstew.pkrandom.constants;

import java.util.Arrays;
import java.util.List;

import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.Type;

public class Gen3Constants {

    public static final int RomType_Ruby = 0;
    public static final int RomType_Sapp = 1;
    public static final int RomType_Em = 2;
    public static final int RomType_FRLG = 3;

    public static final int size8M = 0x800000, size16M = 0x1000000, size32M = 0x2000000;

    public static final String unofficialEmeraldROMName = "YJencrypted";

    public static final int romNameOffset = 0xA0, romCodeOffset = 0xAC, romVersionOffset = 0xBC,
            headerChecksumOffset = 0xBD;

    public static final String wildPokemonPointerPrefix = "0348048009E00000FFFF0000";

    public static final String mapBanksPointerPrefix = "80180068890B091808687047";

    public static final String rsPokemonNamesPointerSuffix = "30B50025084CC8F7";

    public static final String frlgMapLabelsPointerPrefix = "AC470000AE470000B0470000";

    public static final String rseMapLabelsPointerPrefix = "C078288030BC01BC00470000";

    public static final String pokedexOrderPointerPrefix = "0448814208D0481C0004000C05E00000";

    public static final String rsFrontSpritesPointerPrefix = "05E0";

    public static final String rsFrontSpritesPointerSuffix = "1068191C";

    public static final String rsPokemonPalettesPointerPrefix = "04D90148006817E0";

    public static final String rsPokemonPalettesPointerSuffix = "080C064A11404840";

    public static final int efrlgPokemonNamesPointer = 0x144, efrlgMoveNamesPointer = 0x148,
            efrlgAbilityNamesPointer = 0x1C0, efrlgItemDataPointer = 0x1C8, efrlgMoveDataPointer = 0x1CC,
            efrlgPokemonStatsPointer = 0x1BC, efrlgFrontSpritesPointer = 0x128, efrlgPokemonPalettesPointer = 0x130;

    public static final byte[] emptyPokemonSig = new byte[] { 0x32, (byte) 0x96, 0x32, (byte) 0x96, (byte) 0x96, 0x32,
            0x00, 0x00, 0x03, 0x01, (byte) 0xAA, 0x0A, 0x00, 0x00, 0x00, 0x00, (byte) 0xFF, 0x78, 0x00, 0x00, 0x0F,
            0x0F, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00 };

    public static final int baseStatsEntrySize = 0x1C;

    public static final int bsHPOffset = 0, bsAttackOffset = 1, bsDefenseOffset = 2, bsSpeedOffset = 3,
            bsSpAtkOffset = 4, bsSpDefOffset = 5, bsPrimaryTypeOffset = 6, bsSecondaryTypeOffset = 7,
            bsCatchRateOffset = 8, bsCommonHeldItemOffset = 12, bsRareHeldItemOffset = 14, bsGenderRatioOffset = 16,
            bsGrowthCurveOffset = 19, bsAbility1Offset = 22, bsAbility2Offset = 23;

    public static final int textTerminator = 0xFF, textVariable = 0xFD;

    public static final byte freeSpaceByte = (byte) 0xFF;

    public static final int rseStarter2Offset = 2, rseStarter3Offset = 4, frlgStarter2Offset = 515,
            frlgStarter3Offset = 461, frlgStarterRepeatOffset = 5;

    public static final int frlgBaseStarter1 = 1, frlgBaseStarter2 = 4, frlgBaseStarter3 = 7;

    public static final int frlgStarterItemsOffset = 218;

    public static final int gbaAddRxOpcode = 0x30, gbaUnconditionalJumpOpcode = 0xE0, gbaSetRxOpcode = 0x20,
            gbaCmpRxOpcode = 0x28, gbaNopOpcode = 0x46C0;

    public static final int gbaR0 = 0, gbaR1 = 1, gbaR2 = 2, gbaR3 = 3, gbaR4 = 4, gbaR5 = 5, gbaR6 = 6, gbaR7 = 7;

    public static final Type[] typeTable = constructTypeTable();

    public static final int grassSlots = 12, surfingSlots = 5, rockSmashSlots = 5, fishingSlots = 10;

    public static final int unownIndex = 201, slowpokeIndex = 79, seadraIndex = 117, poliwhirlIndex = 61,
            mewIndex = 151, clamperlIndex = 366, huntailIndex = 367, gorebyssIndex = 368;

    public static final int tmCount = 50, hmCount = 8;

    public static final List<Integer> hmMoves = Arrays.asList(0xf, 0x13, 0x39, 0x46, 0x94, 0xf9, 0x7f, 0x123);

    public static final int tmItemOffset = 289;

    public static final int rseItemDescCharsPerLine = 18, frlgItemDescCharsPerLine = 24;

    public static final int regularTextboxCharsPerLine = 36;

    public static final int pointerSearchRadius = 500;

    public static final int itemDataDescriptionOffset = 0x14;

    public static final String deoxysObeyCode = "CD21490088420FD0";

    public static final int mewObeyOffsetFromDeoxysObey = 0x16;

    public static final String levelEvoKantoDexCheckCode = "972814DD";

    public static final String stoneEvoKantoDexCheckCode = "972808D9";

    public static final int levelEvoKantoDexJumpAmount = 0x14, stoneEvoKantoDexJumpAmount = 0x08;

    public static final String rsPokedexScriptIdentifier = "326629010803";

    public static final String rsNatDexScriptPart1 = "31720167";

    public static final String rsNatDexScriptPart2 = "32662901082B00801102006B02021103016B020211DABE4E020211675A6A02022A008003";

    public static final String frlgPokedexScriptIdentifier = "292908258101";

    public static final String frlgNatDexScript = "292908258101256F0103";

    public static final String frlgNatDexFlagChecker = "260D809301210D800100";

    public static final String frlgE4FlagChecker = "2B2C0800000000000000";

    public static final String frlgOaksLabKantoDexChecker = "257D011604800000260D80D400";

    public static final String frlgOaksLabFix = "257D011604800100";

    public static final String frlgOakOutsideHouseCheck = "1604800000260D80D4001908800580190980068083000880830109802109803C";

    public static final String frlgOakOutsideHouseFix = "1604800100";

    public static final String ePokedexScriptIdentifier = "3229610825F00129E40816CD40010003";

    public static final String eNatDexScriptPart1 = "31720167";

    public static final String eNatDexScriptPart2 = "3229610825F00129E40825F30116CD40010003";

    public static final int unhackedMaxPokedex = 411, unhackedRealPokedex = 386, hoennPokesStart = 252;

    public static final int evolutionMethodCount = 15;

    public static final int sunStoneIndex = 93, moonStoneIndex = 94, waterStoneIndex = 97;

    public static final int highestAbilityIndex = 77;

    public static final int frlgMapLabelsStart = 0x58;

    public static final List<Integer> rsRequiredFieldTMs = Arrays.asList(new Integer[] { 1, 2, 6, 7, 11, 18, 22, 23,
            26, 30, 37, 48 });

    public static final List<Integer> eRequiredFieldTMs = Arrays.asList(new Integer[] { 2, 6, 7, 11, 18, 22, 23, 30,
            37, 48 });

    public static final List<Integer> frlgRequiredFieldTMs = Arrays.asList(new Integer[] { 1, 2, 7, 8, 9, 11, 12, 14,
            17, 18, 21, 22, 25, 32, 36, 37, 40, 41, 44, 46, 47, 48, 49, 50 });

    public static final List<Integer> rseFieldMoves = Arrays.asList(15, 19, 57, 70, 148, 91, 100, 127, 249, 230, 291,
            290);

    public static final List<Integer> frlgFieldMoves = Arrays.asList(15, 19, 57, 70, 148, 91, 100, 127, 249, 230);

    public static final List<Integer> rseEarlyRequiredHMMoves = Arrays.asList(249);

    public static final List<Integer> frlgEarlyRequiredHMMoves = Arrays.asList(15);
    
    public static final int luckyEggIndex = 0xC5;

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

    public static ItemList allowedItems, nonBadItems;

    static {
        setupAllowedItems();
    }

    private static void setupAllowedItems() {
        allowedItems = new ItemList(376);
        // Key items (+1 unknown item)
        allowedItems.banRange(259, 30);
        allowedItems.banRange(349, 28);
        // Unknown blank items
        allowedItems.banRange(52, 11);
        allowedItems.banRange(87, 6);
        allowedItems.banRange(99, 4);
        allowedItems.banRange(112, 9);
        allowedItems.banRange(176, 3);
        allowedItems.banRange(226, 28);
        allowedItems.banRange(347, 2);
        allowedItems.banSingles(72, 82, 105, 267);
        // HMs
        allowedItems.banRange(339, 8);
        // TMs
        allowedItems.tmRange(289, 50);

        // non-bad items
        // ban specific pokemon hold items, berries, apricorns, mail
        nonBadItems = allowedItems.copy();
        nonBadItems.banSingles(0xCA, 0x8B); // light ball, oran berry
        nonBadItems.banRange(0x79, 12); // mail
        nonBadItems.banRange(0x8F, 33); // berries
        nonBadItems.banRange(0xDE, 4); // pokemon specific
        nonBadItems.banRange(0xFE, 5); // contest scarves
    }

    public static void trainerTagsRS(List<Trainer> trs, int romType) {
        // Gym Trainers
        tag(trs, "GYM1", 0x140, 0x141);
        tag(trs, "GYM2", 0x1AA, 0x1A9, 0xB3);
        tag(trs, "GYM3", 0xBF, 0x143, 0xC2, 0x289);
        tag(trs, "GYM4", 0xC9, 0x288, 0xCB, 0x28A, 0xCD);
        tag(trs, "GYM5", 0x47, 0x59, 0x49, 0x5A, 0x48, 0x5B, 0x4A);
        tag(trs, "GYM6", 0x191, 0x28F, 0x28E, 0x194);
        tag(trs, "GYM7", 0xE9, 0xEA, 0xEB, 0xF4, 0xF5, 0xF6);
        tag(trs, "GYM8", 0x82, 0x266, 0x83, 0x12D, 0x81, 0x74, 0x80, 0x265);

        // Gym Leaders
        tag(trs, 0x109, "GYM1");
        tag(trs, 0x10A, "GYM2");
        tag(trs, 0x10B, "GYM3");
        tag(trs, 0x10C, "GYM4");
        tag(trs, 0x10D, "GYM5");
        tag(trs, 0x10E, "GYM6");
        tag(trs, 0x10F, "GYM7");
        tag(trs, 0x110, "GYM8");
        // Elite 4
        tag(trs, 0x105, "ELITE1");
        tag(trs, 0x106, "ELITE2");
        tag(trs, 0x107, "ELITE3");
        tag(trs, 0x108, "ELITE4");
        tag(trs, 0x14F, "CHAMPION");
        // Brendan
        tag(trs, 0x208, "RIVAL1-2");
        tag(trs, 0x20B, "RIVAL1-0");
        tag(trs, 0x20E, "RIVAL1-1");

        tag(trs, 0x209, "RIVAL2-2");
        tag(trs, 0x20C, "RIVAL2-0");
        tag(trs, 0x20F, "RIVAL2-1");

        tag(trs, 0x20A, "RIVAL3-2");
        tag(trs, 0x20D, "RIVAL3-0");
        tag(trs, 0x210, "RIVAL3-1");

        tag(trs, 0x295, "RIVAL4-2");
        tag(trs, 0x296, "RIVAL4-0");
        tag(trs, 0x297, "RIVAL4-1");

        // May
        tag(trs, 0x211, "RIVAL1-2");
        tag(trs, 0x214, "RIVAL1-0");
        tag(trs, 0x217, "RIVAL1-1");

        tag(trs, 0x212, "RIVAL2-2");
        tag(trs, 0x215, "RIVAL2-0");
        tag(trs, 0x218, "RIVAL2-1");

        tag(trs, 0x213, "RIVAL3-2");
        tag(trs, 0x216, "RIVAL3-0");
        tag(trs, 0x219, "RIVAL3-1");

        tag(trs, 0x298, "RIVAL4-2");
        tag(trs, 0x299, "RIVAL4-0");
        tag(trs, 0x29A, "RIVAL4-1");

        if (romType == RomType_Ruby) {
            tag(trs, "THEMED:MAXIE", 0x259, 0x25A);
            tag(trs, "THEMED:COURTNEY", 0x257, 0x258);
            tag(trs, "THEMED:TABITHA", 0x254, 0x255);
        } else {
            tag(trs, "THEMED:ARCHIE", 0x23, 0x22);
            tag(trs, "THEMED:MATT", 0x1E, 0x1F);
            tag(trs, "THEMED:SHELLY", 0x20, 0x21);
        }

    }

    public static void trainerTagsE(List<Trainer> trs) {
        // Gym Trainers
        tag(trs, "GYM1", 0x140, 0x141, 0x23B);
        tag(trs, "GYM2", 0x1AA, 0x1A9, 0xB3, 0x23C, 0x23D, 0x23E);
        tag(trs, "GYM3", 0xBF, 0x143, 0xC2, 0x289, 0x322);
        tag(trs, "GYM4", 0x288, 0xC9, 0xCB, 0x28A, 0xCA, 0xCC, 0x1F5, 0xCD);
        tag(trs, "GYM5", 0x47, 0x59, 0x49, 0x5A, 0x48, 0x5B, 0x4A);
        tag(trs, "GYM6", 0x192, 0x28F, 0x191, 0x28E, 0x194, 0x323);
        tag(trs, "GYM7", 0xE9, 0xEA, 0xEB, 0xF4, 0xF5, 0xF6, 0x24F, 0x248, 0x247, 0x249, 0x246, 0x23F);
        tag(trs, "GYM8", 0x265, 0x80, 0x1F6, 0x73, 0x81, 0x76, 0x82, 0x12D, 0x83, 0x266);

        // Gym Leaders + Emerald Rematches!
        tag(trs, "GYM1", 0x109, 0x302, 0x303, 0x304, 0x305);
        tag(trs, "GYM2", 0x10A, 0x306, 0x307, 0x308, 0x309);
        tag(trs, "GYM3", 0x10B, 0x30A, 0x30B, 0x30C, 0x30D);
        tag(trs, "GYM4", 0x10C, 0x30E, 0x30F, 0x310, 0x311);
        tag(trs, "GYM5", 0x10D, 0x312, 0x313, 0x314, 0x315);
        tag(trs, "GYM6", 0x10E, 0x316, 0x317, 0x318, 0x319);
        tag(trs, "GYM7", 0x10F, 0x31A, 0x31B, 0x31C, 0x31D);
        tag(trs, "GYM8", 0x110, 0x31E, 0x31F, 0x320, 0x321);

        // Elite 4
        tag(trs, 0x105, "ELITE1");
        tag(trs, 0x106, "ELITE2");
        tag(trs, 0x107, "ELITE3");
        tag(trs, 0x108, "ELITE4");
        tag(trs, 0x14F, "CHAMPION");

        // Brendan
        tag(trs, 0x208, "RIVAL1-2");
        tag(trs, 0x20B, "RIVAL1-0");
        tag(trs, 0x20E, "RIVAL1-1");

        tag(trs, 0x251, "RIVAL2-2");
        tag(trs, 0x250, "RIVAL2-0");
        tag(trs, 0x257, "RIVAL2-1");

        tag(trs, 0x209, "RIVAL3-2");
        tag(trs, 0x20C, "RIVAL3-0");
        tag(trs, 0x20F, "RIVAL3-1");

        tag(trs, 0x20A, "RIVAL4-2");
        tag(trs, 0x20D, "RIVAL4-0");
        tag(trs, 0x210, "RIVAL4-1");

        tag(trs, 0x295, "RIVAL5-2");
        tag(trs, 0x296, "RIVAL5-0");
        tag(trs, 0x297, "RIVAL5-1");

        // May
        tag(trs, 0x211, "RIVAL1-2");
        tag(trs, 0x214, "RIVAL1-0");
        tag(trs, 0x217, "RIVAL1-1");

        tag(trs, 0x258, "RIVAL2-2");
        tag(trs, 0x300, "RIVAL2-0");
        tag(trs, 0x301, "RIVAL2-1");

        tag(trs, 0x212, "RIVAL3-2");
        tag(trs, 0x215, "RIVAL3-0");
        tag(trs, 0x218, "RIVAL3-1");

        tag(trs, 0x213, "RIVAL4-2");
        tag(trs, 0x216, "RIVAL4-0");
        tag(trs, 0x219, "RIVAL4-1");

        tag(trs, 0x298, "RIVAL5-2");
        tag(trs, 0x299, "RIVAL5-0");
        tag(trs, 0x29A, "RIVAL5-1");

        // Themed
        tag(trs, "THEMED:MAXIE", 0x259, 0x25A, 0x2DE);
        tag(trs, "THEMED:TABITHA", 0x202, 0x255, 0x2DC);
        tag(trs, "THEMED:ARCHIE", 0x22);
        tag(trs, "THEMED:MATT", 0x1E);
        tag(trs, "THEMED:SHELLY", 0x20, 0x21);

        // Steven
        tag(trs, 0x324, "UBER");

    }

    public static void trainerTagsFRLG(List<Trainer> trs) {

        // Gym Trainers
        tag(trs, "GYM1", 0x8E);
        tag(trs, "GYM2", 0xEA, 0x96);
        tag(trs, "GYM3", 0xDC, 0x8D, 0x1A7);
        tag(trs, "GYM4", 0x10A, 0x84, 0x109, 0xA0, 0x192, 0x10B, 0x85);
        tag(trs, "GYM5", 0x125, 0x124, 0x120, 0x127, 0x126, 0x121);
        tag(trs, "GYM6", 0x11A, 0x119, 0x1CF, 0x11B, 0x1CE, 0x1D0, 0x118);
        tag(trs, "GYM7", 0xD5, 0xB1, 0xB2, 0xD6, 0xB3, 0xD7, 0xB4);
        tag(trs, "GYM8", 0x129, 0x143, 0x188, 0x190, 0x142, 0x128, 0x191, 0x144);

        // Gym Leaders
        tag(trs, 0x19E, "GYM1");
        tag(trs, 0x19F, "GYM2");
        tag(trs, 0x1A0, "GYM3");
        tag(trs, 0x1A1, "GYM4");
        tag(trs, 0x1A2, "GYM5");
        tag(trs, 0x1A4, "GYM6");
        tag(trs, 0x1A3, "GYM7");
        tag(trs, 0x15E, "GYM8");

        // Giovanni
        tag(trs, 0x15C, "GIO1");
        tag(trs, 0x15D, "GIO2");

        // E4 Round 1
        tag(trs, 0x19A, "ELITE1-1");
        tag(trs, 0x19B, "ELITE2-1");
        tag(trs, 0x19C, "ELITE3-1");
        tag(trs, 0x19D, "ELITE4-1");

        // E4 Round 2
        tag(trs, 0x2DF, "ELITE1-2");
        tag(trs, 0x2E0, "ELITE2-2");
        tag(trs, 0x2E1, "ELITE3-2");
        tag(trs, 0x2E2, "ELITE4-2");

        // Rival Battles

        // Initial Rival
        tag(trs, 0x148, "RIVAL1-0");
        tag(trs, 0x146, "RIVAL1-1");
        tag(trs, 0x147, "RIVAL1-2");

        // Route 22 (weak)
        tag(trs, 0x14B, "RIVAL2-0");
        tag(trs, 0x149, "RIVAL2-1");
        tag(trs, 0x14A, "RIVAL2-2");

        // Cerulean
        tag(trs, 0x14E, "RIVAL3-0");
        tag(trs, 0x14C, "RIVAL3-1");
        tag(trs, 0x14D, "RIVAL3-2");

        // SS Anne
        tag(trs, 0x1AC, "RIVAL4-0");
        tag(trs, 0x1AA, "RIVAL4-1");
        tag(trs, 0x1AB, "RIVAL4-2");

        // Pokemon Tower
        tag(trs, 0x1AF, "RIVAL5-0");
        tag(trs, 0x1AD, "RIVAL5-1");
        tag(trs, 0x1AE, "RIVAL5-2");

        // Silph Co
        tag(trs, 0x1B2, "RIVAL6-0");
        tag(trs, 0x1B0, "RIVAL6-1");
        tag(trs, 0x1B1, "RIVAL6-2");

        // Route 22 (strong)
        tag(trs, 0x1B5, "RIVAL7-0");
        tag(trs, 0x1B3, "RIVAL7-1");
        tag(trs, 0x1B4, "RIVAL7-2");

        // E4 Round 1
        tag(trs, 0x1B8, "RIVAL8-0");
        tag(trs, 0x1B6, "RIVAL8-1");
        tag(trs, 0x1B7, "RIVAL8-2");

        // E4 Round 2
        tag(trs, 0x2E5, "RIVAL9-0");
        tag(trs, 0x2E3, "RIVAL9-1");
        tag(trs, 0x2E4, "RIVAL9-2");

    }

    private static void tag(List<Trainer> trainers, int trainerNum, String tag) {
        trainers.get(trainerNum - 1).tag = tag;
    }

    private static void tag(List<Trainer> allTrainers, String tag, int... numbers) {
        for (int num : numbers) {
            allTrainers.get(num - 1).tag = tag;
        }
    }

}
