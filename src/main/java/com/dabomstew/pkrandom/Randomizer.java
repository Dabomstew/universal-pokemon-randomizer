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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import com.dabomstew.pkrandom.pokemon.*;
import com.dabomstew.pkrandom.romhandlers.Gen1RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen5RomHandler;
import com.dabomstew.pkrandom.romhandlers.RomHandler;

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
        return randomize(filename, new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
            }
        }));
    }

    public int randomize(final String filename, final PrintStream log) {
        long seed = RandomSource.pickSeed();
        return randomize(filename, log, seed);
    }

    public int randomize(final String filename, final PrintStream log, long seed) {
        final long startTime = System.currentTimeMillis();
        RandomSource.seed(seed);
        final boolean raceMode = settings.isRaceMode();
        int checkValue = 0;
        String cssContent = "";
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("com/dabomstew/pkrandom/gui/log.css");
        Scanner scanner = new Scanner(is);
        
        // Cache original trainers
        for(Trainer t : romHandler.getTrainers()) {
            originalTrainers.add(new Trainer(t));
        }

        //Stream contents of css style guide into String
        while(scanner.hasNextLine()) {
            cssContent += scanner.nextLine() + "\n";
        }
        scanner.close();
                
        //Initialize HTML log with CSS style guide
        log.println("<!DOCTYPE html>\n" + 
                "<head>\n" + 
                "  <title>" + romHandler.getROMName() + " randomization log</title>\n" + 
                "  <meta charset=\"UTF-8\">\n" + 
                "  <style type=\"text/css\">\n" + 
                cssContent +
                "  </style>\n" + 
                "</head>\n" + 
                "<body>");

        // limit pokemon based on generation
        if (settings.isLimitPokemon()) {
            romHandler.setPokemonPool(settings.getCurrentRestrictions());
            romHandler.removeEvosForPokemonPool();
        } else {
            romHandler.setPokemonPool(null);
        }

        // Gen 5/6 Move stat updates & data changes
        if (settings.isUpdateMoves()) {
            romHandler.initMoveUpdates();
            if (!(romHandler instanceof Gen5RomHandler)) {
                romHandler.updateMovesToGen5();
            }
            if (!settings.isUpdateMovesLegacy()) {
                romHandler.updateMovesToGen6();
            }
            romHandler.printMoveUpdates();
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

        List<Move> moves = romHandler.getMoves();

        // Misc Tweaks?
        int currentMiscTweaks = settings.getCurrentMiscTweaks();
        if (romHandler.miscTweaksAvailable() != 0) {
            int codeTweaksAvailable = romHandler.miscTweaksAvailable();
            List<MiscTweak> tweaksToApply = new ArrayList<MiscTweak>();

            for (MiscTweak mt : MiscTweak.allTweaks) {
                if ((codeTweaksAvailable & mt.getValue()) > 0 && (currentMiscTweaks & mt.getValue()) > 0) {
                    tweaksToApply.add(mt);
                }
            }

            // Sort so priority is respected in tweak ordering.
            Collections.sort(tweaksToApply);

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
            maybeChangeStats(log, romHandler);
        }

        // Pokemon Types (pre-evolution randomization)
        if(settings.isTypesRandomizeFirst()) {
            maybeChangeTypes(log, romHandler);
        }

        // Pokemon evolutions
        maybeChangeEvolutions(log, romHandler);
        
        // Base stats adjustment (post-evolution randomization)
        if (!settings.isStatsRandomizeFirst()) {
            maybeChangeStats(log, romHandler);
        }

        // Pokemon Types (post-evolution randomization)
        if(!settings.isTypesRandomizeFirst()) {
            maybeChangeTypes(log, romHandler);
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

        maybeLogBaseStatAndTypeChanges(log, romHandler);
        for (Pokemon pkmn : romHandler.getPokemon()) {
            if (pkmn != null) {
                checkValue = addToCV(checkValue, pkmn.hp, pkmn.attack, pkmn.defense, pkmn.speed, pkmn.spatk,
                        pkmn.spdef, pkmn.ability1, pkmn.ability2, pkmn.ability3);
            }
        }

        // Trade evolutions removal
        if (settings.isChangeImpossibleEvolutions()) {
            romHandler.removeTradeEvolutions(!(settings.getMovesetsMod() == Settings.MovesetsMod.UNCHANGED));
        }

        // Easier evolutions
        if (settings.isMakeEvolutionsEasier()) {
            romHandler.condenseLevelEvolutions(40, 30);
        }

        // Starter Pokemon
        // Applied after type to update the strings correctly based on new types
        maybeChangeAndLogStarters(log, romHandler);

        // Move Data Log
        // Placed here so it matches its position in the randomizer interface
        maybeLogMoveChanges(log, romHandler);

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

        // Show the new movesets if applicable
        if (settings.getMovesetsMod() == Settings.MovesetsMod.UNCHANGED) {
            if (settings.doBlockBrokenMoves()) {
                log.print("<h2>Pokemon Movesets</h2><p>Removed Game-Breaking Moves (");

                List<Integer> gameBreakingMoves = romHandler.getGameBreakingMoves();
                int numberPrinted = 0;

                for (Move move : moves) {
                    if (move == null) {
                        continue;
                    }

                    if (gameBreakingMoves.contains(move.number)) {
                        numberPrinted++;
                        log.print(move.name);

                        if (numberPrinted < gameBreakingMoves.size()) {
                            log.print(", ");
                        }
                    }
                }

                log.println(").</p>");
            } else {
                log.println("<h2>Pokemon Movesets</h2><p>Unchanged.</p>");
            }
        } else if (settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY) {
            log.println("<h2>Pokemon Movesets</h2><p>Metronome Only.</p>");
        } else {
            log.println("<h2>Pokemon Movesets</h2>");
            List<String> movesets = new ArrayList<String>();
            Map<Pokemon, List<MoveLearnt>> moveData = romHandler.getMovesLearnt();
            for (Pokemon pkmn : moveData.keySet()) {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("<h3>%03d %s</h3>", pkmn.number, pkmn.name));
                sb.append("<ul class=\"moveset\">");
                List<MoveLearnt> data = moveData.get(pkmn);
                for (MoveLearnt ml : data) {
                    try {
                        sb.append(String.format("<li><strong>%s</strong><em>Lv %d</em></li>", moves.get(ml.move).name, ml.level));
                    } catch (NullPointerException ex) {
                        sb.append(String.format("<li><span class=\"error\">invalid move at level %d</span></li>", ml.level));
                    }
                }
                sb.append("</ul>");
                movesets.add(sb.toString());
            }
            Collections.sort(movesets);
            for (String moveset : movesets) {
                log.println(moveset);
            }
            log.println();
        }

        // Trainer Pokemon
        if (settings.getTrainersMod() == Settings.TrainersMod.UNCHANGED){
            romHandler.levelUpTrainerPokes(settings.isTrainersLevelModified() ? settings.getTrainersLevelModifier() : 0);
        }
        if (settings.getTrainersMod() == Settings.TrainersMod.RANDOM) {
            romHandler.randomizeTrainerPokes(settings.isTrainersUsePokemonOfSimilarStrength(),
                    settings.isTrainersBlockLegendaries(), settings.isTrainersBlockEarlyWonderGuard(),
                    settings.isTrainersLevelModified() ? settings.getTrainersLevelModifier() : 0);
        } else if (settings.getTrainersMod() == Settings.TrainersMod.TYPE_THEMED) {
            romHandler.typeThemeTrainerPokes(settings.isTrainersUsePokemonOfSimilarStrength(),
                    settings.isTrainersMatchTypingDistribution(), settings.isTrainersBlockLegendaries(),
                    settings.isTrainersBlockEarlyWonderGuard(),
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

        maybeLogTrainerChanges(log, romHandler);

        // Apply metronome only mode now that trainers have been dealt with
        if (settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY) {
            romHandler.metronomeOnlyMode();
        }

        List<Trainer> trainers = romHandler.getTrainers();
        for (Trainer t : trainers) {
            for (TrainerPokemon tpk : t.pokemon) {
                checkValue = addToCV(checkValue, tpk.level, tpk.pokemon.number);
            }
        }

        // Static Pokemon
        checkValue = maybeChangeAndLogStaticPokemon(log, romHandler, raceMode, checkValue);

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

        maybeLogWildPokemonChanges(log, romHandler);
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
            log.println("<h2>TM Moves</h2>");
            log.println("<ul class=\"tm-list\">");
            List<Integer> tmMoves = romHandler.getTMMoves();
            for (int i = 0; i < tmMoves.size(); i++) {
                log.printf("<li><strong>TM%02d</strong> %s</li>", i + 1, moves.get(tmMoves.get(i)).name);
                checkValue = addToCV(checkValue, tmMoves.get(i));
            }
            log.println("</ul>");
        } else if (settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY) {
            log.println("<h2>TM Moves</h2><p>Metronome Only.<p>");
        } else {
            log.println("<h2>TM Moves</h2><p>Unchanged.<p>");
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
            if (!(settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY)
                    && settings.getMoveTutorMovesMod() == Settings.MoveTutorMovesMod.RANDOM) {
                List<Integer> oldMtMoves = romHandler.getMoveTutorMoves();
                double goodDamagingProb = settings.isTutorsForceGoodDamaging() ? settings
                        .getTutorsGoodDamagingPercent() / 100.0 : 0;
                romHandler.randomizeMoveTutorMoves(noBrokenMoves, settings.isKeepFieldMoveTutors(), goodDamagingProb);
                log.println("<h2>Move Tutor Moves</h2>");
                log.print("<ul>");
                List<Integer> newMtMoves = romHandler.getMoveTutorMoves();
                for (int i = 0; i < newMtMoves.size(); i++) {
                    log.printf("<li>%s => %s</li>", moves.get(oldMtMoves.get(i)).name,
                            moves.get(newMtMoves.get(i)).name);
                    checkValue = addToCV(checkValue, newMtMoves.get(i));
                }
                log.println("</ul>");
            } else if (settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY) {
                log.println("<h2>Move Tutor Moves</h2><p>Metronome Only.</p>");
            } else {
                log.println("<h2>Move Tutor Moves</h2><p>Unchanged.</p>");
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

        if (!(settings.getInGameTradesMod() == Settings.InGameTradesMod.UNCHANGED)) {
            log.println("<h2>In-Game Trades</h2>");
            log.println("<p>Trades are shown in pairs. New trades in <strong>bold</strong> on top, " + 
                "old trades below in <em>italics</em>.</p>");
            log.println("<table class=\"trades-table\">");
            log.println("<tr><th>Requested</th><th>Given</th><th>Held Item</th></tr>");
            List<IngameTrade> newTrades = romHandler.getIngameTrades();
            int size = oldTrades.size();
            for (int i = 0; i < size; i++) {
                IngameTrade oldT = oldTrades.get(i);
                IngameTrade newT = newTrades.get(i);
                log.printf("<tr><td>%s</td><td>%s (%s)</td><td>%d</td></tr>",
                newT.requestedPokemon.name, newT.givenPokemon.name, newT.nickname, newT.item);
                log.printf("<tr class=\"alt\"><td>%s</td><td>%s (%s)</td><td>%d</td></tr>",
                oldT.requestedPokemon.name, oldT.givenPokemon.name, oldT.nickname, oldT.item);
            }
            log.println("</table>");
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
        log.println("<hr>");
        log.println(String.format("<p>Randomization of <strong>%s</strong> completed.</p>", romHandler.getROMName()));
        log.println(String.format("<p>Time elapsed: %dms<p>", (System.currentTimeMillis() - startTime)));
        log.println(String.format("<p>RNG Calls: %d<p>", RandomSource.callsSinceSeed()));
        log.println(String.format("<p>RNG Seed: %d<p>", RandomSource.getSeed()));
        log.println(String.format("<p>Settings: %s<p>", settings.VERSION + settings.toString()));
        log.println("</body></html>");
        
        return checkValue;
    }

    private void maybeLogBaseStatAndTypeChanges(final PrintStream log, final RomHandler romHandler) {
        List<Pokemon> allPokes = romHandler.getPokemon();
        String[] itemNames = romHandler.getItemNames();
        // Log base stats & types if changed at all
        if (settings.getBaseStatisticsMod() == Settings.BaseStatisticsMod.UNCHANGED
                && settings.getTypesMod() == Settings.TypesMod.UNCHANGED
                && settings.getAbilitiesMod() == Settings.AbilitiesMod.UNCHANGED
                && !settings.isRandomizeWildPokemonHeldItems()) {
            log.println("<h2>Pokemon base stats & type</h2>");
            log.println("<p>Unchanged.</p>");
        } else {
            log.println("<h2>Pokemon Base Stats & Types</h2>");
            log.println("<table class=\"pk-table\">");
            if (romHandler instanceof Gen1RomHandler) {
                log.println("<tr><th>NUM</th><th>NAME</th><th>TYPE</th><th>HP</th><th>ATK</th><th>DEF</th><th>SPE</th><th>SPEC</th><th>TOTAL</th><th>BIG</th></tr>");
                for (Pokemon pkmn : allPokes) {
                    if (pkmn != null) {
                        String pkmnType1 = pkmn.primaryType == null ? "???" : pkmn.primaryType.toString();
                        pkmnType1 = String.format("<span class=\"pk-type %s\">%s</span>", pkmnType1.toLowerCase(), pkmnType1);
                        String pkmnType2 = "";
                        if (pkmn.secondaryType != null) {
                            pkmnType2 = pkmn.secondaryType.toString();
                            pkmnType2 = String.format("<span class=\"pk-type %s\">%s</span>", pkmnType2.toLowerCase(), pkmnType2);
                        }
                        int total = pkmn.hp + pkmn.attack + pkmn.defense + pkmn.speed + pkmn.special;
                        boolean topPoke = total > 590 || Collections.max(Arrays.asList(pkmn.hp, pkmn.attack, pkmn.defense, pkmn.speed, pkmn.special)) > 190;                        
                        log.printf("<tr%s><td>%3d</td><td class=\"left\">%s</td><td>%s%s</td><td>%d</td><td>%d</td><td>%d</td><td>%d</td><td>%d</td><td>%d</td><td>%s</td></tr>",
                                pkmn.number % 2 == 0 ? " class=\"alt\"" : "", pkmn.number, pkmn.name, pkmnType1, pkmnType2, pkmn.hp, pkmn.attack, pkmn.defense, pkmn.speed, pkmn.special, total, 
                                    topPoke ? "YES" : "");
                    }
                }
            } else {
                log.print("<tr><th>NUM</th><th>NAME</th><th>TYPE</th><th>HP</th><th>ATK</th><th>DEF</th><th>SPE</th><th>SATK</th><th>SDEF</th><th>TOTAL</th>");
                int abils = romHandler.abilitiesPerPokemon();
                for (int i = 0; i < abils; i++) {
                    log.printf("<th>ABILITY%d</th>", (i + 1));
                }
                log.print("<th>ITEM</th><th>BIG</th>");
                log.println("</tr>");
                for (Pokemon pkmn : allPokes) {
                    if (pkmn != null) {
                        String pkmnType1 = pkmn.primaryType == null ? "???" : pkmn.primaryType.toString();
                        pkmnType1 = String.format("<span class=\"pk-type %s\">%s</span>", pkmnType1.toLowerCase(), pkmnType1);
                        String pkmnType2 = "";
                        if (pkmn.secondaryType != null) {
                            pkmnType2 = pkmn.secondaryType.toString();
                            pkmnType2 = String.format("<span class=\"pk-type %s\">%s</span>", pkmnType2.toLowerCase(), pkmnType2);
                        }
                        int total = pkmn.hp + pkmn.attack + pkmn.defense + pkmn.speed + pkmn.spatk + pkmn.spdef;
                        boolean topPoke = total > 590 || Collections.max(Arrays.asList(pkmn.hp, pkmn.attack, pkmn.defense, pkmn.speed, pkmn.spatk, pkmn.spdef)) > 190;             
                        log.printf("<tr%s><td>%3d</td><td class=\"left\">%s</td><td>%s%s</td><td>%d</td><td>%d</td><td>%d</td><td>%d</td><td>%d</td><td>%d</td><td>%d</td>",
                                pkmn.number % 2 == 0 ? " class=\"alt\"" : "", pkmn.number, pkmn.name, pkmnType1, pkmnType2, pkmn.hp, pkmn.attack, pkmn.defense, pkmn.speed, pkmn.spatk, pkmn.spdef, total);
                        if (abils > 0) {
                            log.printf("<td>%s</td><td>%s", romHandler.abilityName(pkmn.ability1),
                                    romHandler.abilityName(pkmn.ability2));
                            if (abils > 2) {
                                log.printf("</td><td>%-12s", romHandler.abilityName(pkmn.ability3));
                            }
                        }
                        log.print("</td><td>");
                        if (pkmn.guaranteedHeldItem > 0) {
                            log.print(itemNames[pkmn.guaranteedHeldItem] + " (100%)");
                        } else {
                            int itemCount = 0;
                            if (pkmn.commonHeldItem > 0) {
                                itemCount++;
                                log.print(itemNames[pkmn.commonHeldItem] + " (common)");
                            }
                            if (pkmn.rareHeldItem > 0) {
                                if (itemCount > 0) {
                                    log.print(", ");
                                }
                                itemCount++;
                                log.print(itemNames[pkmn.rareHeldItem] + " (rare)");
                            }
                            if (pkmn.darkGrassHeldItem > 0) {
                                if (itemCount > 0) {
                                    log.print(", ");
                                }
                                itemCount++;
                                log.print(itemNames[pkmn.darkGrassHeldItem] + " (dark grass only)");
                            }
                        }
                        log.printf("</td><td>%s</td>", topPoke ? "YES" : "");
                        log.println("</tr>");
                    }
                }
            }
            log.println("</table>");
        }
    }

    private void maybeChangeEvolutions(final PrintStream log, final RomHandler romHandler) {
        if (settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM) {
            romHandler.randomizeEvolutions(settings.isEvosSimilarStrength(), settings.isEvosSameTyping(),
                    settings.isEvosMaxThreeStages(), settings.isEvosForceChange(), settings.isEvosNoConverge(), 
                    settings.isEvosForceGrowth());

            log.println("<h2>Randomized Evolutions</h2>\n<ul>");
            List<Pokemon> allPokes = romHandler.getPokemon();
            List<Pokemon> basePokes = new ArrayList<Pokemon>();
            for (Pokemon pk : allPokes) {
                if (pk != null) {
                    int numEvos = pk.evolutionsFrom.size();
                    if (numEvos > 0) {
                        if (pk.evolutionsTo.size() == 0) {
                            basePokes.add(pk);
                        }
                        StringBuilder evoStr = new StringBuilder(pk.evolutionsFrom.get(0).to.name);
                        for (int i = 1; i < numEvos; i++) {
                            if (i == numEvos - 1) {
                                evoStr.append(" and " + pk.evolutionsFrom.get(i).to.name);
                            } else {
                                evoStr.append(", " + pk.evolutionsFrom.get(i).to.name);
                            }
                        }
                        log.println("<li>" + pk.name + " now evolves into " + evoStr.toString() + "</li>");
                    }
                }
            }
            // Limited to 3 columns due to complexity of algining paths
            log.println("</ul><h2>New Evolution Paths</h2><table class=\"pk-table\">");
            for (Pokemon pk : basePokes) {
                log.println("<tr>");
                log.println("<td><div class=\"pk-chain-element\">" + pk.name + "</div></td><td>");
                for(Evolution e : pk.evolutionsFrom) {
                    log.println("<div class=\"pk-chain-element\">" + e.to.name + "</div>");
                }
                log.println("</td>");
                log.println("<td>");
                for(Evolution e: pk.evolutionsFrom) {
                    log.println("<div style=\"min-height:34px;margin:5px 0;\">");
                    for(Evolution ev: e.to.evolutionsFrom) {
                        log.println("<div class=\"pk-chain-element\">" + ev.to.name + "</div>");
                    }
                    log.println("</div>");
                }
                log.println("</td>");
                log.println("</tr>");

            }
            log.println("</table>");
        }        
    }

    private void maybeChangeStats(final PrintStream log, final RomHandler romHandler) {
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

    private void maybeChangeTypes(final PrintStream log, final RomHandler romHandler) {
        switch (settings.getTypesMod()) {
        case SHUFFLE:
            romHandler.shufflePokemonTypes();
            log.println("<h2>Shuffled Types</h2>");
            log.println("<table class=\"pk-table\">");
            log.println("<tr><th>Old Type</th><th>New Type</th></tr>");
            for(Type t: Type.getTypes(romHandler.getTypeSize())) {
                log.println("<tr><td>");
                log.println(String.format("<span class=\"pk-type %s\">%s</span> ", 
                    t.toString().toLowerCase(), 
                    t.toString()));
                log.println("</td><td>");
                log.println(String.format("<span class=\"pk-type %s\">%s</span> ", 
                    Type.getShuffledList().get(t.ordinal()).toString().toLowerCase(), 
                    Type.getShuffledList().get(t.ordinal()).toString()));
                log.println("</td></tr>");
            }
            log.println("</table>");
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

    private void maybeChangeAndLogStarters(final PrintStream log, final RomHandler romHandler) {
        if (romHandler.canChangeStarters()) {  
            if (settings.getStartersMod() == Settings.StartersMod.CUSTOM) {
                log.println("<h2>Custom Starters</h2>");
                log.println("<ul>");
                List<Pokemon> romPokemon = romHandler.getPokemon();
                int[] customStarters = settings.getCustomStarters();
                Pokemon pkmn1 = romPokemon.get(customStarters[0]);
                log.println(String.format("<li>Set starter 1 to <strong>%s</strong></li>", pkmn1.name));
                Pokemon pkmn2 = romPokemon.get(customStarters[1]);
                log.println(String.format("<li>Set starter 2 to <strong>%s</strong></li>", pkmn2.name));
                if (romHandler.isYellow()) {
                    romHandler.setStarters(Arrays.asList(pkmn1, pkmn2));
                } else {
                    Pokemon pkmn3 = romPokemon.get(customStarters[2]);
                    log.println(String.format("<li>Set starter 3 to <strong>%s</strong></li>", pkmn3.name));
                    romHandler.setStarters(Arrays.asList(pkmn1, pkmn2, pkmn3));
                }
                log.println("</ul>");
            } else if (settings.getStartersMod() == Settings.StartersMod.COMPLETELY_RANDOM) {
                // Randomise
                log.println("<h2>Random Starters</h2>");
                log.println("<ul>");
                int starterCount = 3;
                if (romHandler.isYellow()) {
                    starterCount = 2;
                }
                
                List<Pokemon> starters = new ArrayList<Pokemon>();
                selectRandomStarter(starterCount, starters, log, () -> romHandler.randomStarterPokemon(
                    settings.isStartersNoSplit(), settings.isStartersUniqueTypes(), 
                    settings.isStartersBaseEvoOnly(),
                    settings.isStartersLimitBST() ? settings.getStartersBSTLimitModifier() : 9999));

                romHandler.setStarters(starters);
                log.println("</ul>");
            } else if (settings.getStartersMod() == Settings.StartersMod.RANDOM_WITH_ONE_OR_TWO_EVOLUTIONS) {
                // Randomise
                log.println("<h2>Random 1/2-Evolution Starters</h2>");
                log.println("<ul>");
                int starterCount = 3;
                if (romHandler.isYellow()) {
                    starterCount = 2;
                }

                List<Pokemon> starters = new ArrayList<Pokemon>();
                selectRandomStarter(starterCount, starters, log, () -> 
                    romHandler.random1or2EvosPokemon(settings.isStartersNoSplit(), 
                    settings.isStartersUniqueTypes(), settings.isStartersBaseEvoOnly(),
                    settings.isStartersLimitBST() ? settings.getStartersBSTLimitModifier() : 9999));

                romHandler.setStarters(starters);
                log.println("</ul>");
            } else if (settings.getStartersMod() == Settings.StartersMod.RANDOM_WITH_TWO_EVOLUTIONS) {
                // Randomise
                log.println("<h2>Random 2-Evolution Starters</h2>");
                log.println("<ul>");
                int starterCount = 3;
                if (romHandler.isYellow()) {
                    starterCount = 2;
                }

                List<Pokemon> starters = new ArrayList<Pokemon>();
                selectRandomStarter(starterCount, starters, log, () -> 
                    romHandler.random2EvosPokemon(settings.isStartersNoSplit(), 
                    settings.isStartersUniqueTypes(), settings.isStartersBaseEvoOnly(),
                    settings.isStartersLimitBST() ? settings.getStartersBSTLimitModifier() : 9999));

                romHandler.setStarters(starters);
                log.println("</ul>");
            }
            if (settings.isRandomizeStartersHeldItems() && !(romHandler instanceof Gen1RomHandler)) {
                romHandler.randomizeStarterHeldItems(settings.isBanBadRandomStarterHeldItems());
            }
        }
    }

    private void selectRandomStarter(int starterCount, List<Pokemon> starters, 
    final PrintStream log, Supplier<Pokemon> randomPicker) {
        Set<Type> typesUsed = new HashSet<Type>();
        for (int i = 0; i < starterCount; i++) {
            Pokemon pkmn = randomPicker.get();
            while (starters.contains(pkmn) || 
                   (settings.isStartersUniqueTypes() &&
                   (typesUsed.contains(pkmn.primaryType) || 
                    pkmn.secondaryType != null && typesUsed.contains(pkmn.secondaryType)))) {
                pkmn = randomPicker.get();
            }
            log.println(String.format("<li>Set starter %d to <strong>%s</strong></li>", (i + 1), pkmn.name));
            starters.add(pkmn);
            typesUsed.add(pkmn.primaryType);
            if(pkmn.secondaryType != null) {
                typesUsed.add(pkmn.secondaryType);
            }
        }
    }

    private void maybeLogWildPokemonChanges(final PrintStream log, final RomHandler romHandler) {
        if (settings.getWildPokemonMod() == Settings.WildPokemonMod.UNCHANGED) {
            log.println("<h2>Wild Pokemon</h2><p>Unchanged.</p>");
        } else {
            log.println("<h2>Wild Pokemon</h2>");
            List<EncounterSet> encounters = romHandler.getEncounters(settings.isUseTimeBasedEncounters());
            int idx = 0;
            for (EncounterSet es : encounters) {
                idx++;
                String divCls = "";
                String ulCls = "";
                if (es.displayName != null) {
                    if(es.displayName.contains("Surfing")) {
                        divCls = "pk-set-surfing";
                        ulCls = "pk-list-surfing";
                    } else if(es.displayName.contains("Fishing") ||
                            es.displayName.contains("Old Rod") ||
                            es.displayName.contains("Good Rod") ||
                            es.displayName.contains("Super Rod")) {
                        divCls = "pk-set-fishing";
                        ulCls = "pk-list-fishing";
                    } else if(es.displayName.contains("Grass/Cave")) {
                        divCls = "pk-set-grass";
                        ulCls = "pk-list-grass";
                    } else if(es.displayName.contains("Headbutt Trees")) {
                        divCls = "pk-set-headbutt-trees";
                        ulCls = "pk-list-headbutt-trees";
                    } else if(es.displayName.contains("Bug Catching Contest")) {
                        divCls = "pk-set-bug-catching-contest";
                        ulCls = "pk-list-bug-catching-contest";
                    } else if(es.displayName.contains("Rock Smash")) {
                        divCls = "pk-set-rock-smash";
                        ulCls = "pk-list-rock-smash";
                    } else if(es.displayName.contains("Swarm/Radar/GBA")) {
                        divCls = "pk-set-poke-radar";
                        ulCls = "pk-list-poke-radar";
                    } else if(es.displayName.contains("Doubles Grass")) {
                        divCls = "pk-set-doubles-grass";
                        ulCls = "pk-list-doubles-grass";
                    } else if(es.displayName.contains("Shaking Spots")) {
                        divCls = "pk-set-shaking-spot";
                        ulCls = "pk-list-shaking-spot";
                    } 
                }
                log.print(String.format("<div class=\"wild-pk-set %s\">", divCls));
                log.print("Set #" + idx + " ");
                if (es.displayName != null) {
                    log.print("- " + es.displayName + " ");
                }
                log.print("(rate=" + es.rate + ")");
                log.print(String.format("</div><ul class=\"pk-set-list %s\">", ulCls));
                for (Encounter e : es.encounters) {
                    log.print("<li>");
                    log.print(e.pokemon.name + " <em>Lv");
                    if (e.maxLevel > 0 && e.maxLevel != e.level) {
                        log.print("s " + e.level + "-" + e.maxLevel);
                    } else {
                        log.print(e.level);
                    }
                    log.print("</em></li>");
                }
                log.println("</ul>");
            }
            log.println();
        }
    }

    private void maybeLogTrainerChanges(final PrintStream log, final RomHandler romHandler) {
        if (settings.getTrainersMod() == Settings.TrainersMod.UNCHANGED && !settings.isRivalCarriesStarterThroughout()) {
            log.println("<h2>Trainers</h2><p>Unchanged.</p>");
        } else {
            log.println("<h2>Trainers Pokemon</h2>");
            List<Trainer> trainers = romHandler.getTrainers();
            int idx = 0;
            for (Trainer t : trainers) {
                idx++;
                log.print("<div class=\"trainer-box\"><div class=\"trainer\">");
                log.printf("<span class=\"trainer-name\"><em># %d</em> ", idx);
                if (t.fullDisplayName != null) {
                    log.print(t.fullDisplayName + "</span>");
                } else if (t.name != null) {
                    log.print(t.name + "</span>");
                }
                if (t.offset != idx && t.offset != 0) {
                    log.printf("<em>@%X</em>", t.offset);
                }
                log.print("</div><em>Old Team</em><ul class=\"old-trainer-pk\">");
                for(TrainerPokemon tpk : originalTrainers.get(idx-1).pokemon) {
                    log.print(String.format("<li>%s <em>Lv%d</em></li>", tpk.pokemon.name, tpk.level));
                }
                log.print("</ul><em>New Team</em><ul class=\"new-trainer-pk\">");
                for (TrainerPokemon tpk : t.pokemon) {
                    log.print(String.format("<li>%s <em>Lv%d</em></li>", tpk.pokemon.name, tpk.level));
                }
                log.println("</ul></div>");
            }
            log.println("<div class=\"clear\"></div>");
        }
    }

    private int maybeChangeAndLogStaticPokemon(final PrintStream log, final RomHandler romHandler, boolean raceMode,
            int checkValue) {
        if (romHandler.canChangeStaticPokemon()) {
            List<Pokemon> oldStatics = romHandler.getStaticPokemon();
            if (settings.getStaticPokemonMod() == Settings.StaticPokemonMod.RANDOM_MATCHING) {
                romHandler.randomizeStaticPokemon(true);
            } else if (settings.getStaticPokemonMod() == Settings.StaticPokemonMod.COMPLETELY_RANDOM) {
                romHandler.randomizeStaticPokemon(false);
            }
            List<Pokemon> newStatics = romHandler.getStaticPokemon();
            if (settings.getStaticPokemonMod() == Settings.StaticPokemonMod.UNCHANGED) {
                log.println("<h2>Static Pokemon</h2><p>Unchanged.</p>");
            } else {
                log.println("<h2>Static Pokemon</h2>");
                log.println("<ul>");
                Map<Pokemon, Integer> seenPokemon = new TreeMap<Pokemon, Integer>();
                for (int i = 0; i < oldStatics.size(); i++) {
                    Pokemon oldP = oldStatics.get(i);
                    Pokemon newP = newStatics.get(i);
                    checkValue = addToCV(checkValue, newP.number);
                    log.print("<li>" + oldP.name);
                    if (seenPokemon.containsKey(oldP)) {
                        int amount = seenPokemon.get(oldP);
                        log.print("(" + (++amount) + ")");
                        seenPokemon.put(oldP, amount);
                    } else {
                        seenPokemon.put(oldP, 1);
                    }
                    log.println(" => " + newP.name + "</li>");
                }
                log.println("</ul>");
            }
        }
        return checkValue;
    }

    private void maybeLogMoveChanges(final PrintStream log, final RomHandler romHandler) {
        if (!settings.isRandomizeMoveAccuracies() && !settings.isRandomizeMovePowers()
                && !settings.isRandomizeMovePPs() && !settings.isRandomizeMoveCategory()
                && !settings.isRandomizeMoveTypes()) {
            if (!settings.isUpdateMoves()) {
                log.println("<h2>Move Data</h2><p>Unchanged.</p>");
            }
        } else {
            log.println("<h2>Move Data</h2>");
            log.println("<table class=\"moves-table\">");
            log.print("<tr><th>NUM</th><th>NAME</th><th>TYPE</th><th>POWER</th><th>ACC.</th><th>PP</th>");
            if (romHandler.hasPhysicalSpecialSplit()) {
                log.print("<th>CATEGORY</th>");
            }
            log.println("<th>BIG</th></tr>");
            List<Move> allMoves = romHandler.getMoves();
            for (Move mv : allMoves) {
                if (mv != null) {
                    String mvType = (mv.type == null) ? "???" : mv.type.toString();
                    boolean topMove = mv.power > 95;
                    mvType = String.format("<span class=\"pk-type %s\">%s</span>", mvType.toLowerCase(), mvType);
                    log.printf("<tr%s><td>%d</td><td class=\"left\">%s</td><td>%s</td><td>%d</td><td>%d</td><td>%d</td>",
                            mv.internalId % 2 == 0 ? " class=\"alt\"" : "", mv.internalId, mv.name, mvType, mv.power, (int) mv.hitratio, mv.pp);
                    if (romHandler.hasPhysicalSpecialSplit()) {
                        log.printf("<td>%s</td>", mv.category.toString());
                    }
                    log.printf("<td>%s</td>", topMove ? "YES" : "");
                    log.println("</tr>");
                }
            }
            log.println("</table>");
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