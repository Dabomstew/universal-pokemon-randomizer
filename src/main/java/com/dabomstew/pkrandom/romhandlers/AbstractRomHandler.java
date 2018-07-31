package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  AbstractRomHandler.java - a base class for all rom handlers which     --*/
/*--                            implements the majority of the actual       --*/
/*--                            randomizer logic by building on the base    --*/
/*--                            getters & setters provided by each concrete --*/
/*--                            handler.                                    --*/
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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import com.dabomstew.pkrandom.CustomNamesSet;
import com.dabomstew.pkrandom.MiscTweak;
import com.dabomstew.pkrandom.RomFunctions;
import com.dabomstew.pkrandom.constants.GlobalConstants;
import com.dabomstew.pkrandom.exceptions.RandomizationException;
import com.dabomstew.pkrandom.pokemon.Encounter;
import com.dabomstew.pkrandom.pokemon.EncounterSet;
import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.EvolutionType;
import com.dabomstew.pkrandom.pokemon.ExpCurve;
import com.dabomstew.pkrandom.pokemon.GenRestrictions;
import com.dabomstew.pkrandom.pokemon.IngameTrade;
import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.Move;
import com.dabomstew.pkrandom.pokemon.MoveCategory;
import com.dabomstew.pkrandom.pokemon.MoveLearnt;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.TrainerPokemon;
import com.dabomstew.pkrandom.pokemon.Type;

public abstract class AbstractRomHandler implements RomHandler {

    private boolean restrictionsSet;
    protected List<Pokemon> mainPokemonList;
    protected List<Pokemon> noLegendaryList, onlyLegendaryList;
    protected final Random random;
    protected PrintStream logStream;

    /* Constructor */

    public AbstractRomHandler(Random random, PrintStream logStream) {
        this.random = random;
        this.logStream = logStream;
    }

    /*
     * Public Methods, implemented here for all gens. Unlikely to be overridden.
     */

    public void setLog(PrintStream logStream) {
        this.logStream = logStream;
    }

    public void setPokemonPool(GenRestrictions restrictions) {
        restrictionsSet = true;
        mainPokemonList = this.allPokemonWithoutNull();
        if (restrictions != null) {
            mainPokemonList = new ArrayList<Pokemon>();
            List<Pokemon> allPokemon = this.getPokemon();

            if (restrictions.allow_gen1) {
                addPokesFromRange(mainPokemonList, allPokemon, 1, 151);
                if (restrictions.assoc_g1_g2 && allPokemon.size() > 251) {
                    addEvosFromRange(mainPokemonList, 1, 151, 152, 251);
                }
                if (restrictions.assoc_g1_g4 && allPokemon.size() > 493) {
                    addEvosFromRange(mainPokemonList, 1, 151, 387, 493);
                }
            }

            if (restrictions.allow_gen2 && allPokemon.size() > 251) {
                addPokesFromRange(mainPokemonList, allPokemon, 152, 251);
                if (restrictions.assoc_g2_g1) {
                    addEvosFromRange(mainPokemonList, 152, 251, 1, 151);
                }
                if (restrictions.assoc_g2_g3 && allPokemon.size() > 386) {
                    addEvosFromRange(mainPokemonList, 152, 251, 252, 386);
                }
                if (restrictions.assoc_g2_g4 && allPokemon.size() > 493) {
                    addEvosFromRange(mainPokemonList, 152, 251, 387, 493);
                }
            }

            if (restrictions.allow_gen3 && allPokemon.size() > 386) {
                addPokesFromRange(mainPokemonList, allPokemon, 252, 386);
                if (restrictions.assoc_g3_g2) {
                    addEvosFromRange(mainPokemonList, 252, 386, 152, 251);
                }
                if (restrictions.assoc_g3_g4 && allPokemon.size() > 493) {
                    addEvosFromRange(mainPokemonList, 252, 386, 387, 493);
                }
            }

            if (restrictions.allow_gen4 && allPokemon.size() > 493) {
                addPokesFromRange(mainPokemonList, allPokemon, 387, 493);
                if (restrictions.assoc_g4_g1) {
                    addEvosFromRange(mainPokemonList, 387, 493, 1, 151);
                }
                if (restrictions.assoc_g4_g2) {
                    addEvosFromRange(mainPokemonList, 387, 493, 152, 251);
                }
                if (restrictions.assoc_g4_g3) {
                    addEvosFromRange(mainPokemonList, 387, 493, 252, 386);
                }
            }

            if (restrictions.allow_gen5 && allPokemon.size() > 649) {
                addPokesFromRange(mainPokemonList, allPokemon, 494, 649);
            }
        }

        noLegendaryList = new ArrayList<Pokemon>();
        onlyLegendaryList = new ArrayList<Pokemon>();

        for (Pokemon p : mainPokemonList) {
            if (p.isLegendary()) {
                onlyLegendaryList.add(p);
            } else {
                noLegendaryList.add(p);
            }
        }
    }

    private void addPokesFromRange(List<Pokemon> pokemonPool, List<Pokemon> allPokemon, int range_min, int range_max) {
        for (int i = range_min; i <= range_max; i++) {
            if (!pokemonPool.contains(allPokemon.get(i))) {
                pokemonPool.add(allPokemon.get(i));
            }
        }
    }

    private void addEvosFromRange(List<Pokemon> pokemonPool, int first_min, int first_max, int second_min,
            int second_max) {
        Set<Pokemon> newPokemon = new TreeSet<Pokemon>();
        for (Pokemon pk : pokemonPool) {
            if (pk.number >= first_min && pk.number <= first_max) {
                for (Evolution ev : pk.evolutionsFrom) {
                    if (ev.to.number >= second_min && ev.to.number <= second_max) {
                        if (!pokemonPool.contains(ev.to) && !newPokemon.contains(ev.to)) {
                            newPokemon.add(ev.to);
                        }
                    }
                }

                for (Evolution ev : pk.evolutionsTo) {
                    if (ev.from.number >= second_min && ev.from.number <= second_max) {
                        if (!pokemonPool.contains(ev.from) && !newPokemon.contains(ev.from)) {
                            newPokemon.add(ev.from);
                        }
                    }
                }
            }
        }

        pokemonPool.addAll(newPokemon);
    }

    @Override
    public void shufflePokemonStats(boolean evolutionSanity) {
        if (evolutionSanity) {
            copyUpEvolutionsHelper(new BasePokemonAction() {
                public void applyTo(Pokemon pk) {
                    pk.shuffleStats(AbstractRomHandler.this.random);
                }
            }, new EvolvedPokemonAction() {
                public void applyTo(Pokemon evFrom, Pokemon evTo, boolean toMonIsFinalEvo) {
                    evTo.copyShuffledStatsUpEvolution(evFrom);
                }
            });
        } else {
            List<Pokemon> allPokes = this.getPokemon();
            for (Pokemon pk : allPokes) {
                if (pk != null) {
                    pk.shuffleStats(this.random);
                }
            }
        }
    }

    @Override
    public void randomizePokemonStats(boolean evolutionSanity) {

        if (evolutionSanity) {
            copyUpEvolutionsHelper(new BasePokemonAction() {
                public void applyTo(Pokemon pk) {
                    pk.randomizeStatsWithinBST(AbstractRomHandler.this.random);
                }
            }, new EvolvedPokemonAction() {
                public void applyTo(Pokemon evFrom, Pokemon evTo, boolean toMonIsFinalEvo) {
                    evTo.copyRandomizedStatsUpEvolution(evFrom);
                }
            });
        } else {
            List<Pokemon> allPokes = this.getPokemon();
            for (Pokemon pk : allPokes) {
                if (pk != null) {
                    pk.randomizeStatsWithinBST(this.random);
                }
            }
        }

    }

    @Override
    public void updatePokemonStats() {
        List<Pokemon> pokes = getPokemon();

        // non-special stat gen1 pokemon
        pokes.get(15).attack = 90; // BEEDRILL
        pokes.get(18).speed = 101; // PIDGEOT
        pokes.get(25).defense = 40; // PIKACHU
        pokes.get(26).speed = 110; // RAICHU
        pokes.get(31).attack = 92; // NIDOQUEEN
        pokes.get(34).attack = 102; // NIDOKING
        pokes.get(62).attack = 95; // POLIWRATH
        pokes.get(76).attack = 120; // GOLEM

        // behavior regarding special stat changes
        // depending on whether this is actually gen1 or not.
        if (generationOfPokemon() == 1) {
            // only update the pokemon who's updated stat was
            // equal to their Gen1 special stat.

            pokes.get(12).special = 90; // BUTTERFREE
            // skip PIKACHU s.def
            pokes.get(36).special = 95; // CLEFABLE
            // skip WIGGLYTUFF s.atk
            pokes.get(45).special = 110; // VILEPLUME
            // skip ALAKAZAM s.def
            // skip VICTREEBEL s.def
        } else {
            // do the special stat changes then move on from gen2 onwards

            pokes.get(12).spatk = 90; // BUTTERFREE
            pokes.get(25).spdef = 50; // PIKACHU
            pokes.get(36).spatk = 95; // CLEFABLE
            pokes.get(40).spatk = 85; // WIGGLYTUFF
            pokes.get(45).spatk = 110; // VILEPLUME
            pokes.get(65).spdef = 95; // ALAKAZAM
            pokes.get(71).spdef = 70; // VICTREEBEL

            // gen 2
            pokes.get(181).defense = 85; // AMPHAROS
            pokes.get(182).defense = 95; // BELLOSSOM
            pokes.get(184).spatk = 60; // AZUMARILL
            pokes.get(189).spdef = 95; // JUMPLUFF

            // gen 3
            if (generationOfPokemon() >= 3) {
                pokes.get(267).spatk = 100; // BEAUTIFLY
                pokes.get(295).spdef = 73; // EXPLOUD
            }

            // gen 4
            if (generationOfPokemon() >= 4) {
                pokes.get(398).spdef = 60; // STARAPTOR
                pokes.get(407).defense = 65; // ROSERADE
            }

            // gen 5
            if (generationOfPokemon() >= 5) {
                pokes.get(508).attack = 110; // STOUTLAND
                pokes.get(521).attack = 115; // UNFEZANT
                pokes.get(526).spdef = 80; // GIGALITH
                pokes.get(537).attack = 95; // SEISMITOAD
                pokes.get(542).spdef = 80; // LEAVANNY
                pokes.get(545).attack = 100; // SCOLIPEDE
                pokes.get(553).defense = 80; // KROOKODILE
            }
        }
    }

    public Pokemon randomPokemon() {
        checkPokemonRestrictions();
        return mainPokemonList.get(this.random.nextInt(mainPokemonList.size()));
    }

    @Override
    public Pokemon randomNonLegendaryPokemon() {
        checkPokemonRestrictions();
        return noLegendaryList.get(this.random.nextInt(noLegendaryList.size()));
    }

    @Override
    public Pokemon randomLegendaryPokemon() {
        checkPokemonRestrictions();
        return onlyLegendaryList.get(this.random.nextInt(onlyLegendaryList.size()));
    }

    private List<Pokemon> twoEvoPokes;

    @Override
    public Pokemon random2EvosPokemon() {
        if (twoEvoPokes == null) {
            // Prepare the list
            twoEvoPokes = new ArrayList<Pokemon>();
            List<Pokemon> allPokes = this.getPokemon();
            for (Pokemon pk : allPokes) {
                if (pk != null && pk.evolutionsTo.size() == 0 && pk.evolutionsFrom.size() > 0) {
                    // Potential candidate
                    for (Evolution ev : pk.evolutionsFrom) {
                        // If any of the targets here evolve, the original
                        // Pokemon has 2+ stages.
                        if (ev.to.evolutionsFrom.size() > 0) {
                            twoEvoPokes.add(pk);
                            break;
                        }
                    }
                }
            }
        }
        return twoEvoPokes.get(this.random.nextInt(twoEvoPokes.size()));
    }

    @Override
    public Type randomType() {
        Type t = Type.randomType(this.random);
        while (!typeInGame(t)) {
            t = Type.randomType(this.random);
        }
        return t;
    }

