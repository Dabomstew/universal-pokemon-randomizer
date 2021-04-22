package com.dabomstew.pkrandom.settings;

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
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import java.util.zip.CRC32;

import com.dabomstew.pkrandom.CustomNamesSet;
import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.SettingsUpdater;
import com.dabomstew.pkrandom.SysConstants;
import com.dabomstew.pkrandom.pokemon.GenRestrictions;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.romhandlers.Gen1RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen2RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen3RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen5RomHandler;
import com.dabomstew.pkrandom.romhandlers.RomHandler;

public class Settings {

    public static final int VERSION = 181;

    public static final int LENGTH_OF_SETTINGS_DATA = 42;

    public static final ResourceBundle bundle = ResourceBundle.getBundle("com/dabomstew/pkrandom/gui/Bundle");

    private CustomNamesSet customNames;

    private String romName;
    private Integer generationOfRom = 1;
    private boolean updatedFromOldVersion = false;

    private SettingsMap settingsMap;

    public enum BaseStatisticsMod {
        UNCHANGED, SHUFFLE_ORDER, SHUFFLE_BST, SHUFFLE_ALL, RANDOM_WITHIN_BST, RANDOM_UNRESTRICTED, RANDOM_COMPLETELY
    }

    public enum AbilitiesMod {
        UNCHANGED, RANDOMIZE
    }

    public enum StartersMod {
        UNCHANGED, CUSTOM, RANDOM
    }

    public enum TypesMod {
        UNCHANGED, RANDOM_RETAIN, COMPLETELY_RANDOM, SHUFFLE
    }

    public enum EvolutionsMod {
        UNCHANGED, RANDOM
    }

    public enum MovesetsMod {
        UNCHANGED, RANDOM_PREFER_SAME_TYPE, COMPLETELY_RANDOM, METRONOME_ONLY
    }

    public enum TrainersMod {
        UNCHANGED, RANDOM, TYPE_THEMED, GLOBAL_MAPPING
    }

    public enum WildPokemonMod {
        UNCHANGED, RANDOM, AREA_MAPPING, GLOBAL_MAPPING
    }

    public enum WildPokemonRestrictionMod {
        NONE, SIMILAR_STRENGTH, CATCH_EM_ALL, TYPE_THEME_AREAS, MATCH_TYPING_DISTRIBUTION
    }

    public enum StaticPokemonMod {
        UNCHANGED, RANDOM_MATCHING, COMPLETELY_RANDOM
    }

    public enum TMsMod {
        UNCHANGED, RANDOM
    }

    public enum TMsHMsCompatibilityMod {
        UNCHANGED, RANDOM_PREFER_TYPE, COMPLETELY_RANDOM, FULL
    }

    public enum MoveTutorMovesMod {
        UNCHANGED, RANDOM
    }

    public enum MoveTutorsCompatibilityMod {
        UNCHANGED, RANDOM_PREFER_TYPE, COMPLETELY_RANDOM, FULL
    }

    public enum InGameTradesMod {
        UNCHANGED, RANDOMIZE_GIVEN, RANDOMIZE_GIVEN_AND_REQUESTED
    }

    public enum FieldItemsMod {
        UNCHANGED, SHUFFLE, RANDOM
    }

