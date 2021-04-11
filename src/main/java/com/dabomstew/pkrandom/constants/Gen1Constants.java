package com.dabomstew.pkrandom.constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.Type;

/*----------------------------------------------------------------------------*/
/*--  Gen1Constants.java - holds values for games based on gen 1.           --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer" by Dabomstew                   --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2012.                                 --*/
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

public class Gen1Constants {

    public static final int baseStatsEntrySize = 0x1C;

    public static final int bsHPOffset = 1, bsAttackOffset = 2, bsDefenseOffset = 3, bsSpeedOffset = 4,
            bsSpecialOffset = 5, bsPrimaryTypeOffset = 6, bsSecondaryTypeOffset = 7, bsCatchRateOffset = 8,
            bsExpYieldOffset = 9, bsFrontSpriteOffset = 11, bsLevel1MovesOffset = 15, bsGrowthCurveOffset = 19,
            bsTMHMCompatOffset = 20;

    public static final int moonStoneIndex = 10, fireStoneIndex = 32, thunderStoneIndex = 33, waterStoneIndex = 34,
            leafStoneIndex = 47;
    
    public static final List<Integer> availableStones = Arrays.asList(
                Gen1Constants.moonStoneIndex, Gen1Constants.fireStoneIndex,
                Gen1Constants.waterStoneIndex, Gen1Constants.thunderStoneIndex,
                Gen1Constants.leafStoneIndex);

    public static final int mewIndex = 151, marowakIndex = 105;

    public static final int encounterTableEnd = 0xFFFF, encounterTableSize = 10, yellowSuperRodTableSize = 4;

    public static final int trainerClassCount = 47;

    public static final int champRivalOffsetFromGymLeaderMoves = 0x44;

    public static final int tmCount = 50, hmCount = 5;

    public static final int[] gymLeaderTMs = new int[] { 34, 11, 24, 21, 6, 46, 38, 27 };

    public static final int[] tclassesCounts = new int[] { 21, 47 };

    public static final List<Integer> singularTrainers = Arrays.asList(28, 32, 33, 34, 35, 36, 37, 38, 39, 43, 45, 46);

    public static final List<Integer> fieldMoves = Arrays.asList(15, 19, 57, 70, 148, 91, 100);

    public static final List<Integer> earlyRequiredHMs = Arrays.asList(15);

    public static final int hmsStartIndex = 0xC4, tmsStartIndex = 0xC9;

    public static final List<Integer> requiredFieldTMs = Arrays.asList(new Integer[] { 3, 4, 8, 10, 12, 14, 16, 19, 20,
            22, 25, 26, 30, 40, 43, 44, 45, 47 });

    public static final int towerMapsStartIndex = 0x90, towerMapsEndIndex = 0x94;

    public static final Map<String, Integer> bannedMoves = constructBannedList();

    public static final Map<String, Integer> extendedBannedMoves = constructExtendedBansList();

    private static Map<String, Integer> constructBannedList() {
        Map<String, Integer> bList = new HashMap();
        bList.put("SONICBOOM", 49);
        bList.put("DRAGON RAGE", 82);
        bList.put("SPORE", 147);
        return bList;
    }

    private static Map<String, Integer> constructExtendedBansList() {
        Map<String, Integer> exList = constructBannedList();
        exList.put("GUILLOTINE", 12);
        exList.put("HORN DRILL", 32);
        exList.put("FISSURE", 90);
        return exList;
    }

    public static final Type[] typeTable = constructTypeTable();

    private static Type[] constructTypeTable() {
        Type[] table = new Type[0x20];
        table[0x00] = Type.NORMAL;
        table[0x01] = Type.FIGHTING;
        table[0x02] = Type.FLYING;
        table[0x03] = Type.POISON;
        table[0x04] = Type.GROUND;
        table[0x05] = Type.ROCK;
        table[0x07] = Type.BUG;
        table[0x08] = Type.GHOST;
        table[0x14] = Type.FIRE;
        table[0x15] = Type.WATER;
        table[0x16] = Type.GRASS;
        table[0x17] = Type.ELECTRIC;
        table[0x18] = Type.PSYCHIC;
        table[0x19] = Type.ICE;
        table[0x1A] = Type.DRAGON;
        return table;
    }

    public static byte typeToByte(Type type) {
        for (int i = 0; i < typeTable.length; i++) {
            if (typeTable[i] == type) {
                return (byte) i;
            }
        }
        return (byte) 0;
    }

    public static final ItemList allowedItems = setupAllowedItems();