    @Override
    public void randomizePokemonTypes(boolean evolutionSanity) {
        List<Pokemon> allPokes = this.getPokemon();
        if (evolutionSanity) {
            // Type randomization with evolution sanity
            copyUpEvolutionsHelper(new BasePokemonAction() {
                public void applyTo(Pokemon pk) {
                    // Step 1: Basic or Excluded From Copying Pokemon
                    // A Basic/EFC pokemon has a 35% chance of a second type if
                    // it has an evolution that copies type/stats, a 50% chance
                    // otherwise
                    pk.primaryType = randomType();
                    pk.secondaryType = null;
                    if (pk.evolutionsFrom.size() == 1 && pk.evolutionsFrom.get(0).carryStats) {
                        if (AbstractRomHandler.this.random.nextDouble() < 0.35) {
                            pk.secondaryType = randomType();
                            while (pk.secondaryType == pk.primaryType) {
                                pk.secondaryType = randomType();
                            }
                        }
                    } else {
                        if (AbstractRomHandler.this.random.nextDouble() < 0.5) {
                            pk.secondaryType = randomType();
                            while (pk.secondaryType == pk.primaryType) {
                                pk.secondaryType = randomType();
                            }
                        }
                    }
                }
            }, new EvolvedPokemonAction() {
                public void applyTo(Pokemon evFrom, Pokemon evTo, boolean toMonIsFinalEvo) {
                    evTo.primaryType = evFrom.primaryType;
                    evTo.secondaryType = evFrom.secondaryType;

                    if (evTo.secondaryType == null) {
                        double chance = toMonIsFinalEvo ? 0.25 : 0.15;
                        if (AbstractRomHandler.this.random.nextDouble() < chance) {
                            evTo.secondaryType = randomType();
                            while (evTo.secondaryType == evTo.primaryType) {
                                evTo.secondaryType = randomType();
                            }
                        }
                    }
                }
            });
        } else {
            // Entirely random types
            for (Pokemon pkmn : allPokes) {
                if (pkmn != null) {
                    pkmn.primaryType = randomType();
                    pkmn.secondaryType = null;
                    if (this.random.nextDouble() < 0.5) {
                        pkmn.secondaryType = randomType();
                        while (pkmn.secondaryType == pkmn.primaryType) {
                            pkmn.secondaryType = randomType();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void randomizeAbilities(boolean evolutionSanity, boolean allowWonderGuard, boolean banTrappingAbilities,
            boolean banNegativeAbilities) {
        // Abilities don't exist in some games...
        if (this.abilitiesPerPokemon() == 0) {
            return;
        }

        final boolean hasDWAbilities = (this.abilitiesPerPokemon() == 3);

        final List<Integer> bannedAbilities = new ArrayList<Integer>();

        if (!allowWonderGuard) {
            bannedAbilities.add(GlobalConstants.WONDER_GUARD_INDEX);
        }

        if (banTrappingAbilities) {
            bannedAbilities.addAll(GlobalConstants.battleTrappingAbilities);
        }

        if (banNegativeAbilities) {
            bannedAbilities.addAll(GlobalConstants.negativeAbilities);
        }

        final int maxAbility = this.highestAbilityIndex();

        if (evolutionSanity) {
            // copy abilities straight up evolution lines
            // still keep WG as an exception, though

            copyUpEvolutionsHelper(new BasePokemonAction() {
                public void applyTo(Pokemon pk) {
                    if (pk.ability1 != GlobalConstants.WONDER_GUARD_INDEX
                            && pk.ability2 != GlobalConstants.WONDER_GUARD_INDEX
                            && pk.ability3 != GlobalConstants.WONDER_GUARD_INDEX) {
                        // Pick first ability
                        pk.ability1 = pickRandomAbility(maxAbility, bannedAbilities);

                        // Second ability?
                        if (AbstractRomHandler.this.random.nextDouble() < 0.5) {
                            // Yes, second ability
                            pk.ability2 = pickRandomAbility(maxAbility, bannedAbilities, pk.ability1);
                        } else {
                            // Nope
                            pk.ability2 = 0;
                        }

                        // Third ability?
                        if (hasDWAbilities) {
                            pk.ability3 = pickRandomAbility(maxAbility, bannedAbilities, pk.ability1, pk.ability2);
                        }
                    }
                }
            }, new EvolvedPokemonAction() {
                public void applyTo(Pokemon evFrom, Pokemon evTo, boolean toMonIsFinalEvo) {
                    if (evTo.ability1 != GlobalConstants.WONDER_GUARD_INDEX
                            && evTo.ability2 != GlobalConstants.WONDER_GUARD_INDEX
                            && evTo.ability3 != GlobalConstants.WONDER_GUARD_INDEX) {
                        evTo.ability1 = evFrom.ability1;
                        evTo.ability2 = evFrom.ability2;
                        evTo.ability3 = evFrom.ability3;
                    }
                }
            });
        }

        else {
            List<Pokemon> allPokes = this.getPokemon();
            for (Pokemon pk : allPokes) {
                if (pk == null) {
                    continue;
                }

                // Don't remove WG if already in place.
                if (pk.ability1 != GlobalConstants.WONDER_GUARD_INDEX
                        && pk.ability2 != GlobalConstants.WONDER_GUARD_INDEX
                        && pk.ability3 != GlobalConstants.WONDER_GUARD_INDEX) {
                    // Pick first ability
                    pk.ability1 = this.pickRandomAbility(maxAbility, bannedAbilities);

                    // Second ability?
                    if (this.random.nextDouble() < 0.5) {
                        // Yes, second ability
                        pk.ability2 = this.pickRandomAbility(maxAbility, bannedAbilities, pk.ability1);
                    } else {
                        // Nope
                        pk.ability2 = 0;
                    }

                    // Third ability?
                    if (hasDWAbilities) {
                        pk.ability3 = pickRandomAbility(maxAbility, bannedAbilities, pk.ability1, pk.ability2);
                    }
                }
            }
        }
    }

    private int pickRandomAbility(int maxAbility, List<Integer> bannedAbilities, int... alreadySetAbilities) {
        int newAbility = 0;

        while (true) {
            newAbility = this.random.nextInt(maxAbility) + 1;

            if (bannedAbilities.contains(newAbility)) {
                continue;
            }

            boolean repeat = false;
            for (int i = 0; i < alreadySetAbilities.length; i++) {
                if (alreadySetAbilities[i] == newAbility) {
                    repeat = true;
                    break;
                }
            }

            if (repeat) {
                continue;
            } else {
                break;
            }
        }

        return newAbility;
    }

    @Override
    public void randomEncounters(boolean useTimeOfDay, boolean catchEmAll, boolean typeThemed, boolean usePowerLevels,
            boolean noLegendaries) {
        checkPokemonRestrictions();
        List<EncounterSet> currentEncounters = this.getEncounters(useTimeOfDay);

        // New: randomize the order encounter sets are randomized in.
        // Leads to less predictable results for various modifiers.
        // Need to keep the original ordering around for saving though.
        List<EncounterSet> scrambledEncounters = new ArrayList<EncounterSet>(currentEncounters);
        Collections.shuffle(scrambledEncounters, this.random);

        List<Pokemon> banned = this.bannedForWildEncounters();
        // Assume EITHER catch em all OR type themed OR match strength for now
        if (catchEmAll) {

            List<Pokemon> allPokes = noLegendaries ? new ArrayList<Pokemon>(noLegendaryList) : new ArrayList<Pokemon>(
                    mainPokemonList);
            allPokes.removeAll(banned);
            for (EncounterSet area : scrambledEncounters) {
                List<Pokemon> pickablePokemon = allPokes;
                if (area.bannedPokemon.size() > 0) {
                    pickablePokemon = new ArrayList<Pokemon>(allPokes);
                    pickablePokemon.removeAll(area.bannedPokemon);
                }
                for (Encounter enc : area.encounters) {
                    // Pick a random pokemon
                    if (pickablePokemon.size() == 0) {
                        // Only banned pokes are left, ignore them and pick
                        // something else for now.
                        List<Pokemon> tempPickable = noLegendaries ? new ArrayList<Pokemon>(noLegendaryList)
                                : new ArrayList<Pokemon>(mainPokemonList);
                        tempPickable.removeAll(banned);
                        tempPickable.removeAll(area.bannedPokemon);
                        if (tempPickable.size() == 0) {
                            throw new RandomizationException("ERROR: Couldn't replace a wild Pokemon!");
                        }
                        int picked = this.random.nextInt(tempPickable.size());
                        enc.pokemon = tempPickable.get(picked);
                    } else {
                        // Picked this Pokemon, remove it
                        int picked = this.random.nextInt(pickablePokemon.size());
                        enc.pokemon = pickablePokemon.get(picked);
                        pickablePokemon.remove(picked);
                        if (allPokes != pickablePokemon) {
                            allPokes.remove(enc.pokemon);
                        }
                        if (allPokes.size() == 0) {
                            // Start again
                            allPokes.addAll(noLegendaries ? noLegendaryList : mainPokemonList);
                            allPokes.removeAll(banned);
                            if (pickablePokemon != allPokes) {
                                pickablePokemon.addAll(allPokes);
                                pickablePokemon.removeAll(area.bannedPokemon);
                            }
                        }
                    }
                }
            }
        } else if (typeThemed) {
            Map<Type, List<Pokemon>> cachedPokeLists = new TreeMap<Type, List<Pokemon>>();
            for (EncounterSet area : scrambledEncounters) {
                List<Pokemon> possiblePokemon = null;
                int iterLoops = 0;
                while (possiblePokemon == null && iterLoops < 10000) {
                    Type areaTheme = randomType();
                    if (!cachedPokeLists.containsKey(areaTheme)) {
                        List<Pokemon> pType = pokemonOfType(areaTheme, noLegendaries);
                        pType.removeAll(banned);
                        cachedPokeLists.put(areaTheme, pType);
                    }
                    possiblePokemon = cachedPokeLists.get(areaTheme);
                    if (area.bannedPokemon.size() > 0) {
                        possiblePokemon = new ArrayList<Pokemon>(possiblePokemon);
                        possiblePokemon.removeAll(area.bannedPokemon);
                    }
                    if (possiblePokemon.size() == 0) {
                        // Can't use this type for this area
                        possiblePokemon = null;
                    }
                    iterLoops++;
                }
                if (possiblePokemon == null) {
                    throw new RandomizationException("Could not randomize an area in a reasonable amount of attempts.");
                }
                for (Encounter enc : area.encounters) {
                    // Pick a random themed pokemon
                    enc.pokemon = possiblePokemon.get(this.random.nextInt(possiblePokemon.size()));
                }
            }
        } else if (usePowerLevels) {
            List<Pokemon> allowedPokes = noLegendaries ? new ArrayList<Pokemon>(noLegendaryList)
                    : new ArrayList<Pokemon>(mainPokemonList);
            allowedPokes.removeAll(banned);
            for (EncounterSet area : scrambledEncounters) {
                List<Pokemon> localAllowed = allowedPokes;
                if (area.bannedPokemon.size() > 0) {
                    localAllowed = new ArrayList<Pokemon>(allowedPokes);
                    localAllowed.removeAll(area.bannedPokemon);
                }
                for (Encounter enc : area.encounters) {
                    enc.pokemon = pickWildPowerLvlReplacement(localAllowed, enc.pokemon, false, null);
                }
            }
        } else {
            // Entirely random
            for (EncounterSet area : scrambledEncounters) {
                for (Encounter enc : area.encounters) {
                    enc.pokemon = noLegendaries ? randomNonLegendaryPokemon() : randomPokemon();
                    while (banned.contains(enc.pokemon) || area.bannedPokemon.contains(enc.pokemon)) {
                        enc.pokemon = noLegendaries ? randomNonLegendaryPokemon() : randomPokemon();
                    }
                }
            }
        }

        setEncounters(useTimeOfDay, currentEncounters);
    }

    @Override
    public void area1to1Encounters(boolean useTimeOfDay, boolean catchEmAll, boolean typeThemed,
            boolean usePowerLevels, boolean noLegendaries) {
        checkPokemonRestrictions();
        List<EncounterSet> currentEncounters = this.getEncounters(useTimeOfDay);
        List<Pokemon> banned = this.bannedForWildEncounters();

        // New: randomize the order encounter sets are randomized in.
        // Leads to less predictable results for various modifiers.
        // Need to keep the original ordering around for saving though.
        List<EncounterSet> scrambledEncounters = new ArrayList<EncounterSet>(currentEncounters);
        Collections.shuffle(scrambledEncounters, this.random);

        // Assume EITHER catch em all OR type themed for now
        if (catchEmAll) {
            List<Pokemon> allPokes = noLegendaries ? new ArrayList<Pokemon>(noLegendaryList) : new ArrayList<Pokemon>(
                    mainPokemonList);
            allPokes.removeAll(banned);
            for (EncounterSet area : scrambledEncounters) {
                // Poke-set
                Set<Pokemon> inArea = pokemonInArea(area);
                // Build area map using catch em all
                Map<Pokemon, Pokemon> areaMap = new TreeMap<Pokemon, Pokemon>();
                List<Pokemon> pickablePokemon = allPokes;
                if (area.bannedPokemon.size() > 0) {
                    pickablePokemon = new ArrayList<Pokemon>(allPokes);
                    pickablePokemon.removeAll(area.bannedPokemon);
                }
                for (Pokemon areaPk : inArea) {
                    if (pickablePokemon.size() == 0) {
                        // No more pickable pokes left, take a random one
                        List<Pokemon> tempPickable = noLegendaries ? new ArrayList<Pokemon>(noLegendaryList)
                                : new ArrayList<Pokemon>(mainPokemonList);
                        tempPickable.removeAll(banned);
                        tempPickable.removeAll(area.bannedPokemon);
                        if (tempPickable.size() == 0) {
                            throw new RandomizationException("ERROR: Couldn't replace a wild Pokemon!");
                        }
                        int picked = this.random.nextInt(tempPickable.size());
                        Pokemon pickedMN = tempPickable.get(picked);
                        areaMap.put(areaPk, pickedMN);
                    } else {
                        int picked = this.random.nextInt(allPokes.size());
                        Pokemon pickedMN = allPokes.get(picked);
                        areaMap.put(areaPk, pickedMN);
                        pickablePokemon.remove(pickedMN);
                        if (allPokes != pickablePokemon) {
                            allPokes.remove(pickedMN);
                        }
                        if (allPokes.size() == 0) {
                            // Start again
                            allPokes.addAll(noLegendaries ? noLegendaryList : mainPokemonList);
                            allPokes.removeAll(banned);
                            if (pickablePokemon != allPokes) {
                                pickablePokemon.addAll(allPokes);
                                pickablePokemon.removeAll(area.bannedPokemon);
                            }
                        }
                    }
                }
                for (Encounter enc : area.encounters) {
                    // Apply the map
                    enc.pokemon = areaMap.get(enc.pokemon);
                }
            }
        } else if (typeThemed) {
            Map<Type, List<Pokemon>> cachedPokeLists = new TreeMap<Type, List<Pokemon>>();
            for (EncounterSet area : scrambledEncounters) {
                // Poke-set
                Set<Pokemon> inArea = pokemonInArea(area);
                List<Pokemon> possiblePokemon = null;
                int iterLoops = 0;
                while (possiblePokemon == null && iterLoops < 10000) {
                    Type areaTheme = randomType();
                    if (!cachedPokeLists.containsKey(areaTheme)) {
                        List<Pokemon> pType = pokemonOfType(areaTheme, noLegendaries);
                        pType.removeAll(banned);
                        cachedPokeLists.put(areaTheme, pType);
                    }
                    possiblePokemon = new ArrayList<Pokemon>(cachedPokeLists.get(areaTheme));
                    if (area.bannedPokemon.size() > 0) {
                        possiblePokemon.removeAll(area.bannedPokemon);
                    }
                    if (possiblePokemon.size() < inArea.size()) {
                        // Can't use this type for this area
                        possiblePokemon = null;
                    }
                    iterLoops++;
                }
                if (possiblePokemon == null) {
                    throw new RandomizationException("Could not randomize an area in a reasonable amount of attempts.");
                }

                // Build area map using type theme.
                Map<Pokemon, Pokemon> areaMap = new TreeMap<Pokemon, Pokemon>();
                for (Pokemon areaPk : inArea) {
                    int picked = this.random.nextInt(possiblePokemon.size());
                    Pokemon pickedMN = possiblePokemon.get(picked);
                    areaMap.put(areaPk, pickedMN);
                    possiblePokemon.remove(picked);
                }
                for (Encounter enc : area.encounters) {
                    // Apply the map
                    enc.pokemon = areaMap.get(enc.pokemon);
                }
            }
        } else if (usePowerLevels) {
            List<Pokemon> allowedPokes = noLegendaries ? new ArrayList<Pokemon>(noLegendaryList)
                    : new ArrayList<Pokemon>(mainPokemonList);
            allowedPokes.removeAll(banned);
            for (EncounterSet area : scrambledEncounters) {
                // Poke-set
                Set<Pokemon> inArea = pokemonInArea(area);
                // Build area map using randoms
                Map<Pokemon, Pokemon> areaMap = new TreeMap<Pokemon, Pokemon>();
                List<Pokemon> usedPks = new ArrayList<Pokemon>();
                List<Pokemon> localAllowed = allowedPokes;
                if (area.bannedPokemon.size() > 0) {
                    localAllowed = new ArrayList<Pokemon>(allowedPokes);
                    localAllowed.removeAll(area.bannedPokemon);
                }
                for (Pokemon areaPk : inArea) {
                    Pokemon picked = pickWildPowerLvlReplacement(localAllowed, areaPk, false, usedPks);
                    areaMap.put(areaPk, picked);
                    usedPks.add(picked);
                }
                for (Encounter enc : area.encounters) {
                    // Apply the map
                    enc.pokemon = areaMap.get(enc.pokemon);
                }
            }
        } else {
            // Entirely random
            for (EncounterSet area : scrambledEncounters) {
                // Poke-set
                Set<Pokemon> inArea = pokemonInArea(area);
                // Build area map using randoms
                Map<Pokemon, Pokemon> areaMap = new TreeMap<Pokemon, Pokemon>();
                for (Pokemon areaPk : inArea) {
                    Pokemon picked = noLegendaries ? randomNonLegendaryPokemon() : randomPokemon();
                    while (areaMap.containsValue(picked) || banned.contains(picked)
                            || area.bannedPokemon.contains(picked)) {
                        picked = noLegendaries ? randomNonLegendaryPokemon() : randomPokemon();
                    }
                    areaMap.put(areaPk, picked);
                }
                for (Encounter enc : area.encounters) {
                    // Apply the map
                    enc.pokemon = areaMap.get(enc.pokemon);
                }
            }
        }

        setEncounters(useTimeOfDay, currentEncounters);

    }

    @Override
    public void game1to1Encounters(boolean useTimeOfDay, boolean usePowerLevels, boolean noLegendaries) {
        checkPokemonRestrictions();
        // Build the full 1-to-1 map
        Map<Pokemon, Pokemon> translateMap = new TreeMap<Pokemon, Pokemon>();
        List<Pokemon> remainingLeft = allPokemonWithoutNull();
        List<Pokemon> remainingRight = noLegendaries ? new ArrayList<Pokemon>(noLegendaryList)
                : new ArrayList<Pokemon>(mainPokemonList);
        List<Pokemon> banned = this.bannedForWildEncounters();
        // Banned pokemon should be mapped to themselves
        for (Pokemon bannedPK : banned) {
            translateMap.put(bannedPK, bannedPK);
            remainingLeft.remove(bannedPK);
            remainingRight.remove(bannedPK);
        }
        while (remainingLeft.isEmpty() == false) {
            if (usePowerLevels) {
                int pickedLeft = this.random.nextInt(remainingLeft.size());
                Pokemon pickedLeftP = remainingLeft.remove(pickedLeft);
                Pokemon pickedRightP = null;
                if (remainingRight.size() == 1) {
                    // pick this (it may or may not be the same poke)
                    pickedRightP = remainingRight.get(0);
                } else {
                    // pick on power level with the current one blocked
                    pickedRightP = pickWildPowerLvlReplacement(remainingRight, pickedLeftP, true, null);
                }
                remainingRight.remove(pickedRightP);
                translateMap.put(pickedLeftP, pickedRightP);
            } else {
                int pickedLeft = this.random.nextInt(remainingLeft.size());
                int pickedRight = this.random.nextInt(remainingRight.size());
                Pokemon pickedLeftP = remainingLeft.remove(pickedLeft);
                Pokemon pickedRightP = remainingRight.get(pickedRight);
                while (pickedLeftP.number == pickedRightP.number && remainingRight.size() != 1) {
                    // Reroll for a different pokemon if at all possible
                    pickedRight = this.random.nextInt(remainingRight.size());
                    pickedRightP = remainingRight.get(pickedRight);
                }
                remainingRight.remove(pickedRight);
                translateMap.put(pickedLeftP, pickedRightP);
            }
            if (remainingRight.size() == 0) {
                // restart
                remainingRight.addAll(noLegendaries ? noLegendaryList : mainPokemonList);
                remainingRight.removeAll(banned);
            }
        }

        // Map remaining to themselves just in case
        List<Pokemon> allPokes = allPokemonWithoutNull();
        for (Pokemon poke : allPokes) {
            if (!translateMap.containsKey(poke)) {
                translateMap.put(poke, poke);
            }
        }

        List<EncounterSet> currentEncounters = this.getEncounters(useTimeOfDay);

        for (EncounterSet area : currentEncounters) {
            for (Encounter enc : area.encounters) {
                // Apply the map
                enc.pokemon = translateMap.get(enc.pokemon);
                if (area.bannedPokemon.contains(enc.pokemon)) {
                    // Ignore the map and put a random non-banned poke
                    List<Pokemon> tempPickable = noLegendaries ? new ArrayList<Pokemon>(noLegendaryList)
                            : new ArrayList<Pokemon>(mainPokemonList);
                    tempPickable.removeAll(banned);
                    tempPickable.removeAll(area.bannedPokemon);
                    if (tempPickable.size() == 0) {
                        throw new RandomizationException("ERROR: Couldn't replace a wild Pokemon!");
                    }
                    if (usePowerLevels) {
                        enc.pokemon = pickWildPowerLvlReplacement(tempPickable, enc.pokemon, false, null);
                    } else {
                        int picked = this.random.nextInt(tempPickable.size());
                        enc.pokemon = tempPickable.get(picked);
                    }
                }
            }
        }

        setEncounters(useTimeOfDay, currentEncounters);

    }

    @Override
    public void randomizeTrainerPokes(boolean usePowerLevels, boolean noLegendaries, boolean noEarlyWonderGuard,
            int levelModifier) {
        checkPokemonRestrictions();
        List<Trainer> currentTrainers = this.getTrainers();

        // New: randomize the order trainers are randomized in.
        // Leads to less predictable results for various modifiers.
        // Need to keep the original ordering around for saving though.
        List<Trainer> scrambledTrainers = new ArrayList<Trainer>(currentTrainers);
        Collections.shuffle(scrambledTrainers, this.random);

        cachedReplacementLists = new TreeMap<Type, List<Pokemon>>();
        cachedAllList = noLegendaries ? new ArrayList<Pokemon>(noLegendaryList) : new ArrayList<Pokemon>(
                mainPokemonList);

        // Fully random is easy enough - randomize then worry about rival
        // carrying starter at the end
        for (Trainer t : scrambledTrainers) {
            if (t.tag != null && t.tag.equals("IRIVAL")) {
                continue; // skip
            }
            for (TrainerPokemon tp : t.pokemon) {
                boolean wgAllowed = (!noEarlyWonderGuard) || tp.level >= 20;
                tp.pokemon = pickReplacement(tp.pokemon, usePowerLevels, null, noLegendaries, wgAllowed);
                tp.resetMoves = true;
                if (levelModifier != 0) {
                    tp.level = Math.min(100, (int) Math.round(tp.level * (1 + levelModifier / 100.0)));
                }
            }
        }

        // Save it all up
        this.setTrainers(currentTrainers);
    }

    @Override
    public void typeThemeTrainerPokes(boolean usePowerLevels, boolean weightByFrequency, boolean noLegendaries,
            boolean noEarlyWonderGuard, int levelModifier) {
        checkPokemonRestrictions();
        List<Trainer> currentTrainers = this.getTrainers();
        cachedReplacementLists = new TreeMap<Type, List<Pokemon>>();
        cachedAllList = noLegendaries ? new ArrayList<Pokemon>(noLegendaryList) : new ArrayList<Pokemon>(
                mainPokemonList);
        typeWeightings = new TreeMap<Type, Integer>();
        totalTypeWeighting = 0;

        // Construct groupings for types
        // Anything starting with GYM or ELITE or CHAMPION is a group
        Set<Trainer> assignedTrainers = new TreeSet<Trainer>();
        Map<String, List<Trainer>> groups = new TreeMap<String, List<Trainer>>();
        for (Trainer t : currentTrainers) {
            if (t.tag != null && t.tag.equals("IRIVAL")) {
                continue; // skip
            }
            String group = t.tag == null ? "" : t.tag;
            if (group.contains("-")) {
                group = group.substring(0, group.indexOf('-'));
            }
            if (group.startsWith("GYM") || group.startsWith("ELITE") || group.startsWith("CHAMPION")
                    || group.startsWith("THEMED")) {
                // Yep this is a group
                if (!groups.containsKey(group)) {
                    groups.put(group, new ArrayList<Trainer>());
                }
                groups.get(group).add(t);
                assignedTrainers.add(t);
            } else if (group.startsWith("GIO")) {
                // Giovanni has same grouping as his gym, gym 8
                if (!groups.containsKey("GYM8")) {
                    groups.put("GYM8", new ArrayList<Trainer>());
                }
                groups.get("GYM8").add(t);
                assignedTrainers.add(t);
            }
        }

        // Give a type to each group
        // Gym & elite types have to be unique
        // So do uber types, including the type we pick for champion
        Set<Type> usedGymTypes = new TreeSet<Type>();
        Set<Type> usedEliteTypes = new TreeSet<Type>();
        Set<Type> usedUberTypes = new TreeSet<Type>();
        for (String group : groups.keySet()) {
            List<Trainer> trainersInGroup = groups.get(group);
            // Shuffle ordering within group to promote randomness
            Collections.shuffle(trainersInGroup, random);
            Type typeForGroup = pickType(weightByFrequency, noLegendaries);
            if (group.startsWith("GYM")) {
                while (usedGymTypes.contains(typeForGroup)) {
                    typeForGroup = pickType(weightByFrequency, noLegendaries);
                }
                usedGymTypes.add(typeForGroup);
            }
            if (group.startsWith("ELITE")) {
                while (usedEliteTypes.contains(typeForGroup)) {
                    typeForGroup = pickType(weightByFrequency, noLegendaries);
                }
                usedEliteTypes.add(typeForGroup);
            }
            if (group.equals("CHAMPION")) {
                usedUberTypes.add(typeForGroup);
            }
            // Themed groups just have a theme, no special criteria
            for (Trainer t : trainersInGroup) {
                for (TrainerPokemon tp : t.pokemon) {
                    boolean wgAllowed = (!noEarlyWonderGuard) || tp.level >= 20;
                    tp.pokemon = pickReplacement(tp.pokemon, usePowerLevels, typeForGroup, noLegendaries, wgAllowed);
                    tp.resetMoves = true;
                    if (levelModifier != 0) {
                        tp.level = Math.min(100, (int) Math.round(tp.level * (1 + levelModifier / 100.0)));
                    }
                }
            }
        }

        // New: randomize the order trainers are randomized in.
        // Leads to less predictable results for various modifiers.
        // Need to keep the original ordering around for saving though.
        List<Trainer> scrambledTrainers = new ArrayList<Trainer>(currentTrainers);
        Collections.shuffle(scrambledTrainers, this.random);

        // Give a type to each unassigned trainer
        for (Trainer t : scrambledTrainers) {
            if (t.tag != null && t.tag.equals("IRIVAL")) {
                continue; // skip
            }

            if (!assignedTrainers.contains(t)) {
                Type typeForTrainer = pickType(weightByFrequency, noLegendaries);
                // Ubers: can't have the same type as each other
                if (t.tag != null && t.tag.equals("UBER")) {
                    while (usedUberTypes.contains(typeForTrainer)) {
                        typeForTrainer = pickType(weightByFrequency, noLegendaries);
                    }
                    usedUberTypes.add(typeForTrainer);
                }
                for (TrainerPokemon tp : t.pokemon) {
                    boolean shedAllowed = (!noEarlyWonderGuard) || tp.level >= 20;
                    tp.pokemon = pickReplacement(tp.pokemon, usePowerLevels, typeForTrainer, noLegendaries, shedAllowed);
                    tp.resetMoves = true;
                    if (levelModifier != 0) {
                        tp.level = Math.min(100, (int) Math.round(tp.level * (1 + levelModifier / 100.0)));
                    }
                }
            }
        }

        // Save it all up
        this.setTrainers(currentTrainers);
    }

    @Override
    public void rivalCarriesStarter() {
        checkPokemonRestrictions();
        List<Trainer> currentTrainers = this.getTrainers();
        rivalCarriesStarterUpdate(currentTrainers, "RIVAL", 1);
        rivalCarriesStarterUpdate(currentTrainers, "FRIEND", 2);
        this.setTrainers(currentTrainers);
    }

    @Override
    public void forceFullyEvolvedTrainerPokes(int minLevel) {
        checkPokemonRestrictions();
        List<Trainer> currentTrainers = this.getTrainers();
        for (Trainer t : currentTrainers) {
            for (TrainerPokemon tp : t.pokemon) {
                if (tp.level >= minLevel) {
                    Pokemon newPokemon = fullyEvolve(tp.pokemon);
                    if (newPokemon != tp.pokemon) {
                        tp.pokemon = newPokemon;
                        tp.resetMoves = true;
                    }
                }
            }
        }
        this.setTrainers(currentTrainers);
    }

    // MOVE DATA
    // All randomizers don't touch move ID 165 (Struggle)
    // They also have other exclusions where necessary to stop things glitching.

    @Override
    public void randomizeMovePowers() {
        List<Move> moves = this.getMoves();
        for (Move mv : moves) {
            if (mv != null && mv.internalId != 165 && mv.power >= 10) {
                // "Generic" damaging move to randomize power
                if (random.nextInt(3) != 2) {
                    // "Regular" move
                    mv.power = random.nextInt(11) * 5 + 50; // 50 ... 100
                } else {
                    // "Extreme" move
                    mv.power = random.nextInt(27) * 5 + 20; // 20 ... 150
                }
                // Tiny chance for massive power jumps
                for (int i = 0; i < 2; i++) {
                    if (random.nextInt(100) == 0) {
                        mv.power += 50;
                    }
                }

                if (mv.hitCount != 1) {
                    // Divide randomized power by average hit count, round to
                    // nearest 5
                    mv.power = (int) (Math.round(mv.power / mv.hitCount / 5) * 5);
                    if (mv.power == 0) {
                        mv.power = 5;
                    }
                }
            }
        }
    }

    @Override
    public void randomizeMovePPs() {
        List<Move> moves = this.getMoves();
        for (Move mv : moves) {
            if (mv != null && mv.internalId != 165) {
                if (random.nextInt(3) != 2) {
                    // "average" PP: 15-25
                    mv.pp = random.nextInt(3) * 5 + 15;
                } else {
                    // "extreme" PP: 5-40
                    mv.pp = random.nextInt(8) * 5 + 5;
                }
            }
        }
    }

    @Override
    public void randomizeMoveAccuracies() {
        List<Move> moves = this.getMoves();
        for (Move mv : moves) {
            if (mv != null && mv.internalId != 165 && mv.hitratio >= 5) {
                // "Sane" accuracy randomization
                // Broken into three tiers based on original accuracy
                // Designed to limit the chances of 100% accurate OHKO moves and
                // keep a decent base of 100% accurate regular moves.

                if (mv.hitratio <= 50) {
                    // lowest tier (acc <= 50)
                    // new accuracy = rand(20...50) inclusive
                    // with a 10% chance to increase by 50%
                    mv.hitratio = random.nextInt(7) * 5 + 20;
                    if (random.nextInt(10) == 0) {
                        mv.hitratio = (mv.hitratio * 3 / 2) / 5 * 5;
                    }
                } else if (mv.hitratio < 90) {
                    // middle tier (50 < acc < 90)
                    // count down from 100% to 20% in 5% increments with 20%
                    // chance to "stop" and use the current accuracy at each
                    // increment
                    // gives decent-but-not-100% accuracy most of the time
                    mv.hitratio = 100;
                    while (mv.hitratio > 20) {
                        if (random.nextInt(10) < 2) {
                            break;
                        }
                        mv.hitratio -= 5;
                    }
                } else {
                    // highest tier (90 <= acc <= 100)
                    // count down from 100% to 20% in 5% increments with 40%
                    // chance to "stop" and use the current accuracy at each
                    // increment
                    // gives high accuracy most of the time
                    mv.hitratio = 100;
                    while (mv.hitratio > 20) {
                        if (random.nextInt(10) < 4) {
                            break;
                        }
                        mv.hitratio -= 5;
                    }
                }
            }
        }
    }

    @Override
    public void randomizeMoveTypes() {
        List<Move> moves = this.getMoves();
        for (Move mv : moves) {
            if (mv != null && mv.internalId != 165 && mv.type != null) {
                mv.type = randomType();
            }
        }
    }

    @Override
    public void randomizeMoveCategory() {
        if (!this.hasPhysicalSpecialSplit()) {
            return;
        }
        List<Move> moves = this.getMoves();
        for (Move mv : moves) {
            if (mv != null && mv.internalId != 165 && mv.category != MoveCategory.STATUS) {
                if (random.nextInt(2) == 0) {
                    mv.category = (mv.category == MoveCategory.PHYSICAL) ? MoveCategory.SPECIAL : MoveCategory.PHYSICAL;
                }
            }
        }

    }

    @Override
    public void updateMovesToGen5() {
        List<Move> moves = this.getMoves();

        // gen1
        // Karate Chop => FIGHTING (gen1)
        updateMoveType(moves, 2, Type.FIGHTING);
        // Razor Wind => 100% accuracy (gen1/2)
        updateMoveAccuracy(moves, 13, 100);
        // Gust => FLYING (gen1)
        updateMoveType(moves, 16, Type.FLYING);
        // Wing Attack => 60 power (gen1)
        updateMovePower(moves, 17, 60);
        // Whirlwind => 100 accuracy
        updateMoveAccuracy(moves, 18, 100);
        // Fly => 90 power (gen1/2/3)
        updateMovePower(moves, 19, 90);
        // Bind => 85% accuracy (gen1-4)
        updateMoveAccuracy(moves, 20, 85);
        // Vine Whip => 15 pp (gen1/2/3)
        updateMovePP(moves, 22, 15);
        // Jump Kick => 10 pp, 100 power (gen1-4)
        updateMovePP(moves, 26, 10);
        updateMovePower(moves, 26, 100);
        // Sand Attack => GROUND (gen1)
        updateMoveType(moves, 28, Type.GROUND);
        // Tackle => 50 power, 100% accuracy , gen1-4
        updateMovePower(moves, 33, 50);
        updateMoveAccuracy(moves, 33, 100);
        // Wrap => 90% accuracy (gen1-4)
        updateMoveAccuracy(moves, 35, 90);
        // Thrash => 120 power, 10pp (gen1-4)
        updateMovePP(moves, 37, 10);
        updateMovePower(moves, 37, 120);
        // Double-Edge => 120 power (gen1)
        updateMovePower(moves, 38, 120);
        // Move 44, Bite, becomes dark (but doesn't exist anyway)
        // Disable => 100% accuracy (gen1-4)
        updateMoveAccuracy(moves, 50, 100);
        // Blizzard => 70% accuracy (gen1)
        updateMoveAccuracy(moves, 59, 70);
        // Move 67, Low Kick, has weight-based power in gen3+
        // Low Kick => 100% accuracy (gen1)
        updateMoveAccuracy(moves, 67, 100);
        // Absorb => 25pp (gen1/2/3)
        updateMovePP(moves, 71, 25);
        // Mega Drain => 15pp (gen1/2/3)
        updateMovePP(moves, 72, 15);
        // Petal Dance => 120power, 10pp (gen1-4)
        updateMovePP(moves, 80, 10);
        updateMovePower(moves, 80, 120);
        // Fire Spin => 35 power, 85% acc (gen1-4)
        updateMoveAccuracy(moves, 83, 85);
        updateMovePower(moves, 83, 35);
        // Rock Throw => 90% accuracy (gen1)
        updateMoveAccuracy(moves, 88, 90);
        // Dig => 80 power (gen1/2/3)
        updateMovePower(moves, 91, 80);
        // Toxic => 90% accuracy (gen1-4)
        updateMoveAccuracy(moves, 92, 90);
        // Hypnosis => 60% accuracy
        updateMoveAccuracy(moves, 95, 60);
        // Recover => 10pp (gen1/2/3)
        updateMovePP(moves, 105, 10);
        // SelfDestruct => 200power (gen1)
        updateMovePower(moves, 120, 200);
        // Clamp => 85% acc (gen1-4)
        updateMoveAccuracy(moves, 128, 85);
        updateMovePP(moves, 128, 15);
        // HJKick => 130 power, 10pp (gen1-4)
        updateMovePP(moves, 136, 10);
        updateMovePower(moves, 136, 130);
        // Glare => 90% acc (gen1-4)
        updateMoveAccuracy(moves, 137, 90);
        // Poison Gas => 80% acc (gen1-4)
        updateMoveAccuracy(moves, 139, 80);
        // Flash => 100% acc (gen1/2/3)
        updateMoveAccuracy(moves, 148, 100);
        // Crabhammer => 90% acc (gen1-4)
        updateMoveAccuracy(moves, 152, 90);
        // Explosion => 250 power (gen1)
        updateMovePower(moves, 153, 250);
        // GEN2+ moves only from here
        if (moves.size() >= 251) {
            // Curse => GHOST (gen2-4)
            updateMoveType(moves, 174, Type.GHOST);
            // Cotton Spore => 100% acc (gen2-4)
            updateMoveAccuracy(moves, 178, 100);
            // Scary Face => 100% acc (gen2-4)
            updateMoveAccuracy(moves, 184, 100);
            // Zap Cannon => 120 power (gen2-3)
            updateMovePower(moves, 192, 120);
            // Bone Rush => 90% acc (gen2-4)
            updateMoveAccuracy(moves, 198, 90);
            // Outrage => 120 power (gen2-3)
            updateMovePower(moves, 200, 120);
            updateMovePP(moves, 200, 10);
            // Giga Drain => 10pp (gen2-3), 75 power (gen2-4)
            updateMovePP(moves, 202, 10);
            updateMovePower(moves, 202, 75);
            // Fury Cutter => 20 power (gen2-4)
            updateMovePower(moves, 210, 20);
            // Future Sight => 10 pp, 100 power, 100% acc (gen2-4)
            updateMovePP(moves, 248, 10);
            updateMovePower(moves, 248, 100);
            updateMoveAccuracy(moves, 248, 100);
            // Rock Smash => 40 power (gen2-3)
            updateMovePower(moves, 249, 40);
            // Whirlpool => 35 pow, 85% acc (gen2-4)
            updateMovePower(moves, 250, 35);
            updateMoveAccuracy(moves, 250, 85);
        }
        // GEN3+ only moves from here
        if (moves.size() >= 354) {
            // Uproar => 90 power (gen3-4)
            updateMovePower(moves, 253, 90);
            updateMovePP(moves, 254, 20);
            updateMovePower(moves, 291, 80);
            // Sand Tomb => 35 pow, 85% acc (gen3-4)
            updateMovePower(moves, 328, 35);
            updateMoveAccuracy(moves, 328, 85);
            // Bullet Seed => 25 power (gen3-4)
            updateMovePower(moves, 331, 25);
            // Icicle Spear => 25 power (gen3-4)
            updateMovePower(moves, 333, 25);
            // Covet => 60 power (gen3-4)
            updateMovePower(moves, 343, 60);
            updateMovePower(moves, 348, 90);
            // Rock Blast => 90% acc (gen3-4)
            updateMoveAccuracy(moves, 350, 90);
            // Doom Desire => 140 pow, 100% acc, gen3-4
            updateMovePower(moves, 353, 140);
            updateMoveAccuracy(moves, 353, 100);
        }
        // GEN4+ only moves from here
        if (moves.size() >= 467) {
            // Feint => 30 pow
            updateMovePower(moves, 364, 30);
            // Last Resort => 140 pow
            updateMovePower(moves, 387, 140);
            // Drain Punch => 10 pp, 75 pow
            updateMovePP(moves, 409, 10);
            updateMovePower(moves, 409, 75);
            // Magma Storm => 75% acc
            updateMoveAccuracy(moves, 463, 75);
        }
    }

    @Override
    public void updateMovesToGen6() {
        List<Move> moves = this.getMoves();

        // gen 1
        // Swords Dance 20 PP
        updateMovePP(moves, 14, 20);

        // Vine Whip 25 PP, 45 Power
        updateMovePP(moves, 22, 25);
        updateMovePower(moves, 22, 45);

        // Pin Missile 25 Power, 95% Accuracy
        updateMovePower(moves, 42, 25);
        updateMoveAccuracy(moves, 42, 95);

        // Flamethrower 90 Power
        updateMovePower(moves, 53, 90);

        // Hydro Pump 110 Power
        updateMovePower(moves, 56, 110);

        // Surf 90 Power
        updateMovePower(moves, 57, 90);

        // Ice Beam 90 Power
        updateMovePower(moves, 58, 90);

        // Blizzard 110 Power
        updateMovePower(moves, 59, 110);

        // Growth 20 PP
        updateMovePP(moves, 74, 20);

        // Thunderbolt 90 Power
        updateMovePower(moves, 85, 90);

        // Thunder 110 Power
        updateMovePower(moves, 87, 110);

        // Minimize 10 PP
        updateMovePP(moves, 107, 10);

        // Barrier 20 PP
        updateMovePP(moves, 112, 20);

        // Lick 30 Power
        updateMovePower(moves, 122, 30);

        // Smog 30 Power
        updateMovePower(moves, 123, 30);

        // Fire Blast 110 Power
        updateMovePower(moves, 126, 110);

        // Skull Bash 10 PP, 130 Power
        updateMovePP(moves, 130, 10);
        updateMovePower(moves, 130, 130);

        // Glare 100% Accuracy
        updateMoveAccuracy(moves, 137, 100);

        // Poison Gas 90% Accuracy
        updateMoveAccuracy(moves, 139, 90);

        // Bubble 40 Power
        updateMovePower(moves, 145, 40);

        // Psywave 100% Accuracy
        updateMoveAccuracy(moves, 149, 100);

        // Acid Armor 20 PP
        updateMovePP(moves, 151, 20);

        // Crabhammer 100 Power
        updateMovePower(moves, 152, 100);

        // Gen2+ only
        if (moves.size() >= 251) {
            // Thief 25 PP, 60 Power
            updateMovePP(moves, 168, 25);
            updateMovePower(moves, 168, 60);

            // Snore 50 Power
            updateMovePower(moves, 173, 50);

            // Fury Cutter 40 Power
            updateMovePower(moves, 210, 40);

            // Future Sight 120 Power
            updateMovePower(moves, 248, 120);
        }

        // Gen3+ only
        if (moves.size() >= 354) {
            // Heat Wave 95 Power
            updateMovePower(moves, 257, 95);

            // Will-o-Wisp 85% Accuracy
            updateMoveAccuracy(moves, 261, 85);

            // Smellingsalt 70 Power
            updateMovePower(moves, 265, 70);

            // Knock off 65 Power
            updateMovePower(moves, 282, 65);

            // Meteor Mash 90 Power, 90% Accuracy
            updateMovePower(moves, 309, 90);
            updateMoveAccuracy(moves, 309, 90);

            // Air Cutter 60 Power
            updateMovePower(moves, 314, 60);

            // Overheat 130 Power
            updateMovePower(moves, 315, 130);

            // Rock Tomb 15 PP, 60 Power, 95% Accuracy
            updateMovePP(moves, 317, 15);
            updateMovePower(moves, 317, 60);
            updateMoveAccuracy(moves, 317, 95);

            // Extrasensory 20 PP
            updateMovePP(moves, 326, 20);

            // Muddy Water 90 Power
            updateMovePower(moves, 330, 90);

            // Covet 25 PP
            updateMovePP(moves, 343, 25);
        }

        // Gen4+ only
        if (moves.size() >= 467) {
            // Wake-Up Slap 70 Power
            updateMovePower(moves, 358, 70);

            // Tailwind 15 PP
            updateMovePP(moves, 366, 15);

            // Assurance 60 Power
            updateMovePower(moves, 372, 60);

            // Psycho Shift 100% Accuracy
            updateMoveAccuracy(moves, 375, 100);

            // Aura Sphere 80 Power
            updateMovePower(moves, 396, 80);

            // Air Slash 15 PP
            updateMovePP(moves, 403, 15);

            // Dragon Pulse 85 Power
            updateMovePower(moves, 406, 85);

            // Power Gem 80 Power
            updateMovePower(moves, 408, 80);

            // Energy Ball 90 Power
            updateMovePower(moves, 412, 90);

            // Draco Meteor 130 Power
            updateMovePower(moves, 434, 130);

            // Leaf Storm 130 Power
            updateMovePower(moves, 437, 130);

            // Gunk Shot 80% Accuracy
            updateMoveAccuracy(moves, 441, 80);

            // Chatter 65 Power
            updateMovePower(moves, 448, 65);

            // Magma Storm 100 Power
            updateMovePower(moves, 463, 100);
        }

        // Gen5+ only
        if (moves.size() >= 559) {
            // Storm Throw 60 Power
            updateMovePower(moves, 480, 60);

            // Synchronoise 120 Power
            updateMovePower(moves, 485, 120);

            // Low Sweep 65 Power
            updateMovePower(moves, 490, 65);

            // Hex 65 Power
            updateMovePower(moves, 506, 65);

            // Incinerate 60 Power
            updateMovePower(moves, 510, 60);

            // Pledges 80 Power
            updateMovePower(moves, 518, 80);
            updateMovePower(moves, 519, 80);
            updateMovePower(moves, 520, 80);

            // Struggle Bug 50 Power
            updateMovePower(moves, 522, 50);

            // Frost Breath 45 Power
            // crits are 2x in these games
            updateMovePower(moves, 524, 45);

            // Sacred Sword 15 PP
            updateMovePP(moves, 533, 15);

            // Hurricane 110 Power
            updateMovePower(moves, 542, 110);

            // Techno Blast 120 Power
            updateMovePower(moves, 546, 120);
        }
    }

    private Map<Integer, boolean[]> moveUpdates;

    @Override
    public void initMoveUpdates() {
        moveUpdates = new TreeMap<Integer, boolean[]>();
    }

    @Override
    public void printMoveUpdates() {
        log("--Move Updates--");
        List<Move> moves = this.getMoves();
        for (int moveID : moveUpdates.keySet()) {
            boolean[] changes = moveUpdates.get(moveID);
            Move mv = moves.get(moveID);
            List<String> nonTypeChanges = new ArrayList<String>();
            if (changes[0]) {
                nonTypeChanges.add(String.format("%d power", mv.power));
            }
            if (changes[1]) {
                nonTypeChanges.add(String.format("%d PP", mv.pp));
            }
            if (changes[2]) {
                nonTypeChanges.add(String.format("%.00f%% accuracy", mv.hitratio));
            }
            String logStr = "Made " + mv.name;
            // type or not?
            if (changes[3]) {
                logStr += " be " + mv.type + "-type";
                if (nonTypeChanges.size() > 0) {
                    logStr += " and";
                }
            }
            if (nonTypeChanges.size() > 0) {
                logStr += " have ";
                if (nonTypeChanges.size() == 3) {
                    logStr += nonTypeChanges.get(0) + ", " + nonTypeChanges.get(1) + " and " + nonTypeChanges.get(2);
                } else if (nonTypeChanges.size() == 2) {
                    logStr += nonTypeChanges.get(0) + " and " + nonTypeChanges.get(1);
                } else {
                    logStr += nonTypeChanges.get(0);
                }
            }
            log(logStr);
        }
        logBlankLine();
    }

    @Override
    public void randomizeMovesLearnt(boolean typeThemed, boolean noBroken, boolean forceFourStartingMoves,
            double goodDamagingProbability) {
        // Get current sets
        Map<Pokemon, List<MoveLearnt>> movesets = this.getMovesLearnt();
        List<Integer> hms = this.getHMMoves();
        List<Move> allMoves = this.getMoves();

        @SuppressWarnings("unchecked")
        Set<Integer> allBanned = new HashSet<Integer>(noBroken ? this.getGameBreakingMoves() : Collections.EMPTY_SET);
        allBanned.addAll(hms);
        allBanned.addAll(this.getMovesBannedFromLevelup());

        // Build sets of moves
        List<Move> validMoves = new ArrayList<Move>();
        List<Move> validDamagingMoves = new ArrayList<Move>();
        Map<Type, List<Move>> validTypeMoves = new HashMap<Type, List<Move>>();
        Map<Type, List<Move>> validTypeDamagingMoves = new HashMap<Type, List<Move>>();

        for (Move mv : allMoves) {
            if (mv != null && !GlobalConstants.bannedRandomMoves[mv.number] && !allBanned.contains(mv.number)) {
                validMoves.add(mv);
                if (mv.type != null) {
                    if (!validTypeMoves.containsKey(mv.type)) {
                        validTypeMoves.put(mv.type, new ArrayList<Move>());
                    }
                    validTypeMoves.get(mv.type).add(mv);
                }

                if (!GlobalConstants.bannedForDamagingMove[mv.number]) {
                    if (mv.power >= 2 * GlobalConstants.MIN_DAMAGING_MOVE_POWER
                            || (mv.power >= GlobalConstants.MIN_DAMAGING_MOVE_POWER && mv.hitratio >= 90)) {
                        validDamagingMoves.add(mv);
                        if (mv.type != null) {
                            if (!validTypeDamagingMoves.containsKey(mv.type)) {
                                validTypeDamagingMoves.put(mv.type, new ArrayList<Move>());
                            }
                            validTypeDamagingMoves.get(mv.type).add(mv);
                        }
                    }
                }
            }
        }

        for (Pokemon pkmn : movesets.keySet()) {
            Set<Integer> learnt = new TreeSet<Integer>();
            List<MoveLearnt> moves = movesets.get(pkmn);

            // 4 starting moves?
            if (forceFourStartingMoves) {
                int lv1count = 0;
                for (MoveLearnt ml : moves) {
                    if (ml.level == 1) {
                        lv1count++;
                    }
                }
                if (lv1count < 4) {
                    for (int i = 0; i < 4 - lv1count; i++) {
                        MoveLearnt fakeLv1 = new MoveLearnt();
                        fakeLv1.level = 1;
                        fakeLv1.move = 0;
                        moves.add(0, fakeLv1);
                    }
                }
            }

            // Find last lv1 move
            // lv1index ends up as the index of the first non-lv1 move
            int lv1index = 0;
            while (lv1index < moves.size() && moves.get(lv1index).level == 1) {
                lv1index++;
            }

            // last lv1 move is 1 before lv1index
            if (lv1index != 0) {
                lv1index--;
            }

            // Replace moves as needed
            for (int i = 0; i < moves.size(); i++) {
                // should this move be forced damaging?
                boolean attemptDamaging = i == lv1index ? true : random.nextDouble() < goodDamagingProbability;

                // type themed?
                Type typeOfMove = null;
                if (typeThemed) {
                    double picked = random.nextDouble();
                    if (pkmn.primaryType == Type.NORMAL || pkmn.secondaryType == Type.NORMAL) {
                        if (pkmn.secondaryType == null) {
                            // Pure NORMAL: 75% normal, 25% random
                            if (picked < 0.75) {
                                typeOfMove = Type.NORMAL;
                            }
                            // else random
                        } else {
                            // Find the other type
                            // Normal/OTHER: 30% normal, 55% other, 15% random
                            Type otherType = pkmn.primaryType;
                            if (otherType == Type.NORMAL) {
                                otherType = pkmn.secondaryType;
                            }
                            if (picked < 0.3) {
                                typeOfMove = Type.NORMAL;
                            } else if (picked < 0.85) {
                                typeOfMove = otherType;
                            }
                            // else random
                        }
                    } else if (pkmn.secondaryType != null) {
                        // Primary/Secondary: 50% primary, 30% secondary, 5%
                        // normal, 15% random
                        if (picked < 0.5) {
                            typeOfMove = pkmn.primaryType;
                        } else if (picked < 0.8) {
                            typeOfMove = pkmn.secondaryType;
                        } else if (picked < 0.85) {
                            typeOfMove = Type.NORMAL;
                        }
                        // else random
                    } else {
                        // Primary/None: 60% primary, 20% normal, 20% random
                        if (picked < 0.6) {
                            typeOfMove = pkmn.primaryType;
                        } else if (picked < 0.8) {
                            typeOfMove = Type.NORMAL;
                        }
                        // else random
                    }
                }

                // select a list to pick a move from that has at least one free
                List<Move> pickList = validMoves;
                if (attemptDamaging) {
                    if (typeOfMove != null) {
                        if (validTypeDamagingMoves.containsKey(typeOfMove)
                                && checkForUnusedMove(validTypeDamagingMoves.get(typeOfMove), learnt)) {
                            pickList = validTypeDamagingMoves.get(typeOfMove);
                        } else if (checkForUnusedMove(validDamagingMoves, learnt)) {
                            pickList = validDamagingMoves;
                        }
                    } else if (checkForUnusedMove(validDamagingMoves, learnt)) {
                        pickList = validDamagingMoves;
                    }
                } else if (typeOfMove != null) {
                    if (validTypeMoves.containsKey(typeOfMove)
                            && checkForUnusedMove(validTypeMoves.get(typeOfMove), learnt)) {
                        pickList = validTypeMoves.get(typeOfMove);
                    }
                }

                // now pick a move until we get a valid one
                Move mv = pickList.get(random.nextInt(pickList.size()));
                while (learnt.contains(mv.number)) {
                    mv = pickList.get(random.nextInt(pickList.size()));
                }

                // write it
                moves.get(i).move = mv.number;
                if (i == lv1index) {
                    // just in case, set this to lv1
                    moves.get(i).level = 1;
                }
                learnt.add(mv.number);

            }
        }
        // Done, save
        this.setMovesLearnt(movesets);

    }

    @Override
    public void orderDamagingMovesByDamage() {
        Map<Pokemon, List<MoveLearnt>> movesets = this.getMovesLearnt();
        List<Move> allMoves = this.getMoves();
        for (Pokemon pkmn : movesets.keySet()) {
            List<MoveLearnt> moves = movesets.get(pkmn);

            // Build up a list of damaging moves and their positions
            List<Integer> damagingMoveIndices = new ArrayList<Integer>();
            List<Move> damagingMoves = new ArrayList<Move>();
            for (int i = 0; i < moves.size(); i++) {
                Move mv = allMoves.get(moves.get(i).move);
                if (mv.power > 1) {
                    // considered a damaging move for this purpose
                    damagingMoveIndices.add(i);
                    damagingMoves.add(mv);
                }
            }

            // Ties should be sorted randomly, so shuffle the list first.
            Collections.shuffle(damagingMoves, random);

            // Sort the damaging moves by power
            Collections.sort(damagingMoves, new Comparator<Move>() {

                @Override
                public int compare(Move m1, Move m2) {
                    if (m1.power * m1.hitCount < m2.power * m2.hitCount) {
                        return -1;
                    } else if (m1.power * m1.hitCount > m2.power * m2.hitCount) {
                        return 1;
                    } else {
                        // stay with the random order
                        return 0;
                    }
                }
            });

            // Reassign damaging moves in the ordered positions
            for (int i = 0; i < damagingMoves.size(); i++) {
                moves.get(damagingMoveIndices.get(i)).move = damagingMoves.get(i).number;
            }
        }

        // Done, save
        this.setMovesLearnt(movesets);
    }

    @Override
    public void metronomeOnlyMode() {

        // movesets
        Map<Pokemon, List<MoveLearnt>> movesets = this.getMovesLearnt();

        MoveLearnt metronomeML = new MoveLearnt();
        metronomeML.level = 1;
        metronomeML.move = GlobalConstants.METRONOME_MOVE;

        for (List<MoveLearnt> ms : movesets.values()) {
            if (ms != null && ms.size() > 0) {
                ms.clear();
                ms.add(metronomeML);
            }
        }

        this.setMovesLearnt(movesets);

        // trainers
        // run this to remove all custom non-Metronome moves
        List<Trainer> trainers = this.getTrainers();

        for (Trainer t : trainers) {
            for (TrainerPokemon tpk : t.pokemon) {
                tpk.resetMoves = true;
            }
        }

        this.setTrainers(trainers);

        // tms
        List<Integer> tmMoves = this.getTMMoves();

        for (int i = 0; i < tmMoves.size(); i++) {
            tmMoves.set(i, GlobalConstants.METRONOME_MOVE);
        }

        this.setTMMoves(tmMoves);

        // movetutors
        if (this.hasMoveTutors()) {
            List<Integer> mtMoves = this.getMoveTutorMoves();

            for (int i = 0; i < mtMoves.size(); i++) {
                mtMoves.set(i, GlobalConstants.METRONOME_MOVE);
            }

            this.setMoveTutorMoves(mtMoves);
        }

        // move tweaks
        List<Move> moveData = this.getMoves();

        Move metronome = moveData.get(GlobalConstants.METRONOME_MOVE);

        metronome.pp = 40;

        List<Integer> hms = this.getHMMoves();

        for (int hm : hms) {
            Move thisHM = moveData.get(hm);
            thisHM.pp = 0;
        }
    }

    @Override
    public void randomizeStaticPokemon(boolean legendForLegend) {
        // Load
        checkPokemonRestrictions();
        List<Pokemon> currentStaticPokemon = this.getStaticPokemon();
        List<Pokemon> replacements = new ArrayList<Pokemon>();
        List<Pokemon> banned = this.bannedForStaticPokemon();

        if (legendForLegend) {
            List<Pokemon> legendariesLeft = new ArrayList<Pokemon>(onlyLegendaryList);
            List<Pokemon> nonlegsLeft = new ArrayList<Pokemon>(noLegendaryList);
            legendariesLeft.removeAll(banned);
            nonlegsLeft.removeAll(banned);
            for (int i = 0; i < currentStaticPokemon.size(); i++) {
                Pokemon old = currentStaticPokemon.get(i);
                Pokemon newPK;
                if (old.isLegendary()) {
                    newPK = legendariesLeft.remove(this.random.nextInt(legendariesLeft.size()));
                    if (legendariesLeft.size() == 0) {
                        legendariesLeft.addAll(onlyLegendaryList);
                        legendariesLeft.removeAll(banned);
                    }
                } else {
                    newPK = nonlegsLeft.remove(this.random.nextInt(nonlegsLeft.size()));
                    if (nonlegsLeft.size() == 0) {
                        nonlegsLeft.addAll(noLegendaryList);
                        nonlegsLeft.removeAll(banned);
                    }
                }
                replacements.add(newPK);
            }
        } else {
            List<Pokemon> pokemonLeft = new ArrayList<Pokemon>(mainPokemonList);
            pokemonLeft.removeAll(banned);
            for (int i = 0; i < currentStaticPokemon.size(); i++) {
                Pokemon newPK = pokemonLeft.remove(this.random.nextInt(pokemonLeft.size()));
                if (pokemonLeft.size() == 0) {
                    pokemonLeft.addAll(mainPokemonList);
                    pokemonLeft.removeAll(banned);
                }
                replacements.add(newPK);
            }
        }

        // Save
        this.setStaticPokemon(replacements);
    }

    @Override
    public void randomizeTMMoves(boolean noBroken, boolean preserveField, double goodDamagingProbability) {
        // Pick some random TM moves.
        int tmCount = this.getTMCount();
        List<Move> allMoves = this.getMoves();
        List<Integer> hms = this.getHMMoves();
        List<Integer> oldTMs = this.getTMMoves();
        @SuppressWarnings("unchecked")
        List<Integer> banned = new ArrayList<Integer>(noBroken ? this.getGameBreakingMoves() : Collections.EMPTY_LIST);
        // field moves?
        List<Integer> fieldMoves = this.getFieldMoves();
        int preservedFieldMoveCount = 0;

        if (preserveField) {
            List<Integer> banExistingField = new ArrayList<Integer>(oldTMs);
            banExistingField.retainAll(fieldMoves);
            preservedFieldMoveCount = banExistingField.size();
            banned.addAll(banExistingField);
        }

        // Determine which moves are pickable
        List<Move> usableMoves = new ArrayList<Move>(allMoves);
        usableMoves.remove(0); // remove null entry
        Set<Move> unusableMoves = new HashSet<Move>();
        Set<Move> unusableDamagingMoves = new HashSet<Move>();

        for (Move mv : usableMoves) {
            if (GlobalConstants.bannedRandomMoves[mv.number] || hms.contains(mv.number) || banned.contains(mv.number)) {
                unusableMoves.add(mv);
            } else if (GlobalConstants.bannedForDamagingMove[mv.number]
                    || mv.power < GlobalConstants.MIN_DAMAGING_MOVE_POWER) {
                unusableDamagingMoves.add(mv);
            }
        }

        usableMoves.removeAll(unusableMoves);
        List<Move> usableDamagingMoves = new ArrayList<Move>(usableMoves);
        usableDamagingMoves.removeAll(unusableDamagingMoves);

        // pick (tmCount - preservedFieldMoveCount) moves
        List<Integer> pickedMoves = new ArrayList<Integer>();

        for (int i = 0; i < tmCount - preservedFieldMoveCount; i++) {
            Move chosenMove;
            if (random.nextDouble() < goodDamagingProbability && usableDamagingMoves.size() > 0) {
                chosenMove = usableDamagingMoves.get(random.nextInt(usableDamagingMoves.size()));
            } else {
                chosenMove = usableMoves.get(random.nextInt(usableMoves.size()));
            }
            pickedMoves.add(chosenMove.number);
            usableMoves.remove(chosenMove);
            usableDamagingMoves.remove(chosenMove);
        }

        // shuffle the picked moves because high goodDamagingProbability
        // could bias them towards early numbers otherwise

        Collections.shuffle(pickedMoves, random);

        // finally, distribute them as tms
        int pickedMoveIndex = 0;
        List<Integer> newTMs = new ArrayList<Integer>();

        for (int i = 0; i < tmCount; i++) {
            if (preserveField && fieldMoves.contains(oldTMs.get(i))) {
                newTMs.add(oldTMs.get(i));
            } else {
                newTMs.add(pickedMoves.get(pickedMoveIndex++));
            }
        }

        this.setTMMoves(newTMs);
    }

    @Override
    public void randomizeTMHMCompatibility(boolean preferSameType) {
        // Get current compatibility
        // new: increase HM chances if required early on
        List<Integer> requiredEarlyOn = this.getEarlyRequiredHMMoves();
        Map<Pokemon, boolean[]> compat = this.getTMHMCompatibility();
        List<Integer> tmHMs = new ArrayList<Integer>(this.getTMMoves());
        tmHMs.addAll(this.getHMMoves());
        List<Move> moveData = this.getMoves();
        for (Map.Entry<Pokemon, boolean[]> compatEntry : compat.entrySet()) {
            Pokemon pkmn = compatEntry.getKey();
            boolean[] flags = compatEntry.getValue();
            for (int i = 1; i <= tmHMs.size(); i++) {
                int move = tmHMs.get(i - 1);
                Move mv = moveData.get(move);
                double probability = 0.5;
                if (preferSameType) {
                    if (pkmn.primaryType.equals(mv.type)
                            || (pkmn.secondaryType != null && pkmn.secondaryType.equals(mv.type))) {
                        probability = 0.9;
                    } else if (mv.type != null && mv.type.equals(Type.NORMAL)) {
                        probability = 0.5;
                    } else {
                        probability = 0.25;
                    }
                }
                if (requiredEarlyOn.contains(move)) {
                    probability = Math.min(1.0, probability * 1.8);
                }
                flags[i] = (this.random.nextDouble() < probability);
            }
        }

        // Set the new compatibility
        this.setTMHMCompatibility(compat);
    }

    @Override
    public void fullTMHMCompatibility() {
        Map<Pokemon, boolean[]> compat = this.getTMHMCompatibility();
        for (Map.Entry<Pokemon, boolean[]> compatEntry : compat.entrySet()) {
            boolean[] flags = compatEntry.getValue();
            for (int i = 1; i < flags.length; i++) {
                flags[i] = true;
            }
        }
        this.setTMHMCompatibility(compat);
    }

    @Override
    public void ensureTMCompatSanity() {
        // if a pokemon learns a move in its moveset
        // and there is a TM of that move, make sure
        // that TM can be learned.
        Map<Pokemon, boolean[]> compat = this.getTMHMCompatibility();
        Map<Pokemon, List<MoveLearnt>> movesets = this.getMovesLearnt();
        List<Integer> tmMoves = this.getTMMoves();
        for (Pokemon pkmn : compat.keySet()) {
            List<MoveLearnt> moveset = movesets.get(pkmn);
            boolean[] pkmnCompat = compat.get(pkmn);
            for (MoveLearnt ml : moveset) {
                if (tmMoves.contains(ml.move)) {
                    int tmIndex = tmMoves.indexOf(ml.move);
                    pkmnCompat[tmIndex + 1] = true;
                }
            }
        }
        this.setTMHMCompatibility(compat);
    }

    @Override
    public void fullHMCompatibility() {
        Map<Pokemon, boolean[]> compat = this.getTMHMCompatibility();
        int tmCount = this.getTMCount();
        for (boolean[] flags : compat.values()) {
            for (int i = tmCount + 1; i < flags.length; i++) {
                flags[i] = true;
            }
        }

        // Set the new compatibility
        this.setTMHMCompatibility(compat);
    }

    @Override
    public void randomizeMoveTutorMoves(boolean noBroken, boolean preserveField, double goodDamagingProbability) {
        if (!this.hasMoveTutors()) {
            return;
        }

        // Pick some random Move Tutor moves, excluding TMs.
        List<Move> allMoves = this.getMoves();
        List<Integer> tms = this.getTMMoves();
        List<Integer> oldMTs = this.getMoveTutorMoves();
        int mtCount = oldMTs.size();
        List<Integer> hms = this.getHMMoves();
        @SuppressWarnings("unchecked")
        List<Integer> banned = new ArrayList<Integer>(noBroken ? this.getGameBreakingMoves() : Collections.EMPTY_LIST);

        // field moves?
        List<Integer> fieldMoves = this.getFieldMoves();
        int preservedFieldMoveCount = 0;
        if (preserveField) {
            List<Integer> banExistingField = new ArrayList<Integer>(oldMTs);
            banExistingField.retainAll(fieldMoves);
            preservedFieldMoveCount = banExistingField.size();
            banned.addAll(banExistingField);
        }

        // Determine which moves are pickable
        List<Move> usableMoves = new ArrayList<Move>(allMoves);
        usableMoves.remove(0); // remove null entry
        Set<Move> unusableMoves = new HashSet<Move>();
        Set<Move> unusableDamagingMoves = new HashSet<Move>();

        for (Move mv : usableMoves) {
            if (GlobalConstants.bannedRandomMoves[mv.number] || tms.contains(mv.number) || hms.contains(mv.number)
                    || banned.contains(mv.number)) {
                unusableMoves.add(mv);
            } else if (GlobalConstants.bannedForDamagingMove[mv.number]
                    || mv.power < GlobalConstants.MIN_DAMAGING_MOVE_POWER) {
                unusableDamagingMoves.add(mv);
            }
        }

        usableMoves.removeAll(unusableMoves);
        List<Move> usableDamagingMoves = new ArrayList<Move>(usableMoves);
        usableDamagingMoves.removeAll(unusableDamagingMoves);

        // pick (tmCount - preservedFieldMoveCount) moves
        List<Integer> pickedMoves = new ArrayList<Integer>();

        for (int i = 0; i < mtCount - preservedFieldMoveCount; i++) {
            Move chosenMove;
            if (random.nextDouble() < goodDamagingProbability && usableDamagingMoves.size() > 0) {
                chosenMove = usableDamagingMoves.get(random.nextInt(usableDamagingMoves.size()));
            } else {
                chosenMove = usableMoves.get(random.nextInt(usableMoves.size()));
            }
            pickedMoves.add(chosenMove.number);
            usableMoves.remove(chosenMove);
            usableDamagingMoves.remove(chosenMove);
        }

        // shuffle the picked moves because high goodDamagingProbability
        // could bias them towards early numbers otherwise

        Collections.shuffle(pickedMoves, random);

        // finally, distribute them as tutors
        int pickedMoveIndex = 0;
        List<Integer> newMTs = new ArrayList<Integer>();

        for (int i = 0; i < mtCount; i++) {
            if (preserveField && fieldMoves.contains(oldMTs.get(i))) {
                newMTs.add(oldMTs.get(i));
            } else {
                newMTs.add(pickedMoves.get(pickedMoveIndex++));
            }
        }

        this.setMoveTutorMoves(newMTs);
    }

    @Override
    public void randomizeMoveTutorCompatibility(boolean preferSameType) {
        if (!this.hasMoveTutors()) {
            return;
        }
        // Get current compatibility
        Map<Pokemon, boolean[]> compat = this.getMoveTutorCompatibility();
        List<Integer> mts = this.getMoveTutorMoves();
        List<Move> moveData = this.getMoves();
        for (Map.Entry<Pokemon, boolean[]> compatEntry : compat.entrySet()) {
            Pokemon pkmn = compatEntry.getKey();
            boolean[] flags = compatEntry.getValue();
            for (int i = 1; i <= mts.size(); i++) {
                int move = mts.get(i - 1);
                Move mv = moveData.get(move);
                double probability = 0.5;
                if (preferSameType) {
                    if (pkmn.primaryType.equals(mv.type)
                            || (pkmn.secondaryType != null && pkmn.secondaryType.equals(mv.type))) {
                        probability = 0.9;
                    } else if (mv.type != null && mv.type.equals(Type.NORMAL)) {
                        probability = 0.5;
                    } else {
                        probability = 0.25;
                    }
                }
                flags[i] = (this.random.nextDouble() < probability);
            }
        }

        // Set the new compatibility
        this.setMoveTutorCompatibility(compat);

    }

    @Override
    public void fullMoveTutorCompatibility() {
        if (!this.hasMoveTutors()) {
            return;
        }
        Map<Pokemon, boolean[]> compat = this.getMoveTutorCompatibility();
        for (Map.Entry<Pokemon, boolean[]> compatEntry : compat.entrySet()) {
            boolean[] flags = compatEntry.getValue();
            for (int i = 1; i < flags.length; i++) {
                flags[i] = true;
            }
        }
        this.setMoveTutorCompatibility(compat);
    }

    @Override
    public void ensureMoveTutorCompatSanity() {
        if (!this.hasMoveTutors()) {
            return;
        }
        // if a pokemon learns a move in its moveset
        // and there is a tutor of that move, make sure
        // that tutor can be learned.
        Map<Pokemon, boolean[]> compat = this.getMoveTutorCompatibility();
        Map<Pokemon, List<MoveLearnt>> movesets = this.getMovesLearnt();
        List<Integer> mtMoves = this.getMoveTutorMoves();
        for (Pokemon pkmn : compat.keySet()) {
            List<MoveLearnt> moveset = movesets.get(pkmn);
            boolean[] pkmnCompat = compat.get(pkmn);
            for (MoveLearnt ml : moveset) {
                if (mtMoves.contains(ml.move)) {
                    int mtIndex = mtMoves.indexOf(ml.move);
                    pkmnCompat[mtIndex + 1] = true;
                }
            }
        }
        this.setMoveTutorCompatibility(compat);

    }

    @SuppressWarnings("unchecked")
    @Override
    public void randomizeTrainerNames(CustomNamesSet customNames) {
        if (!this.canChangeTrainerText()) {
            return;
        }

        // index 0 = singles, 1 = doubles
        List<String>[] allTrainerNames = new List[] { new ArrayList<String>(), new ArrayList<String>() };
        Map<Integer, List<String>> trainerNamesByLength[] = new Map[] { new TreeMap<Integer, List<String>>(),
                new TreeMap<Integer, List<String>>() };

        // Read name lists
        for (String trainername : customNames.getTrainerNames()) {
            int len = this.internalStringLength(trainername);
            if (len <= 10) {
                allTrainerNames[0].add(trainername);
                if (trainerNamesByLength[0].containsKey(len)) {
                    trainerNamesByLength[0].get(len).add(trainername);
                } else {
                    List<String> namesOfThisLength = new ArrayList<String>();
                    namesOfThisLength.add(trainername);
                    trainerNamesByLength[0].put(len, namesOfThisLength);
                }
            }
        }

        for (String trainername : customNames.getDoublesTrainerNames()) {
            int len = this.internalStringLength(trainername);
            if (len <= 10) {
                allTrainerNames[1].add(trainername);
                if (trainerNamesByLength[1].containsKey(len)) {
                    trainerNamesByLength[1].get(len).add(trainername);
                } else {
                    List<String> namesOfThisLength = new ArrayList<String>();
                    namesOfThisLength.add(trainername);
                    trainerNamesByLength[1].put(len, namesOfThisLength);
                }
            }
        }

        // Get the current trainer names data
        List<String> currentTrainerNames = this.getTrainerNames();
        if (currentTrainerNames.size() == 0) {
            // RBY have no trainer names
            return;
        }
        TrainerNameMode mode = this.trainerNameMode();
        int maxLength = this.maxTrainerNameLength();
        int totalMaxLength = this.maxSumOfTrainerNameLengths();

        boolean success = false;
        int tries = 0;

        // Init the translation map and new list
        Map<String, String> translation = new HashMap<String, String>();
        List<String> newTrainerNames = new ArrayList<String>();
        List<Integer> tcNameLengths = this.getTCNameLengthsByTrainer();

        // loop until we successfully pick names that fit
        // should always succeed first attempt except for gen2.
        while (!success && tries < 10000) {
            success = true;
            translation.clear();
            newTrainerNames.clear();
            int totalLength = 0;

            // Start choosing
            int tnIndex = -1;
            for (String trainerName : currentTrainerNames) {
                tnIndex++;
                if (translation.containsKey(trainerName) && trainerName.equalsIgnoreCase("GRUNT") == false
                        && trainerName.equalsIgnoreCase("EXECUTIVE") == false) {
                    // use an already picked translation
                    newTrainerNames.add(translation.get(trainerName));
                    totalLength += this.internalStringLength(translation.get(trainerName));
                } else {
                    int idx = trainerName.contains("&") ? 1 : 0;
                    List<String> pickFrom = allTrainerNames[idx];
                    int intStrLen = this.internalStringLength(trainerName);
                    if (mode == TrainerNameMode.SAME_LENGTH) {
                        pickFrom = trainerNamesByLength[idx].get(intStrLen);
                    }
                    String changeTo = trainerName;
                    int ctl = intStrLen;
                    if (pickFrom != null && pickFrom.size() > 0 && intStrLen > 1) {
                        int innerTries = 0;
                        changeTo = pickFrom.get(this.random.nextInt(pickFrom.size()));
                        ctl = this.internalStringLength(changeTo);
                        while ((mode == TrainerNameMode.MAX_LENGTH && ctl > maxLength)
                                || (mode == TrainerNameMode.MAX_LENGTH_WITH_CLASS && ctl + tcNameLengths.get(tnIndex) > maxLength)) {
                            innerTries++;
                            if (innerTries == 100) {
                                changeTo = trainerName;
                                ctl = intStrLen;
                                break;
                            }
                            changeTo = pickFrom.get(this.random.nextInt(pickFrom.size()));
                            ctl = this.internalStringLength(changeTo);
                        }
                    }
                    translation.put(trainerName, changeTo);
                    newTrainerNames.add(changeTo);
                    totalLength += ctl;
                }

                if (totalLength > totalMaxLength) {
                    success = false;
                    tries++;
                    break;
                }
            }
        }

        if (!success) {
            throw new RandomizationException("Could not randomize trainer names in a reasonable amount of attempts."
                    + "\nPlease add some shorter names to your custom trainer names.");
        }

        // Done choosing, save
        this.setTrainerNames(newTrainerNames);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void randomizeTrainerClassNames(CustomNamesSet customNames) {
        if (!this.canChangeTrainerText()) {
            return;
        }

        // index 0 = singles, index 1 = doubles
        List<String> allTrainerClasses[] = new List[] { new ArrayList<String>(), new ArrayList<String>() };
        Map<Integer, List<String>> trainerClassesByLength[] = new Map[] { new HashMap<Integer, List<String>>(),
                new HashMap<Integer, List<String>>() };

        // Read names data
        for (String trainerClassName : customNames.getTrainerClasses()) {
            allTrainerClasses[0].add(trainerClassName);
            int len = this.internalStringLength(trainerClassName);
            if (trainerClassesByLength[0].containsKey(len)) {
                trainerClassesByLength[0].get(len).add(trainerClassName);
            } else {
                List<String> namesOfThisLength = new ArrayList<String>();
                namesOfThisLength.add(trainerClassName);
                trainerClassesByLength[0].put(len, namesOfThisLength);
            }
        }

        for (String trainerClassName : customNames.getDoublesTrainerClasses()) {
            allTrainerClasses[1].add(trainerClassName);
            int len = this.internalStringLength(trainerClassName);
            if (trainerClassesByLength[1].containsKey(len)) {
                trainerClassesByLength[1].get(len).add(trainerClassName);
            } else {
                List<String> namesOfThisLength = new ArrayList<String>();
                namesOfThisLength.add(trainerClassName);
                trainerClassesByLength[1].put(len, namesOfThisLength);
            }
        }

        // Get the current trainer names data
        List<String> currentClassNames = this.getTrainerClassNames();
        boolean mustBeSameLength = this.fixedTrainerClassNamesLength();
        int maxLength = this.maxTrainerClassNameLength();

        // Init the translation map and new list
        Map<String, String> translation = new HashMap<String, String>();
        List<String> newClassNames = new ArrayList<String>();

        int numTrainerClasses = currentClassNames.size();
        List<Integer> doublesClasses = this.getDoublesTrainerClasses();

        // Start choosing
        for (int i = 0; i < numTrainerClasses; i++) {
            String trainerClassName = currentClassNames.get(i);
            if (translation.containsKey(trainerClassName)) {
                // use an already picked translation
                newClassNames.add(translation.get(trainerClassName));
            } else {
                int idx = doublesClasses.contains(i) ? 1 : 0;
                List<String> pickFrom = allTrainerClasses[idx];
                int intStrLen = this.internalStringLength(trainerClassName);
                if (mustBeSameLength) {
                    pickFrom = trainerClassesByLength[idx].get(intStrLen);
                }
                String changeTo = trainerClassName;
                if (pickFrom != null && pickFrom.size() > 0) {
                    changeTo = pickFrom.get(this.random.nextInt(pickFrom.size()));
                    while (changeTo.length() > maxLength) {
                        changeTo = pickFrom.get(this.random.nextInt(pickFrom.size()));
                    }
                }
                translation.put(trainerClassName, changeTo);
                newClassNames.add(changeTo);
            }
        }

        // Done choosing, save
        this.setTrainerClassNames(newClassNames);
    }

    @Override
    public void randomizeWildHeldItems(boolean banBadItems) {
        List<Pokemon> pokemon = allPokemonWithoutNull();
        ItemList possibleItems = banBadItems ? this.getNonBadItems() : this.getAllowedItems();
        for (Pokemon pk : pokemon) {
            if (pk.guaranteedHeldItem == -1 && pk.commonHeldItem == -1 && pk.rareHeldItem == -1
                    && pk.darkGrassHeldItem == -1) {
                // No held items at all, abort
                return;
            }
            boolean canHaveDarkGrass = pk.darkGrassHeldItem != -1;
            if (pk.guaranteedHeldItem != -1) {
                // Guaranteed held items are supported.
                if (pk.guaranteedHeldItem > 0) {
                    // Currently have a guaranteed item
                    double decision = this.random.nextDouble();
                    if (decision < 0.9) {
                        // Stay as guaranteed
                        canHaveDarkGrass = false;
                        pk.guaranteedHeldItem = possibleItems.randomItem(this.random);
                    } else {
                        // Change to 25% or 55% chance
                        pk.guaranteedHeldItem = 0;
                        pk.commonHeldItem = possibleItems.randomItem(this.random);
                        pk.rareHeldItem = possibleItems.randomItem(this.random);
                        while (pk.rareHeldItem == pk.commonHeldItem) {
                            pk.rareHeldItem = possibleItems.randomItem(this.random);
                        }
                    }
                } else {
                    // No guaranteed item atm
                    double decision = this.random.nextDouble();
                    if (decision < 0.5) {
                        // No held item at all
                        pk.commonHeldItem = 0;
                        pk.rareHeldItem = 0;
                    } else if (decision < 0.65) {
                        // Just a rare item
                        pk.commonHeldItem = 0;
                        pk.rareHeldItem = possibleItems.randomItem(this.random);
                    } else if (decision < 0.8) {
                        // Just a common item
                        pk.commonHeldItem = possibleItems.randomItem(this.random);
                        pk.rareHeldItem = 0;
                    } else if (decision < 0.95) {
                        // Both a common and rare item
                        pk.commonHeldItem = possibleItems.randomItem(this.random);
                        pk.rareHeldItem = possibleItems.randomItem(this.random);
                        while (pk.rareHeldItem == pk.commonHeldItem) {
                            pk.rareHeldItem = possibleItems.randomItem(this.random);
                        }
                    } else {
                        // Guaranteed item
                        canHaveDarkGrass = false;
                        pk.guaranteedHeldItem = possibleItems.randomItem(this.random);
                        pk.commonHeldItem = 0;
                        pk.rareHeldItem = 0;
                    }
                }
            } else {
                // Code for no guaranteed items
                double decision = this.random.nextDouble();
                if (decision < 0.5) {
                    // No held item at all
                    pk.commonHeldItem = 0;
                    pk.rareHeldItem = 0;
                } else if (decision < 0.65) {
                    // Just a rare item
                    pk.commonHeldItem = 0;
                    pk.rareHeldItem = possibleItems.randomItem(this.random);
                } else if (decision < 0.8) {
                    // Just a common item
                    pk.commonHeldItem = possibleItems.randomItem(this.random);
                    pk.rareHeldItem = 0;
                } else {
                    // Both a common and rare item
                    pk.commonHeldItem = possibleItems.randomItem(this.random);
                    pk.rareHeldItem = possibleItems.randomItem(this.random);
                    while (pk.rareHeldItem == pk.commonHeldItem) {
                        pk.rareHeldItem = possibleItems.randomItem(this.random);
                    }
                }
            }

            if (canHaveDarkGrass) {
                double dgDecision = this.random.nextDouble();
                if (dgDecision < 0.5) {
                    // Yes, dark grass item
                    pk.darkGrassHeldItem = possibleItems.randomItem(this.random);
                } else {
                    pk.darkGrassHeldItem = 0;
                }
            } else if (pk.darkGrassHeldItem != -1) {
                pk.darkGrassHeldItem = 0;
            }
        }

    }

    @Override
    public void randomizeStarterHeldItems(boolean banBadItems) {
        List<Integer> oldHeldItems = this.getStarterHeldItems();
        List<Integer> newHeldItems = new ArrayList<Integer>();
        ItemList possibleItems = banBadItems ? this.getNonBadItems() : this.getAllowedItems();
        for (int i = 0; i < oldHeldItems.size(); i++) {
            newHeldItems.add(possibleItems.randomItem(this.random));
        }
        this.setStarterHeldItems(newHeldItems);
    }

    @Override
    public void shuffleFieldItems() {
        List<Integer> currentItems = this.getRegularFieldItems();
        List<Integer> currentTMs = this.getCurrentFieldTMs();

        Collections.shuffle(currentItems, this.random);
        Collections.shuffle(currentTMs, this.random);

        this.setRegularFieldItems(currentItems);
        this.setFieldTMs(currentTMs);
    }

    @Override
    public void randomizeFieldItems(boolean banBadItems) {
        ItemList possibleItems = banBadItems ? this.getNonBadItems() : this.getAllowedItems();
        List<Integer> currentItems = this.getRegularFieldItems();
        List<Integer> currentTMs = this.getCurrentFieldTMs();
        List<Integer> requiredTMs = this.getRequiredFieldTMs();

        int fieldItemCount = currentItems.size();
        int fieldTMCount = currentTMs.size();
        int reqTMCount = requiredTMs.size();
        int totalTMCount = this.getTMCount();

        List<Integer> newItems = new ArrayList<Integer>();
        List<Integer> newTMs = new ArrayList<Integer>();

        for (int i = 0; i < fieldItemCount; i++) {
            newItems.add(possibleItems.randomNonTM(this.random));
        }

        newTMs.addAll(requiredTMs);

        for (int i = reqTMCount; i < fieldTMCount; i++) {
            while (true) {
                int tm = this.random.nextInt(totalTMCount) + 1;
                if (!newTMs.contains(tm)) {
                    newTMs.add(tm);
                    break;
                }
            }
        }

        Collections.shuffle(newItems, this.random);
        Collections.shuffle(newTMs, this.random);

        this.setRegularFieldItems(newItems);
        this.setFieldTMs(newTMs);
    }

    @Override
    public void randomizeIngameTrades(boolean randomizeRequest, boolean randomNickname, boolean randomOT,
            boolean randomStats, boolean randomItem, CustomNamesSet customNames) {
        checkPokemonRestrictions();
        // Process trainer names
        List<String> trainerNames = new ArrayList<String>();
        // Check for the file
        if (randomOT) {
            int maxOT = this.maxTradeOTNameLength();
            for (String trainername : customNames.getTrainerNames()) {
                int len = this.internalStringLength(trainername);
                if (len <= maxOT && !trainerNames.contains(trainername)) {
                    trainerNames.add(trainername);
                }
            }
        }

        // Process nicknames
        List<String> nicknames = new ArrayList<String>();
        // Check for the file
        if (randomNickname) {
            int maxNN = this.maxTradeNicknameLength();
            for (String nickname : customNames.getPokemonNicknames()) {
                int len = this.internalStringLength(nickname);
                if (len <= maxNN && !nicknames.contains(nickname)) {
                    nicknames.add(nickname);
                }
            }
        }

        // get old trades
        List<IngameTrade> trades = this.getIngameTrades();
        List<Pokemon> usedRequests = new ArrayList<Pokemon>();
        List<Pokemon> usedGivens = new ArrayList<Pokemon>();
        List<String> usedOTs = new ArrayList<String>();
        List<String> usedNicknames = new ArrayList<String>();
        ItemList possibleItems = this.getAllowedItems();

        int nickCount = nicknames.size();
        int trnameCount = trainerNames.size();

        for (IngameTrade trade : trades) {
            // pick new given pokemon
            Pokemon oldgiven = trade.givenPokemon;
            Pokemon given = this.randomPokemon();
            while (usedGivens.contains(given)) {
                given = this.randomPokemon();
            }
            usedGivens.add(given);
            trade.givenPokemon = given;

            // requested pokemon?
            if (oldgiven == trade.requestedPokemon) {
                // preserve trades for the same pokemon
                trade.requestedPokemon = given;
            } else if (randomizeRequest) {
                Pokemon request = this.randomPokemon();
                while (usedRequests.contains(request) || request == given) {
                    request = this.randomPokemon();
                }
                usedRequests.add(request);
                trade.requestedPokemon = request;
            }

            // nickname?
            if (randomNickname && nickCount > usedNicknames.size()) {
                String nickname = nicknames.get(this.random.nextInt(nickCount));
                while (usedNicknames.contains(nickname)) {
                    nickname = nicknames.get(this.random.nextInt(nickCount));
                }
                usedNicknames.add(nickname);
                trade.nickname = nickname;
            } else if (trade.nickname.equalsIgnoreCase(oldgiven.name)) {
                // change the name for sanity
                trade.nickname = trade.givenPokemon.name;
            }

            if (randomOT && trnameCount > usedOTs.size()) {
                String ot = trainerNames.get(this.random.nextInt(trnameCount));
                while (usedOTs.contains(ot)) {
                    ot = trainerNames.get(this.random.nextInt(trnameCount));
                }
                usedOTs.add(ot);
                trade.otName = ot;
                trade.otId = this.random.nextInt(65536);
            }

            if (randomStats) {
                int maxIV = this.hasDVs() ? 16 : 32;
                for (int i = 0; i < trade.ivs.length; i++) {
                    trade.ivs[i] = this.random.nextInt(maxIV);
                }
            }

            if (randomItem) {
                trade.item = possibleItems.randomItem(this.random);
            }
        }

        // things that the game doesn't support should just be ignored
        this.setIngameTrades(trades);
    }

    @Override
    public void condenseLevelEvolutions(int maxLevel, int maxIntermediateLevel) {
        List<Pokemon> allPokemon = this.getPokemon();
        Set<Evolution> changedEvos = new TreeSet<Evolution>();
        // search for level evolutions
        for (Pokemon pk : allPokemon) {
            if (pk != null) {
                for (Evolution checkEvo : pk.evolutionsFrom) {
                    if (checkEvo.type.usesLevel()) {
                        // bring down the level of this evo if it exceeds max
                        // level
                        if (checkEvo.extraInfo > maxLevel) {
                            checkEvo.extraInfo = maxLevel;
                            changedEvos.add(checkEvo);
                        }
                        // Now, seperately, if an intermediate level evo is too
                        // high, bring it down
                        for (Evolution otherEvo : pk.evolutionsTo) {
                            if (otherEvo.type.usesLevel() && otherEvo.extraInfo > maxIntermediateLevel) {
                                otherEvo.extraInfo = maxIntermediateLevel;
                                changedEvos.add(otherEvo);
                            }
                        }
                    }
                }
            }
        }
        // Log changes now that we're done (to avoid repeats)
        log("--Condensed Level Evolutions--");
        for (Evolution evol : changedEvos) {
            log(String.format("%s now evolves into %s at minimum level %d", evol.from.name, evol.to.name,
                    evol.extraInfo));
        }
        logBlankLine();

    }

    @Override
    public void randomizeEvolutions(boolean similarStrength, boolean sameType, boolean limitToThreeStages,
            boolean forceChange) {
        checkPokemonRestrictions();
        List<Pokemon> pokemonPool = new ArrayList<Pokemon>(mainPokemonList);
        int stageLimit = limitToThreeStages ? 3 : 10;

        // Cache old evolutions for data later
        Map<Pokemon, List<Evolution>> originalEvos = new HashMap<Pokemon, List<Evolution>>();
        for (Pokemon pk : pokemonPool) {
            originalEvos.put(pk, new ArrayList<Evolution>(pk.evolutionsFrom));
        }

        Set<EvolutionPair> newEvoPairs = new HashSet<EvolutionPair>();
        Set<EvolutionPair> oldEvoPairs = new HashSet<EvolutionPair>();

        if (forceChange) {
            for (Pokemon pk : pokemonPool) {
                for (Evolution ev : pk.evolutionsFrom) {
                    oldEvoPairs.add(new EvolutionPair(ev.from, ev.to));
                }
            }
        }

        List<Pokemon> replacements = new ArrayList<Pokemon>();

        int loops = 0;
        while (loops < 1) {
            // Setup for this loop.
            boolean hadError = false;
            for (Pokemon pk : pokemonPool) {
                pk.evolutionsFrom.clear();
                pk.evolutionsTo.clear();
            }
            newEvoPairs.clear();

            // Shuffle pokemon list so the results aren't overly predictable.
            Collections.shuffle(pokemonPool, this.random);

            for (Pokemon fromPK : pokemonPool) {
                List<Evolution> oldEvos = originalEvos.get(fromPK);
                for (Evolution ev : oldEvos) {
                    // Pick a Pokemon as replacement
                    replacements.clear();

                    // Step 1: base filters
                    for (Pokemon pk : mainPokemonList) {
                        // Prevent evolving into oneself (mandatory)
                        if (pk == fromPK) {
                            continue;
                        }

                        // Force same EXP curve (mandatory)
                        if (pk.growthCurve != fromPK.growthCurve) {
                            continue;
                        }

                        EvolutionPair ep = new EvolutionPair(fromPK, pk);
                        // Prevent split evos choosing the same Pokemon
                        // (mandatory)
                        if (newEvoPairs.contains(ep)) {
                            continue;
                        }

                        // Prevent evolving into old thing if flagged
                        if (forceChange && oldEvoPairs.contains(ep)) {
                            continue;
                        }

                        // Prevent evolution that causes cycle (mandatory)
                        if (evoCycleCheck(fromPK, pk)) {
                            continue;
                        }

                        // Prevent evolution that exceeds stage limit
                        Evolution tempEvo = new Evolution(fromPK, pk, false, EvolutionType.NONE, 0);
                        fromPK.evolutionsFrom.add(tempEvo);
                        pk.evolutionsTo.add(tempEvo);
                        boolean exceededLimit = false;

                        Set<Pokemon> related = relatedPokemon(fromPK);

                        for (Pokemon pk2 : related) {
                            int numPreEvos = numPreEvolutions(pk2, stageLimit);
                            if (numPreEvos >= stageLimit) {
                                exceededLimit = true;
                                break;
                            } else if (numPreEvos == stageLimit - 1 && pk2.evolutionsFrom.size() == 0
                                    && originalEvos.get(pk2).size() > 0) {
                                exceededLimit = true;
                                break;
                            }
                        }

                        fromPK.evolutionsFrom.remove(tempEvo);
                        pk.evolutionsTo.remove(tempEvo);

                        if (exceededLimit) {
                            continue;
                        }

                        // Passes everything, add as a candidate.
                        replacements.add(pk);
                    }

                    // If we don't have any candidates after Step 1, severe
                    // failure
                    // exit out of this loop and try again from scratch
                    if (replacements.size() == 0) {
                        hadError = true;
                        break;
                    }

                    // Step 2: filter by type, if needed
                    if (replacements.size() > 1 && sameType) {
                        Set<Pokemon> includeType = new HashSet<Pokemon>();
                        for (Pokemon pk : replacements) {
                            if (pk.primaryType == fromPK.primaryType
                                    || (fromPK.secondaryType != null && pk.primaryType == fromPK.secondaryType)
                                    || (pk.secondaryType != null && pk.secondaryType == fromPK.primaryType)
                                    || (fromPK.secondaryType != null && pk.secondaryType != null && pk.secondaryType == fromPK.secondaryType)) {
                                includeType.add(pk);
                            }
                        }

                        if (includeType.size() != 0) {
                            replacements.retainAll(includeType);
                        }
                    }

                    // Step 3: pick - by similar strength or otherwise
                    Pokemon picked = null;

                    if (replacements.size() == 1) {
                        // Foregone conclusion.
                        picked = replacements.get(0);
                    } else if (similarStrength) {
                        picked = pickEvoPowerLvlReplacement(replacements, ev.to);
                    } else {
                        picked = replacements.get(this.random.nextInt(replacements.size()));
                    }

                    // Step 4: add it to the new evos pool
                    Evolution newEvo = new Evolution(fromPK, picked, ev.carryStats, ev.type, ev.extraInfo);
                    fromPK.evolutionsFrom.add(newEvo);
                    picked.evolutionsTo.add(newEvo);
                    newEvoPairs.add(new EvolutionPair(fromPK, picked));
                }

                if (hadError) {
                    // No need to check the other Pokemon if we already errored
                    break;
                }
            }

            // If no error, done and return
            if (!hadError) {
                return;
            } else {
                loops++;
            }
        }

        // If we made it out of the loop, we weren't able to randomize evos.
        throw new RandomizationException("Not able to randomize evolutions in a sane amount of retries.");
    }

    @Override
    public void minimumCatchRate(int rateNonLegendary, int rateLegendary) {
        List<Pokemon> pokes = getPokemon();
        for (Pokemon pkmn : pokes) {
            if (pkmn == null) {
                continue;
            }
            int minCatchRate = pkmn.isLegendary() ? rateLegendary : rateNonLegendary;
            pkmn.catchRate = Math.max(pkmn.catchRate, minCatchRate);
        }

    }

    @Override
    public void standardizeEXPCurves() {
        List<Pokemon> pokes = getPokemon();
        for (Pokemon pkmn : pokes) {
            if (pkmn == null) {
                continue;
            }
            pkmn.growthCurve = pkmn.isLegendary() ? ExpCurve.SLOW : ExpCurve.MEDIUM_FAST;
        }
    }

    /* Private methods/structs used internally by the above methods */

    private void updateMovePower(List<Move> moves, int moveNum, int power) {
        Move mv = moves.get(moveNum);
        if (mv.power != power) {
            mv.power = power;
            addMoveUpdate(moveNum, 0);
        }
    }

    private void updateMovePP(List<Move> moves, int moveNum, int pp) {
        Move mv = moves.get(moveNum);
        if (mv.pp != pp) {
            mv.pp = pp;
            addMoveUpdate(moveNum, 1);
        }
    }

    private void updateMoveAccuracy(List<Move> moves, int moveNum, int accuracy) {
        Move mv = moves.get(moveNum);
        if (Math.abs(mv.hitratio - accuracy) >= 1) {
            mv.hitratio = accuracy;
            addMoveUpdate(moveNum, 2);
        }
    }

    private void updateMoveType(List<Move> moves, int moveNum, Type type) {
        Move mv = moves.get(moveNum);
        if (mv.type != type) {
            mv.type = type;
            addMoveUpdate(moveNum, 3);
        }
    }

    private void addMoveUpdate(int moveNum, int updateType) {
        if (!moveUpdates.containsKey(moveNum)) {
            boolean[] updateField = new boolean[4];
            updateField[updateType] = true;
            moveUpdates.put(moveNum, updateField);
        } else {
            moveUpdates.get(moveNum)[updateType] = true;
        }
    }

    private Pokemon pickEvoPowerLvlReplacement(List<Pokemon> pokemonPool, Pokemon current) {
        // start with within 10% and add 5% either direction till we find
        // something
        int currentBST = current.bstForPowerLevels();
        int minTarget = currentBST - currentBST / 10;
        int maxTarget = currentBST + currentBST / 10;
        List<Pokemon> canPick = new ArrayList<Pokemon>();
        int expandRounds = 0;
        while (canPick.isEmpty() || (canPick.size() < 3 && expandRounds < 3)) {
            for (Pokemon pk : pokemonPool) {
                if (pk.bstForPowerLevels() >= minTarget && pk.bstForPowerLevels() <= maxTarget && !canPick.contains(pk)) {
                    canPick.add(pk);
                }
            }
            minTarget -= currentBST / 20;
            maxTarget += currentBST / 20;
            expandRounds++;
        }
        return canPick.get(this.random.nextInt(canPick.size()));
    }

    private static class EvolutionPair {
        private Pokemon from;
        private Pokemon to;

        public EvolutionPair(Pokemon from, Pokemon to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((from == null) ? 0 : from.hashCode());
            result = prime * result + ((to == null) ? 0 : to.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            EvolutionPair other = (EvolutionPair) obj;
            if (from == null) {
                if (other.from != null)
                    return false;
            } else if (!from.equals(other.from))
                return false;
            if (to == null) {
                if (other.to != null)
                    return false;
            } else if (!to.equals(other.to))
                return false;
            return true;
        }
    }

    /**
     * Check whether adding an evolution from one Pokemon to another will cause
     * an evolution cycle.
     * 
     * @param from
     * @param to
     * @param newEvos
     * @return
     */
    private boolean evoCycleCheck(Pokemon from, Pokemon to) {
        Evolution tempEvo = new Evolution(from, to, false, EvolutionType.NONE, 0);
        from.evolutionsFrom.add(tempEvo);
        Set<Pokemon> visited = new HashSet<Pokemon>();
        Set<Pokemon> recStack = new HashSet<Pokemon>();
        boolean recur = isCyclic(from, visited, recStack);
        from.evolutionsFrom.remove(tempEvo);
        return recur;
    }

    private boolean isCyclic(Pokemon pk, Set<Pokemon> visited, Set<Pokemon> recStack) {
        if (!visited.contains(pk)) {
            visited.add(pk);
            recStack.add(pk);
            for (Evolution ev : pk.evolutionsFrom) {
                if (!visited.contains(ev.to) && isCyclic(ev.to, visited, recStack)) {
                    return true;
                } else if (recStack.contains(ev.to)) {
                    return true;
                }
            }
        }
        recStack.remove(pk);
        return false;
    }

    private interface BasePokemonAction {
        public void applyTo(Pokemon pk);
    }

    private interface EvolvedPokemonAction {
        public void applyTo(Pokemon evFrom, Pokemon evTo, boolean toMonIsFinalEvo);
    }

    /**
     * Universal implementation for things that have "copy X up evolutions"
     * support.
     * 
     * @param bpAction
     *            Method to run on all base or no-copy Pokemon
     * @param epAction
     *            Method to run on all evolved Pokemon with a linear chain of
     *            single evolutions.
     */
    private void copyUpEvolutionsHelper(BasePokemonAction bpAction, EvolvedPokemonAction epAction) {
        List<Pokemon> allPokes = this.getPokemon();
        for (Pokemon pk : allPokes) {
            if (pk != null) {
                pk.temporaryFlag = false;
            }
        }

        // Get evolution data.
        Set<Pokemon> dontCopyPokes = RomFunctions.getBasicOrNoCopyPokemon(this);
        Set<Pokemon> middleEvos = RomFunctions.getMiddleEvolutions(this);

        for (Pokemon pk : dontCopyPokes) {
            bpAction.applyTo(pk);
            pk.temporaryFlag = true;
        }

        // go "up" evolutions looking for pre-evos to do first
        for (Pokemon pk : allPokes) {
            if (pk != null && !pk.temporaryFlag) {
                // Non-randomized pokes at this point must have
                // a linear chain of single evolutions down to
                // a randomized poke.
                Stack<Evolution> currentStack = new Stack<Evolution>();
                Evolution ev = pk.evolutionsTo.get(0);
                while (!ev.from.temporaryFlag) {
                    currentStack.push(ev);
                    ev = ev.from.evolutionsTo.get(0);
                }

                // Now "ev" is set to an evolution from a Pokemon that has had
                // the base action done on it to one that hasn't.
                // Do the evolution action for everything left on the stack.
                epAction.applyTo(ev.from, ev.to, !middleEvos.contains(ev.to));
                ev.to.temporaryFlag = true;
                while (!currentStack.isEmpty()) {
                    ev = currentStack.pop();
                    epAction.applyTo(ev.from, ev.to, !middleEvos.contains(ev.to));
                    ev.to.temporaryFlag = true;
                }
            }
        }
    }

    private boolean checkForUnusedMove(List<Move> potentialList, Set<Integer> alreadyUsed) {
        for (Move mv : potentialList) {
            if (!alreadyUsed.contains(mv.number)) {
                return true;
            }
        }
        return false;
    }

    private List<Pokemon> pokemonOfType(Type type, boolean noLegendaries) {
        List<Pokemon> typedPokes = new ArrayList<Pokemon>();
        for (Pokemon pk : mainPokemonList) {
            if (pk != null && (!noLegendaries || !pk.isLegendary())) {
                if (pk.primaryType == type || pk.secondaryType == type) {
                    typedPokes.add(pk);
                }
            }
        }
        return typedPokes;
    }

    private List<Pokemon> allPokemonWithoutNull() {
        List<Pokemon> allPokes = new ArrayList<Pokemon>(this.getPokemon());
        allPokes.remove(0);
        return allPokes;
    }

    private Set<Pokemon> pokemonInArea(EncounterSet area) {
        Set<Pokemon> inArea = new TreeSet<Pokemon>();
        for (Encounter enc : area.encounters) {
            inArea.add(enc.pokemon);
        }
        return inArea;
    }

    private Map<Type, Integer> typeWeightings;
    private int totalTypeWeighting;

    private Type pickType(boolean weightByFrequency, boolean noLegendaries) {
        if (totalTypeWeighting == 0) {
            // Determine weightings
            for (Type t : Type.values()) {
                if (typeInGame(t)) {
                    int pkWithTyping = pokemonOfType(t, noLegendaries).size();
                    typeWeightings.put(t, pkWithTyping);
                    totalTypeWeighting += pkWithTyping;
                }
            }
        }

        if (weightByFrequency) {
            int typePick = this.random.nextInt(totalTypeWeighting);
            int typePos = 0;
            for (Type t : typeWeightings.keySet()) {
                int weight = typeWeightings.get(t);
                if (typePos + weight > typePick) {
                    return t;
                }
                typePos += weight;
            }
            return null;
        } else {
            return randomType();
        }
    }

    private void rivalCarriesStarterUpdate(List<Trainer> currentTrainers, String prefix, int pokemonOffset) {
        // Find the highest rival battle #
        int highestRivalNum = 0;
        for (Trainer t : currentTrainers) {
            if (t.tag != null && t.tag.startsWith(prefix)) {
                highestRivalNum = Math.max(highestRivalNum,
                        Integer.parseInt(t.tag.substring(prefix.length(), t.tag.indexOf('-'))));
            }
        }

        if (highestRivalNum == 0) {
            // This rival type not used in this game
            return;
        }

        // Get the starters
        // us 0 1 2 => them 0+n 1+n 2+n
        List<Pokemon> starters = this.getStarters();

        // Yellow needs its own case, unfortunately.
        if (isYellow()) {
            // The rival's starter is index 1
            Pokemon rivalStarter = starters.get(1);
            int timesEvolves = numEvolutions(rivalStarter, 2);
            // Apply evolutions as appropriate
            if (timesEvolves == 0) {
                for (int j = 1; j <= 3; j++) {
                    changeStarterWithTag(currentTrainers, prefix + j + "-0", rivalStarter);
                }
                for (int j = 4; j <= 7; j++) {
                    for (int i = 0; i < 3; i++) {
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, rivalStarter);
                    }
                }
            } else if (timesEvolves == 1) {
                for (int j = 1; j <= 3; j++) {
                    changeStarterWithTag(currentTrainers, prefix + j + "-0", rivalStarter);
                }
                rivalStarter = pickRandomEvolutionOf(rivalStarter, false);
                for (int j = 4; j <= 7; j++) {
                    for (int i = 0; i < 3; i++) {
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, rivalStarter);
                    }
                }
            } else if (timesEvolves == 2) {
                for (int j = 1; j <= 2; j++) {
                    changeStarterWithTag(currentTrainers, prefix + j + "-" + 0, rivalStarter);
                }
                rivalStarter = pickRandomEvolutionOf(rivalStarter, true);
                changeStarterWithTag(currentTrainers, prefix + "3-0", rivalStarter);
                for (int i = 0; i < 3; i++) {
                    changeStarterWithTag(currentTrainers, prefix + "4-" + i, rivalStarter);
                }
                rivalStarter = pickRandomEvolutionOf(rivalStarter, false);
                for (int j = 5; j <= 7; j++) {
                    for (int i = 0; i < 3; i++) {
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, rivalStarter);
                    }
                }
            }
        } else {
            // Replace each starter as appropriate
            // Use level to determine when to evolve, not number anymore
            for (int i = 0; i < 3; i++) {
                // Rival's starters are pokemonOffset over from each of ours
                int starterToUse = (i + pokemonOffset) % 3;
                Pokemon thisStarter = starters.get(starterToUse);
                int timesEvolves = numEvolutions(thisStarter, 2);
                // If a fully evolved pokemon, use throughout
                // Otherwise split by evolutions as appropriate
                if (timesEvolves == 0) {
                    for (int j = 1; j <= highestRivalNum; j++) {
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, thisStarter);
                    }
                } else if (timesEvolves == 1) {
                    int j = 1;
                    for (; j <= highestRivalNum / 2; j++) {
                        if (getLevelOfStarter(currentTrainers, prefix + j + "-" + i) >= 30) {
                            break;
                        }
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, thisStarter);
                    }
                    thisStarter = pickRandomEvolutionOf(thisStarter, false);
                    for (; j <= highestRivalNum; j++) {
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, thisStarter);
                    }
                } else if (timesEvolves == 2) {
                    int j = 1;
                    for (; j <= highestRivalNum; j++) {
                        if (getLevelOfStarter(currentTrainers, prefix + j + "-" + i) >= 16) {
                            break;
                        }
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, thisStarter);
                    }
                    thisStarter = pickRandomEvolutionOf(thisStarter, true);
                    for (; j <= highestRivalNum; j++) {
                        if (getLevelOfStarter(currentTrainers, prefix + j + "-" + i) >= 36) {
                            break;
                        }
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, thisStarter);
                    }
                    thisStarter = pickRandomEvolutionOf(thisStarter, false);
                    for (; j <= highestRivalNum; j++) {
                        changeStarterWithTag(currentTrainers, prefix + j + "-" + i, thisStarter);
                    }
                }
            }
        }

    }

