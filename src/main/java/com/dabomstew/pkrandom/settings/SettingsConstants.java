package com.dabomstew.pkrandom.settings;

import java.util.ResourceBundle;

/*----------------------------------------------------------------------------*/
/*--  SettingsConstants.java - static values for all setting names          --*/
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

public class SettingsConstants {
    public static final String LIMIT_POKEMON = "limitPokemon";
    public static final String CURRENT_RESTRICTIONS = "currentRestrictions";
    public static final String BLOCK_BROKEN_MOVES = "blockBrokenMoves";
    public static final String RACE_MODE = "raceMode";
    public static final String BASE_STATISTICS_MOD = "baseStatisticsMod";
    public static final String STANDARDIZE_EXP_CURVES = "standardizeEXPCurves";
    public static final String UPDATE_BASE_STATS = "updateBaseStats";
    public static final String BASE_STATS_FOLLOW_EVOLUTIONS = "baseStatsFollowEvolutions";
    public static final String STATS_RANDOMIZE_FIRST = "statsRandomizeFirst";
    public static final String ABILITIES_MOD = "abilitiesMod";
    public static final String ALLOW_WONDER_GUARD = "allowWonderGuard";
    public static final String ABILITIES_FOLLOW_EVOLUTIONS = "abilitiesFollowEvolutions";
    public static final String BAN_TRAPPING_ABILITIES = "banTrappingAbilities";
    public static final String BAN_NEGATIVE_ABILITIES = "banNegativeAbilities";
    public static final String TYPES_MOD = "typesMod";
    public static final String TYPES_FOLLOW_EVOS = "typesFollowEvos";
    public static final String TYPES_RANDOMIZE_FIRST = "typesRandomizeFirst";
    public static final String EVOLUTIONS_MOD = "evolutionsMod";
    public static final String CHANGE_IMPOSSIBLE_EVOLUTIONS = "changeImpossibleEvolutions";
    public static final String MAKE_EVOLUTIONS_EASIER = "makeEvolutionsEasier";
    public static final String EVOS_SIMILAR_STRENGTH = "evosSimilarStrength";
    public static final String EVOS_SAME_TYPING = "evosSameTyping";
    public static final String EVOS_CHANGE_METHOD = "evosChangeMethod";
    public static final String EVOS_MAX_THREE_STAGES = "evosMaxThreeStages";
    public static final String EVOS_FORCE_CHANGE = "evosForceChange";
    public static final String EVOS_NO_CONVERGE = "evosNoConverge";
    public static final String EVOS_FORCE_GROWTH = "evosForceGrowth";
    public static final String STARTERS_MOD = "startersMod";
    public static final String CUSTOM_STARTERS = "customStarters";
    public static final String RANDOMIZE_STARTERS_HELD_ITEMS = "randomizeStartersHeldItems";
    public static final String BAN_BAD_RANDOM_STARTER_HELD_ITEMS = "banBadRandomStarterHeldItems";
    public static final String STARTERS_NO_SPLIT = "startersNoSplit";
    public static final String STARTERS_UNIQUE_TYPES = "startersUniqueTypes";
    public static final String STARTERS_LIMIT_BST = "startersLimitBST";
    public static final String STARTERS_BST_MODIFIER = "startersBSTModifier";
    public static final String STARTERS_BASE_EVO_ONLY = "startersBaseEvoOnly";
    public static final String STARTERS_EXACT_EVOS = "startersExactEvos";
    public static final String STARTERS_MINIMUM_EVOS = "startersMinimumEvos";
    public static final String RANDOMIZE_MOVE_POWERS = "randomizeMovePowers";
    public static final String RANDOMIZE_MOVE_ACCURACIES = "randomizeMoveAccuracies";
    public static final String RANDOMIZE_MOVE_PPS = "randomizeMovePPs";
    public static final String RANDOMIZE_MOVE_TYPES = "randomizeMoveTypes";
    public static final String RANDOMIZE_MOVE_CATEGORY = "randomizeMoveCategory";
    public static final String UPDATE_MOVES = "updateMoves";
    public static final String UPDATE_MOVES_LEGACY = "updateMovesLegacy";
    public static final String MOVESETS_MOD = "movesetsMod";
    public static final String START_WITH_GUARANTEED_MOVES = "startWithGuaranteedMoves";
    public static final String GUARANTEED_MOVE_COUNT = "guaranteedMoveCount";
    public static final String REORDER_DAMAGING_MOVES = "reorderDamagingMoves";
    public static final String MOVESETS_FORCE_GOOD_DAMAGING = "movesetsForceGoodDamaging";
    public static final String MOVESETS_GOOD_DAMAGING_PERCENT = "movesetsGoodDamagingPercent";

    public static final ResourceBundle bundle = ResourceBundle.getBundle("com/dabomstew/pkrandom/gui/Bundle");
    public static final int BST_MINIMUM_INT = Integer.parseInt(bundle.getString("RandomizerGUI.spBSTLimitSlider.minimum"));
    public static final int BST_MAXIMUM_INT = Integer.parseInt(bundle.getString("RandomizerGUI.spBSTLimitSlider.maximum"));
}