    private static ItemList setupAllowedItems() {
        ItemList allowedItems = new ItemList(250); // 251-255 are junk TMs
        // Assorted key items & junk
        // 23/01/2014: ban fake PP Up
        allowedItems.banSingles(5, 6, 7, 8, 9, 31, 48, 50, 59, 63, 64);
        allowedItems.banRange(21, 8);
        allowedItems.banRange(41, 5);
        allowedItems.banRange(69, 10);
        // Unused
        allowedItems.banRange(84, 112);
        // HMs
        allowedItems.banRange(hmsStartIndex, hmCount);
        // Real TMs
        allowedItems.tmRange(tmsStartIndex, tmCount);
        return allowedItems;
    }

    public static void tagTrainersUniversal(List<Trainer> trs) {
        // Gym Leaders
        tbc(trs, 34, 0, "GYM1");
        tbc(trs, 35, 0, "GYM2");
        tbc(trs, 36, 0, "GYM3");
        tbc(trs, 37, 0, "GYM4");
        tbc(trs, 38, 0, "GYM5");
        tbc(trs, 40, 0, "GYM6");
        tbc(trs, 39, 0, "GYM7");
        tbc(trs, 29, 2, "GYM8");

        // Other giovanni teams
        tbc(trs, 29, 0, "GIO1");
        tbc(trs, 29, 1, "GIO2");

        // Elite 4
        tbc(trs, 44, 0, "ELITE1");
        tbc(trs, 33, 0, "ELITE2");
        tbc(trs, 46, 0, "ELITE3");
        tbc(trs, 47, 0, "ELITE4");
    }

    public static void tagTrainersRB(List<Trainer> trs) {
        // Gary Battles
        tbc(trs, 25, 0, "RIVAL1-0");
        tbc(trs, 25, 1, "RIVAL1-1");
        tbc(trs, 25, 2, "RIVAL1-2");

        tbc(trs, 25, 3, "RIVAL2-0");
        tbc(trs, 25, 4, "RIVAL2-1");
        tbc(trs, 25, 5, "RIVAL2-2");

        tbc(trs, 25, 6, "RIVAL3-0");
        tbc(trs, 25, 7, "RIVAL3-1");
        tbc(trs, 25, 8, "RIVAL3-2");

        tbc(trs, 42, 0, "RIVAL4-0");
        tbc(trs, 42, 1, "RIVAL4-1");
        tbc(trs, 42, 2, "RIVAL4-2");

        tbc(trs, 42, 3, "RIVAL5-0");
        tbc(trs, 42, 4, "RIVAL5-1");
        tbc(trs, 42, 5, "RIVAL5-2");

        tbc(trs, 42, 6, "RIVAL6-0");
        tbc(trs, 42, 7, "RIVAL6-1");
        tbc(trs, 42, 8, "RIVAL6-2");

        tbc(trs, 42, 9, "RIVAL7-0");
        tbc(trs, 42, 10, "RIVAL7-1");
        tbc(trs, 42, 11, "RIVAL7-2");

        tbc(trs, 43, 0, "RIVAL8-0");
        tbc(trs, 43, 1, "RIVAL8-1");
        tbc(trs, 43, 2, "RIVAL8-2");

        // Gym Trainers
        tbc(trs, 5, 0, "GYM1");

        tbc(trs, 15, 0, "GYM2");
        tbc(trs, 6, 0, "GYM2");

        tbc(trs, 4, 7, "GYM3");
        tbc(trs, 20, 0, "GYM3");
        tbc(trs, 41, 2, "GYM3");

        tbc(trs, 3, 16, "GYM4");
        tbc(trs, 3, 17, "GYM4");
        tbc(trs, 6, 10, "GYM4");
        tbc(trs, 18, 0, "GYM4");
        tbc(trs, 18, 1, "GYM4");
        tbc(trs, 18, 2, "GYM4");
        tbc(trs, 32, 0, "GYM4");

        tbc(trs, 21, 2, "GYM5");
        tbc(trs, 21, 3, "GYM5");
        tbc(trs, 21, 6, "GYM5");
        tbc(trs, 21, 7, "GYM5");
        tbc(trs, 22, 0, "GYM5");
        tbc(trs, 22, 1, "GYM5");

        tbc(trs, 19, 0, "GYM6");
        tbc(trs, 19, 1, "GYM6");
        tbc(trs, 19, 2, "GYM6");
        tbc(trs, 19, 3, "GYM6");
        tbc(trs, 45, 21, "GYM6");
        tbc(trs, 45, 22, "GYM6");
        tbc(trs, 45, 23, "GYM6");

        tbc(trs, 8, 8, "GYM7");
        tbc(trs, 8, 9, "GYM7");
        tbc(trs, 8, 10, "GYM7");
        tbc(trs, 8, 11, "GYM7");
        tbc(trs, 11, 3, "GYM7");
        tbc(trs, 11, 4, "GYM7");
        tbc(trs, 11, 5, "GYM7");

        tbc(trs, 22, 2, "GYM8");
        tbc(trs, 22, 3, "GYM8");
        tbc(trs, 24, 5, "GYM8");
        tbc(trs, 24, 6, "GYM8");
        tbc(trs, 24, 7, "GYM8");
        tbc(trs, 31, 0, "GYM8");
        tbc(trs, 31, 8, "GYM8");
        tbc(trs, 31, 9, "GYM8");
    }