    private Pokemon pickRandomEvolutionOf(Pokemon base, boolean mustEvolveItself) {
        // Used for "rival carries starter"
        // Pick a random evolution of base Pokemon, subject to
        // "must evolve itself" if appropriate.
        List<Pokemon> candidates = new ArrayList<Pokemon>();
        for (Evolution ev : base.evolutionsFrom) {
            if (!mustEvolveItself || ev.to.evolutionsFrom.size() > 0) {
                candidates.add(ev.to);
            }
        }

        if (candidates.size() == 0) {
            throw new RandomizationException("Random evolution called on a Pokemon without any usable evolutions.");
        }

        return candidates.get(random.nextInt(candidates.size()));
    }

    private int getLevelOfStarter(List<Trainer> currentTrainers, String tag) {
        for (Trainer t : currentTrainers) {
            if (t.tag != null && t.tag.equals(tag)) {
                // Bingo, get highest level
                // last pokemon is given priority +2 but equal priority
                // = first pokemon wins, so its effectively +1
                // If it's tagged the same we can assume it's the same team
                // just the opposite gender or something like that...
                // So no need to check other trainers with same tag.
                int highestLevel = t.pokemon.get(0).level;
                int trainerPkmnCount = t.pokemon.size();
                for (int i = 1; i < trainerPkmnCount; i++) {
                    int levelBonus = (i == trainerPkmnCount - 1) ? 2 : 0;
                    if (t.pokemon.get(i).level + levelBonus > highestLevel) {
                        highestLevel = t.pokemon.get(i).level;
                    }
                }
                return highestLevel;
            }
        }
        return 0;
    }

