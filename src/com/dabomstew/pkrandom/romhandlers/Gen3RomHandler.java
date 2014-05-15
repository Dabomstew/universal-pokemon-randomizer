package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  Gen3RomHandler.java - randomizer handler for R/S/E/FR/LG.				--*/
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

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.RandomSource;
import com.dabomstew.pkrandom.RomFunctions;
import com.dabomstew.pkrandom.pokemon.Encounter;
import com.dabomstew.pkrandom.pokemon.EncounterSet;
import com.dabomstew.pkrandom.pokemon.Evolution;
import com.dabomstew.pkrandom.pokemon.EvolutionType;
import com.dabomstew.pkrandom.pokemon.ExpCurve;
import com.dabomstew.pkrandom.pokemon.IngameTrade;
import com.dabomstew.pkrandom.pokemon.ItemList;
import com.dabomstew.pkrandom.pokemon.Move;
import com.dabomstew.pkrandom.pokemon.MoveLearnt;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Trainer;
import com.dabomstew.pkrandom.pokemon.TrainerPokemon;
import com.dabomstew.pkrandom.pokemon.Type;

public class Gen3RomHandler extends AbstractGBRomHandler {

	private static final int[] hoennToNum = new int[] { 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 252, 253, 254, 255, 256, 257, 258, 259, 260, 261, 262, 263,
			264, 265, 266, 267, 268, 269, 270, 271, 272, 273, 274, 275, 290,
			291, 292, 276, 277, 285, 286, 327, 278, 279, 283, 284, 320, 321,
			300, 301, 352, 343, 344, 299, 324, 302, 339, 340, 370, 341, 342,
			349, 350, 318, 319, 328, 329, 330, 296, 297, 309, 310, 322, 323,
			363, 364, 365, 331, 332, 361, 362, 337, 338, 298, 325, 326, 311,
			312, 303, 307, 308, 333, 334, 360, 355, 356, 315, 287, 288, 289,
			316, 317, 357, 293, 294, 295, 366, 367, 368, 359, 353, 354, 336,
			335, 369, 304, 305, 306, 351, 313, 314, 345, 346, 347, 348, 280,
			281, 282, 371, 372, 373, 374, 375, 376, 377, 378, 379, 382, 383,
			384, 380, 381, 385, 386, 358 };
	private static final int[] numToHoenn = new int[] { 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 252, 253, 254, 255, 256, 257, 258, 259, 260, 261, 262, 263,
			264, 265, 266, 267, 268, 269, 270, 271, 272, 273, 274, 275, 279,
			280, 284, 285, 367, 368, 369, 286, 287, 281, 282, 339, 340, 341,
			276, 277, 278, 345, 346, 347, 310, 311, 325, 295, 290, 291, 297,
			330, 357, 358, 359, 331, 332, 312, 313, 328, 329, 361, 362, 338,
			342, 343, 305, 306, 288, 289, 314, 315, 296, 326, 327, 283, 307,
			308, 309, 319, 320, 333, 334, 355, 354, 323, 324, 298, 299, 301,
			302, 293, 294, 363, 364, 365, 366, 303, 304, 360, 292, 352, 353,
			336, 337, 344, 386, 351, 335, 321, 322, 316, 317, 318, 348, 349,
			350, 356, 300, 370, 371, 372, 373, 374, 375, 376, 377, 378, 382,
			383, 379, 380, 381, 384, 385 };

	private static int pokeNumTo3GIndex(int pokenum) {
		if (pokenum < 252) {
			return pokenum;
		} else {
			return numToHoenn[pokenum] + 25;
		}
	}

	private static int poke3GIndexToNum(int thirdgindex) {
		if (thirdgindex < 252) {
			return thirdgindex;
		} else {
			return hoennToNum[thirdgindex - 25];
		}
	}

	private static class RomEntry {
		private String name;
		private String romCode;
		private String tableFile;
		private int version;
		private int romType;
		private boolean copyStaticPokemon;
		private Map<String, Integer> entries = new HashMap<String, Integer>();
		private Map<String, int[]> arrayEntries = new HashMap<String, int[]>();
		private List<StaticPokemon> staticPokemon = new ArrayList<StaticPokemon>();

		private int getValue(String key) {
			if (!entries.containsKey(key)) {
				entries.put(key, 0);
			}
			return entries.get(key);
		}
	}

	private static List<RomEntry> roms;
	private static ItemList allowedItems;

	private static final Type[] typeTable = constructTypeTable();

	static {
		loadROMInfo();
		setupAllowedItems();
	}

	private static Type[] constructTypeTable() {
		Type[] table = new Type[256];
		table[0x00] = Type.NORMAL;
		table[0x01] = Type.FIGHTING;
		table[0x02] = Type.FLYING;
		table[0x03] = Type.POISON;
		table[0x04] = Type.GROUND;
		table[0x05] = Type.ROCK;
		table[0x06] = Type.BUG;
		table[0x07] = Type.GHOST;
		table[0x08] = Type.STEEL;
		table[0x0A] = Type.FIRE;
		table[0x0B] = Type.WATER;
		table[0x0C] = Type.GRASS;
		table[0x0D] = Type.ELECTRIC;
		table[0x0E] = Type.PSYCHIC;
		table[0x0F] = Type.ICE;
		table[0x10] = Type.DRAGON;
		table[0x11] = Type.DARK;
		return table;
	}

