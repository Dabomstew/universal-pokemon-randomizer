package com.dabomstew.pkrandom;

/*----------------------------------------------------------------------------*/
/*--  Randomizer.java - Can randomize a file based on settings.             --*/
/*--                    Output varies by seed.                              --*/
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import com.dabomstew.pkrandom.constants.GlobalConstants;
import com.dabomstew.pkrandom.pokemon.*;
import com.dabomstew.pkrandom.romhandlers.Gen1RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen5RomHandler;
import com.dabomstew.pkrandom.romhandlers.RomHandler;
import com.dabomstew.pkrandom.settings.Settings;

// Can randomize a file based on settings. Output varies by seed.
public class Randomizer {

    private final Settings settings;
    private final RomHandler romHandler;
    private List<Trainer> originalTrainers = new ArrayList<Trainer>();

    public Randomizer(Settings settings, RomHandler romHandler) {
        this.settings = settings;
        this.romHandler = romHandler;
    }

    public int randomize(final String filename) {
        long seed = RandomSource.pickSeed();
        return randomize(filename, seed);
    }

    public int randomize(final String filename, long seed) {
        final long startTime = System.currentTimeMillis();
        RandomSource.seed(seed);
        final boolean raceMode = settings.isRaceMode();
        int checkValue = 0;
        
        // Cache original trainers
        for(Trainer t : romHandler.getTrainers()) {
            originalTrainers.add(new Trainer(t));
        }
                
        // Template stuff
        this.romHandler.getTemplateData().put("romHandler", romHandler);
        this.romHandler.getTemplateData().put("gen1", romHandler instanceof Gen1RomHandler);

        // limit pokemon based on generation
        if (settings.isLimitPokemon()) {
            romHandler.setPokemonPool(settings.getCurrentRestrictions());
            romHandler.removeEvosForPokemonPool();
        } else {
            romHandler.setPokemonPool(null);
        }

        // Gen 5/6 Move stat updates & data changes
        if (settings.isUpdateMoves() || settings.isUpdateMovesLegacy()) {
            // Regardless of whether Gen 5, Gen 6, or both, initialize the 
            // change data structure
            romHandler.initMoveModernization();

            // Update to Gen 5 if selected and not already a Gen 5 ROM
            if (settings.isUpdateMovesLegacy()  && !(romHandler instanceof Gen5RomHandler)) {
                romHandler.updateMovesToGen5();
            }

            // Update to Gen 6 if selected, which will override some Gen 5 updates
            if (settings.isUpdateMoves()) {
                romHandler.updateMovesToGen6();
            }
            romHandler.printMoveModernization();
        }

        if (settings.isRandomizeMovePowers()) {
            romHandler.randomizeMovePowers();
        }

        if (settings.isRandomizeMoveAccuracies()) {
            romHandler.randomizeMoveAccuracies();
        }

        if (settings.isRandomizeMovePPs()) {
            romHandler.randomizeMovePPs();
        }

        if (settings.isRandomizeMoveTypes()) {
            romHandler.randomizeMoveTypes();
        }

        if (settings.isRandomizeMoveCategory() && romHandler.hasPhysicalSpecialSplit()) {
            romHandler.randomizeMoveCategory();
        }

        // Misc Tweaks?
        int currentMiscTweaks = settings.getCurrentMiscTweaks();
        if (romHandler.miscTweaksAvailable() != 0) {
            int codeTweaksAvailable = romHandler.miscTweaksAvailable();
            List<MiscTweak> tweaksToApply = new ArrayList<MiscTweak>();
            Map<String, Boolean> tweakMap = new HashMap<String, Boolean>();

            for (MiscTweak mt : MiscTweak.allTweaks) {
                if ((codeTweaksAvailable & mt.getValue()) > 0 && (currentMiscTweaks & mt.getValue()) > 0) {
                    tweaksToApply.add(mt);
                }
            }

            // Sort so priority is respected in tweak ordering.
            Collections.sort(tweaksToApply);
            romHandler.getTemplateData().put("tweakMap", tweakMap);

            // Now apply in order.
            for (MiscTweak mt : tweaksToApply) {
                romHandler.applyMiscTweak(mt);
            }
        }

        if (settings.isStandardizeEXPCurves()) {
            romHandler.standardizeEXPCurves();
        }
        
        if (settings.isUpdateBaseStats()) {
            romHandler.updatePokemonStats();
        }
        
        // Base stats adjustment (pre-evolution randomization)
        if (settings.isStatsRandomizeFirst()) {
            maybeChangeStats(romHandler);
        }

        // Pokemon Types (pre-evolution randomization)
        if(settings.isTypesRandomizeFirst()) {
            maybeChangeTypes(romHandler);
        }

        // Pokemon evolutions
        maybeChangeEvolutions(romHandler);
        
        // Base stats adjustment (post-evolution randomization)
        if (!settings.isStatsRandomizeFirst()) {
            maybeChangeStats(romHandler);
        }

        // Pokemon Types (post-evolution randomization)
        if(!settings.isTypesRandomizeFirst()) {
            maybeChangeTypes(romHandler);
        }

        // Wild Held Items?
        if (settings.isRandomizeWildPokemonHeldItems()) {
            romHandler.randomizeWildHeldItems(settings.isBanBadRandomWildPokemonHeldItems());
        }

        // Abilities? (new 1.0.2)
        if (romHandler.abilitiesPerPokemon() > 0 && settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE) {
            romHandler.randomizeAbilities(settings.isAbilitiesFollowEvolutions(), settings.isAllowWonderGuard(),
                    settings.isBanTrappingAbilities(), settings.isBanNegativeAbilities());
        }

        maybeLogBaseStatAndTypeChanges(romHandler);
        for (Pokemon pkmn : romHandler.getPokemon()) {
            if (pkmn != null) {
                checkValue = addToCV(checkValue, pkmn.hp, pkmn.attack, pkmn.defense, pkmn.speed, pkmn.spatk,
                        pkmn.spdef, pkmn.ability1, pkmn.ability2, pkmn.ability3);
            }
        }

        // Starter Pokemon
        // Applied after type to update the strings correctly based on new types
        maybeChangeAndLogStarters(romHandler);

        // Move Data Log
        // Placed here so it matches its position in the randomizer interface
        maybeLogMoveChanges(romHandler);

        // Movesets
        boolean noBrokenMoves = settings.doBlockBrokenMoves();
        boolean forceLv1s = romHandler.supportsFourStartingMoves() && settings.isStartWithGuaranteedMoves();
        int forceLv1Count = settings.getGuaranteedMoveCount();
        double msGoodDamagingProb = settings.isMovesetsForceGoodDamaging() ? settings.getMovesetsGoodDamagingPercent() / 100.0
                : 0;
        if (settings.getMovesetsMod() == Settings.MovesetsMod.RANDOM_PREFER_SAME_TYPE) {
            romHandler.randomizeMovesLearnt(true, noBrokenMoves, forceLv1s, forceLv1Count, msGoodDamagingProb);
        } else if (settings.getMovesetsMod() == Settings.MovesetsMod.COMPLETELY_RANDOM) {
            romHandler.randomizeMovesLearnt(false, noBrokenMoves, forceLv1s, forceLv1Count, msGoodDamagingProb);
        } else if (noBrokenMoves) {
            romHandler.removeBrokenMoves();
        }

        if (settings.isReorderDamagingMoves()) {
            romHandler.orderDamagingMovesByDamage();
        }

        // Trade evolutions removal
        if (settings.isChangeImpossibleEvolutions()) {
            romHandler.removeTradeEvolutions(!(settings.getMovesetsMod() == Settings.MovesetsMod.UNCHANGED),
                settings.isEvosChangeMethod());
        }

        // Easier evolutions
        if (settings.isMakeEvolutionsEasier()) {
            romHandler.condenseLevelEvolutions(GlobalConstants.MAXIMUM_EVO_LEVEL, GlobalConstants.MAXIMUM_INTERMEDIATE_EVO_LEVEL);
        }

        // Show the new movesets if applicable
        if (settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY) {
            romHandler.getTemplateData().put("logPokemonMoves", "metronome");
        } 
        else if (settings.getMovesetsMod() != Settings.MovesetsMod.UNCHANGED) {
            romHandler.getTemplateData().put("logPokemonMoves", "random");
        }

        // Trainer Pokemon
        if (settings.getTrainersMod() == Settings.TrainersMod.UNCHANGED){
            romHandler.modifyTrainerPokes(settings.isTrainersRandomHeldItem(), settings.isTrainersLevelModified() ? settings.getTrainersLevelModifier() : 0);
        }
        else {
            romHandler.randomizeTrainerPokes(settings.isTrainersUsePokemonOfSimilarStrength(),
            settings.isTrainersMatchTypingDistribution(), settings.isTrainersBlockLegendaries(),
            settings.isTrainersBlockEarlyWonderGuard(),
            (settings.getCurrentMiscTweaks() & MiscTweak.USE_RESISTANT_TYPE.getValue()) > 0,
            settings.getTrainersMod() == Settings.TrainersMod.TYPE_THEMED,
            settings.getTrainersMod() == Settings.TrainersMod.GLOBAL_MAPPING, 
            settings.isGymTypeTheme(), settings.isTrainersRandomHeldItem(),
            settings.isTrainersLevelModified() ? settings.getTrainersLevelModifier() : 0);
        }

        if ((settings.getTrainersMod() != Settings.TrainersMod.UNCHANGED || settings.getStartersMod() != Settings.StartersMod.UNCHANGED)
                && settings.isRivalCarriesStarterThroughout()) {
                    // First randomize the starter
                    romHandler.rivalCarriesStarter(settings.isTrainersBlockLegendaries());
                    // Then randomize the team
                    if (settings.isRivalCarriesTeamThroughout()) {
                        romHandler.rivalCarriesTeam();
                    }
        }

        if (settings.isTrainersForceFullyEvolved()) {
            romHandler.forceFullyEvolvedTrainerPokes(settings.getTrainersForceFullyEvolvedLevel());
        }

        // Trainer names & class names randomization
        // done before trainer log to add proper names

        if (romHandler.canChangeTrainerText()) {
            if (settings.isRandomizeTrainerClassNames()) {
                romHandler.randomizeTrainerClassNames(settings.getCustomNames());
            }

            if (settings.isRandomizeTrainerNames()) {
                romHandler.randomizeTrainerNames(settings.getCustomNames());
            }
        }

        maybeLogTrainerChanges(romHandler);

        // Apply metronome only mode now that trainers have been dealt with
        if (settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY) {
            romHandler.metronomeOnlyMode();
        }

        List<Trainer> trainers = romHandler.getTrainers();
        for (Trainer t : trainers) {
            for (TrainerPokemon tpk : t.getPokemon()) {
                checkValue = addToCV(checkValue, tpk.level, tpk.pokemon.number);
            }
        }

        // Static Pokemon
        checkValue = maybeChangeAndLogStaticPokemon(romHandler, raceMode, checkValue);

        // Wild Pokemon
        if (settings.isUseMinimumCatchRate()) {
            boolean gen5 = romHandler instanceof Gen5RomHandler;
            int normalMin, legendaryMin;
            switch (settings.getMinimumCatchRateLevel()) {
            case 1:
            default:
                normalMin = gen5 ? 50 : 75;
                legendaryMin = gen5 ? 25 : 37;
                break;
            case 2:
                normalMin = gen5 ? 100 : 128;
                legendaryMin = gen5 ? 45 : 64;
                break;
            case 3:
                normalMin = gen5 ? 180 : 200;
                legendaryMin = gen5 ? 75 : 100;
                break;
            case 4:
                normalMin = legendaryMin = 255;
                break;
            }
            romHandler.minimumCatchRate(normalMin, legendaryMin);
        }

        switch (settings.getWildPokemonMod()) {
        case RANDOM:
            romHandler.randomEncounters(settings.isUseTimeBasedEncounters(),
                    settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.CATCH_EM_ALL,
                    settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.TYPE_THEME_AREAS,
                    settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.SIMILAR_STRENGTH,
                    settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.MATCH_TYPING_DISTRIBUTION,
                    settings.isBlockWildLegendaries(), settings.isAllowLowLevelEvolvedTypes());
            break;
        case AREA_MAPPING:
            romHandler.area1to1Encounters(settings.isUseTimeBasedEncounters(),
                    settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.CATCH_EM_ALL,
                    settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.TYPE_THEME_AREAS,
                    settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.SIMILAR_STRENGTH,
                    settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.MATCH_TYPING_DISTRIBUTION,
                    settings.isBlockWildLegendaries(), settings.isAllowLowLevelEvolvedTypes());
            break;
        case GLOBAL_MAPPING:
            romHandler.game1to1Encounters(settings.isUseTimeBasedEncounters(),
                    settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.SIMILAR_STRENGTH,
                    settings.isBlockWildLegendaries());
            break;
        default:
            break;
        }

        maybeLogWildPokemonChanges(romHandler);
        List<EncounterSet> encounters = romHandler.getEncounters(settings.isUseTimeBasedEncounters());
        for (EncounterSet es : encounters) {
            for (Encounter e : es.encounters) {
                checkValue = addToCV(checkValue, e.level, e.pokemon.number);
            }
        }

        // TMs
        if (!(settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY)
                && settings.getTmsMod() == Settings.TMsMod.RANDOM) {
            double goodDamagingProb = settings.isTmsForceGoodDamaging() ? settings.getTmsGoodDamagingPercent() / 100.0
                    : 0;
            romHandler.randomizeTMMoves(noBrokenMoves, settings.isKeepFieldMoveTMs(), goodDamagingProb);
            List<Integer> tmMoves = romHandler.getTMMoves();
            for (int i = 0; i < tmMoves.size(); i++) {
                checkValue = addToCV(checkValue, tmMoves.get(i));
            }
            romHandler.getTemplateData().put("logTMMoves", "random");
        } else if (settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY) {
            romHandler.getTemplateData().put("logTMMoves", "metronome");
        }

        // TM/HM compatibility
        switch (settings.getTmsHmsCompatibilityMod()) {
        case RANDOM_PREFER_TYPE:
            romHandler.randomizeTMHMCompatibility(true);
            break;
        case COMPLETELY_RANDOM:
            romHandler.randomizeTMHMCompatibility(false);
            break;
        case FULL:
            romHandler.fullTMHMCompatibility();
            break;
        default:
            break;
        }

        if (settings.isTmLevelUpMoveSanity()) {
            romHandler.ensureTMCompatSanity();
        }

        if (settings.isFullHMCompat()) {
            romHandler.fullHMCompatibility();
        }

        // Move Tutors (new 1.0.3)
        if (romHandler.hasMoveTutors()) {
            if (settings.getMovesetsMod() != Settings.MovesetsMod.METRONOME_ONLY
                    && settings.getMoveTutorMovesMod() == Settings.MoveTutorMovesMod.RANDOM) {
                List<Integer> oldMtMoves = romHandler.getMoveTutorMoves();
                double goodDamagingProb = settings.isTutorsForceGoodDamaging() ? settings
                        .getTutorsGoodDamagingPercent() / 100.0 : 0;
                romHandler.randomizeMoveTutorMoves(noBrokenMoves, settings.isKeepFieldMoveTutors(), goodDamagingProb);
                List<Integer> newMtMoves = romHandler.getMoveTutorMoves();
                for (int i = 0; i < newMtMoves.size(); i++) {
                    checkValue = addToCV(checkValue, newMtMoves.get(i));
                }
                romHandler.getTemplateData().put("logTutorMoves", "random");
                romHandler.getTemplateData().put("oldTutorMoves", oldMtMoves);
            } else if (settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY) {
                romHandler.getTemplateData().put("logTutorMoves", "metronome");
            } 

            // Compatibility
            switch (settings.getMoveTutorsCompatibilityMod()) {
            case RANDOM_PREFER_TYPE:
                romHandler.randomizeMoveTutorCompatibility(true);
                break;
            case COMPLETELY_RANDOM:
                romHandler.randomizeMoveTutorCompatibility(false);
                break;
            case FULL:
                romHandler.fullMoveTutorCompatibility();
                break;
            default:
                break;
            }

            if (settings.isTutorLevelUpMoveSanity()) {
                romHandler.ensureMoveTutorCompatSanity();
            }
        }

        // In-game trades
        List<IngameTrade> oldTrades = romHandler.getIngameTrades();
        if (settings.getInGameTradesMod() == Settings.InGameTradesMod.RANDOMIZE_GIVEN) {
            romHandler.randomizeIngameTrades(false, settings.isRandomizeInGameTradesNicknames(),
                    settings.isRandomizeInGameTradesOTs(), settings.isRandomizeInGameTradesIVs(),
                    settings.isRandomizeInGameTradesItems(), settings.getCustomNames());
        } else if (settings.getInGameTradesMod() == Settings.InGameTradesMod.RANDOMIZE_GIVEN_AND_REQUESTED) {
            romHandler.randomizeIngameTrades(true, settings.isRandomizeInGameTradesNicknames(),
                    settings.isRandomizeInGameTradesOTs(), settings.isRandomizeInGameTradesIVs(),
                    settings.isRandomizeInGameTradesItems(), settings.getCustomNames());
        }

        if (settings.getInGameTradesMod() != Settings.InGameTradesMod.UNCHANGED) {
            romHandler.getTemplateData().put("oldTrades", oldTrades);
        }

        // Field Items
        if (settings.getFieldItemsMod() == Settings.FieldItemsMod.SHUFFLE) {
            romHandler.shuffleFieldItems();
        } else if (settings.getFieldItemsMod() == Settings.FieldItemsMod.RANDOM) {
            romHandler.randomizeFieldItems(settings.isBanBadRandomFieldItems());
        }

        // Signature...
        romHandler.applySignature();

        // Record check value?
        romHandler.writeCheckValueToROM(checkValue);

        // Save
        romHandler.saveRom(filename);

        // Log tail
        romHandler.getTemplateData().put("elapsed", (System.currentTimeMillis() - startTime));
        romHandler.getTemplateData().put("rngCalls", RandomSource.callsSinceSeed());
        romHandler.getTemplateData().put("rngSeed", RandomSource.getSeed());
        romHandler.getTemplateData().put("settingsString", Settings.VERSION + settings.toString());
        romHandler.generateTableOfContents();
                
        return checkValue;
    }

