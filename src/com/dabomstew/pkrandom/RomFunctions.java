package com.dabomstew.pkrandom;

/*----------------------------------------------------------------------------*/
/*--  RomFunctions.java - contains functions useful throughout the program.	--*/
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.romhandlers.RomHandler;

public class RomFunctions {

	public static final boolean[] bannedRandomMoves = new boolean[560],
			bannedForDamagingMove = new boolean[560];
	static {
		bannedRandomMoves[144] = true; // Transform, glitched in RBY
		bannedRandomMoves[165] = true; // Struggle, self explanatory

		bannedForDamagingMove[120] = true; // SelfDestruct
		bannedForDamagingMove[138] = true; // Dream Eater
		bannedForDamagingMove[153] = true; // Explosion
		bannedForDamagingMove[173] = true; // Snore
		bannedForDamagingMove[206] = true; // False Swipe
		bannedForDamagingMove[248] = true; // Future Sight
		bannedForDamagingMove[252] = true; // Fake Out
		bannedForDamagingMove[264] = true; // Focus Punch
		bannedForDamagingMove[353] = true; // Doom Desire
		bannedForDamagingMove[364] = true; // Feint
		bannedForDamagingMove[387] = true; // Last Resort
		bannedForDamagingMove[389] = true; // Sucker Punch

		// new 160
		bannedForDamagingMove[132] = true; // Constrict, overly weak
		bannedForDamagingMove[99] = true; // Rage, lock-in in gen1
		bannedForDamagingMove[205] = true; // Rollout, lock-in
		bannedForDamagingMove[301] = true; // Ice Ball, Rollout clone
		
		// make sure these cant roll
		bannedForDamagingMove[39] = true; // Sonicboom
		bannedForDamagingMove[82] = true; // Dragon Rage
		bannedForDamagingMove[32] = true; // Horn Drill
		bannedForDamagingMove[12] = true; // Guillotine
		bannedForDamagingMove[90] = true; // Fissure
		bannedForDamagingMove[329] = true; // Sheer Cold

	}

	public static Set<Pokemon> getBasicOrNoCopyPokemon(RomHandler baseRom) {
		List<Pokemon> allPokes = baseRom.getPokemon();
		List<Evolution> evos = baseRom.getEvolutions();

		Set<Pokemon> doCopyPokes = new TreeSet<Pokemon>();
		for (Evolution e : evos) {
			if (e.carryStats) {
				doCopyPokes.add(allPokes.get(e.to));
			}
		}
		Set<Pokemon> dontCopyPokes = new TreeSet<Pokemon>();
		for (Pokemon pkmn : allPokes) {
			if (pkmn != null) {
				if (doCopyPokes.contains(pkmn) == false) {
					dontCopyPokes.add(pkmn);
				}
			}
		}
		return dontCopyPokes;
	}

	public static Set<Pokemon> getFirstEvolutions(RomHandler baseRom) {
		List<Pokemon> allPokes = baseRom.getPokemon();
		List<Evolution> evos = baseRom.getEvolutions();
		Set<Pokemon> basicPokemon = getBasicOrNoCopyPokemon(baseRom);

		Set<Pokemon> firstEvos = new TreeSet<Pokemon>();
		for (Evolution e : evos) {
			if (basicPokemon.contains(allPokes.get(e.from))) {
				firstEvos.add(allPokes.get(e.to));
			}
		}
		return firstEvos;
	}

	public static Set<Pokemon> getSecondEvolutions(RomHandler baseRom) {
		List<Pokemon> allPokes = baseRom.getPokemon();
		List<Evolution> evos = baseRom.getEvolutions();
		Set<Pokemon> firstEvos = getFirstEvolutions(baseRom);

		Set<Pokemon> secondEvos = new TreeSet<Pokemon>();
		for (Evolution e : evos) {
			if (firstEvos.contains(allPokes.get(e.from))) {
				secondEvos.add(allPokes.get(e.to));
			}
		}
		return secondEvos;
	}

	public static boolean pokemonHasEvo(RomHandler baseRom, Pokemon pkmn) {
		List<Evolution> evos = baseRom.getEvolutions();
		for (Evolution evo : evos) {
			if (evo.from == pkmn.number) {
				return true;
			}
		}
		return false;
	}

	public static Pokemon evolvesFrom(RomHandler baseRom, Pokemon pkmn) {
		List<Evolution> evos = baseRom.getEvolutions();
		for (Evolution evo : evos) {
			if (evo.to == pkmn.number) {
				return baseRom.getPokemon().get(evo.from);
			}
		}
		return null;
	}

	public static String camelCase(String original) {
		char[] string = original.toLowerCase().toCharArray();
		boolean docap = true;
		for (int j = 0; j < string.length; j++) {
			char current = string[j];
			if (docap && Character.isLetter(current)) {
				string[j] = Character.toUpperCase(current);
				docap = false;
			} else {
				if (!docap && !Character.isLetter(current) && current != '\'') {
					docap = true;
				}
			}
		}
		return new String(string);
	}

	public static int freeSpaceFinder(byte[] rom, byte freeSpace, int amount,
			int offset) {
		// by default align to 4 bytes to make sure things don't break
		return freeSpaceFinder(rom, freeSpace, amount, offset, true);
	}