    private void changeStarterWithTag(List<Trainer> currentTrainers, String tag, Pokemon starter) {
        for (Trainer t : currentTrainers) {
            if (t.tag != null && t.tag.equals(tag)) {
                // Bingo
                // Change the highest level pokemon, not the last.
                // BUT: last gets +2 lvl priority (effectively +1)
                // same as above, equal priority = earlier wins
                TrainerPokemon bestPoke = t.pokemon.get(0);
                int trainerPkmnCount = t.pokemon.size();
                for (int i = 1; i < trainerPkmnCount; i++) {
                    int levelBonus = (i == trainerPkmnCount - 1) ? 2 : 0;
                    if (t.pokemon.get(i).level + levelBonus > bestPoke.level) {
                        bestPoke = t.pokemon.get(i);
                    }
                }
                bestPoke.pokemon = starter;
                bestPoke.resetMoves = true;
            }
        }

    }

    // Return the max depth of pre-evolutions a Pokemon has
    private int numPreEvolutions(Pokemon pk, int maxInterested) {
        return numPreEvolutions(pk, 0, maxInterested);
    }

    private int numPreEvolutions(Pokemon pk, int depth, int maxInterested) {
        if (pk.evolutionsTo.size() == 0) {
            return 0;
        } else {
            if (depth == maxInterested - 1) {
                return 1;
            } else {
                int maxPreEvos = 0;
                for (Evolution ev : pk.evolutionsTo) {
                    maxPreEvos = Math.max(maxPreEvos, numPreEvolutions(ev.from, depth + 1, maxInterested) + 1);
                }
                return maxPreEvos;
            }
        }
    }

