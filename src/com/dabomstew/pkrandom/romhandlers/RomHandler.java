package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  RomHandler.java - defines the functionality that each randomization   --*/
/*--                    handler must implement.                             --*/
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

import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.dabomstew.pkrandom.CustomNamesSet;
import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.pokemon.EncounterSet;
import com.dabomstew.pkrandom.pokemon.GenRestrictions;
import com.dabomstew.pkrandom.pokemon.IngameTrade;
import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.Move;
import com.dabomstew.pkrandom.pokemon.MoveLearnt;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.Type;

public interface RomHandler {

    public abstract class Factory {
        public RomHandler create(Random random) {
            return create(random, null);
        }

        public abstract RomHandler create(Random random, PrintStream log);

        public abstract boolean isLoadable(String filename);
    }

    // Basic load/save to filenames

    public boolean loadRom(String filename);

    public boolean saveRom(String filename);

    public String loadedFilename();

    // Log stuff

    public void setLog(PrintStream logStream);

    // Get a List of Pokemon objects in this game.
    // 0 = null 1-whatever = the Pokemon.
    public List<Pokemon> getPokemon();

    // Setup Gen Restrictions.
    public void setPokemonPool(GenRestrictions restrictions);

    public void removeEvosForPokemonPool();

    // Randomizer: Starters
    // Get starters, they should be ordered with Pokemon
    // following the one it is SE against.
    // E.g. Grass, Fire, Water or Fire, Water, Grass etc.
    public List<Pokemon> getStarters();

    // Change the starter data in the ROM.
    // Optionally also change the starter used by the rival in
    // the level 5 battle, if there is one.
    public boolean setStarters(List<Pokemon> newStarters);

    // Tells whether this ROM has the ability to have starters changed.
    // Was for before CUE's compressors were found and arm9 was untouchable.
    public boolean canChangeStarters();

    // Randomizer: Pokemon stats

    // Run the stats shuffler on each Pokemon.
    public void shufflePokemonStats(boolean evolutionSanity);

    // Randomise stats following evolutions for proportions or not (see
    // tooltips)
    public void randomizePokemonStats(boolean evolutionSanity);

    // Update base stats to gen6
    public void updatePokemonStats();

    // Give a random Pokemon who's in this game
    public Pokemon randomPokemon();

    // Give a random non-legendary Pokemon who's in this game
    // Business rules for who's legendary are in Pokemon class
    public Pokemon randomNonLegendaryPokemon();

    // Give a random legendary Pokemon who's in this game
    // Business rules for who's legendary are in Pokemon class
    public Pokemon randomLegendaryPokemon();

    // Give a random Pokemon who has 2 evolution stages
    // Should make a good starter Pokemon
    public Pokemon random2EvosPokemon();

    // Randomizer: types

    // return a random type valid in this game.
    // straightforward except for gen1 where dark&steel are excluded.
    public Type randomType();

    public boolean typeInGame(Type type);

    // randomise Pokemon types, with a switch on whether evolutions
    // should follow the same types or not.
    // some evolutions dont anyway, e.g. Eeveelutions, Hitmons
    public void randomizePokemonTypes(boolean evolutionSanity);

    // Randomizer: pokemon abilities
    public int abilitiesPerPokemon();

    public int highestAbilityIndex();

    public String abilityName(int number);

    public void randomizeAbilities(boolean evolutionSanity, boolean allowWonderGuard, boolean banTrappingAbilities,
            boolean banNegativeAbilities);

    // Randomizer: wild pokemon
    public List<EncounterSet> getEncounters(boolean useTimeOfDay);

    public void setEncounters(boolean useTimeOfDay, List<EncounterSet> encounters);

    public void randomEncounters(boolean useTimeOfDay, boolean catchEmAll, boolean typeThemed, boolean usePowerLevels,
            boolean noLegendaries);

    public void area1to1Encounters(boolean useTimeOfDay, boolean catchEmAll, boolean typeThemed,
            boolean usePowerLevels, boolean noLegendaries);

