package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  AbstractRomHandler.java - a base class for all rom handlers which		--*/
/*--							implements the majority of the actual		--*/
/*--							randomizer logic by building on the base	--*/
/*--							getters & setters provided by each concrete	--*/
/*--							handler.									--*/
/*--  																		--*/
/*--  Part of "Universal Pokemon Randomizer" by Dabomstew					--*/
/*--  Pokemon and any associated names and the like are						--*/
/*--  trademark and (C) Nintendo 1996-2012.									--*/
/*--  																		--*/
/*--  The custom code written here is licensed under the terms of the GPL:	--*/
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

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.RandomSource;
import com.dabomstew.pkrandom.RomFunctions;
import com.dabomstew.pkrandom.gui.RandomizerGUI;
import com.dabomstew.pkrandom.pokemon.Encounter;
import com.dabomstew.pkrandom.pokemon.EncounterSet;
import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.ExpCurve;
import com.dabomstew.pkrandom.pokemon.GenRestrictions;
import com.dabomstew.pkrandom.pokemon.IngameTrade;
import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.Move;
import com.dabomstew.pkrandom.pokemon.MoveLearnt;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.TrainerPokemon;
import com.dabomstew.pkrandom.pokemon.Type;

public abstract class AbstractRomHandler implements RomHandler {

	private static final String tnamesFile = "trainernames.txt";
	private static final String tclassesFile = "trainerclasses.txt";
	private static final String nnamesFile = "nicknames.txt";

	private boolean restrictionsSet;
	protected List<Pokemon> mainPokemonList;
	protected List<Pokemon> noLegendaryList, onlyLegendaryList;

	/* Constructor */

	public AbstractRomHandler() {
	}

	/* Public Methods, implemented here for all gens */

	protected void checkPokemonRestrictions() {
		if (!restrictionsSet) {
			setPokemonPool(null);
		}
	}