    private void maybeLogBaseStatAndTypeChanges(final RomHandler romHandler) {
        // Log base stats & types if changed at all
        if (settings.getBaseStatisticsMod() != Settings.BaseStatisticsMod.UNCHANGED
                || settings.getTypesMod() != Settings.TypesMod.UNCHANGED
                || settings.getAbilitiesMod() != Settings.AbilitiesMod.UNCHANGED
                || settings.isRandomizeWildPokemonHeldItems()) {
            romHandler.getTemplateData().put("logPokemon", true);
        } 
    }

    private void maybeChangeEvolutions(final RomHandler romHandler) {
        if (settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM) {
            romHandler.randomizeEvolutions(settings.isEvosSimilarStrength(), settings.isEvosSameTyping(),
                    settings.isEvosChangeMethod(), settings.isEvosMaxThreeStages(), settings.isEvosForceChange(),
                    settings.isEvosNoConverge(), settings.isEvosForceGrowth());

            List<Pokemon> allPokes = romHandler.getPokemon();
            List<Pokemon> basePokes = new ArrayList<Pokemon>();
            for (Pokemon pk : allPokes) {
                if (pk != null) {
                    int numEvos = pk.evolutionsFrom.size();
                    if (numEvos > 0) {
                        if (pk.evolutionsTo.size() == 0) {
                            basePokes.add(pk);
                        }
                    }
                }
            }
            romHandler.getTemplateData().put("basePokes", basePokes);
        }        
    }

