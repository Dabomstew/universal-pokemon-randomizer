package com.dabomstew.pkrandom;

/*----------------------------------------------------------------------------*/
/*--  Settings.java - encapsulates a configuration of settings used by the  --*/
/*--                  randomizer to determine how to randomize the          --*/
/*--                  target game.                                          --*/
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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.zip.CRC32;

import javax.xml.bind.DatatypeConverter;

import com.dabomstew.pkrandom.pokemon.GenRestrictions;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.romhandlers.Gen1RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen2RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen3RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen5RomHandler;
import com.dabomstew.pkrandom.romhandlers.RomHandler;

public class Settings {

    public static final int VERSION = 172;

    public static final int LENGTH_OF_SETTINGS_DATA = 36;

    private CustomNamesSet customNames;

    private String romName;
    private boolean updatedFromOldVersion = false;
    private GenRestrictions currentRestrictions;
    private int currentMiscTweaks;

    private boolean changeImpossibleEvolutions;
    private boolean makeEvolutionsEasier;
    private boolean raceMode;
    private boolean blockBrokenMoves;
    private boolean limitPokemon;

    public enum BaseStatisticsMod {
        UNCHANGED, SHUFFLE, RANDOM,
    }

    private BaseStatisticsMod baseStatisticsMod = BaseStatisticsMod.UNCHANGED;
    private boolean standardizeEXPCurves;
    private boolean baseStatsFollowEvolutions;
    private boolean updateBaseStats;

    public enum AbilitiesMod {
        UNCHANGED, RANDOMIZE
    }

    private AbilitiesMod abilitiesMod = AbilitiesMod.UNCHANGED;
    private boolean allowWonderGuard = true;
    private boolean abilitiesFollowEvolutions;
    private boolean banTrappingAbilities;
    private boolean banNegativeAbilities;

    public enum StartersMod {
        UNCHANGED, CUSTOM, COMPLETELY_RANDOM, RANDOM_WITH_TWO_EVOLUTIONS
    }

    private StartersMod startersMod = StartersMod.UNCHANGED;

    // index in the rom's list of pokemon
    // offset from the dropdown index from RandomizerGUI by 1
    private int[] customStarters = new int[3];
    private boolean randomizeStartersHeldItems;
    private boolean banBadRandomStarterHeldItems;

    public enum TypesMod {
        UNCHANGED, RANDOM_FOLLOW_EVOLUTIONS, COMPLETELY_RANDOM
    }

    private TypesMod typesMod = TypesMod.UNCHANGED;

    // Evolutions
    public enum EvolutionsMod {
        UNCHANGED, RANDOM
    }

    private EvolutionsMod evolutionsMod = EvolutionsMod.UNCHANGED;
    private boolean evosSimilarStrength;
    private boolean evosSameTyping;
    private boolean evosMaxThreeStages;
    private boolean evosForceChange;

    // Move data
    private boolean randomizeMovePowers;
    private boolean randomizeMoveAccuracies;
    private boolean randomizeMovePPs;
    private boolean randomizeMoveTypes;
    private boolean randomizeMoveCategory;
    private boolean updateMoves;
    private boolean updateMovesLegacy;

    public enum MovesetsMod {
        UNCHANGED, RANDOM_PREFER_SAME_TYPE, COMPLETELY_RANDOM, METRONOME_ONLY
    }

    private MovesetsMod movesetsMod = MovesetsMod.UNCHANGED;
    private boolean startWithFourMoves;
    private boolean reorderDamagingMoves;
    private boolean movesetsForceGoodDamaging;
    private int movesetsGoodDamagingPercent = 0;

    public enum TrainersMod {
        UNCHANGED, RANDOM, TYPE_THEMED
    }

    private TrainersMod trainersMod = TrainersMod.UNCHANGED;
    private boolean rivalCarriesStarterThroughout;
    private boolean trainersUsePokemonOfSimilarStrength;
    private boolean trainersMatchTypingDistribution;
    private boolean trainersBlockLegendaries = true;
    private boolean trainersBlockEarlyWonderGuard = true;
    private boolean randomizeTrainerNames;
    private boolean randomizeTrainerClassNames;
    private boolean trainersForceFullyEvolved;
    private int trainersForceFullyEvolvedLevel = 30;
    private boolean trainersLevelModified;
    private int trainersLevelModifier = 0; // -50 ~ 50

    public enum WildPokemonMod {
        UNCHANGED, RANDOM, AREA_MAPPING, GLOBAL_MAPPING
    }

    public enum WildPokemonRestrictionMod {
        NONE, SIMILAR_STRENGTH, CATCH_EM_ALL, TYPE_THEME_AREAS
    }

    private WildPokemonMod wildPokemonMod = WildPokemonMod.UNCHANGED;
    private WildPokemonRestrictionMod wildPokemonRestrictionMod = WildPokemonRestrictionMod.NONE;
    private boolean useTimeBasedEncounters;
    private boolean blockWildLegendaries = true;
    private boolean useMinimumCatchRate;
    private int minimumCatchRateLevel = 1;
    private boolean randomizeWildPokemonHeldItems;
    private boolean banBadRandomWildPokemonHeldItems;

    public enum StaticPokemonMod {
        UNCHANGED, RANDOM_MATCHING, COMPLETELY_RANDOM
    }

    private StaticPokemonMod staticPokemonMod = StaticPokemonMod.UNCHANGED;

    public enum TMsMod {
        UNCHANGED, RANDOM
    }

    private TMsMod tmsMod = TMsMod.UNCHANGED;
    private boolean tmLevelUpMoveSanity;
    private boolean keepFieldMoveTMs;
    private boolean fullHMCompat;
    private boolean tmsForceGoodDamaging;
    private int tmsGoodDamagingPercent = 0;

    public enum TMsHMsCompatibilityMod {
        UNCHANGED, RANDOM_PREFER_TYPE, COMPLETELY_RANDOM, FULL
    }

    private TMsHMsCompatibilityMod tmsHmsCompatibilityMod = TMsHMsCompatibilityMod.UNCHANGED;

    public enum MoveTutorMovesMod {
        UNCHANGED, RANDOM
    }

    private MoveTutorMovesMod moveTutorMovesMod = MoveTutorMovesMod.UNCHANGED;
    private boolean tutorLevelUpMoveSanity;
    private boolean keepFieldMoveTutors;
    private boolean tutorsForceGoodDamaging;
    private int tutorsGoodDamagingPercent = 0;

    public enum MoveTutorsCompatibilityMod {
        UNCHANGED, RANDOM_PREFER_TYPE, COMPLETELY_RANDOM, FULL
    }

    private MoveTutorsCompatibilityMod moveTutorsCompatibilityMod = MoveTutorsCompatibilityMod.UNCHANGED;

    public enum InGameTradesMod {
        UNCHANGED, RANDOMIZE_GIVEN, RANDOMIZE_GIVEN_AND_REQUESTED
    }

    private InGameTradesMod inGameTradesMod = InGameTradesMod.UNCHANGED;
    private boolean randomizeInGameTradesNicknames;
    private boolean randomizeInGameTradesOTs;
    private boolean randomizeInGameTradesIVs;
    private boolean randomizeInGameTradesItems;