	private static void loadROMInfo() {
		roms = new ArrayList<RomEntry>();
		RomEntry current = null;
		try {
			Scanner sc = new Scanner(
					FileFunctions.openConfig("gen3_offsets.ini"), "UTF-8");
			while (sc.hasNextLine()) {
				String q = sc.nextLine().trim();
				if (q.contains("//")) {
					q = q.substring(0, q.indexOf("//")).trim();
				}
				if (!q.isEmpty()) {
					if (q.startsWith("[") && q.endsWith("]")) {
						// New rom
						current = new RomEntry();
						current.name = q.substring(1, q.length() - 1);
						roms.add(current);
					} else {
						String[] r = q.split("=", 2);
						if (r.length == 1) {
							System.err.println("invalid entry " + q);
							continue;
						}
						if (r[1].endsWith("\r\n")) {
							r[1] = r[1].substring(0, r[1].length() - 2);
						}
						r[1] = r[1].trim();
						// Static Pokemon?
						if (r[0].equals("StaticPokemon[]")) {
							if (r[1].startsWith("[") && r[1].endsWith("]")) {
								String[] offsets = r[1].substring(1,
										r[1].length() - 1).split(",");
								int[] offs = new int[offsets.length];
								int c = 0;
								for (String off : offsets) {
									offs[c++] = parseRIInt(off);
								}
								current.staticPokemon.add(new StaticPokemon(
										offs));
							} else {
								int offs = parseRIInt(r[1]);
								current.staticPokemon.add(new StaticPokemon(
										offs));
							}
						} else if (r[0].equals("Game")) {
							current.romCode = r[1];
						} else if (r[0].equals("Version")) {
							current.version = parseRIInt(r[1]);
						} else if (r[0].equals("Type")) {
							if (r[1].equalsIgnoreCase("Ruby")) {
								current.romType = RomType_Ruby;
							} else if (r[1].equalsIgnoreCase("Sapp")) {
								current.romType = RomType_Sapp;
							} else if (r[1].equalsIgnoreCase("Em")) {
								current.romType = RomType_Em;
							} else if (r[1].equalsIgnoreCase("FRLG")) {
								current.romType = RomType_FRLG;
							} else {
								System.err.println("unrecognised rom type: "
										+ r[1]);
							}
						} else if (r[0].equals("TableFile")) {
							current.tableFile = r[1];
						} else if (r[0].equals("CopyStaticPokemon")) {
							int csp = parseRIInt(r[1]);
							current.copyStaticPokemon = (csp > 0);
						} else if (r[0].equals("CopyFrom")) {
							for (RomEntry otherEntry : roms) {
								if (r[1].equalsIgnoreCase(otherEntry.name)) {
									// copy from here
									current.arrayEntries
											.putAll(otherEntry.arrayEntries);
									current.entries.putAll(otherEntry.entries);
									if (current.copyStaticPokemon) {
										current.staticPokemon
												.addAll(otherEntry.staticPokemon);
										current.entries.put(
												"StaticPokemonSupport", 1);
									} else {
										current.entries.put(
												"StaticPokemonSupport", 0);
									}
									current.tableFile = otherEntry.tableFile;
								}
							}
						} else {
							if (r[1].startsWith("[") && r[1].endsWith("]")) {
								String[] offsets = r[1].substring(1,
										r[1].length() - 1).split(",");
								if (offsets.length == 1
										&& offsets[0].trim().isEmpty()) {
									current.arrayEntries.put(r[0], new int[0]);
								} else {
									int[] offs = new int[offsets.length];
									int c = 0;
									for (String off : offsets) {
										offs[c++] = parseRIInt(off);
									}
									current.arrayEntries.put(r[0], offs);
								}
							} else {
								int offs = parseRIInt(r[1]);
								current.entries.put(r[0], offs);
							}
						}
					}
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
		}

	}

	private static int parseRIInt(String off) {
		int radix = 10;
		off = off.trim().toLowerCase();
		if (off.startsWith("0x") || off.startsWith("&h")) {
			radix = 16;
			off = off.substring(2);
		}
		try {
			return Integer.parseInt(off, radix);
		} catch (NumberFormatException ex) {
			System.err.println("invalid base " + radix + "number " + off);
			return 0;
		}
	}

	private void loadTextTable(String filename) {
		try {
			Scanner sc = new Scanner(
					FileFunctions.openConfig(filename + ".tbl"), "UTF-8");
			while (sc.hasNextLine()) {
				String q = sc.nextLine();
				if (!q.trim().isEmpty()) {
					String[] r = q.split("=", 2);
					if (r[1].endsWith("\r\n")) {
						r[1] = r[1].substring(0, r[1].length() - 2);
					}
					tb[Integer.parseInt(r[0], 16)] = r[1];
					d.put(r[1], (byte) Integer.parseInt(r[0], 16));
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
		}

	}

	private static void setupAllowedItems() {
		allowedItems = new ItemList(376);
		// Key items (+1 unknown item)
		allowedItems.banRange(259, 30);
		allowedItems.banRange(349, 28);
		// Unknown blank items
		allowedItems.banRange(52, 11);
		allowedItems.banRange(87, 6);
		allowedItems.banRange(99, 4);
		allowedItems.banRange(112, 9);
		allowedItems.banRange(176, 3);
		allowedItems.banRange(226, 28);
		allowedItems.banRange(347, 2);
		allowedItems.banSingles(72, 82, 105, 267);
		// HMs
		allowedItems.banRange(339, 8);
		// TMs
		allowedItems.tmRange(289, 50);
	}

	private static byte typeToByte(Type type) {
		if (type == null) {
			return 0x09; // ???-type
		}
		switch (type) {
		case NORMAL:
			return 0x00;
		case FIGHTING:
			return 0x01;
		case FLYING:
			return 0x02;
		case POISON:
			return 0x03;
		case GROUND:
			return 0x04;
		case ROCK:
			return 0x05;
		case BUG:
			return 0x06;
		case GHOST:
			return 0x07;
		case FIRE:
			return 0x0A;
		case WATER:
			return 0x0B;
		case GRASS:
			return 0x0C;
		case ELECTRIC:
			return 0x0D;
		case PSYCHIC:
			return 0x0E;
		case ICE:
			return 0x0F;
		case DRAGON:
			return 0x10;
		case STEEL:
			return 0x08;
		case DARK:
			return 0x11;
		default:
			return 0; // normal by default
		}
	}

	// This ROM's data
	private Pokemon[] pokes;
	private List<Pokemon> pokemonList;
	private Move[] moves;
	private RomEntry romEntry;
	private boolean havePatchedObedience;
	public String[] tb;
	public Map<String, Byte> d;
	private String[] abilityNames;
	private String[] itemNames;
	private boolean mapLoadingDone;
	private List<Integer> itemOffs;
	private String[][] mapNames;

	private static final int RomType_Ruby = 0;
	private static final int RomType_Sapp = 1;
	private static final int RomType_Em = 2;
	private static final int RomType_FRLG = 3;

	@Override
	public boolean detectRom(byte[] rom) {
		if (rom.length != 8388608 && rom.length != 16777216
				&& rom.length != 33554432) {
			return false; // size check
		}
		// Special case for Emerald unofficial translation
		if (romName(rom, "YJencrypted")) {
			// give it a rom code so it can be detected
			rom[0xAC] = 'B';
			rom[0xAD] = 'P';
			rom[0xAE] = 'E';
			rom[0xAF] = 'T';
			rom[0xBD] = 0x66;
		}
		// Wild Pokemon header
		if (find(rom, "0348048009E00000FFFF0000") == -1) {
			return false;
		}
		// Map Banks header
		if (find(rom, "80180068890B091808687047") == -1) {
			return false;
		}
		for (RomEntry re : roms) {
			if (romCode(rom, re.romCode) && (rom[0xBC] & 0xFF) == re.version) {
				return true; // match
			}
		}
		return false; // GBA rom we don't support yet
	}

	@Override
	public void loadedRom() {
		for (RomEntry re : roms) {
			if (romCode(rom, re.romCode) && (rom[0xBC] & 0xFF) == re.version) {
				romEntry = re;
				break;
			}
		}

		tb = new String[256];
		d = new HashMap<String, Byte>();

		// Pokemon names offset
		if (romEntry.romType == RomType_Ruby
				|| romEntry.romType == RomType_Sapp) {
			int baseNomOffset = find(rom, "30B50025084CC8F7");
			romEntry.entries
					.put("PokemonNames", readPointer(baseNomOffset - 4));
		} else {
			romEntry.entries.put("PokemonNames", readPointer(0x144));
			romEntry.entries.put("MoveNames", readPointer(0x148));
			romEntry.entries.put("AbilityNames", readPointer(0x1C0));
			romEntry.entries.put("ItemData", readPointer(0x1C8));
			romEntry.entries.put("MoveData", readPointer(0x1CC) + 0xC);
		}

		loadTextTable(romEntry.tableFile);
		loadPokemonStats();
		pokemonList = Arrays.asList(pokes);
		loadMoves();

		// Get wild Pokemon offset
		int baseWPOffset = findMultiple(rom, "0348048009E00000FFFF0000").get(0);
		romEntry.entries.put("WildPokemon", readPointer(baseWPOffset + 12));

		// map banks
		int baseMapsOffset = findMultiple(rom, "80180068890B091808687047").get(
				0);
		romEntry.entries.put("MapHeaders", readPointer(baseMapsOffset + 12));

		// map labels
		if (romEntry.romType == RomType_FRLG) {
			int baseMLOffset = find(rom, "AC470000AE470000B0470000");
			romEntry.entries.put("MapLabels", readPointer(baseMLOffset + 12));
		} else {
			int baseMLOffset = find(rom, "C078288030BC01BC00470000");
			romEntry.entries.put("MapLabels", readPointer(baseMLOffset + 12));
		}

		mapLoadingDone = false;
		loadAbilityNames();
		loadItemNames();

	}

	@Override
	public void savingRom() {
		savePokemonStats();
		saveMoves();
	}

	private void loadPokemonStats() {
		pokes = new Pokemon[387];
		// Fetch our names
		String[] pokeNames = readPokemonNames();
		int offs = romEntry.getValue("PokemonStats");
		// Get base stats
		for (int i = 1; i <= 386; i++) {
			pokes[i] = new Pokemon();
			pokes[i].number = i;
			loadBasicPokeStats(pokes[i], offs + (pokeNumTo3GIndex(i) - 1)
					* 0x1C);
			// Name?
			pokes[i].name = pokeNames[pokeNumTo3GIndex(i)];
		}

	}

	private void savePokemonStats() {
		// Write pokemon names
		int offs = romEntry.getValue("PokemonNames");
		int nameLen = romEntry.getValue("PokemonNameLength");
		for (int i = 1; i <= 386; i++) {
			int stringOffset = offs + pokeNumTo3GIndex(i) * nameLen;
			writeFixedLengthString(pokes[i].name, stringOffset, nameLen);
		}
		// Write pokemon stats
		int offs2 = romEntry.getValue("PokemonStats");
		for (int i = 1; i <= 386; i++) {
			saveBasicPokeStats(pokes[i], offs2 + (pokeNumTo3GIndex(i) - 1)
					* 0x1C);
		}
	}

	private void loadMoves() {
		moves = new Move[355];
		int offs = romEntry.getValue("MoveData");
		int nameoffs = romEntry.getValue("MoveNames");
		int namelen = romEntry.getValue("MoveNameLength");
		for (int i = 1; i <= 354; i++) {
			moves[i] = new Move();
			moves[i].name = readFixedLengthString(nameoffs + i * namelen,
					namelen);
			moves[i].number = i;
			moves[i].effectIndex = rom[offs + (i - 1) * 0xC] & 0xFF;
			moves[i].hitratio = ((rom[offs + (i - 1) * 0xC + 3] & 0xFF) + 0);
			moves[i].power = rom[offs + (i - 1) * 0xC + 1] & 0xFF;
			moves[i].pp = rom[offs + (i - 1) * 0xC + 4] & 0xFF;
			moves[i].type = typeTable[rom[offs + (i - 1) * 0xC + 2]];
		}

	}

	private void saveMoves() {
		int offs = romEntry.getValue("MoveData");
		for (int i = 1; i <= 354; i++) {
			rom[offs + (i - 1) * 0xC] = (byte) moves[i].effectIndex;
			rom[offs + (i - 1) * 0xC + 1] = (byte) moves[i].power;
			rom[offs + (i - 1) * 0xC + 2] = typeToByte(moves[i].type);
			int hitratio = (int) Math.round(moves[i].hitratio);
			if (hitratio < 0) {
				hitratio = 0;
			}
			if (hitratio > 100) {
				hitratio = 100;
			}
			rom[offs + (i - 1) * 0xC + 3] = (byte) hitratio;
			rom[offs + (i - 1) * 0xC + 4] = (byte) moves[i].pp;
		}
	}

	public List<Move> getMoves() {
		return Arrays.asList(moves);
	}

	private void loadBasicPokeStats(Pokemon pkmn, int offset) {
		pkmn.hp = rom[offset] & 0xFF;
		pkmn.attack = rom[offset + 1] & 0xFF;
		pkmn.defense = rom[offset + 2] & 0xFF;
		pkmn.speed = rom[offset + 3] & 0xFF;
		pkmn.spatk = rom[offset + 4] & 0xFF;
		pkmn.spdef = rom[offset + 5] & 0xFF;
		// Type
		pkmn.primaryType = typeTable[rom[offset + 6] & 0xFF];
		pkmn.secondaryType = typeTable[rom[offset + 7] & 0xFF];
		// Only one type?
		if (pkmn.secondaryType == pkmn.primaryType) {
			pkmn.secondaryType = null;
		}
		pkmn.catchRate = rom[offset + 8] & 0xFF;
		pkmn.growthCurve = ExpCurve.fromByte(rom[offset + 19]);
		// Abilities
		pkmn.ability1 = rom[offset + 22] & 0xFF;
		pkmn.ability2 = rom[offset + 23] & 0xFF;

		// Held Items?
		int item1 = readWord(offset + 12);
		int item2 = readWord(offset + 14);

		if (item1 == item2) {
			// guaranteed
			pkmn.guaranteedHeldItem = item1;
			pkmn.commonHeldItem = 0;
			pkmn.rareHeldItem = 0;
		} else {
			pkmn.guaranteedHeldItem = 0;
			pkmn.commonHeldItem = item1;
			pkmn.rareHeldItem = item2;
		}
		pkmn.darkGrassHeldItem = -1;
	}

	private void saveBasicPokeStats(Pokemon pkmn, int offset) {
		rom[offset] = (byte) pkmn.hp;
		rom[offset + 1] = (byte) pkmn.attack;
		rom[offset + 2] = (byte) pkmn.defense;
		rom[offset + 3] = (byte) pkmn.speed;
		rom[offset + 4] = (byte) pkmn.spatk;
		rom[offset + 5] = (byte) pkmn.spdef;
		rom[offset + 6] = typeToByte(pkmn.primaryType);
		if (pkmn.secondaryType == null) {
			rom[offset + 7] = rom[offset + 6];
		} else {
			rom[offset + 7] = typeToByte(pkmn.secondaryType);
		}
		rom[offset + 8] = (byte) pkmn.catchRate;
		rom[offset + 19] = pkmn.growthCurve.toByte();

		rom[offset + 22] = (byte) pkmn.ability1;
		if (pkmn.ability2 == 0) {
			// required to not break evos with random ability
			rom[offset + 23] = (byte) pkmn.ability1;
		} else {
			rom[offset + 23] = (byte) pkmn.ability2;
		}

		// Held items
		if (pkmn.guaranteedHeldItem > 0) {
			writeWord(offset + 12, pkmn.guaranteedHeldItem);
			writeWord(offset + 14, pkmn.guaranteedHeldItem);
		} else {
			writeWord(offset + 12, pkmn.commonHeldItem);
			writeWord(offset + 14, pkmn.rareHeldItem);
		}
	}

	private String[] readPokemonNames() {
		int offs = romEntry.getValue("PokemonNames");
		int nameLen = romEntry.getValue("PokemonNameLength");
		String[] names = new String[412];
		for (int i = 1; i <= 411; i++) {
			names[i] = readFixedLengthString(offs + i * nameLen, nameLen);
		}
		return names;
	}

	private String readString(int offset, int maxLength) {
		StringBuilder string = new StringBuilder();
		for (int c = 0; c < maxLength; c++) {
			int currChar = rom[offset + c] & 0xFF;
			if (tb[currChar] != null) {
				string.append(tb[currChar]);
			} else {
				if (currChar == 0xFF) {
					break;
				} else if (currChar == 0xFD) {
					int nextChar = rom[offset + c + 1] & 0xFF;
					string.append("\\v" + String.format("%02X", nextChar));
					c++;
				} else {
					string.append("\\x" + String.format("%02X", currChar));
				}
			}
		}
		return string.toString();
	}

	private byte[] translateString(String text) {
		List<Byte> data = new ArrayList<Byte>();
		while (text.length() != 0) {
			int i = Math.max(0, 4 - text.length());
			if (text.charAt(0) == '\\' && text.charAt(1) == 'x') {
				data.add((byte) Integer.parseInt(text.substring(2, 4), 16));
				text = text.substring(4);
			} else if (text.charAt(0) == '\\' && text.charAt(1) == 'v') {
				data.add((byte) 0xFD);
				data.add((byte) Integer.parseInt(text.substring(2, 4), 16));
				text = text.substring(4);
			} else {
				while (!(d.containsKey(text.substring(0, 4 - i)) || (i == 4))) {
					i++;
				}
				if (i == 4) {
					text = text.substring(1);
				} else {
					data.add(d.get(text.substring(0, 4 - i)));
					text = text.substring(4 - i);
				}
			}
		}
		byte[] ret = new byte[data.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = data.get(i);
		}
		return ret;
	}

	private String readFixedLengthString(int offset, int length) {
		return readString(offset, length);
	}

	public String readVariableLengthString(int offset) {
		return readString(offset, Integer.MAX_VALUE);
	}

	private void writeFixedLengthString(String str, int offset, int length) {
		byte[] translated = translateString(str);
		int len = Math.min(translated.length, length);
		System.arraycopy(translated, 0, rom, offset, len);
		if (len < length) {
			rom[offset + len] = (byte) 0xFF;
			len++;
		}
		while (len < length) {
			rom[offset + len] = 0;
			len++;
		}
	}

	private void writeVariableLengthString(String str, int offset) {
		byte[] translated = translateString(str);
		System.arraycopy(translated, 0, rom, offset, translated.length);
		rom[offset + translated.length] = (byte) 0xFF;
	}

	private int lengthOfStringAt(int offset) {
		int len = 0;
		while ((rom[offset + (len++)] & 0xFF) != 0xFF) {
		}
		return len - 1;
	}

	public byte[] traduire(String str) {
		return translateString(str);
	}

	private boolean romName(byte[] rom, String name) {
		try {
			int sigOffset = 0xA0;
			byte[] sigBytes = name.getBytes("US-ASCII");
			for (int i = 0; i < sigBytes.length; i++) {
				if (rom[sigOffset + i] != sigBytes[i]) {
					return false;
				}
			}
			return true;
		} catch (UnsupportedEncodingException ex) {
			return false;
		}

	}

	private boolean romCode(byte[] rom, String codeToCheck) {
		try {
			int sigOffset = 0xAC;
			byte[] sigBytes = codeToCheck.getBytes("US-ASCII");
			for (int i = 0; i < sigBytes.length; i++) {
				if (rom[sigOffset + i] != sigBytes[i]) {
					return false;
				}
			}
			return true;
		} catch (UnsupportedEncodingException ex) {
			return false;
		}

	}

	private int readPointer(int offset) {
		return (rom[offset] & 0xFF) + ((rom[offset + 1] & 0xFF) << 8)
				+ ((rom[offset + 2] & 0xFF) << 16)
				+ (((rom[offset + 3] & 0xFF) - 8) << 24);
	}

	private void writePointer(int offset, int pointer) {
		rom[offset] = (byte) (pointer & 0xFF);
		rom[offset + 1] = (byte) ((pointer >> 8) & 0xFF);
		rom[offset + 2] = (byte) ((pointer >> 16) & 0xFF);
		rom[offset + 3] = (byte) (((pointer >> 24) & 0xFF) + 8);
	}

	@Override
	public boolean isInGame(Pokemon pkmn) {
		return (pkmn.number >= 1 && pkmn.number <= 386);
	}

	@Override
	public boolean isInGame(int pokemonNumber) {
		return (pokemonNumber >= 1 && pokemonNumber <= 386);
	}

	@Override
	public List<Pokemon> getStarters() {
		List<Pokemon> starters = new ArrayList<Pokemon>();
		int baseOffset = romEntry.getValue("StarterPokemon");
		if (romEntry.romType == RomType_Ruby
				|| romEntry.romType == RomType_Sapp
				|| romEntry.romType == RomType_Em) {
			// do something
			Pokemon starter1 = pokes[poke3GIndexToNum(readWord(baseOffset))];
			Pokemon starter2 = pokes[poke3GIndexToNum(readWord(baseOffset + 2))];
			Pokemon starter3 = pokes[poke3GIndexToNum(readWord(baseOffset + 4))];
			starters.add(starter1);
			starters.add(starter2);
			starters.add(starter3);
		} else {
			// do something else
			Pokemon starter1 = pokes[poke3GIndexToNum(readWord(baseOffset))];
			Pokemon starter2 = pokes[poke3GIndexToNum(readWord(baseOffset + 515))];
			Pokemon starter3 = pokes[poke3GIndexToNum(readWord(baseOffset + 461))];
			starters.add(starter1);
			starters.add(starter2);
			starters.add(starter3);
		}
		return starters;
	}

	@Override
	public boolean setStarters(List<Pokemon> newStarters) {
		if (newStarters.size() != 3) {
			return false;
		}
		for (Pokemon pkmn : newStarters) {
			if (!isInGame(pkmn)) {
				return false;
			}
		}
		// Support Deoxys/Mew starters in E/FR/LG
		if (!havePatchedObedience) {
			attemptObedienceEvolutionPatches();
		}
		int baseOffset = romEntry.getValue("StarterPokemon");

		int starter0 = pokeNumTo3GIndex(newStarters.get(0).number);
		int starter1 = pokeNumTo3GIndex(newStarters.get(1).number);
		int starter2 = pokeNumTo3GIndex(newStarters.get(2).number);
		if (romEntry.romType == RomType_Ruby
				|| romEntry.romType == RomType_Sapp
				|| romEntry.romType == RomType_Em) {

			// US
			// order: 0, 1, 2
			writeWord(baseOffset, starter0);
			writeWord(baseOffset + 2, starter1);
			writeWord(baseOffset + 4, starter2);

		} else {

			// frlg:

			// US
			// order: 0, 1, 2
			writeWord(baseOffset, starter0);
			writeWord(baseOffset + 5, starter1);

			writeWord(baseOffset + 515, starter1);
			writeWord(baseOffset + 520, starter2);

			writeWord(baseOffset + 461, starter2);
			writeWord(baseOffset + 466, starter0);

			if (romEntry.romCode.charAt(3) != 'J'
					&& romEntry.romCode.charAt(3) != 'B') {
				// Update PROF. Oak's descriptions for each starter
				// First result for each STARTERNAME is the text we need
				writeFRLGStarterText(pokes[1].name, newStarters.get(0),
						"you want to go with\\nthe ");
				writeFRLGStarterText(pokes[4].name, newStarters.get(1),
						"you’re claiming the\\n");
				writeFRLGStarterText(pokes[7].name, newStarters.get(2),
						"you’ve decided on the\\n");
			}
		}
		return true;

	}

	@Override
	public List<Integer> getStarterHeldItems() {
		List<Integer> sHeldItems = new ArrayList<Integer>();
		if (romEntry.romType == RomType_FRLG) {
			// offset from normal starter offset as a word
			int baseOffset = romEntry.getValue("StarterPokemon");
			sHeldItems.add(readWord(baseOffset + 218));
		} else {
			int baseOffset = romEntry.getValue("StarterItems");
			int i1 = rom[baseOffset] & 0xFF;
			int i2 = rom[baseOffset + 2] & 0xFF;
			if (i2 == 0) {
				sHeldItems.add(i1);
			} else {
				sHeldItems.add(i2 + 255);
			}
		}
		return sHeldItems;
	}

	@Override
	public void setStarterHeldItems(List<Integer> items) {
		if (items.size() != 1) {
			return;
		}
		int item = items.get(0);
		if (romEntry.romType == RomType_FRLG) {
			// offset from normal starter offset as a word
			int baseOffset = romEntry.getValue("StarterPokemon");
			writeWord(baseOffset + 218, item);
		} else {
			int baseOffset = romEntry.getValue("StarterItems");
			if (item < 256) {
				rom[baseOffset] = (byte) item;
				rom[baseOffset + 2] = 0;
				rom[baseOffset + 3] = 0x32;
			} else {
				rom[baseOffset] = (byte) 0xFF;
				rom[baseOffset + 2] = (byte) (item - 255);
				rom[baseOffset + 3] = 0x32;
			}
		}
	}

	private void writeFRLGStarterText(String findName, Pokemon pkmn,
			String oakText) {
		List<Integer> foundTexts = RomFunctions.search(rom, traduire(findName));
		if (foundTexts.size() > 0) {
			int offset = foundTexts.get(0);
			String pokeName = pkmn.name;
			String pokeType = pkmn.primaryType.toString();
			if (pokeType.equals("NORMAL") && pkmn.secondaryType != null) {
				pokeType = pkmn.secondaryType.toString();
			}
			String speech = pokeName + " is your choice.\\pSo, \\v01, "
					+ oakText + pokeType + " POKéMON " + pokeName + "?";
			writeFixedLengthString(speech, offset, lengthOfStringAt(offset) + 1);
		}
	}

	@Override
	public void shufflePokemonStats() {
		for (int i = 1; i <= 386; i++) {
			pokes[i].shuffleStats();
		}
	}

	@Override
	public List<EncounterSet> getEncounters(boolean useTimeOfDay) {
		if (!mapLoadingDone) {
			preprocessMaps();
			mapLoadingDone = true;
		}

		int startOffs = romEntry.getValue("WildPokemon");
		List<EncounterSet> encounterAreas = new ArrayList<EncounterSet>();
		Set<Integer> seenOffsets = new TreeSet<Integer>();
		int offs = startOffs;
		while (true) {
			// Read pointers
			int bank = rom[offs] & 0xFF;
			int map = rom[offs + 1] & 0xFF;
			if (bank == 0xFF && map == 0xFF) {
				break;
			}

			String mapName = mapNames[bank][map];

			int grassPokes = readPointer(offs + 4);
			int waterPokes = readPointer(offs + 8);
			int treePokes = readPointer(offs + 12);
			int fishPokes = readPointer(offs + 16);

			// Add pokemanz
			if (grassPokes != -134217728 && rom[grassPokes] != 0
					&& !seenOffsets.contains(readPointer(grassPokes + 4))) {
				encounterAreas.add(readWildArea(grassPokes, 12, mapName
						+ " Grass/Cave"));
				seenOffsets.add(readPointer(grassPokes + 4));
			}
			if (waterPokes != -134217728 && rom[waterPokes] != 0
					&& !seenOffsets.contains(readPointer(waterPokes + 4))) {
				encounterAreas.add(readWildArea(waterPokes, 5, mapName
						+ " Surfing"));
				seenOffsets.add(readPointer(waterPokes + 4));
			}
			if (treePokes != -134217728 && rom[treePokes] != 0
					&& !seenOffsets.contains(readPointer(treePokes + 4))) {
				encounterAreas.add(readWildArea(treePokes, 5, mapName
						+ " Rock Smash"));
				seenOffsets.add(readPointer(treePokes + 4));
			}
			if (fishPokes != -134217728 && rom[fishPokes] != 0
					&& !seenOffsets.contains(readPointer(fishPokes + 4))) {
				encounterAreas.add(readWildArea(fishPokes, 10, mapName
						+ " Fishing"));
				seenOffsets.add(readPointer(fishPokes + 4));
			}

			offs += 20;
		}
		if (romEntry.arrayEntries.containsKey("BattleTrappersBanned")) {
			// Some encounter sets aren't allowed to have Pokemon
			// with Arena Trap, Shadow Tag etc.
			int[] bannedAreas = romEntry.arrayEntries
					.get("BattleTrappersBanned");
			for (int areaIdx : bannedAreas) {
				encounterAreas.get(areaIdx).battleTrappersBanned = true;
			}
		}
		return encounterAreas;
	}

	private EncounterSet readWildArea(int offset, int numOfEntries,
			String setName) {
		EncounterSet thisSet = new EncounterSet();
		thisSet.rate = rom[offset];
		thisSet.displayName = setName;
		// Grab the *real* pointer to data
		int dataOffset = readPointer(offset + 4);
		// Read the entries
		for (int i = 0; i < numOfEntries; i++) {
			// min, max, species, species
			Encounter enc = new Encounter();
			enc.level = rom[dataOffset + i * 4];
			enc.maxLevel = rom[dataOffset + i * 4 + 1];
			try {
				enc.pokemon = pokes[poke3GIndexToNum(readWord(dataOffset + i
						* 4 + 2))];
			} catch (ArrayIndexOutOfBoundsException ex) {
				throw ex;
			}
			thisSet.encounters.add(enc);
		}
		return thisSet;
	}

	@Override
	public void setEncounters(boolean useTimeOfDay,
			List<EncounterSet> encounters) {
		// Support Deoxys/Mew catches in E/FR/LG
		if (!havePatchedObedience) {
			attemptObedienceEvolutionPatches();
		}

		int startOffs = romEntry.getValue("WildPokemon");
		Iterator<EncounterSet> encounterAreas = encounters.iterator();
		Set<Integer> seenOffsets = new TreeSet<Integer>();
		int offs = startOffs;
		while (true) {
			// Read pointers
			int bank = rom[offs] & 0xFF;
			int map = rom[offs + 1] & 0xFF;
			if (bank == 0xFF && map == 0xFF) {
				break;
			}

			int grassPokes = readPointer(offs + 4);
			int waterPokes = readPointer(offs + 8);
			int treePokes = readPointer(offs + 12);
			int fishPokes = readPointer(offs + 16);

			// Add pokemanz
			if (grassPokes != -134217728 && rom[grassPokes] != 0
					&& !seenOffsets.contains(readPointer(grassPokes + 4))) {
				writeWildArea(grassPokes, 12, encounterAreas.next());
				seenOffsets.add(readPointer(grassPokes + 4));
			}
			if (waterPokes != -134217728 && rom[waterPokes] != 0
					&& !seenOffsets.contains(readPointer(waterPokes + 4))) {
				writeWildArea(waterPokes, 5, encounterAreas.next());
				seenOffsets.add(readPointer(waterPokes + 4));
			}
			if (treePokes != -134217728 && rom[treePokes] != 0
					&& !seenOffsets.contains(readPointer(treePokes + 4))) {
				writeWildArea(treePokes, 5, encounterAreas.next());
				seenOffsets.add(readPointer(treePokes + 4));
			}
			if (fishPokes != -134217728 && rom[fishPokes] != 0
					&& !seenOffsets.contains(readPointer(fishPokes + 4))) {
				writeWildArea(fishPokes, 10, encounterAreas.next());
				seenOffsets.add(readPointer(fishPokes + 4));
			}

			offs += 20;
		}
	}

	@Override
	public List<Pokemon> bannedForWildEncounters() {
		return Arrays.asList(pokes[201]); // Unown banned
	}

	@Override
	public List<Trainer> getTrainers() {
		int baseOffset = romEntry.getValue("TrainerData");
		int amount = romEntry.getValue("TrainerCount");
		int entryLen = romEntry.getValue("TrainerEntrySize");
		List<Trainer> theTrainers = new ArrayList<Trainer>();
		List<String> tcnames = this.getTrainerClassNames();
		for (int i = 1; i < amount; i++) {
			int trOffset = baseOffset + i * entryLen;
			Trainer tr = new Trainer();
			tr.offset = trOffset;
			int trainerclass = rom[trOffset + 1] & 0xFF;
			tr.trainerclass = (rom[trOffset + 2] & 0x80) > 0 ? 1 : 0;

			int pokeDataType = rom[trOffset] & 0xFF;
			int numPokes = rom[trOffset + (entryLen - 8)] & 0xFF;
			int pointerToPokes = readPointer(trOffset + (entryLen - 4));
			tr.poketype = pokeDataType;
			tr.name = this.readVariableLengthString(trOffset + 4);
			tr.fullDisplayName = tcnames.get(trainerclass) + " " + tr.name;
			// Pokemon data!
			if (pokeDataType == 0) {
				// blocks of 8 bytes
				for (int poke = 0; poke < numPokes; poke++) {
					TrainerPokemon thisPoke = new TrainerPokemon();
					thisPoke.AILevel = readWord(pointerToPokes + poke * 8);
					thisPoke.level = readWord(pointerToPokes + poke * 8 + 2);
					thisPoke.pokemon = pokes[poke3GIndexToNum(readWord(pointerToPokes
							+ poke * 8 + 4))];
					tr.pokemon.add(thisPoke);
				}
			} else if (pokeDataType == 2) {
				// blocks of 8 bytes
				for (int poke = 0; poke < numPokes; poke++) {
					TrainerPokemon thisPoke = new TrainerPokemon();
					thisPoke.AILevel = readWord(pointerToPokes + poke * 8);
					thisPoke.level = readWord(pointerToPokes + poke * 8 + 2);
					thisPoke.pokemon = pokes[poke3GIndexToNum(readWord(pointerToPokes
							+ poke * 8 + 4))];
					thisPoke.heldItem = readWord(pointerToPokes + poke * 8 + 6);
					tr.pokemon.add(thisPoke);
				}
			} else if (pokeDataType == 1) {
				// blocks of 16 bytes
				for (int poke = 0; poke < numPokes; poke++) {
					TrainerPokemon thisPoke = new TrainerPokemon();
					thisPoke.AILevel = readWord(pointerToPokes + poke * 16);
					thisPoke.level = readWord(pointerToPokes + poke * 16 + 2);
					thisPoke.pokemon = pokes[poke3GIndexToNum(readWord(pointerToPokes
							+ poke * 16 + 4))];
					thisPoke.move1 = readWord(pointerToPokes + poke * 16 + 6);
					thisPoke.move2 = readWord(pointerToPokes + poke * 16 + 8);
					thisPoke.move3 = readWord(pointerToPokes + poke * 16 + 10);
					thisPoke.move4 = readWord(pointerToPokes + poke * 16 + 12);
					tr.pokemon.add(thisPoke);
				}
			} else if (pokeDataType == 3) {
				// blocks of 16 bytes
				for (int poke = 0; poke < numPokes; poke++) {
					TrainerPokemon thisPoke = new TrainerPokemon();
					thisPoke.AILevel = readWord(pointerToPokes + poke * 16);
					thisPoke.level = readWord(pointerToPokes + poke * 16 + 2);
					thisPoke.pokemon = pokes[poke3GIndexToNum(readWord(pointerToPokes
							+ poke * 16 + 4))];
					thisPoke.heldItem = readWord(pointerToPokes + poke * 16 + 6);
					thisPoke.move1 = readWord(pointerToPokes + poke * 16 + 8);
					thisPoke.move2 = readWord(pointerToPokes + poke * 16 + 10);
					thisPoke.move3 = readWord(pointerToPokes + poke * 16 + 12);
					thisPoke.move4 = readWord(pointerToPokes + poke * 16 + 14);
					tr.pokemon.add(thisPoke);
				}
			}
			theTrainers.add(tr);
		}

		if (romEntry.romType == RomType_Ruby
				|| romEntry.romType == RomType_Sapp) {
			trainerTagsRS(theTrainers);
		} else if (romEntry.romType == RomType_Em) {
			trainerTagsE(theTrainers);
		} else {
			trainerTagsFRLG(theTrainers);
		}
		return theTrainers;
	}

	private void trainerTagsRS(List<Trainer> trs) {
		// Gym Trainers
		tag(trs, "GYM1", 0x140, 0x141);
		tag(trs, "GYM2", 0x1AA, 0x1A9, 0xB3);
		tag(trs, "GYM3", 0xBF, 0x143, 0xC2, 0x289);
		tag(trs, "GYM4", 0xC9, 0x288, 0xCB, 0x28A, 0xCD);
		tag(trs, "GYM5", 0x47, 0x59, 0x49, 0x5A, 0x48, 0x5B, 0x4A);
		tag(trs, "GYM6", 0x191, 0x28F, 0x28E, 0x194);
		tag(trs, "GYM7", 0xE9, 0xEA, 0xEB, 0xF4, 0xF5, 0xF6);
		tag(trs, "GYM8", 0x82, 0x266, 0x83, 0x12D, 0x81, 0x74, 0x80, 0x265);

		// Gym Leaders
		tag(trs, 0x109, "GYM1");
		tag(trs, 0x10A, "GYM2");
		tag(trs, 0x10B, "GYM3");
		tag(trs, 0x10C, "GYM4");
		tag(trs, 0x10D, "GYM5");
		tag(trs, 0x10E, "GYM6");
		tag(trs, 0x10F, "GYM7");
		tag(trs, 0x110, "GYM8");
		// Elite 4
		tag(trs, 0x105, "ELITE1");
		tag(trs, 0x106, "ELITE2");
		tag(trs, 0x107, "ELITE3");
		tag(trs, 0x108, "ELITE4");
		tag(trs, 0x14F, "CHAMPION");
		// Brendan
		tag(trs, 0x208, "RIVAL1-2");
		tag(trs, 0x20B, "RIVAL1-0");
		tag(trs, 0x20E, "RIVAL1-1");

		tag(trs, 0x209, "RIVAL2-2");
		tag(trs, 0x20C, "RIVAL2-0");
		tag(trs, 0x20F, "RIVAL2-1");

		tag(trs, 0x20A, "RIVAL3-2");
		tag(trs, 0x20D, "RIVAL3-0");
		tag(trs, 0x210, "RIVAL3-1");

		tag(trs, 0x295, "RIVAL4-2");
		tag(trs, 0x296, "RIVAL4-0");
		tag(trs, 0x297, "RIVAL4-1");

		// May
		tag(trs, 0x211, "RIVAL1-2");
		tag(trs, 0x214, "RIVAL1-0");
		tag(trs, 0x217, "RIVAL1-1");

		tag(trs, 0x212, "RIVAL2-2");
		tag(trs, 0x215, "RIVAL2-0");
		tag(trs, 0x218, "RIVAL2-1");

		tag(trs, 0x213, "RIVAL3-2");
		tag(trs, 0x216, "RIVAL3-0");
		tag(trs, 0x219, "RIVAL3-1");

		tag(trs, 0x298, "RIVAL4-2");
		tag(trs, 0x299, "RIVAL4-0");
		tag(trs, 0x29A, "RIVAL4-1");

		if (romEntry.romType == RomType_Ruby) {
			tag(trs, "THEMED:MAXIE", 0x259, 0x25A);
			tag(trs, "THEMED:COURTNEY", 0x257, 0x258);
			tag(trs, "THEMED:TABITHA", 0x254, 0x255);
		} else {
			tag(trs, "THEMED:ARCHIE", 0x23, 0x22);
			tag(trs, "THEMED:MATT", 0x1E, 0x1F);
			tag(trs, "THEMED:SHELLY", 0x20, 0x21);
		}

	}

	private void trainerTagsE(List<Trainer> trs) {
		// Gym Trainers
		tag(trs, "GYM1", 0x140, 0x141, 0x23B);
		tag(trs, "GYM2", 0x1AA, 0x1A9, 0xB3, 0x23C, 0x23D, 0x23E);
		tag(trs, "GYM3", 0xBF, 0x143, 0xC2, 0x289, 0x322);
		tag(trs, "GYM4", 0x288, 0xC9, 0xCB, 0x28A, 0xCA, 0xCC, 0x1F5, 0xCD);
		tag(trs, "GYM5", 0x47, 0x59, 0x49, 0x5A, 0x48, 0x5B, 0x4A);
		tag(trs, "GYM6", 0x192, 0x28F, 0x191, 0x28E, 0x194, 0x323);
		tag(trs, "GYM7", 0xE9, 0xEA, 0xEB, 0xF4, 0xF5, 0xF6, 0x24F, 0x248,
				0x247, 0x249, 0x246, 0x23F);
		tag(trs, "GYM8", 0x265, 0x80, 0x1F6, 0x73, 0x81, 0x76, 0x82, 0x12D,
				0x83, 0x266);

		// Gym Leaders + Emerald Rematches!
		tag(trs, "GYM1", 0x109, 0x302, 0x303, 0x304, 0x305);
		tag(trs, "GYM2", 0x10A, 0x306, 0x307, 0x308, 0x309);
		tag(trs, "GYM3", 0x10B, 0x30A, 0x30B, 0x30C, 0x30D);
		tag(trs, "GYM4", 0x10C, 0x30E, 0x30F, 0x310, 0x311);
		tag(trs, "GYM5", 0x10D, 0x312, 0x313, 0x314, 0x315);
		tag(trs, "GYM6", 0x10E, 0x316, 0x317, 0x318, 0x319);
		tag(trs, "GYM7", 0x10F, 0x31A, 0x31B, 0x31C, 0x31D);
		tag(trs, "GYM8", 0x110, 0x31E, 0x31F, 0x320, 0x321);

		// Elite 4
		tag(trs, 0x105, "ELITE1");
		tag(trs, 0x106, "ELITE2");
		tag(trs, 0x107, "ELITE3");
		tag(trs, 0x108, "ELITE4");
		tag(trs, 0x14F, "CHAMPION");

		// Brendan
		tag(trs, 0x208, "RIVAL1-2");
		tag(trs, 0x20B, "RIVAL1-0");
		tag(trs, 0x20E, "RIVAL1-1");

		tag(trs, 0x251, "RIVAL2-2");
		tag(trs, 0x250, "RIVAL2-0");
		tag(trs, 0x257, "RIVAL2-1");

		tag(trs, 0x209, "RIVAL3-2");
		tag(trs, 0x20C, "RIVAL3-0");
		tag(trs, 0x20F, "RIVAL3-1");

		tag(trs, 0x20A, "RIVAL4-2");
		tag(trs, 0x20D, "RIVAL4-0");
		tag(trs, 0x210, "RIVAL4-1");

		tag(trs, 0x295, "RIVAL5-2");
		tag(trs, 0x296, "RIVAL5-0");
		tag(trs, 0x297, "RIVAL5-1");

		// May
		tag(trs, 0x211, "RIVAL1-2");
		tag(trs, 0x214, "RIVAL1-0");
		tag(trs, 0x217, "RIVAL1-1");

		tag(trs, 0x258, "RIVAL2-2");
		tag(trs, 0x300, "RIVAL2-0");
		tag(trs, 0x301, "RIVAL2-1");

		tag(trs, 0x212, "RIVAL3-2");
		tag(trs, 0x215, "RIVAL3-0");
		tag(trs, 0x218, "RIVAL3-1");

		tag(trs, 0x213, "RIVAL4-2");
		tag(trs, 0x216, "RIVAL4-0");
		tag(trs, 0x219, "RIVAL4-1");

		tag(trs, 0x298, "RIVAL5-2");
		tag(trs, 0x299, "RIVAL5-0");
		tag(trs, 0x29A, "RIVAL5-1");

		// Themed
		tag(trs, "THEMED:MAXIE", 0x259, 0x25A, 0x2DE);
		tag(trs, "THEMED:TABITHA", 0x202, 0x255, 0x2DC);
		tag(trs, "THEMED:ARCHIE", 0x22);
		tag(trs, "THEMED:MATT", 0x1E);
		tag(trs, "THEMED:SHELLY", 0x20, 0x21);

		// Steven
		tag(trs, 0x324, "UBER");

	}

	private void trainerTagsFRLG(List<Trainer> trs) {

		// Gym Trainers
		tag(trs, "GYM1", 0x8E);
		tag(trs, "GYM2", 0xEA, 0x96);
		tag(trs, "GYM3", 0xDC, 0x8D, 0x1A7);
		tag(trs, "GYM4", 0x10A, 0x84, 0x109, 0xA0, 0x192, 0x10B, 0x85);
		tag(trs, "GYM5", 0x125, 0x124, 0x120, 0x127, 0x126, 0x121);
		tag(trs, "GYM6", 0x11A, 0x119, 0x1CF, 0x11B, 0x1CE, 0x1D0, 0x118);
		tag(trs, "GYM7", 0xD5, 0xB1, 0xB2, 0xD6, 0xB3, 0xD7, 0xB4);
		tag(trs, "GYM8", 0x129, 0x143, 0x188, 0x190, 0x142, 0x128, 0x191, 0x144);

		// Gym Leaders
		tag(trs, 0x19E, "GYM1");
		tag(trs, 0x19F, "GYM2");
		tag(trs, 0x1A0, "GYM3");
		tag(trs, 0x1A1, "GYM4");
		tag(trs, 0x1A2, "GYM5");
		tag(trs, 0x1A4, "GYM6");
		tag(trs, 0x1A3, "GYM7");
		tag(trs, 0x15E, "GYM8");

		// Giovanni
		tag(trs, 0x15C, "GIO1");
		tag(trs, 0x15D, "GIO2");

		// E4 Round 1
		tag(trs, 0x19A, "ELITE1-1");
		tag(trs, 0x19B, "ELITE2-1");
		tag(trs, 0x19C, "ELITE3-1");
		tag(trs, 0x19D, "ELITE4-1");

		// E4 Round 2
		tag(trs, 0x2DF, "ELITE1-2");
		tag(trs, 0x2E0, "ELITE2-2");
		tag(trs, 0x2E1, "ELITE3-2");
		tag(trs, 0x2E2, "ELITE4-2");

		// Rival Battles

		// Initial Rival
		tag(trs, 0x148, "RIVAL1-0");
		tag(trs, 0x146, "RIVAL1-1");
		tag(trs, 0x147, "RIVAL1-2");

		// Route 22 (weak)
		tag(trs, 0x14B, "RIVAL2-0");
		tag(trs, 0x149, "RIVAL2-1");
		tag(trs, 0x14A, "RIVAL2-2");

		// Cerulean
		tag(trs, 0x14E, "RIVAL3-0");
		tag(trs, 0x14C, "RIVAL3-1");
		tag(trs, 0x14D, "RIVAL3-2");

		// SS Anne
		tag(trs, 0x1AC, "RIVAL4-0");
		tag(trs, 0x1AA, "RIVAL4-1");
		tag(trs, 0x1AB, "RIVAL4-2");

		// Pokemon Tower
		tag(trs, 0x1AF, "RIVAL5-0");
		tag(trs, 0x1AD, "RIVAL5-1");
		tag(trs, 0x1AE, "RIVAL5-2");

		// Silph Co
		tag(trs, 0x1B2, "RIVAL6-0");
		tag(trs, 0x1B0, "RIVAL6-1");
		tag(trs, 0x1B1, "RIVAL6-2");

		// Route 22 (strong)
		tag(trs, 0x1B5, "RIVAL7-0");
		tag(trs, 0x1B3, "RIVAL7-1");
		tag(trs, 0x1B4, "RIVAL7-2");

		// E4 Round 1
		tag(trs, 0x1B8, "RIVAL8-0");
		tag(trs, 0x1B6, "RIVAL8-1");
		tag(trs, 0x1B7, "RIVAL8-2");

		// E4 Round 2
		tag(trs, 0x2E5, "RIVAL9-0");
		tag(trs, 0x2E3, "RIVAL9-1");
		tag(trs, 0x2E4, "RIVAL9-2");

	}

	private void tag(List<Trainer> trainers, int trainerNum, String tag) {
		trainers.get(trainerNum - 1).tag = tag;
	}

	private void tag(List<Trainer> allTrainers, String tag, int... numbers) {
		for (int num : numbers) {
			allTrainers.get(num - 1).tag = tag;
		}
	}

	@Override
	public void setTrainers(List<Trainer> trainerData) {
		int baseOffset = romEntry.getValue("TrainerData");
		int amount = romEntry.getValue("TrainerCount");
		int entryLen = romEntry.getValue("TrainerEntrySize");
		Iterator<Trainer> theTrainers = trainerData.iterator();
		for (int i = 1; i < amount; i++) {
			int trOffset = baseOffset + i * entryLen;
			Trainer tr = theTrainers.next();
			// Write out the data as type 0 to avoid moves & hold items carrying
			// over
			rom[trOffset] = 0;
			rom[trOffset + (entryLen - 8)] = (byte) tr.pokemon.size();
			int pointerToPokes = readPointer(trOffset + (entryLen - 4));
			Iterator<TrainerPokemon> pokes = tr.pokemon.iterator();
			// Pokemon data!
			// if (pokeDataType == 0) {
			// blocks of 8 bytes
			for (int poke = 0; poke < tr.pokemon.size(); poke++) {
				TrainerPokemon thisPoke = pokes.next();
				writeWord(pointerToPokes + poke * 8, thisPoke.AILevel);
				writeWord(pointerToPokes + poke * 8 + 2, thisPoke.level);
				writeWord(pointerToPokes + poke * 8 + 4,
						pokeNumTo3GIndex(thisPoke.pokemon.number));
				writeWord(pointerToPokes + poke * 8 + 6, 0);
			}
		}

	}

	private void writeWildArea(int offset, int numOfEntries,
			EncounterSet encounters) {
		// Grab the *real* pointer to data
		int dataOffset = readPointer(offset + 4);
		// Write the entries
		for (int i = 0; i < numOfEntries; i++) {
			Encounter enc = encounters.encounters.get(i);
			// min, max, species, species
			writeWord(dataOffset + i * 4 + 2,
					pokeNumTo3GIndex(enc.pokemon.number));
		}
	}

	@Override
	public List<Pokemon> getPokemon() {
		return pokemonList;
	}

	@Override
	public Map<Pokemon, List<MoveLearnt>> getMovesLearnt() {
		Map<Pokemon, List<MoveLearnt>> movesets = new TreeMap<Pokemon, List<MoveLearnt>>();
		int baseOffset = romEntry.getValue("PokemonMovesets");
		for (int i = 1; i <= 411; i++) {
			int offsToPtr = baseOffset + (i - 1) * 4;
			int moveDataLoc = readPointer(offsToPtr);
			if (i >= 252 && i <= 276) {
				continue;
			}
			Pokemon pkmn = pokes[poke3GIndexToNum(i)];
			List<MoveLearnt> moves = new ArrayList<MoveLearnt>();
			while ((rom[moveDataLoc] & 0xFF) != 0xFF
					|| (rom[moveDataLoc + 1] & 0xFF) != 0xFF) {
				int move = (rom[moveDataLoc] & 0xFF);
				int level = (rom[moveDataLoc + 1] & 0xFE) >> 1;
				if ((rom[moveDataLoc + 1] & 0x01) == 0x01) {
					move += 256;
				}
				MoveLearnt ml = new MoveLearnt();
				ml.level = level;
				ml.move = move;
				moves.add(ml);
				moveDataLoc += 2;
			}
			movesets.put(pkmn, moves);
		}
		return movesets;
	}

	@Override
	public void setMovesLearnt(Map<Pokemon, List<MoveLearnt>> movesets) {
		int baseOffset = romEntry.getValue("PokemonMovesets");
		int fso = romEntry.getValue("FreeSpace");
		for (int i = 1; i <= 411; i++) {
			int offsToPtr = baseOffset + (i - 1) * 4;
			int moveDataLoc = readPointer(offsToPtr);
			if (i >= 252 && i <= 276) {
				continue;
			}
			Pokemon pkmn = pokes[poke3GIndexToNum(i)];
			List<MoveLearnt> moves = movesets.get(pkmn);
			int mloc = moveDataLoc;
			while ((rom[mloc] & 0xFF) != 0xFF || (rom[mloc + 1] & 0xFF) != 0xFF) {
				mloc += 2;
			}
			int currentMoveCount = (mloc - moveDataLoc);
			int newMoveCount = moves.size();
			if (currentMoveCount <= newMoveCount) {
				int looplimit = Math.min(currentMoveCount, newMoveCount);
				for (int mv = 0; mv < looplimit; mv++) {
					MoveLearnt ml = moves.get(mv);
					rom[moveDataLoc] = (byte) (ml.move & 0xFF);
					int levelPart = (ml.level << 1) & 0xFE;
					if (ml.move > 255) {
						levelPart++;
					}
					rom[moveDataLoc + 1] = (byte) levelPart;
					moveDataLoc += 2;
				}
				if (looplimit < currentMoveCount) {
					// need a new terminator
					rom[moveDataLoc] = (byte) 0xFF;
					rom[moveDataLoc + 1] = (byte) 0xFF;
				}
			} else {
				// repoint!
				int newBytesNeeded = newMoveCount * 2 + 4;
				int writeSpace = RomFunctions.freeSpaceFinder(rom, (byte) 0xFF,
						newBytesNeeded, fso);
				if (writeSpace < fso) {
					throw new RuntimeException("ROM is full");
				}
				writePointer(offsToPtr, writeSpace);
				moveDataLoc = writeSpace;
				for (int mv = 0; mv < newMoveCount; mv++) {
					MoveLearnt ml = moves.get(mv);
					rom[moveDataLoc] = (byte) (ml.move & 0xFF);
					int levelPart = (ml.level << 1) & 0xFE;
					if (ml.move > 255) {
						levelPart++;
					}
					rom[moveDataLoc + 1] = (byte) levelPart;
					moveDataLoc += 2;
				}
				// need a new terminator
				rom[moveDataLoc] = (byte) 0xFF;
				rom[moveDataLoc + 1] = (byte) 0xFF;
				// for safety (the freespace finder should prevent the
				// terminator being overwritten but...)
				rom[moveDataLoc + 2] = 0x00;
				rom[moveDataLoc + 3] = 0x00;
			}
		}

	}

	private static class StaticPokemon {
		private int[] offsets;

		public StaticPokemon(int... offsets) {
			this.offsets = offsets;
		}

		public Pokemon getPokemon(Gen3RomHandler parent) {
			return parent.pokes[poke3GIndexToNum(parent.readWord(offsets[0]))];
		}

		public void setPokemon(Gen3RomHandler parent, Pokemon pkmn) {
			int value = pokeNumTo3GIndex(pkmn.number);
			for (int offset : offsets) {
				parent.writeWord(offset, value);
			}
		}
	}

	@Override
	public List<Pokemon> getStaticPokemon() {
		List<Pokemon> statics = new ArrayList<Pokemon>();
		List<StaticPokemon> staticsHere = romEntry.staticPokemon;
		for (StaticPokemon staticPK : staticsHere) {
			statics.add(staticPK.getPokemon(this));
		}
		return statics;
	}

	@Override
	public boolean setStaticPokemon(List<Pokemon> staticPokemon) {
		// Support Deoxys/Mew gifts/catches in E/FR/LG
		if (!havePatchedObedience) {
			attemptObedienceEvolutionPatches();
		}

		List<StaticPokemon> staticsHere = romEntry.staticPokemon;
		if (staticPokemon.size() != staticsHere.size()) {
			return false;
		}
		for (Pokemon pkmn : staticPokemon) {
			if (!isInGame(pkmn)) {
				return false;
			}
		}
		for (int i = 0; i < staticsHere.size(); i++) {
			staticsHere.get(i).setPokemon(this, staticPokemon.get(i));
		}
		return true;
	}

	@Override
	public List<Integer> getTMMoves() {
		List<Integer> tms = new ArrayList<Integer>();
		int offset = romEntry.getValue("TmMoves");
		for (int i = 1; i <= 50; i++) {
			tms.add(readWord(offset + (i - 1) * 2));
		}
		return tms;
	}

	@Override
	public List<Integer> getHMMoves() {
		return Arrays.asList(0xf, 0x13, 0x39, 0x46, 0x94, 0xf9, 0x7f, 0x123);
	}

	@Override
	public void setTMMoves(List<Integer> moveIndexes) {
		int offset = romEntry.getValue("TmMoves");
		for (int i = 1; i <= 50; i++) {
			writeWord(offset + (i - 1) * 2, moveIndexes.get(i - 1));
		}
		int otherOffset = romEntry.getValue("TmMovesDuplicate");
		if (otherOffset > 0) {
			// Emerald/FR/LG have *two* TM tables
			System.arraycopy(rom, offset, rom, otherOffset, 100);
		}

		int iiOffset = romEntry.getValue("ItemImages");
		if (iiOffset > 0) {
			int[] pals = romEntry.arrayEntries.get("TmPals");
			// Update the item image palettes
			// Gen3 TMs are 289-338
			for (int i = 0; i < 50; i++) {
				Move mv = moves[moveIndexes.get(i)];
				int typeID = typeToByte(mv.type);
				writePointer(iiOffset + (289 + i) * 8 + 4, pals[typeID]);
			}
		}

		// Item descriptions
		if (romEntry.getValue("MoveDescriptions") > 0) {
			// JP blocked for now - uses different item structure anyway
			int idOffset = romEntry.getValue("ItemData");
			int mdOffset = romEntry.getValue("MoveDescriptions");
			int entrySize = romEntry.getValue("ItemEntrySize");
			int limitPerLine = (romEntry.romType == RomType_FRLG) ? 24 : 18;
			for (int i = 0; i < 50; i++) {
				int itemBaseOffset = idOffset + (i + 289) * entrySize;
				int moveBaseOffset = mdOffset + (moveIndexes.get(i) - 1) * 4;
				int moveTextPointer = readPointer(moveBaseOffset);
				String moveDesc = readVariableLengthString(moveTextPointer);
				String newItemDesc = RomFunctions
						.rewriteDescriptionForNewLineSize(moveDesc, "\\n",
								limitPerLine, ssd);
				// Find freespace
				int fsBytesNeeded = translateString(newItemDesc).length + 1;
				int newItemDescOffset = RomFunctions.freeSpaceFinder(rom,
						(byte) 0xFF, fsBytesNeeded,
						romEntry.getValue("FreeSpace"));
				if (newItemDescOffset < romEntry.getValue("FreeSpace")) {
					String nl = System.getProperty("line.separator");
					log("Couldn't insert new item description." + nl);
					return;
				}
				writeVariableLengthString(newItemDesc, newItemDescOffset);
				writePointer(itemBaseOffset + 0x14, newItemDescOffset);
			}
		}
	}

	private RomFunctions.StringSizeDeterminer ssd = new RomFunctions.StringSizeDeterminer() {

		@Override
		public int lengthFor(String encodedText) {
			return translateString(encodedText).length;
		}
	};

	@Override
	public int getTMCount() {
		return 50;
	}

	@Override
	public int getHMCount() {
		return 8;
	}

	@Override
	public Map<Pokemon, boolean[]> getTMHMCompatibility() {
		Map<Pokemon, boolean[]> compat = new TreeMap<Pokemon, boolean[]>();
		int offset = romEntry.getValue("PokemonTMHMCompat");
		for (int i = 1; i <= 386; i++) {
			int compatOffset = offset + (pokeNumTo3GIndex(i) - 1) * 8;
			Pokemon pkmn = pokes[i];
			boolean[] flags = new boolean[59];
			for (int j = 0; j < 8; j++) {
				readByteIntoFlags(flags, j * 8 + 1, compatOffset + j);
			}
			compat.put(pkmn, flags);
		}
		return compat;
	}

	@Override
	public void setTMHMCompatibility(Map<Pokemon, boolean[]> compatData) {
		int offset = romEntry.getValue("PokemonTMHMCompat");
		for (Map.Entry<Pokemon, boolean[]> compatEntry : compatData.entrySet()) {
			Pokemon pkmn = compatEntry.getKey();
			boolean[] flags = compatEntry.getValue();
			int compatOffset = offset + (pokeNumTo3GIndex(pkmn.number) - 1) * 8;
			for (int j = 0; j < 8; j++) {
				rom[compatOffset + j] = getByteFromFlags(flags, j * 8 + 1);
			}
		}
	}

	@Override
	public boolean hasMoveTutors() {
		return (romEntry.romType == RomType_Em || romEntry.romType == RomType_FRLG);
	}

	@Override
	public List<Integer> getMoveTutorMoves() {
		if (!hasMoveTutors()) {
			return new ArrayList<Integer>();
		}
		List<Integer> mts = new ArrayList<Integer>();
		int moveCount = romEntry.getValue("MoveTutorMoves");
		int offset = romEntry.getValue("MoveTutorData");
		for (int i = 0; i < moveCount; i++) {
			mts.add(readWord(offset + i * 2));
		}
		return mts;
	}

	@Override
	public void setMoveTutorMoves(List<Integer> moves) {
		if (!hasMoveTutors()) {
			return;
		}
		int moveCount = romEntry.getValue("MoveTutorMoves");
		int offset = romEntry.getValue("MoveTutorData");
		if (moveCount != moves.size()) {
			return;
		}
		for (int i = 0; i < moveCount; i++) {
			writeWord(offset + i * 2, moves.get(i));
		}
	}

	@Override
	public Map<Pokemon, boolean[]> getMoveTutorCompatibility() {
		if (!hasMoveTutors()) {
			return new TreeMap<Pokemon, boolean[]>();
		}
		Map<Pokemon, boolean[]> compat = new TreeMap<Pokemon, boolean[]>();
		int moveCount = romEntry.getValue("MoveTutorMoves");
		int offset = romEntry.getValue("MoveTutorData") + moveCount * 2;
		int bytesRequired = ((moveCount + 7) & ~7) / 8;
		for (int i = 1; i <= 386; i++) {
			int compatOffset = offset + pokeNumTo3GIndex(i) * moveCount;
			Pokemon pkmn = pokes[i];
			boolean[] flags = new boolean[moveCount + 1];
			for (int j = 0; j < bytesRequired; j++) {
				readByteIntoFlags(flags, j * 8 + 1, compatOffset + j);
			}
			compat.put(pkmn, flags);
		}
		return compat;
	}

	@Override
	public void setMoveTutorCompatibility(Map<Pokemon, boolean[]> compatData) {
		if (!hasMoveTutors()) {
			return;
		}
		int moveCount = romEntry.getValue("MoveTutorMoves");
		int offset = romEntry.getValue("MoveTutorData") + moveCount * 2;
		int bytesRequired = ((moveCount + 7) & ~7) / 8;
		for (Map.Entry<Pokemon, boolean[]> compatEntry : compatData.entrySet()) {
			Pokemon pkmn = compatEntry.getKey();
			boolean[] flags = compatEntry.getValue();
			int compatOffset = offset + pokeNumTo3GIndex(pkmn.number)
					* bytesRequired;
			for (int j = 0; j < bytesRequired; j++) {
				rom[compatOffset + j] = getByteFromFlags(flags, j * 8 + 1);
			}
		}
	}

	@Override
	public String getROMName() {
		return romEntry.name;
	}

	@Override
	public String getROMCode() {
		return romEntry.romCode;
	}

	@Override
	public String getSupportLevel() {
		return (romEntry.getValue("StaticPokemonSupport") > 0) ? "Complete"
				: "No Static Pokemon";
	}

	// For dynamic offsets later
	private int find(String hexString) {
		return find(rom, hexString);
	}

	private int find(byte[] haystack, String hexString) {
		if (hexString.length() % 2 != 0) {
			return -3; // error
		}
		byte[] searchFor = new byte[hexString.length() / 2];
		for (int i = 0; i < searchFor.length; i++) {
			searchFor[i] = (byte) Integer.parseInt(
					hexString.substring(i * 2, i * 2 + 2), 16);
		}
		List<Integer> found = RomFunctions.search(haystack, searchFor);
		if (found.size() == 0) {
			return -1; // not found
		} else if (found.size() > 1) {
			return -2; // not unique
		} else {
			return found.get(0);
		}
	}

	private List<Integer> findMultiple(String hexString) {
		return findMultiple(rom, hexString);
	}

	private List<Integer> findMultiple(byte[] haystack, String hexString) {
		if (hexString.length() % 2 != 0) {
			return new ArrayList<Integer>(); // error
		}
		byte[] searchFor = new byte[hexString.length() / 2];
		for (int i = 0; i < searchFor.length; i++) {
			searchFor[i] = (byte) Integer.parseInt(
					hexString.substring(i * 2, i * 2 + 2), 16);
		}
		List<Integer> found = RomFunctions.search(haystack, searchFor);
		return found;
	}

	private void writeHexString(String hexString, int offset) {
		if (hexString.length() % 2 != 0) {
			return; // error
		}
		for (int i = 0; i < hexString.length() / 2; i++) {
			rom[offset + i] = (byte) Integer.parseInt(
					hexString.substring(i * 2, i * 2 + 2), 16);
		}
	}

	private void attemptObedienceEvolutionPatches() {
		havePatchedObedience = true;
		// This routine *appears* to only exist in E/FR/LG...
		// Look for the deoxys part which is
		// MOVS R1, 0x19A
		// CMP R0, R1
		// BEQ <mew/deoxys case>
		// Hex is CD214900 8842 0FD0
		int deoxysObOffset = find("CD21490088420FD0");
		if (deoxysObOffset > 0) {
			// We found the deoxys check...
			// Replacing it with MOVS R1, 0x0 would work fine.
			// This would make it so species 0x0 (glitch only) would disobey.
			// But MOVS R1, 0x0 (the version I know) is 2-byte
			// So we just use it twice...
			// the equivalent of nop'ing the second time.
			rom[deoxysObOffset] = 0x00;
			rom[deoxysObOffset + 1] = 0x21;
			rom[deoxysObOffset + 2] = 0x00;
			rom[deoxysObOffset + 3] = 0x21;
			// Look for the mew check too... it's 0x16 ahead
			if (readWord(deoxysObOffset + 0x16) == 0x2897) {
				// Bingo, thats CMP R0, 0x97
				// change to CMP R0, 0x0
				writeWord(deoxysObOffset + 0x16, 0x2800);
			}
		}

		// Look for evolutions too
		if (romEntry.romType == RomType_FRLG) {
			int evoJumpOffset = find("972814DD");
			if (evoJumpOffset > 0) {
				// This currently compares species to 0x97 and then allows
				// evolution if it's <= that.
				// Allow it regardless by using an unconditional jump instead
				writeWord(evoJumpOffset, 0x0000);
				writeWord(evoJumpOffset + 2, 0xE014);
			}
		}
	}

	public void patchForNationalDex() {
		log("--Patching for National Dex at Start of Game--");
		String nl = System.getProperty("line.separator");
		int fso = romEntry.getValue("FreeSpace");
		if (romEntry.romType == RomType_Ruby
				|| romEntry.romType == RomType_Sapp) {
			// Find the original pokedex script
			int pkDexOffset = find("326629010803");
			if (pkDexOffset < 0) {
				log("Patch unsuccessful." + nl);
				return;
			}
			int textPointer = readPointer(pkDexOffset - 4);
			int realScriptLocation = pkDexOffset - 8;
			int pointerLocToScript = find(pointerToHexString(realScriptLocation));
			if (pointerLocToScript < 0) {
				log("Patch unsuccessful." + nl);
				return;
			}
			// Find free space for our new routine
			int writeSpace = RomFunctions.freeSpaceFinder(rom, (byte) 0xFF, 44,
					fso);
			if (writeSpace < fso) {
				log("Patch unsuccessful." + nl);
				// Somehow this ROM is full
				return;
			}
			writePointer(pointerLocToScript, writeSpace);
			writeHexString("31720167", writeSpace);
			writePointer(writeSpace + 4, textPointer);
			writeHexString(
					"32662901082B00801102006B02021103016B020211DABE4E020211675A6A02022A008003",
					writeSpace + 8);

		} else if (romEntry.romType == RomType_FRLG) {
			// Find the original pokedex script
			int pkDexOffset = find("292908258101");
			if (pkDexOffset < 0) {
				log("Patch unsuccessful." + nl);
				return;
			}
			// Find free space for our new routine
			int writeSpace = RomFunctions.freeSpaceFinder(rom, (byte) 0xFF, 10,
					fso);
			if (writeSpace < fso) {
				// Somehow this ROM is full
				log("Patch unsuccessful." + nl);
				return;
			}
			rom[pkDexOffset] = 4;
			writePointer(pkDexOffset + 1, writeSpace);
			rom[pkDexOffset + 5] = 0; // NOP

			// Now write our new routine
			writeHexString("292908258101256F0103", writeSpace);

			// Fix people using the national dex flag
			List<Integer> ndexChecks = findMultiple("260D809301210D800100");
			for (int ndexCheckOffset : ndexChecks) {
				// change to a flag-check
				// 82C = "beaten e4/gary once"
				writeHexString("2B2C0800000000000000", ndexCheckOffset);
			}

			// Fix oak in his lab
			int oakLabCheckOffs = find("257D011604800000260D80D400");
			if (oakLabCheckOffs > 0) {
				// replace it
				writeHexString("257D011604800100", oakLabCheckOffs);
			}

			// Fix oak outside your house
			int oakHouseCheckOffs = find("1604800000260D80D4001908800580190980068083000880830109802109803C");
			if (oakHouseCheckOffs > 0) {
				// fix him to use ndex count
				writeHexString("1604800100", oakHouseCheckOffs);
			}
		} else {
			// Find the original pokedex script
			int pkDexOffset = find("3229610825F00129E40816CD40010003");
			if (pkDexOffset < 0) {
				log("Patch unsuccessful." + nl);
				return;
			}
			int textPointer = readPointer(pkDexOffset - 4);
			int realScriptLocation = pkDexOffset - 8;
			int pointerLocToScript = find(pointerToHexString(realScriptLocation));
			if (pointerLocToScript < 0) {
				log("Patch unsuccessful." + nl);
				return;
			}
			// Find free space for our new routine
			int writeSpace = RomFunctions.freeSpaceFinder(rom, (byte) 0xFF, 27,
					fso);
			if (writeSpace < fso) {
				// Somehow this ROM is full
				log("Patch unsuccessful." + nl);
				return;
			}
			writePointer(pointerLocToScript, writeSpace);
			writeHexString("31720167", writeSpace);
			writePointer(writeSpace + 4, textPointer);
			writeHexString("3229610825F00129E40825F30116CD40010003",
					writeSpace + 8);
		}
		log("Patch successful!" + nl);
	}

	public String pointerToHexString(int pointer) {
		String hex = String.format("%08X", pointer + 0x08000000);
		return new String(new char[] { hex.charAt(6), hex.charAt(7),
				hex.charAt(4), hex.charAt(5), hex.charAt(2), hex.charAt(3),
				hex.charAt(0), hex.charAt(1) });
	}

	@Override
	public List<Evolution> getEvolutions() {
		int baseOffset = romEntry.getValue("PokemonEvolutions");
		List<Evolution> evos = new ArrayList<Evolution>();
		List<Evolution> evosForThisPoke = new ArrayList<Evolution>();
		for (int i = 1; i <= 386; i++) {
			evosForThisPoke.clear();
			int idx = pokeNumTo3GIndex(i);
			int evoOffset = baseOffset + (idx - 1) * 0x28;
			for (int j = 0; j < 5; j++) {
				int method = readWord(evoOffset + j * 8);
				int evolvingTo = readWord(evoOffset + j * 8 + 4);
				if (method >= 1 && method <= 15 && evolvingTo >= 1
						&& evolvingTo <= 411) {
					evolvingTo = poke3GIndexToNum(evolvingTo);
					int extraInfo = readWord(evoOffset + j * 8 + 2);
					EvolutionType et = EvolutionType.fromIndex(3, method);
					Evolution evo = new Evolution(i, evolvingTo, true, et,
							extraInfo);
					if (!evos.contains(evo)) {
						evos.add(evo);
						evosForThisPoke.add(evo);
					}
				}
			}
			// split evos don't carry stats
			if (evosForThisPoke.size() > 1) {
				for (Evolution e : evosForThisPoke) {
					e.carryStats = false;
				}
			}
		}
		return evos;
	}

	@Override
	public void removeTradeEvolutions(boolean changeMoveEvos) {
		// no move evos, so no need to check for those
		int baseOffset = romEntry.getValue("PokemonEvolutions");
		log("--Removing Trade Evolutions--");
		for (int i = 1; i <= 386; i++) {
			int idx = pokeNumTo3GIndex(i);
			int evoOffset = baseOffset + (idx - 1) * 0x28;
			// Types:
			// 1 0 Pokemon 0 (happiness any time)
			// 2 0 Pokemon 0 (happiness day)
			// 3 0 Pokemon 0 (happiness night)
			// 4 LV Pokemon 0
			// 5 0 Pokemon 0 (trade w/o item)
			// 6 Item Pokemon 0 (trade w/ item)
			// 7 Stone Pokemon 0
			// 8 Level Pokemon 0 (Attack>Defense)
			// 9 Level Pokemon 0 (Attack=Defense)
			// 10 Level Pokemon 0 (Attack<Defense)
			// 11 Level Pokemon 0 ((PV & 0xFFFF) % 10 < 5)
			// 12 Level Pokemon 0 ((PV & 0xFFFF) % 10 >= 5)
			// 13 Level Pokemon 0
			// (split evolution level up, pokemon becomes this)
			// 14 Level Pokemon 0
			// (split evolution level up, pokemon splits to this)
			// 15 Value Pokemon 0 (Level up w/ Beauty >= Value)
			for (int j = 0; j < 5; j++) {
				int method = readWord(evoOffset + j * 8);
				int evolvingTo = poke3GIndexToNum(readWord(evoOffset + j * 8
						+ 4));
				// Not trades, but impossible without trading
				if (method == 2 && romEntry.romType == RomType_FRLG) {
					// happiness day change to Sun Stone
					writeWord(evoOffset + j * 8, 7);
					writeWord(evoOffset + j * 8 + 2, 93);
					logEvoChangeStone(pokes[i].name, pokes[evolvingTo].name,
							itemNames[93]);
				}
				if (method == 3 && romEntry.romType == RomType_FRLG) {
					// happiness night change to Moon Stone
					writeWord(evoOffset + j * 8, 7);
					writeWord(evoOffset + j * 8 + 2, 94);
					logEvoChangeStone(pokes[i].name, pokes[evolvingTo].name,
							itemNames[94]);
				}
				if (method == 15 && romEntry.romType == RomType_FRLG) {
					// beauty change to level 35
					writeWord(evoOffset + j * 8, 4);
					writeWord(evoOffset + j * 8 + 2, 35);
					logEvoChangeLevel(pokes[i].name, pokes[evolvingTo].name, 35);
				}
				// Pure Trade
				if (method == 5) {
					// Haunter, Machoke, Kadabra, Graveler
					// Make it into level 37, we're done.
					writeWord(evoOffset + j * 8, 4);
					writeWord(evoOffset + j * 8 + 2, 37);
					logEvoChangeLevel(pokes[i].name, pokes[evolvingTo].name, 37);
				}
				// Trade w/ Held Item
				if (method == 6) {
					if (i == 61) {
						// Poliwhirl: Lv 37
						writeWord(evoOffset + j * 8, 4);
						writeWord(evoOffset + j * 8 + 2, 37);
						logEvoChangeLevel(pokes[i].name,
								pokes[evolvingTo].name, 37);
					} else if (i == 79) {
						// Slowpoke: Water Stone
						writeWord(evoOffset + j * 8, 7);
						writeWord(evoOffset + j * 8 + 2, 97);
						logEvoChangeStone(pokes[i].name,
								pokes[evolvingTo].name, itemNames[97]);
					} else if (i == 117) {
						// Seadra: Lv 40
						writeWord(evoOffset + j * 8, 4);
						writeWord(evoOffset + j * 8 + 2, 40);
						logEvoChangeLevel(pokes[i].name,
								pokes[evolvingTo].name, 40);
					} else if (i == 366 && evolvingTo == 367) {
						// Clamperl -> Huntail: Lv30
						writeWord(evoOffset + j * 8, 4);
						writeWord(evoOffset + j * 8 + 2, 30);
						logEvoChangeLevel(pokes[i].name,
								pokes[evolvingTo].name, 30);
					} else if (i == 366 && evolvingTo == 368) {
						// Clamperl -> Gorebyss: Water Stone
						writeWord(evoOffset + j * 8, 7);
						writeWord(evoOffset + j * 8 + 2, 97);
						logEvoChangeStone(pokes[i].name,
								pokes[evolvingTo].name, itemNames[97]);
					} else {
						// Onix, Scyther or Porygon: Lv30
						writeWord(evoOffset + j * 8, 4);
						writeWord(evoOffset + j * 8 + 2, 30);
						logEvoChangeLevel(pokes[i].name,
								pokes[evolvingTo].name, 30);
					}
				}
			}
		}
		logBlankLine();

	}

	@Override
	public List<String> getTrainerNames() {
		int baseOffset = romEntry.getValue("TrainerData");
		int amount = romEntry.getValue("TrainerCount");
		int entryLen = romEntry.getValue("TrainerEntrySize");
		List<String> theTrainers = new ArrayList<String>();
		for (int i = 1; i < amount; i++) {
			theTrainers.add(readVariableLengthString(baseOffset + i * entryLen
					+ 4));
		}
		return theTrainers;
	}

	@Override
	public void setTrainerNames(List<String> trainerNames) {
		int baseOffset = romEntry.getValue("TrainerData");
		int amount = romEntry.getValue("TrainerCount");
		int entryLen = romEntry.getValue("TrainerEntrySize");
		int nameLen = romEntry.getValue("TrainerNameLength");
		Iterator<String> theTrainers = trainerNames.iterator();
		for (int i = 1; i < amount; i++) {
			String newName = theTrainers.next();
			writeFixedLengthString(newName, baseOffset + i * entryLen + 4,
					nameLen);
		}

	}

	@Override
	public TrainerNameMode trainerNameMode() {
		return TrainerNameMode.MAX_LENGTH;
	}

	@Override
	public List<Integer> getTCNameLengthsByTrainer() {
		// not needed
		return new ArrayList<Integer>();
	}

	@Override
	public int maxTrainerNameLength() {
		return romEntry.getValue("TrainerNameLength") - 1;
	}

	@Override
	public List<String> getTrainerClassNames() {
		int baseOffset = romEntry.getValue("TrainerClassNames");
		int amount = romEntry.getValue("TrainerClassCount");
		int length = romEntry.getValue("TrainerClassNameLength");
		List<String> trainerClasses = new ArrayList<String>();
		for (int i = 0; i < amount; i++) {
			trainerClasses
					.add(readVariableLengthString(baseOffset + i * length));
		}
		return trainerClasses;
	}

	@Override
	public void setTrainerClassNames(List<String> trainerClassNames) {
		int baseOffset = romEntry.getValue("TrainerClassNames");
		int amount = romEntry.getValue("TrainerClassCount");
		int length = romEntry.getValue("TrainerClassNameLength");
		Iterator<String> trainerClasses = trainerClassNames.iterator();
		for (int i = 0; i < amount; i++) {
			writeFixedLengthString(trainerClasses.next(), baseOffset + i
					* length, length);
		}
	}

	@Override
	public int maxTrainerClassNameLength() {
		return romEntry.getValue("TrainerClassNameLength") - 1;
	}

	@Override
	public boolean fixedTrainerClassNamesLength() {
		return false;
	}

	@Override
	public boolean canChangeStaticPokemon() {
		return (romEntry.getValue("StaticPokemonSupport") > 0);
	}

	@Override
	public String getDefaultExtension() {
		return "gba";
	}

	@Override
	public int abilitiesPerPokemon() {
		return 2;
	}

	@Override
	public int highestAbilityIndex() {
		return 77;
	}

	private void loadAbilityNames() {
		int nameoffs = romEntry.getValue("AbilityNames");
		int namelen = romEntry.getValue("AbilityNameLength");
		abilityNames = new String[78];
		for (int i = 0; i <= 77; i++) {
			abilityNames[i] = readFixedLengthString(nameoffs + namelen * i,
					namelen);
		}
	}

	@Override
	public String abilityName(int number) {
		return abilityNames[number];
	}

	@Override
	public int internalStringLength(String string) {
		return translateString(string).length;
	}

	@Override
	public void applySignature() {
		// FRLG
		if (romEntry.romType == RomType_FRLG) {
			// intro sprites : first 251 only due to size
			int introPokemon = RandomSource.nextInt(251) + 1;
			int frontSprites = readPointer(0x128);
			int palettes = readPointer(0x130);

			rom[romEntry.getValue("IntroCryOffset")] = (byte) introPokemon;
			rom[romEntry.getValue("IntroOtherOffset")] = (byte) introPokemon;

			int spriteBase = romEntry.getValue("IntroSpriteOffset");
			writePointer(spriteBase, frontSprites + introPokemon * 8);
			writePointer(spriteBase + 4, palettes + introPokemon * 8);
		} else if (romEntry.romType == RomType_Ruby
				|| romEntry.romType == RomType_Sapp) {
			// intro sprites : hoenn pokes only
			int introPokemon = pokeNumTo3GIndex(RandomSource.nextInt(135) + 252);
			int frontSprites = romEntry.getValue("PokemonFrontSprites");
			int palettes = romEntry.getValue("PokemonNormalPalettes");
			int cryCommand = romEntry.getValue("IntroCryOffset");
			int otherCommand = romEntry.getValue("IntroOtherOffset");

			if (introPokemon > 255) {
				rom[cryCommand] = (byte) 0xFF;
				rom[cryCommand + 1] = 0x20;

				rom[cryCommand + 2] = (byte) (introPokemon - 255);
				rom[cryCommand + 3] = 0x30;

				rom[otherCommand] = (byte) 0xFF;
				rom[otherCommand + 1] = 0x24;

				rom[otherCommand + 2] = (byte) (introPokemon - 255);
				rom[otherCommand + 3] = 0x34;
			} else {
				rom[cryCommand] = (byte) introPokemon;
				rom[cryCommand + 1] = 0x20;

				writeWord(cryCommand + 2, 0x46C0);

				rom[otherCommand] = (byte) introPokemon;
				rom[otherCommand + 1] = 0x20;

				writeWord(otherCommand + 2, 0x46C0);
			}

			writePointer(romEntry.getValue("IntroSpriteOffset"), frontSprites
					+ introPokemon * 8);
			writePointer(romEntry.getValue("IntroPaletteOffset"), palettes
					+ introPokemon * 8);
		} else {
			// Emerald, intro sprite: any Pokemon.
			int introPokemon = pokeNumTo3GIndex(randomPokemon().number);
			writeWord(romEntry.getValue("IntroSpriteOffset"), introPokemon);
			writeWord(romEntry.getValue("IntroCryOffset"), introPokemon);
		}

	}

	private void preprocessMaps() {
		itemOffs = new ArrayList<Integer>();
		int bankCount = romEntry.getValue("MapBankCount");
		int[] bankMapCounts = romEntry.arrayEntries.get("MapBankSizes");
		int itemBall = romEntry.getValue("ItemBallPic");
		mapNames = new String[bankCount][];
		int mbpsOffset = romEntry.getValue("MapHeaders");
		int mapLabels = romEntry.getValue("MapLabels");
		Map<Integer, String> mapLabelsM = new HashMap<Integer, String>();
		for (int bank = 0; bank < bankCount; bank++) {
			int bankOffset = readPointer(mbpsOffset + bank * 4);
			mapNames[bank] = new String[bankMapCounts[bank]];
			for (int map = 0; map < bankMapCounts[bank]; map++) {
				int mhOffset = readPointer(bankOffset + map * 4);

				// map name
				int mapLabel = rom[mhOffset + 0x14] & 0xFF;
				if (mapLabelsM.containsKey(mapLabel)) {
					mapNames[bank][map] = mapLabelsM.get(mapLabel);
				} else {
					if (romEntry.romType == RomType_FRLG) {
						mapNames[bank][map] = readVariableLengthString(readPointer(mapLabels
								+ (mapLabel - 0x58) * 4));
					} else {
						mapNames[bank][map] = readVariableLengthString(readPointer(mapLabels
								+ mapLabel * 8 + 4));
					}
					mapLabelsM.put(mapLabel, mapNames[bank][map]);
				}

				// events
				int eventOffset = readPointer(mhOffset + 4);

				int pCount = rom[eventOffset] & 0xFF;
				// int wCount = rom[eventOffset + 1] & 0xFF;
				// int scCount = rom[eventOffset + 2] & 0xFF;
				int spCount = rom[eventOffset + 3] & 0xFF;

				if (pCount > 0) {
					int peopleOffset = readPointer(eventOffset + 4);
					for (int p = 0; p < pCount; p++) {
						int pSprite = rom[peopleOffset + p * 24 + 1];
						if (pSprite == itemBall
								&& readPointer(peopleOffset + p * 24 + 16) >= 0) {
							// Get script and look inside
							int scriptOffset = readPointer(peopleOffset + p
									* 24 + 16);
							if (rom[scriptOffset] == 0x1A
									&& rom[scriptOffset + 1] == 0x00
									&& (rom[scriptOffset + 2] & 0xFF) == 0x80
									&& rom[scriptOffset + 5] == 0x1A
									&& rom[scriptOffset + 6] == 0x01
									&& (rom[scriptOffset + 7] & 0xFF) == 0x80
									&& rom[scriptOffset + 10] == 0x09
									&& (rom[scriptOffset + 11] == 0x00 || rom[scriptOffset + 11] == 0x01)) {
								// item ball script
								// int itemHere = readWord(scriptOffset + 3);
								itemOffs.add(scriptOffset + 3);
							}
						}
					}
				}

				if (spCount > 0) {
					int signpostsOffset = readPointer(eventOffset + 16);
					for (int sp = 0; sp < spCount; sp++) {
						int spType = rom[signpostsOffset + sp * 12 + 5];
						if (spType >= 5 && spType <= 7) {
							// hidden item
							int itemHere = readWord(signpostsOffset + sp * 12
									+ 8);
							if (itemHere != 0) {
								// itemid 0 is coins
								itemOffs.add(signpostsOffset + sp * 12 + 8);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public ItemList getAllowedItems() {
		return allowedItems;
	}

	private void loadItemNames() {
		int nameoffs = romEntry.getValue("ItemData");
		int structlen = romEntry.getValue("ItemEntrySize");
		int maxcount = romEntry.getValue("ItemCount");
		itemNames = new String[maxcount + 1];
		for (int i = 0; i <= maxcount; i++) {
			itemNames[i] = readVariableLengthString(nameoffs + structlen * i);
		}
	}

	@Override
	public String[] getItemNames() {
		return itemNames;
	}

	@Override
	public List<Integer> getRequiredFieldTMs() {
		if (romEntry.romType == RomType_FRLG) {
			return Arrays
					.asList(new Integer[] { 1, 2, 7, 8, 9, 11, 12, 14, 17, 18,
							21, 22, 25, 32, 36, 37, 40, 41, 44, 46, 47, 48, 49,
							50 });
		} else if (romEntry.romType == RomType_Ruby
				|| romEntry.romType == RomType_Sapp) {
			return Arrays.asList(new Integer[] { 1, 2, 6, 7, 11, 18, 22, 23,
					26, 30, 37, 48 });
		} else {
			// emerald has a few TMs from pickup
			return Arrays.asList(new Integer[] { 2, 6, 7, 11, 18, 22, 23, 30,
					37, 48 });
		}
	}

	@Override
	public List<Integer> getCurrentFieldTMs() {
		if (!mapLoadingDone) {
			preprocessMaps();
			mapLoadingDone = true;
		}
		List<Integer> fieldTMs = new ArrayList<Integer>();

		for (int offset : itemOffs) {
			int itemHere = readWord(offset);
			if (allowedItems.isTM(itemHere)) {
				int thisTM = itemHere - 0x120;
				// hack for repeat TMs
				if (fieldTMs.contains(thisTM) == false) {
					fieldTMs.add(thisTM);
				}
			}
		}
		return fieldTMs;
	}

	@Override
	public void setFieldTMs(List<Integer> fieldTMs) {
		if (!mapLoadingDone) {
			preprocessMaps();
			mapLoadingDone = true;
		}
		Iterator<Integer> iterTMs = fieldTMs.iterator();
		int[] givenTMs = new int[512];

		for (int offset : itemOffs) {
			int itemHere = readWord(offset);
			if (allowedItems.isTM(itemHere)) {
				// Cache replaced TMs to duplicate repeats
				if (givenTMs[itemHere] != 0) {
					rom[offset] = (byte) givenTMs[itemHere];
				} else {
					// Replace this with a TM from the list
					int tm = iterTMs.next();
					tm += 0x120;
					givenTMs[itemHere] = tm;
					writeWord(offset, tm);
				}
			}
		}
	}

	@Override
	public List<Integer> getRegularFieldItems() {
		if (!mapLoadingDone) {
			preprocessMaps();
			mapLoadingDone = true;
		}
		List<Integer> fieldItems = new ArrayList<Integer>();

		for (int offset : itemOffs) {
			int itemHere = readWord(offset);
			if (allowedItems.isAllowed(itemHere)
					&& !(allowedItems.isTM(itemHere))) {
				fieldItems.add(itemHere);
			}
		}
		return fieldItems;
	}

	@Override
	public void setRegularFieldItems(List<Integer> items) {
		if (!mapLoadingDone) {
			preprocessMaps();
			mapLoadingDone = true;
		}
		Iterator<Integer> iterItems = items.iterator();

		for (int offset : itemOffs) {
			int itemHere = readWord(offset);
			if (allowedItems.isAllowed(itemHere)
					&& !(allowedItems.isTM(itemHere))) {
				// Replace it
				writeWord(offset, iterItems.next());
			}
		}

	}

	@Override
	public List<IngameTrade> getIngameTrades() {
		List<IngameTrade> trades = new ArrayList<IngameTrade>();

		// info
		int tableOffset = romEntry.getValue("TradeTableOffset");
		int tableSize = romEntry.getValue("TradeTableSize");
		int[] unused = romEntry.arrayEntries.get("TradesUnused");
		int unusedOffset = 0;
		int entryLength = 60;

		for (int entry = 0; entry < tableSize; entry++) {
			if (unusedOffset < unused.length && unused[unusedOffset] == entry) {
				unusedOffset++;
				continue;
			}
			IngameTrade trade = new IngameTrade();
			int entryOffset = tableOffset + entry * entryLength;
			trade.nickname = readVariableLengthString(entryOffset);
			trade.givenPokemon = pokes[poke3GIndexToNum(readWord(entryOffset + 12))];
			trade.ivs = new int[6];
			for (int i = 0; i < 6; i++) {
				trade.ivs[i] = rom[entryOffset + 14 + i] & 0xFF;
			}
			trade.otId = readWord(entryOffset + 24);
			trade.item = readWord(entryOffset + 40);
			trade.otName = readVariableLengthString(entryOffset + 43);
			trade.requestedPokemon = pokes[poke3GIndexToNum(readWord(entryOffset + 56))];
			trades.add(trade);
		}

		return trades;

	}

	@Override
	public void setIngameTrades(List<IngameTrade> trades) {
		// info
		int tableOffset = romEntry.getValue("TradeTableOffset");
		int tableSize = romEntry.getValue("TradeTableSize");
		int[] unused = romEntry.arrayEntries.get("TradesUnused");
		int unusedOffset = 0;
		int entryLength = 60;
		int tradeOffset = 0;

		for (int entry = 0; entry < tableSize; entry++) {
			if (unusedOffset < unused.length && unused[unusedOffset] == entry) {
				unusedOffset++;
				continue;
			}
			IngameTrade trade = trades.get(tradeOffset++);
			int entryOffset = tableOffset + entry * entryLength;
			writeFixedLengthString(trade.nickname, entryOffset, 12);
			writeWord(entryOffset + 12,
					pokeNumTo3GIndex(trade.givenPokemon.number));
			for (int i = 0; i < 6; i++) {
				rom[entryOffset + 14 + i] = (byte) trade.ivs[i];
			}
			writeWord(entryOffset + 24, trade.otId);
			writeWord(entryOffset + 40, trade.item);
			writeFixedLengthString(trade.otName, entryOffset + 43, 11);
			writeWord(entryOffset + 56,
					pokeNumTo3GIndex(trade.requestedPokemon.number));
		}
	}

	@Override
	public boolean hasDVs() {
		return false;
	}

	@Override
	public int generationOfPokemon() {
		return 3;
	}

	@Override
	public void removeEvosForPokemonPool() {
		List<Pokemon> pokemonIncluded = this.mainPokemonList;
		int baseOffset = romEntry.getValue("PokemonEvolutions");
		for (int i = 1; i <= 386; i++) {
			boolean included = pokemonIncluded.contains(pokes[i]);
			int idx = pokeNumTo3GIndex(i);
			int evoOffset = baseOffset + (idx - 1) * 0x28;
			for (int j = 0; j < 5; j++) {
				int method = readWord(evoOffset + j * 8);
				int evolvingTo = readWord(evoOffset + j * 8 + 4);
				if (method >= 1 && method <= 15 && evolvingTo >= 1
						&& evolvingTo <= 411) {
					Pokemon evolvingInto = pokes[poke3GIndexToNum(evolvingTo)];
					if (!included || !pokemonIncluded.contains(evolvingInto)) {
						// remove this evolution
						writeWord(evoOffset + j * 8, 0);
						writeWord(evoOffset + j * 8 + 2, 0);
						writeWord(evoOffset + j * 8 + 4, 0);
					}
				}
			}
		}
	}

	@Override
	public boolean supportsFourStartingMoves() {
		return true;
	}
}