    private void maybeChangeStats(final RomHandler romHandler) {
        switch (settings.getBaseStatisticsMod()) {
        case SHUFFLE_ORDER:
            romHandler.shufflePokemonStats(settings.isBaseStatsFollowEvolutions());
            break;
        case SHUFFLE_BST:
            romHandler.shuffleAllPokemonBSTs(settings.isBaseStatsFollowEvolutions(), false);
            break;
        case SHUFFLE_ALL:
            romHandler.shufflePokemonStats(settings.isBaseStatsFollowEvolutions());
            romHandler.shuffleAllPokemonBSTs(settings.isBaseStatsFollowEvolutions(), false);
            break;
        case RANDOM_WITHIN_BST:
            romHandler.randomizePokemonStatsWithinBST(settings.isBaseStatsFollowEvolutions());
            break;
        case RANDOM_UNRESTRICTED:
            romHandler.randomizePokemonStatsUnrestricted(settings.isBaseStatsFollowEvolutions());
            break;
        case RANDOM_COMPLETELY:
            romHandler.randomizeCompletelyPokemonStats(settings.isBaseStatsFollowEvolutions());
            break;
        default:
            break;
        }
    }

    private void maybeChangeTypes(final RomHandler romHandler) {
        switch (settings.getTypesMod()) {
        case SHUFFLE:
            romHandler.shufflePokemonTypes();
            break;
        case RANDOM_RETAIN:
            romHandler.randomizeRetainPokemonTypes(settings.isTypesFollowEvolutions());
            break;
        case COMPLETELY_RANDOM:
            romHandler.randomizePokemonTypes(settings.isTypesFollowEvolutions());
            break;
        default:
            break;
        }
    }