    public enum FieldItemsMod {
        UNCHANGED, SHUFFLE, RANDOM
    }

    private FieldItemsMod fieldItemsMod = FieldItemsMod.UNCHANGED;
    private boolean banBadRandomFieldItems;

    // to and from strings etc
    public void write(FileOutputStream out) throws IOException {
        out.write(VERSION);
        byte[] settings = toString().getBytes("UTF-8");
        out.write(settings.length);
        out.write(settings);
    }

    public static Settings read(FileInputStream in) throws IOException, UnsupportedOperationException {
        int version = in.read();
        if (version > VERSION) {
            throw new UnsupportedOperationException("Cannot read settings from a newer version of the randomizer.");
        }
        int length = in.read();
        byte[] buffer = FileFunctions.readFullyIntoBuffer(in, length);
        String settings = new String(buffer, "UTF-8");
        boolean oldUpdate = false;

        if (version < VERSION) {
            oldUpdate = true;
            settings = new SettingsUpdater().update(version, settings);
        }

        Settings settingsObj = fromString(settings);
        settingsObj.setUpdatedFromOldVersion(oldUpdate);
        return settingsObj;
    }

    @Override
    public String toString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // 0: general options #1 + trainer/class names
        out.write(makeByteSelected(changeImpossibleEvolutions, updateMoves, updateMovesLegacy, randomizeTrainerNames,
                randomizeTrainerClassNames, makeEvolutionsEasier));

        // 1: pokemon base stats & abilities
        out.write(makeByteSelected(baseStatsFollowEvolutions, baseStatisticsMod == BaseStatisticsMod.RANDOM,
                baseStatisticsMod == BaseStatisticsMod.SHUFFLE, baseStatisticsMod == BaseStatisticsMod.UNCHANGED,
                standardizeEXPCurves, updateBaseStats));

        // 2: pokemon types & more general options
        out.write(makeByteSelected(typesMod == TypesMod.RANDOM_FOLLOW_EVOLUTIONS,
                typesMod == TypesMod.COMPLETELY_RANDOM, typesMod == TypesMod.UNCHANGED, raceMode, blockBrokenMoves,
                limitPokemon));

        // 3: v171: changed to the abilities byte

        out.write(makeByteSelected(abilitiesMod == AbilitiesMod.UNCHANGED, abilitiesMod == AbilitiesMod.RANDOMIZE,
                allowWonderGuard, abilitiesFollowEvolutions, banTrappingAbilities, banNegativeAbilities));

        // 4: starter pokemon stuff
        out.write(makeByteSelected(startersMod == StartersMod.CUSTOM, startersMod == StartersMod.COMPLETELY_RANDOM,
                startersMod == StartersMod.UNCHANGED, startersMod == StartersMod.RANDOM_WITH_TWO_EVOLUTIONS,
                randomizeStartersHeldItems, banBadRandomStarterHeldItems));

        // @5 dropdowns
        write2ByteInt(out, customStarters[0] - 1);
        write2ByteInt(out, customStarters[1] - 1);
        write2ByteInt(out, customStarters[2] - 1);

        // 11 movesets
        out.write(makeByteSelected(movesetsMod == MovesetsMod.COMPLETELY_RANDOM,
                movesetsMod == MovesetsMod.RANDOM_PREFER_SAME_TYPE, movesetsMod == MovesetsMod.UNCHANGED,
                movesetsMod == MovesetsMod.METRONOME_ONLY, startWithFourMoves, reorderDamagingMoves));

        // 12 movesets good damaging
        out.write((movesetsForceGoodDamaging ? 0x80 : 0) | movesetsGoodDamagingPercent);

        // 13 trainer pokemon
        // changed 160
        out.write(makeByteSelected(trainersUsePokemonOfSimilarStrength, trainersMod == TrainersMod.RANDOM,
                rivalCarriesStarterThroughout, trainersMod == TrainersMod.TYPE_THEMED, trainersMatchTypingDistribution,
                trainersMod == TrainersMod.UNCHANGED, trainersBlockLegendaries, trainersBlockEarlyWonderGuard));

        // 14 trainer pokemon force evolutions
        out.write((trainersForceFullyEvolved ? 0x80 : 0) | trainersForceFullyEvolvedLevel);

        // 15 wild pokemon
        out.write(makeByteSelected(wildPokemonRestrictionMod == WildPokemonRestrictionMod.CATCH_EM_ALL,
                wildPokemonMod == WildPokemonMod.AREA_MAPPING,
                wildPokemonRestrictionMod == WildPokemonRestrictionMod.NONE,
                wildPokemonRestrictionMod == WildPokemonRestrictionMod.TYPE_THEME_AREAS,
                wildPokemonMod == WildPokemonMod.GLOBAL_MAPPING, wildPokemonMod == WildPokemonMod.RANDOM,
                wildPokemonMod == WildPokemonMod.UNCHANGED, useTimeBasedEncounters));

        // 16 wild pokemon 2
        // bugfix 161
        out.write(makeByteSelected(useMinimumCatchRate, blockWildLegendaries,
                wildPokemonRestrictionMod == WildPokemonRestrictionMod.SIMILAR_STRENGTH, randomizeWildPokemonHeldItems,
                banBadRandomWildPokemonHeldItems)
                | ((minimumCatchRateLevel - 1) << 5));

        // 17 static pokemon
        out.write(makeByteSelected(staticPokemonMod == StaticPokemonMod.UNCHANGED,
                staticPokemonMod == StaticPokemonMod.RANDOM_MATCHING,
                staticPokemonMod == StaticPokemonMod.COMPLETELY_RANDOM));

        // 18 tm randomization
        // new stuff 162
        out.write(makeByteSelected(tmsHmsCompatibilityMod == TMsHMsCompatibilityMod.COMPLETELY_RANDOM,
                tmsHmsCompatibilityMod == TMsHMsCompatibilityMod.RANDOM_PREFER_TYPE,
                tmsHmsCompatibilityMod == TMsHMsCompatibilityMod.UNCHANGED, tmsMod == TMsMod.RANDOM,
                tmsMod == TMsMod.UNCHANGED, tmLevelUpMoveSanity, keepFieldMoveTMs,
                tmsHmsCompatibilityMod == TMsHMsCompatibilityMod.FULL));

        // 19 tms part 2
        // new in 170
        out.write(makeByteSelected(fullHMCompat));

        // 20 tms good damaging
        out.write((tmsForceGoodDamaging ? 0x80 : 0) | tmsGoodDamagingPercent);

        // 21 move tutor randomization
        out.write(makeByteSelected(moveTutorsCompatibilityMod == MoveTutorsCompatibilityMod.COMPLETELY_RANDOM,
                moveTutorsCompatibilityMod == MoveTutorsCompatibilityMod.RANDOM_PREFER_TYPE,
                moveTutorsCompatibilityMod == MoveTutorsCompatibilityMod.UNCHANGED,
                moveTutorMovesMod == MoveTutorMovesMod.RANDOM, moveTutorMovesMod == MoveTutorMovesMod.UNCHANGED,
                tutorLevelUpMoveSanity, keepFieldMoveTutors,
                moveTutorsCompatibilityMod == MoveTutorsCompatibilityMod.FULL));

