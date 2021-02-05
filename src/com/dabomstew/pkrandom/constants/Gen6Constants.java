package com.dabomstew.pkrandom.constants;

/*----------------------------------------------------------------------------*/
/*--  Gen6Constants.java - Constants for X/Y/Omega Ruby/Alpha Sapphire      --*/
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
            788, 789, 790, // Pumpkaboo
            791, 792, 793, // Gourgeist
            794, 795, 796, 797, // Floette (non-Eternal)
            815, 816, 817, 818, 819, 820 // Cosplay Pikachu
    );

    public static final Map<Integer,List<Integer>> speciesToMegaStoneXY = setupSpeciesToMegaStone(Type_XY);
    public static final Map<Integer,List<Integer>> speciesToMegaStoneORAS = setupSpeciesToMegaStone(Type_ORAS);

    public static final Map<Integer,String> formeSuffixes = setupFormeSuffixes();
    public static final Map<Integer,String> dummyFormeSuffixes = setupDummyFormeSuffixes();
    public static final Map<Integer,Map<Integer,String>> formeSuffixesByBaseForme = setupFormeSuffixesByBaseForme();

    public static String getFormeSuffixByBaseForme(int baseForme, int formNum) {
        return formeSuffixesByBaseForme.getOrDefault(baseForme,dummyFormeSuffixes).getOrDefault(formNum,"");
    }

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

    public static List<Integer> bannedMoves = Collections.singletonList(
            621 // Ban Hyperspace Fury
    );

    public static final Type[] typeTable = constructTypeTable();

    // Copied from pk3DS. "Dark Grass Held Item" should probably be renamed
    public static final int bsHPOffset = 0, bsAttackOffset = 1, bsDefenseOffset = 2, bsSpeedOffset = 3,
            bsSpAtkOffset = 4, bsSpDefOffset = 5, bsPrimaryTypeOffset = 6, bsSecondaryTypeOffset = 7,
            bsCatchRateOffset = 8, bsCommonHeldItemOffset = 12, bsRareHeldItemOffset = 14, bsDarkGrassHeldItemOffset = 16,
            bsGenderOffset = 18, bsGrowthCurveOffset = 21, bsAbility1Offset = 24, bsAbility2Offset = 25,
            bsAbility3Offset = 26, bsFormeOffset = 28, bsFormeSpriteOffset = 30, bsFormeCountOffset = 32,
            bsTMHMCompatOffset = 40, bsSpecialMTCompatOffset = 56, bsMTCompatOffset = 64;

    private static final int bsSizeXY = 0x40;
    private static final int bsSizeORAS = 0x50;

    public static final int evolutionMethodCount = 34;

    public static final int slowpokeIndex = 79, eeveeIndex = 133, karrablastIndex = 588, shelmetIndex = 616;

    public static final int sunStoneIndex = 80, moonStoneIndex = 81, waterStoneIndex = 84, luckyEggIndex = 0xE7;

    public static final int staticPokemonSize = 0xC;
    private static final int staticPokemonCountXY = 0xD;
    private static final int staticPokemonCountORAS = 0x3B;

    private static final int giftPokemonSizeXY = 0x18;
    private static final int giftPokemonSizeORAS = 0x24;
    private static final int giftPokemonCountXY = 0x13;
    private static final int giftPokemonCountORAS = 0x25;

    public static final String tmDataPrefix = "D400AE02AF02B002";
    public static final int tmCount = 100, tmBlockOneCount = 92, tmBlockTwoCount = 3, tmBlockThreeCount = 5,
            tmBlockOneOffset = 328, tmBlockTwoOffset = 618, tmBlockThreeOffset = 690, hmBlockOneCount = 5,
            rockSmashOffsetORAS = 10, diveOffsetORAS = 28;
    private static final int tmBlockTwoStartingOffsetXY = 97, tmBlockTwoStartingOffsetORAS = 98,
            hmCountXY = 5, hmCountORAS = 7;
    public static final int hiddenItemCountORAS = 170;
    public static final String hiddenItemsPrefixORAS = "A100A200A300A400A5001400010053004A0084000900";
    public static final String itemPalettesPrefix = "6F7461746500FF920A063F";
    private static final String shopItemsLocatorXY = "0400110004000300", shopItemsLocatorORAS = "04001100120004000300";

    public static final int masterBallIndex = 1;

    public static final int tutorMoveCount = 60;
    public static final String tutorsLocator = "C2015701A20012024401BA01";
    public static final String tutorsShopPrefix = "8A02000030000000";

    public static final int[] tutorSize = new int[]{15, 17, 16, 15};

    private static final String ingameTradesPrefixXY = "BA0A02015E000100BC0A150069000100";
    private static final String ingameTradesPrefixORAS = "810B7A0097000A00000047006B000A00";

    public static final int ingameTradeSize = 0x24;

    public static final String[] fastestTextPrefixes = new String[]{"1080BDE80000A0E31080BDE8F0412DE9", "485080E59C4040E24C50C0E5EC009FE5"};

    private static final List<Integer> mainGameShopsXY = Arrays.asList(
            10,11,12,13,16,17,20,21,24,25
    );

    private static final List<Integer> mainGameShopsORAS = Arrays.asList(
            10, 11, 13, 14, 16, 17, 18, 19, 20, 21
    );

    private static final List<String> shopNamesXY = Arrays.asList(
            "Primary 0 Badges",
            "Primary 1 Badges",
            "Primary 2 Badges",
            "Primary 3 Badges",
            "Primary 4 Badges",
            "Primary 5 Badges",
            "Primary 6 Badges",
            "Primary 7 Badges",
            "Primary 8 Badges",
            "Unused",
            "Lumiose Herboriste",
            "Lumiose Poké Ball Boutique",
            "Lumiose Stone Emporium",
            "Coumarine Incenses",
            "Aquacorde Poké Ball",
            "Aquacorde Potion",
            "Lumiose North Secondary",
            "Cyllage Secondary",
            "Shalour Secondary (TMs)",
            "Lumiose South Secondary (TMs)",
            "Laverre Secondary",
            "Snowbelle Secondary",
            "Kiloude Secondary (TMs)",
            "Anistar Secondary (TMs)",
            "Santalune Secondary",
            "Coumarine Secondary");

    private static final List<String> shopNamesORAS = Arrays.asList(
            "Primary 0 Badges (After Pokédex)",
            "Primary 1 Badges",
            "Primary 2 Badges",
            "Primary 3 Badges",
            "Primary 4 Badges",
            "Primary 5 Badges",
            "Primary 6 Badges",
            "Primary 7 Badges",
            "Primary 8 Badges",
            "Primary 0 Badges (Before Pokédex)",
            "Slateport Incenses",
            "Slateport Vitamins",
            "Slateport TMs",
            "Rustboro Secondary",
            "Slateport Secondary",
            "Mauville Secondary (TMs)",
            "Verdanturf Secondary",
            "Fallarbor Secondary",
            "Lavaridge Herbs",
            "Lilycove Dept. Store 2F Left",
            "Lilycove Dept. Store 3F Left",
            "Lilycove Dept. Store 3F Right",
            "Lilycove Dept. Store 4F Left (TMs)",
            "Lilycove Dept. Store 4F Right (TMs)");


    public static final List<Integer> evolutionItems = Arrays.asList(80,81,82,83,84,85,107,108,109,
            110,221,226,227,233,235,252,321,322,323,324,325,326,327,537,646,647);

    private static final List<Integer> requiredFieldTMsXY = Arrays.asList(
            1, 9, 40, 19, 65, 73, 69, 74, 81, 57, 61, 97, 95, 71, 79, 30, 31, 36, 53, 29, 22, 3, 2, 80, 26);

    private static final List<Integer> requiredFieldTMsORAS = Arrays.asList(
            37, 32, 62, 11, 86, 29, 59, 43, 53, 69, 6, 2, 13, 18, 22, 61, 30, 97, 7, 90, 26, 55, 34, 35, 64, 65, 66,
            74, 79, 80, 81, 84, 89, 91, 93, 95);

    public static final List<Integer> fieldMovesXY = Arrays.asList(15, 19, 57, 70, 148, 91, 100, 127, 230, 249);
    public static final List<Integer> fieldMovesORAS = Arrays.asList(15, 19, 57, 70, 148, 91, 100, 127, 230, 249, 290, 291);

    public static final int fallingEncounterOffset = 0xF4270, fallingEncounterCount = 55, fieldEncounterSize = 0x3C,
                            rustlingBushEncounterOffset = 0xF40CC, rustlingBushEncounterCount = 7;
    public static final Map<Integer, String> fallingEncounterNameMap = constructFallingEncounterNameMap();
    public static final Map<Integer, String> rustlingBushEncounterNameMap = constructRustlingBushEncounterNameMap();

    private static final String saveLoadFormeReversionPrefixXY = "09EB000094E5141094E54A0B80E2", saveLoadFormeReversionPrefixORAS = "09EB000094E5141094E5120A80E2";
    public static final String afterBattleFormeReversionPrefix = "E4FFFFEA0000000000000000";
    public static final String ninjaskSpeciesPrefix = "241094E5B810D1E1", shedinjaSpeciesPrefix = "C2FFFFEB0040A0E10020A0E3";
    public static final String boxLegendaryFunctionPrefixXY = "14D08DE20900A0E1";
    public static final int boxLegendaryEncounterFileXY = 341, boxLegendaryLocalScriptOffsetXY = 0x6E0;
    public static final int[] boxLegendaryCodeOffsetsXY = new int[]{ 144, 300, 584 };
    public static final String rayquazaFunctionPrefixORAS = "0900A0E1F08FBDE8";
    public static final int[] rayquazaScriptOffsetsORAS = new int[]{ 3334, 14734 }, rayquazaCodeOffsetsORAS = new int[]{ 136, 292, 576 };

    public static String getIngameTradesPrefix(int romType) {
        if (romType == Type_XY) {
            return ingameTradesPrefixXY;
        } else {
            return ingameTradesPrefixORAS;
        }
    }

    public static List<Integer> getRequiredFieldTMs(int romType) {
        if (romType == Type_XY) {
            return requiredFieldTMsXY;
        } else {
            return requiredFieldTMsORAS;
        }
    }

    public static List<Integer> getMainGameShops(int romType) {
        if (romType == Type_XY) {
            return mainGameShopsXY;
        } else {
            return mainGameShopsORAS;
        }
    }

    public static List<String> getShopNames(int romType) {
        if (romType == Type_XY) {
            return shopNamesXY;
        } else {
            return shopNamesORAS;
        }
    }

    public static int getBsSize(int romType) {
        if (romType == Type_XY) {
            return bsSizeXY;
        } else {
            return bsSizeORAS;
        }
    }

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

    public static int getStaticPokemonCount(int romType) {
        if (romType == Type_XY) {
            return staticPokemonCountXY;
        } else if (romType == Type_ORAS) {
            return staticPokemonCountORAS;
        }
        return staticPokemonCountXY;
    }

    public static int getGiftPokemonCount(int romType) {
        if (romType == Type_XY) {
            return giftPokemonCountXY;
        } else if (romType == Type_ORAS) {
            return giftPokemonCountORAS;
        }
        return giftPokemonCountXY;
    }

    public static int getGiftPokemonSize(int romType) {
        if (romType == Type_XY) {
            return giftPokemonSizeXY;
        } else if (romType == Type_ORAS) {
            return giftPokemonSizeORAS;
        }
        return giftPokemonSizeXY;
    }

    public static String getShopItemsLocator(int romType) {
        if (romType == Type_XY) {
            return shopItemsLocatorXY;
        } else if (romType == Type_ORAS) {
            return shopItemsLocatorORAS;
        }
        return shopItemsLocatorXY;
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

    public static String getSaveLoadFormeReversionPrefix(int romType) {
        if (romType == Type_XY) {
            return saveLoadFormeReversionPrefixXY;
        } else {
            return saveLoadFormeReversionPrefixORAS;
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

    public static ItemList allowedItemsXY, allowedItemsORAS, nonBadItems;
    public static List<Integer> regularShopItems, opShopItems;

    static {
        setupAllowedItems();
    }

    private static void setupAllowedItems() {
        allowedItemsXY = new ItemList(717);
        // Key items + version exclusives
        allowedItemsXY.banRange(428, 76);
        allowedItemsXY.banRange(505,32);
        allowedItemsXY.banRange(621, 18);
        allowedItemsXY.banSingles(216, 574, 578, 579, 616, 617);
        // Unknown blank items or version exclusives
        allowedItemsXY.banRange(113, 3);
        allowedItemsXY.banRange(120, 14);
        // TMs & HMs - tms cant be held in gen5
        allowedItemsXY.tmRange(328, 92);
        allowedItemsXY.tmRange(618, 3);
        allowedItemsXY.banRange(328, 100);
        allowedItemsXY.banRange(618, 3);
        // Battle Launcher exclusives
        allowedItemsXY.banRange(592, 24);

        // Key items (Gen 6)
        allowedItemsXY.banRange(641,3);
        allowedItemsXY.banSingles(651, 689);
        allowedItemsXY.banRange(695,4);
        allowedItemsXY.banRange(700,4);
        allowedItemsXY.banRange(705,3);
        allowedItemsXY.banRange(712,3);
        allowedItemsXY.banRange(716,2);

        // TMs (Gen 6)
        allowedItemsXY.tmRange(690,5);
        allowedItemsXY.banRange(690,5);

        allowedItemsORAS = allowedItemsXY.copy(775);
        // Key items and an HM
        allowedItemsORAS.banRange(718,34);
        allowedItemsORAS.banRange(765,2);
        allowedItemsORAS.banRange(771,5);

        // non-bad items
        // ban specific pokemon hold items, berries, apricorns, mail
        nonBadItems = allowedItemsXY.copy();

        nonBadItems.banSingles(0x6F, 0x70, 0xE1, 0xEC, 0x9B, 0x112, 0x23F, 0x2BB, 0x2C0);
        nonBadItems.banRange(0x5F, 4); // mulch
        nonBadItems.banRange(0x87, 2); // orbs
        nonBadItems.banRange(0x89, 12); // mails
        nonBadItems.banRange(0x9F, 25); // berries without useful battle effects
        nonBadItems.banRange(0x100, 4); // pokemon specific
        nonBadItems.banRange(0x104, 5); // contest scarves
        nonBadItems.banRange(0x248,7); // relic items
        nonBadItems.banRange(0x28C,4); // more mulch

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

    public static ItemList getAllowedItems(int romType) {
        if (romType == Type_XY) {
            return allowedItemsXY;
        } else {
            return allowedItemsORAS;
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

    public static void tagTrainersXY(List<Trainer> trs) {

        // Gym Trainers
        tag(trs,"GYM1", 39, 40, 48);
        tag(trs,"GYM2",64, 63, 106, 105);
        tag(trs,"GYM3",83, 84, 146, 147);
        tag(trs,"GYM4", 121, 122, 123, 124);
        tag(trs,"GYM5", 461, 462, 463, 464, 465, 466, 467, 468, 469, 28, 29, 30);
        tag(trs,"GYM6", 245, 250, 248, 243);
        tag(trs,"GYM7", 170, 171, 172, 365, 366);
        tag(trs,"GYM8", 168, 169, 31, 32);

        // Gym Leaders
        tag(trs,"GYM1-LEADER", 6);
        tag(trs,"GYM2-LEADER",76);
        tag(trs,"GYM3-LEADER",21);
        tag(trs,"GYM4-LEADER", 22);
        tag(trs,"GYM5-LEADER", 23);
        tag(trs,"GYM6-LEADER", 24);
        tag(trs,"GYM7-LEADER", 25);
        tag(trs,"GYM8-LEADER", 26);

        tag(trs, 188, "NOTSTRONG"); // Successor Korrina

        // Elite 4
        tag(trs, 269, "ELITE1"); // Malva
        tag(trs, 271, "ELITE2"); // Siebold
        tag(trs, 187, "ELITE3"); // Wikstrom
        tag(trs, 270, "ELITE4"); // Drasna
        tag(trs, 276, "CHAMPION"); // Diantha

        tag(trs,"THEMED:LYSANDRE-LEADER", 303, 525, 526);
        tag(trs,"STRONG", 174, 175, 304, 344, 345, 346, 347, 348, 349, 350, 351, 470, 471, 472, 473, 474, 475, 476, 477, 478, 479); // Team Flare Admins lol
        tag(trs,"STRONG", 324, 325, 438, 439, 573); // Tierno and Trevor
        tag(trs,"STRONG", 327, 328); // Sycamore

        // Rival - Serena
        tagRival(trs, "RIVAL1", 596);
        tagRival(trs, "RIVAL2", 575);
        tagRival(trs, "RIVAL3", 581);
        tagRival(trs, "RIVAL4", 578);
        tagRival(trs, "RIVAL5", 584);
        tagRival(trs, "RIVAL6", 607);
        tagRival(trs, "RIVAL7", 587);
        tagRival(trs, "RIVAL8", 590);
        tagRival(trs, "RIVAL9", 593);
        tagRival(trs, "RIVAL10", 599);

        // Rival - Calem
        tagRival(trs, "RIVAL1", 435);
        tagRival(trs, "RIVAL2", 130);
        tagRival(trs, "RIVAL3", 329);
        tagRival(trs, "RIVAL4", 184);
        tagRival(trs, "RIVAL5", 332);
        tagRival(trs, "RIVAL6", 604);
        tagRival(trs, "RIVAL7", 335);
        tagRival(trs, "RIVAL8", 338);
        tagRival(trs, "RIVAL9", 341);
        tagRival(trs, "RIVAL10", 519);

        // Rival - Shauna
        tagRival(trs, "FRIEND1", 137);
        tagRival(trs, "FRIEND2", 321);
    }

    public static void tagTrainersORAS(List<Trainer> trs) {

        // Gym Trainers & Leaders
        tag(trs,"GYM1",562, 22, 667);
        tag(trs,"GYM2",60, 56, 59);
        tag(trs,"GYM3",34, 568, 614, 35);
        tag(trs,"GYM4",81, 824, 83, 615, 823, 613, 85);
        tag(trs,"GYM5",63, 64, 65, 66, 67, 68, 69);
        tag(trs,"GYM6",115, 517, 516, 118, 730);
        tag(trs,"GYM7",157, 158, 159, 226, 320, 225);
        tag(trs,"GYM8",647, 342, 594, 646, 338, 339, 340, 341); // Includes Wallace in Delta Episode

        // Gym Leaders
        tag(trs,"GYM1-LEADER", 561);
        tag(trs,"GYM2-LEADER",563);
        tag(trs,"GYM3-LEADER",567);
        tag(trs,"GYM4-LEADER", 569);
        tag(trs,"GYM5-LEADER", 570);
        tag(trs,"GYM6-LEADER", 571);
        tag(trs,"GYM7-LEADER", 552);
        tag(trs,"GYM8-LEADER", 572, 943);

        // Elite 4
        tag(trs, "ELITE1", 553, 909); // Sidney
        tag(trs, "ELITE2", 554, 910); // Phoebe
        tag(trs, "ELITE3", 555, 911); // Glacia
        tag(trs, "ELITE4", 556, 912); // Drake
        tag(trs, "CHAMPION", 557, 913, 680, 942); // Steven (includes other appearances)

        tag(trs,"THEMED:MAXIE-LEADER", 235, 236, 271);
        tag(trs,"THEMED:ARCHIE-LEADER",178, 231, 266);
        tag(trs,"THEMED:MATT-STRONG",683, 684, 685, 686, 687);
        tag(trs,"THEMED:SHELLY-STRONG",688,689,690);
        tag(trs,"THEMED:TABITHA-STRONG",691,692,693);
        tag(trs,"THEMED:COURTNEY-STRONG",694,695,696,697,698);
        tag(trs, "THEMED:WALLY-STRONG", 518, 583, 944, 946);

        // Rival - Brendan
        tagRival(trs, "RIVAL1", 1);
        tagRival(trs, "RIVAL2", 289);
        tagRival(trs, "RIVAL3", 674);
        tagRival(trs, "RIVAL4", 292);
        tagRival(trs, "RIVAL5", 527);
        tagRival(trs, "RIVAL6", 699);

        // Rival - May
        tagRival(trs, "RIVAL1", 4);
        tagRival(trs, "RIVAL2", 295);
        tagRival(trs, "RIVAL3", 677);
        tagRival(trs, "RIVAL4", 298);
        tagRival(trs, "RIVAL5", 530);
        tagRival(trs, "RIVAL6", 906);
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

    public static void setCouldBeMultiBattleXY(List<Trainer> trs) {
        // 108 + 111: Team Flare Grunts in Glittering Cave
        // 348 + 350: Team Flare Celosia and Bryony fight in Poké Ball Factory
        // 438 + 439: Tierno and Trevor fight on Route 7
        // 470 + 611, 472 + 610, 476 + 612: Team Flare Admin and Grunt fights in Team Flare Secret HQ
        setCouldBeMultiBattle(trs, 108, 111, 348, 350, 438, 439, 470, 472, 476, 610, 611, 612);
    }

    public static void setCouldBeMultiBattleORAS(List<Trainer> trs) {
        // 683 + 904: Aqua Admin Matt and Team Aqua Grunt fight on the Southern Island
        // 687 + 905: Aqua Admin Matt and Team Aqua Grunt fight at the Mossdeep Space Center
        // 688 + 903: Aqua Admin Shelly and Team Aqua Grunt fight in Meteor Falls
        // 691 + 902: Magma Admin Tabitha and Team Magma Grunt fight in Meteor Falls
        // 694 + 900: Magma Admin Courtney and Team Magma Grunt fight on the Southern Island
        // 698 + 901: Magma Admin Courtney and Team Magma Grunt fight at the Mossdeep Space Center
        setCouldBeMultiBattle(trs, 683, 687, 688, 691, 694, 698, 900, 901, 902, 903, 904, 905);
    }

    private static void setCouldBeMultiBattle(List<Trainer> allTrainers, int... numbers) {
        for (int num : numbers) {
            if (allTrainers.size() > (num - 1)) {
                allTrainers.get(num - 1).couldBeMultiBattle = true;
            }
        }
    }

    private static Map<Integer, String> constructFallingEncounterNameMap() {
        Map<Integer, String> map = new TreeMap<>();
        map.put(0, "Glittering Cave Ceiling Encounter");
        map.put(4, "Reflection Cave Ceiling Encounter");
        map.put(20, "Victory Road Outside 2 Sky Encounter");
        map.put(24, "Victory Road Inside 2 Encounter");
        map.put(28, "Victory Road Outside 3 Sky Encounter");
        map.put(32, "Victory Road Inside 3 Ceiling Encounter");
        map.put(36, "Victory Road Outside 4 Sky Encounter");
        map.put(46, "Terminus Cave Ceiling Encounter");
        return map;
    }

    private static Map<Integer, String> constructRustlingBushEncounterNameMap() {
        Map<Integer, String> map = new TreeMap<>();
        map.put(0, "Route 6 Rustling Bush Encounter");
        map.put(3, "Route 18 Rustling Bush Encounter");
        return map;
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
            {504, 15}, // RageCandyBar
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
            {580, 1250}, // BalmMushroom
            {581, 2000}, // Big Nugget
            {582, 1500}, // Pearl String
            {583, 3000}, // Comet Shard
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
            {638, 0}, // Reveal Glass
            {639,200}, // Weakness Policy
            {640,600}, // Assault Vest
            {641,0}, // Holo Caster
            {642,0}, // Prof’s Letter
            {643,0}, // Roller Skates
            {644,200}, // Pixie Plate
            {645,500}, // Ability Capsule
            {646,300}, // Whipped Dream
            {647,300}, // Sachet
            {648,20}, // Luminous Moss
            {649,20}, // Snowball
            {650,300}, // Safety Goggles
            {651,0}, // Poké Flute
            {652,20}, // Rich Mulch
            {653,20}, // Surprise Mulch
            {654,20}, // Boost Mulch
            {655,20}, // Amaze Mulch
            {656,1000}, // Gengarite
            {657,1000}, // Gardevoirite
            {658,1000}, // Ampharosite
            {659,1000}, // Venusaurite
            {660,1000}, // Charizardite X
            {661,1000}, // Blastoisinite
            {662,2000}, // Mewtwonite X
            {663,2000}, // Mewtwonite Y
            {664,1000}, // Blazikenite
            {665,500}, // Medichamite
            {666,1000}, // Houndoominite
            {667,1000}, // Aggronite
            {668,500}, // Banettite
            {669,2000}, // Tyranitarite
            {670,1000}, // Scizorite
            {671,1000}, // Pinsirite
            {672,1000}, // Aerodactylite
            {673,1000}, // Lucarionite
            {674,500}, // Abomasite
            {675,500}, // Kangaskhanite
            {676,1000}, // Gyaradosite
            {677,500}, // Absolite
            {678,1000}, // Charizardite Y
            {679,1000}, // Alakazite
            {680,1000}, // Heracronite
            {681,300}, // Mawilite
            {682,500}, // Manectite
            {683,2000}, // Garchompite
            {684,2000}, // Latiasite
            {685,2000}, // Latiosite
            {686,100}, // Roseli Berry
            {687,100}, // Kee Berry
            {688,100}, // Maranga Berry
            {689,0}, // Sprinklotad
            {690,1000}, // TM96
            {691,1000}, // TM97
            {692,1000}, // TM98
            {693,1000}, // TM99
            {694,500}, // TM100
            {695,0}, // Power Plant Pass
            {696,0}, // Mega Ring
            {697,0}, // Intriguing Stone
            {698,0}, // Common Stone
            {699,2}, // Discount Coupon
            {700,0}, // Elevator Key
            {701,0}, // TMV Pass
            {702,0}, // Honor of Kalos
            {703,0}, // Adventure Rules
            {704,1}, // Strange Souvenir
            {705,0}, // Lens Case
            {706,0}, // Travel Trunk
            {707,0}, // Travel Trunk
            {708,45}, // Lumiose Galette
            {709,45}, // Shalour Sable
            {710,500}, // Jaw Fossil
            {711,500}, // Sail Fossil
            {712,0}, // Looker Ticket
            {713,0}, // Bike
            {714,0}, // Holo Caster
            {715,100}, // Fairy Gem
            {716,0}, // Mega Charm
            {717,0}, // Mega Glove
            {718,0}, // Mach Bike
            {719,0}, // Acro Bike
            {720,0}, // Wailmer Pail
            {721,0}, // Devon Parts
            {722,0}, // Soot Sack
            {723,0}, // Basement Key
            {724,0}, // Pokéblock Kit
            {725,0}, // Letter
            {726,0}, // Eon Ticket
            {727,0}, // Scanner
            {728,0}, // Go-Goggles
            {729,0}, // Meteorite
            {730,0}, // Key to Room 1
            {731,0}, // Key to Room 2
            {732,0}, // Key to Room 4
            {733,0}, // Key to Room 6
            {734,0}, // Storage Key
            {735,0}, // Devon Scope
            {736,0}, // S.S. Ticket
            {737,0}, // HM07
            {738,0}, // Devon Scuba Gear
            {739,0}, // Contest Costume
            {740,0}, // Contest Costume
            {741,0}, // Magma Suit
            {742,0}, // Aqua Suit
            {743,0}, // Pair of Tickets
            {744,0}, // Mega Bracelet
            {745,0}, // Mega Pendant
            {746,0}, // Mega Glasses
            {747,0}, // Mega Anchor
            {748,0}, // Mega Stickpin
            {749,0}, // Mega Tiara
            {750,0}, // Mega Anklet
            {751,0}, // Meteorite
            {752,1000}, // Swampertite
            {753,1000}, // Sceptilite
            {754,300}, // Sablenite
            {755,500}, // Altarianite
            {756,1000}, // Galladite
            {757,500}, // Audinite
            {758,2000}, // Metagrossite
            {759,500}, // Sharpedonite
            {760,500}, // Slowbronite
            {761,1000}, // Steelixite
            {762,500}, // Pidgeotite
            {763,500}, // Glalitite
            {764,2000}, // Diancite
            {765,0}, // Prison Bottle
            {766,0}, // Mega Cuff
            {767,500}, // Cameruptite
            {768,500}, // Lopunnite
            {769,2000}, // Salamencite
            {770,300}, // Beedrillite
            {771,0}, // Meteorite
            {772,0}, // Meteorite
            {773,0}, // Key Stone
            {774,0}, // Meteorite Shard
            {775,0}, // Eon Flute
    }).collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
}