    private void maybeChangeAndLogStarters(final RomHandler romHandler) {
        if (romHandler.canChangeStarters()) {  
            if (settings.getStartersMod() == Settings.StartersMod.CUSTOM) {
                List<Pokemon> starters = new ArrayList();
                romHandler.getTemplateData().put("logStarters", "custom");
                for(Integer customStarter : settings.getCustomStarters()) {
                    starters.add(romHandler.getPokemon().get(customStarter));
                }
                // Ensure starter list only contains 2
                if (romHandler.isYellow()) {
                    starters = starters.subList(0, 2);                    
                }
                romHandler.setStarters(starters);
                romHandler.getTemplateData().put("startersList", starters);
            } else if (settings.getStartersMod() == Settings.StartersMod.RANDOM) {
                int starterCount = romHandler.isYellow() ? 2 : 3;
                List<Pokemon> starters = new ArrayList<Pokemon>();
                ArrayList<Type> typeArr = getSETriangleTypes();
                selectRandomStarter(starterCount, starters, () -> romHandler.randomStarterPokemon(
                    settings.isStartersNoSplit(), settings.isStartersUniqueTypes(), 
                    settings.isStartersBaseEvoOnly(),
                    settings.isStartersLimitBST() ? settings.getStartersBSTLimitModifier() : 9999,
                    settings.getStartersMinimumEvos(), settings.isStartersExactEvo(), typeArr));

                romHandler.setStarters(starters);
                romHandler.getTemplateData().put("startersList", starters);
            }
            if (settings.isRandomizeStartersHeldItems() && !(romHandler instanceof Gen1RomHandler)) {
                romHandler.randomizeStarterHeldItems(settings.isBanBadRandomStarterHeldItems());
            }
        }
    }