        // 22 tutors good damaging
        out.write((tutorsForceGoodDamaging ? 0x80 : 0) | tutorsGoodDamagingPercent);

        // new 150
        // 23 in game trades
        out.write(makeByteSelected(inGameTradesMod == InGameTradesMod.RANDOMIZE_GIVEN_AND_REQUESTED,
                inGameTradesMod == InGameTradesMod.RANDOMIZE_GIVEN, randomizeInGameTradesItems,
                randomizeInGameTradesIVs, randomizeInGameTradesNicknames, randomizeInGameTradesOTs,
                inGameTradesMod == InGameTradesMod.UNCHANGED));

        // 24 field items
        out.write(makeByteSelected(fieldItemsMod == FieldItemsMod.RANDOM, fieldItemsMod == FieldItemsMod.SHUFFLE,
                fieldItemsMod == FieldItemsMod.UNCHANGED, banBadRandomFieldItems));

        // new 170
        // 25 move randomizers
        out.write(makeByteSelected(randomizeMovePowers, randomizeMoveAccuracies, randomizeMovePPs, randomizeMoveTypes,
                randomizeMoveCategory));

        // 26 evolutions
        out.write(makeByteSelected(evolutionsMod == EvolutionsMod.UNCHANGED, evolutionsMod == EvolutionsMod.RANDOM,
                evosSimilarStrength, evosSameTyping, evosMaxThreeStages, evosForceChange));

        // @ 27 pokemon restrictions
        try {
            if (currentRestrictions != null) {
                writeFullInt(out, currentRestrictions.toInt());
            } else {
                writeFullInt(out, 0);
            }
        } catch (IOException e) {
        }

        // @ 31 misc tweaks
        try {
            writeFullInt(out, currentMiscTweaks);
        } catch (IOException e) {

        }

        // @ 35 trainer pokemon level modifier
        out.write((trainersLevelModified ? 0x80 : 0) | (trainersLevelModifier+50));

        try {
            byte[] romName = this.romName.getBytes("US-ASCII");
            out.write(romName.length);
            out.write(romName);
        } catch (UnsupportedEncodingException e) {
            out.write(0);
        } catch (IOException e) {
            out.write(0);
        }

        byte[] current = out.toByteArray();
        CRC32 checksum = new CRC32();
        checksum.update(current);

        try {
            writeFullInt(out, (int) checksum.getValue());
            writeFullInt(out, FileFunctions.getFileChecksum(SysConstants.customNamesFile));
        } catch (IOException e) {
        }