    private int numEvolutions(Pokemon pk, int maxInterested) {
        return numEvolutions(pk, 0, maxInterested);
    }

    private int numEvolutions(Pokemon pk, int depth, int maxInterested) {
        if (pk.evolutionsFrom.size() == 0) {
            return 0;
        } else {
            if (depth == maxInterested - 1) {
                return 1;
            } else {
                int maxEvos = 0;
                for (Evolution ev : pk.evolutionsFrom) {
                    maxEvos = Math.max(maxEvos, numEvolutions(ev.to, depth + 1, maxInterested) + 1);
                }
                return maxEvos;
            }
        }
    }

    private Pokemon fullyEvolve(Pokemon pokemon) {
        Set<Pokemon> seenMons = new HashSet<Pokemon>();
        seenMons.add(pokemon);

        while (true) {
            if (pokemon.evolutionsFrom.size() == 0) {
                // fully evolved
                break;
            }

            // check for cyclic evolutions from what we've already seen
            boolean cyclic = false;
            for (Evolution ev : pokemon.evolutionsFrom) {
                if (seenMons.contains(ev.to)) {
                    // cyclic evolution detected - bail now
                    cyclic = true;
                    break;
                }
            }

            if (cyclic) {
                break;
            }

            // pick a random evolution to continue from
            pokemon = pokemon.evolutionsFrom.get(random.nextInt(pokemon.evolutionsFrom.size())).to;
            seenMons.add(pokemon);
        }

        return pokemon;
    }