    public void game1to1Encounters(boolean useTimeOfDay, boolean usePowerLevels, boolean noLegendaries);

    public boolean hasTimeBasedEncounters();

    public List<Pokemon> bannedForWildEncounters();

    // Randomizer: trainer pokemon
    public List<Trainer> getTrainers();

    public void setTrainers(List<Trainer> trainerData);

    public void randomizeTrainerPokes(boolean usePowerLevels, boolean noLegendaries, boolean noEarlyWonderGuard,int levelModifier);

    public void typeThemeTrainerPokes(boolean usePowerLevels, boolean weightByFrequency, boolean noLegendaries,
            boolean noEarlyWonderGuard);

    public void rivalCarriesStarter();

    public void forceFullyEvolvedTrainerPokes(int minLevel);

    // Randomizer: moves

    public void randomizeMovePowers();

    public void randomizeMovePPs();

    public void randomizeMoveAccuracies();

    public void randomizeMoveTypes();

    public boolean hasPhysicalSpecialSplit();

    public void randomizeMoveCategory();

    // Update all moves to gen5 definitions as much as possible
    // e.g. change typing, power, accuracy, but don't try to
    // stuff around with effects.
    public void updateMovesToGen5();

    // same for gen6
    public void updateMovesToGen6();

    // stuff for printing move changes
    public void initMoveUpdates();

    public void printMoveUpdates();

    // return all the moves valid in this game.
    public List<Move> getMoves();

    // Randomizer: moves learnt

    public Map<Pokemon, List<MoveLearnt>> getMovesLearnt();

    public void setMovesLearnt(Map<Pokemon, List<MoveLearnt>> movesets);

    public List<Integer> getMovesBannedFromLevelup();

    public void randomizeMovesLearnt(boolean typeThemed, boolean noBroken, boolean forceFourStartingMoves,
            double goodDamagingProbability);

    public void orderDamagingMovesByDamage();

    public void metronomeOnlyMode();

    public boolean supportsFourStartingMoves();

    // Randomizer: static pokemon (except starters)

    public List<Pokemon> getStaticPokemon();

    public boolean setStaticPokemon(List<Pokemon> staticPokemon);

    public void randomizeStaticPokemon(boolean legendForLegend);

    public boolean canChangeStaticPokemon();

    public List<Pokemon> bannedForStaticPokemon();

    // Randomizer: TMs/HMs

    public List<Integer> getTMMoves();

    public List<Integer> getHMMoves();

    public void setTMMoves(List<Integer> moveIndexes);

    public void randomizeTMMoves(boolean noBroken, boolean preserveField, double goodDamagingProbability);

    public int getTMCount();

    public int getHMCount();

    /**
     * Get TM/HM compatibility data from this rom. The result should contain a
     * boolean array for each Pokemon indexed as such:
     * 
     * 0: blank (false) / 1 - (getTMCount()) : TM compatibility /
     * (getTMCount()+1) - (getTMCount()+getHMCount()) - HM compatibility
     * 
     * @return
     */

    public Map<Pokemon, boolean[]> getTMHMCompatibility();

    public void setTMHMCompatibility(Map<Pokemon, boolean[]> compatData);

    public void randomizeTMHMCompatibility(boolean preferSameType);

    public void fullTMHMCompatibility();

    // tm/moveset sanity

    public void ensureTMCompatSanity();

    // new 170: full HM (but not TM) compat override

    public void fullHMCompatibility();

    // Randomizer: move tutors

    public boolean hasMoveTutors();

    public List<Integer> getMoveTutorMoves();

    public void setMoveTutorMoves(List<Integer> moves);

    public void randomizeMoveTutorMoves(boolean noBroken, boolean preserveField, double goodDamagingProbability);

    public Map<Pokemon, boolean[]> getMoveTutorCompatibility();

    public void setMoveTutorCompatibility(Map<Pokemon, boolean[]> compatData);