    @SuppressWarnings({"rawtypes"})
    public Settings() {
        this.settingsMap = new SettingsMap();
        SettingsOptionFactory.setSettingsMap(settingsMap);

        // General Options
        SettingsOptionComposite limitPokemon = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.LIMIT_POKEMON, false));
        SettingsOptionComposite currentRestrictions = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.CURRENT_RESTRICTIONS,
            new GenRestrictions()).addMatches(new PredicatePair(limitPokemon, PredicatePair.BOOLEAN_TRUE)));
        SettingsOptionComposite blockBrokenMoves = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.BLOCK_BROKEN_MOVES, false));
        SettingsOptionComposite raceMode = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.RACE_MODE, false));

        // Base statistics
        SettingsOptionComposite baseStatisticsMod = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.BASE_STATISTICS_MOD, BaseStatisticsMod.UNCHANGED));
        SettingsOptionComposite standardizeEXPCurves = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.STANDARDIZE_EXP_CURVES, false));
        SettingsOptionComposite updateBaseStats = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.UPDATE_BASE_STATS, false));
        SettingsOptionComposite baseStatsFollowEvolutions = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.BASE_STATS_FOLLOW_EVOLUTIONS,
            false).addMatches(new PredicatePair(baseStatisticsMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite statsRandomizeFirst = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.STATS_RANDOMIZE_FIRST,
            false).addMatches(new PredicatePair(baseStatsFollowEvolutions, PredicatePair.BOOLEAN_TRUE)));

        // Abilities
        SettingsOptionComposite abilitiesMod = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.ABILITIES_MOD, AbilitiesMod.UNCHANGED));
        SettingsOptionComposite allowWonderGuard = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.ALLOW_WONDER_GUARD, false)
            .addMatches(new PredicatePair(abilitiesMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite abilitiesFollowEvolutions = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.ABILITIES_FOLLOW_EVOLUTIONS,
            false).addMatches(new PredicatePair(abilitiesMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite banTrappingAbilities = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.BAN_TRAPPING_ABILITIES,
            false).addMatches(new PredicatePair(abilitiesMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite banNegativeAbilities = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.BAN_NEGATIVE_ABILITIES,
            false).addMatches(new PredicatePair(abilitiesMod, PredicatePair.ENUM_NOT_UNCHANGED)));

        // Types
        SettingsOptionComposite typesMod = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TYPES_MOD, TypesMod.UNCHANGED));
        SettingsOptionComposite typesFollowEvos = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TYPES_FOLLOW_EVOS, false)
            .addMatches(new PredicatePair(typesMod, PredicatePair.TYPES_MOD_RANDOM)));
        SettingsOptionComposite typesRandomizeFirst = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TYPES_RANDOMIZE_FIRST,
            false).addMatches(new PredicatePair(typesFollowEvos, PredicatePair.BOOLEAN_TRUE)));

        // Evos
        SettingsOptionComposite evolutionsMod = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.EVOLUTIONS_MOD, EvolutionsMod.UNCHANGED));
        SettingsOptionComposite changeImpossibleEvolutions = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.CHANGE_IMPOSSIBLE_EVOLUTIONS, false));
        SettingsOptionComposite makeEvolutionsEasier = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.MAKE_EVOLUTIONS_EASIER, false));
        SettingsOptionComposite evosSimilarStrength = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.EVOS_SIMILAR_STRENGTH, 
            false).addMatches(new PredicatePair(evolutionsMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite evosSameTyping = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.EVOS_SAME_TYPING,
            false).addMatches(new PredicatePair(evolutionsMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite evosChangeMethod = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.EVOS_CHANGE_METHOD,
            false).addMatches(new PredicatePair(evolutionsMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite evosMaxThreeStages = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.EVOS_MAX_THREE_STAGES,
            false).addMatches(new PredicatePair(evolutionsMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite evosForceChange = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.EVOS_FORCE_CHANGE,
            false).addMatches(new PredicatePair(evolutionsMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite evosNoConverge = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.EVOS_NO_CONVERGE,
            false).addMatches(new PredicatePair(evolutionsMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite evosForceGrowth = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.EVOS_FORCE_GROWTH,
            false).addMatches(new PredicatePair(evolutionsMod, PredicatePair.ENUM_NOT_UNCHANGED)));

        // Starters
        SettingsOptionComposite startersMod = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.STARTERS_MOD, StartersMod.UNCHANGED));
        SettingsOptionComposite customStarters = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.CUSTOM_STARTERS, new int[]{-1, -1, -1})
            .addMatches(new PredicatePair(startersMod, PredicatePair.STARTERS_MOD_CUSTOM))
            .addValidInts(0, 0));
        SettingsOptionComposite randomizeStartersHeldItems = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.RANDOMIZE_STARTERS_HELD_ITEMS, false));
        SettingsOptionComposite banBadRandomStarterHeldItems = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.BAN_BAD_RANDOM_STARTER_HELD_ITEMS, false)
            .addMatches(new PredicatePair(randomizeStartersHeldItems, PredicatePair.BOOLEAN_TRUE)));
        SettingsOptionComposite startersNoSplit = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.STARTERS_NO_SPLIT, false)
            .addMatches(new PredicatePair(startersMod, PredicatePair.STARTERS_MOD_RANDOM)));
        SettingsOptionComposite startersUniqueTypes = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.STARTERS_UNIQUE_TYPES, false)
            .addMatches(new PredicatePair(startersMod, PredicatePair.STARTERS_MOD_RANDOM)));
        SettingsOptionComposite starterLimitBST = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.STARTERS_LIMIT_BST, false)
            .addMatches(new PredicatePair(startersMod, PredicatePair.STARTERS_MOD_RANDOM)));
        SettingsOptionComposite starterBSTModifier = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.STARTERS_BST_MODIFIER, SettingsConstants.BST_MINIMUM_INT)
            .addMatches(new PredicatePair(starterLimitBST, PredicatePair.BOOLEAN_TRUE))
            .addValidInts(SettingsConstants.BST_MINIMUM_INT, SettingsConstants.BST_MAXIMUM_INT));
        SettingsOptionComposite startersBaseEvoOnly = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.STARTERS_BASE_EVO_ONLY, false)
            .addMatches(new PredicatePair(startersMod, PredicatePair.STARTERS_MOD_RANDOM)));
        SettingsOptionComposite startersExactEvos = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.STARTERS_EXACT_EVOS, false)
            .addMatches(new PredicatePair(startersMod, PredicatePair.STARTERS_MOD_RANDOM)));
        SettingsOptionComposite startersMinimumEvos = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.STARTERS_MINIMUM_EVOS, 0)
            .addMatches(new PredicatePair(startersMod, PredicatePair.STARTERS_MOD_RANDOM))
            .addValidInts(0, 2));
        SettingsOptionComposite startersSETriangle = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.STARTERS_SE_TRIANGLE, false)
            .addMatches(new PredicatePair(startersMod, PredicatePair.STARTERS_MOD_RANDOM)));

        // Moves
        SettingsOptionComposite randomizeMovePowers = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.RANDOMIZE_MOVE_POWERS, false));
        SettingsOptionComposite randomizeMoveAccuracies = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.RANDOMIZE_MOVE_ACCURACIES, false));
        SettingsOptionComposite randomizeMovePPs = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.RANDOMIZE_MOVE_PPS, false));
        SettingsOptionComposite randomizeMoveTypes = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.RANDOMIZE_MOVE_TYPES, false));
        SettingsOptionComposite randomizeMoveCategory = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.RANDOMIZE_MOVE_CATEGORY, false));
        SettingsOptionComposite updateMoves = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.UPDATE_MOVES, false));
        SettingsOptionComposite updateMovesLegacy = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.UPDATE_MOVES_LEGACY, false));

        // Movesets
        SettingsOptionComposite movesetsMod = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.MOVESETS_MOD, MovesetsMod.UNCHANGED));
        SettingsOptionComposite startWithGuaranteedMoves = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.START_WITH_GUARANTEED_MOVES, false)
            .addMatches(new PredicatePair(movesetsMod, PredicatePair.MOVESETS_MOD_RANDOM)));
        SettingsOptionComposite guaranteedMoveCount = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.GUARANTEED_MOVE_COUNT, 2)
            .addMatches(new PredicatePair(startWithGuaranteedMoves, PredicatePair.BOOLEAN_TRUE))
            .addValidInts(2, 4));
        SettingsOptionComposite reorderDamagingMoves = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.REORDER_DAMAGING_MOVES, false)
            .addMatches(new PredicatePair(movesetsMod, PredicatePair.MOVESETS_MOD_RANDOM)));
        SettingsOptionComposite movesetsForceGoodDamaging = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.MOVESETS_FORCE_GOOD_DAMAGING, false)
            .addMatches(new PredicatePair(movesetsMod, PredicatePair.MOVESETS_MOD_RANDOM)));
        SettingsOptionComposite movesetsGoodDamagingPercent = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.MOVESETS_GOOD_DAMAGING_PERCENT, 0)
            .addMatches(new PredicatePair(movesetsForceGoodDamaging, PredicatePair.BOOLEAN_TRUE))
            .addValidInts(0, 100));

        // Trainer pokemon
        SettingsOptionComposite trainersMod = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TRAINERS_MOD, TrainersMod.UNCHANGED));
        SettingsOptionComposite rivalCarriesStarterThroughout = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.RIVAL_CARRIES_STARTER_THROUGHOUT, false)
            .addMatches(new PredicatePair(trainersMod, PredicatePair.ENUM_NOT_UNCHANGED),
            new PredicatePair(startersMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite rivalCarriesTeamThroughout = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.RIVAL_CARRIES_TEAM_THROUGHOUT, false)
            .addMatches(new PredicatePair(rivalCarriesStarterThroughout, PredicatePair.BOOLEAN_TRUE)));
        SettingsOptionComposite trainersUsePokemonOfSimilarStrength = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TRAINERS_USE_POKEMON_OF_SIMILAR_STRENGTH, false)
            .addMatches(new PredicatePair(trainersMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite trainersMatchTypingDistribution = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TRAINERS_MATCH_TYPING_DISTRIBUTION, false)
            .addMatches(new PredicatePair(trainersMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite trainersBlockLegendaries = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TRAINERS_BLOCK_LEGENDARIES, true)
            .addMatches(new PredicatePair(trainersMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite trainersBlockEarlyWonderGuard = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TRAINERS_BLOCK_EARLY_WONDER_GUARD, true)
            .addMatches(new PredicatePair(trainersMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite gymTypeTheme = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.GYM_TYPE_THEME, false)
            .addMatches(new PredicatePair(trainersMod, PredicatePair.TRAINERS_MOD_RANDOM)));
        SettingsOptionComposite trainersRandomHeldItem = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TRAINERS_RANDOM_HELD_ITEM, false)
            .addGenRestriction(3, 4, 5));
        SettingsOptionComposite randomizeTrainerNames = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.RANDOMIZE_TRAINER_NAMES, false));
        SettingsOptionComposite randomizeTrainerClassNames = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.RANDOMIZE_TRAINER_CLASS_NAMES, false));
        SettingsOptionComposite trainersForceFullyEvolved = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TRAINERS_FORCE_FULLY_EVOLVED, false));
        SettingsOptionComposite trainersForceFullyEvolvedLevel = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TRAINERS_FORCE_FULLY_EVOLVED_LEVEL, SettingsConstants.FULLY_EVOLVED_MINIMUM_INT)
            .addMatches(new PredicatePair(trainersForceFullyEvolved, PredicatePair.BOOLEAN_TRUE))
            .addValidInts(SettingsConstants.FULLY_EVOLVED_MINIMUM_INT, SettingsConstants.FULLY_EVOLVED_MAXIMUM_INT));
        SettingsOptionComposite trainersLevelModified = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TRAINERS_LEVEL_MODIFIED, false));
        SettingsOptionComposite trainersLevelModifier = SettingsOptionFactory.createSettingsOption(
            // 0 is used here since the minimum is -50 and we don't want that
            new SettingsOption.Builder(SettingsConstants.TRAINERS_LEVEL_MODIFIER, 0)
            .addMatches(new PredicatePair(trainersLevelModified, PredicatePair.BOOLEAN_TRUE))
            .addValidInts(SettingsConstants.LEVEL_MODIFIER_MINIMUM_INT, SettingsConstants.LEVEL_MODIFIER_MAXIMUM_INT));

        // Wild Pokemon
        SettingsOptionComposite wildPokemonMod = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.WILD_POKEMON_MOD, WildPokemonMod.UNCHANGED));
        SettingsOptionComposite wildPokemonRestrictionMod = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.WILD_POKEMON_RESTRICTION_MOD, WildPokemonRestrictionMod.NONE)
            .addMatches(new PredicatePair(wildPokemonMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite useTimeBasedEncounters = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.USE_TIME_BASED_ENCOUNTERS, false)
            .addMatches(new PredicatePair(wildPokemonMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite blockWildLegendaries = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.BLOCK_WILD_LEGENDARIES, true)
            .addMatches(new PredicatePair(wildPokemonMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite useMinimumCatchRate = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.USE_MINIMUM_CATCH_RATE, false));
        SettingsOptionComposite minimumCatchRateLevel = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.MINIMUM_CATCH_RATE_LEVEL, SettingsConstants.MINIMUM_CATCH_MINIMUM_INT)
            .addMatches(new PredicatePair(useMinimumCatchRate, PredicatePair.BOOLEAN_TRUE))
            .addValidInts(SettingsConstants.MINIMUM_CATCH_MINIMUM_INT, SettingsConstants.MINIMUM_CATCH_MAXIMUM_INT));
        SettingsOptionComposite randomizeWildPokemonHeldItems = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.RANDOMIZE_WILD_POKEMON_HELD_ITEMS, false));
        SettingsOptionComposite banBadRandomWildPokemonHeldItems = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.BAN_BAD_RANDOM_WILD_POKEMON_HELD_ITEMS, false)
            .addMatches(new PredicatePair(randomizeWildPokemonHeldItems, PredicatePair.BOOLEAN_TRUE)));
        SettingsOptionComposite allowLowLevelEvolvedTypes = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.ALLOW_LOW_LEVEL_EVOLVED_TYPES, true)
            .addMatches(new PredicatePair(wildPokemonMod, PredicatePair.ENUM_NOT_UNCHANGED)));

        // Static Pokemon
        SettingsOptionComposite staticPokemonMod = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.STATIC_POKEMON_MOD, StaticPokemonMod.UNCHANGED));

        // TM/HM
        SettingsOptionComposite tmsMod = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TMS_MOD, TMsMod.UNCHANGED));
        SettingsOptionComposite tmsHmsCompatibilityMod = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TMS_HMS_COMPATIBILITY_MOD, TMsHMsCompatibilityMod.UNCHANGED));
        SettingsOptionComposite tmLevelUpMoveSanity = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TM_LEVEL_UP_MOVE_SANITY, false)
            .addMatches(new PredicatePair(tmsMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite keepFieldMoveTMs = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.KEEP_FIELD_MOVE_TMS, false)
            .addMatches(new PredicatePair(tmsMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite fullHMCompat = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.FULL_HM_COMPAT, false)
            .addMatches(new PredicatePair(tmsMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite tmsForceGoodDamaging = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TMS_FORCE_GOOD_DAMAGING, false)
            .addMatches(new PredicatePair(tmsMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite tmsGoodDamagingPercent = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TMS_GOOD_DAMAGING_PERCENT, 0)
            .addMatches(new PredicatePair(tmsForceGoodDamaging, PredicatePair.BOOLEAN_TRUE))
            .addValidInts(0, 100));

        // Move tutors
        SettingsOptionComposite moveTutorMovesMod = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.MOVE_TUTOR_MOVES_MOD, MoveTutorMovesMod.UNCHANGED));
        SettingsOptionComposite moveTutorsCompatibilityMod = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.MOVE_TUTORS_COMPATIBILITY_MOD, MoveTutorsCompatibilityMod.UNCHANGED));
        SettingsOptionComposite tutorLevelUpMoveSanity = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TUTOR_LEVEL_UP_MOVE_SANITY, false)
            .addMatches(new PredicatePair(moveTutorMovesMod, PredicatePair.ENUM_NOT_UNCHANGED),
            new PredicatePair(moveTutorsCompatibilityMod, PredicatePair.ENUM_NOT_UNCHANGED),
            new PredicatePair(movesetsMod, PredicatePair.MOVESETS_MOD_RANDOM)));
        SettingsOptionComposite keepFieldMoveTutors = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.KEEP_FIELD_MOVE_TUTORS, false)
            .addMatches(new PredicatePair(moveTutorMovesMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite tutorsForceGoodDamaging = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TUTORS_FORCE_GOOD_DAMAGING, false)
            .addMatches(new PredicatePair(moveTutorMovesMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        SettingsOptionComposite tutorsGoodDamagingPercent = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.TUTORS_GOOD_DAMAGING_PERCENT, 0)
            .addMatches(new PredicatePair(tutorsForceGoodDamaging, PredicatePair.BOOLEAN_TRUE))
            .addValidInts(0, 100));

        // Trades
        SettingsOptionComposite inGameTradesMod = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.IN_GAME_TRADES_MOD, InGameTradesMod.UNCHANGED));
        SettingsOptionComposite randomizeInGameTradesNicknames = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.RANDOMIZE_IN_GAME_TRADES_NICKNAMES, false));
        SettingsOptionComposite randomizeInGameTradesOTs = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.RANDOMIZE_IN_GAME_TRADES_OTS, false));
        SettingsOptionComposite randomizeInGameTradesIVs = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.RANDOMIZE_IN_GAME_TRADES_IVS, false));
        SettingsOptionComposite randomizeInGameTradesItems = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.RANDOMIZE_IN_GAME_TRADES_ITEMS, false));

        // Field items
        SettingsOptionComposite fieldItemsMod = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.FIELD_ITEMS_MOD, FieldItemsMod.UNCHANGED));
        SettingsOptionComposite banBadRandomFieldItems = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.BAN_BAD_RANDOM_FIELD_ITEMS, false)
            .addMatches(new PredicatePair(fieldItemsMod, PredicatePair.ENUM_NOT_UNCHANGED)));
        
        // Misc tweaks
        SettingsOptionComposite currentMiscTweaks = SettingsOptionFactory.createSettingsOption(
            new SettingsOption.Builder(SettingsConstants.CURRENT_MISC_TWEAKS, 0)
            .addValidInts(0, MiscTweak.getHighestBitValue()));
    }

    public void randomSettings() {
        // List of keys that should not have a random option applied
        List<String> bannedKeys = Arrays.asList("raceMode");
        Random random = new Random();

        settingsMap.forEachParent((option) -> {
            if (!bannedKeys.contains(option.getKey())) {
                option.getValue().randomValue(random, generationOfRom);
            }
        });  
    }

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
        out.write(makeByteSelected(
                settingsMap.getValue(SettingsConstants.CHANGE_IMPOSSIBLE_EVOLUTIONS),
                settingsMap.getValue(SettingsConstants.UPDATE_MOVES),
                settingsMap.getValue(SettingsConstants.UPDATE_MOVES_LEGACY),
                settingsMap.getValue(SettingsConstants.RANDOMIZE_TRAINER_NAMES),
                settingsMap.getValue(SettingsConstants.RANDOMIZE_TRAINER_CLASS_NAMES),
                settingsMap.getValue(SettingsConstants.MAKE_EVOLUTIONS_EASIER)));

        // 1: pokemon base stats (see byte 36 for additional options)
        out.write(makeByteSelected(
                settingsMap.getValue(SettingsConstants.BASE_STATS_FOLLOW_EVOLUTIONS),
                settingsMap.getValue(SettingsConstants.BASE_STATISTICS_MOD) == BaseStatisticsMod.RANDOM_WITHIN_BST,
                settingsMap.getValue(SettingsConstants.BASE_STATISTICS_MOD) == BaseStatisticsMod.SHUFFLE_ORDER,
                settingsMap.getValue(SettingsConstants.BASE_STATISTICS_MOD) == BaseStatisticsMod.UNCHANGED,
                settingsMap.getValue(SettingsConstants.STANDARDIZE_EXP_CURVES), 
                settingsMap.getValue(SettingsConstants.UPDATE_BASE_STATS),
                settingsMap.getValue(SettingsConstants.BASE_STATISTICS_MOD) == BaseStatisticsMod.RANDOM_UNRESTRICTED,
                settingsMap.getValue(SettingsConstants.BASE_STATISTICS_MOD) == BaseStatisticsMod.RANDOM_COMPLETELY));

        // 2: pokemon types & more general options (see byte 36 for additional options)
        out.write(makeByteSelected(
                settingsMap.getValue(SettingsConstants.TYPES_MOD) == TypesMod.RANDOM_RETAIN,
                settingsMap.getValue(SettingsConstants.TYPES_MOD) == TypesMod.COMPLETELY_RANDOM,
                settingsMap.getValue(SettingsConstants.TYPES_MOD) == TypesMod.UNCHANGED,
                settingsMap.getValue(SettingsConstants.RACE_MODE),
                settingsMap.getValue(SettingsConstants.BLOCK_BROKEN_MOVES),
                settingsMap.getValue(SettingsConstants.LIMIT_POKEMON),
                settingsMap.getValue(SettingsConstants.TYPES_RANDOMIZE_FIRST),
                settingsMap.getValue(SettingsConstants.TYPES_MOD) == TypesMod.SHUFFLE));

        // 3: v171: changed to the abilities byte
        out.write(makeByteSelected(
                settingsMap.getValue(SettingsConstants.ABILITIES_MOD) == AbilitiesMod.UNCHANGED,
                settingsMap.getValue(SettingsConstants.ABILITIES_MOD) == AbilitiesMod.RANDOMIZE,
                settingsMap.getValue(SettingsConstants.ALLOW_WONDER_GUARD),
                settingsMap.getValue(SettingsConstants.ABILITIES_FOLLOW_EVOLUTIONS),
                settingsMap.getValue(SettingsConstants.BAN_TRAPPING_ABILITIES),
                settingsMap.getValue(SettingsConstants.BAN_NEGATIVE_ABILITIES)));

        // 4: starter pokemon stuff (see byte 37 for additional options)
        out.write(makeByteSelected(
                settingsMap.getValue(SettingsConstants.STARTERS_MOD) == StartersMod.CUSTOM, 
                settingsMap.getValue(SettingsConstants.STARTERS_MOD) == StartersMod.RANDOM && (int)settingsMap.getValue(SettingsConstants.STARTERS_MINIMUM_EVOS) == 0,
                settingsMap.getValue(SettingsConstants.STARTERS_MOD) == StartersMod.UNCHANGED, 
                settingsMap.getValue(SettingsConstants.STARTERS_MOD) == StartersMod.RANDOM && (int)settingsMap.getValue(SettingsConstants.STARTERS_MINIMUM_EVOS) == 2,
                settingsMap.getValue(SettingsConstants.RANDOMIZE_STARTERS_HELD_ITEMS),
                settingsMap.getValue(SettingsConstants.BAN_BAD_RANDOM_STARTER_HELD_ITEMS),
                settingsMap.getValue(SettingsConstants.STARTERS_EXACT_EVOS)));

        // @5 dropdowns
        int[] customStarters = settingsMap.getValue(SettingsConstants.CUSTOM_STARTERS);
        write2ByteInt(out, customStarters[0] - 1);
        write2ByteInt(out, customStarters[1] - 1);
        write2ByteInt(out, customStarters[2] - 1);

        // 11 movesets
        out.write(makeByteSelected(
                settingsMap.getValue(SettingsConstants.MOVESETS_MOD) == MovesetsMod.COMPLETELY_RANDOM,
                settingsMap.getValue(SettingsConstants.MOVESETS_MOD) == MovesetsMod.RANDOM_PREFER_SAME_TYPE,
                settingsMap.getValue(SettingsConstants.MOVESETS_MOD) == MovesetsMod.UNCHANGED,
                settingsMap.getValue(SettingsConstants.MOVESETS_MOD) == MovesetsMod.METRONOME_ONLY,
                settingsMap.getValue(SettingsConstants.START_WITH_GUARANTEED_MOVES),
                settingsMap.getValue(SettingsConstants.REORDER_DAMAGING_MOVES))
                | (((int)settingsMap.getValue(SettingsConstants.GUARANTEED_MOVE_COUNT) - 2) << 6));

        // 12 movesets good damaging
        out.write(((boolean)settingsMap.getValue(SettingsConstants.MOVESETS_FORCE_GOOD_DAMAGING) ? 0x80 : 0) | (int)settingsMap.getValue(SettingsConstants.MOVESETS_GOOD_DAMAGING_PERCENT));

        // 13 trainer pokemon (see byte 40 for additional options)
        // changed 160
        out.write(makeByteSelected(
                settingsMap.getValue(SettingsConstants.TRAINERS_USE_POKEMON_OF_SIMILAR_STRENGTH),
                settingsMap.getValue(SettingsConstants.TRAINERS_MOD) == TrainersMod.RANDOM,
                settingsMap.getValue(SettingsConstants.RIVAL_CARRIES_STARTER_THROUGHOUT),
                settingsMap.getValue(SettingsConstants.TRAINERS_MOD) == TrainersMod.TYPE_THEMED,
                settingsMap.getValue(SettingsConstants.TRAINERS_MATCH_TYPING_DISTRIBUTION),
                settingsMap.getValue(SettingsConstants.TRAINERS_MOD) == TrainersMod.UNCHANGED,
                settingsMap.getValue(SettingsConstants.TRAINERS_BLOCK_LEGENDARIES),
                settingsMap.getValue(SettingsConstants.TRAINERS_BLOCK_EARLY_WONDER_GUARD)));

        // 14 trainer pokemon force evolutions
        out.write(((boolean)settingsMap.getValue(SettingsConstants.TRAINERS_FORCE_FULLY_EVOLVED) ? 0x80 : 0) | (int)settingsMap.getValue(SettingsConstants.TRAINERS_FORCE_FULLY_EVOLVED_LEVEL));

        // 15 wild pokemon
        out.write(makeByteSelected(
                settingsMap.getValue(SettingsConstants.WILD_POKEMON_RESTRICTION_MOD) == WildPokemonRestrictionMod.CATCH_EM_ALL,
                settingsMap.getValue(SettingsConstants.WILD_POKEMON_MOD) == WildPokemonMod.AREA_MAPPING,
                settingsMap.getValue(SettingsConstants.WILD_POKEMON_RESTRICTION_MOD) == WildPokemonRestrictionMod.NONE,
                settingsMap.getValue(SettingsConstants.WILD_POKEMON_RESTRICTION_MOD) == WildPokemonRestrictionMod.TYPE_THEME_AREAS,
                settingsMap.getValue(SettingsConstants.WILD_POKEMON_MOD) == WildPokemonMod.GLOBAL_MAPPING,
                settingsMap.getValue(SettingsConstants.WILD_POKEMON_MOD) == WildPokemonMod.RANDOM,
                settingsMap.getValue(SettingsConstants.WILD_POKEMON_MOD) == WildPokemonMod.UNCHANGED,
                settingsMap.getValue(SettingsConstants.USE_TIME_BASED_ENCOUNTERS)));

        // 16 wild pokemon 2 (see byte 40 for additional options)
        // bugfix 161
        out.write(makeByteSelected(
                settingsMap.getValue(SettingsConstants.USE_MINIMUM_CATCH_RATE),
                settingsMap.getValue(SettingsConstants.BLOCK_WILD_LEGENDARIES),
                settingsMap.getValue(SettingsConstants.WILD_POKEMON_RESTRICTION_MOD) == WildPokemonRestrictionMod.SIMILAR_STRENGTH,
                settingsMap.getValue(SettingsConstants.RANDOMIZE_WILD_POKEMON_HELD_ITEMS),
                settingsMap.getValue(SettingsConstants.BAN_BAD_RANDOM_WILD_POKEMON_HELD_ITEMS),
                settingsMap.getValue(SettingsConstants.WILD_POKEMON_RESTRICTION_MOD) == WildPokemonRestrictionMod.MATCH_TYPING_DISTRIBUTION)
                | (((int)settingsMap.getValue(SettingsConstants.MINIMUM_CATCH_RATE_LEVEL) - 1) << 6));

        // 17 static pokemon
        out.write(makeByteSelected(
                settingsMap.getValue(SettingsConstants.STATIC_POKEMON_MOD) == StaticPokemonMod.UNCHANGED,
                settingsMap.getValue(SettingsConstants.STATIC_POKEMON_MOD) == StaticPokemonMod.RANDOM_MATCHING,
                settingsMap.getValue(SettingsConstants.STATIC_POKEMON_MOD) == StaticPokemonMod.COMPLETELY_RANDOM));

        // 18 tm randomization
        // new stuff 162
        out.write(makeByteSelected(
                settingsMap.getValue(SettingsConstants.TMS_HMS_COMPATIBILITY_MOD) == TMsHMsCompatibilityMod.COMPLETELY_RANDOM,
                settingsMap.getValue(SettingsConstants.TMS_HMS_COMPATIBILITY_MOD) == TMsHMsCompatibilityMod.RANDOM_PREFER_TYPE,
                settingsMap.getValue(SettingsConstants.TMS_HMS_COMPATIBILITY_MOD) == TMsHMsCompatibilityMod.UNCHANGED,
                settingsMap.getValue(SettingsConstants.TMS_MOD) == TMsMod.RANDOM,
                settingsMap.getValue(SettingsConstants.TMS_MOD) == TMsMod.UNCHANGED,
                settingsMap.getValue(SettingsConstants.TM_LEVEL_UP_MOVE_SANITY),
                settingsMap.getValue(SettingsConstants.KEEP_FIELD_MOVE_TMS),
                settingsMap.getValue(SettingsConstants.TMS_HMS_COMPATIBILITY_MOD) == TMsHMsCompatibilityMod.FULL));

        // 19 tms part 2
        // new in 170
        out.write(makeByteSelected(
            (Boolean)settingsMap.getValue(SettingsConstants.FULL_HM_COMPAT)));

        // 20 tms good damaging
        out.write(((boolean)settingsMap.getValue(SettingsConstants.TMS_FORCE_GOOD_DAMAGING) ? 0x80 : 0) | 
            (int)settingsMap.getValue(SettingsConstants.TMS_GOOD_DAMAGING_PERCENT));

        // 21 move tutor randomization
        out.write(makeByteSelected(
                settingsMap.getValue(SettingsConstants.MOVE_TUTORS_COMPATIBILITY_MOD) == MoveTutorsCompatibilityMod.COMPLETELY_RANDOM,
                settingsMap.getValue(SettingsConstants.MOVE_TUTORS_COMPATIBILITY_MOD) == MoveTutorsCompatibilityMod.RANDOM_PREFER_TYPE,
                settingsMap.getValue(SettingsConstants.MOVE_TUTORS_COMPATIBILITY_MOD) == MoveTutorsCompatibilityMod.UNCHANGED,
                settingsMap.getValue(SettingsConstants.MOVE_TUTOR_MOVES_MOD) == MoveTutorMovesMod.RANDOM,
                settingsMap.getValue(SettingsConstants.MOVE_TUTOR_MOVES_MOD) == MoveTutorMovesMod.UNCHANGED,
                settingsMap.getValue(SettingsConstants.TUTOR_LEVEL_UP_MOVE_SANITY),
                settingsMap.getValue(SettingsConstants.KEEP_FIELD_MOVE_TUTORS),
                settingsMap.getValue(SettingsConstants.MOVE_TUTORS_COMPATIBILITY_MOD) == MoveTutorsCompatibilityMod.FULL));

        // 22 tutors good damaging
        out.write(((boolean)settingsMap.getValue(SettingsConstants.TUTORS_FORCE_GOOD_DAMAGING) ? 0x80 : 0) | 
            (int)settingsMap.getValue(SettingsConstants.TUTORS_GOOD_DAMAGING_PERCENT));

        // new 150
        // 23 in game trades
        out.write(makeByteSelected(
                settingsMap.getValue(SettingsConstants.IN_GAME_TRADES_MOD) == InGameTradesMod.RANDOMIZE_GIVEN_AND_REQUESTED,
                settingsMap.getValue(SettingsConstants.IN_GAME_TRADES_MOD) == InGameTradesMod.RANDOMIZE_GIVEN,
                settingsMap.getValue(SettingsConstants.RANDOMIZE_IN_GAME_TRADES_ITEMS),
                settingsMap.getValue(SettingsConstants.RANDOMIZE_IN_GAME_TRADES_IVS),
                settingsMap.getValue(SettingsConstants.RANDOMIZE_IN_GAME_TRADES_NICKNAMES),
                settingsMap.getValue(SettingsConstants.RANDOMIZE_IN_GAME_TRADES_OTS),
                settingsMap.getValue(SettingsConstants.IN_GAME_TRADES_MOD) == InGameTradesMod.UNCHANGED));

        // 24 field items
        out.write(makeByteSelected(
                settingsMap.getValue(SettingsConstants.FIELD_ITEMS_MOD) == FieldItemsMod.RANDOM,
                settingsMap.getValue(SettingsConstants.FIELD_ITEMS_MOD) == FieldItemsMod.SHUFFLE,
                settingsMap.getValue(SettingsConstants.FIELD_ITEMS_MOD) == FieldItemsMod.UNCHANGED,
                settingsMap.getValue(SettingsConstants.BAN_BAD_RANDOM_FIELD_ITEMS)));

        // new 170
        // 25 move randomizers
        out.write(makeByteSelected(
            settingsMap.getValue(SettingsConstants.RANDOMIZE_MOVE_POWERS),
            settingsMap.getValue(SettingsConstants.RANDOMIZE_MOVE_ACCURACIES),
            settingsMap.getValue(SettingsConstants.RANDOMIZE_MOVE_PPS),
            settingsMap.getValue(SettingsConstants.RANDOMIZE_MOVE_TYPES),
            settingsMap.getValue(SettingsConstants.RANDOMIZE_MOVE_CATEGORY)));

        // 26 evolutions (see byte 41 for additional options)
        out.write(makeByteSelected(
            settingsMap.getValue(SettingsConstants.EVOLUTIONS_MOD) == EvolutionsMod.UNCHANGED,
            settingsMap.getValue(SettingsConstants.EVOLUTIONS_MOD) == EvolutionsMod.RANDOM,
            settingsMap.getValue(SettingsConstants.EVOS_SIMILAR_STRENGTH),
            settingsMap.getValue(SettingsConstants.EVOS_SAME_TYPING),
            settingsMap.getValue(SettingsConstants.EVOS_MAX_THREE_STAGES),
            settingsMap.getValue(SettingsConstants.EVOS_FORCE_CHANGE),
            settingsMap.getValue(SettingsConstants.EVOS_NO_CONVERGE),
            settingsMap.getValue(SettingsConstants.EVOS_FORCE_GROWTH)));

        // @ 27 pokemon restrictions
        try {
            GenRestrictions currentRestrictions = settingsMap.getValue(SettingsConstants.CURRENT_RESTRICTIONS);
            if (currentRestrictions != null) {
                writeFullInt(out, currentRestrictions.toInt());
            } else {
                writeFullInt(out, 0);
            }
        } catch (IOException e) {
        }

        // @ 31 misc tweaks
        try {
            writeFullInt(out, settingsMap.getValue(SettingsConstants.CURRENT_MISC_TWEAKS));
        } catch (IOException e) {

        }

        // @ 35 trainer pokemon level modifier
        out.write(((boolean)settingsMap.getValue(SettingsConstants.TRAINERS_LEVEL_MODIFIED) ? 0x80 : 0) | ((int)settingsMap.getValue(SettingsConstants.TRAINERS_LEVEL_MODIFIER)+50));

        // @ 36 Base Statistics and Types Overflow
        out.write(makeByteSelected(
            settingsMap.getValue(SettingsConstants.STATS_RANDOMIZE_FIRST),
            settingsMap.getValue(SettingsConstants.BASE_STATISTICS_MOD) == BaseStatisticsMod.SHUFFLE_BST, 
            settingsMap.getValue(SettingsConstants.BASE_STATISTICS_MOD) == BaseStatisticsMod.SHUFFLE_ALL,
            settingsMap.getValue(SettingsConstants.TYPES_FOLLOW_EVOS)));

        // @ 37 Starter Pokemon Overflow
        out.write(makeByteSelected(
            settingsMap.getValue(SettingsConstants.STARTERS_MOD) == StartersMod.RANDOM && (int)settingsMap.getValue(SettingsConstants.STARTERS_MINIMUM_EVOS) == 1, 
            settingsMap.getValue(SettingsConstants.STARTERS_NO_SPLIT),
            settingsMap.getValue(SettingsConstants.STARTERS_UNIQUE_TYPES),
            settingsMap.getValue(SettingsConstants.STARTERS_LIMIT_BST),
            settingsMap.getValue(SettingsConstants.STARTERS_BASE_EVO_ONLY),
            settingsMap.getValue(SettingsConstants.STARTERS_SE_TRIANGLE)));

        // @ 38 Starter Pokemon BST Modifier
        write2ByteInt(out, (int)settingsMap.getValue(SettingsConstants.STARTERS_BST_MODIFIER) - 1);
        
        // @ 40 Trainer and Wild Pokemon Overflow
        out.write(makeByteSelected(
            settingsMap.getValue(SettingsConstants.RIVAL_CARRIES_TEAM_THROUGHOUT),
            settingsMap.getValue(SettingsConstants.ALLOW_LOW_LEVEL_EVOLVED_TYPES),
            settingsMap.getValue(SettingsConstants.TRAINERS_RANDOM_HELD_ITEM),
            settingsMap.getValue(SettingsConstants.GYM_TYPE_THEME),
            settingsMap.getValue(SettingsConstants.TRAINERS_MOD) == TrainersMod.GLOBAL_MAPPING));

        // @ 41 Evolution overflow
        out.write(makeByteSelected(
            (Boolean)settingsMap.getValue(SettingsConstants.EVOS_CHANGE_METHOD)));

        // @ 42 Rom Title Name (update LENGTH_OF_SETTINGS_DATA if this changes)
        try {
            byte[] romName = this.romName.getBytes("US-ASCII");
            out.write(romName.length);
            out.write(romName);
        } catch (UnsupportedEncodingException e) {
            out.write(0);
        } catch (IOException e) {
            out.write(0);
        }

        // Create checksum to ensure correct deserialization between versions.        
        byte[] current = out.toByteArray();
        CRC32 checksum = new CRC32();
        checksum.update(current);

        // Add checksum to byte array
        try {
            writeFullInt(out, (int) checksum.getValue());
            writeFullInt(out, FileFunctions.getFileChecksum(SysConstants.customNamesFile));
        } catch (IOException e) {
        }
        
        // Convert to Base64 stream for file stream
        return java.util.Base64.getEncoder().encodeToString(out.toByteArray());
    }

    public static Settings fromString(String settingsString) throws UnsupportedEncodingException {
        byte[] data = java.util.Base64.getDecoder().decode(settingsString);
        checkChecksum(data);

        Settings settings = new Settings();

        // Restore the actual controls
        settings.setChangeImpossibleEvolutions(restoreState(data[0], 0));
        settings.setUpdateMoves(restoreState(data[0], 1));
        settings.setUpdateMovesLegacy(restoreState(data[0], 2));
        settings.setRandomizeTrainerNames(restoreState(data[0], 3));
        settings.setRandomizeTrainerClassNames(restoreState(data[0], 4));
        settings.setMakeEvolutionsEasier(restoreState(data[0], 5));

        settings.setBaseStatisticsMod(getEnum(BaseStatisticsMod.class, 
                restoreState(data[1], 3), // UNCHANGED
                restoreState(data[1], 2), // SHUFFLE_ORDER
                restoreState(data[36], 1),// SHUFFLE_BST
                restoreState(data[36], 2),// SHUFFLE_ALL
                restoreState(data[1], 1), // RANDOM_WITHIN_BST
                restoreState(data[1], 6), // RANDOM_UNRESTRICTED
                restoreState(data[1], 7)  // RANDOM_COMPLETELY
        ));
        settings.setStandardizeEXPCurves(restoreState(data[1], 4));
        settings.setBaseStatsFollowEvolutions(restoreState(data[1], 0));
        settings.setUpdateBaseStats(restoreState(data[1], 5));

        settings.setTypesMod(restoreEnum(TypesMod.class, data[2], 2, // UNCHANGED
                0, // RANDOM_RETAIN
                1, // COMPLETELY_RANDOM
                7  // SHUFFLE
        ));
        settings.setRaceMode(restoreState(data[2], 3));
        settings.setBlockBrokenMoves(restoreState(data[2], 4));
        settings.setLimitPokemon(restoreState(data[2], 5));
        settings.setTypesRandomizeFirst(restoreState(data[2], 6));

        settings.setAbilitiesMod(restoreEnum(AbilitiesMod.class, data[3], 0, // UNCHANGED
                1 // RANDOMIZE
        ));
        settings.setAllowWonderGuard(restoreState(data[3], 2));
        settings.setAbilitiesFollowEvolutions(restoreState(data[3], 3));
        settings.setBanTrappingAbilities(restoreState(data[3], 4));
        settings.setBanNegativeAbilities(restoreState(data[3], 5));

        settings.setStartersMod(getEnum(StartersMod.class, 
                restoreState(data[4], 2), // UNCHANGED
                restoreState(data[4], 0), // CUSTOM
                restoreState(data[4], 1) // RANDOM
        ));
        if (restoreState(data[4], 3)) {
            settings.setStartersMinimumEvos(2);
            settings.setStartersMod(StartersMod.RANDOM);   
        } else {
            settings.setStartersMinimumEvos(0);
        }
        settings.setRandomizeStartersHeldItems(restoreState(data[4], 4));
        settings.setBanBadRandomStarterHeldItems(restoreState(data[4], 5));
        settings.setStartersExactEvos(restoreState(data[4], 6));

        settings.setCustomStarters(new int[] { FileFunctions.read2ByteInt(data, 5) + 1,
                FileFunctions.read2ByteInt(data, 7) + 1, FileFunctions.read2ByteInt(data, 9) + 1 });

        settings.setMovesetsMod(restoreEnum(MovesetsMod.class, data[11], 2, // UNCHANGED
                1, // RANDOM_PREFER_SAME_TYPE
                0, // COMPLETELY_RANDOM
                3 // METRONOME_ONLY
        ));
        settings.setStartWithGuaranteedMoves(restoreState(data[11], 4));
        settings.setReorderDamagingMoves(restoreState(data[11], 5));
        settings.setGuaranteedMoveCount(((data[11] & 0xC0) >> 6) + 2);

        settings.setMovesetsForceGoodDamaging(restoreState(data[12], 7));
        settings.setMovesetsGoodDamagingPercent(data[12] & 0x7F);

        // changed 160
        settings.setTrainersMod(getEnum(TrainersMod.class, restoreState(data[13], 5), // UNCHANGED
                restoreState(data[13], 1), // RANDOM
                restoreState(data[13], 3), // TYPE_THEMED
                restoreState(data[40], 4)  // GLOBAL_MAPPING
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
                restoreState(data[15], 3), // TYPE_THEME_AREAS
                restoreState(data[16], 5) // MATCH_TYPING_DISTRIBUTION
        ));
        settings.setUseTimeBasedEncounters(restoreState(data[15], 7));

        settings.setUseMinimumCatchRate(restoreState(data[16], 0));
        settings.setBlockWildLegendaries(restoreState(data[16], 1));
        settings.setRandomizeWildPokemonHeldItems(restoreState(data[16], 3));
        settings.setBanBadRandomWildPokemonHeldItems(restoreState(data[16], 4));
        settings.setMinimumCatchRateLevel(((data[16] & 0xC0) >> 6) + 1);

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
        settings.setEvosNoConverge(restoreState(data[26], 6));
        settings.setEvosForceGrowth(restoreState(data[26], 7));

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

        settings.setStatsRandomizeFirst(restoreState(data[36], 0));
        settings.setTypesFollowEvos(restoreState(data[36], 3));

        if (restoreState(data[37], 0)) {
            settings.setStartersMinimumEvos(1);
            settings.setStartersMod(StartersMod.RANDOM);
        }
        settings.setStartersNoSplit(restoreState(data[37], 1));
        settings.setStartersUniqueTypes(restoreState(data[37], 2));
        settings.setStartersLimitBST(restoreState(data[37], 3));
        settings.setStartersBaseEvoOnly(restoreState(data[37], 4));
        settings.setStartersSETriangle(restoreState(data[37], 5));

        settings.setStartersBSTLimitModifier(FileFunctions.read2ByteInt(data, 38) + 1);

        settings.setRivalCarriesTeamThroughout(restoreState(data[40], 0));
        settings.setAllowLowLevelEvolvedTypes(restoreState(data[40], 1));
        settings.setTrainersRandomHeldItem(restoreState(data[40], 2));
        settings.setGymTypeTheme(restoreState(data[40], 3));

        settings.setEvosChangeMethod(restoreState(data[41], 0));
        
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

        // update the generation
        generationOfRom = rh.generationOfPokemon();

        // move update check
        if (this.isUpdateMovesLegacy() && rh instanceof Gen5RomHandler) {
            // don't actually update moves
            this.setUpdateMovesLegacy(false);
            this.setUpdateMoves(false);
        }

        // starters
        List<Pokemon> romPokemon = rh.getPokemon();
        List<Pokemon> romStarters = rh.getStarters();
        // set the custom starters option with the correct integers for random selection
        SettingsOption customStartersOption = ((SettingsOptionComposite)settingsMap.getOption(SettingsConstants.CUSTOM_STARTERS)).getCompositeValue();
        ((IntArraySettingsOption)customStartersOption).setAllowedValues(IntStream.range(1, romPokemon.size()));
        // update the custom starters option current value to use the starters of this rom
        for (int starter = 0; starter < 3; starter++) {
            int[] customStarters = settingsMap.getValue(SettingsConstants.CUSTOM_STARTERS);
            if (customStarters[starter] < 0 || customStarters[starter] >= romPokemon.size()) {
                // invalid starter for this game
                feedback.setChangedStarter(true);
                if (starter >= romStarters.size()) {
                    customStarters[starter] = 1;
                } else {
                    customStarters[starter] = romPokemon.indexOf(romStarters.get(starter));
                }
            }
        }

        // gen restrictions
        GenRestrictions genRes = settingsMap.getValue(SettingsConstants.CURRENT_RESTRICTIONS);
        if (rh instanceof Gen1RomHandler || rh.isROMHack()) {
            settingsMap.putValue(SettingsConstants.CURRENT_RESTRICTIONS, null);
            settingsMap.putValue(SettingsConstants.LIMIT_POKEMON, false);
        } else if (genRes != null) {
            genRes.limitToGen(rh.generationOfPokemon());
        }

        // trainers
        // held items are only randomized in gen3 and higher
        if (rh.generationOfPokemon() < 3) {
            this.setTrainersRandomHeldItem(false);
        }

        // misc tweaks
        int oldMiscTweaks = settingsMap.getValue(SettingsConstants.CURRENT_MISC_TWEAKS);
        settingsMap.putValue(SettingsConstants.CURRENT_MISC_TWEAKS, oldMiscTweaks & rh.miscTweaksAvailable());

        if (oldMiscTweaks != (int)settingsMap.getValue(SettingsConstants.CURRENT_MISC_TWEAKS)) {
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
            this.setStartWithGuaranteedMoves(false);
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

    public boolean isLimitPokemon() {
        return settingsMap.getValue(SettingsConstants.LIMIT_POKEMON);
    }

    public Settings setLimitPokemon(boolean limitPokemon) {
        settingsMap.putValue(SettingsConstants.LIMIT_POKEMON, limitPokemon);
        return this;
    }

    public GenRestrictions getCurrentRestrictions() {
        return settingsMap.getValue(SettingsConstants.CURRENT_RESTRICTIONS);
    }

    public Settings setCurrentRestrictions(GenRestrictions currentRestrictions) {
        settingsMap.putValue(SettingsConstants.CURRENT_RESTRICTIONS, currentRestrictions);
        return this;
    }

    public boolean isRaceMode() {
        return settingsMap.getValue(SettingsConstants.RACE_MODE);
    }

    public Settings setRaceMode(boolean raceMode) {
        settingsMap.putValue(SettingsConstants.RACE_MODE, raceMode);
        return this;
    }

    public boolean doBlockBrokenMoves() {
        return settingsMap.getValue(SettingsConstants.BLOCK_BROKEN_MOVES);
    }

    public Settings setBlockBrokenMoves(boolean blockBrokenMoves) {
        settingsMap.putValue(SettingsConstants.BLOCK_BROKEN_MOVES, blockBrokenMoves);
        return this;
    }

    public BaseStatisticsMod getBaseStatisticsMod() {
        return settingsMap.getValue(SettingsConstants.BASE_STATISTICS_MOD);
    }

    public Settings setBaseStatisticsMod(BaseStatisticsMod baseStatisticsMod) {
        settingsMap.putValue(SettingsConstants.BASE_STATISTICS_MOD, baseStatisticsMod);
        return this;
    }

    public Settings setBaseStatisticsMod(boolean... bools) {
        return setBaseStatisticsMod(getEnum(BaseStatisticsMod.class, bools));
    }

    public boolean isStandardizeEXPCurves() {
        return settingsMap.getValue(SettingsConstants.STANDARDIZE_EXP_CURVES);
    }

    public Settings setStandardizeEXPCurves(boolean standardizeEXPCurves) {
        settingsMap.putValue(SettingsConstants.STANDARDIZE_EXP_CURVES, standardizeEXPCurves);
        return this;
    }

    public boolean isUpdateBaseStats() {
        return settingsMap.getValue(SettingsConstants.UPDATE_BASE_STATS);
    }

    public Settings setUpdateBaseStats(boolean updateBaseStats) {
        settingsMap.putValue(SettingsConstants.UPDATE_BASE_STATS, updateBaseStats);
        return this;
    }

    public boolean isBaseStatsFollowEvolutions() {
        return settingsMap.getValue(SettingsConstants.BASE_STATS_FOLLOW_EVOLUTIONS);
    }

    public Settings setBaseStatsFollowEvolutions(boolean baseStatsFollowEvolutions) {
        settingsMap.putValue(SettingsConstants.BASE_STATS_FOLLOW_EVOLUTIONS, baseStatsFollowEvolutions);
        return this;
    }

    public boolean isStatsRandomizeFirst() {
        return settingsMap.getValue(SettingsConstants.STATS_RANDOMIZE_FIRST);
    }

    public Settings setStatsRandomizeFirst(boolean statsRandomizeFirst) {
        settingsMap.putValue(SettingsConstants.STATS_RANDOMIZE_FIRST, statsRandomizeFirst);
        return this;
    }

    public AbilitiesMod getAbilitiesMod() {
        return settingsMap.getValue(SettingsConstants.ABILITIES_MOD);
    }

    public Settings setAbilitiesMod(AbilitiesMod abilitiesMod) {
        settingsMap.putValue(SettingsConstants.ABILITIES_MOD, abilitiesMod);
        return this;
    }

    public Settings setAbilitiesMod(boolean... bools) {
        return setAbilitiesMod(getEnum(AbilitiesMod.class, bools));
    }

    public boolean isAllowWonderGuard() {
        return settingsMap.getValue(SettingsConstants.ALLOW_WONDER_GUARD);
    }

    public Settings setAllowWonderGuard(boolean allowWonderGuard) {
        settingsMap.putValue(SettingsConstants.ALLOW_WONDER_GUARD, allowWonderGuard);
        return this;
    }

    public boolean isAbilitiesFollowEvolutions() {
        return settingsMap.getValue(SettingsConstants.ABILITIES_FOLLOW_EVOLUTIONS);
    }

    public Settings setAbilitiesFollowEvolutions(boolean abilitiesFollowEvolutions) {
        settingsMap.putValue(SettingsConstants.ABILITIES_FOLLOW_EVOLUTIONS, abilitiesFollowEvolutions);
        return this;
    }

    public boolean isBanTrappingAbilities() {
        return settingsMap.getValue(SettingsConstants.BAN_TRAPPING_ABILITIES);
    }

    public Settings setBanTrappingAbilities(boolean banTrappingAbilities) {
        settingsMap.putValue(SettingsConstants.BAN_TRAPPING_ABILITIES, banTrappingAbilities);
        return this;
    }

    public boolean isBanNegativeAbilities() {
        return settingsMap.getValue(SettingsConstants.BAN_NEGATIVE_ABILITIES);
    }

    public Settings setBanNegativeAbilities(boolean banNegativeAbilities) {
        settingsMap.putValue(SettingsConstants.BAN_NEGATIVE_ABILITIES, banNegativeAbilities);
        return this;
    }

    public TypesMod getTypesMod() {
        return settingsMap.getValue(SettingsConstants.TYPES_MOD);
    }

    public Settings setTypesMod(TypesMod typesMod) {
        settingsMap.putValue(SettingsConstants.TYPES_MOD, typesMod);
        return this;
    }

    public Settings setTypesMod(boolean... bools) {
        return setTypesMod(getEnum(TypesMod.class, bools));
    }

    public boolean isTypesFollowEvolutions() {
        return settingsMap.getValue(SettingsConstants.TYPES_FOLLOW_EVOS);
    }

    public Settings setTypesFollowEvos(boolean typesFollowEvos) {
        settingsMap.putValue(SettingsConstants.TYPES_FOLLOW_EVOS, typesFollowEvos);
        return this;
    }

    public boolean isTypesRandomizeFirst() {
        return settingsMap.getValue(SettingsConstants.TYPES_RANDOMIZE_FIRST);
    }

    public Settings setTypesRandomizeFirst(boolean typesRandomizeFirst) {
        settingsMap.putValue(SettingsConstants.TYPES_RANDOMIZE_FIRST, typesRandomizeFirst);
        return this;
    }

    public EvolutionsMod getEvolutionsMod() {
        return settingsMap.getValue(SettingsConstants.EVOLUTIONS_MOD);
    }

    public Settings setEvolutionsMod(EvolutionsMod evolutionsMod) {
        settingsMap.putValue(SettingsConstants.EVOLUTIONS_MOD, evolutionsMod);
        return this;
    }

    public Settings setEvolutionsMod(boolean... bools) {
        return setEvolutionsMod(getEnum(EvolutionsMod.class, bools));
    }

    public boolean isChangeImpossibleEvolutions() {
        return settingsMap.getValue(SettingsConstants.CHANGE_IMPOSSIBLE_EVOLUTIONS);
    }

    public Settings setChangeImpossibleEvolutions(boolean changeImpossibleEvolutions) {
        settingsMap.putValue(SettingsConstants.CHANGE_IMPOSSIBLE_EVOLUTIONS, changeImpossibleEvolutions);
        return this;
    }

    public boolean isMakeEvolutionsEasier() {
        return settingsMap.getValue(SettingsConstants.MAKE_EVOLUTIONS_EASIER);
    }

    public Settings setMakeEvolutionsEasier(boolean makeEvolutionsEasier) {
        settingsMap.putValue(SettingsConstants.MAKE_EVOLUTIONS_EASIER, makeEvolutionsEasier);
        return this;
    }

    public boolean isEvosSimilarStrength() {
        return settingsMap.getValue(SettingsConstants.EVOS_SIMILAR_STRENGTH);
    }

    public Settings setEvosSimilarStrength(boolean evosSimilarStrength) {
        settingsMap.putValue(SettingsConstants.EVOS_SIMILAR_STRENGTH, evosSimilarStrength);
        return this;
    }

    public boolean isEvosSameTyping() {
        return settingsMap.getValue(SettingsConstants.EVOS_SAME_TYPING);
    }

    public Settings setEvosSameTyping(boolean evosSameTyping) {
        settingsMap.putValue(SettingsConstants.EVOS_SAME_TYPING, evosSameTyping);
        return this;
    }

    public boolean isEvosChangeMethod() {
        return settingsMap.getValue(SettingsConstants.EVOS_CHANGE_METHOD);
    }

    public Settings setEvosChangeMethod(boolean evosChangeMethod) {
        settingsMap.putValue(SettingsConstants.EVOS_CHANGE_METHOD, evosChangeMethod);
        return this;
    }

    public boolean isEvosMaxThreeStages() {
        return settingsMap.getValue(SettingsConstants.EVOS_MAX_THREE_STAGES);
    }

    public Settings setEvosMaxThreeStages(boolean evosMaxThreeStages) {
        settingsMap.putValue(SettingsConstants.EVOS_MAX_THREE_STAGES, evosMaxThreeStages);
        return this;
    }

    public boolean isEvosForceChange() {
        return settingsMap.getValue(SettingsConstants.EVOS_FORCE_CHANGE);
    }

    public Settings setEvosForceChange(boolean evosForceChange) {
        settingsMap.putValue(SettingsConstants.EVOS_FORCE_CHANGE, evosForceChange);
        return this;
    }

    public boolean isEvosNoConverge() {
        return settingsMap.getValue(SettingsConstants.EVOS_NO_CONVERGE);
    }

    public Settings setEvosNoConverge(boolean evosNoConverge) {
        settingsMap.putValue(SettingsConstants.EVOS_NO_CONVERGE, evosNoConverge);
        return this;
    }
    
    public boolean isEvosForceGrowth() {
        return settingsMap.getValue(SettingsConstants.EVOS_FORCE_GROWTH);
    }

    public Settings setEvosForceGrowth(boolean evosForceGrowth) {
        settingsMap.putValue(SettingsConstants.EVOS_FORCE_GROWTH, evosForceGrowth);
        return this;
    }

    public StartersMod getStartersMod() {
        return settingsMap.getValue(SettingsConstants.STARTERS_MOD);
    }

    public Settings setStartersMod(StartersMod startersMod) {
        settingsMap.putValue(SettingsConstants.STARTERS_MOD, startersMod);
        return this;
    }

    public Settings setStartersMod(boolean... bools) {
        return setStartersMod(getEnum(StartersMod.class, bools));
    }

    public int[] getCustomStarters() {
        return settingsMap.getValue(SettingsConstants.CUSTOM_STARTERS);
    }

    public Settings setCustomStarters(int[] customStarters) {
        settingsMap.putValue(SettingsConstants.CUSTOM_STARTERS, customStarters);
        return this;
    }

    public boolean isRandomizeStartersHeldItems() {
        return settingsMap.getValue(SettingsConstants.RANDOMIZE_STARTERS_HELD_ITEMS);
    }

    public Settings setRandomizeStartersHeldItems(boolean randomizeStartersHeldItems) {
        settingsMap.putValue(SettingsConstants.RANDOMIZE_STARTERS_HELD_ITEMS, randomizeStartersHeldItems);
        return this;
    }

    public boolean isBanBadRandomStarterHeldItems() {
        return settingsMap.getValue(SettingsConstants.BAN_BAD_RANDOM_STARTER_HELD_ITEMS);
    }

    public Settings setBanBadRandomStarterHeldItems(boolean banBadRandomStarterHeldItems) {
        settingsMap.putValue(SettingsConstants.BAN_BAD_RANDOM_STARTER_HELD_ITEMS, banBadRandomStarterHeldItems);
        return this;
    }

    public boolean isStartersNoSplit() {
        return settingsMap.getValue(SettingsConstants.STARTERS_NO_SPLIT);
    }

    public Settings setStartersNoSplit(boolean startersNoSplit) {
        settingsMap.putValue(SettingsConstants.STARTERS_NO_SPLIT, startersNoSplit);
        return this;
    }

    public boolean isStartersUniqueTypes() {
        return settingsMap.getValue(SettingsConstants.STARTERS_UNIQUE_TYPES);
    }
    
    public Settings setStartersUniqueTypes(boolean startersUniqueTypes) {
        settingsMap.putValue(SettingsConstants.STARTERS_UNIQUE_TYPES, startersUniqueTypes);
        return this;
    }

    public boolean isStartersLimitBST() {
        return settingsMap.getValue(SettingsConstants.STARTERS_LIMIT_BST);
    }

    public Settings setStartersLimitBST(boolean starterLimitBST) {
        settingsMap.putValue(SettingsConstants.STARTERS_LIMIT_BST, starterLimitBST);
        return this;
    }

    public int getStartersBSTLimitModifier() {
        return settingsMap.getValue(SettingsConstants.STARTERS_BST_MODIFIER);
    }

    public Settings setStartersBSTLimitModifier(int starterBSTModifier) {
        settingsMap.putValue(SettingsConstants.STARTERS_BST_MODIFIER, starterBSTModifier);
        return this;
    }

    public boolean isStartersBaseEvoOnly() {
        return settingsMap.getValue(SettingsConstants.STARTERS_BASE_EVO_ONLY);
    }

    public Settings setStartersBaseEvoOnly(boolean startersBaseEvoOnly) {
        settingsMap.putValue(SettingsConstants.STARTERS_BASE_EVO_ONLY, startersBaseEvoOnly);
        return this;
    }

    public boolean isStartersExactEvo() {
        return settingsMap.getValue(SettingsConstants.STARTERS_EXACT_EVOS);
    }

    public Settings setStartersExactEvos(boolean startersExactEvos) {
        settingsMap.putValue(SettingsConstants.STARTERS_EXACT_EVOS, startersExactEvos);
        return this;
    }

    public int getStartersMinimumEvos() {
        return settingsMap.getValue(SettingsConstants.STARTERS_MINIMUM_EVOS);
    }

    public Settings setStartersMinimumEvos(int startersMinimumEvos) {
        settingsMap.putValue(SettingsConstants.STARTERS_MINIMUM_EVOS, startersMinimumEvos);
        return this;
    }

    public boolean isStartersSETriangle() {
        return settingsMap.getValue(SettingsConstants.STARTERS_SE_TRIANGLE);
    }

    public Settings setStartersSETriangle(boolean startersSETriangle) {
        settingsMap.putValue(SettingsConstants.STARTERS_SE_TRIANGLE, startersSETriangle);
        return this;
    }

    public boolean isRandomizeMovePowers() {
        return settingsMap.getValue(SettingsConstants.RANDOMIZE_MOVE_POWERS);
    }

    public Settings setRandomizeMovePowers(boolean randomizeMovePowers) {
        settingsMap.putValue(SettingsConstants.RANDOMIZE_MOVE_POWERS, randomizeMovePowers);
        return this;
    }

    public boolean isRandomizeMoveAccuracies() {
        return settingsMap.getValue(SettingsConstants.RANDOMIZE_MOVE_ACCURACIES);
    }

    public Settings setRandomizeMoveAccuracies(boolean randomizeMoveAccuracies) {
        settingsMap.putValue(SettingsConstants.RANDOMIZE_MOVE_ACCURACIES, randomizeMoveAccuracies);
        return this;
    }

    public boolean isRandomizeMovePPs() {
        return settingsMap.getValue(SettingsConstants.RANDOMIZE_MOVE_PPS);
    }

    public Settings setRandomizeMovePPs(boolean randomizeMovePPs) {
        settingsMap.putValue(SettingsConstants.RANDOMIZE_MOVE_PPS, randomizeMovePPs);
        return this;
    }

    public boolean isRandomizeMoveTypes() {
        return settingsMap.getValue(SettingsConstants.RANDOMIZE_MOVE_TYPES);
    }

    public Settings setRandomizeMoveTypes(boolean randomizeMoveTypes) {
        settingsMap.putValue(SettingsConstants.RANDOMIZE_MOVE_TYPES, randomizeMoveTypes);
        return this;
    }

    public boolean isRandomizeMoveCategory() {
        return settingsMap.getValue(SettingsConstants.RANDOMIZE_MOVE_CATEGORY);
    }

    public Settings setRandomizeMoveCategory(boolean randomizeMoveCategory) {
        settingsMap.putValue(SettingsConstants.RANDOMIZE_MOVE_CATEGORY, randomizeMoveCategory);
        return this;
    }

    public boolean isUpdateMoves() {
        return settingsMap.getValue(SettingsConstants.UPDATE_MOVES);
    }

    public Settings setUpdateMoves(boolean updateMoves) {
        settingsMap.putValue(SettingsConstants.UPDATE_MOVES, updateMoves);
        return this;
    }

    public boolean isUpdateMovesLegacy() {
        return settingsMap.getValue(SettingsConstants.UPDATE_MOVES_LEGACY);
    }

    public Settings setUpdateMovesLegacy(boolean updateMovesLegacy) {
        settingsMap.putValue(SettingsConstants.UPDATE_MOVES_LEGACY, updateMovesLegacy);
        return this;
    }

    public MovesetsMod getMovesetsMod() {
        return settingsMap.getValue(SettingsConstants.MOVESETS_MOD);
    }

    public Settings setMovesetsMod(MovesetsMod movesetsMod) {
        settingsMap.putValue(SettingsConstants.MOVESETS_FORCE_GOOD_DAMAGING, movesetsMod);
        return this;
    }

    public Settings setMovesetsMod(boolean... bools) {
        return setMovesetsMod(getEnum(MovesetsMod.class, bools));
    }

    public boolean isStartWithGuaranteedMoves() {
        return settingsMap.getValue(SettingsConstants.START_WITH_GUARANTEED_MOVES);
    }

    public Settings setStartWithGuaranteedMoves(boolean startWithGuaranteedMoves) {
        settingsMap.putValue(SettingsConstants.START_WITH_GUARANTEED_MOVES, startWithGuaranteedMoves);
        return this;
    }

    public int getGuaranteedMoveCount() {
        return settingsMap.getValue(SettingsConstants.GUARANTEED_MOVE_COUNT);
    }

    public Settings setGuaranteedMoveCount(int guaranteedMoveCount) {
        settingsMap.putValue(SettingsConstants.GUARANTEED_MOVE_COUNT, guaranteedMoveCount);
        return this;
    }

    public boolean isReorderDamagingMoves() {
        return settingsMap.getValue(SettingsConstants.REORDER_DAMAGING_MOVES);
    }

    public Settings setReorderDamagingMoves(boolean reorderDamagingMoves) {
        settingsMap.putValue(SettingsConstants.REORDER_DAMAGING_MOVES, reorderDamagingMoves);
        return this;
    }

    public boolean isMovesetsForceGoodDamaging() {
        return settingsMap.getValue(SettingsConstants.MOVESETS_FORCE_GOOD_DAMAGING);
    }

    public Settings setMovesetsForceGoodDamaging(boolean movesetsForceGoodDamaging) {
        settingsMap.putValue(SettingsConstants.MOVESETS_FORCE_GOOD_DAMAGING, movesetsForceGoodDamaging);
        return this;
    }

    public int getMovesetsGoodDamagingPercent() {
        return settingsMap.getValue(SettingsConstants.MOVESETS_GOOD_DAMAGING_PERCENT);
    }

    public Settings setMovesetsGoodDamagingPercent(int movesetsGoodDamagingPercent) {
        settingsMap.putValue(SettingsConstants.MOVESETS_GOOD_DAMAGING_PERCENT, movesetsGoodDamagingPercent);
        return this;
    }

    public TrainersMod getTrainersMod() {
        return settingsMap.getValue(SettingsConstants.TRAINERS_MOD);
    }

    public Settings setTrainersMod(TrainersMod trainersMod) {
        settingsMap.putValue(SettingsConstants.TRAINERS_MOD, trainersMod);
        return this;
    }

    public Settings setTrainersMod(boolean... bools) {
        return setTrainersMod(getEnum(TrainersMod.class, bools));
    }

    public boolean isRivalCarriesStarterThroughout() {
        return settingsMap.getValue(SettingsConstants.RIVAL_CARRIES_STARTER_THROUGHOUT);
    }

    public Settings setRivalCarriesStarterThroughout(boolean rivalCarriesStarterThroughout) {
        settingsMap.putValue(SettingsConstants.RIVAL_CARRIES_STARTER_THROUGHOUT, rivalCarriesStarterThroughout);
        return this;
    }

    public boolean isRivalCarriesTeamThroughout() {
        return settingsMap.getValue(SettingsConstants.RIVAL_CARRIES_TEAM_THROUGHOUT);
    }

    public Settings setRivalCarriesTeamThroughout(boolean rivalCarriesTeamThroughout) {
        settingsMap.putValue(SettingsConstants.RIVAL_CARRIES_TEAM_THROUGHOUT, rivalCarriesTeamThroughout);
        return this;
    }

    public boolean isTrainersUsePokemonOfSimilarStrength() {
        return settingsMap.getValue(SettingsConstants.TRAINERS_USE_POKEMON_OF_SIMILAR_STRENGTH);
    }

    public Settings setTrainersUsePokemonOfSimilarStrength(boolean trainersUsePokemonOfSimilarStrength) {
        settingsMap.putValue(SettingsConstants.TRAINERS_USE_POKEMON_OF_SIMILAR_STRENGTH, trainersUsePokemonOfSimilarStrength);
        return this;
    }

    public boolean isTrainersMatchTypingDistribution() {
        return settingsMap.getValue(SettingsConstants.TRAINERS_MATCH_TYPING_DISTRIBUTION);
    }

    public Settings setTrainersMatchTypingDistribution(boolean trainersMatchTypingDistribution) {
        settingsMap.putValue(SettingsConstants.TRAINERS_MATCH_TYPING_DISTRIBUTION, trainersMatchTypingDistribution);
        return this;
    }

    public boolean isTrainersBlockLegendaries() {
        return settingsMap.getValue(SettingsConstants.TRAINERS_BLOCK_LEGENDARIES);
    }

    public Settings setTrainersBlockLegendaries(boolean trainersBlockLegendaries) {
        settingsMap.putValue(SettingsConstants.TRAINERS_BLOCK_LEGENDARIES, trainersBlockLegendaries);
        return this;
    }

    public boolean isTrainersBlockEarlyWonderGuard() {
        return settingsMap.getValue(SettingsConstants.TRAINERS_BLOCK_EARLY_WONDER_GUARD);
    }

    public Settings setTrainersBlockEarlyWonderGuard(boolean trainersBlockEarlyWonderGuard) {
        settingsMap.putValue(SettingsConstants.TRAINERS_BLOCK_EARLY_WONDER_GUARD, trainersBlockEarlyWonderGuard);
        return this;
    }

    public boolean isGymTypeTheme() {
        return settingsMap.getValue(SettingsConstants.GYM_TYPE_THEME);
    }

    public Settings setGymTypeTheme(boolean gymTypeTheme) {
        settingsMap.putValue(SettingsConstants.GYM_TYPE_THEME, gymTypeTheme);
        return this;
    }

    public boolean isTrainersRandomHeldItem() {
        return settingsMap.getValue(SettingsConstants.TRAINERS_RANDOM_HELD_ITEM);
    }

    public Settings setTrainersRandomHeldItem(boolean trainersRandomHeldItem) {
        settingsMap.putValue(SettingsConstants.TRAINERS_RANDOM_HELD_ITEM, trainersRandomHeldItem);
        return this;
    }

    public boolean isRandomizeTrainerNames() {
        return settingsMap.getValue(SettingsConstants.RANDOMIZE_TRAINER_NAMES);
    }

    public Settings setRandomizeTrainerNames(boolean randomizeTrainerNames) {
        settingsMap.putValue(SettingsConstants.RANDOMIZE_TRAINER_NAMES, randomizeTrainerNames);
        return this;
    }

    public boolean isRandomizeTrainerClassNames() {
        return settingsMap.getValue(SettingsConstants.RANDOMIZE_TRAINER_CLASS_NAMES);
    }

    public Settings setRandomizeTrainerClassNames(boolean randomizeTrainerClassNames) {
        settingsMap.putValue(SettingsConstants.RANDOMIZE_TRAINER_CLASS_NAMES, randomizeTrainerClassNames);
        return this;
    }

    public boolean isTrainersForceFullyEvolved() {
        return settingsMap.getValue(SettingsConstants.TRAINERS_FORCE_FULLY_EVOLVED);
    }

    public Settings setTrainersForceFullyEvolved(boolean trainersForceFullyEvolved) {
        settingsMap.putValue(SettingsConstants.TRAINERS_FORCE_FULLY_EVOLVED, trainersForceFullyEvolved);
        return this;
    }

    public int getTrainersForceFullyEvolvedLevel() {
        return settingsMap.getValue(SettingsConstants.TRAINERS_FORCE_FULLY_EVOLVED_LEVEL);
    }

    public Settings setTrainersForceFullyEvolvedLevel(int trainersForceFullyEvolvedLevel) {
        settingsMap.putValue(SettingsConstants.TRAINERS_FORCE_FULLY_EVOLVED_LEVEL, trainersForceFullyEvolvedLevel);
        return this;
    }

    public boolean isTrainersLevelModified() {
        return settingsMap.getValue(SettingsConstants.TRAINERS_LEVEL_MODIFIED);
    }

    public Settings setTrainersLevelModified(boolean trainersLevelModified) {
        settingsMap.putValue(SettingsConstants.TRAINERS_LEVEL_MODIFIED, trainersLevelModified);
        return this;
    }

    public int getTrainersLevelModifier() {
        return settingsMap.getValue(SettingsConstants.TRAINERS_LEVEL_MODIFIER);
    }

    public Settings setTrainersLevelModifier(int trainersLevelModifier) {
        settingsMap.putValue(SettingsConstants.TRAINERS_LEVEL_MODIFIER, trainersLevelModifier);
        return this;
    }

    public WildPokemonMod getWildPokemonMod() {
        return settingsMap.getValue(SettingsConstants.WILD_POKEMON_MOD);
    }

    public Settings setWildPokemonMod(WildPokemonMod wildPokemonMod) {
        settingsMap.putValue(SettingsConstants.WILD_POKEMON_MOD, wildPokemonMod);
        return this;
    }

    public Settings setWildPokemonMod(boolean... bools) {
        return setWildPokemonMod(getEnum(WildPokemonMod.class, bools));
    }

    public WildPokemonRestrictionMod getWildPokemonRestrictionMod() {
        return settingsMap.getValue(SettingsConstants.WILD_POKEMON_RESTRICTION_MOD);
    }

    public Settings setWildPokemonRestrictionMod(WildPokemonRestrictionMod wildPokemonRestrictionMod) {
        settingsMap.putValue(SettingsConstants.WILD_POKEMON_RESTRICTION_MOD, wildPokemonRestrictionMod);
        return this;
    }

    public Settings setWildPokemonRestrictionMod(boolean... bools) {
        return setWildPokemonRestrictionMod(getEnum(WildPokemonRestrictionMod.class, bools));
    }

    public boolean isUseTimeBasedEncounters() {
        return settingsMap.getValue(SettingsConstants.USE_TIME_BASED_ENCOUNTERS);
    }

    public Settings setUseTimeBasedEncounters(boolean useTimeBasedEncounters) {
        settingsMap.putValue(SettingsConstants.USE_TIME_BASED_ENCOUNTERS, useTimeBasedEncounters);
        return this;
    }

    public boolean isBlockWildLegendaries() {
        return settingsMap.getValue(SettingsConstants.BLOCK_WILD_LEGENDARIES);
    }

    public Settings setBlockWildLegendaries(boolean blockWildLegendaries) {
        settingsMap.putValue(SettingsConstants.BLOCK_WILD_LEGENDARIES, blockWildLegendaries);
        return this;
    }

    public boolean isUseMinimumCatchRate() {
        return settingsMap.getValue(SettingsConstants.USE_MINIMUM_CATCH_RATE);
    }

    public Settings setUseMinimumCatchRate(boolean useMinimumCatchRate) {
        settingsMap.putValue(SettingsConstants.USE_MINIMUM_CATCH_RATE, useMinimumCatchRate);
        return this;
    }

    public int getMinimumCatchRateLevel() {
        return settingsMap.getValue(SettingsConstants.MINIMUM_CATCH_RATE_LEVEL);
    }

    public Settings setMinimumCatchRateLevel(int minimumCatchRateLevel) {
        settingsMap.putValue(SettingsConstants.MINIMUM_CATCH_RATE_LEVEL, minimumCatchRateLevel);
        return this;
    }

    public boolean isRandomizeWildPokemonHeldItems() {
        return settingsMap.getValue(SettingsConstants.RANDOMIZE_WILD_POKEMON_HELD_ITEMS);
    }

    public Settings setRandomizeWildPokemonHeldItems(boolean randomizeWildPokemonHeldItems) {
        settingsMap.putValue(SettingsConstants.RANDOMIZE_WILD_POKEMON_HELD_ITEMS, randomizeWildPokemonHeldItems);
        return this;
    }

    public boolean isBanBadRandomWildPokemonHeldItems() {
        return settingsMap.getValue(SettingsConstants.BAN_BAD_RANDOM_WILD_POKEMON_HELD_ITEMS);
    }

    public Settings setBanBadRandomWildPokemonHeldItems(boolean banBadRandomWildPokemonHeldItems) {
        settingsMap.putValue(SettingsConstants.BAN_BAD_RANDOM_WILD_POKEMON_HELD_ITEMS, banBadRandomWildPokemonHeldItems);
        return this;
    }

    public boolean isAllowLowLevelEvolvedTypes() {
        return settingsMap.getValue(SettingsConstants.ALLOW_LOW_LEVEL_EVOLVED_TYPES);
    }

    public Settings setAllowLowLevelEvolvedTypes(boolean allowLowLevelEvolvedTypes) {
        settingsMap.putValue(SettingsConstants.ALLOW_LOW_LEVEL_EVOLVED_TYPES, allowLowLevelEvolvedTypes);
        return this;
    }

    public StaticPokemonMod getStaticPokemonMod() {
        return settingsMap.getValue(SettingsConstants.STATIC_POKEMON_MOD);
    }

    public Settings setStaticPokemonMod(StaticPokemonMod staticPokemonMod) {
        settingsMap.putValue(SettingsConstants.STATIC_POKEMON_MOD, staticPokemonMod);
        return this;
    }

    public Settings setStaticPokemonMod(boolean... bools) {
        return setStaticPokemonMod(getEnum(StaticPokemonMod.class, bools));
    }

    public TMsMod getTmsMod() {
        return settingsMap.getValue(SettingsConstants.TMS_MOD);
    }

    public Settings setTmsMod(TMsMod tmsMod) {
        settingsMap.putValue(SettingsConstants.TMS_MOD, tmsMod);
        return this;
    }

    public Settings setTmsMod(boolean... bools) {
        return setTmsMod(getEnum(TMsMod.class, bools));
    }

    public boolean isTmLevelUpMoveSanity() {
        return settingsMap.getValue(SettingsConstants.TM_LEVEL_UP_MOVE_SANITY);
    }

    public Settings setTmLevelUpMoveSanity(boolean tmLevelUpMoveSanity) {
        settingsMap.putValue(SettingsConstants.TM_LEVEL_UP_MOVE_SANITY, tmLevelUpMoveSanity);
        return this;
    }

    public boolean isKeepFieldMoveTMs() {
        return settingsMap.getValue(SettingsConstants.KEEP_FIELD_MOVE_TMS);
    }

    public Settings setKeepFieldMoveTMs(boolean keepFieldMoveTMs) {
        settingsMap.putValue(SettingsConstants.KEEP_FIELD_MOVE_TMS, keepFieldMoveTMs);
        return this;
    }

    public boolean isFullHMCompat() {
        return settingsMap.getValue(SettingsConstants.FULL_HM_COMPAT);
    }

    public Settings setFullHMCompat(boolean fullHMCompat) {
        settingsMap.putValue(SettingsConstants.FULL_HM_COMPAT, fullHMCompat);
        return this;
    }

    public boolean isTmsForceGoodDamaging() {
        return settingsMap.getValue(SettingsConstants.TMS_FORCE_GOOD_DAMAGING);
    }

    public Settings setTmsForceGoodDamaging(boolean tmsForceGoodDamaging) {
        settingsMap.putValue(SettingsConstants.TMS_FORCE_GOOD_DAMAGING, tmsForceGoodDamaging);
        return this;
    }

    public int getTmsGoodDamagingPercent() {
        return settingsMap.getValue(SettingsConstants.TMS_GOOD_DAMAGING_PERCENT);
    }

    public Settings setTmsGoodDamagingPercent(int tmsGoodDamagingPercent) {
        settingsMap.putValue(SettingsConstants.TMS_GOOD_DAMAGING_PERCENT, tmsGoodDamagingPercent);
        return this;
    }

    public TMsHMsCompatibilityMod getTmsHmsCompatibilityMod() {
        return settingsMap.getValue(SettingsConstants.TMS_HMS_COMPATIBILITY_MOD);
    }

    public Settings setTmsHmsCompatibilityMod(TMsHMsCompatibilityMod tmsHmsCompatibilityMod) {
        settingsMap.putValue(SettingsConstants.TMS_HMS_COMPATIBILITY_MOD, tmsHmsCompatibilityMod);
        return this;
    }

    public Settings setTmsHmsCompatibilityMod(boolean... bools) {
        return setTmsHmsCompatibilityMod(getEnum(TMsHMsCompatibilityMod.class, bools));
    }

    public MoveTutorMovesMod getMoveTutorMovesMod() {
        return settingsMap.getValue(SettingsConstants.MOVE_TUTOR_MOVES_MOD);
    }

    public Settings setMoveTutorMovesMod(MoveTutorMovesMod moveTutorMovesMod) {
        settingsMap.putValue(SettingsConstants.MOVE_TUTOR_MOVES_MOD, moveTutorMovesMod);
        return this;
    }

    public Settings setMoveTutorMovesMod(boolean... bools) {
        return setMoveTutorMovesMod(getEnum(MoveTutorMovesMod.class, bools));
    }

    public boolean isTutorLevelUpMoveSanity() {
        return settingsMap.getValue(SettingsConstants.TUTOR_LEVEL_UP_MOVE_SANITY);
    }

    public Settings setTutorLevelUpMoveSanity(boolean tutorLevelUpMoveSanity) {
        settingsMap.putValue(SettingsConstants.TUTOR_LEVEL_UP_MOVE_SANITY, tutorLevelUpMoveSanity);
        return this;
    }

    public boolean isKeepFieldMoveTutors() {
        return settingsMap.getValue(SettingsConstants.KEEP_FIELD_MOVE_TUTORS);
    }

    public Settings setKeepFieldMoveTutors(boolean keepFieldMoveTutors) {
        settingsMap.putValue(SettingsConstants.KEEP_FIELD_MOVE_TUTORS, keepFieldMoveTutors);
        return this;
    }

    public boolean isTutorsForceGoodDamaging() {
        return settingsMap.getValue(SettingsConstants.TUTORS_FORCE_GOOD_DAMAGING);
    }

    public Settings setTutorsForceGoodDamaging(boolean tutorsForceGoodDamaging) {
        settingsMap.putValue(SettingsConstants.TUTORS_FORCE_GOOD_DAMAGING, tutorsForceGoodDamaging);
        return this;
    }

    public int getTutorsGoodDamagingPercent() {
        return settingsMap.getValue(SettingsConstants.TUTORS_GOOD_DAMAGING_PERCENT);
    }

    public Settings setTutorsGoodDamagingPercent(int tutorsGoodDamagingPercent) {
        settingsMap.putValue(SettingsConstants.TUTORS_GOOD_DAMAGING_PERCENT, tutorsGoodDamagingPercent);
        return this;
    }

    public MoveTutorsCompatibilityMod getMoveTutorsCompatibilityMod() {
        return settingsMap.getValue(SettingsConstants.MOVE_TUTORS_COMPATIBILITY_MOD);
    }

    public Settings setMoveTutorsCompatibilityMod(MoveTutorsCompatibilityMod moveTutorsCompatibilityMod) {
        settingsMap.putValue(SettingsConstants.MOVE_TUTORS_COMPATIBILITY_MOD, moveTutorsCompatibilityMod);
        return this;
    }

    public Settings setMoveTutorsCompatibilityMod(boolean... bools) {
        return setMoveTutorsCompatibilityMod(getEnum(MoveTutorsCompatibilityMod.class, bools));
    }

    public InGameTradesMod getInGameTradesMod() {
        return settingsMap.getValue(SettingsConstants.IN_GAME_TRADES_MOD);
    }

    public Settings setInGameTradesMod(InGameTradesMod inGameTradesMod) {
        settingsMap.putValue(SettingsConstants.IN_GAME_TRADES_MOD, inGameTradesMod);
        return this;
    }

    public Settings setInGameTradesMod(boolean... bools) {
        return setInGameTradesMod(getEnum(InGameTradesMod.class, bools));
    }

    public boolean isRandomizeInGameTradesNicknames() {
        return settingsMap.getValue(SettingsConstants.RANDOMIZE_IN_GAME_TRADES_NICKNAMES);
    }

    public Settings setRandomizeInGameTradesNicknames(boolean randomizeInGameTradesNicknames) {
        settingsMap.putValue(SettingsConstants.RANDOMIZE_IN_GAME_TRADES_NICKNAMES, randomizeInGameTradesNicknames);
        return this;
    }

    public boolean isRandomizeInGameTradesOTs() {
        return settingsMap.getValue(SettingsConstants.RANDOMIZE_IN_GAME_TRADES_OTS);
    }

    public Settings setRandomizeInGameTradesOTs(boolean randomizeInGameTradesOTs) {
        settingsMap.putValue(SettingsConstants.RANDOMIZE_IN_GAME_TRADES_OTS, randomizeInGameTradesOTs);
        return this;
    }

    public boolean isRandomizeInGameTradesIVs() {
        return settingsMap.getValue(SettingsConstants.RANDOMIZE_IN_GAME_TRADES_IVS);
    }

    public Settings setRandomizeInGameTradesIVs(boolean randomizeInGameTradesIVs) {
        settingsMap.putValue(SettingsConstants.RANDOMIZE_IN_GAME_TRADES_IVS, randomizeInGameTradesIVs);
        return this;
    }

    public boolean isRandomizeInGameTradesItems() {
        return settingsMap.getValue(SettingsConstants.RANDOMIZE_IN_GAME_TRADES_ITEMS);
    }

    public Settings setRandomizeInGameTradesItems(boolean randomizeInGameTradesItems) {
        settingsMap.putValue(SettingsConstants.RANDOMIZE_IN_GAME_TRADES_ITEMS, randomizeInGameTradesItems);
        return this;
    }

    public FieldItemsMod getFieldItemsMod() {
        return settingsMap.getValue(SettingsConstants.FIELD_ITEMS_MOD);
    }

    public Settings setFieldItemsMod(FieldItemsMod fieldItemsMod) {
        settingsMap.putValue(SettingsConstants.FIELD_ITEMS_MOD, fieldItemsMod);
        return this;
    }

    public Settings setFieldItemsMod(boolean... bools) {
        return setFieldItemsMod(getEnum(FieldItemsMod.class, bools));
    }

    public boolean isBanBadRandomFieldItems() {
        return settingsMap.getValue(SettingsConstants.BAN_BAD_RANDOM_FIELD_ITEMS);
    }

    public Settings setBanBadRandomFieldItems(boolean banBadRandomFieldItems) {
        settingsMap.putValue(SettingsConstants.BAN_BAD_RANDOM_FIELD_ITEMS, banBadRandomFieldItems);
        return this;
    }

    public int getCurrentMiscTweaks() {
        return settingsMap.getValue(SettingsConstants.CURRENT_MISC_TWEAKS);
    }

    public Settings setCurrentMiscTweaks(int currentMiscTweaks) {
        settingsMap.putValue(SettingsConstants.CURRENT_MISC_TWEAKS, currentMiscTweaks);
        return this;
    }

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
        value = (value >> index) & 0x01;
        return value == 0x01; //((value >> index) & 0x01) == 0x01;
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