    private Set<Pokemon> relatedPokemon(Pokemon original) {
        Set<Pokemon> results = new HashSet<Pokemon>();
        results.add(original);
        Queue<Pokemon> toCheck = new LinkedList<Pokemon>();
        toCheck.add(original);
        while (!toCheck.isEmpty()) {
            Pokemon check = toCheck.poll();
            for (Evolution ev : check.evolutionsFrom) {
                if (!results.contains(ev.to)) {
                    results.add(ev.to);
                    toCheck.add(ev.to);
                }
            }
            for (Evolution ev : check.evolutionsTo) {
                if (!results.contains(ev.from)) {
                    results.add(ev.from);
                    toCheck.add(ev.from);
                }
            }
        }
        return results;
    }

    private Map<Type, List<Pokemon>> cachedReplacementLists;
    private List<Pokemon> cachedAllList;

    private Pokemon pickReplacement(Pokemon current, boolean usePowerLevels, Type type, boolean noLegendaries,
            boolean wonderGuardAllowed) {
        List<Pokemon> pickFrom = cachedAllList;
        if (type != null) {
            if (!cachedReplacementLists.containsKey(type)) {
                cachedReplacementLists.put(type, pokemonOfType(type, noLegendaries));
            }
            pickFrom = cachedReplacementLists.get(type);
        }

        if (usePowerLevels) {
            // start with within 10% and add 5% either direction till we find
            // something
            int currentBST = current.bstForPowerLevels();
            int minTarget = currentBST - currentBST / 10;
            int maxTarget = currentBST + currentBST / 10;
            List<Pokemon> canPick = new ArrayList<Pokemon>();
            int expandRounds = 0;
            while (canPick.isEmpty() || (canPick.size() < 3 && expandRounds < 2)) {
                for (Pokemon pk : pickFrom) {
                    if (pk.bstForPowerLevels() >= minTarget
                            && pk.bstForPowerLevels() <= maxTarget
                            && (wonderGuardAllowed || (pk.ability1 != GlobalConstants.WONDER_GUARD_INDEX
                                    && pk.ability2 != GlobalConstants.WONDER_GUARD_INDEX && pk.ability3 != GlobalConstants.WONDER_GUARD_INDEX))) {
                        canPick.add(pk);
                    }
                }
                minTarget -= currentBST / 20;
                maxTarget += currentBST / 20;
                expandRounds++;
            }
            return canPick.get(this.random.nextInt(canPick.size()));
        } else {
            if (wonderGuardAllowed) {
                return pickFrom.get(this.random.nextInt(pickFrom.size()));
            } else {
                Pokemon pk = pickFrom.get(this.random.nextInt(pickFrom.size()));
                while (pk.ability1 == GlobalConstants.WONDER_GUARD_INDEX
                        || pk.ability2 == GlobalConstants.WONDER_GUARD_INDEX
                        || pk.ability3 == GlobalConstants.WONDER_GUARD_INDEX) {
                    pk = pickFrom.get(this.random.nextInt(pickFrom.size()));
                }
                return pk;
            }
        }
    }