    public void randomizeMoveTutorCompatibility(boolean preferSameType);

    public void fullMoveTutorCompatibility();

    // mt/moveset sanity

    public void ensureMoveTutorCompatSanity();

    // Randomizer: trainer names

    public boolean canChangeTrainerText();

    public List<String> getTrainerNames();

    public void setTrainerNames(List<String> trainerNames);

    public enum TrainerNameMode {
        SAME_LENGTH, MAX_LENGTH, MAX_LENGTH_WITH_CLASS
    };

    public TrainerNameMode trainerNameMode();

    // Returns this with or without the class
    public int maxTrainerNameLength();

    // Only relevant for gen2, which has fluid trainer name length but
    // only a certain amount of space in the ROM bank.
    public int maxSumOfTrainerNameLengths();

    // Only needed if above mode is "MAX LENGTH WITH CLASS"
    public List<Integer> getTCNameLengthsByTrainer();

    public void randomizeTrainerNames(CustomNamesSet customNames);

    // Randomizer: trainer class names

    public List<String> getTrainerClassNames();

    public void setTrainerClassNames(List<String> trainerClassNames);

    public boolean fixedTrainerClassNamesLength();

    public int maxTrainerClassNameLength();

    public void randomizeTrainerClassNames(CustomNamesSet customNames);

    public List<Integer> getDoublesTrainerClasses();

    // Items

    public ItemList getAllowedItems();

    public ItemList getNonBadItems();

    public void randomizeWildHeldItems(boolean banBadItems);

    public String[] getItemNames();

    public List<Integer> getStarterHeldItems();

    public void setStarterHeldItems(List<Integer> items);

    public void randomizeStarterHeldItems(boolean banBadItems);

    // Field Items

    // TMs on the field

    public List<Integer> getRequiredFieldTMs();

    public List<Integer> getCurrentFieldTMs();

    public void setFieldTMs(List<Integer> fieldTMs);

    // Everything else

    public List<Integer> getRegularFieldItems();

    public void setRegularFieldItems(List<Integer> items);

    // Randomizer methods

    public void shuffleFieldItems();

    public void randomizeFieldItems(boolean banBadItems);

    // Trades

    public List<IngameTrade> getIngameTrades();

    public void setIngameTrades(List<IngameTrade> trades);

    public void randomizeIngameTrades(boolean randomizeRequest, boolean randomNickname, boolean randomOT,
            boolean randomStats, boolean randomItem, CustomNamesSet customNames);

    public boolean hasDVs();

    public int maxTradeNicknameLength();

    public int maxTradeOTNameLength();

    // Evos

    public void removeTradeEvolutions(boolean changeMoveEvos);

    public void condenseLevelEvolutions(int maxLevel, int maxIntermediateLevel);

    public void randomizeEvolutions(boolean similarStrength, boolean sameType, boolean limitToThreeStages,
            boolean forceChange);

    // stats stuff
    public void minimumCatchRate(int rateNonLegendary, int rateLegendary);

    public void standardizeEXPCurves();

    // (Mostly) unchanging lists of moves

    public List<Integer> getGameBreakingMoves();

    // includes game or gen-specific moves like Secret Power
    // but NOT healing moves (Softboiled, Milk Drink)
    public List<Integer> getFieldMoves();

    // any HMs required to obtain 4 badges
    // (excluding Gameshark codes or early drink in RBY)
    public List<Integer> getEarlyRequiredHMMoves();

    // Misc

    public boolean isYellow();

    public String getROMName();

    public String getROMCode();

    public String getSupportLevel();

    public String getDefaultExtension();

    public int internalStringLength(String string);

    public void applySignature();

    public BufferedImage getMascotImage();

    public boolean isROMHack();

    public int generationOfPokemon();

    public void writeCheckValueToROM(int value);

    // code tweaks

    public int miscTweaksAvailable();

    public void applyMiscTweak(MiscTweak tweak);

}
