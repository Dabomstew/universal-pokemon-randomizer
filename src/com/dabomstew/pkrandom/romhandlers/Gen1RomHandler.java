package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  Gen1RomHandler.java - randomizer handler for R/B/Y.					--*/
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import com.dabomstew.pkrandom.CodeTweaks;
import com.dabomstew.pkrandom.FileFunctions;
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

public class Gen1RomHandler extends AbstractGBRomHandler {

	// Important RBY Data Structures

	private int[] pokeNumToRBYTable;
	private int[] pokeRBYToNumTable;
	private int[] moveNumToRomTable;
	private int[] moveRomToNumTable;
	private int pokedexCount;

	private static final Type[] typeTable = constructTypeTable();

	private static Type[] constructTypeTable() {
		Type[] table = new Type[256];
		table[0x00] = Type.NORMAL;
		table[0x01] = Type.FIGHTING;
		table[0x02] = Type.FLYING;
		table[0x03] = Type.POISON;
		table[0x04] = Type.GROUND;
		table[0x05] = Type.ROCK;
		table[0x07] = Type.BUG;
		table[0x08] = Type.GHOST;
		table[0x14] = Type.FIRE;
		table[0x15] = Type.WATER;
		table[0x16] = Type.GRASS;
		table[0x17] = Type.ELECTRIC;
		table[0x18] = Type.PSYCHIC;
		table[0x19] = Type.ICE;
		table[0x1A] = Type.DRAGON;
		return table;
	}

	private Type idToType(int value) {
		if (typeTable[value] != null) {
			return typeTable[value];
		}
		if (romEntry.extraTypeLookup.containsKey(value)) {
			return romEntry.extraTypeLookup.get(value);
		}
		return null;
	}

	private byte typeToByte(Type type) {
		if (type == null) {
			return 0x00; // revert to normal
		}
		if (romEntry.extraTypeReverse.containsKey(type)) {
			return romEntry.extraTypeReverse.get(type).byteValue();
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
			return 0x07;
		case GHOST:
			return 0x08;
		case FIRE:
			return 0x14;
		case WATER:
			return 0x15;
		case GRASS:
			return 0x16;
		case ELECTRIC:
			return 0x17;
		case PSYCHIC:
			return 0x18;
		case ICE:
			return 0x19;
		case DRAGON:
			return 0x1A;
		case STEEL:
			return 0x17; // turn steel into electric, to account for
							// magnemite/magneton
		case DARK:
			return 0x08; // turn dark into ghost
		default:
			return 0; // normal by default
		}
	}

	private static class RomEntry {
		private String name;
		private String romName;
		private int version, nonJapanese;
		private String extraTableFile;
		private boolean isYellow;
		private int crcInHeader = -1;
		private Map<String, String> codeTweaks = new HashMap<String, String>();
		private List<TMTextEntry> tmTexts = new ArrayList<TMTextEntry>();
		private Map<String, Integer> entries = new HashMap<String, Integer>();
		private Map<String, int[]> arrayEntries = new HashMap<String, int[]>();
		private List<Integer> staticPokemonSingle = new ArrayList<Integer>();
		private List<GameCornerPokemon> staticPokemonGameCorner = new ArrayList<GameCornerPokemon>();
		private Map<Integer, Type> extraTypeLookup = new HashMap<Integer, Type>();
		private Map<Type, Integer> extraTypeReverse = new HashMap<Type, Integer>();

		private int getValue(String key) {
			if (!entries.containsKey(key)) {
				entries.put(key, 0);
			}
			return entries.get(key);
		}
	}

	private static List<RomEntry> roms;
	private static ItemList allowedItems;

	static {
		loadROMInfo();
		setupAllowedItems();
	}

	private static class GameCornerPokemon {
		private int[] offsets;

		public String toString() {
			return Arrays.toString(offsets);
		}
	}

	private static class TMTextEntry {
		private int number;
		private int offset;
		private String template;
	}