    private Pokemon pickWildPowerLvlReplacement(List<Pokemon> pokemonPool, Pokemon current, boolean banSamePokemon,
            List<Pokemon> usedUp) {
        // start with within 10% and add 5% either direction till we find
        // something
        int currentBST = current.bstForPowerLevels();
        int minTarget = currentBST - currentBST / 10;
        int maxTarget = currentBST + currentBST / 10;
        List<Pokemon> canPick = new ArrayList<Pokemon>();
        int expandRounds = 0;
        while (canPick.isEmpty() || (canPick.size() < 3 && expandRounds < 3)) {
            for (Pokemon pk : pokemonPool) {
                if (pk.bstForPowerLevels() >= minTarget && pk.bstForPowerLevels() <= maxTarget
                        && (!banSamePokemon || pk != current) && (usedUp == null || !usedUp.contains(pk))
                        && !canPick.contains(pk)) {
                    canPick.add(pk);
                }
            }
            minTarget -= currentBST / 20;
            maxTarget += currentBST / 20;
            expandRounds++;
        }
        return canPick.get(this.random.nextInt(canPick.size()));
    }

    /* Helper methods used by subclasses and/or this class */

    protected void checkPokemonRestrictions() {
        if (!restrictionsSet) {
            setPokemonPool(null);
        }
    }