	public static int freeSpaceFinder(byte[] rom, byte freeSpace, int amount,
			int offset, boolean longAligned) {
		if (!longAligned) {
			// Find 2 more than necessary and return 2 into it,
			// to preserve stuff like FF terminators for strings
			// 161: and FFFF terminators for movesets 
			byte[] searchNeedle = new byte[amount + 2];
			for (int i = 0; i < amount + 2; i++) {
				searchNeedle[i] = freeSpace;
			}
			return searchForFirst(rom, offset, searchNeedle) + 2;
		} else {
			// Find 5 more than necessary and return into it as necessary for
			// 4-alignment,
			// to preserve stuff like FF terminators for strings
			// 161: and FFFF terminators for movesets 
			byte[] searchNeedle = new byte[amount + 5];
			for (int i = 0; i < amount + 5; i++) {
				searchNeedle[i] = freeSpace;
			}
			return (searchForFirst(rom, offset, searchNeedle) + 5) & ~3;
		}
	}

	public static List<Integer> search(byte[] haystack, byte[] needle) {
		return search(haystack, 0, needle);
	}

	public static List<Integer> search(byte[] haystack, int beginOffset,
			byte[] needle) {
		int currentMatchStart = beginOffset;
		int currentCharacterPosition = 0;

		int docSize = haystack.length;
		int needleSize = needle.length;

		int[] toFillTable = buildKMPSearchTable(needle);
		List<Integer> results = new ArrayList<Integer>();

		while ((currentMatchStart + currentCharacterPosition) < docSize) {

			if (needle[currentCharacterPosition] == (haystack[currentCharacterPosition
					+ currentMatchStart])) {
				currentCharacterPosition = currentCharacterPosition + 1;

				if (currentCharacterPosition == (needleSize)) {
					results.add(currentMatchStart);
					currentCharacterPosition = 0;
					currentMatchStart = currentMatchStart + needleSize;

				}

			} else {
				currentMatchStart = currentMatchStart
						+ currentCharacterPosition
						- toFillTable[currentCharacterPosition];

				if (toFillTable[currentCharacterPosition] > -1) {
					currentCharacterPosition = toFillTable[currentCharacterPosition];
				}

				else {
					currentCharacterPosition = 0;

				}

			}
		}
		return results;
	}

	public static int searchForFirst(byte[] haystack, int beginOffset,
			byte[] needle) {
		int currentMatchStart = beginOffset;
		int currentCharacterPosition = 0;

		int docSize = haystack.length;
		int needleSize = needle.length;

		int[] toFillTable = buildKMPSearchTable(needle);

		while ((currentMatchStart + currentCharacterPosition) < docSize) {

			if (needle[currentCharacterPosition] == (haystack[currentCharacterPosition
					+ currentMatchStart])) {
				currentCharacterPosition = currentCharacterPosition + 1;

				if (currentCharacterPosition == (needleSize)) {
					return currentMatchStart;
				}

			} else {
				currentMatchStart = currentMatchStart
						+ currentCharacterPosition
						- toFillTable[currentCharacterPosition];

				if (toFillTable[currentCharacterPosition] > -1) {
					currentCharacterPosition = toFillTable[currentCharacterPosition];
				}

				else {
					currentCharacterPosition = 0;

				}

			}
		}
		return -1;
	}

	private static int[] buildKMPSearchTable(byte[] needle) {
		int[] stable = new int[needle.length];
		int pos = 2;
		int j = 0;
		stable[0] = -1;
		stable[1] = 0;
		while (pos < needle.length) {
			if (needle[pos - 1] == needle[j]) {
				stable[pos] = j + 1;
				pos++;
				j++;
			} else if (j > 0) {
				j = stable[j];
			} else {
				stable[pos] = 0;
				pos++;
			}
		}
		return stable;
	}

	public static String rewriteDescriptionForNewLineSize(String moveDesc,
			String newline, int lineSize, StringSizeDeterminer ssd) {
		// We rewrite the description we're given based on some new chars per
		// line.
		moveDesc = moveDesc.replace("-" + newline, "").replace(newline, " ");
		// Keep spatk/spdef as one word on one line
		moveDesc = moveDesc.replace("Sp. Atk", "Sp__Atk");
		moveDesc = moveDesc.replace("Sp. Def", "Sp__Def");
		moveDesc = moveDesc.replace("SP. ATK", "SP__ATK");
		moveDesc = moveDesc.replace("SP. DEF", "SP__DEF");
		String[] words = moveDesc.split(" ");
		StringBuilder fullDesc = new StringBuilder();
		StringBuilder thisLine = new StringBuilder();
		int currLineWC = 0;
		int currLineCC = 0;
		int linesWritten = 0;
		for (int i = 0; i < words.length; i++) {
			// Reverse the spatk/spdef preservation from above
			words[i] = words[i].replace("SP__", "SP. ");
			words[i] = words[i].replace("Sp__", "Sp. ");
			int reqLength = ssd.lengthFor(words[i]);
			if (currLineWC > 0) {
				reqLength++;
			}
			if (currLineCC + reqLength <= lineSize) {
				// add to current line
				if (currLineWC > 0) {
					thisLine.append(' ');
				}
				thisLine.append(words[i]);
				currLineWC++;
				currLineCC += reqLength;
			} else {
				// Save current line, if applicable
				if (currLineWC > 0) {
					if (linesWritten > 0) {
						fullDesc.append(newline);
					}
					fullDesc.append(thisLine.toString());
					linesWritten++;
					thisLine = new StringBuilder();
				}
				// Start the new line
				thisLine.append(words[i]);
				currLineWC = 1;
				currLineCC = ssd.lengthFor(words[i]);
			}
		}

		// If the last line has anything add it
		if (currLineWC > 0) {
			if (linesWritten > 0) {
				fullDesc.append(newline);
			}
			fullDesc.append(thisLine.toString());
			linesWritten++;
		}

		return fullDesc.toString();
	}

	public interface StringSizeDeterminer {
		public int lengthFor(String encodedText);
	}

	public static class StringLengthSD implements StringSizeDeterminer {

		@Override
		public int lengthFor(String encodedText) {
			return encodedText.length();
		}

	}

}