        return DatatypeConverter.printBase64Binary(out.toByteArray());
    }

    public static Settings fromString(String settingsString) throws UnsupportedEncodingException {
        byte[] data = DatatypeConverter.parseBase64Binary(settingsString);
        checkChecksum(data);

        Settings settings = new Settings();

        // Restore the actual controls
        settings.setChangeImpossibleEvolutions(restoreState(data[0], 0));
        settings.setUpdateMoves(restoreState(data[0], 1));
        settings.setUpdateMovesLegacy(restoreState(data[0], 2));
        settings.setRandomizeTrainerNames(restoreState(data[0], 3));
        settings.setRandomizeTrainerClassNames(restoreState(data[0], 4));
        settings.setMakeEvolutionsEasier(restoreState(data[0], 5));

        settings.setBaseStatisticsMod(restoreEnum(BaseStatisticsMod.class, data[1], 3, // UNCHANGED
                2, // SHUFFLE
                1 // RANDOM
        ));
        settings.setStandardizeEXPCurves(restoreState(data[1], 4));
        settings.setBaseStatsFollowEvolutions(restoreState(data[1], 0));
        settings.setUpdateBaseStats(restoreState(data[1], 5));

        settings.setTypesMod(restoreEnum(TypesMod.class, data[2], 2, // UNCHANGED
                0, // RANDOM_FOLLOW_EVOLUTIONS
                1 // COMPLETELY_RANDOM
        ));
        settings.setRaceMode(restoreState(data[2], 3));
        settings.setBlockBrokenMoves(restoreState(data[2], 4));
        settings.setLimitPokemon(restoreState(data[2], 5));

        settings.setAbilitiesMod(restoreEnum(AbilitiesMod.class, data[3], 0, // UNCHANGED
                1 // RANDOMIZE
        ));
        settings.setAllowWonderGuard(restoreState(data[3], 2));
        settings.setAbilitiesFollowEvolutions(restoreState(data[3], 3));
        settings.setBanTrappingAbilities(restoreState(data[3], 4));
        settings.setBanNegativeAbilities(restoreState(data[3], 5));

        settings.setStartersMod(restoreEnum(StartersMod.class, data[4], 2, // UNCHANGED
                0, // CUSTOM
                1, // COMPLETELY_RANDOM
                3 // RANDOM_WITH_TWO_EVOLUTIONS
        ));
        settings.setRandomizeStartersHeldItems(restoreState(data[4], 4));
        settings.setBanBadRandomStarterHeldItems(restoreState(data[4], 5));

        settings.setCustomStarters(new int[] { FileFunctions.read2ByteInt(data, 5) + 1,
                FileFunctions.read2ByteInt(data, 7) + 1, FileFunctions.read2ByteInt(data, 9) + 1 });

        settings.setMovesetsMod(restoreEnum(MovesetsMod.class, data[11], 2, // UNCHANGED
                1, // RANDOM_PREFER_SAME_TYPE
                0, // COMPLETELY_RANDOM
                3 // METRONOME_ONLY
        ));
        settings.setStartWithFourMoves(restoreState(data[11], 4));
        settings.setReorderDamagingMoves(restoreState(data[11], 5));

        settings.setMovesetsForceGoodDamaging(restoreState(data[12], 7));
        settings.setMovesetsGoodDamagingPercent(data[12] & 0x7F);

        // changed 160
        settings.setTrainersMod(restoreEnum(TrainersMod.class, data[13], 5, // UNCHANGED
                1, // RANDOM
                3 // TYPE_THEMED
        ));
        settings.setTrainersUsePokemonOfSimilarStrength(restoreState(data[13], 0));
        settings.setRivalCarriesStarterThroughout(restoreState(data[13], 2));
        settings.setTrainersMatchTypingDistribution(restoreState(data[13], 4));
        settings.setTrainersBlockLegendaries(restoreState(data[13], 6));
        settings.setTrainersBlockEarlyWonderGuard(restoreState(data[13], 7));

        settings.setTrainersForceFullyEvolved(restoreState(data[14], 7));
        settings.setTrainersForceFullyEvolvedLevel(data[14] & 0x7F);

        settings.setWildPokemonMod(restoreEnum(WildPokemonMod.class, data[15], 6, // UNCHANGED
                5, // RANDOM
                1, // AREA_MAPPING
                4 // GLOBAL_MAPPING
        ));
        settings.setWildPokemonRestrictionMod(getEnum(WildPokemonRestrictionMod.class, restoreState(data[15], 2), // NONE
                restoreState(data[16], 2), // SIMILAR_STRENGTH
                restoreState(data[15], 0), // CATCH_EM_ALL
                restoreState(data[15], 3) // TYPE_THEME_AREAS
        ));
        settings.setUseTimeBasedEncounters(restoreState(data[15], 7));

        settings.setUseMinimumCatchRate(restoreState(data[16], 0));
        settings.setBlockWildLegendaries(restoreState(data[16], 1));
        settings.setRandomizeWildPokemonHeldItems(restoreState(data[16], 3));
        settings.setBanBadRandomWildPokemonHeldItems(restoreState(data[16], 4));

        settings.setMinimumCatchRateLevel(((data[16] & 0x60) >> 5) + 1);

        settings.setStaticPokemonMod(restoreEnum(StaticPokemonMod.class, data[17], 0, // UNCHANGED
                1, // RANDOM_MATCHING
                2 // COMPLETELY_RANDOM
        ));

        settings.setTmsMod(restoreEnum(TMsMod.class, data[18], 4, // UNCHANGED
                3 // RANDOM
        ));
        settings.setTmsHmsCompatibilityMod(restoreEnum(TMsHMsCompatibilityMod.class, data[18], 2, // UNCHANGED
                1, // RANDOM_PREFER_TYPE
                0, // COMPLETELY_RANDOM
                7 // FULL
        ));
        settings.setTmLevelUpMoveSanity(restoreState(data[18], 5));
        settings.setKeepFieldMoveTMs(restoreState(data[18], 6));
        settings.setFullHMCompat(restoreState(data[19], 0));

        settings.setTmsForceGoodDamaging(restoreState(data[20], 7));
        settings.setTmsGoodDamagingPercent(data[20] & 0x7F);

        settings.setMoveTutorMovesMod(restoreEnum(MoveTutorMovesMod.class, data[21], 4, // UNCHANGED
                3 // RANDOM
        ));
        settings.setMoveTutorsCompatibilityMod(restoreEnum(MoveTutorsCompatibilityMod.class, data[21], 2, // UNCHANGED
                1, // RANDOM_PREFER_TYPE
                0, // COMPLETELY_RANDOM
                7 // FULL
        ));
        settings.setTutorLevelUpMoveSanity(restoreState(data[21], 5));
        settings.setKeepFieldMoveTutors(restoreState(data[21], 6));

        settings.setTutorsForceGoodDamaging(restoreState(data[22], 7));
        settings.setTutorsGoodDamagingPercent(data[22] & 0x7F);

        // new 150
        settings.setInGameTradesMod(restoreEnum(InGameTradesMod.class, data[23], 6, // UNCHANGED
                1, // RANDOMIZE_GIVEN
                0 // RANDOMIZE_GIVEN_AND_REQUESTED
        ));
        settings.setRandomizeInGameTradesItems(restoreState(data[23], 2));
        settings.setRandomizeInGameTradesIVs(restoreState(data[23], 3));
        settings.setRandomizeInGameTradesNicknames(restoreState(data[23], 4));
        settings.setRandomizeInGameTradesOTs(restoreState(data[23], 5));

        settings.setFieldItemsMod(restoreEnum(FieldItemsMod.class, data[24], 2, // UNCHANGED
                1, // SHUFFLE
                0 // RANDOM
        ));
        settings.setBanBadRandomFieldItems(restoreState(data[24], 3));

        // new 170
        settings.setRandomizeMovePowers(restoreState(data[25], 0));
        settings.setRandomizeMoveAccuracies(restoreState(data[25], 1));
        settings.setRandomizeMovePPs(restoreState(data[25], 2));
        settings.setRandomizeMoveTypes(restoreState(data[25], 3));
        settings.setRandomizeMoveCategory(restoreState(data[25], 4));

        settings.setEvolutionsMod(restoreEnum(EvolutionsMod.class, data[26], 0, // UNCHANGED
                1 // RANDOM
        ));
        settings.setEvosSimilarStrength(restoreState(data[26], 2));
        settings.setEvosSameTyping(restoreState(data[26], 3));
        settings.setEvosMaxThreeStages(restoreState(data[26], 4));
        settings.setEvosForceChange(restoreState(data[26], 5));

        // gen restrictions
        int genLimit = FileFunctions.readFullInt(data, 27);
        GenRestrictions restrictions = null;
        if (genLimit != 0) {
            restrictions = new GenRestrictions(genLimit);
        }
        settings.setCurrentRestrictions(restrictions);

        int codeTweaks = FileFunctions.readFullInt(data, 31);

        settings.setCurrentMiscTweaks(codeTweaks);

        settings.setTrainersLevelModified(restoreState(data[35], 7));
        settings.setTrainersLevelModifier((data[35] & 0x7F) - 50);

        int romNameLength = data[LENGTH_OF_SETTINGS_DATA] & 0xFF;
        String romName = new String(data, LENGTH_OF_SETTINGS_DATA + 1, romNameLength, "US-ASCII");
        settings.setRomName(romName);

        return settings;
    }

    public static class TweakForROMFeedback {
        private boolean changedStarter;
        private boolean removedCodeTweaks;

        public boolean isChangedStarter() {
            return changedStarter;
        }

        public TweakForROMFeedback setChangedStarter(boolean changedStarter) {
            this.changedStarter = changedStarter;
            return this;
        }

        public boolean isRemovedCodeTweaks() {
            return removedCodeTweaks;
        }

        public TweakForROMFeedback setRemovedCodeTweaks(boolean removedCodeTweaks) {
            this.removedCodeTweaks = removedCodeTweaks;
            return this;
        }
    }

    public TweakForROMFeedback tweakForRom(RomHandler rh) {

        TweakForROMFeedback feedback = new TweakForROMFeedback();

        // move update check
        if (this.isUpdateMovesLegacy() && rh instanceof Gen5RomHandler) {
            // don't actually update moves
            this.setUpdateMovesLegacy(false);
            this.setUpdateMoves(false);
        }

        // starters
        List<Pokemon> romPokemon = rh.getPokemon();
        List<Pokemon> romStarters = rh.getStarters();
        for (int starter = 0; starter < 3; starter++) {
            if (this.customStarters[starter] < 0 || this.customStarters[starter] >= romPokemon.size()) {
                // invalid starter for this game
                feedback.setChangedStarter(true);
                if (starter >= romStarters.size()) {
                    this.customStarters[starter] = 1;
                } else {
                    this.customStarters[starter] = romPokemon.indexOf(romStarters.get(starter));
                }
            }
        }

        // gen restrictions
        if (rh instanceof Gen1RomHandler || rh.isROMHack()) {
            this.currentRestrictions = null;
            this.setLimitPokemon(false);
        } else if (this.currentRestrictions != null) {
            this.currentRestrictions.limitToGen(rh.generationOfPokemon());
        }

        // misc tweaks
        int oldMiscTweaks = this.currentMiscTweaks;
        this.currentMiscTweaks &= rh.miscTweaksAvailable();

        if (oldMiscTweaks != this.currentMiscTweaks) {
            feedback.setRemovedCodeTweaks(true);
        }

        if (rh.abilitiesPerPokemon() == 0) {
            this.setAbilitiesMod(AbilitiesMod.UNCHANGED);
            this.setAllowWonderGuard(false);
        }

        if (!(rh instanceof Gen2RomHandler || rh instanceof Gen3RomHandler)) {
            // starter held items don't exist
            this.setRandomizeStartersHeldItems(false);
            this.setBanBadRandomStarterHeldItems(false);
        }

        if (!rh.supportsFourStartingMoves()) {
            this.setStartWithFourMoves(false);
        }

        if (rh instanceof Gen1RomHandler || rh instanceof Gen2RomHandler) {
            this.setTrainersBlockEarlyWonderGuard(false);
        }

        if (!rh.hasTimeBasedEncounters()) {
            this.setUseTimeBasedEncounters(false);
        }

        if (rh instanceof Gen1RomHandler) {
            this.setRandomizeWildPokemonHeldItems(false);
            this.setBanBadRandomWildPokemonHeldItems(false);
        }

        if (!rh.canChangeStaticPokemon()) {
            this.setStaticPokemonMod(StaticPokemonMod.UNCHANGED);
        }

        if (!rh.hasMoveTutors()) {
            this.setMoveTutorMovesMod(MoveTutorMovesMod.UNCHANGED);
            this.setMoveTutorsCompatibilityMod(MoveTutorsCompatibilityMod.UNCHANGED);
            this.setTutorLevelUpMoveSanity(false);
            this.setKeepFieldMoveTutors(false);
        }

        if (rh instanceof Gen1RomHandler) {
            // missing some ingame trade fields
            this.setRandomizeInGameTradesItems(false);
            this.setRandomizeInGameTradesIVs(false);
            this.setRandomizeInGameTradesOTs(false);
        }

        if (!rh.hasPhysicalSpecialSplit()) {
            this.setRandomizeMoveCategory(false);
        }

        // done
        return feedback;
    }

    // getters and setters

    public CustomNamesSet getCustomNames() {
        return customNames;
    }

    public Settings setCustomNames(CustomNamesSet customNames) {
        this.customNames = customNames;
        return this;
    }

    public String getRomName() {
        return romName;
    }

    public Settings setRomName(String romName) {
        this.romName = romName;
        return this;
    }

    public boolean isUpdatedFromOldVersion() {
        return updatedFromOldVersion;
    }

    public Settings setUpdatedFromOldVersion(boolean updatedFromOldVersion) {
        this.updatedFromOldVersion = updatedFromOldVersion;
        return this;
    }

    public GenRestrictions getCurrentRestrictions() {
        return currentRestrictions;
    }

    public Settings setCurrentRestrictions(GenRestrictions currentRestrictions) {
        this.currentRestrictions = currentRestrictions;
        return this;
    }

    public int getCurrentMiscTweaks() {
        return currentMiscTweaks;
    }

    public Settings setCurrentMiscTweaks(int currentMiscTweaks) {
        this.currentMiscTweaks = currentMiscTweaks;
        return this;
    }

    public boolean isUpdateMoves() {
        return updateMoves;
    }

    public Settings setUpdateMoves(boolean updateMoves) {
        this.updateMoves = updateMoves;
        return this;
    }

    public boolean isUpdateMovesLegacy() {
        return updateMovesLegacy;
    }

    public Settings setUpdateMovesLegacy(boolean updateMovesLegacy) {
        this.updateMovesLegacy = updateMovesLegacy;
        return this;
    }

    public boolean isChangeImpossibleEvolutions() {
        return changeImpossibleEvolutions;
    }

    public Settings setChangeImpossibleEvolutions(boolean changeImpossibleEvolutions) {
        this.changeImpossibleEvolutions = changeImpossibleEvolutions;
        return this;
    }

    public boolean isMakeEvolutionsEasier() {
        return makeEvolutionsEasier;
    }

    public Settings setMakeEvolutionsEasier(boolean makeEvolutionsEasier) {
        this.makeEvolutionsEasier = makeEvolutionsEasier;
        return this;
    }

    public boolean isRaceMode() {
        return raceMode;
    }

    public Settings setRaceMode(boolean raceMode) {
        this.raceMode = raceMode;
        return this;
    }

    public boolean doBlockBrokenMoves() {
        return blockBrokenMoves;
    }

    public Settings setBlockBrokenMoves(boolean blockBrokenMoves) {
        this.blockBrokenMoves = blockBrokenMoves;
        return this;
    }

    public boolean isLimitPokemon() {
        return limitPokemon;
    }

    public Settings setLimitPokemon(boolean limitPokemon) {
        this.limitPokemon = limitPokemon;
        return this;
    }

    public BaseStatisticsMod getBaseStatisticsMod() {
        return baseStatisticsMod;
    }

    public Settings setBaseStatisticsMod(BaseStatisticsMod baseStatisticsMod) {
        this.baseStatisticsMod = baseStatisticsMod;
        return this;
    }

    public Settings setBaseStatisticsMod(boolean... bools) {
        return setBaseStatisticsMod(getEnum(BaseStatisticsMod.class, bools));
    }

    public boolean isBaseStatsFollowEvolutions() {
        return baseStatsFollowEvolutions;
    }

    public Settings setBaseStatsFollowEvolutions(boolean baseStatsFollowEvolutions) {
        this.baseStatsFollowEvolutions = baseStatsFollowEvolutions;
        return this;
    }

    public boolean isStandardizeEXPCurves() {
        return standardizeEXPCurves;
    }

    public Settings setStandardizeEXPCurves(boolean standardizeEXPCurves) {
        this.standardizeEXPCurves = standardizeEXPCurves;
        return this;
    }

    public boolean isUpdateBaseStats() {
        return updateBaseStats;
    }

    public Settings setUpdateBaseStats(boolean updateBaseStats) {
        this.updateBaseStats = updateBaseStats;
        return this;
    }

    public AbilitiesMod getAbilitiesMod() {
        return abilitiesMod;
    }

    public Settings setAbilitiesMod(AbilitiesMod abilitiesMod) {
        this.abilitiesMod = abilitiesMod;
        return this;
    }

    public Settings setAbilitiesMod(boolean... bools) {
        return setAbilitiesMod(getEnum(AbilitiesMod.class, bools));
    }

    public boolean isAllowWonderGuard() {
        return allowWonderGuard;
    }

    public Settings setAllowWonderGuard(boolean allowWonderGuard) {
        this.allowWonderGuard = allowWonderGuard;
        return this;
    }

    public boolean isAbilitiesFollowEvolutions() {
        return abilitiesFollowEvolutions;
    }

    public Settings setAbilitiesFollowEvolutions(boolean abilitiesFollowEvolutions) {
        this.abilitiesFollowEvolutions = abilitiesFollowEvolutions;
        return this;
    }

    public boolean isBanTrappingAbilities() {
        return banTrappingAbilities;
    }

    public Settings setBanTrappingAbilities(boolean banTrappingAbilities) {
        this.banTrappingAbilities = banTrappingAbilities;
        return this;
    }

    public boolean isBanNegativeAbilities() {
        return banNegativeAbilities;
    }

    public Settings setBanNegativeAbilities(boolean banNegativeAbilities) {
        this.banNegativeAbilities = banNegativeAbilities;
        return this;
    }

    public StartersMod getStartersMod() {
        return startersMod;
    }

    public Settings setStartersMod(StartersMod startersMod) {
        this.startersMod = startersMod;
        return this;
    }

    public Settings setStartersMod(boolean... bools) {
        return setStartersMod(getEnum(StartersMod.class, bools));
    }

    public int[] getCustomStarters() {
        return customStarters;
    }

    public Settings setCustomStarters(int[] customStarters) {
        this.customStarters = customStarters;
        return this;
    }

    public boolean isRandomizeStartersHeldItems() {
        return randomizeStartersHeldItems;
    }

    public Settings setRandomizeStartersHeldItems(boolean randomizeStartersHeldItems) {
        this.randomizeStartersHeldItems = randomizeStartersHeldItems;
        return this;
    }

    public boolean isBanBadRandomStarterHeldItems() {
        return banBadRandomStarterHeldItems;
    }

    public Settings setBanBadRandomStarterHeldItems(boolean banBadRandomStarterHeldItems) {
        this.banBadRandomStarterHeldItems = banBadRandomStarterHeldItems;
        return this;
    }

    public TypesMod getTypesMod() {
        return typesMod;
    }

    public Settings setTypesMod(TypesMod typesMod) {
        this.typesMod = typesMod;
        return this;
    }

    public Settings setTypesMod(boolean... bools) {
        return setTypesMod(getEnum(TypesMod.class, bools));
    }

    public EvolutionsMod getEvolutionsMod() {
        return evolutionsMod;
    }

    public Settings setEvolutionsMod(EvolutionsMod evolutionsMod) {
        this.evolutionsMod = evolutionsMod;
        return this;
    }

    public Settings setEvolutionsMod(boolean... bools) {
        return setEvolutionsMod(getEnum(EvolutionsMod.class, bools));
    }

    public boolean isEvosSimilarStrength() {
        return evosSimilarStrength;
    }

    public Settings setEvosSimilarStrength(boolean evosSimilarStrength) {
        this.evosSimilarStrength = evosSimilarStrength;
        return this;
    }

    public boolean isEvosSameTyping() {
        return evosSameTyping;
    }

    public Settings setEvosSameTyping(boolean evosSameTyping) {
        this.evosSameTyping = evosSameTyping;
        return this;
    }

    public boolean isEvosMaxThreeStages() {
        return evosMaxThreeStages;
    }

    public Settings setEvosMaxThreeStages(boolean evosMaxThreeStages) {
        this.evosMaxThreeStages = evosMaxThreeStages;
        return this;
    }

    public boolean isEvosForceChange() {
        return evosForceChange;
    }

    public Settings setEvosForceChange(boolean evosForceChange) {
        this.evosForceChange = evosForceChange;
        return this;
    }

    public boolean isRandomizeMovePowers() {
        return randomizeMovePowers;
    }

    public Settings setRandomizeMovePowers(boolean randomizeMovePowers) {
        this.randomizeMovePowers = randomizeMovePowers;
        return this;
    }

    public boolean isRandomizeMoveAccuracies() {
        return randomizeMoveAccuracies;
    }

    public Settings setRandomizeMoveAccuracies(boolean randomizeMoveAccuracies) {
        this.randomizeMoveAccuracies = randomizeMoveAccuracies;
        return this;
    }

    public boolean isRandomizeMovePPs() {
        return randomizeMovePPs;
    }

    public Settings setRandomizeMovePPs(boolean randomizeMovePPs) {
        this.randomizeMovePPs = randomizeMovePPs;
        return this;
    }

    public boolean isRandomizeMoveTypes() {
        return randomizeMoveTypes;
    }

    public Settings setRandomizeMoveTypes(boolean randomizeMoveTypes) {
        this.randomizeMoveTypes = randomizeMoveTypes;
        return this;
    }

    public boolean isRandomizeMoveCategory() {
        return randomizeMoveCategory;
    }

    public Settings setRandomizeMoveCategory(boolean randomizeMoveCategory) {
        this.randomizeMoveCategory = randomizeMoveCategory;
        return this;
    }

    public MovesetsMod getMovesetsMod() {
        return movesetsMod;
    }

    public Settings setMovesetsMod(MovesetsMod movesetsMod) {
        this.movesetsMod = movesetsMod;
        return this;
    }

    public Settings setMovesetsMod(boolean... bools) {
        return setMovesetsMod(getEnum(MovesetsMod.class, bools));
    }

    public boolean isStartWithFourMoves() {
        return startWithFourMoves;
    }

    public Settings setStartWithFourMoves(boolean startWithFourMoves) {
        this.startWithFourMoves = startWithFourMoves;
        return this;
    }

    public boolean isReorderDamagingMoves() {
        return reorderDamagingMoves;
    }

    public Settings setReorderDamagingMoves(boolean reorderDamagingMoves) {
        this.reorderDamagingMoves = reorderDamagingMoves;
        return this;
    }

    public boolean isMovesetsForceGoodDamaging() {
        return movesetsForceGoodDamaging;
    }

    public Settings setMovesetsForceGoodDamaging(boolean movesetsForceGoodDamaging) {
        this.movesetsForceGoodDamaging = movesetsForceGoodDamaging;
        return this;
    }

    public int getMovesetsGoodDamagingPercent() {
        return movesetsGoodDamagingPercent;
    }

    public Settings setMovesetsGoodDamagingPercent(int movesetsGoodDamagingPercent) {
        this.movesetsGoodDamagingPercent = movesetsGoodDamagingPercent;
        return this;
    }

    public TrainersMod getTrainersMod() {
        return trainersMod;
    }

    public Settings setTrainersMod(TrainersMod trainersMod) {
        this.trainersMod = trainersMod;
        return this;
    }

    public Settings setTrainersMod(boolean... bools) {
        return setTrainersMod(getEnum(TrainersMod.class, bools));
    }

    public boolean isRivalCarriesStarterThroughout() {
        return rivalCarriesStarterThroughout;
    }

    public Settings setRivalCarriesStarterThroughout(boolean rivalCarriesStarterThroughout) {
        this.rivalCarriesStarterThroughout = rivalCarriesStarterThroughout;
        return this;
    }

    public boolean isTrainersUsePokemonOfSimilarStrength() {
        return trainersUsePokemonOfSimilarStrength;
    }

    public Settings setTrainersUsePokemonOfSimilarStrength(boolean trainersUsePokemonOfSimilarStrength) {
        this.trainersUsePokemonOfSimilarStrength = trainersUsePokemonOfSimilarStrength;
        return this;
    }

    public boolean isTrainersMatchTypingDistribution() {
        return trainersMatchTypingDistribution;
    }

    public Settings setTrainersMatchTypingDistribution(boolean trainersMatchTypingDistribution) {
        this.trainersMatchTypingDistribution = trainersMatchTypingDistribution;
        return this;
    }

    public boolean isTrainersBlockLegendaries() {
        return trainersBlockLegendaries;
    }

    public Settings setTrainersBlockLegendaries(boolean trainersBlockLegendaries) {
        this.trainersBlockLegendaries = trainersBlockLegendaries;
        return this;
    }

    public boolean isTrainersBlockEarlyWonderGuard() {
        return trainersBlockEarlyWonderGuard;
    }

    public Settings setTrainersBlockEarlyWonderGuard(boolean trainersBlockEarlyWonderGuard) {
        this.trainersBlockEarlyWonderGuard = trainersBlockEarlyWonderGuard;
        return this;
    }

    public boolean isRandomizeTrainerNames() {
        return randomizeTrainerNames;
    }

    public Settings setRandomizeTrainerNames(boolean randomizeTrainerNames) {
        this.randomizeTrainerNames = randomizeTrainerNames;
        return this;
    }

    public boolean isRandomizeTrainerClassNames() {
        return randomizeTrainerClassNames;
    }

    public Settings setRandomizeTrainerClassNames(boolean randomizeTrainerClassNames) {
        this.randomizeTrainerClassNames = randomizeTrainerClassNames;
        return this;
    }

    public boolean isTrainersForceFullyEvolved() {
        return trainersForceFullyEvolved;
    }

    public Settings setTrainersForceFullyEvolved(boolean trainersForceFullyEvolved) {
        this.trainersForceFullyEvolved = trainersForceFullyEvolved;
        return this;
    }

    public int getTrainersForceFullyEvolvedLevel() {
        return trainersForceFullyEvolvedLevel;
    }

    public Settings setTrainersForceFullyEvolvedLevel(int trainersForceFullyEvolvedLevel) {
        this.trainersForceFullyEvolvedLevel = trainersForceFullyEvolvedLevel;
        return this;
    }

    public boolean isTrainersLevelModified() {
        return trainersLevelModified;
    }

    public Settings setTrainersLevelModified(boolean trainersLevelModified) {
        this.trainersLevelModified = trainersLevelModified;
        return this;
    }

    public int getTrainersLevelModifier() {
        return trainersLevelModifier;
    }

    public Settings setTrainersLevelModifier(int trainersLevelModifier) {
        this.trainersLevelModifier = trainersLevelModifier;
        return this;
    }

    public WildPokemonMod getWildPokemonMod() {
        return wildPokemonMod;
    }

    public Settings setWildPokemonMod(WildPokemonMod wildPokemonMod) {
        this.wildPokemonMod = wildPokemonMod;
        return this;
    }

    public Settings setWildPokemonMod(boolean... bools) {
        return setWildPokemonMod(getEnum(WildPokemonMod.class, bools));
    }

    public WildPokemonRestrictionMod getWildPokemonRestrictionMod() {
        return wildPokemonRestrictionMod;
    }

    public Settings setWildPokemonRestrictionMod(WildPokemonRestrictionMod wildPokemonRestrictionMod) {
        this.wildPokemonRestrictionMod = wildPokemonRestrictionMod;
        return this;
    }

    public Settings setWildPokemonRestrictionMod(boolean... bools) {
        return setWildPokemonRestrictionMod(getEnum(WildPokemonRestrictionMod.class, bools));
    }

    public boolean isUseTimeBasedEncounters() {
        return useTimeBasedEncounters;
    }

    public Settings setUseTimeBasedEncounters(boolean useTimeBasedEncounters) {
        this.useTimeBasedEncounters = useTimeBasedEncounters;
        return this;
    }

    public boolean isBlockWildLegendaries() {
        return blockWildLegendaries;
    }

    public Settings setBlockWildLegendaries(boolean blockWildLegendaries) {
        this.blockWildLegendaries = blockWildLegendaries;
        return this;
    }

    public boolean isUseMinimumCatchRate() {
        return useMinimumCatchRate;
    }

    public Settings setUseMinimumCatchRate(boolean useMinimumCatchRate) {
        this.useMinimumCatchRate = useMinimumCatchRate;
        return this;
    }

    public int getMinimumCatchRateLevel() {
        return minimumCatchRateLevel;
    }

    public Settings setMinimumCatchRateLevel(int minimumCatchRateLevel) {
        this.minimumCatchRateLevel = minimumCatchRateLevel;
        return this;
    }

    public boolean isRandomizeWildPokemonHeldItems() {
        return randomizeWildPokemonHeldItems;
    }

    public Settings setRandomizeWildPokemonHeldItems(boolean randomizeWildPokemonHeldItems) {
        this.randomizeWildPokemonHeldItems = randomizeWildPokemonHeldItems;
        return this;
    }

    public boolean isBanBadRandomWildPokemonHeldItems() {
        return banBadRandomWildPokemonHeldItems;
    }

    public Settings setBanBadRandomWildPokemonHeldItems(boolean banBadRandomWildPokemonHeldItems) {
        this.banBadRandomWildPokemonHeldItems = banBadRandomWildPokemonHeldItems;
        return this;
    }

    public StaticPokemonMod getStaticPokemonMod() {
        return staticPokemonMod;
    }

    public Settings setStaticPokemonMod(StaticPokemonMod staticPokemonMod) {
        this.staticPokemonMod = staticPokemonMod;
        return this;
    }

    public Settings setStaticPokemonMod(boolean... bools) {
        return setStaticPokemonMod(getEnum(StaticPokemonMod.class, bools));
    }

    public TMsMod getTmsMod() {
        return tmsMod;
    }

    public Settings setTmsMod(TMsMod tmsMod) {
        this.tmsMod = tmsMod;
        return this;
    }

    public Settings setTmsMod(boolean... bools) {
        return setTmsMod(getEnum(TMsMod.class, bools));
    }

    public boolean isTmLevelUpMoveSanity() {
        return tmLevelUpMoveSanity;
    }

    public Settings setTmLevelUpMoveSanity(boolean tmLevelUpMoveSanity) {
        this.tmLevelUpMoveSanity = tmLevelUpMoveSanity;
        return this;
    }

    public boolean isKeepFieldMoveTMs() {
        return keepFieldMoveTMs;
    }

    public Settings setKeepFieldMoveTMs(boolean keepFieldMoveTMs) {
        this.keepFieldMoveTMs = keepFieldMoveTMs;
        return this;
    }

    public boolean isFullHMCompat() {
        return fullHMCompat;
    }

    public Settings setFullHMCompat(boolean fullHMCompat) {
        this.fullHMCompat = fullHMCompat;
        return this;
    }

    public boolean isTmsForceGoodDamaging() {
        return tmsForceGoodDamaging;
    }

    public Settings setTmsForceGoodDamaging(boolean tmsForceGoodDamaging) {
        this.tmsForceGoodDamaging = tmsForceGoodDamaging;
        return this;
    }

    public int getTmsGoodDamagingPercent() {
        return tmsGoodDamagingPercent;
    }

    public Settings setTmsGoodDamagingPercent(int tmsGoodDamagingPercent) {
        this.tmsGoodDamagingPercent = tmsGoodDamagingPercent;
        return this;
    }

    public TMsHMsCompatibilityMod getTmsHmsCompatibilityMod() {
        return tmsHmsCompatibilityMod;
    }

    public Settings setTmsHmsCompatibilityMod(TMsHMsCompatibilityMod tmsHmsCompatibilityMod) {
        this.tmsHmsCompatibilityMod = tmsHmsCompatibilityMod;
        return this;
    }

    public Settings setTmsHmsCompatibilityMod(boolean... bools) {
        return setTmsHmsCompatibilityMod(getEnum(TMsHMsCompatibilityMod.class, bools));
    }

    public MoveTutorMovesMod getMoveTutorMovesMod() {
        return moveTutorMovesMod;
    }

    public Settings setMoveTutorMovesMod(MoveTutorMovesMod moveTutorMovesMod) {
        this.moveTutorMovesMod = moveTutorMovesMod;
        return this;
    }

    public Settings setMoveTutorMovesMod(boolean... bools) {
        return setMoveTutorMovesMod(getEnum(MoveTutorMovesMod.class, bools));
    }

    public boolean isTutorLevelUpMoveSanity() {
        return tutorLevelUpMoveSanity;
    }

    public Settings setTutorLevelUpMoveSanity(boolean tutorLevelUpMoveSanity) {
        this.tutorLevelUpMoveSanity = tutorLevelUpMoveSanity;
        return this;
    }

    public boolean isKeepFieldMoveTutors() {
        return keepFieldMoveTutors;
    }

    public Settings setKeepFieldMoveTutors(boolean keepFieldMoveTutors) {
        this.keepFieldMoveTutors = keepFieldMoveTutors;
        return this;
    }

    public boolean isTutorsForceGoodDamaging() {
        return tutorsForceGoodDamaging;
    }

    public Settings setTutorsForceGoodDamaging(boolean tutorsForceGoodDamaging) {
        this.tutorsForceGoodDamaging = tutorsForceGoodDamaging;
        return this;
    }

    public int getTutorsGoodDamagingPercent() {
        return tutorsGoodDamagingPercent;
    }

    public Settings setTutorsGoodDamagingPercent(int tutorsGoodDamagingPercent) {
        this.tutorsGoodDamagingPercent = tutorsGoodDamagingPercent;
        return this;
    }

    public MoveTutorsCompatibilityMod getMoveTutorsCompatibilityMod() {
        return moveTutorsCompatibilityMod;
    }

    public Settings setMoveTutorsCompatibilityMod(MoveTutorsCompatibilityMod moveTutorsCompatibilityMod) {
        this.moveTutorsCompatibilityMod = moveTutorsCompatibilityMod;
        return this;
    }

    public Settings setMoveTutorsCompatibilityMod(boolean... bools) {
        return setMoveTutorsCompatibilityMod(getEnum(MoveTutorsCompatibilityMod.class, bools));
    }

    public InGameTradesMod getInGameTradesMod() {
        return inGameTradesMod;
    }

    public Settings setInGameTradesMod(InGameTradesMod inGameTradesMod) {
        this.inGameTradesMod = inGameTradesMod;
        return this;
    }

    public Settings setInGameTradesMod(boolean... bools) {
        return setInGameTradesMod(getEnum(InGameTradesMod.class, bools));
    }

    public boolean isRandomizeInGameTradesNicknames() {
        return randomizeInGameTradesNicknames;
    }

    public Settings setRandomizeInGameTradesNicknames(boolean randomizeInGameTradesNicknames) {
        this.randomizeInGameTradesNicknames = randomizeInGameTradesNicknames;
        return this;
    }

    public boolean isRandomizeInGameTradesOTs() {
        return randomizeInGameTradesOTs;
    }

    public Settings setRandomizeInGameTradesOTs(boolean randomizeInGameTradesOTs) {
        this.randomizeInGameTradesOTs = randomizeInGameTradesOTs;
        return this;
    }

    public boolean isRandomizeInGameTradesIVs() {
        return randomizeInGameTradesIVs;
    }

    public Settings setRandomizeInGameTradesIVs(boolean randomizeInGameTradesIVs) {
        this.randomizeInGameTradesIVs = randomizeInGameTradesIVs;
        return this;
    }

    public boolean isRandomizeInGameTradesItems() {
        return randomizeInGameTradesItems;
    }

    public Settings setRandomizeInGameTradesItems(boolean randomizeInGameTradesItems) {
        this.randomizeInGameTradesItems = randomizeInGameTradesItems;
        return this;
    }

    public FieldItemsMod getFieldItemsMod() {
        return fieldItemsMod;
    }

    public Settings setFieldItemsMod(FieldItemsMod fieldItemsMod) {
        this.fieldItemsMod = fieldItemsMod;
        return this;
    }

    public Settings setFieldItemsMod(boolean... bools) {
        return setFieldItemsMod(getEnum(FieldItemsMod.class, bools));
    }

    public boolean isBanBadRandomFieldItems() {
        return banBadRandomFieldItems;
    }

    public Settings setBanBadRandomFieldItems(boolean banBadRandomFieldItems) {
        this.banBadRandomFieldItems = banBadRandomFieldItems;
        return this;
    }

    private static int makeByteSelected(boolean... bools) {
        if (bools.length > 8) {
            throw new IllegalArgumentException("Can't set more than 8 bits in a byte!");
        }

        int initial = 0;
        int state = 1;
        for (boolean b : bools) {
            initial |= b ? state : 0;
            state *= 2;
        }
        return initial;
    }

    private static boolean restoreState(byte b, int index) {
        if (index >= 8) {
            throw new IllegalArgumentException("Can't read more than 8 bits from a byte!");
        }

        int value = b & 0xFF;
        return ((value >> index) & 0x01) == 0x01;
    }

    private static void writeFullInt(ByteArrayOutputStream out, int value) throws IOException {
        byte[] crc = ByteBuffer.allocate(4).putInt(value).array();
        out.write(crc);
    }

    private static void write2ByteInt(ByteArrayOutputStream out, int value) {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
    }

    public static <E extends Enum<E>> E restoreEnum(Class<E> clazz, byte b, int... indices) {
        boolean[] bools = new boolean[indices.length];
        int i = 0;
        for (int idx : indices) {
            bools[i] = restoreState(b, idx);
            i++;
        }
        return getEnum(clazz, bools);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> E getEnum(Class<E> clazz, boolean... bools) {
        int index = getSetEnum(clazz.getSimpleName(), bools);
        try {
            return ((E[]) clazz.getMethod("values").invoke(null))[index];
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Unable to parse enum of type %s", clazz.getSimpleName()),
                    e);
        }
    }

    private static int getSetEnum(String type, boolean... bools) {
        int index = -1;
        for (int i = 0; i < bools.length; i++) {
            if (bools[i]) {
                if (index >= 0) {
                    throw new IllegalStateException(String.format("Only one value for %s may be chosen!", type));
                }
                index = i;
            }
        }
        // We have to return something, so return the default
        return index >= 0 ? index : 0;
    }

    private static void checkChecksum(byte[] data) {
        // Check the checksum
        ByteBuffer buf = ByteBuffer.allocate(4).put(data, data.length - 8, 4);
        buf.rewind();
        int crc = buf.getInt();

        CRC32 checksum = new CRC32();
        checksum.update(data, 0, data.length - 8);

        if ((int) checksum.getValue() != crc) {
            throw new IllegalArgumentException("Malformed input string");
        }
    }

}