    protected void applyCamelCaseNames() {
        List<Pokemon> pokes = getPokemon();
        for (Pokemon pkmn : pokes) {
            if (pkmn == null) {
                continue;
            }
            pkmn.name = RomFunctions.camelCase(pkmn.name);
        }

    }

    protected void log(String log) {
        if (logStream != null) {
            logStream.println(log);
        }
    }

    protected void logBlankLine() {
        if (logStream != null) {
            logStream.println();
        }
    }

    protected void logEvoChangeLevel(String pkFrom, String pkTo, int level) {
        if (logStream != null) {
            logStream.printf("Made %s evolve into %s at level %d", pkFrom, pkTo, level);
            logStream.println();
        }
    }

    protected void logEvoChangeLevelWithItem(String pkFrom, String pkTo, String itemName) {
        if (logStream != null) {
            logStream.printf("Made %s evolve into %s by leveling up holding %s", pkFrom, pkTo, itemName);
            logStream.println();
        }
    }

    protected void logEvoChangeStone(String pkFrom, String pkTo, String itemName) {
        if (logStream != null) {
            logStream.printf("Made %s evolve into %s using a %s", pkFrom, pkTo, itemName);
            logStream.println();
        }
    }

    protected void logEvoChangeLevelWithPkmn(String pkFrom, String pkTo, String otherRequired) {
        if (logStream != null) {
            logStream.printf("Made %s evolve into %s by leveling up with %s in the party", pkFrom, pkTo, otherRequired);
            logStream.println();
        }
    }

    /* Default Implementations */
    /* Used when a subclass doesn't override */
    /*
     * The implication here is that these WILL be overridden by at least one
     * subclass.
     */

    @Override
    public boolean canChangeStarters() {
        return true;
    }

    @Override
    public boolean typeInGame(Type type) {
        return type.isHackOnly == false;
    }

    @Override
    public String abilityName(int number) {
        return "";
    }

    @Override
    public boolean hasTimeBasedEncounters() {
        // DEFAULT: no
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pokemon> bannedForWildEncounters() {
        return (List<Pokemon>) Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Integer> getMovesBannedFromLevelup() {
        return (List<Integer>) Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pokemon> bannedForStaticPokemon() {
        return (List<Pokemon>) Collections.EMPTY_LIST;
    }

    @Override
    public int maxTrainerNameLength() {
        // default: no real limit
        return Integer.MAX_VALUE;
    }

    @Override
    public int maxSumOfTrainerNameLengths() {
        // default: no real limit
        return Integer.MAX_VALUE;
    }

    @Override
    public int maxTrainerClassNameLength() {
        // default: no real limit
        return Integer.MAX_VALUE;
    }

    @Override
    public int maxTradeNicknameLength() {
        return 10;
    }

    @Override
    public int maxTradeOTNameLength() {
        return 7;
    }

    @Override
    public List<Integer> getGameBreakingMoves() {
        // Sonicboom & drage
        return Arrays.asList(49, 82);
    }

    @Override
    public boolean isYellow() {
        return false;
    }

    @Override
    public boolean isROMHack() {
        // override until detection implemented
        return false;
    }

    @Override
    public void writeCheckValueToROM(int value) {
        // do nothing
    }

    @Override
    public int miscTweaksAvailable() {
        // default: none
        return 0;
    }

    @Override
    public void applyMiscTweak(MiscTweak tweak) {
        // default: do nothing
    }
}