	public void setPokemonPool(GenRestrictions restrictions) {
		restrictionsSet = true;
		mainPokemonList = this.allPokemonWithoutNull();
		if (restrictions != null) {
			mainPokemonList = new ArrayList<Pokemon>();
			List<Pokemon> allPokemon = this.getPokemon();
			List<Evolution> evos = this.getEvolutions();

			if (restrictions.allow_gen1) {
				addPokesFromRange(mainPokemonList, allPokemon, 1, 151);
				if (restrictions.assoc_g1_g2 && allPokemon.size() > 251) {
					addEvosFromRange(mainPokemonList, allPokemon, evos, 1, 151,
							152, 251);
				}
				if (restrictions.assoc_g1_g4 && allPokemon.size() > 493) {
					addEvosFromRange(mainPokemonList, allPokemon, evos, 1, 151,
							387, 493);
				}
			}

			if (restrictions.allow_gen2 && allPokemon.size() > 251) {
				addPokesFromRange(mainPokemonList, allPokemon, 152, 251);
				if (restrictions.assoc_g2_g1) {
					addEvosFromRange(mainPokemonList, allPokemon, evos, 152,
							251, 1, 151);
				}
				if (restrictions.assoc_g2_g3 && allPokemon.size() > 386) {
					addEvosFromRange(mainPokemonList, allPokemon, evos, 152,
							251, 252, 386);
				}
				if (restrictions.assoc_g2_g4 && allPokemon.size() > 493) {
					addEvosFromRange(mainPokemonList, allPokemon, evos, 152,
							251, 387, 493);
				}
			}

			if (restrictions.allow_gen3 && allPokemon.size() > 386) {
				addPokesFromRange(mainPokemonList, allPokemon, 252, 386);
				if (restrictions.assoc_g3_g2) {
					addEvosFromRange(mainPokemonList, allPokemon, evos, 252,
							386, 152, 251);
				}
				if (restrictions.assoc_g3_g4 && allPokemon.size() > 493) {
					addEvosFromRange(mainPokemonList, allPokemon, evos, 252,
							386, 387, 493);
				}
			}

			if (restrictions.allow_gen4 && allPokemon.size() > 493) {
				addPokesFromRange(mainPokemonList, allPokemon, 387, 493);
				if (restrictions.assoc_g4_g1) {
					addEvosFromRange(mainPokemonList, allPokemon, evos, 387,
							493, 1, 151);
				}
				if (restrictions.assoc_g4_g2) {
					addEvosFromRange(mainPokemonList, allPokemon, evos, 387,
							493, 152, 251);
				}
				if (restrictions.assoc_g4_g3) {
					addEvosFromRange(mainPokemonList, allPokemon, evos, 387,
							493, 252, 386);
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

	private void addPokesFromRange(List<Pokemon> pokemonPool,
			List<Pokemon> allPokemon, int range_min, int range_max) {
		for (int i = range_min; i <= range_max; i++) {
			if (!pokemonPool.contains(allPokemon.get(i))) {
				pokemonPool.add(allPokemon.get(i));
			}
		}
	}

	private void addEvosFromRange(List<Pokemon> pokemonPool,
			List<Pokemon> allPokemon, List<Evolution> evos, int first_min,
			int first_max, int second_min, int second_max) {
		for (Evolution e : evos) {
			Pokemon potential = null;
			if ((e.from >= first_min && e.from <= first_max
					&& e.to >= second_min && e.to <= second_max)) {
				potential = allPokemon.get(e.to);
			} else if ((e.from >= second_min && e.from <= second_max
					&& e.to >= first_min && e.to <= first_max)) {
				potential = allPokemon.get(e.from);
			}
			if (potential != null && !pokemonPool.contains(potential)) {
				pokemonPool.add(potential);
			}
		}
	}

	@Override
	public void randomizePokemonStats(boolean evolutionSanity) {
		List<Pokemon> allPokes = this.getPokemon();
		List<Evolution> evolutions = this.getEvolutions();
		if (evolutionSanity) {
			// Spread stats up MOST evolutions.
			Set<Pokemon> dontCopyPokes = RomFunctions
					.getBasicOrNoCopyPokemon(this);

			for (Pokemon pk : dontCopyPokes) {
				pk.randomizeStatsWithinBST();
			}
			// go "up" evolutions looking for pre-evos to do first
			for (Evolution evo : evolutions) {
				if (evo.carryStats) {
					Stack<Evolution> currentStack = new Stack<Evolution>();
					Evolution current = evo;
					while (current != null) {
						Evolution last = current;
						currentStack.push(last);
						current = null;
						for (Evolution evo2 : evolutions) {
							if (last.from == evo2.to && evo2.carryStats) {
								current = evo2;
								break;
							}
						}
					}
					// now we have a stack of evolutions
					while (!currentStack.isEmpty()) {
						Evolution useEvo = currentStack.pop();
						useEvo.carryStats = false; // so we don't waste time
													// later
						Pokemon to = allPokes.get(useEvo.to);
						Pokemon from = allPokes.get(useEvo.from);
						to.copyRandomizedStatsUpEvolution(from);
					}
				}
			}
		} else {
			for (Pokemon pk : allPokes) {
				if (pk != null) {
					pk.randomizeStatsWithinBST();
				}
			}
		}

	}

	@Override
	public void applyCamelCaseNames() {
		List<Pokemon> pokes = getPokemon();
		for (Pokemon pkmn : pokes) {
			if (pkmn == null) {
				continue;
			}
			pkmn.name = RomFunctions.camelCase(pkmn.name);
		}

	}

	@Override
	public void minimumCatchRate(int rateNonLegendary, int rateLegendary) {
		List<Pokemon> pokes = getPokemon();
		for (Pokemon pkmn : pokes) {
			if (pkmn == null) {
				continue;
			}
			int minCatchRate = pkmn.isLegendary() ? rateLegendary
					: rateNonLegendary;
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
			pkmn.growthCurve = pkmn.isLegendary() ? ExpCurve.SLOW
					: ExpCurve.MEDIUM_FAST;
		}
	}

	@Override
	public void randomizePokemonTypes(boolean evolutionSanity) {
		if (evolutionSanity) {
			Set<Pokemon> dontCopyPokes = RomFunctions
					.getBasicOrNoCopyPokemon(this);
			// Type randomization
			// Step 1: Basic or Excluded From Copying Pokemon
			// A Basic/EFC pokemon has a 35% chance of a second type if it has
			// an evolution, a 50% chance otherwise
			for (Pokemon pk : dontCopyPokes) {
				pk.primaryType = randomType();
				pk.secondaryType = null;
				if (RomFunctions.pokemonHasEvo(this, pk)) {
					if (RandomSource.random() < 0.35) {
						pk.secondaryType = randomType();
						while (pk.secondaryType == pk.primaryType) {
							pk.secondaryType = randomType();
						}
					}
				} else {
					if (RandomSource.random() < 0.5) {
						pk.secondaryType = randomType();
						while (pk.secondaryType == pk.primaryType) {
							pk.secondaryType = randomType();
						}
					}
				}
			}
			// Step 2: First Evolutions
			// A first evolution has a 15% chance of adding a type if there's a
			// 3rd stage, Or a 25% chance if there's not
			Set<Pokemon> firstEvos = RomFunctions.getFirstEvolutions(this);
			for (Pokemon pk : firstEvos) {
				Pokemon evolvedFrom = RomFunctions.evolvesFrom(this, pk);
				pk.primaryType = evolvedFrom.primaryType;
				pk.secondaryType = evolvedFrom.secondaryType;
				if (pk.secondaryType == null) {
					if (RomFunctions.pokemonHasEvo(this, pk)) {
						if (RandomSource.random() < 0.15) {
							pk.secondaryType = randomType();
							while (pk.secondaryType == pk.primaryType) {
								pk.secondaryType = randomType();
							}
						}
					} else {
						if (RandomSource.random() < 0.25) {
							pk.secondaryType = randomType();
							while (pk.secondaryType == pk.primaryType) {
								pk.secondaryType = randomType();
							}
						}
					}
				}
			}

			// Step 3: Second Evolutions
			// A second evolution has a 25% chance of adding a type
			Set<Pokemon> secondEvos = RomFunctions.getSecondEvolutions(this);
			for (Pokemon pk : secondEvos) {
				Pokemon evolvedFrom = RomFunctions.evolvesFrom(this, pk);
				pk.primaryType = evolvedFrom.primaryType;
				pk.secondaryType = evolvedFrom.secondaryType;
				if (pk.secondaryType == null) {
					if (RandomSource.random() < 0.25) {
						pk.secondaryType = randomType();
						while (pk.secondaryType == pk.primaryType) {
							pk.secondaryType = randomType();
						}
					}
				}
			}
		} else {
			// Entirely random types
			List<Pokemon> allPokes = this.getPokemon();
			for (Pokemon pkmn : allPokes) {
				if (pkmn != null) {
					pkmn.primaryType = randomType();
					pkmn.secondaryType = null;
					if (RandomSource.random() < 0.5) {
						pkmn.secondaryType = randomType();
						while (pkmn.secondaryType == pkmn.primaryType) {
							pkmn.secondaryType = randomType();
						}
					}
				}
			}
		}
	}

	private static final int WONDER_GUARD_INDEX = 25;

	@Override
	public void randomizeAbilities(boolean allowWonderGuard) {
		// Abilities don't exist in some games...
		if (this.abilitiesPerPokemon() == 0) {
			return;
		}

		// Deal with "natural" abilities first regardless
		List<Pokemon> allPokes = this.getPokemon();
		int maxAbility = this.highestAbilityIndex();
		for (Pokemon pk : allPokes) {
			if (pk == null) {
				continue;
			}

			// Wonder Guard?
			if (pk.ability1 != WONDER_GUARD_INDEX
					&& pk.ability2 != WONDER_GUARD_INDEX
					&& pk.ability3 != WONDER_GUARD_INDEX) {
				// Pick first ability
				pk.ability1 = RandomSource.nextInt(maxAbility) + 1;
				// Wonder guard block
				if (!allowWonderGuard) {
					while (pk.ability1 == WONDER_GUARD_INDEX) {
						pk.ability1 = RandomSource.nextInt(maxAbility) + 1;
					}
				}

				// Second ability?
				if (RandomSource.nextDouble() < 0.5) {
					// Yes, second ability
					pk.ability2 = RandomSource.nextInt(maxAbility) + 1;
					// Wonder guard? Also block first ability from reappearing
					if (allowWonderGuard) {
						while (pk.ability2 == pk.ability1) {
							pk.ability2 = RandomSource.nextInt(maxAbility) + 1;
						}
					} else {
						while (pk.ability2 == WONDER_GUARD_INDEX
								|| pk.ability2 == pk.ability1) {
							pk.ability2 = RandomSource.nextInt(maxAbility) + 1;
						}
					}
				} else {
					// Nope
					pk.ability2 = 0;
				}
			}
		}

		// DW Abilities?
		if (this.abilitiesPerPokemon() == 3) {
			// Give a random DW ability to every Pokemon
			for (Pokemon pk : allPokes) {
				if (pk == null) {
					continue;
				}
				if (pk.ability1 != WONDER_GUARD_INDEX
						&& pk.ability2 != WONDER_GUARD_INDEX
						&& pk.ability3 != WONDER_GUARD_INDEX) {
					pk.ability3 = RandomSource.nextInt(maxAbility) + 1;
					// Wonder guard? Also block other abilities from reappearing
					if (allowWonderGuard) {
						while (pk.ability3 == pk.ability1
								|| pk.ability3 == pk.ability2) {
							pk.ability3 = RandomSource.nextInt(maxAbility) + 1;
						}
					} else {
						while (pk.ability3 == WONDER_GUARD_INDEX
								|| pk.ability3 == pk.ability1
								|| pk.ability3 == pk.ability2) {
							pk.ability3 = RandomSource.nextInt(maxAbility) + 1;
						}
					}
				}
			}
		}
	}

	public Pokemon randomPokemon() {
		checkPokemonRestrictions();
		return mainPokemonList
				.get(RandomSource.nextInt(mainPokemonList.size()));
	}

	@Override
	public Pokemon randomNonLegendaryPokemon() {
		checkPokemonRestrictions();
		return noLegendaryList
				.get(RandomSource.nextInt(noLegendaryList.size()));
	}

	@Override
	public Pokemon randomLegendaryPokemon() {
		checkPokemonRestrictions();
		return onlyLegendaryList.get(RandomSource.nextInt(onlyLegendaryList
				.size()));
	}

	private List<Pokemon> twoEvoPokes;

	@Override
	public Pokemon random2EvosPokemon() {
		if (twoEvoPokes == null) {
			// Prepare the list
			List<Pokemon> allPokes = this.getPokemon();
			List<Pokemon> remainingPokes = allPokemonWithoutNull();
			List<Evolution> allEvos = this.getEvolutions();
			Map<Pokemon, Pokemon> reverseKeepPokemon = new TreeMap<Pokemon, Pokemon>();
			for (Evolution e : allEvos) {
				reverseKeepPokemon
						.put(allPokes.get(e.to), allPokes.get(e.from));
			}
			remainingPokes.retainAll(reverseKeepPokemon.values());
			// All pokemon with evolutions are left
			// Look for the evolutions themselves again in the evo-list
			Set<Pokemon> keepFor2Evos = new TreeSet<Pokemon>();
			for (Evolution e : allEvos) {
				Pokemon from = allPokes.get(e.from);
				if (reverseKeepPokemon.containsKey(from)) {
					keepFor2Evos.add(reverseKeepPokemon.get(from));
				}
			}
			remainingPokes.retainAll(keepFor2Evos);
			twoEvoPokes = remainingPokes;
		}
		return twoEvoPokes.get(RandomSource.nextInt(twoEvoPokes.size()));
	}

	@Override
	public void randomEncounters(boolean useTimeOfDay, boolean catchEmAll,
			boolean typeThemed, boolean usePowerLevels, boolean noLegendaries) {
		checkPokemonRestrictions();
		List<EncounterSet> currentEncounters = this.getEncounters(useTimeOfDay);
		List<Pokemon> banned = this.bannedForWildEncounters();
		// Assume EITHER catch em all OR type themed OR match strength for now
		if (catchEmAll) {

			List<Pokemon> allPokes = noLegendaries ? new ArrayList<Pokemon>(
					noLegendaryList) : new ArrayList<Pokemon>(mainPokemonList);
			allPokes.removeAll(banned);
			for (EncounterSet area : currentEncounters) {
				for (Encounter enc : area.encounters) {
					// Pick a random pokemon
					int picked = RandomSource.nextInt(allPokes.size());
					enc.pokemon = allPokes.get(picked);
					if (area.battleTrappersBanned
							&& hasBattleTrappingAbility(enc.pokemon)) {
						// Skip past this Pokemon for now and just pick a random
						// one
						List<Pokemon> pickable = noLegendaries ? new ArrayList<Pokemon>(
								noLegendaryList) : new ArrayList<Pokemon>(
								mainPokemonList);
						pickable.removeAll(banned);
						if (pickable.size() == 0) {
							JOptionPane.showMessageDialog(null,
									"ERROR: Couldn't replace a Pokemon!");
							return;
						}
						while (hasBattleTrappingAbility(enc.pokemon)) {
							picked = RandomSource.nextInt(pickable.size());
							enc.pokemon = pickable.get(picked);
						}
					} else {
						// Picked this Pokemon, remove it
						allPokes.remove(picked);
						if (allPokes.size() == 0) {
							// Start again
							allPokes.addAll(noLegendaries ? noLegendaryList
									: mainPokemonList);
							allPokes.removeAll(banned);
						}
					}
				}
			}
		} else if (typeThemed) {
			Map<Type, List<Pokemon>> cachedPokeLists = new TreeMap<Type, List<Pokemon>>();
			for (EncounterSet area : currentEncounters) {
				Type areaTheme = randomType();
				if (!cachedPokeLists.containsKey(areaTheme)) {
					cachedPokeLists.put(areaTheme,
							pokemonOfType(areaTheme, noLegendaries));
				}
				List<Pokemon> possiblePokemon = cachedPokeLists.get(areaTheme);
				for (Encounter enc : area.encounters) {
					// Pick a random themed pokemon
					enc.pokemon = possiblePokemon.get(RandomSource
							.nextInt(possiblePokemon.size()));
					while (banned.contains(enc.pokemon)
							|| (area.battleTrappersBanned && hasBattleTrappingAbility(enc.pokemon))) {
						enc.pokemon = possiblePokemon.get(RandomSource
								.nextInt(possiblePokemon.size()));
					}
				}
			}
		} else if (usePowerLevels) {
			List<Pokemon> allowedPokes = noLegendaries ? new ArrayList<Pokemon>(
					noLegendaryList) : new ArrayList<Pokemon>(mainPokemonList);
			allowedPokes.removeAll(banned);
			for (EncounterSet area : currentEncounters) {
				for (Encounter enc : area.encounters) {
					enc.pokemon = pickWildPowerLvlReplacement(allowedPokes,
							enc.pokemon, area.battleTrappersBanned, false, null);
				}
			}
		} else {
			// Entirely random
			for (EncounterSet area : currentEncounters) {
				for (Encounter enc : area.encounters) {
					enc.pokemon = noLegendaries ? randomNonLegendaryPokemon()
							: randomPokemon();
					while (banned.contains(enc.pokemon)
							|| (area.battleTrappersBanned && hasBattleTrappingAbility(enc.pokemon))) {
						enc.pokemon = noLegendaries ? randomNonLegendaryPokemon()
								: randomPokemon();
					}
				}
			}
		}

		setEncounters(useTimeOfDay, currentEncounters);
	}

	@Override
	public void area1to1Encounters(boolean useTimeOfDay, boolean catchEmAll,
			boolean typeThemed, boolean usePowerLevels, boolean noLegendaries) {
		checkPokemonRestrictions();
		List<EncounterSet> currentEncounters = this.getEncounters(useTimeOfDay);
		List<Pokemon> banned = this.bannedForWildEncounters();
		// Assume EITHER catch em all OR type themed for now
		if (catchEmAll) {
			List<Pokemon> allPokes = noLegendaries ? new ArrayList<Pokemon>(
					noLegendaryList) : new ArrayList<Pokemon>(mainPokemonList);
			allPokes.removeAll(banned);
			for (EncounterSet area : currentEncounters) {
				// Poke-set
				Set<Pokemon> inArea = pokemonInArea(area);
				// Build area map using catch em all
				Map<Pokemon, Pokemon> areaMap = new TreeMap<Pokemon, Pokemon>();
				for (Pokemon areaPk : inArea) {
					int picked = RandomSource.nextInt(allPokes.size());
					Pokemon pickedMN = allPokes.get(picked);
					if (area.battleTrappersBanned
							&& hasBattleTrappingAbility(pickedMN)) {
						// Skip past this Pokemon for now and just pick a random
						// one
						List<Pokemon> pickable = noLegendaries ? new ArrayList<Pokemon>(
								noLegendaryList) : new ArrayList<Pokemon>(
								mainPokemonList);
						pickable.removeAll(banned);
						if (pickable.size() == 0) {
							JOptionPane.showMessageDialog(null,
									"ERROR: Couldn't replace a Pokemon!");
							return;
						}
						while (hasBattleTrappingAbility(pickedMN)) {
							picked = RandomSource.nextInt(pickable.size());
							pickedMN = pickable.get(picked);
						}
						areaMap.put(areaPk, pickedMN);
					} else {
						areaMap.put(areaPk, pickedMN);
						allPokes.remove(picked);
						if (allPokes.size() == 0) {
							// Start again
							allPokes.addAll(noLegendaries ? noLegendaryList
									: mainPokemonList);
							allPokes.removeAll(banned);
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
			for (EncounterSet area : currentEncounters) {
				Type areaTheme = randomType();
				if (!cachedPokeLists.containsKey(areaTheme)) {
					cachedPokeLists.put(areaTheme,
							pokemonOfType(areaTheme, noLegendaries));
				}
				List<Pokemon> possiblePokemon = new ArrayList<Pokemon>(
						cachedPokeLists.get(areaTheme));
				possiblePokemon.removeAll(banned);
				// Poke-set
				Set<Pokemon> inArea = pokemonInArea(area);
				// Build area map using type theme, reset the list if needed
				Map<Pokemon, Pokemon> areaMap = new TreeMap<Pokemon, Pokemon>();
				for (Pokemon areaPk : inArea) {
					int picked = RandomSource.nextInt(possiblePokemon.size());
					Pokemon pickedMN = possiblePokemon.get(picked);
					if (area.battleTrappersBanned
							&& hasBattleTrappingAbility(pickedMN)) {
						// Skip past this Pokemon for now and just pick a random
						// one
						List<Pokemon> pickable = new ArrayList<Pokemon>(
								cachedPokeLists.get(areaTheme));
						pickable.removeAll(banned);
						if (pickable.size() == 0) {
							// Try all Pokemon instead
							pickable = noLegendaries ? new ArrayList<Pokemon>(
									noLegendaryList) : new ArrayList<Pokemon>(
									mainPokemonList);
							pickable.removeAll(banned);
						}
						if (pickable.size() == 0) {
							JOptionPane.showMessageDialog(null,
									"ERROR: Couldn't replace a Pokemon!");
							return;
						}
						while (hasBattleTrappingAbility(pickedMN)) {
							picked = RandomSource.nextInt(pickable.size());
							pickedMN = pickable.get(picked);
						}
						areaMap.put(areaPk, pickedMN);
					} else {
						areaMap.put(areaPk, pickedMN);
						possiblePokemon.remove(picked);
						if (possiblePokemon.size() == 0) {
							// Start again
							possiblePokemon.addAll(cachedPokeLists
									.get(areaTheme));
							possiblePokemon.removeAll(banned);
						}
					}
				}
				for (Encounter enc : area.encounters) {
					// Apply the map
					enc.pokemon = areaMap.get(enc.pokemon);
				}
			}
		} else if (usePowerLevels) {
			List<Pokemon> allowedPokes = noLegendaries ? new ArrayList<Pokemon>(
					noLegendaryList) : new ArrayList<Pokemon>(mainPokemonList);
			allowedPokes.removeAll(banned);
			for (EncounterSet area : currentEncounters) {
				// Poke-set
				Set<Pokemon> inArea = pokemonInArea(area);
				// Build area map using randoms
				Map<Pokemon, Pokemon> areaMap = new TreeMap<Pokemon, Pokemon>();
				List<Pokemon> usedPks = new ArrayList<Pokemon>();
				for (Pokemon areaPk : inArea) {
					Pokemon picked = pickWildPowerLvlReplacement(allowedPokes,
							areaPk, area.battleTrappersBanned, false, usedPks);
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
			for (EncounterSet area : currentEncounters) {
				// Poke-set
				Set<Pokemon> inArea = pokemonInArea(area);
				// Build area map using randoms
				Map<Pokemon, Pokemon> areaMap = new TreeMap<Pokemon, Pokemon>();
				for (Pokemon areaPk : inArea) {
					Pokemon picked = noLegendaries ? randomNonLegendaryPokemon()
							: randomPokemon();
					while (areaMap.containsValue(picked)
							|| banned.contains(picked)
							|| (area.battleTrappersBanned && hasBattleTrappingAbility(picked))) {
						picked = noLegendaries ? randomNonLegendaryPokemon()
								: randomPokemon();
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
	public void game1to1Encounters(boolean useTimeOfDay,
			boolean usePowerLevels, boolean noLegendaries) {
		checkPokemonRestrictions();
		// Build the full 1-to-1 map
		Map<Pokemon, Pokemon> translateMap = new TreeMap<Pokemon, Pokemon>();
		List<Pokemon> remainingLeft = allPokemonWithoutNull();
		List<Pokemon> remainingRight = noLegendaries ? new ArrayList<Pokemon>(
				noLegendaryList) : new ArrayList<Pokemon>(mainPokemonList);
		List<Pokemon> banned = this.bannedForWildEncounters();
		// Banned pokemon should be mapped to themselves
		for (Pokemon bannedPK : banned) {
			translateMap.put(bannedPK, bannedPK);
			remainingLeft.remove(bannedPK);
			remainingRight.remove(bannedPK);
		}
		while (remainingLeft.isEmpty() == false) {
			if (usePowerLevels) {
				int pickedLeft = RandomSource.nextInt(remainingLeft.size());
				Pokemon pickedLeftP = remainingLeft.remove(pickedLeft);
				Pokemon pickedRightP = null;
				if (remainingRight.size() == 1) {
					// pick this (it may or may not be the same poke)
					pickedRightP = remainingRight.get(0);
				} else {
					// pick on power level with the current one blocked
					pickedRightP = pickWildPowerLvlReplacement(remainingRight,
							pickedLeftP, false, true, null);
				}
				remainingRight.remove(pickedRightP);
				translateMap.put(pickedLeftP, pickedRightP);
			} else {
				int pickedLeft = RandomSource.nextInt(remainingLeft.size());
				int pickedRight = RandomSource.nextInt(remainingRight.size());
				Pokemon pickedLeftP = remainingLeft.remove(pickedLeft);
				Pokemon pickedRightP = remainingRight.get(pickedRight);
				while (pickedLeftP.number == pickedRightP.number
						&& remainingRight.size() != 1) {
					// Reroll for a different pokemon if at all possible
					pickedRight = RandomSource.nextInt(remainingRight.size());
					pickedRightP = remainingRight.get(pickedRight);
				}
				remainingRight.remove(pickedRight);
				translateMap.put(pickedLeftP, pickedRightP);
			}
			if (remainingRight.size() == 0) {
				// restart
				remainingRight.addAll(noLegendaries ? noLegendaryList
						: mainPokemonList);
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
				if (area.battleTrappersBanned
						&& hasBattleTrappingAbility(enc.pokemon)) {
					// Ignore the map and put a random non-trapping Poke
					List<Pokemon> pickable = noLegendaries ? new ArrayList<Pokemon>(
							noLegendaryList) : new ArrayList<Pokemon>(
							mainPokemonList);
					pickable.removeAll(banned);
					if (pickable.size() == 0) {
						JOptionPane.showMessageDialog(null,
								"ERROR: Couldn't replace a Pokemon!");
						return;
					}
					if (usePowerLevels) {
						enc.pokemon = pickWildPowerLvlReplacement(pickable,
								enc.pokemon, true, false, null);
					} else {
						while (hasBattleTrappingAbility(enc.pokemon)) {
							int picked = RandomSource.nextInt(pickable.size());
							enc.pokemon = pickable.get(picked);
						}
					}
				}
			}
		}

		setEncounters(useTimeOfDay, currentEncounters);

	}

	@Override
	public void randomizeTrainerPokes(boolean rivalCarriesStarter,
			boolean usePowerLevels, boolean noLegendaries,
			boolean noEarlyWonderGuard) {
		checkPokemonRestrictions();
		List<Trainer> currentTrainers = this.getTrainers();
		cachedReplacementLists = new TreeMap<Type, List<Pokemon>>();
		cachedAllList = noLegendaries ? new ArrayList<Pokemon>(noLegendaryList)
				: new ArrayList<Pokemon>(mainPokemonList);

		// Fully random is easy enough - randomize then worry about rival
		// carrying starter at the end
		for (Trainer t : currentTrainers) {
			if (t.tag != null && t.tag.equals("IRIVAL")) {
				continue; // skip
			}
			for (TrainerPokemon tp : t.pokemon) {
				boolean wgAllowed = (!noEarlyWonderGuard) || tp.level >= 20;
				tp.pokemon = pickReplacement(tp.pokemon, usePowerLevels, null,
						noLegendaries, wgAllowed);
			}
		}

		// Rival carries starter?
		if (rivalCarriesStarter) {
			rivalCarriesStarterUpdate(currentTrainers, "RIVAL", 1);
			rivalCarriesStarterUpdate(currentTrainers, "FRIEND", 2);
		}

		// Save it all up
		this.setTrainers(currentTrainers);
	}

	@Override
	public void typeThemeTrainerPokes(boolean rivalCarriesStarter,
			boolean usePowerLevels, boolean weightByFrequency,
			boolean noLegendaries, boolean noEarlyWonderGuard) {
		checkPokemonRestrictions();
		List<Trainer> currentTrainers = this.getTrainers();
		cachedReplacementLists = new TreeMap<Type, List<Pokemon>>();
		cachedAllList = noLegendaries ? new ArrayList<Pokemon>(noLegendaryList)
				: new ArrayList<Pokemon>(mainPokemonList);
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
			if (group.startsWith("GYM") || group.startsWith("ELITE")
					|| group.startsWith("CHAMPION")
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
					tp.pokemon = pickReplacement(tp.pokemon, usePowerLevels,
							typeForGroup, noLegendaries, wgAllowed);
				}
			}
		}

		// Give a type to each unassigned trainer
		for (Trainer t : currentTrainers) {
			if (t.tag != null && t.tag.equals("IRIVAL")) {
				continue; // skip
			}

			if (!assignedTrainers.contains(t)) {
				Type typeForTrainer = pickType(weightByFrequency, noLegendaries);
				// Ubers: can't have the same type as each other
				if (t.tag != null && t.tag.equals("UBER")) {
					while (usedUberTypes.contains(typeForTrainer)) {
						typeForTrainer = pickType(weightByFrequency,
								noLegendaries);
					}
					usedUberTypes.add(typeForTrainer);
				}
				for (TrainerPokemon tp : t.pokemon) {
					boolean shedAllowed = (!noEarlyWonderGuard)
							|| tp.level >= 20;
					tp.pokemon = pickReplacement(tp.pokemon, usePowerLevels,
							typeForTrainer, noLegendaries, shedAllowed);
				}
			}
		}

		// Rival carries starter?
		if (rivalCarriesStarter) {
			rivalCarriesStarterUpdate(currentTrainers, "RIVAL", 1);
			rivalCarriesStarterUpdate(currentTrainers, "FRIEND", 2);
		}

		// Save it all up
		this.setTrainers(currentTrainers);
	}

	@Override
	public void randomizeMovesLearnt(boolean typeThemed, boolean noBroken,
			boolean forceFourStartingMoves) {
		// Get current sets
		Map<Pokemon, List<MoveLearnt>> movesets = this.getMovesLearnt();
		List<Integer> hms = this.getHMMoves();
		@SuppressWarnings("unchecked")
		List<Integer> banned = noBroken ? this.getGameBreakingMoves()
				: Collections.EMPTY_LIST;
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
			// Last level 1 move should be replaced with a damaging one
			int damagingMove = pickMove(pkmn, typeThemed, true, hms);
			// Find last lv1 move
			// lv1index ends up as the index of the first non-lv1 move
			int lv1index = 0;
			while (lv1index < moves.size() && moves.get(lv1index).level == 1) {
				lv1index++;
			}
			// last lv1 move is 1 before lv1index
			if (lv1index == 0) {
				lv1index++;
			}
			moves.get(lv1index - 1).move = damagingMove;
			moves.get(lv1index - 1).level = 1; // just in case
			learnt.add(damagingMove);
			// Rest replace with randoms
			for (int i = 0; i < moves.size(); i++) {
				if (i == (lv1index - 1)) {
					continue;
				}
				int picked = pickMove(pkmn, typeThemed, false, hms);
				while (learnt.contains(picked) || banned.contains(picked)) {
					picked = pickMove(pkmn, typeThemed, false, hms);
				}
				moves.get(i).move = picked;
				learnt.add(picked);
			}
		}
		// Done, save
		this.setMovesLearnt(movesets);

	}

	private static final int METRONOME_MOVE = 118;

	@Override
	public void metronomeOnlyMode() {
		// TODO fix static pokemon with set movesets

		// movesets
		Map<Pokemon, List<MoveLearnt>> movesets = this.getMovesLearnt();

		MoveLearnt metronomeML = new MoveLearnt();
		metronomeML.level = 1;
		metronomeML.move = METRONOME_MOVE;

		for (List<MoveLearnt> ms : movesets.values()) {
			if (ms != null && ms.size() > 0) {
				ms.clear();
				ms.add(metronomeML);
			}
		}

		this.setMovesLearnt(movesets);

		// trainers
		// run this to remove all custom non-Metronome moves
		this.setTrainers(this.getTrainers());

		// tms
		List<Integer> tmMoves = this.getTMMoves();

		for (int i = 0; i < tmMoves.size(); i++) {
			tmMoves.set(i, METRONOME_MOVE);
		}

		this.setTMMoves(tmMoves);

		// movetutors
		if (this.hasMoveTutors()) {
			List<Integer> mtMoves = this.getMoveTutorMoves();

			for (int i = 0; i < mtMoves.size(); i++) {
				mtMoves.set(i, METRONOME_MOVE);
			}

			this.setMoveTutorMoves(mtMoves);
		}

		// move tweaks
		List<Move> moveData = this.getMoves();

		Move metronome = moveData.get(METRONOME_MOVE);

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
			List<Pokemon> legendariesLeft = new ArrayList<Pokemon>(
					onlyLegendaryList);
			List<Pokemon> nonlegsLeft = new ArrayList<Pokemon>(noLegendaryList);
			legendariesLeft.removeAll(banned);
			nonlegsLeft.removeAll(banned);
			for (int i = 0; i < currentStaticPokemon.size(); i++) {
				Pokemon old = currentStaticPokemon.get(i);
				Pokemon newPK;
				if (old.isLegendary()) {
					newPK = legendariesLeft.remove(RandomSource
							.nextInt(legendariesLeft.size()));
					if (legendariesLeft.size() == 0) {
						legendariesLeft.addAll(onlyLegendaryList);
						legendariesLeft.removeAll(banned);
					}
				} else {
					newPK = nonlegsLeft.remove(RandomSource.nextInt(nonlegsLeft
							.size()));
					if (nonlegsLeft.size() == 0) {
						nonlegsLeft.addAll(onlyLegendaryList);
						nonlegsLeft.removeAll(banned);
					}
				}
				replacements.add(newPK);
			}
		} else {
			List<Pokemon> pokemonLeft = new ArrayList<Pokemon>(mainPokemonList);
			pokemonLeft.removeAll(banned);
			for (int i = 0; i < currentStaticPokemon.size(); i++) {
				Pokemon newPK = pokemonLeft.remove(RandomSource
						.nextInt(pokemonLeft.size()));
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
	public void randomizeTMMoves(boolean noBroken) {
		// Pick some random TM moves.
		int tmCount = this.getTMCount();
		List<Move> allMoves = this.getMoves();
		List<Integer> newTMs = new ArrayList<Integer>();
		List<Integer> hms = this.getHMMoves();
		@SuppressWarnings("unchecked")
		List<Integer> banned = noBroken ? this.getGameBreakingMoves()
				: Collections.EMPTY_LIST;
		for (int i = 0; i < tmCount; i++) {
			int chosenMove = RandomSource.nextInt(allMoves.size() - 1) + 1;
			while (newTMs.contains(chosenMove)
					|| RomFunctions.bannedRandomMoves[chosenMove]
					|| hms.contains(chosenMove) || banned.contains(chosenMove)) {
				chosenMove = RandomSource.nextInt(allMoves.size() - 1) + 1;
			}
			newTMs.add(chosenMove);
		}
		this.setTMMoves(newTMs);
	}

	@Override
	public void randomizeTMHMCompatibility(boolean preferSameType) {
		// Get current compatibility
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
							|| (pkmn.secondaryType != null && pkmn.secondaryType
									.equals(mv.type))) {
						probability = 0.9;
					} else if (mv.type != null && mv.type.equals(Type.NORMAL)) {
						probability = 0.5;
					} else {
						probability = 0.25;
					}
				}
				flags[i] = (RandomSource.random() < probability);
			}
		}

		// Set the new compatibility
		this.setTMHMCompatibility(compat);
	}

	@Override
	public void randomizeMoveTutorMoves(boolean noBroken) {
		if (!this.hasMoveTutors()) {
			return;
		}
		// Pick some random Move Tutor moves, excluding TMs.
		int mtCount = this.getMoveTutorMoves().size();
		List<Move> allMoves = this.getMoves();
		List<Integer> tms = this.getTMMoves();
		List<Integer> newMTs = new ArrayList<Integer>();
		List<Integer> hms = this.getHMMoves();
		@SuppressWarnings("unchecked")
		List<Integer> banned = noBroken ? this.getGameBreakingMoves()
				: Collections.EMPTY_LIST;
		for (int i = 0; i < mtCount; i++) {
			int chosenMove = RandomSource.nextInt(allMoves.size() - 1) + 1;
			while (newMTs.contains(chosenMove) || tms.contains(chosenMove)
					|| RomFunctions.bannedRandomMoves[chosenMove]
					|| hms.contains(chosenMove) || banned.contains(chosenMove)) {
				chosenMove = RandomSource.nextInt(allMoves.size() - 1) + 1;
			}
			newMTs.add(chosenMove);
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
							|| (pkmn.secondaryType != null && pkmn.secondaryType
									.equals(mv.type))) {
						probability = 0.9;
					} else if (mv.type != null && mv.type.equals(Type.NORMAL)) {
						probability = 0.5;
					} else {
						probability = 0.25;
					}
				}
				flags[i] = (RandomSource.random() < probability);
			}
		}

		// Set the new compatibility
		this.setMoveTutorCompatibility(compat);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void randomizeTrainerNames(byte[] presetNames) {
		List<String>[] allTrainerNames = new List[] { new ArrayList<String>(),
				new ArrayList<String>() };
		Map<Integer, List<String>> trainerNamesByLength[] = new Map[] {
				new TreeMap<Integer, List<String>>(),
				new TreeMap<Integer, List<String>>() };
		// Check for the file
		if (FileFunctions.configExists(tnamesFile)) {
			try {
				Scanner sc = null;
				if (presetNames == null) {
					sc = new Scanner(FileFunctions.openConfig(tnamesFile),
							"UTF-8");
				} else {
					sc = new Scanner(new ByteArrayInputStream(presetNames),
							"UTF-8");
				}
				while (sc.hasNextLine()) {
					String trainername = sc.nextLine().trim();
					if (trainername.isEmpty()) {
						continue;
					}
					if (trainername.startsWith("\uFEFF")) {
						trainername = trainername.substring(1);
					}
					int idx = trainername.contains("&") ? 1 : 0;
					int len = this.internalStringLength(trainername);
					if (len <= 10) {
						allTrainerNames[idx].add(trainername);
						if (trainerNamesByLength[idx].containsKey(len)) {
							trainerNamesByLength[idx].get(len).add(trainername);
						} else {
							List<String> namesOfThisLength = new ArrayList<String>();
							namesOfThisLength.add(trainername);
							trainerNamesByLength[idx].put(len,
									namesOfThisLength);
						}
					}
				}
				sc.close();
			} catch (FileNotFoundException e) {
				// Can't read, just don't load anything
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

		// Init the translation map and new list
		Map<String, String> translation = new HashMap<String, String>();
		List<String> newTrainerNames = new ArrayList<String>();
		List<Integer> tcNameLengths = this.getTCNameLengthsByTrainer();

		// Start choosing
		int tnIndex = -1;
		for (String trainerName : currentTrainerNames) {
			tnIndex++;
			if (translation.containsKey(trainerName)
					&& trainerName.equalsIgnoreCase("GRUNT") == false
					&& trainerName.equalsIgnoreCase("EXECUTIVE") == false) {
				// use an already picked translation
				newTrainerNames.add(translation.get(trainerName));
			} else {
				int idx = trainerName.contains("&") ? 1 : 0;
				List<String> pickFrom = allTrainerNames[idx];
				int intStrLen = this.internalStringLength(trainerName);
				if (mode == TrainerNameMode.SAME_LENGTH) {
					pickFrom = trainerNamesByLength[idx].get(intStrLen);
				}
				String changeTo = trainerName;
				if (pickFrom != null && pickFrom.size() > 0 && intStrLen > 1) {
					int tries = 0;
					changeTo = pickFrom.get(RandomSource.nextInt(pickFrom
							.size()));
					int ctl = this.internalStringLength(changeTo);
					while ((mode == TrainerNameMode.MAX_LENGTH && ctl > maxLength)
							|| (mode == TrainerNameMode.MAX_LENGTH_WITH_CLASS && ctl
									+ tcNameLengths.get(tnIndex) > maxLength)) {
						tries++;
						if (tries == 50) {
							changeTo = trainerName;
							break;
						}
						changeTo = pickFrom.get(RandomSource.nextInt(pickFrom
								.size()));
						ctl = this.internalStringLength(changeTo);
					}
				}
				translation.put(trainerName, changeTo);
				newTrainerNames.add(changeTo);
			}
		}

		// Done choosing, save
		this.setTrainerNames(newTrainerNames);
	}

	@Override
	public int maxTrainerNameLength() {
		// default: no real limit
		return Integer.MAX_VALUE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void randomizeTrainerClassNames(byte[] presetNames) {
		List<String> allTrainerClasses[] = new List[] {
				new ArrayList<String>(), new ArrayList<String>() };
		Map<Integer, List<String>> trainerClassesByLength[] = new Map[] {
				new HashMap<Integer, List<String>>(),
				new HashMap<Integer, List<String>>() };
		// Check for the file
		if (FileFunctions.configExists(tclassesFile)) {
			try {
				Scanner sc = null;
				if (presetNames == null) {
					sc = new Scanner(FileFunctions.openConfig(tclassesFile),
							"UTF-8");
				} else {
					sc = new Scanner(new ByteArrayInputStream(presetNames),
							"UTF-8");
				}
				while (sc.hasNextLine()) {
					String trainerClassName = sc.nextLine().trim();
					if (trainerClassName.isEmpty()) {
						continue;
					}
					if (trainerClassName.startsWith("\uFEFF")) {
						trainerClassName = trainerClassName.substring(1);
					}
					String checkName = trainerClassName.toLowerCase();
					int idx = (checkName.endsWith("couple")
							|| checkName.contains(" and ")
							|| checkName.endsWith("kin")
							|| checkName.endsWith("team")
							|| checkName.contains("&") || (checkName
							.endsWith("s") && !checkName.endsWith("ss"))) ? 1
							: 0;
					allTrainerClasses[idx].add(trainerClassName);
					int len = this.internalStringLength(trainerClassName);
					if (trainerClassesByLength[idx].containsKey(len)) {
						trainerClassesByLength[idx].get(len).add(
								trainerClassName);
					} else {
						List<String> namesOfThisLength = new ArrayList<String>();
						namesOfThisLength.add(trainerClassName);
						trainerClassesByLength[idx].put(len, namesOfThisLength);
					}
				}
				sc.close();
			} catch (FileNotFoundException e) {
				// Can't read, just don't load anything
			}
		}

		// Get the current trainer names data
		List<String> currentClassNames = this.getTrainerClassNames();
		boolean mustBeSameLength = this.fixedTrainerClassNamesLength();
		int maxLength = this.maxTrainerClassNameLength();

		// Init the translation map and new list
		Map<String, String> translation = new HashMap<String, String>();
		List<String> newClassNames = new ArrayList<String>();

		// Start choosing
		for (String trainerClassName : currentClassNames) {
			if (translation.containsKey(trainerClassName)) {
				// use an already picked translation
				newClassNames.add(translation.get(trainerClassName));
			} else {
				String checkName = trainerClassName.toLowerCase();
				int idx = (checkName.endsWith("couple")
						|| checkName.contains(" and ")
						|| checkName.endsWith("kin")
						|| checkName.endsWith("team")
						|| checkName.contains(" & ") || (checkName
						.endsWith("s") && !checkName.endsWith("ss"))) ? 1 : 0;
				List<String> pickFrom = allTrainerClasses[idx];
				int intStrLen = this.internalStringLength(trainerClassName);
				if (mustBeSameLength) {
					pickFrom = trainerClassesByLength[idx].get(intStrLen);
				}
				String changeTo = trainerClassName;
				if (pickFrom != null && pickFrom.size() > 0) {
					changeTo = pickFrom.get(RandomSource.nextInt(pickFrom
							.size()));
					while (changeTo.length() > maxLength) {
						changeTo = pickFrom.get(RandomSource.nextInt(pickFrom
								.size()));
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
	public int maxTrainerClassNameLength() {
		// default: no real limit
		return Integer.MAX_VALUE;
	}

	@Override
	public void randomizeWildHeldItems() {
		List<Pokemon> pokemon = allPokemonWithoutNull();
		ItemList possibleItems = this.getAllowedItems();
		for (Pokemon pk : pokemon) {
			if (pk.guaranteedHeldItem == -1 && pk.commonHeldItem == -1
					&& pk.rareHeldItem == -1 && pk.darkGrassHeldItem == -1) {
				// No held items at all, abort
				return;
			}
			boolean canHaveDarkGrass = pk.darkGrassHeldItem != -1;
			if (pk.guaranteedHeldItem != -1) {
				// Guaranteed held items are supported.
				if (pk.guaranteedHeldItem > 0) {
					// Currently have a guaranteed item
					double decision = RandomSource.nextDouble();
					if (decision < 0.9) {
						// Stay as guaranteed
						canHaveDarkGrass = false;
						pk.guaranteedHeldItem = possibleItems.randomItem();
					} else {
						// Change to 25% or 55% chance
						pk.guaranteedHeldItem = 0;
						pk.commonHeldItem = possibleItems.randomItem();
						pk.rareHeldItem = possibleItems.randomItem();
						while (pk.rareHeldItem == pk.commonHeldItem) {
							pk.rareHeldItem = possibleItems.randomItem();
						}
					}
				} else {
					// No guaranteed item atm
					double decision = RandomSource.nextDouble();
					if (decision < 0.5) {
						// No held item at all
						pk.commonHeldItem = 0;
						pk.rareHeldItem = 0;
					} else if (decision < 0.65) {
						// Just a rare item
						pk.commonHeldItem = 0;
						pk.rareHeldItem = possibleItems.randomItem();
					} else if (decision < 0.8) {
						// Just a common item
						pk.commonHeldItem = possibleItems.randomItem();
						pk.rareHeldItem = 0;
					} else if (decision < 0.95) {
						// Both a common and rare item
						pk.commonHeldItem = possibleItems.randomItem();
						pk.rareHeldItem = possibleItems.randomItem();
						while (pk.rareHeldItem == pk.commonHeldItem) {
							pk.rareHeldItem = possibleItems.randomItem();
						}
					} else {
						// Guaranteed item
						canHaveDarkGrass = false;
						pk.guaranteedHeldItem = possibleItems.randomItem();
						pk.commonHeldItem = 0;
						pk.rareHeldItem = 0;
					}
				}
			} else {
				// Code for no guaranteed items
				double decision = RandomSource.nextDouble();
				if (decision < 0.5) {
					// No held item at all
					pk.commonHeldItem = 0;
					pk.rareHeldItem = 0;
				} else if (decision < 0.65) {
					// Just a rare item
					pk.commonHeldItem = 0;
					pk.rareHeldItem = possibleItems.randomItem();
				} else if (decision < 0.8) {
					// Just a common item
					pk.commonHeldItem = possibleItems.randomItem();
					pk.rareHeldItem = 0;
				} else {
					// Both a common and rare item
					pk.commonHeldItem = possibleItems.randomItem();
					pk.rareHeldItem = possibleItems.randomItem();
					while (pk.rareHeldItem == pk.commonHeldItem) {
						pk.rareHeldItem = possibleItems.randomItem();
					}
				}
			}

			if (canHaveDarkGrass) {
				double dgDecision = RandomSource.nextDouble();
				if (dgDecision < 0.5) {
					// Yes, dark grass item
					pk.darkGrassHeldItem = possibleItems.randomItem();
				} else {
					pk.darkGrassHeldItem = 0;
				}
			} else if (pk.darkGrassHeldItem != -1) {
				pk.darkGrassHeldItem = 0;
			}
		}

	}

	@Override
	public void randomizeStarterHeldItems() {
		List<Integer> oldHeldItems = this.getStarterHeldItems();
		List<Integer> newHeldItems = new ArrayList<Integer>();
		ItemList possibleItems = this.getAllowedItems();
		for (int i = 0; i < oldHeldItems.size(); i++) {
			newHeldItems.add(possibleItems.randomItem());
		}
		this.setStarterHeldItems(newHeldItems);
	}

	@Override
	public void shuffleFieldItems() {
		List<Integer> currentItems = this.getRegularFieldItems();
		List<Integer> currentTMs = this.getCurrentFieldTMs();

		Collections.shuffle(currentItems, RandomSource.instance());
		Collections.shuffle(currentTMs, RandomSource.instance());

		this.setRegularFieldItems(currentItems);
		this.setFieldTMs(currentTMs);
	}

	@Override
	public void randomizeFieldItems() {
		ItemList possibleItems = this.getAllowedItems();
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
			newItems.add(possibleItems.randomNonTM());
		}

		newTMs.addAll(requiredTMs);

		for (int i = reqTMCount; i < fieldTMCount; i++) {
			while (true) {
				int tm = RandomSource.nextInt(totalTMCount) + 1;
				if (!newTMs.contains(tm)) {
					newTMs.add(tm);
					break;
				}
			}
		}

		Collections.shuffle(newItems, RandomSource.instance());
		Collections.shuffle(newTMs, RandomSource.instance());

		this.setRegularFieldItems(newItems);
		this.setFieldTMs(newTMs);
	}

	@Override
	public void randomizeIngameTrades(boolean randomizeRequest,
			byte[] presetNicknames, boolean randomNickname,
			byte[] presetTrainerNames, boolean randomOT, boolean randomStats,
			boolean randomItem) {
		checkPokemonRestrictions();
		// Process trainer names
		List<String> singleTrainerNames = new ArrayList<String>();
		// Check for the file
		if (FileFunctions.configExists(tnamesFile) && randomOT) {
			int maxOT = this.maxTradeOTNameLength();
			try {
				Scanner sc = null;
				if (presetTrainerNames == null) {
					sc = new Scanner(FileFunctions.openConfig(tnamesFile),
							"UTF-8");
				} else {
					sc = new Scanner(new ByteArrayInputStream(
							presetTrainerNames), "UTF-8");
				}
				while (sc.hasNextLine()) {
					String trainername = sc.nextLine().trim();
					if (trainername.isEmpty()) {
						continue;
					}
					if (trainername.startsWith("\uFEFF")) {
						trainername = trainername.substring(1);
					}
					int idx = trainername.contains("&") ? 1 : 0;
					int len = this.internalStringLength(trainername);
					if (len <= maxOT && idx == 0
							&& !singleTrainerNames.contains(trainername)) {
						singleTrainerNames.add(trainername);
					}
				}
				sc.close();
			} catch (FileNotFoundException e) {
				// Can't read, just don't load anything
			}
		}

		// Process nicknames
		List<String> nicknames = new ArrayList<String>();
		// Check for the file
		if (FileFunctions.configExists(nnamesFile) && randomNickname) {
			int maxNN = this.maxTradeNicknameLength();
			try {
				Scanner sc = null;
				if (presetNicknames == null) {
					sc = new Scanner(FileFunctions.openConfig(nnamesFile),
							"UTF-8");
				} else {
					sc = new Scanner(new ByteArrayInputStream(presetNicknames),
							"UTF-8");
				}
				while (sc.hasNextLine()) {
					String nickname = sc.nextLine().trim();
					if (nickname.isEmpty()) {
						continue;
					}
					if (nickname.startsWith("\uFEFF")) {
						nickname = nickname.substring(1);
					}
					int len = this.internalStringLength(nickname);
					if (len <= maxNN && !nicknames.contains(nickname)) {
						nicknames.add(nickname);
					}
				}
				sc.close();
			} catch (FileNotFoundException e) {
				// Can't read, just don't load anything
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
		int trnameCount = singleTrainerNames.size();

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
			if (randomizeRequest) {
				Pokemon request = this.randomPokemon();
				while (usedRequests.contains(request) || request == given) {
					request = this.randomPokemon();
				}
				usedRequests.add(request);
				trade.requestedPokemon = request;
			}

			// nickname?
			if (randomNickname && nickCount > usedNicknames.size()) {
				String nickname = nicknames
						.get(RandomSource.nextInt(nickCount));
				while (usedNicknames.contains(nickname)) {
					nickname = nicknames.get(RandomSource.nextInt(nickCount));
				}
				usedNicknames.add(nickname);
				trade.nickname = nickname;
			} else if (trade.nickname.equalsIgnoreCase(oldgiven.name)) {
				// change the name for sanity
				trade.nickname = trade.givenPokemon.name;
			}

			if (randomOT && trnameCount > usedOTs.size()) {
				String ot = singleTrainerNames.get(RandomSource
						.nextInt(trnameCount));
				while (usedOTs.contains(ot)) {
					ot = singleTrainerNames.get(RandomSource
							.nextInt(trnameCount));
				}
				usedOTs.add(ot);
				trade.otName = ot;
				trade.otId = RandomSource.nextInt(65536);
			}

			if (randomStats) {
				int maxIV = this.hasDVs() ? 16 : 32;
				for (int i = 0; i < trade.ivs.length; i++) {
					trade.ivs[i] = RandomSource.nextInt(maxIV);
				}
			}

			if (randomItem) {
				trade.item = possibleItems.randomItem();
			}
		}

		// things that the game doesn't support should just be ignored
		this.setIngameTrades(trades);
	}

	@Override
	public int maxTradeNicknameLength() {
		return 10;
	}

	@Override
	public int maxTradeOTNameLength() {
		return 7;
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
				nonTypeChanges.add(String.format("%.00f%% accuracy",
						mv.hitratio));
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
					logStr += nonTypeChanges.get(0) + ", "
							+ nonTypeChanges.get(1) + " and "
							+ nonTypeChanges.get(2);
				} else if (nonTypeChanges.size() == 2) {
					logStr += nonTypeChanges.get(0) + " and "
							+ nonTypeChanges.get(1);
				} else {
					logStr += nonTypeChanges.get(0);
				}
			}
			log(logStr);
		}
		logBlankLine();
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
			mv.setAccuracy(accuracy);
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

	private int pickMove(Pokemon pkmn, boolean typeThemed, boolean damaging,
			List<Integer> hms) {
		// If damaging, we want a move with at least 80% accuracy and 2 power
		List<Move> allMoves = this.getMoves();
		Type typeOfMove = null;
		double picked = RandomSource.random();
		// Type?
		if (typeThemed) {
			if (pkmn.primaryType == Type.NORMAL
					|| pkmn.secondaryType == Type.NORMAL) {
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
				// Primary/Secondary: 50% primary, 30% secondary, 5% normal, 15%
				// random
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
		// Filter by type, and if necessary, by damage
		List<Move> canPick = new ArrayList<Move>();
		for (Move mv : allMoves) {
			if (mv != null && !RomFunctions.bannedRandomMoves[mv.number]
					&& !hms.contains(mv.number)
					&& (mv.type == typeOfMove || typeOfMove == null)) {
				if (!damaging
						|| (mv.power > 1 && mv.hitratio > 79 && !RomFunctions.bannedForDamagingMove[mv.number])) {
					canPick.add(mv);
				}
			}
		}
		// If we ended up with no results, reroll
		if (canPick.size() == 0) {
			return pickMove(pkmn, typeThemed, damaging, hms);
		} else {
			// pick a random one
			return canPick.get(RandomSource.nextInt(canPick.size())).number;
		}
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
			int typePick = RandomSource.nextInt(totalTypeWeighting);
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

	private void rivalCarriesStarterUpdate(List<Trainer> currentTrainers,
			String prefix, int pokemonOffset) {
		// Find the highest rival battle #
		int highestRivalNum = 0;
		for (Trainer t : currentTrainers) {
			if (t.tag != null && t.tag.startsWith(prefix)) {
				highestRivalNum = Math.max(
						highestRivalNum,
						Integer.parseInt(t.tag.substring(prefix.length(),
								t.tag.indexOf('-'))));
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
			int timesEvolves = timesEvolves(rivalStarter);
			// Apply evolutions as appropriate
			if (timesEvolves == 0) {
				for (int j = 1; j <= 3; j++) {
					changeStarterWithTag(currentTrainers, prefix + j + "-0",
							rivalStarter);
				}
				for (int j = 4; j <= 7; j++) {
					for (int i = 0; i < 3; i++) {
						changeStarterWithTag(currentTrainers, prefix + j + "-"
								+ i, rivalStarter);
					}
				}
			} else if (timesEvolves == 1) {
				for (int j = 1; j <= 3; j++) {
					changeStarterWithTag(currentTrainers, prefix + j + "-0",
							rivalStarter);
				}
				rivalStarter = firstEvolution(rivalStarter);
				for (int j = 4; j <= 7; j++) {
					for (int i = 0; i < 3; i++) {
						changeStarterWithTag(currentTrainers, prefix + j + "-"
								+ i, rivalStarter);
					}
				}
			} else if (timesEvolves == 2) {
				for (int j = 1; j <= 2; j++) {
					changeStarterWithTag(currentTrainers, prefix + j + "-" + 0,
							rivalStarter);
				}
				rivalStarter = firstEvolution(rivalStarter);
				changeStarterWithTag(currentTrainers, prefix + "3-0",
						rivalStarter);
				for (int i = 0; i < 3; i++) {
					changeStarterWithTag(currentTrainers, prefix + "4-" + i,
							rivalStarter);
				}
				rivalStarter = firstEvolution(rivalStarter);
				for (int j = 5; j <= 7; j++) {
					for (int i = 0; i < 3; i++) {
						changeStarterWithTag(currentTrainers, prefix + j + "-"
								+ i, rivalStarter);
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
				int timesEvolves = timesEvolves(thisStarter);
				// If a fully evolved pokemon, use throughout
				// Otherwise split by evolutions as appropriate
				if (timesEvolves == 0) {
					for (int j = 1; j <= highestRivalNum; j++) {
						changeStarterWithTag(currentTrainers, prefix + j + "-"
								+ i, thisStarter);
					}
				} else if (timesEvolves == 1) {
					int j = 1;
					for (; j <= highestRivalNum / 2; j++) {
						if (getLevelOfStarter(currentTrainers, prefix + j + "-"
								+ i) >= 30) {
							break;
						}
						changeStarterWithTag(currentTrainers, prefix + j + "-"
								+ i, thisStarter);
					}
					thisStarter = firstEvolution(thisStarter);
					for (; j <= highestRivalNum; j++) {
						changeStarterWithTag(currentTrainers, prefix + j + "-"
								+ i, thisStarter);
					}
				} else if (timesEvolves == 2) {
					int j = 1;
					for (; j <= highestRivalNum; j++) {
						if (getLevelOfStarter(currentTrainers, prefix + j + "-"
								+ i) >= 16) {
							break;
						}
						changeStarterWithTag(currentTrainers, prefix + j + "-"
								+ i, thisStarter);
					}
					thisStarter = firstEvolution(thisStarter);
					for (; j <= highestRivalNum; j++) {
						if (getLevelOfStarter(currentTrainers, prefix + j + "-"
								+ i) >= 36) {
							break;
						}
						changeStarterWithTag(currentTrainers, prefix + j + "-"
								+ i, thisStarter);
					}
					thisStarter = firstEvolution(thisStarter);
					for (; j <= highestRivalNum; j++) {
						changeStarterWithTag(currentTrainers, prefix + j + "-"
								+ i, thisStarter);
					}
				}
			}
		}

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

	private void changeStarterWithTag(List<Trainer> currentTrainers,
			String tag, Pokemon starter) {
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
			}
		}

	}

	private int timesEvolves(Pokemon pk) {
		// This method works ASSUMING a pokemon has no weird split evolutions
		// with different levels on each side
		// Which is true for every pokemon so far.
		List<Evolution> evos = this.getEvolutions();
		List<Pokemon> pokes = this.getPokemon();
		for (Evolution e : evos) {
			if (e.from == pk.number) {
				return timesEvolves(pokes.get(e.to)) + 1;
			}
		}
		return 0;
	}

	private Pokemon firstEvolution(Pokemon pk) {
		List<Evolution> evos = this.getEvolutions();
		List<Pokemon> pokes = this.getPokemon();
		for (Evolution e : evos) {
			if (e.from == pk.number) {
				return pokes.get(e.to);
			}
		}
		return null;
	}

	private Map<Type, List<Pokemon>> cachedReplacementLists;
	private List<Pokemon> cachedAllList;

	private Pokemon pickReplacement(Pokemon current, boolean usePowerLevels,
			Type type, boolean noLegendaries, boolean wonderGuardAllowed) {
		List<Pokemon> pickFrom = cachedAllList;
		if (type != null) {
			if (!cachedReplacementLists.containsKey(type)) {
				cachedReplacementLists.put(type,
						pokemonOfType(type, noLegendaries));
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
			while (canPick.isEmpty()) {
				for (Pokemon pk : pickFrom) {
					if (pk.bstForPowerLevels() >= minTarget
							&& pk.bstForPowerLevels() <= maxTarget
							&& (wonderGuardAllowed || (pk.ability1 != WONDER_GUARD_INDEX
									&& pk.ability2 != WONDER_GUARD_INDEX && pk.ability3 != WONDER_GUARD_INDEX))) {
						canPick.add(pk);
					}
				}
				minTarget -= currentBST / 20;
				maxTarget += currentBST / 20;
			}
			return canPick.get(RandomSource.nextInt(canPick.size()));
		} else {
			if (wonderGuardAllowed) {
				return pickFrom.get(RandomSource.nextInt(pickFrom.size()));
			} else {
				Pokemon pk = pickFrom
						.get(RandomSource.nextInt(pickFrom.size()));
				while (pk.ability1 == WONDER_GUARD_INDEX
						|| pk.ability2 == WONDER_GUARD_INDEX
						|| pk.ability3 == WONDER_GUARD_INDEX) {
					pk = pickFrom.get(RandomSource.nextInt(pickFrom.size()));
				}
				return pk;
			}
		}
	}

	private Pokemon pickWildPowerLvlReplacement(List<Pokemon> pokemonPool,
			Pokemon current, boolean banBattleTrappers, boolean banSamePokemon,
			List<Pokemon> usedUp) {
		// start with within 10% and add 5% either direction till we find
		// something
		int currentBST = current.bstForPowerLevels();
		int minTarget = currentBST - currentBST / 10;
		int maxTarget = currentBST + currentBST / 10;
		List<Pokemon> canPick = new ArrayList<Pokemon>();
		while (canPick.isEmpty()) {
			for (Pokemon pk : pokemonPool) {
				if (pk.bstForPowerLevels() >= minTarget
						&& pk.bstForPowerLevels() <= maxTarget
						&& (!banBattleTrappers || !hasBattleTrappingAbility(pk))
						&& (!banSamePokemon || pk != current)
						&& (usedUp == null || !usedUp.contains(pk))) {
					canPick.add(pk);
				}
			}
			minTarget -= currentBST / 20;
			maxTarget += currentBST / 20;
		}
		return canPick.get(RandomSource.nextInt(canPick.size()));
	}

	private static final List<Integer> battleTrappingAbilities = Arrays.asList(
			23, 42, 71);

	private boolean hasBattleTrappingAbility(Pokemon pokemon) {
		return battleTrappingAbilities.contains(pokemon.ability1)
				|| battleTrappingAbilities.contains(pokemon.ability2)
				|| battleTrappingAbilities.contains(pokemon.ability3);
	}

	/* Helper methods used by subclasses */

	protected void log(String log) {
		RandomizerGUI.verboseLog.println(log);
	}

	protected void logBlankLine() {
		RandomizerGUI.verboseLog.println();
	}

	protected void logEvoChangeLevel(String pkFrom, String pkTo, int level) {
		RandomizerGUI.verboseLog.printf("Made %s evolve into %s at level %d",
				pkFrom, pkTo, level);
		RandomizerGUI.verboseLog.println();
	}

	protected void logEvoChangeLevelWithItem(String pkFrom, String pkTo,
			String itemName) {
		RandomizerGUI.verboseLog.printf(
				"Made %s evolve into %s by leveling up holding %s", pkFrom,
				pkTo, itemName);
		RandomizerGUI.verboseLog.println();
	}

	protected void logEvoChangeStone(String pkFrom, String pkTo, String itemName) {
		RandomizerGUI.verboseLog.printf("Made %s evolve into %s using a %s",
				pkFrom, pkTo, itemName);
		RandomizerGUI.verboseLog.println();
	}

	protected void logEvoChangeLevelWithPkmn(String pkFrom, String pkTo,
			String otherRequired) {
		RandomizerGUI.verboseLog.printf(
				"Made %s evolve into %s by leveling up with %s in the party",
				pkFrom, pkTo, otherRequired);
		RandomizerGUI.verboseLog.println();
	}

	/* Default Implementations */
	/* Used when a subclass doesn't override */

	@Override
	public void fixTypeEffectiveness() {
		// DEFAULT: do nothing

	}

	@Override
	public boolean hasTimeBasedEncounters() {
		// DEFAULT: no
		return false;
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
	public Type randomType() {
		Type t = Type.randomType();
		while (!typeInGame(t)) {
			t = Type.randomType();
		}
		return t;
	}

	@Override
	public boolean isYellow() {
		return false;
	}

	@Override
	public void patchForNationalDex() {
		// Default: Do Nothing.

	}

	@Override
	public boolean canChangeStarters() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pokemon> bannedForWildEncounters() {
		return (List<Pokemon>) Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pokemon> bannedForStaticPokemon() {
		return (List<Pokemon>) Collections.EMPTY_LIST;
	}

	@Override
	public int codeTweaksAvailable() {
		// default: none
		return 0;
	}

	@Override
	public void applyBWEXPPatch() {
		// default: do nothing

	}

	@Override
	public void applyXAccNerfPatch() {
		// default: do nothing

	}

	@Override
	public void applyCritRatePatch() {
		// default: do nothing

	}

	@Override
	public boolean hasHiddenHollowPokemon() {
		// default: no
		return false;
	}

	@Override
	public void randomizeHiddenHollowPokemon() {
		// default: do nothing

	}

	@Override
	public List<Integer> getGameBreakingMoves() {
		// Sonicboom & drage
		return Arrays.asList(49, 82);
	}

	@Override
	public boolean isROMHack() {
		// override until detection implemented
		return false;
	}

}