    private void selectRandomStarter(int starterCount, List<Pokemon> starters, 
            Supplier<Pokemon> randomPicker) {
        Set<Type> typesUsed = new HashSet<Type>();
        starters.add(randomPicker.get());
        for (int i = 1; i < starterCount; i++) {
            Pokemon pkmn = randomPicker.get();
            while (starters.contains(pkmn) || 
                   (settings.isStartersUniqueTypes() &&
                   (typesUsed.contains(pkmn.primaryType) || 
                    pkmn.secondaryType != null && typesUsed.contains(pkmn.secondaryType)) ||
                    (settings.isStartersSETriangle() &&
                    (!starters.get(i-1).isWeakTo(pkmn))))) {
                pkmn = randomPicker.get();
            }
            starters.add(pkmn);
            typesUsed.add(pkmn.primaryType);
            if(pkmn.secondaryType != null) {
                typesUsed.add(pkmn.secondaryType);
            }
        }
    }

    private ArrayList<Type> getSETriangleTypes() {
        if (!settings.isStartersSETriangle()) {
            return null;
        }
        ArrayList<Type> typeArr = new ArrayList<Type>();
        // Iterate until type triangle is found
        boolean found = false;
        while(!found) {
            // Optimistic break
            found = true;
            for(int i = 0; i < 3; i++) {
                if (typeArr.size() > i) {
                    Type currentType = typeArr.get(i);
                    Type checkType = typeArr.get((i+2)%3);
                    if (Type.STRONG_AGAINST.get(checkType.ordinal()).contains(currentType)) {
                        continue;
                    } else {
                        found = false;
                        typeArr.set(i, Type.randomWeakness(RandomSource.instance(), false, checkType));
                    }
                } else {
                    found = false;
                    typeArr.add(romHandler.randomType());
                }
            }
        }

        return typeArr;
    }