    public static void tagTrainersYellow(List<Trainer> trs) {
        // Rival Battles
        tbc(trs, 25, 0, "IRIVAL");

        tbc(trs, 25, 1, "RIVAL1-0");

        tbc(trs, 25, 2, "RIVAL2-0");

        tbc(trs, 42, 0, "RIVAL3-0");

        tbc(trs, 42, 1, "RIVAL4-0");
        tbc(trs, 42, 2, "RIVAL4-1");
        tbc(trs, 42, 3, "RIVAL4-2");

        tbc(trs, 42, 4, "RIVAL5-0");
        tbc(trs, 42, 5, "RIVAL5-1");
        tbc(trs, 42, 6, "RIVAL5-2");

        tbc(trs, 42, 7, "RIVAL6-0");
        tbc(trs, 42, 8, "RIVAL6-1");
        tbc(trs, 42, 9, "RIVAL6-2");

        tbc(trs, 43, 0, "RIVAL7-0");
        tbc(trs, 43, 1, "RIVAL7-1");
        tbc(trs, 43, 2, "RIVAL7-2");

        // Rocket Jessie & James
        tbc(trs, 30, 41, "THEMED:JESSIE&JAMES");
        tbc(trs, 30, 42, "THEMED:JESSIE&JAMES");
        tbc(trs, 30, 43, "THEMED:JESSIE&JAMES");
        tbc(trs, 30, 44, "THEMED:JESSIE&JAMES");

        // Gym Trainers
        tbc(trs, 5, 0, "GYM1");

        tbc(trs, 6, 0, "GYM2");
        tbc(trs, 15, 0, "GYM2");

        tbc(trs, 4, 7, "GYM3");
        tbc(trs, 20, 0, "GYM3");
        tbc(trs, 41, 2, "GYM3");

        tbc(trs, 3, 16, "GYM4");
        tbc(trs, 3, 17, "GYM4");
        tbc(trs, 6, 10, "GYM4");
        tbc(trs, 18, 0, "GYM4");
        tbc(trs, 18, 1, "GYM4");
        tbc(trs, 18, 2, "GYM4");
        tbc(trs, 32, 0, "GYM4");

        tbc(trs, 21, 2, "GYM5");
        tbc(trs, 21, 3, "GYM5");
        tbc(trs, 21, 6, "GYM5");
        tbc(trs, 21, 7, "GYM5");
        tbc(trs, 22, 0, "GYM5");
        tbc(trs, 22, 1, "GYM5");

        tbc(trs, 19, 0, "GYM6");
        tbc(trs, 19, 1, "GYM6");
        tbc(trs, 19, 2, "GYM6");
        tbc(trs, 19, 3, "GYM6");
        tbc(trs, 45, 21, "GYM6");
        tbc(trs, 45, 22, "GYM6");
        tbc(trs, 45, 23, "GYM6");

        tbc(trs, 8, 8, "GYM7");
        tbc(trs, 8, 9, "GYM7");
        tbc(trs, 8, 10, "GYM7");
        tbc(trs, 8, 11, "GYM7");
        tbc(trs, 11, 3, "GYM7");
        tbc(trs, 11, 4, "GYM7");
        tbc(trs, 11, 5, "GYM7");

        tbc(trs, 22, 2, "GYM8");
        tbc(trs, 22, 3, "GYM8");
        tbc(trs, 24, 5, "GYM8");
        tbc(trs, 24, 6, "GYM8");
        tbc(trs, 24, 7, "GYM8");
        tbc(trs, 31, 0, "GYM8");
        tbc(trs, 31, 8, "GYM8");
        tbc(trs, 31, 9, "GYM8");
    }

    private static void tbc(List<Trainer> allTrainers, int classNum, int number, String tag) {
        int currnum = -1;
        for (Trainer t : allTrainers) {
            if (t.getTrainerclass() == classNum) {
                currnum++;
                if (currnum == number) {
                    t.setTag(tag);
                    return;
                }
            }
        }
    }

}