	private static void loadROMInfo() {
		roms = new ArrayList<RomEntry>();
		RomEntry current = null;
		try {
			Scanner sc = new Scanner(
					FileFunctions.openConfig("gen1_offsets.ini"), "UTF-8");
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
						r[0] = r[0].trim();
						// Static Pokemon?
						if (r[0].equals("StaticPokemonGameCorner[]")) {
							if (r[1].startsWith("[") && r[1].endsWith("]")) {
								String[] offsets = r[1].substring(1,
										r[1].length() - 1).split(",");
								int[] offs = new int[offsets.length];
								int c = 0;
								for (String off : offsets) {
									offs[c++] = parseRIInt(off);
								}
								GameCornerPokemon gc = new GameCornerPokemon();
								gc.offsets = offs;
								current.staticPokemonGameCorner.add(gc);
							} else {
								int offs = parseRIInt(r[1]);
								GameCornerPokemon gc = new GameCornerPokemon();
								gc.offsets = new int[] { offs };
								current.staticPokemonGameCorner.add(gc);
							}
						} else if (r[0].equals("TMText[]")) {
							if (r[1].startsWith("[") && r[1].endsWith("]")) {
								String[] parts = r[1].substring(1,
										r[1].length() - 1).split(",", 3);
								TMTextEntry tte = new TMTextEntry();
								tte.number = parseRIInt(parts[0]);
								tte.offset = parseRIInt(parts[1]);
								tte.template = parts[2];
								current.tmTexts.add(tte);
							}
						} else if (r[0].equals("Game")) {
							current.romName = r[1];
						} else if (r[0].equals("Version")) {
							current.version = parseRIInt(r[1]);
						} else if (r[0].equals("NonJapanese")) {
							current.nonJapanese = parseRIInt(r[1]);
						} else if (r[0].equals("Type")) {
							if (r[1].equalsIgnoreCase("Yellow")) {
								current.isYellow = true;
							} else {
								current.isYellow = false;
							}
						} else if (r[0].equals("ExtraTableFile")) {
							current.extraTableFile = r[1];
						} else if (r[0].equals("CRCInHeader")) {
							current.crcInHeader = parseRIInt(r[1]);
						} else if (r[0].endsWith("Tweak")) {
							current.codeTweaks.put(r[0], r[1]);
						} else if (r[0].equals("ExtraTypes")) {
							// remove the containers
							r[1] = r[1].substring(1, r[1].length() - 1);
							String[] parts = r[1].split(",");
							for (String part : parts) {
								String[] iParts = part.split("=");
								int typeId = Integer.parseInt(iParts[0], 16);
								String typeName = iParts[1].trim();
								Type theType = Type.valueOf(typeName);
								current.extraTypeLookup.put(typeId, theType);
								current.extraTypeReverse.put(theType, typeId);
							}
						} else if (r[0].equals("CopyFrom")) {
							for (RomEntry otherEntry : roms) {
								if (r[1].equalsIgnoreCase(otherEntry.name)) {
									// copy from here
									boolean cSP = (current
											.getValue("CopyStaticPokemon") == 1);
									boolean cTT = (current
											.getValue("CopyTMText") == 1);
									current.arrayEntries
											.putAll(otherEntry.arrayEntries);
									current.entries.putAll(otherEntry.entries);
									if (cSP) {
										current.staticPokemonSingle
												.addAll(otherEntry.staticPokemonSingle);
										current.staticPokemonGameCorner
												.addAll(otherEntry.staticPokemonGameCorner);
										current.entries.put(
												"StaticPokemonSupport", 1);
									} else {
										current.entries.put(
												"StaticPokemonSupport", 0);
									}
									if (cTT) {
										current.tmTexts
												.addAll(otherEntry.tmTexts);
									}
									current.extraTableFile = otherEntry.extraTableFile;
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
									if (r[0].startsWith("StaticPokemon")) {
										for (int off : offs) {
											current.staticPokemonSingle
													.add(off);
										}
									} else {
										current.arrayEntries.put(r[0], offs);
									}
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

	private static void setupAllowedItems() {
		allowedItems = new ItemList(250); // 251-255 are junk TMs
		// Assorted key items & junk
		// 23/01/2014: ban fake PP Up
		allowedItems.banSingles(5, 6, 7, 8, 9, 31, 48, 50, 59, 63, 64);
		allowedItems.banRange(21, 8);
		allowedItems.banRange(41, 5);
		allowedItems.banRange(69, 10);
		// Unused
		allowedItems.banRange(84, 112);
		// HMs
		allowedItems.banRange(196, 5);
		// Real TMs
		allowedItems.tmRange(201, 50);
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

	// This ROM's data
	private Pokemon[] pokes;
	private List<Pokemon> pokemonList;
	private RomEntry romEntry;
	private Move[] moves;
	private String[] tb;
	private Map<String, Byte> d;
	private int longestTableToken;
	private String[] itemNames;
	private String[] mapNames;
	private SubMap[] maps;
	private boolean xAccNerfed;

	@Override
	public boolean detectRom(byte[] rom) {
		if (rom.length < 524288 || rom.length > 2097152) {
			return false; // size check
		}
		return checkRomEntry(rom) != null; // so it's OK if it's a valid ROM
	}

	@Override
	public void loadedRom() {
		romEntry = checkRomEntry(this.rom);
		pokeNumToRBYTable = new int[256];
		pokeRBYToNumTable = new int[256];
		moveNumToRomTable = new int[256];
		moveRomToNumTable = new int[256];
		tb = new String[256];
		d = new HashMap<String, Byte>();
		maps = new SubMap[256];
		xAccNerfed = false;
		clearTextTables();
		readTextTable("gameboy_jap");
		if (romEntry.extraTableFile != null
				&& romEntry.extraTableFile.equalsIgnoreCase("none") == false) {
			readTextTable(romEntry.extraTableFile);
		}
		loadPokedexOrder();
		loadPokemonStats();
		pokemonList = Arrays.asList(pokes);
		loadMoves();
		preloadMaps();
		loadItemNames();
		loadMapNames();
	}

	private void loadPokedexOrder() {
		int pkmnCount = romEntry.getValue("InternalPokemonCount");
		int orderOffset = romEntry.getValue("PokedexOrder");
		pokedexCount = 0;
		for (int i = 1; i <= pkmnCount; i++) {
			int pokedexNum = rom[orderOffset + i - 1] & 0xFF;
			pokeRBYToNumTable[i] = pokedexNum;
			if (pokedexNum != 0 && pokeNumToRBYTable[pokedexNum] == 0) {
				pokeNumToRBYTable[pokedexNum] = i;
			}
			pokedexCount = Math.max(pokedexCount, pokedexNum);
		}
	}

	private RomEntry checkRomEntry(byte[] rom) {
		int version = rom[0x14C] & 0xFF;
		int nonjap = rom[0x14A] & 0xFF;
		// Check for specific CRC first
		int crcInHeader = ((rom[0x14E] & 0xFF) << 8) | (rom[0x14F] & 0xFF);
		for (RomEntry re : roms) {
			if (romSig(rom, re.romName) && re.version == version
					&& re.nonJapanese == nonjap
					&& re.crcInHeader == crcInHeader) {
				return re;
			}
		}
		// Now check for non-specific-CRC entries
		for (RomEntry re : roms) {
			if (romSig(rom, re.romName) && re.version == version
					&& re.nonJapanese == nonjap && re.crcInHeader == -1) {
				return re;
			}
		}
		// Not found
		return null;
	}

	private void clearTextTables() {
		tb = new String[256];
		d.clear();
		longestTableToken = 0;
	}

	private void readTextTable(String name) {
		try {
			Scanner sc = new Scanner(FileFunctions.openConfig(name + ".tbl"),
					"UTF-8");
			while (sc.hasNextLine()) {
				String q = sc.nextLine();
				if (!q.trim().isEmpty()) {
					String[] r = q.split("=", 2);
					if (r[1].endsWith("\r\n")) {
						r[1] = r[1].substring(0, r[1].length() - 2);
					}
					int hexcode = Integer.parseInt(r[0], 16);
					if (tb[hexcode] != null) {
						String oldMatch = tb[hexcode];
						tb[hexcode] = null;
						if (d.get(oldMatch) == hexcode) {
							d.remove(oldMatch);
						}
					}
					tb[hexcode] = r[1];
					longestTableToken = Math.max(longestTableToken,
							r[1].length());
					d.put(r[1], (byte) hexcode);
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
		}

	}

	@Override
	public void savingRom() {
		savePokemonStats();
		saveMoves();
	}

	private String[] readMoveNames() {
		int moveCount = romEntry.getValue("MoveCount");
		int offset = romEntry.getValue("MoveNamesOffset");
		String[] moveNames = new String[moveCount + 1];
		for (int i = 1; i <= moveCount; i++) {
			moveNames[i] = readVariableLengthString(offset);
			offset += lengthOfStringAt(offset) + 1;
		}
		return moveNames;
	}

	private void loadMoves() {
		String[] moveNames = readMoveNames();
		int moveCount = romEntry.getValue("MoveCount");
		int movesOffset = romEntry.getValue("MoveDataOffset");
		// check real move count
		int trueMoveCount = 0;
		for (int i = 1; i <= moveCount; i++) {
			// temp hack for Brown
			if (rom[movesOffset + (i - 1) * 6] != 0
					&& moveNames[i].equals("Nothing") == false) {
				trueMoveCount++;
			}
		}
		moves = new Move[trueMoveCount + 1];
		int trueMoveIndex = 0;

		for (int i = 1; i <= moveCount; i++) {
			int anim = rom[movesOffset + (i - 1) * 6] & 0xFF;
			// another temp hack for brown
			if (anim > 0 && moveNames[i].equals("Nothing") == false) {
				trueMoveIndex++;
				moveNumToRomTable[trueMoveIndex] = i;
				moveRomToNumTable[i] = trueMoveIndex;
				moves[trueMoveIndex] = new Move();
				moves[trueMoveIndex].name = moveNames[i];
				moves[trueMoveIndex].internalId = i;
				moves[trueMoveIndex].number = trueMoveIndex;
				moves[trueMoveIndex].effectIndex = rom[movesOffset + (i - 1)
						* 6 + 1] & 0xFF;
				moves[trueMoveIndex].hitratio = ((rom[movesOffset + (i - 1) * 6
						+ 4] & 0xFF) + 0) / 255.0 * 100;
				moves[trueMoveIndex].power = rom[movesOffset + (i - 1) * 6 + 2] & 0xFF;
				moves[trueMoveIndex].pp = rom[movesOffset + (i - 1) * 6 + 5] & 0xFF;
				moves[trueMoveIndex].type = idToType(rom[movesOffset + (i - 1)
						* 6 + 3] & 0xFF);
			}
		}

	}

	private void saveMoves() {
		int movesOffset = romEntry.getValue("MoveDataOffset");
		for (Move m : moves) {
			if (m != null) {
				int i = m.internalId;
				rom[movesOffset + (i - 1) * 6 + 1] = (byte) m.effectIndex;
				rom[movesOffset + (i - 1) * 6 + 2] = (byte) m.power;
				rom[movesOffset + (i - 1) * 6 + 3] = typeToByte(m.type);
				int hitratio = (int) Math.round(m.hitratio * 2.55);
				if (hitratio < 0) {
					hitratio = 0;
				}
				if (hitratio > 255) {
					hitratio = 255;
				}
				rom[movesOffset + (i - 1) * 6 + 4] = (byte) hitratio;
				rom[movesOffset + (i - 1) * 6 + 5] = (byte) m.pp;
			}
		}
	}

	public List<Move> getMoves() {
		return Arrays.asList(moves);
	}

	private void loadPokemonStats() {
		pokes = new Pokemon[pokedexCount + 1];
		// Fetch our names
		String[] pokeNames = readPokemonNames();
		// Get base stats
		int pokeStatsOffset = romEntry.getValue("PokemonStatsOffset");
		for (int i = 1; i <= pokedexCount; i++) {
			pokes[i] = new Pokemon();
			pokes[i].number = i;
			loadBasicPokeStats(pokes[i], pokeStatsOffset + (i - 1) * 0x1C);
			// Name?
			pokes[i].name = pokeNames[pokeNumToRBYTable[i]];
		}

		// Mew override for R/B
		if (!romEntry.isYellow) {
			loadBasicPokeStats(pokes[151], romEntry.getValue("MewStatsOffset"));
		}

	}

	private void savePokemonStats() {
		// Write pokemon names
		int offs = romEntry.getValue("PokemonNamesOffset");
		int nameLength = romEntry.getValue("PokemonNamesLength");
		for (int i = 1; i <= pokedexCount; i++) {
			int rbynum = pokeNumToRBYTable[i];
			int stringOffset = offs + (rbynum - 1) * nameLength;
			writeFixedLengthString(pokes[i].name, stringOffset, nameLength);
		}
		// Write pokemon stats
		int pokeStatsOffset = romEntry.getValue("PokemonStatsOffset");
		for (int i = 1; i <= pokedexCount; i++) {
			if (i == 151) {
				continue;
			}
			saveBasicPokeStats(pokes[i], pokeStatsOffset + (i - 1) * 0x1C);
		}
		// Write MEW
		int mewOffset = romEntry.isYellow ? pokeStatsOffset + 150 * 0x1C
				: romEntry.getValue("MewStatsOffset");
		saveBasicPokeStats(pokes[151], mewOffset);
	}

	private void loadBasicPokeStats(Pokemon pkmn, int offset) {
		pkmn.hp = rom[offset + 1] & 0xFF;
		pkmn.attack = rom[offset + 2] & 0xFF;
		pkmn.defense = rom[offset + 3] & 0xFF;
		pkmn.speed = rom[offset + 4] & 0xFF;
		pkmn.special = rom[offset + 5] & 0xFF;
		pkmn.spatk = pkmn.special;
		pkmn.spdef = pkmn.special;
		// Type
		pkmn.primaryType = idToType(rom[offset + 6] & 0xFF);
		pkmn.secondaryType = idToType(rom[offset + 7] & 0xFF);
		// Only one type?
		if (pkmn.secondaryType == pkmn.primaryType) {
			pkmn.secondaryType = null;
		}

		pkmn.catchRate = rom[offset + 8] & 0xFF;
		pkmn.growthCurve = ExpCurve.fromByte(rom[offset + 19]);

		pkmn.guaranteedHeldItem = -1;
		pkmn.commonHeldItem = -1;
		pkmn.rareHeldItem = -1;
		pkmn.darkGrassHeldItem = -1;
	}

	private void saveBasicPokeStats(Pokemon pkmn, int offset) {
		rom[offset + 1] = (byte) pkmn.hp;
		rom[offset + 2] = (byte) pkmn.attack;
		rom[offset + 3] = (byte) pkmn.defense;
		rom[offset + 4] = (byte) pkmn.speed;
		rom[offset + 5] = (byte) pkmn.special;
		rom[offset + 6] = typeToByte(pkmn.primaryType);
		if (pkmn.secondaryType == null) {
			rom[offset + 7] = rom[offset + 6];
		} else {
			rom[offset + 7] = typeToByte(pkmn.secondaryType);
		}
		rom[offset + 8] = (byte) pkmn.catchRate;
		rom[offset + 19] = pkmn.growthCurve.toByte();
	}

	private String[] readPokemonNames() {
		int offs = romEntry.getValue("PokemonNamesOffset");
		int nameLength = romEntry.getValue("PokemonNamesLength");
		int pkmnCount = romEntry.getValue("InternalPokemonCount");
		String[] names = new String[pkmnCount + 1];
		for (int i = 1; i <= pkmnCount; i++) {
			names[i] = readFixedLengthString(offs + (i - 1) * nameLength,
					nameLength);
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
				if (currChar == 0x50 || currChar == 0x00) {
					break;
				} else {
					string.append("\\x" + String.format("%02X", currChar));
				}
			}
		}
		return string.toString();
	}

	public byte[] translateString(String text) {
		List<Byte> data = new ArrayList<Byte>();
		while (text.length() != 0) {
			int i = Math.max(0, longestTableToken - text.length());
			if (text.charAt(0) == '\\' && text.charAt(1) == 'x') {
				data.add((byte) Integer.parseInt(text.substring(2, 4), 16));
				text = text.substring(4);
			} else {
				while (!(d
						.containsKey(text.substring(0, longestTableToken - i)) || (i == longestTableToken))) {
					i++;
				}
				if (i == longestTableToken) {
					text = text.substring(1);
				} else {
					data.add(d.get(text.substring(0, longestTableToken - i)));
					text = text.substring(longestTableToken - i);
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

	private void writeFixedLengthString(String str, int offset, int length) {
		byte[] translated = translateString(str);
		int len = Math.min(translated.length, length);
		System.arraycopy(translated, 0, rom, offset, len);
		while (len < length) {
			rom[offset + len] = 0x50;
			len++;
		}
	}

	private int makeGBPointer(int offset) {
		if (offset < 0x4000) {
			return offset;
		} else {
			return (offset % 0x4000) + 0x4000;
		}
	}

	private int bankOf(int offset) {
		return (offset / 0x4000);
	}

	private int calculateOffset(int bank, int pointer) {
		if (pointer < 0x4000) {
			return pointer;
		} else {
			return (pointer % 0x4000) + bank * 0x4000;
		}
	}

	public String readVariableLengthString(int offset) {
		return readString(offset, Integer.MAX_VALUE);
	}

	public byte[] traduire(String str) {
		return translateString(str);
	}

	public String readVariableLengthScriptString(int offset) {
		return readString(offset, Integer.MAX_VALUE);
	}

	private void writeFixedLengthScriptString(String str, int offset, int length) {
		byte[] translated = translateString(str);
		int len = Math.min(translated.length, length);
		System.arraycopy(translated, 0, rom, offset, len);
		while (len < length) {
			rom[offset + len] = 0x00;
			len++;
		}
	}

	private int lengthOfStringAt(int offset) {
		int len = 0;
		while (rom[offset + len] != 0x50 && rom[offset + len] != 0x00) {
			len++;
		}
		return len;
	}

	private boolean romSig(byte[] rom, String sig) {
		try {
			int sigOffset = 0x134;
			byte[] sigBytes = sig.getBytes("US-ASCII");
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

	@Override
	public boolean isInGame(Pokemon pkmn) {
		return (pkmn.number >= 1 && pkmn.number <= pokedexCount);
	}

	@Override
	public boolean isInGame(int pokemonNumber) {
		return (pokemonNumber >= 1 && pokemonNumber <= pokedexCount);
	}

	@Override
	public List<Pokemon> getStarters() {
		// Get the starters
		List<Pokemon> starters = new ArrayList<Pokemon>();
		starters.add(pokes[pokeRBYToNumTable[rom[romEntry.arrayEntries
				.get("StarterOffsets1")[0]] & 0xFF]]);
		starters.add(pokes[pokeRBYToNumTable[rom[romEntry.arrayEntries
				.get("StarterOffsets2")[0]] & 0xFF]]);
		if (!romEntry.isYellow) {
			starters.add(pokes[pokeRBYToNumTable[rom[romEntry.arrayEntries
					.get("StarterOffsets3")[0]] & 0xFF]]);
		}
		return starters;
	}

	@Override
	public boolean setStarters(List<Pokemon> newStarters) {
		// Amount?
		int starterAmount = 2;
		if (!romEntry.isYellow) {
			starterAmount = 3;
		}

		// Basic checks
		if (newStarters.size() != starterAmount) {
			return false;
		}

		for (Pokemon pkmn : newStarters) {
			if (!isInGame(pkmn)) {
				return false;
			}
		}

		// Patch starter bytes
		for (int i = 0; i < starterAmount; i++) {
			byte starter = (byte) pokeNumToRBYTable[newStarters.get(i).number];
			int[] offsets = romEntry.arrayEntries.get("StarterOffsets"
					+ (i + 1));
			for (int offset : offsets) {
				rom[offset] = starter;
			}
		}

		// Special stuff for non-Yellow only

		if (!romEntry.isYellow) {

			// Starter text
			if (romEntry.getValue("CanChangeStarterText") > 0) {
				List<Integer> starterTextOffsets = RomFunctions.search(rom,
						traduire("So! You want the"));
				for (int i = 0; i < 3 && i < starterTextOffsets.size(); i++) {
					writeFixedLengthScriptString("So! You want\\n"
							+ newStarters.get(i).name + "?\\e",
							starterTextOffsets.get(i),
							lengthOfStringAt(starterTextOffsets.get(i)) + 1);
				}
			}

			// Patch starter pokedex routine?
			// Can only do in 1M roms because of size concerns
			if (romEntry.getValue("PatchPokedex") > 0) {

				// Starter pokedex required RAM values
				// RAM offset => value
				// Allows for multiple starters in the same RAM byte
				Map<Integer, Integer> onValues = new TreeMap<Integer, Integer>();
				for (int i = 0; i < 3; i++) {
					int pkDexNum = newStarters.get(i).number;
					int ramOffset = (pkDexNum - 1) / 8
							+ romEntry.getValue("PokedexRamOffset");
					int bitShift = (pkDexNum - 1) % 8;
					int writeValue = 1 << bitShift;
					if (onValues.containsKey(ramOffset)) {
						onValues.put(ramOffset, onValues.get(ramOffset)
								| writeValue);
					} else {
						onValues.put(ramOffset, writeValue);
					}
				}

				// Starter pokedex offset/pointer calculations

				int pkDexOnOffset = romEntry.getValue("StarterPokedexOnOffset");
				int pkDexOffOffset = romEntry
						.getValue("StarterPokedexOffOffset");

				int sizeForOnRoutine = 5 * onValues.size() + 3;
				int writeOnRoutineTo = romEntry
						.getValue("StarterPokedexBranchOffset");
				int writeOffRoutineTo = writeOnRoutineTo + sizeForOnRoutine;
				int offsetForOnRoutine = makeGBPointer(writeOnRoutineTo);
				int offsetForOffRoutine = makeGBPointer(writeOffRoutineTo);
				int retOnOffset = makeGBPointer(pkDexOnOffset + 5);
				int retOffOffset = makeGBPointer(pkDexOffOffset + 4);

				// Starter pokedex
				// Branch to our new routine(s)

				// Turn bytes on
				rom[pkDexOnOffset] = (byte) 0xC3;
				writeWord(pkDexOnOffset + 1, offsetForOnRoutine);
				rom[pkDexOnOffset + 3] = 0x00;
				rom[pkDexOnOffset + 4] = 0x00;

				// Turn bytes off
				rom[pkDexOffOffset] = (byte) 0xC3;
				writeWord(pkDexOffOffset + 1, offsetForOffRoutine);
				rom[pkDexOffOffset + 3] = 0x00;

				// Put together the two scripts
				rom[writeOffRoutineTo] = (byte) 0xAF;
				int turnOnOffset = writeOnRoutineTo;
				int turnOffOffset = writeOffRoutineTo + 1;
				for (int ramOffset : onValues.keySet()) {
					int onValue = onValues.get(ramOffset);
					// Turn on code
					rom[turnOnOffset++] = 0x3E;
					rom[turnOnOffset++] = (byte) onValue;
					// Turn on code for ram writing
					rom[turnOnOffset++] = (byte) 0xEA;
					rom[turnOnOffset++] = (byte) (ramOffset % 0x100);
					rom[turnOnOffset++] = (byte) (ramOffset / 0x100);
					// Turn off code for ram writing
					rom[turnOffOffset++] = (byte) 0xEA;
					rom[turnOffOffset++] = (byte) (ramOffset % 0x100);
					rom[turnOffOffset++] = (byte) (ramOffset / 0x100);
				}
				// Jump back
				rom[turnOnOffset++] = (byte) 0xC3;
				writeWord(turnOnOffset, retOnOffset);

				rom[turnOffOffset++] = (byte) 0xC3;
				writeWord(turnOffOffset, retOffOffset);
			}

		}

		return true;

	}

	@Override
	public List<Integer> getStarterHeldItems() {
		// do nothing
		return new ArrayList<Integer>();
	}

	@Override
	public void setStarterHeldItems(List<Integer> items) {
		// do nothing
	}

	@Override
	public void shufflePokemonStats() {
		for (int i = 1; i <= pokedexCount; i++) {
			pokes[i].shuffleStats();
		}
	}

	@Override
	public List<EncounterSet> getEncounters(boolean useTimeOfDay) {
		List<EncounterSet> encounters = new ArrayList<EncounterSet>();

		// grass & water
		List<Integer> usedOffsets = new ArrayList<Integer>();
		int tableOffset = romEntry.getValue("WildPokemonTableOffset");
		int tableBank = bankOf(tableOffset);
		int mapID = -1;

		while (readWord(tableOffset) != 0xFFFF) {
			mapID++;
			int offset = calculateOffset(tableBank, readWord(tableOffset));
			int rootOffset = offset;
			if (!usedOffsets.contains(offset)) {
				usedOffsets.add(offset);
				// grass and water are exactly the same
				for (int a = 0; a < 2; a++) {
					int rate = rom[offset++] & 0xFF;
					if (rate > 0) {
						// there is data here
						EncounterSet thisSet = new EncounterSet();
						thisSet.rate = rate;
						thisSet.offset = rootOffset;
						thisSet.displayName = (a == 1 ? "Surfing"
								: "Grass/Cave") + " on " + mapNames[mapID];
						for (int slot = 0; slot < 10; slot++) {
							Encounter enc = new Encounter();
							enc.level = rom[offset] & 0xFF;
							enc.pokemon = pokes[pokeRBYToNumTable[rom[offset + 1] & 0xFF]];
							thisSet.encounters.add(enc);
							offset += 2;
						}
						encounters.add(thisSet);
					}
				}
			} else {
				for (EncounterSet es : encounters) {
					if (es.offset == offset) {
						es.displayName += ", " + mapNames[mapID];
					}
				}
			}
			tableOffset += 2;
		}

		// old rod
		int oldRodOffset = romEntry.getValue("OldRodOffset");
		EncounterSet oldRodSet = new EncounterSet();
		oldRodSet.displayName = "Old Rod Fishing";
		Encounter oldRodEnc = new Encounter();
		oldRodEnc.level = rom[oldRodOffset + 2] & 0xFF;
		oldRodEnc.pokemon = pokes[pokeRBYToNumTable[rom[oldRodOffset + 1] & 0xFF]];
		oldRodSet.encounters.add(oldRodEnc);
		encounters.add(oldRodSet);

		// good rod
		int goodRodOffset = romEntry.getValue("GoodRodOffset");
		EncounterSet goodRodSet = new EncounterSet();
		goodRodSet.displayName = "Good Rod Fishing";
		for (int grSlot = 0; grSlot < 2; grSlot++) {
			Encounter enc = new Encounter();
			enc.level = rom[goodRodOffset + grSlot * 2] & 0xFF;
			enc.pokemon = pokes[pokeRBYToNumTable[rom[goodRodOffset + grSlot
					* 2 + 1] & 0xFF]];
			goodRodSet.encounters.add(enc);
		}
		encounters.add(goodRodSet);

		// super rod
		if (romEntry.isYellow) {
			int superRodOffset = romEntry.getValue("SuperRodTableOffset");
			while ((rom[superRodOffset] & 0xFF) != 0xFF) {
				int map = rom[superRodOffset++] & 0xFF;
				EncounterSet thisSet = new EncounterSet();
				thisSet.displayName = "Super Rod Fishing on " + mapNames[map];
				for (int encN = 0; encN < 4; encN++) {
					Encounter enc = new Encounter();
					enc.level = rom[superRodOffset + 1] & 0xFF;
					enc.pokemon = pokes[pokeRBYToNumTable[rom[superRodOffset] & 0xFF]];
					thisSet.encounters.add(enc);
					superRodOffset += 2;
				}
				encounters.add(thisSet);
			}
		} else {
			// red/blue
			int superRodOffset = romEntry.getValue("SuperRodTableOffset");
			int superRodBank = bankOf(superRodOffset);
			List<Integer> usedSROffsets = new ArrayList<Integer>();
			while ((rom[superRodOffset] & 0xFF) != 0xFF) {
				int map = rom[superRodOffset++] & 0xFF;
				int setOffset = calculateOffset(superRodBank,
						readWord(superRodOffset));
				superRodOffset += 2;
				if (!usedSROffsets.contains(setOffset)) {
					usedSROffsets.add(setOffset);
					EncounterSet thisSet = new EncounterSet();
					thisSet.displayName = "Super Rod Fishing on "
							+ mapNames[map];
					thisSet.offset = setOffset;
					int pokesInSet = rom[setOffset++] & 0xFF;
					for (int encN = 0; encN < pokesInSet; encN++) {
						Encounter enc = new Encounter();
						enc.level = rom[setOffset] & 0xFF;
						enc.pokemon = pokes[pokeRBYToNumTable[rom[setOffset + 1] & 0xFF]];
						thisSet.encounters.add(enc);
						setOffset += 2;
					}
					encounters.add(thisSet);
				} else {
					for (EncounterSet es : encounters) {
						if (es.offset == setOffset) {
							es.displayName += ", " + mapNames[map];
						}
					}
				}
			}
		}

		return encounters;
	}

	@Override
	public void setEncounters(boolean useTimeOfDay,
			List<EncounterSet> encounters) {
		Iterator<EncounterSet> encsetit = encounters.iterator();

		// grass & water
		List<Integer> usedOffsets = new ArrayList<Integer>();
		int tableOffset = romEntry.getValue("WildPokemonTableOffset");
		int tableBank = bankOf(tableOffset);

		while (readWord(tableOffset) != 0xFFFF) {
			int offset = calculateOffset(tableBank, readWord(tableOffset));
			if (!usedOffsets.contains(offset)) {
				usedOffsets.add(offset);
				// grass and water are exactly the same
				for (int a = 0; a < 2; a++) {
					int rate = rom[offset++] & 0xFF;
					if (rate > 0) {
						// there is data here
						EncounterSet thisSet = encsetit.next();
						for (int slot = 0; slot < 10; slot++) {
							Encounter enc = thisSet.encounters.get(slot);
							rom[offset] = (byte) enc.level;
							rom[offset + 1] = (byte) pokeNumToRBYTable[enc.pokemon.number];
							offset += 2;
						}
					}
				}
			}
			tableOffset += 2;
		}

		// old rod
		int oldRodOffset = romEntry.getValue("OldRodOffset");
		EncounterSet oldRodSet = encsetit.next();
		Encounter oldRodEnc = oldRodSet.encounters.get(0);
		rom[oldRodOffset + 2] = (byte) oldRodEnc.level;
		rom[oldRodOffset + 1] = (byte) pokeNumToRBYTable[oldRodEnc.pokemon.number];

		// good rod
		int goodRodOffset = romEntry.getValue("GoodRodOffset");
		EncounterSet goodRodSet = encsetit.next();
		for (int grSlot = 0; grSlot < 2; grSlot++) {
			Encounter enc = goodRodSet.encounters.get(grSlot);
			rom[goodRodOffset + grSlot * 2] = (byte) enc.level;
			rom[goodRodOffset + grSlot * 2 + 1] = (byte) pokeNumToRBYTable[enc.pokemon.number];
		}

		// super rod
		if (romEntry.isYellow) {
			int superRodOffset = romEntry.getValue("SuperRodTableOffset");
			while ((rom[superRodOffset] & 0xFF) != 0xFF) {
				superRodOffset++;
				EncounterSet thisSet = encsetit.next();
				for (int encN = 0; encN < 4; encN++) {
					Encounter enc = thisSet.encounters.get(encN);
					rom[superRodOffset + 1] = (byte) enc.level;
					rom[superRodOffset] = (byte) pokeNumToRBYTable[enc.pokemon.number];
					superRodOffset += 2;
				}
			}
		} else {
			// red/blue
			int superRodOffset = romEntry.getValue("SuperRodTableOffset");
			int superRodBank = bankOf(superRodOffset);
			List<Integer> usedSROffsets = new ArrayList<Integer>();
			while ((rom[superRodOffset] & 0xFF) != 0xFF) {
				superRodOffset++;
				int setOffset = calculateOffset(superRodBank,
						readWord(superRodOffset));
				superRodOffset += 2;
				if (!usedSROffsets.contains(setOffset)) {
					usedSROffsets.add(setOffset);
					int pokesInSet = rom[setOffset++] & 0xFF;
					EncounterSet thisSet = encsetit.next();
					for (int encN = 0; encN < pokesInSet; encN++) {
						Encounter enc = thisSet.encounters.get(encN);
						rom[setOffset] = (byte) enc.level;
						rom[setOffset + 1] = (byte) pokeNumToRBYTable[enc.pokemon.number];
						setOffset += 2;
					}
				}
			}
		}
	}

	@Override
	public List<Pokemon> getPokemon() {
		return pokemonList;
	}

	public List<Trainer> getTrainers() {
		int traineroffset = romEntry.getValue("TrainerDataTableOffset");
		int traineramount = 47;
		int[] trainerclasslimits = romEntry.arrayEntries
				.get("TrainerDataClassCounts");

		int[] pointers = new int[traineramount + 1];
		for (int i = 1; i <= traineramount; i++) {
			int tPointer = readWord(traineroffset + (i - 1) * 2);
			pointers[i] = calculateOffset(bankOf(traineroffset), tPointer);
		}

		List<String> tcnames = getTrainerClassesForText();

		List<Trainer> allTrainers = new ArrayList<Trainer>();
		for (int i = 1; i <= traineramount; i++) {
			int offs = pointers[i];
			int limit = trainerclasslimits[i];
			String tcname = tcnames.get(i - 1);
			for (int trnum = 0; trnum < limit; trnum++) {
				Trainer tr = new Trainer();
				tr.offset = offs;
				tr.trainerclass = i;
				tr.fullDisplayName = tcname;
				int dataType = rom[offs] & 0xFF;
				if (dataType == 0xFF) {
					// "Special" trainer
					tr.poketype = 1;
					offs++;
					while (rom[offs] != 0x0) {
						TrainerPokemon tpk = new TrainerPokemon();
						tpk.level = rom[offs] & 0xFF;
						tpk.pokemon = pokes[pokeRBYToNumTable[rom[offs + 1] & 0xFF]];
						tr.pokemon.add(tpk);
						offs += 2;
					}
				} else {
					tr.poketype = 0;
					int fixedLevel = dataType;
					offs++;
					while (rom[offs] != 0x0) {
						TrainerPokemon tpk = new TrainerPokemon();
						tpk.level = fixedLevel;
						tpk.pokemon = pokes[pokeRBYToNumTable[rom[offs] & 0xFF]];
						tr.pokemon.add(tpk);
						offs++;
					}
				}
				offs++;
				allTrainers.add(tr);
			}
		}
		tagTrainersUniversal(allTrainers);
		if (romEntry.isYellow) {
			tagTrainersYellow(allTrainers);
		} else {
			tagTrainersRB(allTrainers);
		}
		return allTrainers;
	}

	private void tagTrainersUniversal(List<Trainer> trs) {
		// Gym Leaders
		tbc(trs, 34, 0, "GYM1");
		tbc(trs, 35, 0, "GYM2");
		tbc(trs, 36, 0, "GYM3");
		tbc(trs, 37, 0, "GYM4");
		tbc(trs, 38, 0, "GYM5");
		tbc(trs, 40, 0, "GYM6");
		tbc(trs, 39, 0, "GYM7");
		tbc(trs, 29, 2, "GYM8");

		// Other giovanni teams
		tbc(trs, 29, 0, "GIO1");
		tbc(trs, 29, 1, "GIO2");

		// Elite 4
		tbc(trs, 44, 0, "ELITE1");
		tbc(trs, 33, 0, "ELITE2");
		tbc(trs, 46, 0, "ELITE3");
		tbc(trs, 47, 0, "ELITE4");
	}

	private void tagTrainersRB(List<Trainer> trs) {
		// Gary Battles
		tbc(trs, 25, 0, "RIVAL1-0");
		tbc(trs, 25, 1, "RIVAL1-1");
		tbc(trs, 25, 2, "RIVAL1-2");

		tbc(trs, 25, 3, "RIVAL2-0");
		tbc(trs, 25, 4, "RIVAL2-1");
		tbc(trs, 25, 5, "RIVAL2-2");

		tbc(trs, 25, 6, "RIVAL3-0");
		tbc(trs, 25, 7, "RIVAL3-1");
		tbc(trs, 25, 8, "RIVAL3-2");

		tbc(trs, 42, 0, "RIVAL4-0");
		tbc(trs, 42, 1, "RIVAL4-1");
		tbc(trs, 42, 2, "RIVAL4-2");

		tbc(trs, 42, 3, "RIVAL5-0");
		tbc(trs, 42, 4, "RIVAL5-1");
		tbc(trs, 42, 5, "RIVAL5-2");

		tbc(trs, 42, 6, "RIVAL6-0");
		tbc(trs, 42, 7, "RIVAL6-1");
		tbc(trs, 42, 8, "RIVAL6-2");

		tbc(trs, 42, 9, "RIVAL7-0");
		tbc(trs, 42, 10, "RIVAL7-1");
		tbc(trs, 42, 11, "RIVAL7-2");

		tbc(trs, 43, 0, "RIVAL8-0");
		tbc(trs, 43, 1, "RIVAL8-1");
		tbc(trs, 43, 2, "RIVAL8-2");

		// Gym Trainers
		tbc(trs, 5, 0, "GYM1");

		tbc(trs, 15, 0, "GYM2");
		tbc(trs, 6, 0, "GYM2");

		tbc(trs, 4, 7, "GYM3");
		tbc(trs, 20, 0, "GYM3");
		tbc(trs, 41, 2, "GYM3");

		tbc(trs, 3, 16, "GYM4");
		tbc(trs, 3, 17, "GYM4");
		tbc(trs, 6, 10, "GYM4");
		tbc(trs, 18, 0, "GYM4");
		tbc(trs, 18, 1, "GYM4");
		tbc(trs, 18, 2, "GYM4");
		tbc(trs, 32, 0, "GYM4");

		tbc(trs, 21, 2, "GYM5");
		tbc(trs, 21, 3, "GYM5");
		tbc(trs, 21, 6, "GYM5");
		tbc(trs, 21, 7, "GYM5");
		tbc(trs, 22, 0, "GYM5");
		tbc(trs, 22, 1, "GYM5");

		tbc(trs, 19, 0, "GYM6");
		tbc(trs, 19, 1, "GYM6");
		tbc(trs, 19, 2, "GYM6");
		tbc(trs, 19, 3, "GYM6");
		tbc(trs, 45, 21, "GYM6");
		tbc(trs, 45, 22, "GYM6");
		tbc(trs, 45, 23, "GYM6");

		tbc(trs, 8, 8, "GYM7");
		tbc(trs, 8, 9, "GYM7");
		tbc(trs, 8, 10, "GYM7");
		tbc(trs, 8, 11, "GYM7");
		tbc(trs, 11, 3, "GYM7");
		tbc(trs, 11, 4, "GYM7");
		tbc(trs, 11, 5, "GYM7");

		tbc(trs, 22, 2, "GYM8");
		tbc(trs, 22, 3, "GYM8");
		tbc(trs, 24, 5, "GYM8");
		tbc(trs, 24, 6, "GYM8");
		tbc(trs, 24, 7, "GYM8");
		tbc(trs, 31, 0, "GYM8");
		tbc(trs, 31, 8, "GYM8");
		tbc(trs, 31, 9, "GYM8");
	}

	private void tagTrainersYellow(List<Trainer> trs) {
		// Rival Battles
		tbc(trs, 25, 0, "IRIVAL");

		tbc(trs, 25, 1, "RIVAL1-0");

		tbc(trs, 25, 2, "RIVAL2-0");

		tbc(trs, 42, 0, "RIVAL3-0");

		tbc(trs, 42, 1, "RIVAL4-0");
		tbc(trs, 42, 2, "RIVAL4-1");
		tbc(trs, 42, 3, "RIVAL4-2");

		tbc(trs, 42, 4, "RIVAL5-0");
		tbc(trs, 42, 5, "RIVAL5-1");
		tbc(trs, 42, 6, "RIVAL5-2");

		tbc(trs, 42, 7, "RIVAL6-0");
		tbc(trs, 42, 8, "RIVAL6-1");
		tbc(trs, 42, 9, "RIVAL6-2");

		tbc(trs, 43, 0, "RIVAL7-0");
		tbc(trs, 43, 1, "RIVAL7-1");
		tbc(trs, 43, 2, "RIVAL7-2");

		// Rocket Jessie & James
		tbc(trs, 30, 41, "THEMED:JESSIE&JAMES");
		tbc(trs, 30, 42, "THEMED:JESSIE&JAMES");
		tbc(trs, 30, 43, "THEMED:JESSIE&JAMES");
		tbc(trs, 30, 44, "THEMED:JESSIE&JAMES");

		// Gym Trainers
		tbc(trs, 5, 0, "GYM1");

		tbc(trs, 6, 0, "GYM2");
		tbc(trs, 15, 0, "GYM2");

		tbc(trs, 4, 7, "GYM3");
		tbc(trs, 20, 0, "GYM3");
		tbc(trs, 41, 2, "GYM3");

		tbc(trs, 3, 16, "GYM4");
		tbc(trs, 3, 17, "GYM4");
		tbc(trs, 6, 10, "GYM4");
		tbc(trs, 18, 0, "GYM4");
		tbc(trs, 18, 1, "GYM4");
		tbc(trs, 18, 2, "GYM4");
		tbc(trs, 32, 0, "GYM4");

		tbc(trs, 21, 2, "GYM5");
		tbc(trs, 21, 3, "GYM5");
		tbc(trs, 21, 6, "GYM5");
		tbc(trs, 21, 7, "GYM5");
		tbc(trs, 22, 0, "GYM5");
		tbc(trs, 22, 1, "GYM5");

		tbc(trs, 19, 0, "GYM6");
		tbc(trs, 19, 1, "GYM6");
		tbc(trs, 19, 2, "GYM6");
		tbc(trs, 19, 3, "GYM6");
		tbc(trs, 45, 21, "GYM6");
		tbc(trs, 45, 22, "GYM6");
		tbc(trs, 45, 23, "GYM6");

		tbc(trs, 8, 8, "GYM7");
		tbc(trs, 8, 9, "GYM7");
		tbc(trs, 8, 10, "GYM7");
		tbc(trs, 8, 11, "GYM7");
		tbc(trs, 11, 3, "GYM7");
		tbc(trs, 11, 4, "GYM7");
		tbc(trs, 11, 5, "GYM7");

		tbc(trs, 22, 2, "GYM8");
		tbc(trs, 22, 3, "GYM8");
		tbc(trs, 24, 5, "GYM8");
		tbc(trs, 24, 6, "GYM8");
		tbc(trs, 24, 7, "GYM8");
		tbc(trs, 31, 0, "GYM8");
		tbc(trs, 31, 8, "GYM8");
		tbc(trs, 31, 9, "GYM8");
	}

	public void setTrainers(List<Trainer> trainerData) {
		int traineroffset = romEntry.getValue("TrainerDataTableOffset");
		int traineramount = 47;
		int[] trainerclasslimits = romEntry.arrayEntries
				.get("TrainerDataClassCounts");

		int[] pointers = new int[traineramount + 1];
		for (int i = 1; i <= traineramount; i++) {
			int tPointer = readWord(traineroffset + (i - 1) * 2);
			pointers[i] = calculateOffset(bankOf(traineroffset), tPointer);
		}

		Iterator<Trainer> allTrainers = trainerData.iterator();
		for (int i = 1; i <= traineramount; i++) {
			int offs = pointers[i];
			int limit = trainerclasslimits[i];
			for (int trnum = 0; trnum < limit; trnum++) {
				Trainer tr = allTrainers.next();
				if (tr.trainerclass != i) {
					System.err.println("Trainer mismatch: " + tr.name);
				}
				Iterator<TrainerPokemon> tPokes = tr.pokemon.iterator();
				// Write their pokemon based on poketype
				if (tr.poketype == 0) {
					// Regular trainer
					int fixedLevel = tr.pokemon.get(0).level;
					rom[offs] = (byte) fixedLevel;
					offs++;
					while (tPokes.hasNext()) {
						TrainerPokemon tpk = tPokes.next();
						rom[offs] = (byte) pokeNumToRBYTable[tpk.pokemon.number];
						offs++;
					}
				} else {
					// Special trainer
					rom[offs] = (byte) 0xFF;
					offs++;
					while (tPokes.hasNext()) {
						TrainerPokemon tpk = tPokes.next();
						rom[offs] = (byte) tpk.level;
						rom[offs + 1] = (byte) pokeNumToRBYTable[tpk.pokemon.number];
						offs += 2;
					}
				}
				rom[offs] = 0;
				offs++;
			}
		}

		// Custom Moves AI Table
		// Zero it out entirely.
		rom[romEntry.getValue("ExtraTrainerMovesTableOffset")] = (byte) 0xFF;

		// Champion Rival overrides in Red/Blue
		if (!isYellow()) {
			// hacky relative offset (very likely to work but maybe not always)
			int champRivalJump = romEntry.getValue("GymLeaderMovesTableOffset") - 0x44;
			// nop out this jump
			rom[champRivalJump] = 0x00;
			rom[champRivalJump + 1] = 0x00;
		}

	}

	private void tbc(List<Trainer> allTrainers, int classNum, int number,
			String tag) {
		int currnum = -1;
		for (Trainer t : allTrainers) {
			if (t.trainerclass == classNum) {
				currnum++;
				if (currnum == number) {
					t.tag = tag;
					return;
				}
			}
		}
	}

	@Override
	public boolean isYellow() {
		return romEntry.isYellow;
	}

	@Override
	public boolean typeInGame(Type type) {
		if (type.isHackOnly == false
				&& (type != Type.DARK && type != Type.STEEL)) {
			return true;
		}
		if (romEntry.extraTypeReverse.containsKey(type)) {
			return true;
		}
		return false;
	}

	@Override
	public void fixTypeEffectiveness() {
		int base = romEntry.getValue("TypeEffectivenessOffset");
		log("--Fixing Type Effectiveness--");
		// Change Poison SE to bug (should be neutral)
		// to Ice NE to Fire (is currently neutral)
		log("Replaced: Poison super effective vs Bug => Ice not very effective vs Fire");
		rom[base + 135] = typeToByte(Type.ICE);
		rom[base + 136] = typeToByte(Type.FIRE);
		rom[base + 137] = 5; // Not very effective
		// Change BUG SE to Poison to Bug NE to Poison
		log("Changed: Bug super effective vs Poison => Bug not very effective vs Poison");
		rom[base + 203] = 5; // Not very effective
		// Change Ghost 0E to Psychic to Ghost SE to Psychic
		log("Changed: Psychic immune to Ghost => Ghost super effective vs Psychic");
		rom[base + 227] = 20; // Super effective
		logBlankLine();
	}

	@Override
	public Map<Pokemon, List<MoveLearnt>> getMovesLearnt() {
		Map<Pokemon, List<MoveLearnt>> movesets = new TreeMap<Pokemon, List<MoveLearnt>>();
		int pointersOffset = romEntry.getValue("PokemonMovesetsTableOffset");
		int pokeStatsOffset = romEntry.getValue("PokemonStatsOffset");
		int pkmnCount = romEntry.getValue("InternalPokemonCount");
		for (int i = 1; i <= pkmnCount; i++) {
			int pointer = readWord(pointersOffset + (i - 1) * 2);
			int realPointer = calculateOffset(bankOf(pointersOffset), pointer);
			if (pokeRBYToNumTable[i] != 0) {
				Pokemon pkmn = pokes[pokeRBYToNumTable[i]];
				int statsOffset = 0;
				if (pokeRBYToNumTable[i] == 151 && !romEntry.isYellow) {
					// Mewww
					statsOffset = romEntry.getValue("MewStatsOffset");
				} else {
					statsOffset = (pokeRBYToNumTable[i] - 1) * 0x1C
							+ pokeStatsOffset;
				}
				List<MoveLearnt> ourMoves = new ArrayList<MoveLearnt>();
				for (int delta = 0x0F; delta < 0x13; delta++) {
					if (rom[statsOffset + delta] != 0x00) {
						MoveLearnt learnt = new MoveLearnt();
						learnt.level = 1;
						learnt.move = moveRomToNumTable[rom[statsOffset + delta] & 0xFF];
						ourMoves.add(learnt);
					}
				}
				// Skip over evolution data
				while (rom[realPointer] != 0) {
					if (rom[realPointer] == 1) {
						realPointer += 3;
					} else if (rom[realPointer] == 2) {
						realPointer += 4;
					} else if (rom[realPointer] == 3) {
						realPointer += 3;
					}
				}
				realPointer++;
				while (rom[realPointer] != 0) {
					MoveLearnt learnt = new MoveLearnt();
					learnt.level = rom[realPointer] & 0xFF;
					learnt.move = moveRomToNumTable[rom[realPointer + 1] & 0xFF];
					ourMoves.add(learnt);
					realPointer += 2;
				}
				movesets.put(pkmn, ourMoves);
			}
		}
		return movesets;
	}

	@Override
	public void setMovesLearnt(Map<Pokemon, List<MoveLearnt>> movesets) {
		int pointersOffset = romEntry.getValue("PokemonMovesetsTableOffset");
		int pokeStatsOffset = romEntry.getValue("PokemonStatsOffset");
		int pkmnCount = romEntry.getValue("InternalPokemonCount");
		for (int i = 1; i <= pkmnCount; i++) {
			int pointer = readWord(pointersOffset + (i - 1) * 2);
			int realPointer = calculateOffset(bankOf(pointersOffset), pointer);
			if (pokeRBYToNumTable[i] != 0) {
				Pokemon pkmn = pokes[pokeRBYToNumTable[i]];
				List<MoveLearnt> ourMoves = movesets.get(pkmn);
				int statsOffset = 0;
				if (pokeRBYToNumTable[i] == 151 && !romEntry.isYellow) {
					// Mewww
					statsOffset = romEntry.getValue("MewStatsOffset");
				} else {
					statsOffset = (pokeRBYToNumTable[i] - 1) * 0x1C
							+ pokeStatsOffset;
				}
				int movenum = 0;
				while (movenum < 4 && ourMoves.size() > movenum
						&& ourMoves.get(movenum).level == 1) {
					rom[statsOffset + 0x0F + movenum] = (byte) moveNumToRomTable[ourMoves
							.get(movenum).move];
					movenum++;
				}
				// Write out the rest of zeroes
				for (int mn = movenum; mn < 4; mn++) {
					rom[statsOffset + 0x0F + mn] = 0;
				}
				// Skip over evolution data
				while (rom[realPointer] != 0) {
					if (rom[realPointer] == 1) {
						realPointer += 3;
					} else if (rom[realPointer] == 2) {
						realPointer += 4;
					} else if (rom[realPointer] == 3) {
						realPointer += 3;
					}
				}
				realPointer++;
				while (rom[realPointer] != 0 && movenum < ourMoves.size()) {
					rom[realPointer] = (byte) ourMoves.get(movenum).level;
					rom[realPointer + 1] = (byte) moveNumToRomTable[ourMoves
							.get(movenum).move];
					realPointer += 2;
					movenum++;
				}
				// Make sure we finish off the moveset
				rom[realPointer] = 0;
			}
		}
	}

	@Override
	public List<Pokemon> getStaticPokemon() {
		List<Pokemon> statics = new ArrayList<Pokemon>();
		if (romEntry.getValue("StaticPokemonSupport") > 0) {
			for (int offset : romEntry.staticPokemonSingle) {
				statics.add(pokes[pokeRBYToNumTable[rom[offset] & 0xFF]]);
			}
			for (GameCornerPokemon gcp : romEntry.staticPokemonGameCorner) {
				statics.add(pokes[pokeRBYToNumTable[rom[gcp.offsets[0]] & 0xFF]]);
			}
		}
		return statics;
	}

	@Override
	public boolean setStaticPokemon(List<Pokemon> staticPokemon) {
		if (romEntry.getValue("StaticPokemonSupport") == 0) {
			return false;
		}
		// Checks
		int singleSize = romEntry.staticPokemonSingle.size();
		int gcSize = romEntry.staticPokemonGameCorner.size();
		if (staticPokemon.size() != singleSize + gcSize) {
			return false;
		}
		for (Pokemon pkmn : staticPokemon) {
			if (!isInGame(pkmn)) {
				return false;
			}
		}

		// Singular entries
		for (int i = 0; i < singleSize; i++) {
			rom[romEntry.staticPokemonSingle.get(i)] = (byte) pokeNumToRBYTable[staticPokemon
					.get(i).number];
		}

		// Game corner
		for (int i = 0; i < gcSize; i++) {
			byte pokeNum = (byte) pokeNumToRBYTable[staticPokemon.get(i
					+ singleSize).number];
			int[] offsets = romEntry.staticPokemonGameCorner.get(i).offsets;
			for (int offset : offsets) {
				rom[offset] = pokeNum;
			}
		}
		return true;
	}

	@Override
	public boolean canChangeStaticPokemon() {
		return (romEntry.getValue("StaticPokemonSupport") > 0);
	}

	@Override
	public List<Integer> getTMMoves() {
		List<Integer> tms = new ArrayList<Integer>();
		int offset = romEntry.getValue("TMMovesOffset");
		for (int i = 1; i <= 50; i++) {
			tms.add(moveRomToNumTable[rom[offset + (i - 1)] & 0xFF]);
		}
		return tms;
	}

	@Override
	public List<Integer> getHMMoves() {
		List<Integer> hms = new ArrayList<Integer>();
		int offset = romEntry.getValue("TMMovesOffset");
		for (int i = 1; i <= 5; i++) {
			hms.add(moveRomToNumTable[rom[offset + 50 + (i - 1)] & 0xFF]);
		}
		return hms;
	}

	@Override
	public void setTMMoves(List<Integer> moveIndexes) {
		int offset = romEntry.getValue("TMMovesOffset");
		for (int i = 1; i <= 50; i++) {
			rom[offset + (i - 1)] = (byte) moveNumToRomTable[moveIndexes
					.get(i - 1)];
		}

		// Gym Leader TM Moves (RB only)
		if (!romEntry.isYellow) {
			int[] tms = new int[] { 34, 11, 24, 21, 6, 46, 38, 27 };
			int glMovesOffset = romEntry.getValue("GymLeaderMovesTableOffset");
			for (int i = 0; i < tms.length; i++) {
				// Set the special move used by gym (i+1) to
				// the move we just wrote to TM tms[i]
				rom[glMovesOffset + i * 2] = (byte) moveNumToRomTable[moveIndexes
						.get(tms[i] - 1)];
			}
		}

		// TM Text
		String[] moveNames = readMoveNames();
		for (TMTextEntry tte : romEntry.tmTexts) {
			String moveName = moveNames[moveNumToRomTable[moveIndexes
					.get(tte.number - 1)]];
			String text = tte.template.replace("%m", moveName);
			writeFixedLengthScriptString(text, tte.offset,
					lengthOfStringAt(tte.offset));
		}
	}

	@Override
	public int getTMCount() {
		return 50;
	}

	@Override
	public int getHMCount() {
		return 5;
	}

	@Override
	public Map<Pokemon, boolean[]> getTMHMCompatibility() {
		Map<Pokemon, boolean[]> compat = new TreeMap<Pokemon, boolean[]>();
		int pokeStatsOffset = romEntry.getValue("PokemonStatsOffset");
		for (int i = 1; i <= pokedexCount; i++) {
			int baseStatsOffset = (romEntry.isYellow || i != 151) ? (pokeStatsOffset + (i - 1) * 0x1C)
					: romEntry.getValue("MewStatsOffset");
			Pokemon pkmn = pokes[i];
			boolean[] flags = new boolean[56];
			for (int j = 0; j < 7; j++) {
				readByteIntoFlags(flags, j * 8 + 1, baseStatsOffset + 0x14 + j);
			}
			compat.put(pkmn, flags);
		}
		return compat;
	}

	@Override
	public void setTMHMCompatibility(Map<Pokemon, boolean[]> compatData) {
		int pokeStatsOffset = romEntry.getValue("PokemonStatsOffset");
		for (Map.Entry<Pokemon, boolean[]> compatEntry : compatData.entrySet()) {
			Pokemon pkmn = compatEntry.getKey();
			boolean[] flags = compatEntry.getValue();
			int baseStatsOffset = (romEntry.isYellow || pkmn.number != 151) ? (pokeStatsOffset + (pkmn.number - 1) * 0x1C)
					: romEntry.getValue("MewStatsOffset");
			for (int j = 0; j < 7; j++) {
				rom[baseStatsOffset + 0x14 + j] = getByteFromFlags(flags,
						j * 8 + 1);
			}
		}
	}

	@Override
	public boolean hasMoveTutors() {
		return false;
	}

	@Override
	public List<Integer> getMoveTutorMoves() {
		return new ArrayList<Integer>();
	}

	@Override
	public void setMoveTutorMoves(List<Integer> moves) {
		// Do nothing
	}

	@Override
	public Map<Pokemon, boolean[]> getMoveTutorCompatibility() {
		return new TreeMap<Pokemon, boolean[]>();
	}

	@Override
	public void setMoveTutorCompatibility(Map<Pokemon, boolean[]> compatData) {
		// Do nothing
	}

	@Override
	public String getROMName() {
		return "Pokemon " + romEntry.name;
	}

	@Override
	public String getROMCode() {
		return romEntry.romName + " (" + romEntry.version + "/"
				+ romEntry.nonJapanese + ")";
	}

	@Override
	public String getSupportLevel() {
		return (romEntry.getValue("StaticPokemonSupport") > 0) ? "Complete"
				: "No Static Pokemon";
	}

	@Override
	public List<Evolution> getEvolutions() {
		List<Evolution> evos = new ArrayList<Evolution>();
		int pointersOffset = romEntry.getValue("PokemonMovesetsTableOffset");
		List<Evolution> evosForThisPoke = new ArrayList<Evolution>();
		int pkmnCount = romEntry.getValue("InternalPokemonCount");
		for (int i = 1; i <= pkmnCount; i++) {
			int pointer = readWord(pointersOffset + (i - 1) * 2);
			int realPointer = calculateOffset(bankOf(pointersOffset), pointer);
			if (pokeRBYToNumTable[i] != 0) {
				evosForThisPoke.clear();
				int thisPoke = pokeRBYToNumTable[i];
				while (rom[realPointer] != 0) {
					int method = rom[realPointer];
					EvolutionType type = EvolutionType.fromIndex(1, method);
					int otherPoke = pokeRBYToNumTable[rom[realPointer + 2
							+ (type == EvolutionType.STONE ? 1 : 0)] & 0xFF];
					int extraInfo = rom[realPointer + 1] & 0xFF;
					Evolution evo = new Evolution(thisPoke, otherPoke, true,
							type, extraInfo);
					if (!evos.contains(evo)) {
						evos.add(evo);
						evosForThisPoke.add(evo);
					}
					realPointer += (type == EvolutionType.STONE ? 4 : 3);
				}
				// split evos don't carry stats
				if (evosForThisPoke.size() > 1) {
					for (Evolution e : evosForThisPoke) {
						e.carryStats = false;
					}
				}
			}
		}
		return evos;
	}

	@Override
	public void removeTradeEvolutions(boolean changeMoveEvos) {
		// Gen 1: evolution data is right before moveset data
		// So use those pointers
		// no move evos, so no need to check for those
		int pointersOffset = romEntry.getValue("PokemonMovesetsTableOffset");
		int pkmnCount = romEntry.getValue("InternalPokemonCount");
		log("--Removing Trade Evolutions--");
		for (int i = 1; i <= pkmnCount; i++) {
			int pointer = readWord(pointersOffset + (i - 1) * 2);
			int realPointer = calculateOffset(bankOf(pointersOffset), pointer);
			if (pokeRBYToNumTable[i] != 0) {
				// Evolution data
				// All the 4 trade evos (Abra, Geodude, Gastly, Machop)
				// evolve at around 25
				// So make this "3rd stage" to 37
				while (rom[realPointer] != 0) {
					if (rom[realPointer] == 1) {
						realPointer += 3;
					} else if (rom[realPointer] == 2) {
						realPointer += 4;
					} else if (rom[realPointer] == 3) {
						int otherPoke = pokeRBYToNumTable[rom[realPointer + 2] & 0xFF];
						// Trade evo
						rom[realPointer] = 1;
						rom[realPointer + 1] = 37;
						logEvoChangeLevel(pokes[pokeRBYToNumTable[i]].name,
								pokes[otherPoke].name, 37);
						realPointer += 3;
					}
				}
			}
		}
		logBlankLine();
	}

	private static final int[] tclassesCounts = new int[] { 21, 47 };
	private static final List<Integer> singularTrainers = Arrays.asList(28, 32,
			33, 34, 35, 36, 37, 38, 39, 43, 45, 46);

	private List<String> getTrainerClassesForText() {
		int[] offsets = romEntry.arrayEntries.get("TrainerClassNamesOffsets");
		List<String> tcNames = new ArrayList<String>();
		int offset = offsets[offsets.length - 1];
		for (int j = 0; j < tclassesCounts[1]; j++) {
			String name = readVariableLengthString(offset);
			offset += (internalStringLength(name) + 1);
			tcNames.add(name);
		}
		return tcNames;
	}

	@Override
	public List<String> getTrainerNames() {
		int[] offsets = romEntry.arrayEntries.get("TrainerClassNamesOffsets");
		List<String> trainerNames = new ArrayList<String>();
		int offset = offsets[offsets.length - 1];
		for (int j = 0; j < tclassesCounts[1]; j++) {
			String name = readVariableLengthString(offset);
			offset += (internalStringLength(name) + 1);
			if (singularTrainers.contains(j)) {
				trainerNames.add(name);
			}
		}
		return trainerNames;
	}

	@Override
	public void setTrainerNames(List<String> trainerNames) {
		if (romEntry.getValue("CanChangeTrainerText") > 0) {
			int[] offsets = romEntry.arrayEntries
					.get("TrainerClassNamesOffsets");
			Iterator<String> trainerNamesI = trainerNames.iterator();
			int offset = offsets[offsets.length - 1];
			for (int j = 0; j < tclassesCounts[1]; j++) {
				String name = readVariableLengthString(offset);
				if (singularTrainers.contains(j)) {
					String newName = trainerNamesI.next();
					writeFixedLengthString(newName, offset,
							internalStringLength(name) + 1);
				}
				offset += (internalStringLength(name) + 1);
			}
		}
	}

	@Override
	public TrainerNameMode trainerNameMode() {
		return TrainerNameMode.SAME_LENGTH;
	}

	@Override
	public List<Integer> getTCNameLengthsByTrainer() {
		// not needed
		return new ArrayList<Integer>();
	}

	@Override
	public List<String> getTrainerClassNames() {
		int[] offsets = romEntry.arrayEntries.get("TrainerClassNamesOffsets");
		List<String> trainerClassNames = new ArrayList<String>();
		if (offsets.length == 2) {
			for (int i = 0; i < offsets.length; i++) {
				int offset = offsets[i];
				for (int j = 0; j < tclassesCounts[i]; j++) {
					String name = readVariableLengthString(offset);
					offset += (internalStringLength(name) + 1);
					if (i == 0 || !singularTrainers.contains(j)) {
						trainerClassNames.add(name);
					}
				}
			}
		} else {
			int offset = offsets[0];
			for (int j = 0; j < tclassesCounts[1]; j++) {
				String name = readVariableLengthString(offset);
				offset += (internalStringLength(name) + 1);
				if (!singularTrainers.contains(j)) {
					trainerClassNames.add(name);
				}
			}
		}
		return trainerClassNames;
	}

	@Override
	public void setTrainerClassNames(List<String> trainerClassNames) {
		if (romEntry.getValue("CanChangeTrainerText") > 0) {
			int[] offsets = romEntry.arrayEntries
					.get("TrainerClassNamesOffsets");
			Iterator<String> tcNamesIter = trainerClassNames.iterator();
			if (offsets.length == 2) {
				for (int i = 0; i < offsets.length; i++) {
					int offset = offsets[i];
					for (int j = 0; j < tclassesCounts[i]; j++) {
						String name = readVariableLengthString(offset);
						if (i == 0 || !singularTrainers.contains(j)) {
							String newName = tcNamesIter.next();
							writeFixedLengthString(newName, offset,
									internalStringLength(name) + 1);
						}
						offset += (internalStringLength(name) + 1);
					}
				}
			} else {
				int offset = offsets[0];
				for (int j = 0; j < tclassesCounts[1]; j++) {
					String name = readVariableLengthString(offset);
					if (!singularTrainers.contains(j)) {
						String newName = tcNamesIter.next();
						writeFixedLengthString(newName, offset,
								internalStringLength(name) + 1);
					}
					offset += (internalStringLength(name) + 1);
				}
			}
		}

	}

	@Override
	public boolean fixedTrainerClassNamesLength() {
		return true;
	}

	@Override
	public String getDefaultExtension() {
		if (((rom[0x143] & 0xFF) & 0x80) > 0) {
			return "gbc";
		}
		return "sgb";
	}

	@Override
	public int abilitiesPerPokemon() {
		return 0;
	}

	@Override
	public int highestAbilityIndex() {
		return 0;
	}

	@Override
	public int internalStringLength(String string) {
		return translateString(string).length;
	}

	@Override
	public int codeTweaksAvailable() {
		int available = 0;
		if (romEntry.codeTweaks.get("BWXPTweak") != null) {
			available |= CodeTweaks.BW_EXP_PATCH;
		}
		if (romEntry.codeTweaks.get("XAccNerfTweak") != null) {
			available |= CodeTweaks.NERF_X_ACCURACY;
		}
		if (romEntry.codeTweaks.get("CritRateTweak") != null) {
			available |= CodeTweaks.FIX_CRIT_RATE;
		}
		return available;
	}

	@Override
	public void applyBWEXPPatch() {
		genericIPSPatch("BWXPTweak");
	}

	@Override
	public void applyXAccNerfPatch() {
		xAccNerfed = genericIPSPatch("XAccNerfTweak");
	}

	@Override
	public void applyCritRatePatch() {
		genericIPSPatch("CritRateTweak");
	}

	private boolean genericIPSPatch(String ctName) {
		String patchName = romEntry.codeTweaks.get(ctName);
		if (patchName == null) {
			return false;
		}

		try {
			FileFunctions.applyPatch(rom, patchName);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public List<Integer> getGameBreakingMoves() {
		// Sonicboom & drage & OHKO moves
		// 160 add spore
		// also remove OHKO if xacc nerfed
		if (xAccNerfed) {
			return Arrays.asList(49, 82, 147);
		} else {
			return Arrays.asList(49, 82, 32, 90, 12, 147);
		}
	}

	@Override
	public void applySignature() {
		// First off, intro Pokemon
		// 160 add yellow intro random
		int introPokemon = pokeNumToRBYTable[this.randomPokemon().number];
		rom[romEntry.getValue("IntroPokemonOffset")] = (byte) introPokemon;
		rom[romEntry.getValue("IntroCryOffset")] = (byte) introPokemon;

	}

	@Override
	public ItemList getAllowedItems() {
		return allowedItems;
	}

	private void loadItemNames() {
		itemNames = new String[256];
		itemNames[0] = "glitch";
		// trying to emulate pretty much what the game does here
		// normal items
		int origOffset = romEntry.getValue("ItemNamesOffset");
		int itemNameOffset = origOffset;
		for (int index = 1; index <= 0x100; index++) {
			if (itemNameOffset / 0x4000 > origOffset / 0x4000) {
				// the game would continue making its merry way into VRAM here,
				// but we don't have VRAM to simulate.
				// just give up.
				break;
			}
			int startOfText = itemNameOffset;
			while ((rom[itemNameOffset] & 0xFF) != 0x50) {
				itemNameOffset++;
			}
			itemNameOffset++;
			itemNames[index % 256] = readFixedLengthString(startOfText, 20);
		}
		// hms override
		for (int index = 0xC4; index < 0xC9; index++) {
			itemNames[index] = String.format("HM%02d", index - 0xC3);
		}
		// tms override
		for (int index = 0xC9; index < 0x100; index++) {
			itemNames[index] = String.format("TM%02d", index - 0xC8);
		}
	}

	@Override
	public String[] getItemNames() {
		return itemNames;
	}

	private static class SubMap {
		private int id;
		private int addr;
		private int bank;
		private MapHeader header;
		private Connection[] cons;
		private int n_cons;
		private int obj_addr;
		private List<Integer> itemOffsets;
	}

	private static class MapHeader {
		private int tileset_id; // u8
		private int map_h, map_w; // u8
		private int map_ptr, text_ptr, script_ptr; // u16
		private int connect_byte; // u8
		// 10 bytes
	}

	private static class Connection {
		private int index; // u8
		private int connected_map; // u16
		private int current_map; // u16
		private int bigness; // u8
		private int map_width; // u8
		private int y_align; // u8
		private int x_align; // u8
		private int window; // u16
		// 11 bytes
	}

	private void preloadMaps() {
		int mapBanks = romEntry.getValue("MapBanks");
		int mapAddresses = romEntry.getValue("MapAddresses");

		preloadMap(mapBanks, mapAddresses, 0);
	}

	private void preloadMap(int mapBanks, int mapAddresses, int mapID) {

		if (maps[mapID] != null || mapID == 0xED || mapID == 0xFF) {
			return;
		}

		SubMap map = new SubMap();
		maps[mapID] = map;

		map.id = mapID;
		map.addr = calculateOffset(rom[mapBanks + mapID] & 0xFF,
				readWord(mapAddresses + mapID * 2));
		map.bank = bankOf(map.addr);

		map.header = new MapHeader();
		map.header.tileset_id = rom[map.addr] & 0xFF;
		map.header.map_h = rom[map.addr + 1] & 0xFF;
		map.header.map_w = rom[map.addr + 2] & 0xFF;
		map.header.map_ptr = calculateOffset(map.bank, readWord(map.addr + 3));
		map.header.text_ptr = calculateOffset(map.bank, readWord(map.addr + 5));
		map.header.script_ptr = calculateOffset(map.bank,
				readWord(map.addr + 7));
		map.header.connect_byte = rom[map.addr + 9] & 0xFF;

		int cb = map.header.connect_byte;
		map.n_cons = ((cb & 8) >> 3) + ((cb & 4) >> 2) + ((cb & 2) >> 1)
				+ (cb & 1);

		int cons_offset = map.addr + 10;

		map.cons = new Connection[map.n_cons];
		for (int i = 0; i < map.n_cons; i++) {
			int tcon_offs = cons_offset + i * 11;
			Connection con = new Connection();
			con.index = rom[tcon_offs] & 0xFF;
			con.connected_map = readWord(tcon_offs + 1);
			con.current_map = readWord(tcon_offs + 3);
			con.bigness = rom[tcon_offs + 5] & 0xFF;
			con.map_width = rom[tcon_offs + 6] & 0xFF;
			con.y_align = rom[tcon_offs + 7] & 0xFF;
			con.x_align = rom[tcon_offs + 8] & 0xFF;
			con.window = readWord(tcon_offs + 9);
			map.cons[i] = con;
			preloadMap(mapBanks, mapAddresses, con.index);
		}
		map.obj_addr = calculateOffset(map.bank, readWord(cons_offset
				+ map.n_cons * 11));

		// Read objects
		// +0 is the border tile (ignore)
		// +1 is warp count

		int n_warps = rom[map.obj_addr + 1] & 0xFF;
		int offs = map.obj_addr + 2;
		for (int i = 0; i < n_warps; i++) {
			// track this warp
			int to_map = rom[offs + 3] & 0xFF;
			preloadMap(mapBanks, mapAddresses, to_map);
			offs += 4;
		}

		// Now we're pointing to sign count
		int n_signs = rom[offs++] & 0xFF;
		offs += n_signs * 3;

		// Finally, entities, which contain the items
		map.itemOffsets = new ArrayList<Integer>();
		int n_entities = rom[offs++] & 0xFF;
		for (int i = 0; i < n_entities; i++) {
			// Read text ID
			int tid = rom[offs + 5] & 0xFF;
			if ((tid & (1 << 6)) > 0) {
				// trainer
				offs += 8;
			} else if ((tid & (1 << 7)) > 0 && (rom[offs + 6] != 0x00)) {
				// item
				map.itemOffsets.add(offs + 6);
				offs += 7;
			} else {
				// generic
				offs += 6;
			}
		}
	}

	private void loadMapNames() {
		mapNames = new String[256];
		int mapNameTableOffset = romEntry.getValue("MapNameTableOffset");
		int mapNameBank = bankOf(mapNameTableOffset);
		// external names
		List<Integer> usedExternal = new ArrayList<Integer>();
		for (int i = 0; i < 0x25; i++) {
			int externalOffset = calculateOffset(mapNameBank,
					readWord(mapNameTableOffset + 1));
			usedExternal.add(externalOffset);
			mapNames[i] = readVariableLengthString(externalOffset);
			mapNameTableOffset += 3;
		}

		// internal names
		int lastMaxMap = 0x25;
		Map<Integer, Integer> previousMapCounts = new HashMap<Integer, Integer>();
		while ((rom[mapNameTableOffset] & 0xFF) != 0xFF) {
			int maxMap = rom[mapNameTableOffset] & 0xFF;
			int nameOffset = calculateOffset(mapNameBank,
					readWord(mapNameTableOffset + 2));
			String actualName = readVariableLengthString(nameOffset).trim();
			if (usedExternal.contains(nameOffset)) {
				for (int i = lastMaxMap; i < maxMap; i++) {
					if (maps[i] != null) {
						mapNames[i] = actualName + " (Building)";
					}
				}
			} else {
				int mapCount = 0;
				if (previousMapCounts.containsKey(nameOffset)) {
					mapCount = previousMapCounts.get(nameOffset);
				}
				for (int i = lastMaxMap; i < maxMap; i++) {
					if (maps[i] != null) {
						mapCount++;
						mapNames[i] = actualName + " (" + mapCount + ")";
					}
				}
				previousMapCounts.put(nameOffset, mapCount);
			}
			lastMaxMap = maxMap;
			mapNameTableOffset += 4;
		}
	}

	private List<Integer> getItemOffsets() {

		List<Integer> itemOffs = new ArrayList<Integer>();

		for (int i = 0; i < maps.length; i++) {
			if (maps[i] != null) {
				itemOffs.addAll(maps[i].itemOffsets);
			}
		}

		int hiRoutine = romEntry.getValue("HiddenItemRoutine");
		int spclTable = romEntry.getValue("SpecialMapPointerTable");
		int spclBank = bankOf(spclTable);

		if (!isYellow()) {

			int spclList = romEntry.getValue("SpecialMapList");

			int lOffs = spclList;
			int idx = 0;

			while ((rom[lOffs] & 0xFF) != 0xFF) {

				int spclOffset = calculateOffset(spclBank, readWord(spclTable
						+ idx));

				while ((rom[spclOffset] & 0xFF) != 0xFF) {
					if (calculateOffset(rom[spclOffset + 3] & 0xFF,
							readWord(spclOffset + 4)) == hiRoutine) {
						itemOffs.add(spclOffset + 2);
					}
					spclOffset += 6;
				}
				lOffs++;
				idx += 2;
			}
		} else {

			int lOffs = spclTable;

			while ((rom[lOffs] & 0xFF) != 0xFF) {

				int spclOffset = calculateOffset(spclBank, readWord(lOffs + 1));

				while ((rom[spclOffset] & 0xFF) != 0xFF) {
					if (calculateOffset(rom[spclOffset + 3] & 0xFF,
							readWord(spclOffset + 4)) == hiRoutine) {
						itemOffs.add(spclOffset + 2);
					}
					spclOffset += 6;
				}
				lOffs += 3;
			}
		}

		return itemOffs;
	}

	@Override
	public List<Integer> getRequiredFieldTMs() {
		return Arrays.asList(new Integer[] { 3, 4, 8, 10, 12, 14, 16, 19, 20,
				22, 25, 26, 30, 40, 43, 44, 45, 47 });
	}

	@Override
	public List<Integer> getCurrentFieldTMs() {
		List<Integer> itemOffsets = getItemOffsets();
		List<Integer> fieldTMs = new ArrayList<Integer>();

		for (int offset : itemOffsets) {
			int itemHere = rom[offset] & 0xFF;
			if (allowedItems.isTM(itemHere)) {
				fieldTMs.add(itemHere - 200); // TM offset
			}
		}
		return fieldTMs;
	}

	@Override
	public void setFieldTMs(List<Integer> fieldTMs) {
		List<Integer> itemOffsets = getItemOffsets();
		Iterator<Integer> iterTMs = fieldTMs.iterator();

		for (int offset : itemOffsets) {
			int itemHere = rom[offset] & 0xFF;
			if (allowedItems.isTM(itemHere)) {
				// Replace this with a TM from the list
				rom[offset] = (byte) (iterTMs.next() + 200);
			}
		}
	}

	@Override
	public List<Integer> getRegularFieldItems() {
		List<Integer> itemOffsets = getItemOffsets();
		List<Integer> fieldItems = new ArrayList<Integer>();

		for (int offset : itemOffsets) {
			int itemHere = rom[offset] & 0xFF;
			if (allowedItems.isAllowed(itemHere)
					&& !(allowedItems.isTM(itemHere))) {
				fieldItems.add(itemHere);
			}
		}
		return fieldItems;
	}

	@Override
	public void setRegularFieldItems(List<Integer> items) {
		List<Integer> itemOffsets = getItemOffsets();
		Iterator<Integer> iterItems = items.iterator();

		for (int offset : itemOffsets) {
			int itemHere = rom[offset] & 0xFF;
			if (allowedItems.isAllowed(itemHere)
					&& !(allowedItems.isTM(itemHere))) {
				// Replace it
				rom[offset] = (byte) (iterItems.next().intValue());
			}
		}

	}

	@Override
	public List<IngameTrade> getIngameTrades() {
		List<IngameTrade> trades = new ArrayList<IngameTrade>();

		// info
		int tableOffset = romEntry.getValue("TradeTableOffset");
		int tableSize = romEntry.getValue("TradeTableSize");
		int nicknameLength = romEntry.getValue("TradeNameLength");
		int[] unused = romEntry.arrayEntries.get("TradesUnused");
		int unusedOffset = 0;
		int entryLength = nicknameLength + 3;

		for (int entry = 0; entry < tableSize; entry++) {
			if (unusedOffset < unused.length && unused[unusedOffset] == entry) {
				unusedOffset++;
				continue;
			}
			IngameTrade trade = new IngameTrade();
			int entryOffset = tableOffset + entry * entryLength;
			trade.requestedPokemon = pokes[pokeRBYToNumTable[rom[entryOffset] & 0xFF]];
			trade.givenPokemon = pokes[pokeRBYToNumTable[rom[entryOffset + 1] & 0xFF]];
			trade.nickname = readString(entryOffset + 3, nicknameLength);
			trades.add(trade);
		}

		return trades;
	}

	@Override
	public void setIngameTrades(List<IngameTrade> trades) {

		// info
		int tableOffset = romEntry.getValue("TradeTableOffset");
		int tableSize = romEntry.getValue("TradeTableSize");
		int nicknameLength = romEntry.getValue("TradeNameLength");
		int[] unused = romEntry.arrayEntries.get("TradesUnused");
		int unusedOffset = 0;
		int entryLength = nicknameLength + 3;
		int tradeOffset = 0;

		for (int entry = 0; entry < tableSize; entry++) {
			if (unusedOffset < unused.length && unused[unusedOffset] == entry) {
				unusedOffset++;
				continue;
			}
			IngameTrade trade = trades.get(tradeOffset++);
			int entryOffset = tableOffset + entry * entryLength;
			rom[entryOffset] = (byte) pokeNumToRBYTable[trade.requestedPokemon.number];
			rom[entryOffset + 1] = (byte) pokeNumToRBYTable[trade.givenPokemon.number];
			if (romEntry.getValue("CanChangeTrainerText") > 0) {
				writeFixedLengthString(trade.nickname, entryOffset + 3,
						nicknameLength);
			}
		}
	}

	@Override
	public boolean hasDVs() {
		return true;
	}

	@Override
	public int generationOfPokemon() {
		return 1;
	}

	@Override
	public void removeEvosForPokemonPool() {
		// gen1 doesn't have this functionality anyway
	}

	@Override
	public boolean supportsFourStartingMoves() {
		return true;
	}
}