    private void maybeLogWildPokemonChanges(final RomHandler romHandler) {
        if (settings.getWildPokemonMod() != Settings.WildPokemonMod.UNCHANGED) {
            List<EncounterSet> encounters = romHandler.getEncounters(settings.isUseTimeBasedEncounters());
            romHandler.getTemplateData().put("wildPokemon", encounters);
        }
    }

    private void maybeLogTrainerChanges(final RomHandler romHandler) {
        if (settings.getTrainersMod() != Settings.TrainersMod.UNCHANGED || settings.isRivalCarriesStarterThroughout()
            || settings.isTrainersRandomHeldItem()) {
            romHandler.getTemplateData().put("originalTrainers", originalTrainers);
        }
    }

    private int maybeChangeAndLogStaticPokemon(final RomHandler romHandler, boolean raceMode,
            int checkValue) {
        List<Pokemon> oldStatics = romHandler.getStaticPokemon();
        if (romHandler.canChangeStaticPokemon()) {
            if (settings.getStaticPokemonMod() == Settings.StaticPokemonMod.RANDOM_MATCHING) {
                romHandler.randomizeStaticPokemon(true);
            } else if (settings.getStaticPokemonMod() == Settings.StaticPokemonMod.COMPLETELY_RANDOM) {
                romHandler.randomizeStaticPokemon(false);
            }

            // Update Check Value
            List<Pokemon> newStatics = romHandler.getStaticPokemon();
            if (settings.getStaticPokemonMod() != Settings.StaticPokemonMod.UNCHANGED) {
                for (int i = 0; i < oldStatics.size(); i++) {
                    checkValue = addToCV(checkValue, newStatics.get(i).number);
                }
            }
        }
        return checkValue;
    }

    private void maybeLogMoveChanges(final RomHandler romHandler) {
        if (settings.isRandomizeMoveAccuracies() || settings.isRandomizeMovePowers()
                || settings.isRandomizeMovePPs() || settings.isRandomizeMoveCategory()
                || settings.isRandomizeMoveTypes() || settings.isUpdateMoves() 
                || settings.isUpdateMovesLegacy()) {
            romHandler.getTemplateData().put("logMoves", true);
        }
    }

    private static int addToCV(int checkValue, int... values) {
        for (int value : values) {
            checkValue = Integer.rotateLeft(checkValue, 3);
            checkValue ^= value;
        }
        return checkValue;
    }
}